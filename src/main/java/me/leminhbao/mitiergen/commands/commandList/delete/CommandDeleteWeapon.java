package me.leminhbao.mitiergen.commands.commandList.delete;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandDeleteWeapon {
    private final MiTierGen plugin;

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mtg.delete.weapon") || !sender.hasPermission("mtg.delete") || !sender.hasPermission("mtg.admin")) {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
            return;
        }

        if (args.length != 3) {
            sender.sendMessage(plugin.getPrefix() + "Usage: /mitiergen delete_weapon <tier name> <weapon name>");
            return;
        }

        if (deleteType(args[1], args[2])) {
            sender.sendMessage(plugin.getPrefix() + "Weapon " + args[2] + " in tier " + args[1] + " has been deleted.");
        } else {
            sender.sendMessage(plugin.getPrefix() + "Weapon " + args[2] + " in tier " + args[1] + " does not exist.");
        }
    }

    private boolean deleteType(String tierID, String weaponID) {
        return plugin.getMiTierConfig().removeItem(tierID, weaponID);
    }
}
