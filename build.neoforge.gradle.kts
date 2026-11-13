plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
}

stonecutter {
	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("location()", "identifier()")
	}
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = prop("deps.minecraft")
		}
		required("neoforge") {
			forgeLikeVersionRange.set("[${prop("deps.neoforge")},)")
		}
	}
}

neoForge {
	version = prop("deps.neoforge")
	val atFile = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg")
	if (atFile.exists()) {
		accessTransformers.from(atFile)
	} else {
		accessTransformers.from()
	}
	validateAccessTransformers = true

	if (sc.properties.contains("deps.parchment")) parchment {
		val (mc, ver) = prop("deps.parchment").split(':')
		mappingsVersion = ver
		minecraftVersion = mc
	}

	runs {
		val devJvmArgs = propsList("mod", "dev_jvm_args") +
			propsList("mod", "dev_jvm_args_mixin_debug")
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${stonecutter.current.version})"
			programArgument("--username=Dev")
			jvmArguments.addAll(devJvmArgs)
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "NeoForge Server (${stonecutter.current.version})"
			jvmArguments.addAll(devJvmArgs)
		}
	}

	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
	sourceSets["main"].resources.srcDir("${rootDir}/versions/datagen/${sc.current.version}/src/main/generated")
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	maven {
		url = uri("https://maven2.bai.lol")
		content {
			includeGroup("lol.bai")
			includeGroup("mcp.mobius.waila")
		}
	}
}

dependencies {
	// implementation(libs.moulberry.mixinconstraints)
	// jarJar(libs.moulberry.mixinconstraints)
	if (project.hasProperty("deps.wthit")) {
		val wthitApiVersion = if (project.hasProperty("deps.wthit-api")) prop("deps.wthit-api") else prop("deps.wthit")
		compileOnly("mcp.mobius.waila:wthit-api:neo-$wthitApiVersion")
		runtimeOnly("mcp.mobius.waila:wthit:neo-${prop("deps.wthit")}")
		// BadPackets didn't exist yet for WTHIT releases old enough to still
		// target 1.18/1.18.1 (its own first release postdates WTHIT's last
		// 1.18.1 build by a month, confirmed via Modrinth) - optional here.
		if (project.hasProperty("deps.badpackets")) {
			runtimeOnly("lol.bai:badpackets:neo-${prop("deps.badpackets")}")
		}
	}
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}

// Schema differs by WTHIT generation (legacy wants "initializer", modern
// wants "entrypoints" and breaks if both are present) - generated per
// version instead of static.
val wailaPluginsJsonContent = if (project.hasProperty("deps.wthit-legacy-api")) {
	"""{"markedgoats:plugin":{"initializer":"kiwi.allantaylor.markedgoats.MarkedGoatsWailaPlugin","side":"client"}}"""
} else {
	"""{"markedgoats:plugin":{"entrypoints":{"common":"kiwi.allantaylor.markedgoats.MarkedGoatsWailaPlugin","client":"kiwi.allantaylor.markedgoats.MarkedGoatsWailaPlugin"},"side":"client"}}"""
}
val generateWailaPluginsJson = tasks.register("generateWailaPluginsJson") {
	val outputFile = layout.buildDirectory.file("generated/modManifest/waila_plugins.json")
	val jsonContent = wailaPluginsJsonContent

	inputs.property("jsonContent", jsonContent)
	outputs.file(outputFile)

	doLast {
		val file = outputFile.get().asFile
		file.parentFile.mkdirs()
		file.writeText(jsonContent)
	}
}
tasks.named("processResources") { dependsOn(generateWailaPluginsJson) }
tasks.matching { it.name == "sourcesJar" }.configureEach {
    dependsOn("generateWailaPluginsJson")
}

val syncTestDatapack = tasks.register("syncTestDatapack") {
    val sourcePack = rootProject.file("dev/test_datapack")
    val savesDir = layout.projectDirectory.dir("run/saves").asFile

    doLast {
        if (sourcePack.exists() && savesDir.exists()) {
            savesDir.listFiles()?.filter { it.isDirectory }?.forEach { worldDir ->
                val targetPack = File(worldDir, "datapacks/test_datapack")
                sourcePack.copyRecursively(targetPack, overwrite = true)
            }
        }
    }
}

tasks.matching { it.name.startsWith("runClient") || it.name.startsWith("runServer") }.configureEach {
    dependsOn(syncTestDatapack)
}
