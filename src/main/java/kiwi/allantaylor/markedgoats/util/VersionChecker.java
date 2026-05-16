package kiwi.allantaylor.markedgoats.util;

import net.fabricmc.loader.api.*;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class VersionChecker {
    private static final boolean POST_TINY_TAKEOVER;

    static {
        boolean result = false;
        // Grab the metadata for the game itself (Minecraft)
        ModMetadata minecraftMeta = FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(ModContainer::getMetadata)
                .orElse(null);

        if (minecraftMeta != null) {
            Version currentVersion = minecraftMeta.getVersion();
            try {
                // Baby goats were remodelled in this snapshot
                SemanticVersion targetVersion = SemanticVersion.parse("26.1-snapshot-6");
                result = currentVersion.compareTo(targetVersion) >= 0;
            } catch (VersionParsingException e) {
                throw new RuntimeException(e);
            }
        }
        POST_TINY_TAKEOVER = result;
    }

    public static boolean isPostTinyTakeover() {
        return POST_TINY_TAKEOVER;
    }
}