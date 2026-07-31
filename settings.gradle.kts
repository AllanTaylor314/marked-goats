pluginManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
		maven {
			url = uri("https://maven2.bai.lol")
			content {
				includeGroup("lol.bai")
				includeGroup("mcp.mobius.waila")
			}
		}
	}
	includeBuild("build-logic")
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.9.2"
}

stonecutter {
	create(rootProject) {
		fun match(version: String, vararg loaders: String) =
			loaders.forEach { version("$version-$it", version).buildscript = getBuildscript(it, version) }

		// match("1.17.1", "fabric") /* fletching-table unsupported on JVM 16 */
		match("1.18.1", "fabric")
		match("1.18.2", "fabric")
		match("1.19.2", "fabric", "forge")
		match("1.19.3", "fabric")
		match("1.19.4", "fabric")
		match("1.20.1", "fabric")
		match("1.20.2", "fabric")
		match("1.20.4", "fabric")
		match("1.20.6", "fabric")
		match("1.21.1", "fabric", "neoforge")
		match("1.21.3", "fabric")
		match("1.21.4", "fabric")
		match("1.21.5", "fabric")
		match("1.21.7", "fabric", "neoforge")
		match("1.21.10", "fabric")
		match("1.21.11", "fabric")
		match("26.1.2", "fabric", "neoforge")
		match("26.2", "fabric")

		vcsVersion = "1.21.7-fabric"
	}
}

private fun getBuildscript(loader: String, version: String): String {
	if (loader == "fabric") {
		return if (version.startsWith("1.")) {
			"build.fabric-o.gradle.kts"
		} else {
			"build.fabric-m.gradle.kts"
		}
	}
	return "build.$loader.gradle.kts"
}
