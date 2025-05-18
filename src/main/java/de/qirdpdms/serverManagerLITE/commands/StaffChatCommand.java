package de.qirdpdms.serverManagerLITE.commands;

import de.qirdpdms.serverManagerLITE.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StaffChatCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("servermanager.staffchat") && !sender.hasPermission("servermanager.*")) {
            sender.sendMessage(Main.getMessage("noPermission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Main.getMessage("staffchat.noMessage"));
            return true;
        }

        String message = String.join(" ", args);
        String format = Main.getInstance().getConfig().getString("messages.staffchat.format",
                "&d🗨️ [Staffchat] &b%player%&7: %message%");

        format = format
                .replace("%player%", sender.getName())
                .replace("%message%", message);

        String coloredMessage = ChatColor.translateAlternateColorCodes('&',
                Main.getInstance().getConfig().getString("messages.prefix", "") + format);

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("servermanager.staffchat") || player.hasPermission("servermanager.*")) {
                player.sendMessage(coloredMessage);
            }
        });

        Bukkit.getConsoleSender().sendMessage(coloredMessage);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("servermanager.staffchat") && !sender.hasPermission("servermanager.*")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
