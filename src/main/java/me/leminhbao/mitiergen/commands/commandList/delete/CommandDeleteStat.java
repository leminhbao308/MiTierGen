package me.leminhbao.mitiergen.commands.commandList.delete;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandDeleteStat {
    private final MiTierGen plugin;

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mtg.delete.stat") || !sender.hasPermission("mtg.delete") || !sender.hasPermission("mtg.admin")) {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
            return;
        }

        if (args.length != 4) {
            sender.sendMessage(plugin.getPrefix() + "Usage: /mitiergen delete_stat <tier name> <weapon name> <stat name>");
            return;
        }

        if (deleteStat(args[1], args[2], args[3])) {
            sender.sendMessage(plugin.getPrefix() + "Stat " + args[3] + " in weapon " + args[2] + " of tier " + args[1] + " has been deleted.");
        } else {
            sender.sendMessage(plugin.getPrefix() + "Stat " + args[3] + " in weapon " + args[2] + " of tier " + args[1] + " does not exist.");
        }
    }

    private boolean deleteStat(String tierID, String weaponID, String statID) {
        return plugin.getMiTierConfig().removeStat(tierID, weaponID, statID);
    }
}
