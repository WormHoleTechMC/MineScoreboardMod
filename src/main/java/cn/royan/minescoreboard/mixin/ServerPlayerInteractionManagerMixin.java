package cn.royan.minescoreboard.mixin;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.MinecraftServer;
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
	private void exampleMod$onInit(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue()) {
			System.out.println(x + " " + y + " " + z + "by " + this.player.getName());
			Scoreboard scoreboard = MinecraftServer.getInstance().getWorld(0).getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getObjective("dig");
			ScoreboardScore scoreboardScore = scoreboard.getScore(this.player.getName(), scoreboardObjective);
			scoreboardScore.increase(1);
		}
	}
}
