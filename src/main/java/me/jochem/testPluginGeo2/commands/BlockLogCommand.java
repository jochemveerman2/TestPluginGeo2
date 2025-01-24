package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.jochem.testPluginGeo2.TestPluginGeo2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.time.Duration;

@CommandAlias("bloklog|bloklogs|blocklog|blocklogs")
@CommandPermission("TestPluginGeo2.commands.bloklogs")
@Description("Zet de bloklogs aan of uit.")
public class BlockLogCommand extends BaseCommand {

    @Subcommand("aan")
    @CommandPermission("TestPluginGeo2.commands.bloklogs.aan")
    @Description("Zet de bloklogs aan.")
    public void onEnableCommand(Player player) {
        FileConfiguration config = TestPluginGeo2.getInstance().getConfig();

        config.set("listenersActive", true);
        TestPluginGeo2.getInstance().saveConfig();
        TestPluginGeo2.getInstance().reloadConfig();

        TestPluginGeo2.getInstance().registerListeners();

        sendTitle(player, "Bloklog staat ", "aan", NamedTextColor.GREEN);
    }

    @Subcommand("uit")
    @CommandPermission("TestPluginGeo2.commands.bloklogs.uit")
    @Description("Zet de bloklogs uit.")
    public void onDisableCommand(Player player) {
        FileConfiguration config = TestPluginGeo2.getInstance().getConfig();

        config.set("listenersActive", false);
        TestPluginGeo2.getInstance().saveConfig();
        TestPluginGeo2.getInstance().reloadConfig();

        TestPluginGeo2.getInstance().deregisterListeners();

        sendTitle(player, "Bloklog staat ", "uit", NamedTextColor.RED);
    }

    private void sendTitle(Player player, String prefix, String status, NamedTextColor statusColor) {
        Title title = Title.title(
                Component.empty(),
                Component.text(prefix).color(NamedTextColor.WHITE)
                        .append(Component.text(status).color(statusColor)),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))
        );
        player.showTitle(title);
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        String commandName = help.getCommandName();
        sender.sendMessage(Component.text(commandName + " hulp:").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("=== ").color(NamedTextColor.YELLOW).append(Component.text("Hulp voor ").color(NamedTextColor.AQUA)).append(Component.text("/"+commandName).color(NamedTextColor.GREEN)).append(Component.text(" ===").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Beschrijving: ").color(NamedTextColor.AQUA).append(Component.text("Zet de bloklogs aan of uit.").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Gebruik: ").color(NamedTextColor.AQUA).append(Component.text("/" + commandName + "<aan/uit>").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
    }


    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }
}