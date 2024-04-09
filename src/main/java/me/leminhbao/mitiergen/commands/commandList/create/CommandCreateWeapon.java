package me.leminhbao.mitiergen.commands.commandList.create;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandCreateWeapon {
    private final MiTierGen plugin;

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mtg.create.weapon") || !sender.hasPermission("mtg.create") || !sender.hasPermission("mtg.admin")) {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
            return;
        }

        if (args.length != 3) {
            sender.sendMessage(plugin.getPrefix() + "Usage: /mitiergen create_weapon <tier name> <weapon name>");
            return;
        }

        if (createType(args[1], args[2])) {
            sender.sendMessage(plugin.getPrefix() + "Weapon " + args[2] + " has been created in tier " + args[1] + ".");
        } else {
            sender.sendMessage(plugin.getPrefix() + "Weapon " + args[2] + " already exists in tier " + args[1] + ".");
        }
    }

    private boolean createType(String tierID, String weaponID) {
        return plugin.getMiTierConfig().addItem(tierID, weaponID);
    }

    private boolean createStat(String[] arg) {
        return plugin.getMiTierConfig().addStat(arg[1], arg[2], arg[3]);
    }
}
