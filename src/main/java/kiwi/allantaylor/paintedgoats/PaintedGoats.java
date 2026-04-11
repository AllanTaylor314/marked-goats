package kiwi.allantaylor.paintedgoats;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class PaintedGoats implements ClientModInitializer {

    private static final Logger logger = LoggerFactory.getLogger("PaintedGoats");

    @Override
    public void onInitializeClient() {
        logger.info("Pigifying screaming goats!");
    }

}
