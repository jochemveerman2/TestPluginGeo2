package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.jochem.testPluginGeo2.TestPluginGeo2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CommandAlias("warplist|warplijst|warps")
@CommandPermission("TestPluginGeo2.commands.warplist")
public class WarpListCommand extends BaseCommand {

    private static final int WARPS_PER_PAGE = 10;

    @Default
    @Description("Bekijk bestaande warps.")
    @Syntax("[pagina]")
    public void onWarpListCommand(Player player, @Default("1") int page) {

        final int finalPage = page;
        final Player finalPlayer = player;

        Bukkit.getScheduler().runTaskAsynchronously(TestPluginGeo2.getInstance(), () -> {
            List<String> warps = getWarpsForPage(finalPage);
            int totalWarps = getTotalWarpsCount();

            Bukkit.getScheduler().runTask(TestPluginGeo2.getInstance(), () -> {
                if (warps.isEmpty()) {
                    finalPlayer.sendMessage(Component.text("Geen warps gevonden.").color(NamedTextColor.RED));
                    return;
                }

                finalPlayer.sendMessage(Component.text("Warps - Pagina " + finalPage).color(NamedTextColor.GREEN));
                finalPlayer.sendMessage(Component.text("---------------").color(NamedTextColor.GOLD));
                warps.forEach(warp -> {
                    Component clickableComponent = Component.text("- " + warp)
                            .color(NamedTextColor.YELLOW)
                            .clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand("/warp " + warp));

                    finalPlayer.sendMessage(clickableComponent);
                });

                int totalPages = (int) Math.ceil(totalWarps / (double) WARPS_PER_PAGE);
                finalPlayer.sendMessage(Component.text("---------------").color(NamedTextColor.GOLD));
                finalPlayer.sendMessage(Component.text("Pagina's: " + totalPages).color(NamedTextColor.GRAY));
            });
        });
    }

    private List<String> getWarpsForPage(int page) {
        List<String> warps = new ArrayList<>();
        try (Connection connection = TestPluginGeo2.getInstance().getDataSource().getConnection()) {
            int offset = (page - 1) * WARPS_PER_PAGE;

            String query = "SELECT warpname FROM warps WHERE hidden = 0 LIMIT ? OFFSET ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, WARPS_PER_PAGE);
                stmt.setInt(2, offset);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        warps.add(rs.getString("warpname"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while querying warps for page " + page);
        }
        return warps;
    }

    private int getTotalWarpsCount() {
        int count = 0;
        try (Connection connection = TestPluginGeo2.getInstance().getDataSource().getConnection()) {
            String query = "SELECT COUNT(*) FROM warps WHERE hidden = 0";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while getting total warp count.");
        }
        return count;
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        String commandName = help.getCommandName();
        sender.sendMessage(Component.text(commandName + " hulp:").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("=== ").color(NamedTextColor.YELLOW).append(Component.text("Hulp voor ").color(NamedTextColor.AQUA)).append(Component.text("/"+commandName).color(NamedTextColor.GREEN)).append(Component.text(" ===").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Beschrijving: ").color(NamedTextColor.AQUA).append(Component.text("Bekijk bestaande warps.").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Gebruik: ").color(NamedTextColor.AQUA).append(Component.text("/" + commandName + " [Pagina]").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
    }

    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }
}