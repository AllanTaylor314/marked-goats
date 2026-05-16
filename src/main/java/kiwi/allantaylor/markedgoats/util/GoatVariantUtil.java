package kiwi.allantaylor.markedgoats.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.item.Instrument;
import org.spongepowered.asm.mixin.Unique;

public class GoatVariantUtil {

    @Unique
    private static String SUFFIX = "_goat_horn";

    /**
     * Converts a goat horn instrument entry to a simple variety string.
     * Example: "minecraft:ponder_goat_horn" -> "ponder"
     */
    public static String getNameFromInstrument(Holder<Instrument> instrumentEntry) {
        return instrumentEntry.unwrapKey()
                .map(key -> {
                    String path = key.identifier().getPath();
                    if (path.endsWith(SUFFIX)) {
                        return path.substring(0, path.length() - SUFFIX.length());
                    }
                    return path;
                })
                .orElse("");
    }

    public static String getInstrumentNameFromGoat(Goat goatEntity) {
        return getNameFromInstrument(goatEntity.createHorn().get(DataComponents.INSTRUMENT).instrument());
    }
}
