package net.blockhost.livechattranslate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReplyMessages implements CommandExecutor, TabCompleter {

    private LiveChatTranslate plugin;
    private PrivateMessages privateMessages;
    private AntiSpam antiSpam;
    private String replyUsageMessage;
    private String lastUsageMessage;
    private String noOneToReplyMessage;
    private String playerOfflineMessage;

    public ReplyMessages(LiveChatTranslate plugin, PrivateMessages privateMessages, AntiSpam antiSpam) {
        this.plugin = plugin;
        this.privateMessages = privateMessages;
        this.antiSpam = antiSpam;
        replyUsageMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("reply-usage-message"));
        lastUsageMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("last-usage-message"));
        noOneToReplyMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("no-one-to-reply-message"));
        playerOfflineMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("player-offline-message"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (!antiSpam.canSendMessage((Player) sender)) {
            sender.sendMessage("You must wait 5 seconds between chat messages.");
            return true;
        }

        Player player = (Player) sender;
        UUID lastPlayerUUID;

        if (label.equalsIgnoreCase("last") || label.equalsIgnoreCase("l")) {
            lastPlayerUUID = privateMessages.getLastMessaged(player.getUniqueId());
        } else {
            lastPlayerUUID = privateMessages.getLastReplied(player.getUniqueId());
        }

        if (lastPlayerUUID == null) {
            player.sendMessage(noOneToReplyMessage);
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(lastPlayerUUID);

        if (targetPlayer == null) {
            player.sendMessage(playerOfflineMessage);
            return true;
        }

        if (args.length < 1) {
            String usageMsg = (label.equalsIgnoreCase("last") || label.equalsIgnoreCase("l")) ? lastUsageMessage : replyUsageMessage;
            player.sendMessage(ChatColor.RED + usageMsg);
            return true;
        }

        String message = String.join(" ", args);
        privateMessages.sendMessage(player, targetPlayer, message);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}