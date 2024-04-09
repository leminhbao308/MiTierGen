package me.leminhbao.mitiergen.commands.commandList.create;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandCreateTier {
    private final MiTierGen plugin;

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mtg.create.tier") || !sender.hasPermission("mtg.create") || !sender.hasPermission("mtg.admin")) {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(plugin.getPrefix() + "Usage: /mitiergen create_tier <tier name>");
            return;
        }

        if (createTier(args[1])) {
            sender.sendMessage(plugin.getPrefix() + "Tier " + args[1] + " has been created.");
        } else {
            sender.sendMessage(plugin.getPrefix() + "Tier " + args[1] + " already exists.");
        }
    }

    private boolean createTier(String tierID) {
        return plugin.getMiTierConfig().addTier(tierID);
    }
}
