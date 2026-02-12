package cn.royan.minescoreboard.mixin;

import cn.royan.minescoreboard.MineScoreboardMod;
import cn.royan.minescoreboard.PlayerBoardManager;
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
	private void mineScoreboard$onMineBlock(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue()) {
			Scoreboard scoreboard = MinecraftServer.getInstance().getWorld(0).getScoreboard();
			ScoreboardObjective objective = scoreboard.getObjective("Dig");
			if (objective != null) {
				ScoreboardScore score = scoreboard.getScore(this.player.getName(), objective);
				score.increase(1);
				PlayerBoardManager.onScoreUpdate("Dig", score);

				MineScoreboardMod.increaseTotalDig();
				ScoreboardScore totalScore = scoreboard.getScore(MineScoreboardMod.TOTAL_DIG_NAME, objective);
				PlayerBoardManager.onScoreUpdate("Dig", totalScore);
			}
		}
	}
}
