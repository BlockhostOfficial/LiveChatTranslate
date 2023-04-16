package net.blockhost.livechattranslate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PrivateMessages implements CommandExecutor, TabCompleter {

    private LiveChatTranslate plugin;
    private Translations translations;
    private int pmsFormat;
    private String pmsFormat1;
    private String pmsFormat2Sender;
    private String pmsFormat2Receiver;
    private String usageMessage;
    private String playerNotFoundMessage;

    private boolean enablePmsTranslations;

    private boolean fromBypassPermEnabled;
    private String fromBypassPerm;
    private boolean toBypassPermEnabled;
    private String toBypassPerm;
    private AntiSpam antiSpam;


    public PrivateMessages(LiveChatTranslate plugin, Translations translations, AntiSpam antiSpam) {
        this.plugin = plugin;
        this.translations = translations;
        this.antiSpam = antiSpam;
        pmsFormat = plugin.getConfig().getInt("pms-format");
        pmsFormat1 = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("pms-format-1"));
        pmsFormat2Sender = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("pms-format-2-sender"));
        pmsFormat2Receiver = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("pms-format-2-receiver"));
        usageMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("pms-usage-message"));
        playerNotFoundMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("player-not-found-message"));

        enablePmsTranslations = plugin.getConfig().getBoolean("enable-pms-translations");

        // Added bypass permissions
        fromBypassPermEnabled = plugin.getConfig().getBoolean("from-bypass-perm-enabled");
        fromBypassPerm = plugin.getConfig().getString("from-bypass-perm");
        toBypassPermEnabled = plugin.getConfig().getBoolean("to-bypass-perm-enabled");
        toBypassPerm = plugin.getConfig().getString("to-bypass-perm");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(usageMessage);
            return true;
        }

        if (!antiSpam.canSendMessage((Player) sender)) {
            sender.sendMessage("You must wait 5 seconds between chat messages.");
            return true;
        }

        Player senderPlayer = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) {
            sender.sendMessage(playerNotFoundMessage);
            return true;
        }

        String message = String.join(" ", Arrays.asList(args).subList(1, args.length));
        sendMessage(senderPlayer, targetPlayer, message);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player) || args.length != 1) {
            return new ArrayList<>();
        }

        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }

    private final HashMap<UUID, UUID> lastReplied = new HashMap<>();
    private final HashMap<UUID, UUID> lastMessaged = new HashMap<>();

    public void setLastReplied(UUID sender, UUID target) {
        lastReplied.put(sender, target);
    }

    public void setLastMessaged(UUID sender, UUID target) {
        lastMessaged.put(sender, target);
    }

    public UUID getLastReplied(UUID sender) {
        return lastReplied.get(sender);
    }

    public UUID getLastMessaged(UUID sender) {
        return lastMessaged.get(sender);
    }

    public void sendMessage(Player sender, Player target, String message) {
        boolean shouldTranslateFrom = fromBypassPermEnabled ? !sender.hasPermission(fromBypassPerm) : true;
        boolean shouldTranslateTo = toBypassPermEnabled ? !target.hasPermission(toBypassPerm) : true;

        String translatedMessage = message;

        if (enablePmsTranslations && (shouldTranslateFrom || shouldTranslateTo)) {
            String senderLocale = sender.getLocale();
            String targetLocale = target.getLocale();
            String sourceLang = translations.getLanguageFromLocale(senderLocale);
            String targetLang = translations.getLanguageFromLocale(targetLocale);

            if (!sourceLang.isEmpty() && !targetLang.isEmpty() && !sourceLang.equals(targetLang)) {
                // Use the translation cache to translate the message
                translatedMessage = translations.translateMessageWithCache(message, sourceLang, targetLang);
            }
        }

        if (pmsFormat == 1) {
            String formattedMessageSender = pmsFormat1.replace("%1%", sender.getDisplayName()).replace("%2%", target.getDisplayName()).replace("%message%", message);
            String formattedMessageTarget = pmsFormat1.replace("%1%", sender.getDisplayName()).replace("%2%", target.getDisplayName()).replace("%message%", translatedMessage);
            sender.sendMessage(formattedMessageSender);
            target.sendMessage(formattedMessageTarget);
        } else if (pmsFormat == 2) {
            sender.sendMessage(pmsFormat2Sender.replace("%player%", target.getDisplayName()).replace("%message%", message));
            target.sendMessage(pmsFormat2Receiver.replace("%player%", sender.getDisplayName()).replace("%message%", translatedMessage));
        }

        setLastReplied(sender.getUniqueId(), target.getUniqueId());
        setLastMessaged(sender.getUniqueId(), target.getUniqueId());

        setLastReplied(target.getUniqueId(), sender.getUniqueId());
    }
}