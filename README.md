### Marked Goats

#### Adds 8 unique textures for goats based on which horn they drop. The textures can be fully customised with a resource pack

---

This branch is a rewrite onto [Stonecutter](https://stonecutter.kikugie.dev/), so the mod builds for
**Fabric**, **NeoForge**, and **Forge** across multiple Minecraft versions from a single codebase, instead of
one branch per version.

Bashing your head against a wall trying to work out which "random" goat horn will drop? Worry not, for now you
can leave the head-bashing to the goats!

This mod adds a different texture for each of the 8 variants. For the built-in textures, screaming goats are
grey while normal goats retain most of the vanilla texture. Each goat has coloured bands on its horns and a
letter on its back. The colours are based on
[Goat Horns+](https://www.planetminecraft.com/texture-pack/1-19-goat-horns-optifine-cit-resewn-required/) (use
[Goat Horns+ Remastered](https://modrinth.com/project/eI9qDeU7) for 1.21.5+)

![Two rows of four Minecraft goats in roughly isometric view. Top row is white goats with red P, orange S, yellow S, and lime F letters on their backs. Bottom row is dark grey goats with green A, light blue C, blue Y, and purple D letters on their backs. Each goat has coloured bands on the horns that match the colour of the letter](https://cdn.modrinth.com/data/biTPC5IL/images/76b8f5732ae4ed29806103205cf0ebcb6bb55b01.png)

Pre-1.20 versions have no goat horn item at all, so the texture is picked by replicating vanilla's own
`Goat#createHorn` selection directly (a hash of the goat's UUID, gated on whether it's screaming) — it shows
what the goat would actually drop once the world is loaded in 1.20+.

---

### Installation

* Place the jar file in your `mods` folder.
  * Pick the jar matching your loader and Minecraft version — see the versions listed in
    `stonecutter.properties.toml`.

---

### Resource Packs

Don't like the defaults? Fair enough. It works with resource packs too. The pack structure is as follows, with
each texture based on the original goat one:

* pack.mcmeta
* assets
  * markedgoats
    * ponder.png
    * sing.png
    * seek.png
    * feel.png
    * admire.png
    * call.png
    * yearn.png
    * dream.png
    * ponder_baby.png *etc. (26.1+)*

There's a partial pack called [Mythic Goats](https://modrinth.com/project/o1WLT988) that works as a template.

Made a compatible pack? Let Allan know [via GitHub](https://github.com/AllanTaylor314/marked-goats/issues/1) and
he'll add it to [the collection](https://modrinth.com/collection/w7pqlpIr).

---

### Does this work on servers?

Yes, this client side mod works on singleplayer and multiplayer (unless the server is doing something really
weird and messing with entity UUIDs).

No, it won't do anything if you install it on a dedicated server.

---

### How does it work?

Which horn a goat drops is based on a pseudo-random number generated from the goat's UUID. This mod goes
through the same
[random number generation process](https://mcsrc.dev/1/1.21.11_unobfuscated/net/minecraft/world/entity/animal/goat/Goat#L103-112)
to work out which horn the goat has and choose the right texture for it. Below 1.20, where that process doesn't
exist yet, this branch reimplements it directly rather than skipping those versions.

---

### WTHIT Plugin

*Available on this branch for 1.19.2, 26.1.2, 26.2*

If you have [WTHIT](https://modrinth.com/project/6AQIaxuO) installed, Marked Goats provides an informative
overlay when looking at a goat, so you don't even have to memorise the texture patterns.

#### Features and Settings

- **Instrument Icon** `show_icon`
  - Use a resource pack like [Goat Horns+ Remastered](https://modrinth.com/project/eI9qDeU7), otherwise they all
    look the same
  - Not available below 1.20 — there's no real horn item to show an icon for
- **Instrument Name** `show_instrument`
  - Ponder, Sing, Seek, all localised with the vanilla translations
- **Screaming Status** `show_is_screaming`
  - "Screaming Goat" to differentiate it from regular old "Goat"
  - Named screaming goats will show up as "Your Goat's Name (Screaming Goat)"

Each of these can be toggled in the WTHIT Plugin Settings.

---

## Development

This branch is built on [Stonecutter](https://stonecutter.kikugie.dev/), which lets one codebase target
multiple Minecraft versions and loaders. Version/loader-specific code is written with Stonecutter comments:

```java
//? fabric {
fabricOnlyCode();
//?} else {
/*neoforgeOnlyCode();*/
//?}
```

```java
//? 1.21.7 {
LOGGER.info("hello 1.21.7!");
//?} else {
/*LOGGER.info("hello from any other version!");
 *///?}
```

For more, read the [Stonecutter documentation](https://stonecutter.kikugie.dev/wiki/).

### Configuration

Mod metadata and per-version/per-loader dependencies live in `stonecutter.properties.toml`
(e.g. `[fabric."1.21.7"]`).

### Access Wideners/Transformers

* Fabric Access Wideners: `src/main/resources/aw/*.accesswidener` (one per supported Minecraft version)
* (Neo)Forge Access Transformers: `src/main/resources/aw/*.cfg` (one per supported Minecraft version)

### Running in Development

The Gradle plugins of the respective platform provide run configurations. Be careful to run the correct task
for the Stonecutter version/loader you're targeting, e.g.:

```bash
./gradlew :1.21.7-fabric:runClient
```

### Using the CI

**`build.yml`** runs on every push and pull request — builds all versions and uploads the jars as artifacts.

**`release.yml`** runs when a tag is pushed — validates the tag against `mod.version` + `mod.channel_tag`,
builds all versions, generates a changelog via [`git-cliff`](https://git-cliff.org/), and publishes to whichever
platforms are enabled via the repository's Actions secrets/variables.

## License/Credits

MIT. Check `LICENSE` for details.

* This branch rebuilds the mod on [rotgruengelb/stonecutter-mod-template](https://github.com/rotgruengelb/stonecutter-mod-template)
* Uses [Stonecutter](https://stonecutter.kikugie.dev/) by KikuGie
