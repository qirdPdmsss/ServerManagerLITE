package de.qirdpdms.serverManagerLITE.commands;

import de.qirdpdms.serverManagerLITE.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServerInfoCommand implements CommandExecutor, TabCompleter {

    private final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("servermanager.info") && !sender.hasPermission("servermanager.*")) {
            sender.sendMessage(Main.getMessage("noPermission"));
            return true;
        }

        int online = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        int worlds = Bukkit.getWorlds().size();

        long maxMem = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        long totalMem = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long freeMem = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long usedMem = totalMem - freeMem;

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = getSystemCpuLoad(osBean) * 100.0;

        long totalChunks = Bukkit.getWorlds().stream()
                .mapToLong(world -> world.getLoadedChunks().length)
                .sum();

        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        long uptimeMillis = runtime.getUptime();
        String uptimeFormatted = formatUptime(uptimeMillis);

        // Prefix & header
        String prefix = ChatColor.translateAlternateColorCodes('&',
                Main.getInstance().getConfig().getString("messages.prefix", ""));
        String header = ChatColor.translateAlternateColorCodes('&',
                Main.getInstance().getConfig().getString("messages.serverinfo.header", "&7Serverinfo:"));
        sender.sendMessage(prefix + header);

        List<String> lines = Main.getInstance().getConfig().getStringList("messages.serverinfo.lines");
        for (String line : lines) {
            String formatted = line
                    .replace("%used%", String.valueOf(usedMem))
                    .replace("%max%", String.valueOf(maxMem))
                    .replace("%cpu%", df.format(cpuLoad))
                    .replace("%online%", String.valueOf(online))
                    .replace("%maxplayers%", String.valueOf(maxPlayers))
                    .replace("%worlds%", String.valueOf(worlds))
                    .replace("%chunks%", String.valueOf(totalChunks))
                    .replace("%uptime%", uptimeFormatted);

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', formatted));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        return Collections.emptyList();
    }

    private double getSystemCpuLoad(OperatingSystemMXBean osBean) {
        try {
            return (double) osBean.getClass().getMethod("getSystemCpuLoad").invoke(osBean);
        } catch (Exception e) {
            return -1;
        }
    }

    private String formatUptime(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return hours + "h " + minutes + "min " + seconds + "s";
    }
}
