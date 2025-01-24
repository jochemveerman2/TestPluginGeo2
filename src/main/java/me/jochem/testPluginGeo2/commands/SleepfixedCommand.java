package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import me.jochem.testPluginGeo2.TestPluginGeo2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@CommandAlias("sleepfixed")
@CommandPermission("TestPluginGeo2.commands.sleepfixed")
public class SleepfixedCommand extends BaseCommand {

    @Default
    @Description("Voert sleep uit.")
    @Syntax("<nummer>")
    public void onSleepFixedCommand(Player player, @Single int sleepTime) {
        if (sleepTime <= 0) {
            player.sendMessage(Component.text("Gebruik een geldig positief nummer voor de slaapduur.").color(NamedTextColor.RED));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(TestPluginGeo2.getInstance(), () -> {
            String query = "SELECT SLEEP(?)";
            try (Connection connection = TestPluginGeo2.getInstance().getDataSource().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.setInt(1, sleepTime);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int result = rs.getInt(1);
                        Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                            player.sendMessage(Component.text("SLEEP voltooid: " + result).color(NamedTextColor.GREEN));
                        });
                    }
                }

            } catch (SQLException e) {
                Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                    player.sendMessage(Component.text("Er is een SQL fout opgetreden: " + e.getMessage()).color(NamedTextColor.RED));
                });
                e.printStackTrace();
            }
        });
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        String commandName = help.getCommandName();
        sender.sendMessage(Component.text(commandName + " hulp:").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("=== ").color(NamedTextColor.YELLOW).append(Component.text("Hulp voor ").color(NamedTextColor.AQUA)).append(Component.text("/"+commandName).color(NamedTextColor.GREEN)).append(Component.text(" ===").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Beschrijving: ").color(NamedTextColor.AQUA).append(Component.text("Voert sleep uit.").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Gebruik: ").color(NamedTextColor.AQUA).append(Component.text("/" + commandName + " <nummer>").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
    }

    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }
}