package kiwi.allantaylor.markedgoats;

import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.item.component.UseCooldown;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static kiwi.allantaylor.markedgoats.util.GoatVariantUtil.getInstrumentNameFromGoat;

public class MarkedGoatsWailaPlugin implements IWailaCommonPlugin, IWailaClientPlugin, IEntityComponentProvider {

    public static final Identifier SHOW_ICON = Identifier.parse("marked_goats:show_icon");
    public static final Identifier SHOW_INSTRUMENT = Identifier.parse("marked_goats:show_instrument");
    public static final Identifier SHOW_IS_SCREAMING = Identifier.parse("marked_goats:show_is_screaming");

    // Remove the cooldown overlay from icons
    public static final UseCooldown FAKE_COOLDOWN = new UseCooldown(0, Optional.of(Identifier.parse("marked_goats:fake_horn_cooldown")));

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

            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(fullName));
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
