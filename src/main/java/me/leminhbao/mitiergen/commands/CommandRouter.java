package me.leminhbao.mitiergen.commands;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import me.leminhbao.mitiergen.commands.commandList.CommandEditor;
import me.leminhbao.mitiergen.commands.commandList.CommandIdentify;
import me.leminhbao.mitiergen.commands.commandList.CommandReload;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public class CommandRouter implements TabExecutor {

    private final MiTierGen plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                new CommandReload(plugin).onCommand(sender, command, label, args);
                break;
            case "identify":
                new CommandIdentify(plugin).onCommand(sender, command, label, args);
                break;
            case "editor":
                new CommandEditor(plugin).onCommand(sender, command, label, args);
                break;
            default:
                return false;
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("mitiergen") || command.getName().equalsIgnoreCase("mtg")) {
            if (args.length == 1) {
                return List.of("reload", "identify", "editor");
            }
        }
        return null;
    }
}
