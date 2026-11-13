package kiwi.allantaylor.markedgoats.mixin;

import kiwi.allantaylor.markedgoats.MarkedGoatRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import kiwi.allantaylor.markedgoats.util.GoatVariantUtil;
import net.minecraft.client.renderer.entity.GoatRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;

//? if >=1.21.2 {
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.entity.state.GoatRenderState;
import java.util.WeakHashMap;
//?}

/**
 * Mixins for the GoatRenderer
 * Captures some entities, replaces some textures
 */
@Mixin(GoatRenderer.class)
public class GoatMixins {
	//? if <1.21 {
	/*@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/goat/Goat;)Lnet/minecraft/resources/ResourceLocation;",
			at = @At("HEAD"), cancellable = true)
	public void getTexture(Goat goatEntity, CallbackInfoReturnable<ResourceLocation> cir) {
		String variety = GoatVariantUtil.getInstrumentNameFromGoat(goatEntity);
		cir.setReturnValue(new ResourceLocation("markedgoats", variety + ".png"));
	}
	*///?}
	//? if >=1.21 && <1.21.2 {
	/*@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/goat/Goat;)Lnet/minecraft/resources/ResourceLocation;",
			at = @At("HEAD"), cancellable = true)
	public void getTexture(Goat goatEntity, CallbackInfoReturnable<ResourceLocation> cir) {
		String variety = GoatVariantUtil.getInstrumentNameFromGoat(goatEntity);
		cir.setReturnValue(ResourceLocation.fromNamespaceAndPath("markedgoats", variety + ".png"));
	}
	*///?}
	//? if >=1.21.2 {

	@Inject(method = "createRenderState()Lnet/minecraft/client/renderer/entity/state/GoatRenderState;",
			at = @At("HEAD"), cancellable = true)
	private void onCreateRenderState(CallbackInfoReturnable<GoatRenderState> cir) {
		cir.setReturnValue(new MarkedGoatRenderState());
	}

	/**
	 * Capture the association between the GoatEntity and GoatEntityRenderState during updateRenderState.
	 * @param goatEntity The goat - used to get UUID and isScreaming later
	 * @param goatEntityRenderState Matching render state (so we can find the goat later)
	 * @param f ignored
	 * @param ci ignored
	 */
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/goat/Goat;Lnet/minecraft/client/renderer/entity/state/GoatRenderState;F)V",
            at = @At("HEAD"))
    public void captureEntityRenderState(Goat goatEntity, GoatRenderState goatEntityRenderState, float f, CallbackInfo ci) {
        if (goatEntityRenderState instanceof MarkedGoatRenderState state) {
			state.hornVariant = GoatVariantUtil.getInstrumentNameFromGoat(goatEntity);
		}
    }

	/**
	 * Override the texture if the render state corresponds to a screaming goat.
	 * @param goatEntityRenderState used to find the original goat
	 * @param cir used to replace the texture
	 */
	@Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/GoatRenderState;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"), cancellable = true)
    public void getTexture(GoatRenderState goatEntityRenderState, CallbackInfoReturnable<ResourceLocation> cir) {
		if (goatEntityRenderState instanceof MarkedGoatRenderState state) {
            String variety = state.hornVariant;
			//? if >= 26.1-snapshot-6 {
			/*// Tiny Takeover: New baby goat model added. Requires own texture
			if (state.isBaby) {
				cir.setReturnValue(ResourceLocation.fromNamespaceAndPath("markedgoats", variety + "_baby.png"));
				return;
			}
			*///?}
			cir.setReturnValue(ResourceLocation.fromNamespaceAndPath("markedgoats", variety + ".png"));
        }
    }
	//?}
}
