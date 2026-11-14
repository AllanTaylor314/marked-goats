//? if >= 1.21.2 {
package kiwi.allantaylor.markedgoats.mixin;

import kiwi.allantaylor.markedgoats.MarkedGoatStateAccess;
import net.minecraft.client.renderer.entity.state.GoatRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GoatRenderState.class)
public class GoatRenderStateMixin implements MarkedGoatStateAccess {
	@Unique
	private String markedgoats$hornVariant;

	@Override
	public String markedgoats$getHornVariant() {
		return this.markedgoats$hornVariant;
	}

	@Override
	public void markedgoats$setHornVariant(String variant) {
		this.markedgoats$hornVariant = variant;
	}
}
//? }
