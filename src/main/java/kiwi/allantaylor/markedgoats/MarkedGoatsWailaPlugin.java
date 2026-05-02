package kiwi.allantaylor.markedgoats;

import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static kiwi.allantaylor.markedgoats.util.GoatVariantUtil.getInstrumentNameFromGoat;

public class MarkedGoatsWailaPlugin implements IWailaCommonPlugin, IWailaClientPlugin, IEntityComponentProvider {

    public static final Identifier SHOW_ICON = Identifier.of("marked_goats:show_icon");
    public static final Identifier SHOW_INSTRUMENT = Identifier.of("marked_goats:show_instrument");
    public static final Identifier SHOW_IS_SCREAMING = Identifier.of("marked_goats:show_is_screaming");

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.localConfig(SHOW_ICON, true);
        registrar.localConfig(SHOW_INSTRUMENT, true);
        registrar.localConfig(SHOW_IS_SCREAMING, true);
    }

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.body(this, GoatEntity.class);
        registrar.icon(this, GoatEntity.class);
    }

    @Override
    public void appendBody(@NotNull ITooltip tooltip, IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof GoatEntity entity)) {
            return;
        }
        if (config.getBoolean(SHOW_INSTRUMENT)) {
            String instrumentName = getInstrumentNameFromGoat(entity);
            tooltip.addLine(Text.translatable("instrument.minecraft." + instrumentName + "_goat_horn"));
        }
        if (config.getBoolean(SHOW_IS_SCREAMING)) {
            tooltip.addLine(Text.translatable("marked_goats." + (entity.isScreaming() ? "screaming" : "normal")));
        }
    }

    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, @NotNull IPluginConfig config) {
        if (!(accessor.getEntity() instanceof GoatEntity entity)) {
            return null;
        }
        if (config.getBoolean(SHOW_ICON)) {
            var stack = entity.getGoatHornStack();
            return new ItemComponent(stack);
        }
        return null;
    }
}
