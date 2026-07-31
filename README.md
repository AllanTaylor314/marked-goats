### Marked Goats

#### A mod that differentiates screaming goats from non-screaming goats, and which horns they drop

---

This branch is a rewrite onto [Stonecutter](https://stonecutter.kikugie.dev/), so the mod builds for
**Fabric**, **NeoForge**, and **Forge** across multiple Minecraft versions from a single codebase, instead of
one branch per version.

### Features

* Adds a splash of colour to your goats, based on which horn they drop
* Adds a letter to their backs too
* Pre-1.20 versions have no goat horn item at all, so the texture is picked by replicating vanilla's own
  `Goat#createHorn` selection (a hash of the goat's UUID, gated on whether it's screaming) — it shows what the
  goat would actually drop once the world is loaded in 1.20+

![Included textures: white for normal goats and grey for screaming](https://cdn.modrinth.com/data/biTPC5IL/images/76b8f5732ae4ed29806103205cf0ebcb6bb55b01.png)

---

### Installation

* Place the jar file in your `mods` folder.
  * Pick the jar matching your loader and Minecraft version — see the versions listed in
    `stonecutter.properties.toml`.

---

### Customization

If you want to edit the texture to something more reasonable, here are step-by-step instructions as to how you
could do that.

1. *example pack to come - for now, see [assets folder](src/main/resources/assets/markedgoats)*
2. ~~Extract the zip file~~ *it will need a pack.mcmeta*
3. Open it, then go into `assets -> markedgoats`
4. Once there, you will see the file for each texture. Edit it as you please, or replace it - But make sure it
   has the same name when you are done.
5. Apply the resourcepack (No need to re-zip it) and enjoy!

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
