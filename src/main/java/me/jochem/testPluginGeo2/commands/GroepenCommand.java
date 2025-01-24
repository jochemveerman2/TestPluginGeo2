package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

@CommandAlias("groepen|groups")
@CommandPermission("TestPluginGeo2.commands.groepen")
public class GroepenCommand extends BaseCommand {

    private Permission perms;

    public GroepenCommand() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp != null) {
            perms = rsp.getProvider();
        }
    }

    @Default
    @Description("Bekijk permissiegroepen van jezelf of een andere speler.")
    @CommandCompletion("@players")
    @Syntax("[spelernaam]")
    public void onGroepenCommand(Player player, @Optional @Single String playerName) {
        String message;

        if (playerName == null) {
            String worldName = player.getWorld().getName();
            String[] groups = perms.getPlayerGroups(worldName, player);

            if (groups == null || groups.length == 0) {
                message = "Je hebt geen permissiegroepen.";
            } else {
                message = "Je permissiegroepen: " + String.join(", ", groups);
            }
        } else {
            Player target = Bukkit.getPlayer(playerName);

            if (target == null) {
                message = "Speler " + playerName + " niet gevonden.";
            } else {
                String worldName = target.getWorld().getName();
                String[] groups = perms.getPlayerGroups(worldName, target);

                if (groups == null || groups.length == 0) {
                    message = "De speler " + target.getName() + " heeft geen permissiegroepen.";
                } else {
                    message = "De permissiegroepen van " + target.getName() + ": " + String.join(", ", groups);
                }
            }
        }

        player.sendMessage(Component.text(message).color(NamedTextColor.LIGHT_PURPLE));
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        String commandName = help.getCommandName();
        sender.sendMessage(Component.text(commandName + " hulp:").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("=== ").color(NamedTextColor.YELLOW).append(Component.text("Hulp voor ").color(NamedTextColor.AQUA)).append(Component.text("/"+commandName).color(NamedTextColor.GREEN)).append(Component.text(" ===").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Beschrijving: ").color(NamedTextColor.AQUA).append(Component.text("Bekijk permissiegroepen van jezelf of een andere speler.").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Gebruik: ").color(NamedTextColor.AQUA).append(Component.text("/" + commandName + " [spelernaam]").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
    }

    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }
}