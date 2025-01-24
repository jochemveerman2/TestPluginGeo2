package me.jochem.testPluginGeo2.managers;

import me.jochem.testPluginGeo2.TestPluginGeo2;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class AnnounceManager {
    private final Map<String, Integer> activeAnnouncements = new HashMap<>();

    public boolean has(String name) {
        return activeAnnouncements.containsKey(name);
    }

    public void add(String name, String message, int intervalTicks) {
        if (has(name)) {
            throw new IllegalArgumentException("Announcement met deze naam bestaat al: " + name);
        }

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                TestPluginGeo2.getInstance(),
                () -> Bukkit.broadcastMessage(message),
                0L,
                intervalTicks
        );

        activeAnnouncements.put(name, taskId);
    }

    public void stop(String name) {
        if (!has(name)) {
            throw new IllegalArgumentException("Geen announcement gevonden met naam: " + name);
        }

        int taskId = activeAnnouncements.remove(name);
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public Map<String, Integer> list() {
        return new HashMap<>(activeAnnouncements);
    }
}