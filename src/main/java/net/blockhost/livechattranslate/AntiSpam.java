package net.blockhost.livechattranslate;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.UUID;

public class AntiSpam implements Listener {

    private LiveChatTranslate plugin;
    private static final long MESSAGE_COOLDOWN_MS = 5 * 1000; // 5 seconds in milliseconds
    private static final long COMMAND_COOLDOWN_MS = 3 * 1000; // 3 seconds in milliseconds

    private final HashMap<UUID, Long> lastMessageTimestamps;
    private final HashMap<UUID, Long> lastCommandTimestamps;

    public AntiSpam(LiveChatTranslate plugin) {
        this.plugin = plugin;
        lastMessageTimestamps = new HashMap<>();
        lastCommandTimestamps = new HashMap<>();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!canSendMessage(player)) {
            event.setCancelled(true);
            player.sendMessage("You must wait 5 seconds between chat messages.");
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!canExecuteCommand(player)) {
            event.setCancelled(true);
            player.sendMessage("You must wait 3 seconds between commands.");
        }
    }

    private boolean canSendMessage(Player player) {
        return canPerformAction(player, lastMessageTimestamps, MESSAGE_COOLDOWN_MS);
    }

    private boolean canExecuteCommand(Player player) {
        return canPerformAction(player, lastCommandTimestamps, COMMAND_COOLDOWN_MS);
    }

    private boolean canPerformAction(Player player, HashMap<UUID, Long> timestamps, long cooldownMs) {
        if (player.isOp()) {
            return true;
        }

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Check if the player has performed the action before
        if (timestamps.containsKey(playerId)) {
            long lastActionTime = timestamps.get(playerId);

            // Check if the cooldown has passed
            if (currentTime - lastActionTime < cooldownMs) {
                return false;
            }
        }

        // Update the player's last action timestamp
        timestamps.put(playerId, currentTime);
        return true;
    }
}