package cn.royan.minescoreboard.mixin;

import cn.royan.minescoreboard.MineScoreboardMod;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

	@Shadow
	public ServerPlayerEntity player;

	@Inject(method = "mineBlock", at = @At("TAIL"))
	private void mineScoreboard$onMineBlock(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue()) {
			MineScoreboardMod.increasePlayerScore(this.player, MineScoreboardMod.BOARD_DIG);
		}
	}
}
