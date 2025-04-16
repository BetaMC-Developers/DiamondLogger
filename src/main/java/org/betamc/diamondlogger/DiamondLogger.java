package org.betamc.diamondlogger;

import org.bukkit.Bukkit;
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
        instance = this;

        getConfiguration().load();
        webhookUrl = getConfiguration().getString("webhook_url", "changethis");
        interval = getConfiguration().getInt("interval", 300);
        threshold = getConfiguration().getInt("threshold", 20);
        getConfiguration().save();

        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getLogger().info("[DiamondLogger] Version " + getDescription().getVersion() + " has been enabled.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[DiamondLogger] Version " + getDescription().getVersion() + " has been disabled.");
    }

}
