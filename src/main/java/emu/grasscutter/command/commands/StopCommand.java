package emu.grasscutter.command.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;

import java.util.List;

import static emu.grasscutter.utils.Language.translate;

@Command(label = "stop", usage = "stop", permission = "server.stop")
public final class StopCommand implements CommandHandler {

    @Override
    public String description() {
        return translate("commands.stop.description");
    }

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        CommandHandler.sendMessage(null, translate("commands.stop.success"));
        for (Player p : Grasscutter.getGameServer().getPlayers().values()) {
            CommandHandler.sendMessage(p, translate("commands.stop.success"));
        }

        System.exit(1000);
    }
}