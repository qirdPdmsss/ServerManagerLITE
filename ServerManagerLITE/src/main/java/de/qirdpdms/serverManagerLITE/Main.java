package de.qirdpdms.serverManagerLITE;

import de.qirdpdms.serverManagerLITE.commands.*;
import de.qirdpdms.serverManagerLITE.listener.CommandListener;
import de.qirdpdms.serverManagerLITE.listener.PlayerJoinQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
        getServer().getPluginManager().registerEvents(new CommandListener(), this);
        ServerInfoCommand serverInfo = new ServerInfoCommand();
        getCommand("serverinfo").setExecutor(serverInfo);
        getCommand("serverinfo").setTabCompleter(serverInfo);

        StaffChatCommand staffChatCommand = new StaffChatCommand();
        getCommand("staffchat").setExecutor(staffChatCommand);
        getCommand("staffchat").setTabCompleter(staffChatCommand);

        ReportCommand reportCmd = new ReportCommand();
        getCommand("report").setExecutor(reportCmd);
        getCommand("report").setTabCompleter(reportCmd);

        PlanRestartCommand planRestartCmd = new PlanRestartCommand();
        getCommand("planrestart").setExecutor(planRestartCmd);
        getCommand("planrestart").setTabCompleter(planRestartCmd);

        ServerManagerCommand smCmd = new ServerManagerCommand();
        getCommand("servermanager").setExecutor(smCmd);
        getCommand("servermanager").setTabCompleter(smCmd);

        logBanner();
    }

    @Override
    public void onDisable() {

    }

    public static Main getInstance() {
        return instance;
    }
    public static String getMessage(String key) {
        String prefix = ChatColor.translateAlternateColorCodes('&',
                instance.getConfig().getString("messages.prefix", "[ServerManagerLITE] "));
        String message = instance.getConfig().getString("messages." + key, "&cNachricht nicht gefunden: " + key);
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }
    private void logBanner() {
        String prefix = "§a";
        Bukkit.getConsoleSender().sendMessage(prefix + "==================================================");
        Bukkit.getConsoleSender().sendMessage("§6ServerManagerLITE §ev" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("§7Developed by §bQirdpdms");
        Bukkit.getConsoleSender().sendMessage("§7Discord: §9https://discord.gg/9z7ghDRDWA");
        Bukkit.getConsoleSender().sendMessage(prefix + "==================================================");
        Bukkit.getConsoleSender().sendMessage("§a✅ Plugin loaded successfully!");
    }


}
