package de.qirdpdms.serverManagerLITE.listener;

import de.qirdpdms.serverManagerLITE.Main;
import de.qirdpdms.serverManagerLITE.util.WebhookUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!Main.getInstance().getConfig().getBoolean("discord.commandLogging.enabled")) return;

        Player player = event.getPlayer();
        List<String> whitelist = Main.getInstance().getConfig().getStringList("discord.commandLogging.whitelist");
        if (whitelist.contains(player.getName())) return;

        String cmd = event.getMessage();
        String description = Main.getInstance().getConfig().getString("discord.commandLogging.format.description", "")
                .replace("%player%", player.getName())
                .replace("%command%", cmd);

        int color = Main.getInstance().getConfig().getInt("discord.commandLogging.format.color", 3447003);
        String webhookUrl = Main.getInstance().getConfig().getString("discord.commandLogging.webhookUrl");

        WebhookUtil.sendWebhook(
                webhookUrl,
                null,
                description,
                color
        );

    }
}
