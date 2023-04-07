package net.blockhost.livechattranslate;

import org.bukkit.plugin.java.JavaPlugin;

public final class LiveChatTranslate extends JavaPlugin {

    @Override
    public void onEnable() {
        String apiKey = getConfig().getString("deepl-api-key");
        this.saveDefaultConfig();
        TranslationService translationService = new TranslationService(apiKey);
        getServer().getPluginManager().registerEvents(new TranslateChatListener(this, translationService), this);
    }

}
