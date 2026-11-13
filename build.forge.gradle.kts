plugins {
	id("mod-platform")
	id("net.neoforged.moddev.legacyforge")
}

stonecutter {
	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("location()", "identifier()")
	}
}

platform {
	loader = "forge"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = prop("deps.minecraft")
		}
		required("forge") {
			forgeLikeVersionRange.set("[${prop("deps.forge")},)")
		}
	}
}

legacyForge {
	version = "${prop("deps.minecraft")}-${prop("deps.forge")}"

	validateAccessTransformers = true

	val atFile = rootProject.file("src/main/resources/aw/${sc.current.version}.cfg")
	if (atFile.exists()) {
		accessTransformers.from(atFile)
	} else {
		accessTransformers.from()
	}

	val devJvmArgs = propsList("mod", "dev_jvm_args") +
		propsList("mod", "dev_jvm_args_mixin_debug")
	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "Forge Client (${sc.current.version})"
			programArgument("--username=Dev")
			jvmArguments.addAll(devJvmArgs)
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "Forge Server (${sc.current.version})"
			jvmArguments.addAll(devJvmArgs)
		}
	}


	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
}

mixin {
	add(sourceSets.main.get(), "${prop("mod.id")}.mixins.refmap.json")
	config("${prop("mod.id")}.mixins.json")
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
}

dependencies {
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")

	// implementation(libs.moulberry.mixinconstraints)
	// jarJar(libs.moulberry.mixinconstraints)
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${sc.current.version}/src/main/generated"
		)
	}
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
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
