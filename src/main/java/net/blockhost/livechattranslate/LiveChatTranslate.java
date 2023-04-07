package net.blockhost.livechattranslate;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class LiveChatTranslate extends JavaPlugin implements Listener {

    private final String API_KEY = "3a50084c-81be-e8f2-f4b3-3e13b29d4ab0:fx";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String playerLocale = player.getLocale();

        for (Player recipient : event.getRecipients()) {
            String recipientLocale = recipient.getLocale();
            if (playerLocale.startsWith("es_") && recipientLocale.startsWith("en_")) {
                String translatedMessage = translateMessage(message, "ES", "EN");
                recipient.sendMessage(String.format("<%s> %s", player.getDisplayName(), translatedMessage));
            } else if (playerLocale.startsWith("en_") && recipientLocale.startsWith("es_")) {
                String translatedMessage = translateMessage(message, "EN", "ES");
                recipient.sendMessage(String.format("<%s> %s", player.getDisplayName(), translatedMessage));
            } else {
                recipient.sendMessage(String.format("<%s> %s", player.getDisplayName(), message));
            }
        }

        event.setCancelled(true);
    }


    private String translateMessage(String message, String sourceLang, String targetLang) {
        try {
            URL url = new URL("https://api-free.deepl.com/v2/translate");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "DeepL-Auth-Key " + API_KEY);
            connection.setDoOutput(true);

            String postData = String.format("text=%s&source_lang=%s&target_lang=%s", message, sourceLang, targetLang);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                JSONObject responseObject = new JSONObject(response.toString());
                JSONArray translations = responseObject.getJSONArray("translations");
                return translations.getJSONObject(0).getString("text");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return message;
        }
    }
}