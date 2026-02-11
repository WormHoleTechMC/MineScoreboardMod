package cn.royan.minescoreboard.mixin;

import cn.royan.minescoreboard.PlayerBoardManager;
import net.minecraft.network.Connection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerConnectionMixin {

	@Inject(method = "onLogin", at = @At("TAIL"))
	private void mineScoreboard$onPlayerJoin(Connection connection, ServerPlayerEntity player, CallbackInfo ci) {
		PlayerBoardManager.onPlayerJoin(player);
	}

	@Inject(method = "remove", at = @At("HEAD"))
	private void mineScoreboard$onPlayerLeave(ServerPlayerEntity player, CallbackInfo ci) {
		PlayerBoardManager.onPlayerLeave(player);
	}
}
