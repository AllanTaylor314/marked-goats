package kiwi.allantaylor.markedgoats.util;

import net.minecraft.world.entity.animal.goat.Goat;
import org.spongepowered.asm.mixin.Unique;

//? if >=1.20.5 && <1.21.5 {
/*import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Instrument;
*///?}
//? if >=1.21.5 && <26.1-snapshot-3 {
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Instrument;
//?}
//? if >=26.1-snapshot-3 {
/*import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Instrument;
*///?}

public class GoatVariantUtil {

    @Unique
    private static final String SUFFIX = "_goat_horn";

	//? if <1.20.5 {
	/*@Unique
	private static final String[] REGULAR_HORNS = {"ponder", "sing", "seek", "feel"};
	@Unique
	private static final String[] SCREAMING_HORNS = {"admire", "call", "yearn", "dream"};

	public static String getInstrumentNameFromGoat(Goat goatEntity) {
		String[] pool = goatEntity.isScreamingGoat() ? SCREAMING_HORNS : REGULAR_HORNS;
		return pool[new java.util.Random(goatEntity.getUUID().hashCode()).nextInt(pool.length)];
	}
	*///?}
	//? if >=1.20.5 && <1.21.5 {
	/*// DataComponents.INSTRUMENT is a plain Holder<Instrument> here - the
	// InstrumentComponent wrapper (and its .instrument() accessor) doesn't
	// exist yet, so there's nothing to unwrap first.
	public static String getInstrumentNameFromGoat(Goat goatEntity) {
		Holder<Instrument> instrumentHolder = goatEntity.createHorn().get(DataComponents.INSTRUMENT);
		return instrumentHolder.unwrapKey()
                .map(key -> {
                    String path = key.location().getPath();
                    if (path.endsWith(SUFFIX)) {
                        return path.substring(0, path.length() - SUFFIX.length());
                    }
                    return path;
                })
                .orElse("");
	}
	*///?}
	//? if >=1.21.5 {
    /**
     * Converts a goat horn instrument entry to a simple variety string.
     * Example: "minecraft:ponder_goat_horn" -> "ponder"
     */
	//~ if >=26.1-snapshot-3 'EitherHolder' -> 'Holder' {
	public static String getNameFromInstrument(EitherHolder<Instrument> instrumentEntry) {
		//~ if >=26.1-snapshot-3 '.key()' -> '.unwrapKey()'
		return instrumentEntry.key()
				//~ }
                .map(key -> {
                    String path = key.location().getPath();
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
	//?}
}
