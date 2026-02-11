package cn.royan.minescoreboard.mixin;

import cn.royan.minescoreboard.command.BoardCommand;
import net.minecraft.server.command.handler.CommandManager;
import net.minecraft.server.command.handler.CommandRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin extends CommandRegistry {

	@Inject(method = "<init>", at = @At("RETURN"))
	private void mineScoreboard$registerCommands(CallbackInfo ci) {
		this.register(new BoardCommand());
	}
}
