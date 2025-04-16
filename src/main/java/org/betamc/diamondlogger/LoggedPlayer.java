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
            sendDiscordEmbed();
        }
    }

    public void sendDiscordEmbed() {
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

            Location loc = player.getLocation();
            String locationStr = String.format("`%s, %d, %d, %d`", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            embed.addField("Current Location", locationStr, false);
            webhook.addEmbed(embed);

            try {
                webhook.execute();
            } catch (IOException e) {}
        });
    }

}
