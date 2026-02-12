package cn.royan.minescoreboard;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.lifecycle.api.server.MinecraftServerEvents;

public class MineScoreboardMod implements ModInitializer {

	public static final String BOARD_DIG = "Dig";
	public static final String BOARD_PLACE = "Place";
	public static final String TOTAL_DIG_NAME = "§eTotal_Dig";
	public static final String TOTAL_PLACE_NAME = "§9Total_Place";

	@Override
	public void init() {
		MinecraftServerEvents.READY.register((a) -> {
			Scoreboard scoreboard = getScoreboard();

			ScoreboardObjective digObjective = scoreboard.getObjective(BOARD_DIG);
			if (digObjective == null) {
				scoreboard.createObjective(BOARD_DIG, ScoreboardCriterion.DUMMY).setDisplayName(BOARD_DIG);
				digObjective = scoreboard.getObjective(BOARD_DIG);
			}

			ScoreboardObjective placeObjective = scoreboard.getObjective(BOARD_PLACE);
			if (placeObjective == null) {
				scoreboard.createObjective(BOARD_PLACE, ScoreboardCriterion.DUMMY).setDisplayName(BOARD_PLACE);
				placeObjective = scoreboard.getObjective(BOARD_PLACE);
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

	public static Scoreboard getScoreboard() {
		return MinecraftServer.getInstance().getWorld(0).getScoreboard();
	}

	public static void increasePlayerScore(ServerPlayerEntity player, String boardName) {
		Scoreboard scoreboard = getScoreboard();
		ScoreboardObjective objective = scoreboard.getObjective(boardName);
		if (objective == null) {
			return;
		}

		ScoreboardScore score = scoreboard.getScore(player.getName(), objective);
		score.increase(1);
		PlayerBoardManager.onScoreUpdate(boardName, score);

		String totalName = BOARD_DIG.equals(boardName) ? TOTAL_DIG_NAME : TOTAL_PLACE_NAME;
		ScoreboardScore totalScore = scoreboard.getScore(totalName, objective);
		totalScore.increase(1);
		PlayerBoardManager.onScoreUpdate(boardName, totalScore);
	}
}
