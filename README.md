# CountdownPlugin
### Developed by 3aiya

A Spigot/Paper plugin that allows administrators to create custom countdowns and display them in other plugins (such as Scoreboard, TAB, Chat, Holograms) using PlaceholderAPI.


---

## Installation

1. Copy `countdown-plugin.jar` into your server's `plugins/` folder.
2. Install **PlaceholderAPI** if you want to use placeholders.
3. Restart your server.

---

## Commands

| Command | Description | Permission |
|----------|-------------|------------|
| `/countdown set <id> <seconds>` | Creates a new countdown or resets an existing one. | `countdown.admin` |
| `/countdown add <id> <seconds>` | Adds time to an existing countdown. | `countdown.admin` |
| `/countdown stop <id>` | Stops and removes a countdown. | `countdown.admin` |
| `/countdown list` | Displays all active countdowns. | `countdown.admin` |

### Example

```bash
/countdown set sale 3600
```

Creates a countdown with the ID **sale** that starts from **1 hour (3600 seconds)**.

---

## PlaceholderAPI Placeholders

Replace `<id>` with the countdown ID you created (for example: `sale`).

| Placeholder | Output |
|-------------|--------|
| `%countdown_<id>_time%` | Full countdown in `D:H:M:S` format (Example: `2:04:00:00`) |
| `%countdown_<id>_days%` | Remaining days |
| `%countdown_<id>_hours%` | Remaining hours (0-23) |
| `%countdown_<id>_minutes%` | Remaining minutes (0-59) |
| `%countdown_<id>_secs%` | Remaining seconds (0-59) |
| `%countdown_<id>_seconds%` | Total remaining seconds |
| `%countdown_<id>_running%` | Returns `true` or `false` |

### Example

```bash
/countdown set sale 187200
```

Output:

```text
%countdown_sale_time%     -> 2:04:00:00
%countdown_sale_days%     -> 2
%countdown_sale_hours%    -> 4
%countdown_sale_minutes%  -> 0
%countdown_sale_secs%     -> 0
%countdown_sale_seconds%  -> 187200
%countdown_sale_running%  -> true
```

These placeholders work with any PlaceholderAPI-supported plugin, including:

- Scoreboards
- TAB
- Chat Formats
- Holograms
- BossBars
- ActionBars
- And many more

---

## Notes

### Real-World Time

The plugin stores the exact expiration timestamp inside:

```text
plugins/CountdownPlugin/countdowns.yml
```

Instead of simply counting server ticks.

This means:

- ✅ Countdowns continue while the server is offline.
- ✅ Restarting the server does not reset countdowns.
- ✅ If a countdown expires while the server is offline, it is automatically removed when the server starts again.

---

## Countdown Expiration

When a countdown reaches **0**:

- It is automatically removed.
- `%countdown_<id>_running%` returns `false`.
- `%countdown_<id>_time%` returns:

```text
0:00:00:00
```

- `%countdown_<id>_seconds%` returns:

```text
0
```

---

## Compatibility

- Java 17+
- Spigot
- Paper
- PlaceholderAPI (Optional)