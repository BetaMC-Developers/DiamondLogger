package org.betamc.diamondlogger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.io.IOException;

public class LoggedPlayer {

    private final Player player;
    private int diamondsMined;

    public LoggedPlayer(Player player) {
        this.player = player;
        this.diamondsMined = 0;

        Bukkit.getScheduler().scheduleSyncDelayedTask(DiamondLogger.instance, () -> {
            checkDiamondsMined();
            DiamondLogger.loggedPlayers.remove(player.getUniqueId());
        }, DiamondLogger.threshold * 20L);
    }

    public void incrementDiamondsMined() {
        diamondsMined++;
    }

    public void checkDiamondsMined() {
        if (diamondsMined >= DiamondLogger.threshold) {
            sendDiscordEmbed();
        }
    }

    public void sendDiscordEmbed() {
        DiscordWebhook webhook = new DiscordWebhook(DiamondLogger.webhookUrl);
        webhook.setUsername("Diamond Logger");
        webhook.setTts(false);

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setAuthor(player.getName(), null, "https://minotar.net/avatar/" + player.getName() + ".png")
                .setDescription("Mined **" + diamondsMined + "** diamond ore in the last " + DiamondLogger.interval + " seconds")
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
    }

}
