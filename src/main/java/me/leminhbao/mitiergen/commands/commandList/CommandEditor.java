package me.leminhbao.mitiergen.commands.commandList;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import me.leminhbao.mitiergen.inventoryframework.EditorGui;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandEditor {
    private final MiTierGen plugin;

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only for players.");
            return;
        }

        if (!sender.hasPermission("mtg.editor") || !sender.hasPermission("mtg.admin")) {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
            return;
        }

        if (!plugin.getMiMenuConfig().isValid()) {
            sender.sendMessage(plugin.getPrefix() + "The menu config is invalid. This GUI Editor is disabled.");
            return;
        }

        Player player = (Player) sender;
        new EditorGui(plugin).open(player);
    }
}
