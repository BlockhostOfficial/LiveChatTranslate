package net.blockhost.livechattranslate;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TranslateChatListener implements Listener {
    private final JavaPlugin plugin;
    private final TranslationService translationService;

    public TranslateChatListener(JavaPlugin plugin, TranslationService translationService) {
        this.plugin = plugin;
        this.translationService = translationService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player sender = event.getPlayer();
        String message = event.getMessage();

        String language = translationService.detectLanguage(message);

        if ("en".equals(language)) {
            for (Player recipient : event.getRecipients()) {
                String locale = recipient.getLocale();
                if (locale.startsWith("es_")) {
                    String translatedMessage = translationService.translate(message, "ES");
                    recipient.sendMessage(String.format(event.getFormat(), sender.getDisplayName(), translatedMessage));
                } else {
                    recipient.sendMessage(String.format(event.getFormat(), sender.getDisplayName(), message));
                }
            }
        } else {
            for (Player recipient : event.getRecipients()) {
                recipient.sendMessage(String.format(event.getFormat(), sender.getDisplayName(), message));
            }
        }
    }
}