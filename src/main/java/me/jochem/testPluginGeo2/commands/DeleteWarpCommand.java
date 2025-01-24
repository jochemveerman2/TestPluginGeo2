package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import me.jochem.testPluginGeo2.TestPluginGeo2;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@CommandAlias("deletewarp|delwarp|removewarp")
@CommandPermission("TestPluginGeo2.commands.deletewarp")
public class DeleteWarpCommand extends BaseCommand {

    @Default
    @Description("Verwijder een bestaande warp.")
    @Syntax("<warpnaam>")
    public void onDeleteWarpCommand(Player player, @Single String warpName) {
        Bukkit.getScheduler().runTaskAsynchronously(TestPluginGeo2.getInstance(), () -> {
            boolean success = removeWarp(warpName);

            Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                if (success) {
                    player.sendMessage(Component.text("Warp " + warpName + " is verwijderd.").color(NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("Warp " + warpName + " bestaat niet.").color(NamedTextColor.RED));
                }
            });
        });
    }

    private boolean removeWarp(String warpName) {
        try (Connection connection = TestPluginGeo2.getInstance().getDataSource().getConnection()) {
            String checkQuery = "SELECT * FROM warps WHERE warpname = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, warpName);
                if (!checkStmt.executeQuery().next()) {
                    return false;
                }
            }

            String deleteQuery = "DELETE FROM warps WHERE warpname = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, warpName);
                deleteStmt.executeUpdate();
                return true;
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
        sender.sendMessage(Component.text("Beschrijving: ").color(NamedTextColor.AQUA).append(Component.text("Verwijder een bestaande warp.").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Gebruik: ").color(NamedTextColor.AQUA).append(Component.text("/" + commandName + " <warpnaam>").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
    }

    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }
}