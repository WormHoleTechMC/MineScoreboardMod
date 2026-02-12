package cn.royan.minescoreboard.mixin;

import cn.royan.minescoreboard.MineScoreboardMod;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class PlaceBlockMixin {

	@Shadow
	public ServerPlayerEntity player;

	@Inject(method = "useBlock", at = @At("TAIL"))
	private void mineScoreboard$onPlaceBlock(PlayerEntity player, World world, ItemStack itemInHand, int x, int y, int z, int face, float faceX, float faceY, float faceZ, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && itemInHand != null) {
			MineScoreboardMod.increasePlayerScore(this.player, MineScoreboardMod.BOARD_PLACE);
		}
	}
}
