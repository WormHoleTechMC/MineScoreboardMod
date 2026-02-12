package cn.royan.minescoreboard;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.scoreboard.ServerScoreboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerBoardManager {

	private static final Map<UUID, String> playerBoards = new HashMap<>();
	private static final Set<UUID> hiddenPlayers = new HashSet<>();
	private static final String DEFAULT_BOARD = "Dig";

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
		Scoreboard scoreboard = MinecraftServer.getInstance().getWorld(0).getScoreboard();
		ScoreboardObjective objective = scoreboard.getObjective(boardName);

		if (objective != null) {
			Collection scores = scoreboard.getScores(objective);
			for (Object obj : scores) {
				ScoreboardScore score = (ScoreboardScore) obj;
				ScoreboardScoreS2CPacket packet = new ScoreboardScoreS2CPacket(score, 0);
				player.networkHandler.sendPacket(packet);
			}
			player.networkHandler.sendPacket(new ScoreboardDisplayS2CPacket(1, objective));
		}
	}

	private static void sendFullBoardData(ServerPlayerEntity player, String boardName) {
		Scoreboard scoreboard = MinecraftServer.getInstance().getWorld(0).getScoreboard();

		if (scoreboard instanceof ServerScoreboard) {
			ServerScoreboard serverScoreboard = (ServerScoreboard) scoreboard;

			ScoreboardObjective digObjective = scoreboard.getObjective("Dig");
			ScoreboardObjective placeObjective = scoreboard.getObjective("Place");

			if (digObjective != null) {
				List packets = serverScoreboard.createStartDisplayingObjectivePackets(digObjective);
				for (Object obj : packets) {
					player.networkHandler.sendPacket((Packet) obj);
				}
			}

			if (placeObjective != null) {
				List packets = serverScoreboard.createStartDisplayingObjectivePackets(placeObjective);
				for (Object obj : packets) {
					player.networkHandler.sendPacket((Packet) obj);
				}
			}

			if (!hiddenPlayers.contains(player.getUuid())) {
				ScoreboardObjective selectedObjective = scoreboard.getObjective(boardName);
				if (selectedObjective != null) {
					player.networkHandler.sendPacket(new ScoreboardDisplayS2CPacket(1, selectedObjective));
				}
			}
		}
	}

	public static void onScoreUpdate(String boardName, ScoreboardScore score) {
		List players = MinecraftServer.getInstance().getPlayerManager().players;
		ScoreboardScoreS2CPacket packet = new ScoreboardScoreS2CPacket(score, 0);

		for (Object obj : players) {
			ServerPlayerEntity onlinePlayer = (ServerPlayerEntity) obj;
			String viewingBoard = getPlayerBoard(onlinePlayer.getUuid());

			if (boardName.equals(viewingBoard) && !hiddenPlayers.contains(onlinePlayer.getUuid())) {
				onlinePlayer.networkHandler.sendPacket(packet);
			}
		}
	}

	public static void onPlayerJoin(ServerPlayerEntity player) {
		String board = getPlayerBoard(player.getUuid());
		sendFullBoardData(player, board);
	}

	public static void onPlayerLeave(ServerPlayerEntity player) {
	}
}
