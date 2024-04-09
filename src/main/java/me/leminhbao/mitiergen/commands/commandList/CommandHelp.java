package me.leminhbao.mitiergen.commands.commandList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandHelp {
    private final String title = ChatColor.translateAlternateColorCodes('&', "§7§m-----------------§r §6§lMiTierGen §7§m-----------------");
    private final String footer = ChatColor.translateAlternateColorCodes('&', "§7§m------------------------------------------------");
    private final List<String> helpList = List.of(
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§lhelp"),
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§lreload"),
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§lcreate_tier <tier name>"),
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§lcreate_weapon <tier name> <weapon name>"),
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§lcreate_stat <tier name> <weapon name> <stat name>"),
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§lupdate_stat_type <tier name> <weapon name> <stat name> <new type>"),
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§lupdate_stat_min <tier name> <weapon name> <stat name> <new value>"),
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§lupdate_stat_max <tier name> <weapon name> <stat name> <new value>"),
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§ldelete_tier <tier name>"),
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§ldelete_weapon <tier name> <weapon name>"),
            ChatColor.translateAlternateColorCodes('&', "  §6§l- /mtg §e§ldelete_stat <tier name> <weapon name> <stat name>")
    );

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // No args
        sender.sendMessage(title);
        // Show only command that the sender has permission to use
        if (sender.hasPermission("mtg.admin")) {
            helpList.forEach(sender::sendMessage);
        } else {
            if (sender.hasPermission("mtg.reload")) {
                sender.sendMessage(helpList.get(1));
            }
            if (sender.hasPermission("mtg.create")) {
                helpList.subList(2, 5).forEach(sender::sendMessage);
            }
            if (sender.hasPermission("mtg.update")) {
                helpList.subList(5, 8).forEach(sender::sendMessage);
            }
            if (sender.hasPermission("mtg.delete")) {
                helpList.subList(8, 11).forEach(sender::sendMessage);
            }
        }
        sender.sendMessage(footer);
    }
}
