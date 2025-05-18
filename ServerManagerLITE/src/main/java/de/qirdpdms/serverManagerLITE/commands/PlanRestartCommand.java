package de.qirdpdms.serverManagerLITE.commands;

import de.qirdpdms.serverManagerLITE.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlanRestartCommand implements CommandExecutor, TabCompleter {

    private static BukkitRunnable restartTask;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("servermanager.restart") && !sender.hasPermission("servermanager.*")) {
            sender.sendMessage(Main.getMessage("noPermission"));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("cancel")) {
            if (restartTask != null) {
                restartTask.cancel();
                restartTask = null;
                Bukkit.broadcastMessage(Main.getMessage("restart.cancelled"));
            } else {
                sender.sendMessage(Main.getMessage("restart.noActive"));
            }
            return true;
        }

        if (args.length != 1 || !isInteger(args[0])) {
            sender.sendMessage(Main.getMessage("restart.usage"));
            return true;
        }

        int seconds = Integer.parseInt(args[0]);

        String scheduledMsg = Main.getInstance().getConfig().getString("messages.restart.scheduled", "")
                .replace("%player%", sender.getName())
                .replace("%seconds%", String.valueOf(seconds));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                Main.getInstance().getConfig().getString("messages.prefix", "") + scheduledMsg));

        restartTask = new BukkitRunnable() {
            int countdown = seconds;

            @Override
            public void run() {
                if (countdown == 0) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            Main.getInstance().getConfig().getString("messages.prefix", "") +
                                    Main.getInstance().getConfig().getString("messages.restart.now", "&cRestart!")));
                    Bukkit.shutdown();
                    restartTask = null;
                    cancel();
                    return;
                }

                if (countdown <= 10 || countdown % 30 == 0) {
                    String msg = Main.getInstance().getConfig().getString("messages.restart.countdown", "")
                            .replace("%seconds%", String.valueOf(countdown));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            Main.getInstance().getConfig().getString("messages.prefix", "") + msg));
                }

                countdown--;
            }
        };

        restartTask.runTaskTimer(Main.getInstance(), 0L, 20L);
        return true;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("servermanager.restart") && !sender.hasPermission("servermanager.*")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> suggestions = Arrays.asList("30", "60", "300", "600", "cancel");
            return suggestions.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
