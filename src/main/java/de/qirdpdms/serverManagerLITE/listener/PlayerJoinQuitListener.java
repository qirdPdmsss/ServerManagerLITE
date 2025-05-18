package de.qirdpdms.serverManagerLITE.listener;

import de.qirdpdms.serverManagerLITE.Main;
import de.qirdpdms.serverManagerLITE.util.WebhookUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinQuitListener implements Listener {

    private final HashMap<UUID, LocalDateTime> joinTimes = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        joinTimes.put(player.getUniqueId(), LocalDateTime.now());

        if (!Main.getInstance().getConfig().getBoolean("discord.join.enabled")) return;

        String description = Main.getInstance().getConfig().getString("discord.join.format.description", "")
                .replace("%player%", player.getName())
                .replace("%ip%", player.getAddress().getAddress().getHostAddress())
                .replace("%world%", player.getWorld().getName())
                .replace("%time%", LocalDateTime.now().toString());

        String title = Main.getInstance().getConfig().getString("discord.join.format.title", "Join");
        int color = Main.getInstance().getConfig().getInt("discord.join.format.color", 65280);
        String url = Main.getInstance().getConfig().getString("discord.join.webhookUrl");

        WebhookUtil.sendWebhook(url, title, description, color);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LocalDateTime joined = joinTimes.getOrDefault(player.getUniqueId(), LocalDateTime.now());
        Duration duration = Duration.between(joined, LocalDateTime.now());

        String durationStr = String.format("%dh %dmin", duration.toHours(), duration.toMinutesPart());

        if (!Main.getInstance().getConfig().getBoolean("discord.quit.enabled")) return;

        String description = Main.getInstance().getConfig().getString("discord.quit.format.description", "")
                .replace("%player%", player.getName())
                .replace("%duration%", durationStr);

        String title = Main.getInstance().getConfig().getString("discord.quit.format.title", "Quit");
        int color = Main.getInstance().getConfig().getInt("discord.quit.format.color", 16711680);
        String url = Main.getInstance().getConfig().getString("discord.quit.webhookUrl");

        WebhookUtil.sendWebhook(url, title, description, color);
    }
}
