package me.leminhbao.mitiergen.commands.commandList.update;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import me.leminhbao.mitiergen.config.ConfigConstants;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandUpdateType {
    private final MiTierGen plugin;

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mtg.update.type") || !sender.hasPermission("mtg.update") || !sender.hasPermission("mtg.admin")) {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
            return;
        }

        if (args.length != 5) {
            sender.sendMessage(plugin.getPrefix() + "Usage: /mitiergen update_stat_type <tier name> <weapon name> <stat name> <new type>");
            return;
        }

        if (updateStat(args[1], args[2], args[3], args[4])) {
            sender.sendMessage(plugin.getPrefix() + "Stat " + args[3] + " in weapon " + args[2] + " of tier " + args[1] + " has been updated to type " + args[4]);
        } else {
            sender.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', "&7Stat &4" + args[3] + "&7 in weapon &6" + args[2] + "&7 of tier &6" + args[1] + "&7 failed to update type."));
            sender.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', "&3This may caused by the stat not existing or the new type not being valid."));
            sender.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', "&3Valid types: "));
            sender.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', "  - &a" + ConfigConstants.DATA_TYPE_PERCENT));
            sender.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', "  - &a" + ConfigConstants.DATA_TYPE_NUMBER));
        }
    }

    private boolean updateStat(String tierID, String weaponID, String statID, String newType) {
        return plugin.getMiTierConfig().updateStatType(tierID, weaponID, statID, newType);
    }
}
