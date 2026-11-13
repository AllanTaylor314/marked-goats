@file:Suppress("unused")

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment
import org.gradle.api.NamedDomainObjectContainer
import java.util.*

sealed class Loader(val id: String) {
	abstract val modManifestPath: String
	abstract val excludedResources: List<String>

	open val isFabricLike: Boolean = false

	abstract fun generateManifest(ctx: Context): String

	object Fabric : Loader("fabric") {
		override val isFabricLike = true
		override val modManifestPath = "fabric.mod.json"
		override val excludedResources = listOf(
			"META-INF/mods.toml", "META-INF/neoforge.mods.toml", "aw/*.cfg", ".cache", "pack.mcmeta"
		)

		override fun generateManifest(ctx: Context): String {
			val awFile = ctx.project.file("src/main/resources/aw/${ctx.currentMcVersion}.accesswidener")

			val manifest = FabricManifest(
				id = ctx.modId,
				name = ctx.modName,
				version = ctx.baseVersion,
				authors = ctx.authors,
				contributors = ctx.contributors,
				contact = mapOf(
					"sources" to ctx.sourcesUrl, "issues" to ctx.issuesUrl, "homepage" to ctx.homepageUrl
				),
				custom = buildJsonObject {
					ctx.discordUrl.takeIf { it.isNotEmpty() }?.let { url ->
						putJsonObject("modmenu") {
							putJsonObject("links") {
								put("modmenu.discord", url)
							}
						}
					}
					// WTHIT 4.13.6 (1.18.2) predates the waila_plugins.json
					// file-based discovery mechanism entirely - it reads a
					// custom fabric.mod.json value instead (confirmed via
					// FabricPluginLoader.java's gatherPlugins()).
					if (ctx.project.hasProperty("deps.wthit-legacy-fabric-mod-json")) {
						putJsonObject("waila:plugins") {
							put("id", "${ctx.modId}:plugin")
							put("initializer", "kiwi.allantaylor.markedgoats.MarkedGoatsWailaPlugin")
							put("environment", "client")
						}
					}
				}.takeIf { it.isNotEmpty() },
				description = ctx.description,
				icon = "assets/icon.png",
				license = ctx.licenseName,
				environment = when (ctx.environment) {
					ModrinthEnvironment.CLIENT_ONLY, ModrinthEnvironment.SINGLEPLAYER_ONLY -> "client"

					ModrinthEnvironment.DEDICATED_SERVER_ONLY -> "server"

					ModrinthEnvironment.SERVER_ONLY, ModrinthEnvironment.SERVER_ONLY_CLIENT_OPTIONAL,
					ModrinthEnvironment.CLIENT_ONLY_SERVER_OPTIONAL, ModrinthEnvironment.CLIENT_AND_SERVER,
					ModrinthEnvironment.CLIENT_OR_SERVER_PREFERS_BOTH, ModrinthEnvironment.CLIENT_OR_SERVER -> "*"
				},
				accessWidener = "aw/${ctx.currentMcVersion}.accesswidener".takeIf { awFile.exists() },
				entrypoints = buildMap {
//					"main" to listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.FabricEntrypoint"),
//					"client" to listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.FabricClientEntrypoint"),
//					"fabric-datagen" to listOf("${ctx.modGroup}.${ctx.modId}.platform.fabric.datagen.FabricDataGeneratorEntrypoint")
					if (ctx.project.hasProperty("deps.gametest")) {
						put("fabric-gametest", listOf("${ctx.modGroup}.${ctx.modId}.test.GoatVariantGameTests"))
					}
				},
				mixins = listOf("${ctx.modId}.mixins.json"),
				depends = ctx.extension.dependencies.required.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				recommends = ctx.extension.dependencies.optional.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				breaks = ctx.extension.dependencies.incompatible.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				provides = ctx.extension.dependencies.embeds.map { it.modid.get() }
			)
			return JSON.encodeToString(manifest)
		}
	}

	sealed class ForgeLike(id: String) : Loader(id) {
		override val excludedResources = listOf(
			"fabric.mod.json", "aw/*.accesswidener", ".cache"
		)
		open val modLoaderId: String = "javafml"

		override fun generateManifest(ctx: Context): String {
			val forgeDeps = mutableListOf<ForgeDependency>()

			fun addDeps(container: NamedDomainObjectContainer<Dependency>, type: String) {
				container.forEach {
					forgeDeps.add(
						ForgeDependency(
							modId = it.modid.get(),
							side = it.environment.get().uppercase(Locale.ROOT),
							versionRange = it.forgeLikeVersionRange.get(),
							mandatory = type == "required",
							type = type
						)
					)
				}
			}

			addDeps(ctx.extension.dependencies.required, "required")
			addDeps(ctx.extension.dependencies.optional, "optional")
			addDeps(ctx.extension.dependencies.incompatible, "incompatible")

			val atFile = ctx.project.file("src/main/resources/aw/${ctx.stonecutter.current.version}.cfg")

			val manifest = ForgeManifest(
				modLoader = modLoaderId,
				license = ctx.licenseName,
				issueTrackerURL = ctx.issuesUrl,
				clientSideOnly = !ctx.environmentPhysicalServer,
				mods = listOf(
					ForgeMod(
						modId = ctx.modId,
						displayName = ctx.modName,
						version = ctx.baseVersion,
						displayURL = ctx.homepageUrl,
						modUrl = ctx.homepageUrl,
						logoFile = "assets/icon.png",
						iconFile = "assets/icon.png",
						bannerFile = "assets/banner.png",
						authors = ctx.authors.joinToString(", "),
						credits = "${ctx.authors.joinToString(", ")} Contributors: ${ctx.contributors.joinToString(", ")}",
						description = ctx.description
					)
				),
				dependencies = mapOf(ctx.modId to forgeDeps),
				mixins = listOf(ForgeMixin("${ctx.modId}.mixins.json")),
				accessTransformers = if (atFile.exists()) {
					listOf(ForgeAccessTransformer("aw/${ctx.stonecutter.current.version}.cfg"))
				} else {
					emptyList()
				}
			)

			return TOML.encodeToString(manifest)
		}
	}

	object NeoForge : ForgeLike("neoforge") {
		override val modManifestPath = "META-INF/neoforge.mods.toml"
		override val excludedResources = (super.excludedResources + "META-INF/mods.toml") + "pack.mcmeta"
	}

	object Forge : ForgeLike("forge") {
		override val modManifestPath = "META-INF/mods.toml"
		override val excludedResources = super.excludedResources + "META-INF/neoforge.mods.toml"
		// No @Mod-annotated entrypoint exists anywhere in this mixin-only mod.
		// Legacy Forge's javafml loader hard-requires one ("has mods that were
		// not found" at launch); lowcodefml is Forge's own loader type for
		// exactly this case. NeoForge's javafml tolerates the same setup fine,
		// confirmed via a real launch, so it keeps the default.
		override val modLoaderId = "lowcodefml"
		val mixinConfigAttribute = "MixinConfigs"
	}

	companion object {
		fun of(id: String): Loader = when (id) {
			"fabric" -> Fabric
			"neoforge" -> NeoForge
			"forge" -> Forge
			else -> error("Unknown loader: '$id'")
		}
	}
}
