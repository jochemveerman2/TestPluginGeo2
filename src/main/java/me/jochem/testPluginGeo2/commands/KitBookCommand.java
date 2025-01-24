package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

@CommandAlias("kitbook")
@CommandPermission("TestPluginGeo2.commands.kitbook")
public class KitBookCommand extends BaseCommand {

    @Default
    @Description("Geef jezelf een KitBook.")
    public void onKitBookCommand(Player player) {

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        if (bookMeta != null) {
            bookMeta.setTitle("KitBook");
            bookMeta.setAuthor("Server");

            Component title = Component.text("KitBook\n\n");
            Component dirtText = Component.text("Dirt Kit\n")
                    .clickEvent(ClickEvent.runCommand("/dirt"));
            Component stoneText = Component.text("Stone Kit\n")
                    .clickEvent(ClickEvent.runCommand("/stone"));
            Component woodText = Component.text("Wood Kit")
                    .clickEvent(ClickEvent.runCommand("/wood"));

            Component pageContent = title.append(dirtText).append(stoneText).append(woodText);

            bookMeta.addPages(pageContent);
            book.setItemMeta(bookMeta);

            player.getInventory().addItem(book);

            player.sendMessage(Component.text("Je hebt een KitBook ontvangen!").color(NamedTextColor.LIGHT_PURPLE));
        }
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        String commandName = help.getCommandName();
        sender.sendMessage(Component.text(commandName + " hulp:").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("=== ").color(NamedTextColor.YELLOW).append(Component.text("Hulp voor ").color(NamedTextColor.AQUA)).append(Component.text("/"+commandName).color(NamedTextColor.GREEN)).append(Component.text(" ===").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Beschrijving: ").color(NamedTextColor.AQUA).append(Component.text("Geef jezelf een KitBook.").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Gebruik: ").color(NamedTextColor.AQUA).append(Component.text("/" + commandName).color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
    }

    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }
}