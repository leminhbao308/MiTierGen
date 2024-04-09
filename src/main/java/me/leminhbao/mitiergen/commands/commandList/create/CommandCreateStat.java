package me.leminhbao.mitiergen.commands.commandList.create;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandCreateStat {
    private final MiTierGen plugin;

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mtg.create.stat") || !sender.hasPermission("mtg.create") || !sender.hasPermission("mtg.admin")) {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
            return;
        }

        if (args.length != 4) {
            sender.sendMessage(plugin.getPrefix() + "Usage: /mitiergen create_stat <tier name> <weapon name> <stat name>");
            return;
        }

        if (createStat(args[1], args[2], args[3])) {
            sender.sendMessage(plugin.getPrefix() + "Stat " + args[3] + " has been created in weapon " + args[2] + " of tier " + args[1] + ".");
        } else {
            sender.sendMessage(plugin.getPrefix() + "Stat " + args[3] + " already exists in weapon " + args[2] + " of tier " + args[1] + ".");
        }
    }

    private boolean createStat(String tierID, String weaponID, String statID) {
        return plugin.getMiTierConfig().addStat(tierID, weaponID, statID);
    }
}
