package net.blockhost.livechattranslate;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class TextCommands implements CommandExecutor {
    private final LiveChatTranslate plugin;

    public TextCommands(final LiveChatTranslate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        FileConfiguration config = plugin.getConfig();
        String commandName = cmd.getName().toLowerCase();

        if (config.getBoolean("enable-" + commandName + "-command")) {
            if (commandName.equalsIgnoreCase("shop") || commandName.equalsIgnoreCase("store") ||
                    commandName.equalsIgnoreCase("buy") || commandName.equalsIgnoreCase("donate")) {
                commandName = "shop";
            }

            String message = config.getString(commandName + "-command");
            if (message != null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                return true;
            }
        }

        return false;
    }
}