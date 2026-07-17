package lk.dev.countdown;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CountdownCommand implements CommandExecutor, TabCompleter {

    private final CountdownPlugin plugin;
    private final CountdownManager manager;

    public CountdownCommand(CountdownPlugin plugin, CountdownManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("countdown.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /countdown <set|add|stop|list> [id] [seconds]");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "set": {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /countdown set <id> <seconds>");
                    return true;
                }
                String id = args[1];
                Integer seconds = parseInt(args[2], sender);
                if (seconds == null) return true;

                manager.setCountdown(id, seconds);
                sender.sendMessage(ChatColor.GREEN + "'" + id + "' countdown set to " + seconds + " seconds.");
                return true;
            }
            case "add": {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /countdown add <id> <seconds>");
                    return true;
                }
                String id = args[1];
                Integer seconds = parseInt(args[2], sender);
                if (seconds == null) return true;

                manager.addSeconds(id, seconds);
                sender.sendMessage(ChatColor.GREEN + "Added " + seconds + " seconds to '" + id + "' countdown.");
                return true;
            }
            case "stop": {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /countdown stop <id>");
                    return true;
                }
                String id = args[1];
                manager.stopCountdown(id);
                sender.sendMessage(ChatColor.GREEN + "'" + id + "' countdown stopped.");
                return true;
            }
            case "list": {
                Map<String, Integer> all = manager.getAll();
                if (all.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "There are no active countdowns.");
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "Active Countdowns " + ChatColor.GRAY + "(Dev by 3aiya)" + ChatColor.GOLD + ":");
                for (Map.Entry<String, Integer> e : all.entrySet()) {
                    sender.sendMessage(ChatColor.AQUA + " - " + e.getKey() + ": " + CountdownManager.formatTime(e.getValue()));
                }
                return true;
            }
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Usage: /countdown <set|add|stop|list> [id] [seconds]");
                return true;
        }
    }

    private Integer parseInt(String value, CommandSender sender) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Seconds must be a valid number.");
            return null;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("set", "add", "stop", "list"));
        } else if (args.length == 2
                && (args[0].equalsIgnoreCase("stop")
                || args[0].equalsIgnoreCase("add")
                || args[0].equalsIgnoreCase("set"))) {
            completions.addAll(manager.getAll().keySet());
        }
        return completions;
    }
}
