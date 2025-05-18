package de.qirdpdms.serverManagerLITE.commands;

import de.qirdpdms.serverManagerLITE.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServerManagerCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("servermanager.reload") && !sender.hasPermission("servermanager.*")) {
            sender.sendMessage(Main.getMessage("noPermission"));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            Main.getInstance().reloadConfig();
            sender.sendMessage(Main.getMessage("reloadSuccess"));
            return true;
        }

        sender.sendMessage(Main.getMessage("reloadUsage"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("servermanager.reload") && !sender.hasPermission("servermanager.*")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Collections.singletonList("reload")
                    .stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
