package net.blockhost.livechattranslate;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LiveChatTranslate extends JavaPlugin {

    private Translations translations;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getConfig().getBoolean("enable-chat-translations")) {
            translations = new Translations(this);
            Bukkit.getPluginManager().registerEvents(translations, this);
        }

        boolean enablePms = getConfig().getBoolean("enable-pms");
        if (enablePms) {
            PrivateMessages privateMessages = new PrivateMessages(this, translations);
            ReplyMessages replyMessages = new ReplyMessages(this, privateMessages);

            if (getConfig().getBoolean("pms-command-msg")) {
                getCommand("msg").setExecutor(privateMessages);
                getCommand("msg").setTabCompleter(privateMessages);
            }
            if (getConfig().getBoolean("pms-command-dm")) {
                getCommand("dm").setExecutor(privateMessages);
                getCommand("dm").setTabCompleter(privateMessages);
            }
            if (getConfig().getBoolean("pms-command-pm")) {
                getCommand("pm").setExecutor(privateMessages);
                getCommand("pm").setTabCompleter(privateMessages);
            }
            if (getConfig().getBoolean("pms-command-whisper")) {
                getCommand("whisper").setExecutor(privateMessages);
                getCommand("whisper").setTabCompleter(privateMessages);
            }
            if (getConfig().getBoolean("pms-command-w")) {
                getCommand("w").setExecutor(privateMessages);
                getCommand("w").setTabCompleter(privateMessages);
            }

            if (getConfig().getBoolean("enable-reply")) {
                getCommand("reply").setExecutor(replyMessages);
                getCommand("reply").setTabCompleter(replyMessages);
            }
            if (getConfig().getBoolean("enable-command-r")) {
                getCommand("r").setExecutor(replyMessages);
                getCommand("r").setTabCompleter(replyMessages);
            }

            if (getConfig().getBoolean("enable-last")) {
                getCommand("last").setExecutor(replyMessages);
                getCommand                ("last").setTabCompleter(replyMessages);
            }
            if (getConfig().getBoolean("enable-command-l")) {
                getCommand("l").setExecutor(replyMessages);
                getCommand("l").setTabCompleter(replyMessages);
            }
        }

        TextCommands textCommands = new TextCommands(this);

        if (getConfig().getBoolean("enable-help-command")) {
            getCommand("help").setExecutor(textCommands);
        }
        if (getConfig().getBoolean("enable-discord-command")) {
            getCommand("discord").setExecutor(textCommands);
        }
        if (getConfig().getBoolean("enable-reddit-command")) {
            getCommand("reddit").setExecutor(textCommands);
        }
        if (getConfig().getBoolean("enable-shop-command")) {
            getCommand("shop").setExecutor(textCommands);
            if (getConfig().getBoolean("enable-store-alias")) {
                getCommand("store").setExecutor(textCommands);
            }
            if (getConfig().getBoolean("enable-buy-alias")) {
                getCommand("buy").setExecutor(textCommands);
            }
            if (getConfig().getBoolean("enable-donate-alias")) {
                getCommand("donate").setExecutor(textCommands);
            }
        }
        if (getConfig().getBoolean("enable-website-command")) {
            getCommand("website").setExecutor(textCommands);
        }
        if (getConfig().getBoolean("enable-twitter-command")) {
            getCommand("twitter").setExecutor(textCommands);
        }
        if (getConfig().getBoolean("enable-youtube-command")) {
            getCommand("youtube").setExecutor(textCommands);
        }


    }
}
