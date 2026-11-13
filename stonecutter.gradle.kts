@file:OptIn(dev.kikugie.stonecutter.StonecutterExperimentalAPI::class)

import kotlin.collections.set


plugins {
	alias(libs.plugins.fabric.loom).apply(false)
	alias(libs.plugins.fabric.loom.remap).apply(false)
	alias(libs.plugins.neoforged.moddev).apply(false)
	alias(libs.plugins.jsonlang.postprocess).apply(false)
	alias(libs.plugins.mod.publish.plugin).apply(false)
	alias(libs.plugins.kotlin.jvm).apply(false)
	alias(libs.plugins.devtools.ksp).apply(false)
	alias(libs.plugins.fletching.table).apply(false)
	alias(libs.plugins.legacyforge.moddev).apply(false)
}

stonecutter active file(".sc_active_version")

tasks.register("runActiveClient") {
	group = "stonecutter"
	description = "Run client of the active Stonecutter version"
	dependsOn(stonecutter.current!!.project + ":runClient")
}

tasks.register("runActiveServer") {
	group = "stonecutter"
	description = "Run server of the active Stonecutter version"
	dependsOn(stonecutter.current!!.project + ":runServer")
}

stonecutter parameters {
	val (version, loader) = current.project.split('-', limit = 2)
	constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
	swaps["mod_version"] = "\"${findProperty("mod.version")}\";"
	swaps["mod_id"] = "\"${findProperty("mod.id")}\";"
	swaps["mod_name"] = "\"${findProperty("mod.name")}\";"
	swaps["mod_group"] = "\"${findProperty("mod.group")}\";"
	swaps["minecraft"] = "\"${current.version}\";"
	constants["release"] = findProperty("mod.id") != "modtemplate"
	constants["wthit_plugin"] = findProperty("$loader.$version.deps.wthit") != null
	constants["wthit_plugin_legacy"] = findProperty("$loader.$version.deps.wthit-legacy-api") != null
	constants["gametest"] = findProperty("$loader.$version.deps.gametest") != null
}

for (version in stonecutter.versions.map { it.version }.distinct()) tasks.register("publish$version") {
	group = "publishing"
	dependsOn(stonecutter.tasks.named("publishMods") { metadata.version == version })
}
