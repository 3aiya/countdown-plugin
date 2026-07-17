package lk.dev.countdown;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores each countdown as an ABSOLUTE end-timestamp (System.currentTimeMillis() based),
 * not as a "seconds remaining" counter. This means real-world time keeps passing even
 * while the server is offline or restarting - when the server comes back up, the
 * remaining time is recalculated from the saved end-timestamp vs the current time.
 */
public class CountdownManager {

    private final Plugin plugin;
    private final File file;
    private final FileConfiguration config;
    private final Map<String, Long> endTimes = new ConcurrentHashMap<>();
    private BukkitTask tickTask;

    public CountdownManager(Plugin plugin) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        this.file = new File(plugin.getDataFolder(), "countdowns.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        loadFromFile();
    }

    private void loadFromFile() {
        ConfigurationSection section = config.getConfigurationSection("countdowns");
        if (section == null) return;
        for (String id : section.getKeys(false)) {
            long endTime = section.getLong(id);
            endTimes.put(id, endTime);
        }
        // Immediately drop anything that already expired while the server was offline.
        cleanupExpired();
    }

    private void saveToFile() {
        config.set("countdowns", null);
        for (Map.Entry<String, Long> entry : endTimes.entrySet()) {
            config.set("countdowns." + entry.getKey(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("countdowns.yml save කරන්න බැරි වුනා: " + e.getMessage());
        }
    }

    /** Starts a lightweight repeating task - only used to auto-clean expired entries from memory/file. */
    public void startTicking() {
        tickTask = Bukkit.getScheduler().runTaskTimer(plugin, this::cleanupExpired, 20L, 20L);
    }

    public void stopTicking() {
        if (tickTask != null) {
            tickTask.cancel();
        }
    }

    private void cleanupExpired() {
        boolean changed = false;
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : endTimes.entrySet()) {
            if (entry.getValue() <= now) {
                endTimes.remove(entry.getKey());
                changed = true;
            }
        }
        if (changed) saveToFile();
    }

    public void setCountdown(String id, int seconds) {
        long endTime = System.currentTimeMillis() + (seconds * 1000L);
        endTimes.put(id.toLowerCase(), endTime);
        saveToFile();
    }

    public void addSeconds(String id, int seconds) {
        long now = System.currentTimeMillis();
        long base = Math.max(endTimes.getOrDefault(id.toLowerCase(), now), now);
        endTimes.put(id.toLowerCase(), base + (seconds * 1000L));
        saveToFile();
    }

    public void stopCountdown(String id) {
        if (endTimes.remove(id.toLowerCase()) != null) {
            saveToFile();
        }
    }

    public boolean isRunning(String id) {
        Long end = endTimes.get(id.toLowerCase());
        return end != null && end > System.currentTimeMillis();
    }

    /** Remaining seconds calculated live from the stored real-world end-timestamp. */
    public int getRemaining(String id) {
        Long end = endTimes.get(id.toLowerCase());
        if (end == null) return 0;
        long diffMillis = end - System.currentTimeMillis();
        if (diffMillis <= 0) return 0;
        return (int) (diffMillis / 1000);
    }

    public Map<String, Integer> getAll() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (String id : endTimes.keySet()) {
            int remaining = getRemaining(id);
            if (remaining > 0) {
                result.put(id, remaining);
            }
        }
        return result;
    }

    public static int getDays(int totalSeconds) {
        return Math.max(totalSeconds, 0) / 86400;
    }

    public static int getHours(int totalSeconds) {
        return (Math.max(totalSeconds, 0) % 86400) / 3600;
    }

    public static int getMinutes(int totalSeconds) {
        return (Math.max(totalSeconds, 0) % 3600) / 60;
    }

    public static int getSecondsOnly(int totalSeconds) {
        return Math.max(totalSeconds, 0) % 60;
    }

    /** Formats seconds as D:H:M:S (e.g. 2:05:14:30). */
    public static String formatTime(int totalSeconds) {
        if (totalSeconds <= 0) return "0:00:00:00";
        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }
}
