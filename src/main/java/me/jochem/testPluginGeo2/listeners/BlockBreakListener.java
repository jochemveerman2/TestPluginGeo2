package me.jochem.testPluginGeo2.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Material blockType = event.getBlock().getType();

        String message = player.getName() + " heeft " + blockType.name().toLowerCase().replace("_", " ") + " gebroken!";

        Bukkit.broadcastMessage(message);
    }
}