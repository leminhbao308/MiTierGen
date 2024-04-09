package me.leminhbao.mitiergen.commands.commandList.delete;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandDeleteTier {
    private final MiTierGen plugin;

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mtg.delete.tier") || !sender.hasPermission("mtg.delete") || !sender.hasPermission("mtg.admin")) {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(plugin.getPrefix() + "Usage: /mitiergen delete_tier <tier name>");
            return;
        }

        if (deleteTier(args[1])) {
            sender.sendMessage(plugin.getPrefix() + "Tier " + args[1] + " has been deleted.");
        } else {
            sender.sendMessage(plugin.getPrefix() + "Tier " + args[1] + " does not exist.");
        }
    }

    private boolean deleteTier(String tierID) {
        return plugin.getMiTierConfig().removeTier(tierID);
    }
}
