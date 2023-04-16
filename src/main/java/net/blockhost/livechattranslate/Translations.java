package net.blockhost.livechattranslate;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class Translations implements Listener {

    private LiveChatTranslate plugin;

    private String API_KEY_DEEPL_FREE;
    private String API_KEY_DEEPL_PRO;
    private String CHAT_FORMAT;

    private int chanceDeeplFree;
    private int chanceDeeplPro;

    private boolean fromEnglish;
    private boolean fromSpanish;
    private boolean fromPolish;
    private boolean fromFrench;
    private boolean fromGerman;
    private boolean fromRussian;
    private boolean fromUkrainian;
    private boolean fromPortuguese;
    private boolean fromJapanese;
    private boolean fromGreek;
    private boolean fromTurkish;
    private boolean fromIndonesian;

    private boolean toEnglish;
    private boolean toSpanish;
    private boolean toPolish;
    private boolean toFrench;
    private boolean toGerman;
    private boolean toRussian;
    private boolean toUkrainian;
    private boolean toPortuguese;
    private boolean toJapanese;
    private boolean toGreek;
    private boolean toTurkish;
    private boolean toIndonesian;

    private boolean fromBypassPermEnabled;
    private String fromBypassPerm;
    private boolean toBypassPermEnabled;
    private String toBypassPerm;

    private boolean enableTranslationCache;

    private AntiSpam antiSpam;


    public Translations(LiveChatTranslate plugin, AntiSpam antiSpam) {
        this.plugin = plugin;
        this.antiSpam = antiSpam;

        API_KEY_DEEPL_FREE = plugin.getConfig().getString("deepl_api_key_free");
        API_KEY_DEEPL_PRO = plugin.getConfig().getString("deepl_api_key_pro");
        CHAT_FORMAT = plugin.getConfig().getString("chat-format");

        chanceDeeplFree = plugin.getConfig().getInt("chance_deepl_free");
        chanceDeeplPro = plugin.getConfig().getInt("chance_deepl_pro");

        fromEnglish = plugin.getConfig().getBoolean("From-English");
        fromSpanish = plugin.getConfig().getBoolean("From-Spanish");
        fromPolish = plugin.getConfig().getBoolean("From-Polish");
        fromFrench = plugin.getConfig().getBoolean("From-French");
        fromGerman = plugin.getConfig().getBoolean("From-German");
        fromRussian = plugin.getConfig().getBoolean("From-Russian");
        fromUkrainian = plugin.getConfig().getBoolean("From-Ukrainian");
        fromPortuguese = plugin.getConfig().getBoolean("From-Portuguese");
        fromJapanese = plugin.getConfig().getBoolean("From-Japanese");
        fromGreek = plugin.getConfig().getBoolean("From-Greek");
        fromTurkish = plugin.getConfig().getBoolean("From-Turkish");
        fromIndonesian = plugin.getConfig().getBoolean("From-Indonesian");

        toEnglish = plugin.getConfig().getBoolean("To-English");
        toSpanish = plugin.getConfig().getBoolean("To-Spanish");
        toPolish = plugin.getConfig().getBoolean("To-Polish");
        toFrench = plugin.getConfig().getBoolean("To-French");
        toGerman = plugin.getConfig().getBoolean("To-German");
        toRussian = plugin.getConfig().getBoolean("To-Russian");
        toUkrainian = plugin.getConfig().getBoolean("To-Ukrainian");
        toPortuguese = plugin.getConfig().getBoolean("To-Portuguese");
        toJapanese = plugin.getConfig().getBoolean("To-Japanese");
        toGreek = plugin.getConfig().getBoolean("To-Greek");
        toTurkish = plugin.getConfig().getBoolean("To-Turkish");
        toIndonesian = plugin.getConfig().getBoolean("To-Indonesian");

        fromBypassPermEnabled = plugin.getConfig().getBoolean("from-bypass-perm-enabled");
        fromBypassPerm = plugin.getConfig().getString("from-bypass-perm");
        toBypassPermEnabled = plugin.getConfig().getBoolean("to-bypass-perm-enabled");
        toBypassPerm = plugin.getConfig().getString("to-bypass-perm");

        enableTranslationCache = plugin.getConfig().getBoolean("enable-translation-cache");
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!antiSpam.canSendMessage(event.getPlayer())) {
            Player player = event.getPlayer();
            String message = event.getMessage();
            String playerLocale = player.getLocale();

            boolean shouldTranslateFrom = fromBypassPermEnabled ? !player.hasPermission(fromBypassPerm) : true;

            for (Player recipient : event.getRecipients()) {
                String recipientLocale = recipient.getLocale();
                String sourceLang = "";
                String targetLang = "";

                boolean shouldTranslateTo = toBypassPermEnabled ? !recipient.hasPermission(toBypassPerm) : true;

                // Set source language based on the player's locale
                if (fromEnglish && playerLocale.startsWith("en_")) {
                    sourceLang = "EN";
                } else if (fromSpanish && playerLocale.startsWith("es_")) {
                    sourceLang = "ES";
                } else if (fromPolish && playerLocale.startsWith("pl_")) {
                    sourceLang = "PL";
                } else if (fromFrench && playerLocale.startsWith("fr_")) {
                    sourceLang = "FR";
                } else if (fromGerman && playerLocale.startsWith("de_")) {
                    sourceLang = "DE";
                } else if (fromRussian && playerLocale.startsWith("ru_")) {
                    sourceLang = "RU";
                } else if (fromUkrainian && playerLocale.startsWith("uk_")) {
                    sourceLang = "UK";
                } else if (fromPortuguese && playerLocale.startsWith("pt_")) {
                    sourceLang = "PT";
                } else if (fromJapanese && playerLocale.startsWith("ja_")) {
                    sourceLang = "JA";
                } else if (fromGreek && playerLocale.startsWith("el_")) {
                    sourceLang = "EL";
                } else if (fromTurkish && playerLocale.startsWith("tr_")) {
                    sourceLang = "TR";
                } else if (fromIndonesian && playerLocale.startsWith("in_")) {
                    sourceLang = "ID";
                }
                // Set target language based on the recipient's locale
                if (toEnglish && recipientLocale.startsWith("en_")) {
                    targetLang = "EN";
                } else if (toSpanish && recipientLocale.startsWith("es_")) {
                    targetLang = "ES";
                } else if (toPolish && recipientLocale.startsWith("pl_")) {
                    targetLang = "PL";
                } else if (toFrench && recipientLocale.startsWith("fr_")) {
                    targetLang = "FR";
                } else if (toGerman && recipientLocale.startsWith("de_")) {
                    targetLang = "DE";
                } else if (toRussian && recipientLocale.startsWith("ru_")) {
                    targetLang = "RU";
                } else if (toUkrainian && recipientLocale.startsWith("uk_")) {
                    targetLang = "UK";
                } else if (toPortuguese && recipientLocale.startsWith("pt_")) {
                    targetLang = "PT";
                } else if (toJapanese && recipientLocale.startsWith("ja_")) {
                    targetLang = "JA";
                } else if (toGreek && recipientLocale.startsWith("el_")) {
                    targetLang = "EL";
                } else if (toTurkish && recipientLocale.startsWith("tr_")) {
                    targetLang = "TR";
                } else if (toIndonesian && recipientLocale.startsWith("in_")) {
                    targetLang = "ID";
                }

                String formattedMessage;
                if (shouldTranslateFrom && shouldTranslateTo && !sourceLang.isEmpty() && !targetLang.isEmpty() && !sourceLang.equals(targetLang)) {
                    String translatedMessage = translateMessage(message, sourceLang, targetLang);

                    formattedMessage = CHAT_FORMAT.replace("%player%", player.getDisplayName()).replace("%message%", translatedMessage);
                } else {
                    formattedMessage = CHAT_FORMAT.replace("%player%", player.getDisplayName()).replace("%message%", message);
                }
                recipient.sendMessage(formattedMessage);
            }
            event.setCancelled(true);
        }
        else{
            event.setCancelled(true);
        }
    }

    private ConcurrentHashMap<String, String> translationCache = new ConcurrentHashMap<>();

    public String translateMessage(String message, String sourceLang, String targetLang) {

        if (enableTranslationCache) {
            return translateMessageWithCache(message, sourceLang, targetLang);
        }

        return translateMessageWithoutCache(message, sourceLang, targetLang);
    }

    // New method for translating messages with the cache
    public String translateMessageWithCache(String message, String sourceLang, String targetLang) {
        String cacheKey = sourceLang + "_" + targetLang + "_" + message;

        if (translationCache.containsKey(cacheKey)) {
            return translationCache.get(cacheKey);
        }

        String translatedMessage = translateMessageWithoutCache(message, sourceLang, targetLang);
        translationCache.put(cacheKey, translatedMessage);

        // Log cache usage and translation details
        plugin.getLogger().info(String.format("Caching translation: %s (%s) -> %s (%s): %s", message, sourceLang, translatedMessage, targetLang, cacheKey));

        return translatedMessage;
    }


    // New method for translating messages without the cache
    private String translateMessageWithoutCache(String message, String sourceLang, String targetLang) {
        try {
            Random random = new Random();
            int selectedApi = random.nextInt(100) + 1;

            if (selectedApi <= chanceDeeplFree) {
                return translateMessageDeepL(message, sourceLang, targetLang, API_KEY_DEEPL_FREE, "https://api-free.deepl.com/v2/translate");
            } else {
                return translateMessageDeepL(message, sourceLang, targetLang, API_KEY_DEEPL_PRO, "https://api.deepl.com/v2/translate");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return message;
        }
    }

    private String translateMessageDeepL(String message, String sourceLang, String targetLang, String apiKey, String apiEndpoint) {
        try {
            URL url = new URL(apiEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "DeepL-Auth-Key " + apiKey);
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

    public String getLanguageFromLocale(String locale) {
        if (fromEnglish && locale.startsWith("en_")) {
            return "EN";
        } else if (fromSpanish && locale.startsWith("es_")) {
            return "ES";
        } else if (fromPolish && locale.startsWith("pl_")) {
            return "PL";
        } else if (fromFrench && locale.startsWith("fr_")) {
            return "FR";
        } else if (fromGerman && locale.startsWith("de_")) {
            return "DE";
        } else if (fromRussian && locale.startsWith("ru_")) {
            return "RU";
        } else if (fromUkrainian && locale.startsWith("uk_")) {
            return "UK";
        } else if (fromPortuguese && locale.startsWith("pt_")) {
            return "PT";
        } else if (fromJapanese && locale.startsWith("ja_")) {
            return "JA";
        } else if (fromGreek && locale.startsWith("el_")) {
            return "EL";
        } else if (fromTurkish && locale.startsWith("tr_")) {
            return "TR";
        } else if (fromIndonesian && locale.startsWith("in_")) {
            return "ID";
        }
        return "";
    }

}