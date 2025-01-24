package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.jochem.testPluginGeo2.TestPluginGeo2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@CommandAlias("warp")
@CommandPermission("TestPluginGeo2.commands.warp")
public class WarpCommand extends BaseCommand {

    @Default
    @Description("Teleporteer naar een warp.")
    @Syntax("<warpnaam>")
    public void onWarpCommand(Player player, @Single String warpName) {
        Bukkit.getScheduler().runTaskAsynchronously(TestPluginGeo2.getInstance(), () -> {
            try (Connection connection = TestPluginGeo2.getInstance().getDataSource().getConnection()) {
                String query = "SELECT * FROM warps WHERE warpname = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, warpName);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String worldName = rs.getString("worldname");
                            double x = rs.getDouble("x");
                            double y = rs.getDouble("y");
                            double z = rs.getDouble("z");
                            float pitch = rs.getFloat("pitch");
                            float yaw = rs.getFloat("yaw");

                            World world = Bukkit.getWorld(worldName);
                            if (world != null) {
                                Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                                    player.teleport(new org.bukkit.Location(world, x, y, z, yaw, pitch));
                                    player.sendMessage(Component.text("Je bent geteleporteerd naar warp: " + warpName).color(NamedTextColor.GREEN));
                                });
                            } else {
                                Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                                    player.sendMessage(Component.text("De wereld van de warp kon niet worden gevonden.").color(NamedTextColor.RED));
                                });
                            }
                        } else {
                            Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                                player.sendMessage(Component.text("Warp niet gevonden.").color(NamedTextColor.RED));
                            });
                        }
                    }
                }
            } catch (SQLException e) {
                Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                    player.sendMessage(Component.text("Er is een fout opgetreden bij het ophalen van de warp.").color(NamedTextColor.RED));
                });
                e.printStackTrace();
            }
        });
    }

    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        sender.sendMessage(Component.text("TestPluginGeo2 hulp:").color(NamedTextColor.GOLD));
        help.showHelp();
    }
}