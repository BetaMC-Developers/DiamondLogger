package org.betamc.diamondlogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DiamondLogger extends JavaPlugin {

    private static DiamondLogger instance;

    private String webhookUrl;
    private int interval;
    private final Map<OreType, Integer> thresholds = new EnumMap<>(OreType.class);
    private final Map<UUID, LoggedPlayer> loggedPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        if (instance == null) {
            instance = this;
            Bukkit.getPluginCommand("reloaddl").setExecutor(this);
            Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        }

        getConfiguration().load();
        this.webhookUrl = getConfiguration().getString("webhook_url", "changethis");
        this.interval = getConfiguration().getInt("interval", 300);
        for (OreType oreType : OreType.values()) {
            this.thresholds.put(oreType, getConfiguration().getInt("thresholds." + oreType.name(), -1));
        }
        getConfiguration().save();

        Bukkit.getLogger().info("[DiamondLogger] Version " + getDescription().getVersion() + " has been enabled.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[DiamondLogger] Version " + getDescription().getVersion() + " has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("diamondlogger.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        onDisable();
        onEnable();
        return true;
    }

    public static DiamondLogger getInstance() {
        return instance;
    }

    public String getWebhookUrl() {
        return this.webhookUrl;
    }

    public int getInterval() {
        return this.interval;
    }

    public Map<OreType, Integer> getThresholds() {
        return this.thresholds;
    }

    public Map<UUID, LoggedPlayer> getLoggedPlayers() {
        return this.loggedPlayers;
    }
}
