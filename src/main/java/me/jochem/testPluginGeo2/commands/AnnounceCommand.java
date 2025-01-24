package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import me.jochem.testPluginGeo2.managers.AnnounceManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("announce|createannounce|addannounce")
@CommandPermission("TestPluginGeo2.commands.announce")
public class AnnounceCommand extends BaseCommand {

    private final AnnounceManager announceManager;

    public AnnounceCommand(AnnounceManager announceManager) {
        this.announceManager = announceManager;
    }

    @Default
    @Description("Maak een announcement aan.")
    @Syntax("<naam> <seconden> <bericht>")
    public void onAnnounce(Player player, String name, int seconds, String message) {
        String formattedMessage = translateColorCodes(message);
        int intervalTicks = seconds * 20;

        try {
            announceManager.add(name, formattedMessage, intervalTicks);
            player.sendMessage(Component.text("Announcement gestart met naam: " + name).color(NamedTextColor.GREEN));
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text(e.getMessage()).color(NamedTextColor.RED));
        }
    }

    private String translateColorCodes(String message) {
        return message.replace("&", "§");
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        String commandName = help.getCommandName();
        sender.sendMessage(Component.text(commandName + " hulp:").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("=== ").color(NamedTextColor.YELLOW).append(Component.text("Hulp voor ").color(NamedTextColor.AQUA)).append(Component.text("/"+commandName).color(NamedTextColor.GREEN)).append(Component.text(" ===").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Beschrijving: ").color(NamedTextColor.AQUA).append(Component.text("Maak een announcement aan.").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Gebruik: ").color(NamedTextColor.AQUA).append(Component.text("/" + commandName + " <naam> <seconden> <bericht>").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
    }

    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }
}