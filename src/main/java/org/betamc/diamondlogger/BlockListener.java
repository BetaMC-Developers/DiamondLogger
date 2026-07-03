package org.betamc.diamondlogger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onBlockBreak(BlockBreakEvent event) {
        OreType oreType = OreType.getByMaterial(event.getBlock().getType());
        if (oreType == null) {
            return;
        }

        Player player = event.getPlayer();
        DiamondLogger.getInstance().getLoggedPlayers()
                .computeIfAbsent(player.getUniqueId(), k -> new LoggedPlayer(player))
                .incrementOreMined(oreType);
    }
}
