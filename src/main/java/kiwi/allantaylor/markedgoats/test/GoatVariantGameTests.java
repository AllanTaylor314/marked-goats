package kiwi.allantaylor.markedgoats.test;

//? if gametest {
/*import kiwi.allantaylor.markedgoats.util.GoatVariantUtil;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.goat.Goat;

import java.util.Set;
import java.util.UUID;
*///?}

@SuppressWarnings("MissingJavadoc")
public class GoatVariantGameTests {

	//? if gametest {
	/*private static final Set<String> REGULAR_INSTRUMENTS = Set.of("ponder", "sing", "seek", "feel");
	private static final Set<String> SCREAMING_INSTRUMENTS = Set.of("admire", "call", "yearn", "dream");

	@GameTest
	public void regularGoatGetsRegularInstrument(GameTestHelper helper) {
		Goat goat = helper.spawn(EntityType.GOAT, new BlockPos(2, 1, 2));
		goat.setUUID(UUID.fromString("11111111-1111-1111-1111-111111111111"));
		goat.setScreamingGoat(false);

		String instrument = GoatVariantUtil.getInstrumentNameFromGoat(goat);
		helper.assertTrue(REGULAR_INSTRUMENTS.contains(instrument),
				Component.literal("Expected a regular instrument, got '" + instrument + "'"));
		helper.succeed();
	}

	@GameTest
	public void screamingGoatGetsScreamingInstrument(GameTestHelper helper) {
		Goat goat = helper.spawn(EntityType.GOAT, new BlockPos(5, 1, 5));
		goat.setUUID(UUID.fromString("22222222-2222-2222-2222-222222222222"));
		goat.setScreamingGoat(true);

		String instrument = GoatVariantUtil.getInstrumentNameFromGoat(goat);
		helper.assertTrue(SCREAMING_INSTRUMENTS.contains(instrument),
				Component.literal("Expected a screaming instrument, got '" + instrument + "'"));
		helper.succeed();
	}
	*///?}
}
