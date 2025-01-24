package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import me.jochem.testPluginGeo2.TestPluginGeo2;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@CommandAlias("setwarp|createwarp")
@CommandPermission("TestPluginGeo2.commands.setwarp")
public class SetWarpCommand extends BaseCommand {

    @Default
    @Description("Maak een nieuwe warp aan.")
    @Syntax("<warpnaam> [hidden true/false]")
    public void onSetWarpCommand(Player player, String warpName, @Default("false") boolean hidden) {
        if (warpName.length() > 30) {
            player.sendMessage(Component.text("De warpnaam mag maximaal 30 tekens bevatten.").color(NamedTextColor.RED));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(TestPluginGeo2.getInstance(), () -> {
            if (doesWarpExist(warpName)) {
                Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                    player.sendMessage(Component.text("Er bestaat al een warp met die naam.").color(NamedTextColor.RED));
                });
                return;
            }

            Location location = player.getLocation();
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            float pitch = location.getPitch();
            float yaw = location.getYaw();
            String worldName = location.getWorld().getName();

            try (Connection connection = JavaPlugin.getPlugin(TestPluginGeo2.class).getDataSource().getConnection()) {
                String insertQuery = "INSERT INTO warps (warpname, worldname, x, y, z, pitch, yaw, hidden) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                    statement.setString(1, warpName);
                    statement.setString(2, worldName);
                    statement.setDouble(3, x);
                    statement.setDouble(4, y);
                    statement.setDouble(5, z);
                    statement.setFloat(6, pitch);
                    statement.setFloat(7, yaw);
                    statement.setBoolean(8, hidden);
                    statement.executeUpdate();
                }

                Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                    player.sendMessage(Component.text("Warp '" + warpName + "' is succesvol aangemaakt!").color(NamedTextColor.GREEN));
                });
            } catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                    player.sendMessage(Component.text("Er is een fout opgetreden bij het opslaan van de warp.").color(NamedTextColor.RED));
                });
            }
        });
    }

    private boolean doesWarpExist(String warpName) {
        try (Connection connection = JavaPlugin.getPlugin(TestPluginGeo2.class).getDataSource().getConnection()) {
            String query = "SELECT * FROM warps WHERE warpname = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, warpName);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        String commandName = help.getCommandName();
        sender.sendMessage(Component.text(commandName + " hulp:").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("=== ").color(NamedTextColor.YELLOW).append(Component.text("Hulp voor ").color(NamedTextColor.AQUA)).append(Component.text("/"+commandName).color(NamedTextColor.GREEN)).append(Component.text(" ===").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Beschrijving: ").color(NamedTextColor.AQUA).append(Component.text("Maak een nieuwe warp aan.").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Gebruik: ").color(NamedTextColor.AQUA).append(Component.text("/" + commandName + " <warpnaam> [hiden true/false]").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
    }

    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }
}