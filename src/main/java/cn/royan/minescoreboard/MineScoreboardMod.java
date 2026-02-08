package cn.royan.minescoreboard;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.lifecycle.api.server.MinecraftServerEvents;

public class MineScoreboardMod implements ModInitializer {
	@Override
	public void init() {
		MinecraftServerEvents.READY.register((a) -> {
			Scoreboard scoreboard = MinecraftServer.getInstance().getWorld(0).getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getObjective("dig");
			if (scoreboardObjective == null) {
				scoreboard.createObjective("dig", ScoreboardCriterion.DUMMY).setDisplayName("dig");
				scoreboardObjective = scoreboard.getObjective("dig");
			}
			scoreboard.setDisplayObjective(1, scoreboardObjective);
		});
	}
}
