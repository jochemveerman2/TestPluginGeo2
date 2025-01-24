package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import me.jochem.testPluginGeo2.managers.AnnounceManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("stopannounce|delannounce|deleteannounce")
@CommandPermission("TestPluginGeo2.commands.stopannounce")
public class StopAnnounceCommand extends BaseCommand {

    private final AnnounceManager announceManager;

    public StopAnnounceCommand(AnnounceManager announceManager) {
        this.announceManager = announceManager;
    }

    @Default
    @Description("Stop een announcement.")
    @Syntax("<naam>")
    public void onStopAnnounce(Player player, @Single String name) {
        try {
            announceManager.stop(name);
            player.sendMessage(Component.text("Announcement gestopt: " + name).color(NamedTextColor.GREEN));
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text(e.getMessage()).color(NamedTextColor.RED));
        }
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        String commandName = help.getCommandName();
        sender.sendMessage(Component.text(commandName + " hulp:").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("=== ").color(NamedTextColor.YELLOW).append(Component.text("Hulp voor ").color(NamedTextColor.AQUA)).append(Component.text("/"+commandName).color(NamedTextColor.GREEN)).append(Component.text(" ===").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Beschrijving: ").color(NamedTextColor.AQUA).append(Component.text("Stop een announcement.").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Gebruik: ").color(NamedTextColor.AQUA).append(Component.text("/" + commandName + "<naam>").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
    }

    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }
}