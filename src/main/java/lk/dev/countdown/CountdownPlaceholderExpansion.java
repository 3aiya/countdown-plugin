package lk.dev.countdown;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Registers placeholders in the format:
 *   %countdown_<id>_time%     -> full D:H:M:S string, e.g. 2:05:14:30
 *   %countdown_<id>_days%     -> days component only
 *   %countdown_<id>_hours%    -> hours component only (0-23)
 *   %countdown_<id>_minutes%  -> minutes component only (0-59)
 *   %countdown_<id>_secs%     -> seconds component only (0-59)
 *   %countdown_<id>_seconds%  -> raw total remaining seconds
 *   %countdown_<id>_running%  -> "true" or "false"
 *
 * Example: if you ran "/countdown set sale 187200" (2 days, 4 hours)
 *   %countdown_sale_time%     -> 2:04:00:00
 *   %countdown_sale_days%     -> 2
 *   %countdown_sale_hours%    -> 4
 *   %countdown_sale_minutes%  -> 0
 *   %countdown_sale_secs%     -> 0
 *   %countdown_sale_seconds%  -> 187200
 *   %countdown_sale_running%  -> true
 */
public class CountdownPlaceholderExpansion extends PlaceholderExpansion {

    private final CountdownPlugin plugin;
    private final CountdownManager manager;

    public CountdownPlaceholderExpansion(CountdownPlugin plugin, CountdownManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "countdown";
    }

    @Override
    public @NotNull String getAuthor() {
        return "3aiya";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        // params looks like: "<id>_time", "<id>_seconds" or "<id>_running"
        String[] parts = params.split("_");
        if (parts.length < 2) return "";

        String type = parts[parts.length - 1];
        StringBuilder idBuilder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (idBuilder.length() > 0) idBuilder.append("_");
            idBuilder.append(parts[i]);
        }
        String id = idBuilder.toString();

        int remaining = manager.getRemaining(id);

        switch (type.toLowerCase()) {
            case "time":
                return CountdownManager.formatTime(remaining);
            case "days":
                return String.valueOf(CountdownManager.getDays(remaining));
            case "hours":
                return String.valueOf(CountdownManager.getHours(remaining));
            case "minutes":
                return String.valueOf(CountdownManager.getMinutes(remaining));
            case "secs":
                return String.valueOf(CountdownManager.getSecondsOnly(remaining));
            case "seconds":
                return String.valueOf(remaining);
            case "running":
                return manager.isRunning(id) ? "true" : "false";
            default:
                return "";
        }
    }
}
