package kiwi.allantaylor.markedgoats.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import kiwi.allantaylor.markedgoats.util.GoatVariantUtil;
import net.minecraft.client.renderer.entity.GoatRenderer;
import net.minecraft.client.renderer.entity.state.GoatRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.goat.Goat;
import java.util.WeakHashMap;

@Mixin(GoatRenderer.class)
public class GoatMixins {
    // Map to store the association between GoatEntityRenderState and GoatEntity
    @Unique
    private final WeakHashMap<GoatRenderState, Goat> renderStateToEntityMap = new WeakHashMap<>();

    /**
     * Capture the association between the GoatEntity and GoatEntityRenderState during updateRenderState.
     */
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/goat/Goat;Lnet/minecraft/client/renderer/entity/state/GoatRenderState;F)V",
            at = @At("HEAD"))
    public void captureEntityRenderState(Goat goatEntity, GoatRenderState goatEntityRenderState, float f, CallbackInfo ci) {
        renderStateToEntityMap.put(goatEntityRenderState, goatEntity);
    }

    /**
     * Override the texture if the render state corresponds to a screaming goat.
     */
    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/GoatRenderState;)Lnet/minecraft/resources/Identifier;",
            at = @At("HEAD"), cancellable = true)
    public void getTexture(GoatRenderState goatEntityRenderState, CallbackInfoReturnable<Identifier> cir) {
        Goat goatEntity = renderStateToEntityMap.get(goatEntityRenderState);
        if (goatEntity != null) {
            String variety = GoatVariantUtil.getInstrumentNameFromGoat(goatEntity);
            
            if (variety != null) {
                cir.setReturnValue(Identifier.fromNamespaceAndPath("markedgoats", variety + ".png"));
            }
        }
    }
}
