package kiwi.allantaylor.markedgoats;
//? if wthit_plugin && >=1.21.2 {
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.item.component.UseCooldown;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static kiwi.allantaylor.markedgoats.util.GoatVariantUtil.getInstrumentNameFromGoat;

/**
 * A plugin for WAILA
 * Adds horn icon (use with resource pack), instrument type, and "Screaming Goat" in name
 */
public class MarkedGoatsWailaPlugin implements IWailaCommonPlugin, IWailaClientPlugin, IEntityComponentProvider {

	/// Whether to show the horn icon in the HUD
	public static final ResourceLocation SHOW_ICON = ResourceLocation.parse("marked_goats:show_icon");
	/// Whether to show the instrument name (e.g. Ponder) in the HUD
	public static final ResourceLocation SHOW_INSTRUMENT = ResourceLocation.parse("marked_goats:show_instrument");
	/// Whether to replace "Goat" with "Screaming Goat" in the entity name
	public static final ResourceLocation SHOW_IS_SCREAMING = ResourceLocation.parse("marked_goats:show_is_screaming");

    /// A fake cooldown component to apply to the horn icon so that it doesn't show the cooldown overlay when the player has recently used the horn.
	private static final UseCooldown FAKE_COOLDOWN = new UseCooldown(0, Optional.of(ResourceLocation.parse("marked_goats:fake_horn_cooldown")));

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.localConfig(SHOW_ICON, true);
        registrar.localConfig(SHOW_INSTRUMENT, true);
        registrar.localConfig(SHOW_IS_SCREAMING, true);
    }

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.head(this, Goat.class);
        registrar.body(this, Goat.class);
        registrar.icon(this, Goat.class);
    }

    @Override
    public void appendHead(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (accessor.getEntity() instanceof Goat goat && goat.isScreamingGoat() && config.getBoolean(SHOW_IS_SCREAMING)) {
            var formatter = IWailaConfig.get().getFormatter();
            var customName = goat.getCustomName();
            Component fullName = Component.translatable("marked_goats.screaming_goat");

            if (customName != null) {
                fullName = customName.copy().append(" (").append(fullName).append(")");
            }

            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(fullName.getString()));
        }
    }

    @Override
    public void appendBody(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Goat entity)) {
            return;
        }
        if (config.getBoolean(SHOW_INSTRUMENT)) {
            String instrumentName = getInstrumentNameFromGoat(entity);
            tooltip.addLine(Component.translatable("instrument.minecraft." + instrumentName + "_goat_horn"));
        }
    }

    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Goat entity)) {
            return null;
        }
        if (config.getBoolean(SHOW_ICON)) {
            var stack = entity.createHorn();
            stack.set(DataComponents.USE_COOLDOWN, FAKE_COOLDOWN);
            return new ItemComponent(stack);
        }
        return null;
    }
}
//?}
//? if wthit_plugin && >=1.21 && <1.21.2 {
/*import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;
import org.jetbrains.annotations.NotNull;

import static kiwi.allantaylor.markedgoats.util.GoatVariantUtil.getInstrumentNameFromGoat;

public class MarkedGoatsWailaPlugin implements IWailaCommonPlugin, IWailaClientPlugin, IEntityComponentProvider {

    public static final ResourceLocation SHOW_ICON = ResourceLocation.parse("marked_goats:show_icon");
    public static final ResourceLocation SHOW_INSTRUMENT = ResourceLocation.parse("marked_goats:show_instrument");
    public static final ResourceLocation SHOW_IS_SCREAMING = ResourceLocation.parse("marked_goats:show_is_screaming");

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.localConfig(SHOW_ICON, true);
        registrar.localConfig(SHOW_INSTRUMENT, true);
        registrar.localConfig(SHOW_IS_SCREAMING, true);
    }

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.head(this, Goat.class);
        registrar.body(this, Goat.class);
        registrar.icon(this, Goat.class);
    }

    @Override
    public void appendHead(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (accessor.getEntity() instanceof Goat goat && goat.isScreamingGoat() && config.getBoolean(SHOW_IS_SCREAMING)) {
            var formatter = IWailaConfig.get().getFormatter();
            var customName = goat.getCustomName();
            Component fullName = Component.translatable("marked_goats.screaming_goat");

            if (customName != null) {
                fullName = customName.copy().append(" (").append(fullName).append(")");
            }

            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(fullName.getString()));
        }
    }

    @Override
    public void appendBody(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Goat entity)) {
            return;
        }
        if (config.getBoolean(SHOW_INSTRUMENT)) {
            String instrumentName = getInstrumentNameFromGoat(entity);
            tooltip.addLine(Component.translatable("instrument.minecraft." + instrumentName + "_goat_horn"));
        }
    }

    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Goat entity)) {
            return null;
        }
        if (config.getBoolean(SHOW_ICON)) {
            // No use_cooldown component exists yet at this version to fake out the
            // cooldown overlay (added in 1.21.2's item-component overhaul) - a fresh
            // stack from createHorn() has no real cooldown applied anyway.
            return new ItemComponent(entity.createHorn());
        }
        return null;
    }
}
*///?}
//? if wthit_plugin && <1.21 && !wthit_plugin_legacy {
/*import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;
import org.jetbrains.annotations.NotNull;

import static kiwi.allantaylor.markedgoats.util.GoatVariantUtil.getInstrumentNameFromGoat;

public class MarkedGoatsWailaPlugin implements IWailaCommonPlugin, IWailaClientPlugin, IEntityComponentProvider {

    public static final ResourceLocation SHOW_ICON = new ResourceLocation("marked_goats", "show_icon");
    public static final ResourceLocation SHOW_INSTRUMENT = new ResourceLocation("marked_goats", "show_instrument");
    public static final ResourceLocation SHOW_IS_SCREAMING = new ResourceLocation("marked_goats", "show_is_screaming");

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.localConfig(SHOW_ICON, true);
        registrar.localConfig(SHOW_INSTRUMENT, true);
        registrar.localConfig(SHOW_IS_SCREAMING, true);
    }

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.head(this, Goat.class);
        registrar.body(this, Goat.class);
        registrar.icon(this, Goat.class);
    }

    @Override
    public void appendHead(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (accessor.getEntity() instanceof Goat goat && goat.isScreamingGoat() && config.getBoolean(SHOW_IS_SCREAMING)) {
            var formatter = IWailaConfig.get().getFormatter();
            var customName = goat.getCustomName();
            Component fullName = Component.translatable("marked_goats.screaming_goat");

            if (customName != null) {
                fullName = customName.copy().append(" (").append(fullName).append(")");
            }

            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(fullName.getString()));
        }
    }

    @Override
    public void appendBody(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Goat entity)) {
            return;
        }
        if (config.getBoolean(SHOW_INSTRUMENT)) {
            String instrumentName = getInstrumentNameFromGoat(entity);
            tooltip.addLine(Component.translatable("instrument.minecraft." + instrumentName + "_goat_horn"));
        }
    }

    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Goat entity)) {
            return null;
        }
        if (config.getBoolean(SHOW_ICON)) {
            return new ItemComponent(entity.createHorn());
        }
        return null;
    }
}
*///?}
//? if wthit_plugin_legacy && >=1.19 {
/*import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;
import org.jetbrains.annotations.NotNull;

import static kiwi.allantaylor.markedgoats.util.GoatVariantUtil.getInstrumentNameFromGoat;

// Old unified WTHIT API: one IWailaPlugin.register(IRegistrar) instead of
// the split IWailaCommonPlugin/IWailaClientPlugin. Tooltip methods below
// are the same as the modern branch, only registration differs.
public class MarkedGoatsWailaPlugin implements IWailaPlugin, IEntityComponentProvider {

    public static final ResourceLocation SHOW_ICON = new ResourceLocation("marked_goats", "show_icon");
    public static final ResourceLocation SHOW_INSTRUMENT = new ResourceLocation("marked_goats", "show_instrument");
    public static final ResourceLocation SHOW_IS_SCREAMING = new ResourceLocation("marked_goats", "show_is_screaming");

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(SHOW_ICON, true);
        registrar.addConfig(SHOW_INSTRUMENT, true);
        registrar.addConfig(SHOW_IS_SCREAMING, true);
        registrar.addComponent(this, TooltipPosition.HEAD, Goat.class);
        registrar.addComponent(this, TooltipPosition.BODY, Goat.class);
        registrar.addIcon(this, Goat.class);
    }

    @Override
    public void appendHead(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (accessor.getEntity() instanceof Goat goat && goat.isScreamingGoat() && config.getBoolean(SHOW_IS_SCREAMING)) {
            var formatter = IWailaConfig.get().getFormatter();
            var customName = goat.getCustomName();
            Component fullName = Component.translatable("marked_goats.screaming_goat");

            if (customName != null) {
                fullName = customName.copy().append(" (").append(fullName).append(")");
            }

            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(fullName.getString()));
        }
    }

    @Override
    public void appendBody(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Goat entity)) {
            return;
        }
        if (config.getBoolean(SHOW_INSTRUMENT)) {
            String instrumentName = getInstrumentNameFromGoat(entity);
            tooltip.addLine(Component.translatable("instrument.minecraft." + instrumentName + "_goat_horn"));
        }
    }

    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Goat entity)) {
            return null;
        }
        if (config.getBoolean(SHOW_ICON)) {
            return new ItemComponent(entity.createHorn());
        }
        return null;
    }
}
*///?}
//? if wthit_plugin_legacy && <1.19 {
/*import mcp.mobius.waila.api.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;
import org.jetbrains.annotations.NotNull;

import static kiwi.allantaylor.markedgoats.util.GoatVariantUtil.getInstrumentNameFromGoat;

// Pre-1.19: no goat horn item exists yet, so no icon (no SHOW_ICON,
// no addIcon, no getIcon). Component has no static translatable()/
// literal() factories yet either, so text uses the direct constructors,
// and instrument name is a plain literal since no vanilla key exists.
public class MarkedGoatsWailaPlugin implements IWailaPlugin, IEntityComponentProvider {

    public static final ResourceLocation SHOW_INSTRUMENT = new ResourceLocation("marked_goats", "show_instrument");
    public static final ResourceLocation SHOW_IS_SCREAMING = new ResourceLocation("marked_goats", "show_is_screaming");

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(SHOW_INSTRUMENT, true);
        registrar.addConfig(SHOW_IS_SCREAMING, true);
        registrar.addComponent(this, TooltipPosition.HEAD, Goat.class);
        registrar.addComponent(this, TooltipPosition.BODY, Goat.class);
    }

    @Override
    public void appendHead(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (accessor.getEntity() instanceof Goat goat && goat.isScreamingGoat() && config.getBoolean(SHOW_IS_SCREAMING)) {
            var formatter = IWailaConfig.get().getFormatter();
            var customName = goat.getCustomName();
            Component fullName = new TranslatableComponent("marked_goats.screaming_goat");

            if (customName != null) {
                fullName = customName.copy().append(" (").append(fullName).append(")");
            }

            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(fullName.getString()));
        }
    }

    @Override
    public void appendBody(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Goat entity)) {
            return;
        }
        if (config.getBoolean(SHOW_INSTRUMENT)) {
            String instrumentName = getInstrumentNameFromGoat(entity);
            // No vanilla translation exists at this version to capitalize it
            // for us (see the class-level comment above), so do it by hand.
            String capitalized = instrumentName.substring(0, 1).toUpperCase() + instrumentName.substring(1);
            tooltip.addLine(new TextComponent(capitalized));
        }
    }
}
*///?}
