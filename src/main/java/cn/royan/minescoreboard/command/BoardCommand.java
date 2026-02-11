package cn.royan.minescoreboard.command;

import cn.royan.minescoreboard.PlayerBoardManager;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BoardCommand extends AbstractCommand {

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
			source.sendMessage(new LiteralText("该命令只能由玩家执行"));
			return;
		}

		ServerPlayerEntity player = (ServerPlayerEntity) source;

		if (args.length < 1) {
			String current = PlayerBoardManager.getPlayerBoard(player.getUuid());
			boolean hidden = PlayerBoardManager.isHidden(player.getUuid());
			source.sendMessage(new LiteralText("当前榜单: " + current + (hidden ? " (已隐藏)" : "")));
			source.sendMessage(new LiteralText("用法: /board <dig|place|hide|show>"));
			return;
		}

		String arg = args[0].toLowerCase();

		switch (arg) {
			case "dig":
				PlayerBoardManager.setPlayerBoard(player, "Dig");
				source.sendMessage(new LiteralText("已切换到 dig"));
				break;
			case "place":
				PlayerBoardManager.setPlayerBoard(player, "Place");
				source.sendMessage(new LiteralText("已切换到 Place"));
				break;
			case "hide":
				PlayerBoardManager.hideBoard(player);
				source.sendMessage(new LiteralText("已隐藏计分板"));
				break;
			case "show":
				PlayerBoardManager.showBoard(player);
				source.sendMessage(new LiteralText("已显示计分板"));
				break;
			default:
				source.sendMessage(new LiteralText("无效参数，请使用 dig、place、hide 或 show"));
				break;
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
		if (!(o instanceof Command)) return 0;
		Command command = (Command) o;
		return this.getName().compareToIgnoreCase(command.getName());
	}
}
