package org.betamc.diamondlogger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.DIAMOND_ORE) return;
        Player player = event.getPlayer();

        LoggedPlayer lPlayer = DiamondLogger.loggedPlayers.get(event.getPlayer().getUniqueId());
        if (lPlayer == null) {
            lPlayer = new LoggedPlayer(player);
            DiamondLogger.loggedPlayers.put(player.getUniqueId(), lPlayer);
        }

        lPlayer.incrementDiamondsMined();
    }

}
