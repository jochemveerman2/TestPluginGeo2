package me.jochem.testPluginGeo2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import me.jochem.testPluginGeo2.managers.AnnounceManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandAlias("listannounces|announcelist")
@CommandPermission("TestPluginGeo2.commands.listannounces")
public class ListAnnouncesCommand extends BaseCommand {

    private static final int ANNOUNCES_PER_PAGE = 10;
    private final AnnounceManager announceManager;

    public ListAnnouncesCommand(AnnounceManager announceManager) {
        this.announceManager = announceManager;
    }

    @Default
    @Description("Bekijk actieve announcements.")
    @Syntax("[pagina]")
    public void onListAnnounces(Player player, @Default("1") Integer page) {
        Map<String, Integer> activeAnnouncements = announceManager.list();
        List<String> announces = new ArrayList<>(activeAnnouncements.keySet());
        int totalAnnouncements = announces.size();

        List<String> pageAnnounces = getAnnouncementsForPage(announces, page);
        if (pageAnnounces.isEmpty()) {
            player.sendMessage(Component.text("Geen announcements gevonden voor pagina " + page + ".").color(NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("Actieve announcements - Pagina " + page).color(NamedTextColor.GREEN));
        player.sendMessage(Component.text("---------------").color(NamedTextColor.GOLD));
        pageAnnounces.forEach(name -> {
            Component clickableComponent = Component.text("- " + name)
                    .color(NamedTextColor.YELLOW)
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand("/delannounce " + name));

            player.sendMessage(clickableComponent);
        });

        int totalPages = (int) Math.ceil(totalAnnouncements / (double) ANNOUNCES_PER_PAGE);
        player.sendMessage(Component.text("---------------").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("Pagina's: " + totalPages).color(NamedTextColor.GRAY));
    }

    private List<String> getAnnouncementsForPage(List<String> announces, int page) {
        int start = (page - 1) * ANNOUNCES_PER_PAGE;
        int end = Math.min(start + ANNOUNCES_PER_PAGE, announces.size());
        if (start >= announces.size() || start < 0) {
            return new ArrayList<>();
        }
        return announces.subList(start, end);
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        String commandName = help.getCommandName();
        sender.sendMessage(Component.text(commandName + " hulp:").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("=== ").color(NamedTextColor.YELLOW).append(Component.text("Hulp voor ").color(NamedTextColor.AQUA)).append(Component.text("/"+commandName).color(NamedTextColor.GREEN)).append(Component.text(" ===").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Beschrijving: ").color(NamedTextColor.AQUA).append(Component.text("Bekijk actieve announcements.").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Gebruik: ").color(NamedTextColor.AQUA).append(Component.text("/" + commandName + " [Pagina]").color(NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("-------------------------------").color(NamedTextColor.GRAY));
    }

    @CatchUnknown
    public void onUnknownCommand(Player player, String command) {
        player.sendMessage(Component.text("Er is een fout opgetreden.").color(NamedTextColor.RED));
    }
}