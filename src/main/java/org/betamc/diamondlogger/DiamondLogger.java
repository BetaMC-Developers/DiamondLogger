package org.betamc.diamondlogger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class DiamondLogger extends JavaPlugin {

    static DiamondLogger instance;
    static String webhookUrl;
    static int interval;
    static int threshold;
    static final HashMap<UUID, LoggedPlayer> loggedPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        if (instance == null) {
            instance = this;
            Bukkit.getPluginCommand("reloaddl").setExecutor(this);
            Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        }

        getConfiguration().load();
        webhookUrl = getConfiguration().getString("webhook_url", "changethis");
        interval = getConfiguration().getInt("interval", 300);
        threshold = getConfiguration().getInt("threshold", 20);
        getConfiguration().save();

        Bukkit.getLogger().info("[DiamondLogger] Version " + getDescription().getVersion() + " has been enabled.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[DiamondLogger] Version " + getDescription().getVersion() + " has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        onDisable();
        onEnable();
        return true;
    }
}
