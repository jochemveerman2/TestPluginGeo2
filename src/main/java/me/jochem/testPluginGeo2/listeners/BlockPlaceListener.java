package me.jochem.testPluginGeo2.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        Material blockType = event.getBlock().getType();

        String message = player.getName() + " heeft " + blockType.name().toLowerCase().replace("_", " ") + " geplaatst!";

        Bukkit.broadcastMessage(message);
    }
}