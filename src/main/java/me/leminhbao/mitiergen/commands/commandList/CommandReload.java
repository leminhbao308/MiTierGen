package me.leminhbao.mitiergen.commands.commandList;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import me.leminhbao.mitiergen.utils.mainUtils.EnableUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandReload {

    private final MiTierGen plugin;

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("mtg.reload") || sender.hasPermission("mtg.admin")) {
            EnableUtils.validateConfig(plugin, true);
            sender.sendMessage(plugin.getPrefix() + "Successfully reloaded the config.");
        } else {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
        }
    }
}
