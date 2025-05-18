package de.qirdpdms.serverManagerLITE.commands;

import de.qirdpdms.serverManagerLITE.Main;
import de.qirdpdms.serverManagerLITE.util.WebhookUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReportCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player reporter)) return true;

        if (!reporter.hasPermission("servermanager.report") && !reporter.hasPermission("servermanager.*")) {
            reporter.sendMessage(Main.getMessage("noPermission"));
            return true;
        }

        if (args.length < 2) {
            reporter.sendMessage(Main.getMessage("report.usage"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            reporter.sendMessage(Main.getMessage("report.notFound"));
            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String world = reporter.getWorld().getName();
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Discord senden
        if (Main.getInstance().getConfig().getBoolean("discord.report.enabled")) {
            String title = Main.getInstance().getConfig().getString("discord.report.format.title", "Neuer Report");
            String descTemplate = Main.getInstance().getConfig().getString("discord.report.format.description", "");
            int color = Main.getInstance().getConfig().getInt("discord.report.format.color", 16776960);
            String url = Main.getInstance().getConfig().getString("discord.report.webhookUrl");

            String description = descTemplate
                    .replace("%reporter%", reporter.getName())
                    .replace("%target%", target.getName())
                    .replace("%reason%", reason)
                    .replace("%world%", world)
                    .replace("%time%", time);

            WebhookUtil.sendWebhook(url, title, description, color);
        }


        reporter.sendMessage(Main.getMessage("report.success"));


        String staffMsg = ChatColor.translateAlternateColorCodes('&',
                        Main.getInstance().getConfig().getString("messages.prefix", "") +
                                Main.getInstance().getConfig().getString("messages.report.staffNotify",
                                        "&d[Report] &b%reporter% &7meldet &c%target%&7: %reason%"))
                .replace("%reporter%", reporter.getName())
                .replace("%target%", target.getName())
                .replace("%reason%", reason);

        TextComponent main = new TextComponent(staffMsg);

        TextComponent tp = new TextComponent(" [§aTeleportieren§r]");
        tp.setBold(true);
        tp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + target.getName()));
        tp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§7Klicke hier, um zu §c" + target.getName() + " §7zu teleportieren.").create()));

        main.addExtra(tp);

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("servermanager.staffchat") || player.hasPermission("servermanager.*")) {
                player.spigot().sendMessage(main);
            }
        });

        Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(staffMsg + " [TP: /tp " + target.getName() + "]"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();

        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
