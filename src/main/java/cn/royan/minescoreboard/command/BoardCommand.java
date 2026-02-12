package cn.royan.minescoreboard.command;

import cn.royan.minescoreboard.MineScoreboardMod;
import cn.royan.minescoreboard.PlayerBoardManager;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.List;

public class BoardCommand extends AbstractCommand implements Comparable {

	@Override
	public String getName() {
		return "board";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "/board <dig|place|hide|show>";
	}

	@Override
	public void run(CommandSource source, String[] args) {
		if (!(source instanceof ServerPlayerEntity)) {
			source.sendMessage(new LiteralText("Only players can use this command"));
			return;
		}

		ServerPlayerEntity player = (ServerPlayerEntity) source;

		if (args.length < 1) {
			String current = PlayerBoardManager.getPlayerBoard(player.getUuid());
			boolean hidden = PlayerBoardManager.isHidden(player.getUuid());
			source.sendMessage(new LiteralText("Current Board: " + current + (hidden ? " (Hidden)" : "")));
			source.sendMessage(new LiteralText("Usage: /board <dig|place|hide|show>"));
			return;
		}

		String arg = args[0].toLowerCase();

		if ("dig".equals(arg)) {
			PlayerBoardManager.setPlayerBoard(player, MineScoreboardMod.BOARD_DIG);
			source.sendMessage(new LiteralText("Switched to Dig board"));
		} else if ("place".equals(arg)) {
			PlayerBoardManager.setPlayerBoard(player, MineScoreboardMod.BOARD_PLACE);
			source.sendMessage(new LiteralText("Switched to Place board"));
		} else if ("hide".equals(arg)) {
			PlayerBoardManager.hideBoard(player);
			source.sendMessage(new LiteralText("Scoreboard hidden"));
		} else if ("show".equals(arg)) {
			PlayerBoardManager.showBoard(player);
			source.sendMessage(new LiteralText("Scoreboard shown"));
		} else {
			source.sendMessage(new LiteralText("Invalid argument. Please use dig, place, hide, or show"));
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean canUse(CommandSource source) {
		return true;
	}

	@Override
	public List getSuggestions(CommandSource source, String[] args) {
		if (args.length == 1) {
			return suggestMatching(args, "dig", "place", "hide", "show");
		}
		return null;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Command) {
			return this.getName().compareTo(((Command) o).getName());
		}
		return 0;
	}
}
