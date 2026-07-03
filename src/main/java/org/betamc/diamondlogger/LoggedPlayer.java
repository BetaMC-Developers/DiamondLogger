package org.betamc.diamondlogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class LoggedPlayer {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private final Player player;
    private final Map<OreType, AtomicInteger> oresMined = new EnumMap<>(OreType.class);

    public LoggedPlayer(Player player) {
        this.player = player;

        DiamondLogger dl = DiamondLogger.getInstance();
        Bukkit.getScheduler().scheduleSyncDelayedTask(dl, () -> {
            checkOresMined();
            dl.getLoggedPlayers().remove(player.getUniqueId());
        }, dl.getInterval() * 20L);
    }

    public void incrementOreMined(OreType oreType) {
        this.oresMined.computeIfAbsent(oreType, k -> new AtomicInteger(0)).incrementAndGet();
    }

    private void checkOresMined() {
        if (this.oresMined.entrySet().stream().anyMatch(entry ->
                exceededThreshold(entry.getKey(), entry.getValue().get()))) {
            sendIngameMessage();
            sendDiscordEmbed();
        }
    }

    private void sendIngameMessage() {
        Arrays.stream(Bukkit.getOnlinePlayers())
                .filter(player -> player.hasPermission("diamondlogger.receivemessage"))
                .forEach(player -> {
                    player.sendMessage(ChatColor.AQUA + "[Diamond Logger]");
                    player.sendMessage(ChatColor.DARK_AQUA + this.player.getName()
                            + ChatColor.GRAY + " mined the following ores in the last "
                            + ChatColor.AQUA + DiamondLogger.getInstance().getInterval() + " seconds"
                            + ChatColor.GRAY + ":");

                    for (Map.Entry<OreType, AtomicInteger> entry : this.oresMined.entrySet()) {
                        OreType oreType = entry.getKey();
                        int amountMined = entry.getValue().get();
                        if (amountMined == 0) {
                            continue;
                        }

                        ChatColor color = exceededThreshold(oreType, amountMined) ? ChatColor.RED : ChatColor.GREEN;
                        String oreSum = color + "- " + amountMined + "x " + oreType.getOreName();
                        player.sendMessage(oreSum);
                    }
                });
    }

    private void sendDiscordEmbed() {
        DiamondLogger dl = DiamondLogger.getInstance();
        EXECUTOR.submit(() -> {
            DiscordWebhook webhook = new DiscordWebhook(dl.getWebhookUrl());
            webhook.setUsername("Diamond Logger");
            webhook.setTts(false);

            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setAuthor(this.player.getName(), null, "https://minotar.net/avatar/" + this.player.getName() + ".png")
                    .setTitle("Mined the following ores in the last " + dl.getInterval() + " seconds:")
                    .setColor(Color.CYAN)
                    .setFooter("https://github.com/BetaMC-Developers/DiamondLogger",
                               "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");

            StringBuilder desc = new StringBuilder();
            for (Map.Entry<OreType, AtomicInteger> entry : this.oresMined.entrySet()) {
                OreType oreType = entry.getKey();
                int amountMined = entry.getValue().get();
                if (amountMined == 0) {
                    continue;
                }

                boolean exceededThreshold = exceededThreshold(oreType, amountMined);
                desc.append("- ");
                if (exceededThreshold) {
                    desc.append("**");
                }
                desc.append(amountMined).append("x ").append(oreType.getOreName());
                if (exceededThreshold) {
                    desc.append("**");
                }
                desc.append("\\n");
            }
            desc.delete(desc.length() - 2, desc.length());
            embed.setDescription(desc.toString());
            webhook.addEmbed(embed);

            try {
                webhook.execute();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "[Diamond Logger] Discord embed send failed: ", e);
            }
        });
    }

    private static boolean exceededThreshold(OreType oreType, int amountMined) {
        int threshold = DiamondLogger.getInstance().getThresholds().getOrDefault(oreType, -1);
        return threshold != -1 && amountMined > threshold;
    }
}
