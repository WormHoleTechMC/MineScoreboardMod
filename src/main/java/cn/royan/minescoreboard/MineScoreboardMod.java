package cn.royan.minescoreboard;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.lifecycle.api.server.MinecraftServerEvents;

public class MineScoreboardMod implements ModInitializer {

	public static final String TOTAL_DIG_NAME = "§eTotal_Dig";
	public static final String TOTAL_PLACE_NAME = "§9Total_Place";

	@Override
	public void init() {
		MinecraftServerEvents.READY.register((a) -> {
			Scoreboard scoreboard = MinecraftServer.getInstance().getWorld(0).getScoreboard();

			ScoreboardObjective digObjective = scoreboard.getObjective("Dig");
			if (digObjective == null) {
				scoreboard.createObjective("Dig", ScoreboardCriterion.DUMMY).setDisplayName("Dig");
				digObjective = scoreboard.getObjective("Dig");
			}

			ScoreboardObjective placeObjective = scoreboard.getObjective("Place");
			if (placeObjective == null) {
				scoreboard.createObjective("Place", ScoreboardCriterion.DUMMY).setDisplayName("Place");
				placeObjective = scoreboard.getObjective("Place");
			}

			initTotalScores(scoreboard, digObjective, placeObjective);
		});
	}

	private void initTotalScores(Scoreboard scoreboard, ScoreboardObjective digObjective, ScoreboardObjective placeObjective) {
		if (digObjective != null) {
			int total = 0;
			for (Object obj : scoreboard.getScores(digObjective)) {
				ScoreboardScore score = (ScoreboardScore) obj;
				if (!score.getOwner().equals(TOTAL_DIG_NAME)) {
					total += score.get();
				}
			}
			scoreboard.getScore(TOTAL_DIG_NAME, digObjective).set(total);
		}

		if (placeObjective != null) {
			int total = 0;
			for (Object obj : scoreboard.getScores(placeObjective)) {
				ScoreboardScore score = (ScoreboardScore) obj;
				if (!score.getOwner().equals(TOTAL_PLACE_NAME)) {
					total += score.get();
				}
			}
			scoreboard.getScore(TOTAL_PLACE_NAME, placeObjective).set(total);
		}
	}

	public static void increaseTotalDig() {
		Scoreboard scoreboard = MinecraftServer.getInstance().getWorld(0).getScoreboard();
		ScoreboardObjective objective = scoreboard.getObjective("Dig");
		if (objective != null) {
			scoreboard.getScore(TOTAL_DIG_NAME, objective).increase(1);
		}
	}

	public static void increaseTotalPlace() {
		Scoreboard scoreboard = MinecraftServer.getInstance().getWorld(0).getScoreboard();
		ScoreboardObjective objective = scoreboard.getObjective("Place");
		if (objective != null) {
			scoreboard.getScore(TOTAL_PLACE_NAME, objective).increase(1);
		}
	}
}
