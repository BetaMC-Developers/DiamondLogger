package org.betamc.diamondlogger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LoggedPlayer {

    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final Player player;
    private final AtomicInteger diamondsMined;

    public LoggedPlayer(Player player) {
        this.player = player;
        this.diamondsMined = new AtomicInteger(0);

        Bukkit.getScheduler().scheduleSyncDelayedTask(DiamondLogger.instance, () -> {
            checkDiamondsMined();
            DiamondLogger.loggedPlayers.remove(player.getUniqueId());
        }, DiamondLogger.interval * 20L);
    }

    public void incrementDiamondsMined() {
        diamondsMined.getAndIncrement();
    }

    public void checkDiamondsMined() {
        if (diamondsMined.get() >= DiamondLogger.threshold) {
            Location loc = player.getLocation();
            String worldStr = loc.getWorld().getName() + ": ";
            String locationStr = String.format("/tp %s %d %d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

            Bukkit.getLogger().info("[Diamond Logger] " + player.getName() + " mined " + diamondsMined.get() +
                    " diamond ore in the last " + DiamondLogger.interval + " seconds at " + worldStr + locationStr);
            sendIngameMessage(locationStr, worldStr);
            sendDiscordEmbed(locationStr, worldStr);
        }
    }

    public void sendIngameMessage(String locationStr, String worldStr) {
        for(Player iPlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (iPlayer.isOp() || iPlayer.hasPermission("diamondlogger.recievemessage")) {
                iPlayer.sendMessage("§b[Diamond Logger]");
                iPlayer.sendMessage("§3" + player.getName() + "§7 mined §b" + diamondsMined.get() +
                        "§7 diamond ore in the last §b" + DiamondLogger.interval + "§7 seconds");
                iPlayer.sendMessage("§7Current location: §f" + worldStr + locationStr);
            }
        }
    }

    public void sendDiscordEmbed(String locationStr, String worldStr) {
        executor.submit(() -> {
            DiscordWebhook webhook = new DiscordWebhook(DiamondLogger.webhookUrl);
            webhook.setUsername("Diamond Logger");
            webhook.setTts(false);

            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setAuthor(player.getName(), null, "https://minotar.net/avatar/" + player.getName() + ".png")
                    .setDescription("Mined **" + diamondsMined.get() + "** diamond ore in the last " + DiamondLogger.interval + " seconds")
                    .setColor(Color.CYAN)
                    .setFooter("https://github.com/BetaMC-Developers/DiamondLogger",
                               "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");

            embed.addField("Current Location in " + worldStr, "```" + locationStr + "```", false);
            webhook.addEmbed(embed);

            try {
                webhook.execute();
            } catch (IOException e) {
                Bukkit.getLogger().info("[Diamond Logger] Discord embed send failed");
            }
        });
    }

}
