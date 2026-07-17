package lk.dev.countdown;

import org.bukkit.plugin.java.JavaPlugin;

public class CountdownPlugin extends JavaPlugin {

    private CountdownManager countdownManager;

    @Override
    public void onEnable() {
        this.countdownManager = new CountdownManager(this);
        this.countdownManager.startTicking();

        CountdownCommand commandHandler = new CountdownCommand(this, countdownManager);
        getCommand("countdown").setExecutor(commandHandler);
        getCommand("countdown").setTabCompleter(commandHandler);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CountdownPlaceholderExpansion(this, countdownManager).register();
            getLogger().info("PlaceholderAPI found! Placeholders registered: %countdown_<id>_time% , %countdown_<id>_seconds% , %countdown_<id>_running%");
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholders won't work until it's installed.");
        }

        getLogger().info("CountdownPlugin enabled. Dev by 3aiya");
    }

    @Override
    public void onDisable() {
        if (countdownManager != null) {
            countdownManager.stopTicking();
        }
        getLogger().info("CountdownPlugin disabled.");
    }

    public CountdownManager getCountdownManager() {
        return countdownManager;
    }
}
