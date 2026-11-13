plugins {
	id("mod-platform")
	id("net.fabricmc.fabric-loom-remap")
}

stonecutter {
	val (version, loader) = current.project.split('-', limit = 2)
	properties.tags(version, loader)

	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("location()", "identifier()")
	}
	replacements.string(current.parsed >= "26.1.2") {
		replace("FabricDataOutput", "FabricPackOutput")
	}
}

platform {
	loader = "fabric-o"
	dependencies {
		required("minecraft") {
			fabricLikeVersionRange = if (project.hasProperty("deps.minecraft-range")) prop("deps.minecraft-range") else prop("deps.minecraft")
		}
		required("fabric-api") {
			if (project.hasProperty("deps.fabric-api-id")) modid.set(prop("deps.fabric-api-id"))
			slug("fabric-api")
			fabricLikeVersionRange = ">=${prop("deps.fabric-api")}"
		}
		required("fabricloader") {
			fabricLikeVersionRange = ">=${prop("deps.fabric-loader")}"
		}
		optional("modmenu") {}
	}
}

loom {
	accessWidenerPath.set(
		rootProject.file("src/main/resources/aw/${sc.current.version}.accesswidener").takeIf { it.exists() }
	)
	runs.named("client") {
		client()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Dev")
		configName = "Fabric Client"
	}
	runs.named("server") {
		server()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "server"
		configName = "Fabric Server"
	}
}

fabricApi {
	configureDataGeneration {
		outputDirectory = file("${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated")
		client = true
	}
	if (project.hasProperty("deps.gametest")) {
		configureTests {
			// CI has no EULA prompt to accept - this is a local dev/test
			// environment, not a public server.
			eula.set(true)
		}
	}
}

repositories {
	mavenCentral()
	strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc") { name = "TerraformersMC" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	maven {
		url = uri("https://maven2.bai.lol")
		content {
			includeGroup("lol.bai")
			includeGroup("mcp.mobius.waila")
		}
	}
}

configurations.all {
	resolutionStrategy {
		force("net.fabricmc:fabric-loader:${prop("deps.fabric-loader")}")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
	mappings(
		loom.layered {
			officialMojangMappings()
			if (hasProperty("deps.parchment")) parchment("org.parchmentmc.data:parchment-${prop("deps.parchment")}@zip")
		})
	modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric-loader")}")
	// implementation(libs.moulberry.mixinconstraints)
	// include(libs.moulberry.mixinconstraints)
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	modLocalRuntime("com.terraformersmc:modmenu:${prop("deps.modmenu")}")
	if (project.hasProperty("deps.gametest")) {
		modImplementation(fabricApi.module("fabric-gametest-api-v1", prop("deps.fabric-api")))
	}
	if (project.hasProperty("deps.wthit")) {
		val wthitApiVersion = if (project.hasProperty("deps.wthit-api")) prop("deps.wthit-api") else prop("deps.wthit")
		modCompileOnly("mcp.mobius.waila:wthit-api:fabric-$wthitApiVersion")
		modRuntimeOnly("mcp.mobius.waila:wthit:fabric-${prop("deps.wthit")}")
		// BadPackets didn't exist yet for WTHIT releases old enough to still
		// target 1.18/1.18.1 (its own first release postdates WTHIT's last
		// 1.18.1 build by a month, confirmed via Modrinth) - optional here.
		if (project.hasProperty("deps.badpackets")) {
			modRuntimeOnly("lol.bai:badpackets:fabric-${prop("deps.badpackets")}")
		}
	}
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
	outputs.file(outputFile)
	doLast {
		val file = outputFile.get().asFile
		file.parentFile.mkdirs()
		file.writeText(wailaPluginsJsonContent)
	}
}
tasks.named("processResources") { dependsOn(generateWailaPluginsJson) }
tasks.matching { it.name == "sourcesJar" }.configureEach {
    dependsOn("generateWailaPluginsJson")
}

val syncTestDatapack by tasks.registering {
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
