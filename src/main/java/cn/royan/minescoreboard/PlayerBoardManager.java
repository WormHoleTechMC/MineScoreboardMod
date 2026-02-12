package cn.royan.minescoreboard;

import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBoardManager {

	private static final Map<UUID, String> playerBoards = new ConcurrentHashMap<>();
	private static final Set<UUID> hiddenPlayers = ConcurrentHashMap.newKeySet();
	private static final String DEFAULT_BOARD = MineScoreboardMod.BOARD_DIG;

	public static void setPlayerBoard(ServerPlayerEntity player, String boardName) {
		playerBoards.put(player.getUuid(), boardName);
		if (!hiddenPlayers.contains(player.getUuid())) {
			sendBoardScores(player, boardName);
		}
	}

	public static String getPlayerBoard(UUID uuid) {
		return playerBoards.getOrDefault(uuid, DEFAULT_BOARD);
	}

	public static void hideBoard(ServerPlayerEntity player) {
		hiddenPlayers.add(player.getUuid());
		player.networkHandler.sendPacket(new ScoreboardDisplayS2CPacket(1, null));
	}

	public static void showBoard(ServerPlayerEntity player) {
		hiddenPlayers.remove(player.getUuid());
		String boardName = getPlayerBoard(player.getUuid());
		sendBoardScores(player, boardName);
	}

	public static boolean isHidden(UUID uuid) {
		return hiddenPlayers.contains(uuid);
	}

	private static void sendBoardScores(ServerPlayerEntity player, String boardName) {
		Scoreboard scoreboard = MineScoreboardMod.getScoreboard();
		ScoreboardObjective objective = scoreboard.getObjective(boardName);

		if (objective != null) {
			Collection<ScoreboardScore> scores = scoreboard.getScores(objective);
			for (ScoreboardScore score : scores) {
				ScoreboardScoreS2CPacket packet = new ScoreboardScoreS2CPacket(score, 0);
				player.networkHandler.sendPacket(packet);
			}
			player.networkHandler.sendPacket(new ScoreboardDisplayS2CPacket(1, objective));
		}
	}

	public static void onScoreUpdate(String boardName, ScoreboardScore score) {
		@SuppressWarnings("unchecked")
		java.util.List<ServerPlayerEntity> players = MinecraftServer.getInstance().getPlayerManager().players;
		ScoreboardScoreS2CPacket packet = new ScoreboardScoreS2CPacket(score, 0);

		for (ServerPlayerEntity onlinePlayer : players) {
			String viewingBoard = getPlayerBoard(onlinePlayer.getUuid());

			if (boardName.equals(viewingBoard) && !hiddenPlayers.contains(onlinePlayer.getUuid())) {
				onlinePlayer.networkHandler.sendPacket(packet);
			}
		}
	}

	public static void onPlayerJoin(ServerPlayerEntity player) {
		Scoreboard scoreboard = MineScoreboardMod.getScoreboard();

		ScoreboardObjective digObjective = scoreboard.getObjective(MineScoreboardMod.BOARD_DIG);
		if (digObjective != null) {
			player.networkHandler.sendPacket(new ScoreboardObjectiveS2CPacket(digObjective, 0));
		}

		ScoreboardObjective placeObjective = scoreboard.getObjective(MineScoreboardMod.BOARD_PLACE);
		if (placeObjective != null) {
			player.networkHandler.sendPacket(new ScoreboardObjectiveS2CPacket(placeObjective, 0));
		}

		String board = getPlayerBoard(player.getUuid());
		sendBoardScores(player, board);
	}

	public static void onPlayerLeave(ServerPlayerEntity player) {
		playerBoards.remove(player.getUuid());
		hiddenPlayers.remove(player.getUuid());
	}
}
