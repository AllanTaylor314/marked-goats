package kiwi.allantaylor.markedgoats.util;

import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.item.Instrument;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.random.Random;

public class GoatVariantUtil {

    @Unique
    private static String SUFFIX = "_goat_horn";

    /**
     * Converts a goat horn instrument entry to a simple variety string.
     * Example: "minecraft:ponder_goat_horn" -> "ponder"
     */
    public static String getNameFromInstrument(RegistryEntry<Instrument> instrumentEntry) {
        return instrumentEntry.getKey()
                .map(key -> {
                    String path = key.getValue().getPath();
                    if (path.endsWith(SUFFIX)) {
                        return path.substring(0, path.length() - SUFFIX.length());
                    }
                    return path;
                })
                .orElse("");
    }

    public static String getInstrumentNameFromGoat(GoatEntity goatEntity) {
        Random random = Random.create((long) goatEntity.getUuid().hashCode());
        TagKey<Instrument> tagKey = goatEntity.isScreaming() ? InstrumentTags.SCREAMING_GOAT_HORNS
                : InstrumentTags.REGULAR_GOAT_HORNS;
        return goatEntity.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.INSTRUMENT)
                .getRandomEntry(tagKey, random).map((instrument) -> {
                    return getNameFromInstrument(instrument);
                }).orElse(null);
    }
}
