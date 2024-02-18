package me.leminhbao.mitiergen.utils.mainUtils;

import me.leminhbao.mitiergen.MiTierGen;
import me.leminhbao.mitiergen.commands.CommandRouter;
import me.leminhbao.mitiergen.config.MiMenuConfig;
import me.leminhbao.mitiergen.config.MiTierConfig;
import me.leminhbao.mitiergen.utils.ChatColor;
import me.leminhbao.mitiergen.utils.LogBuilder;

public class EnableUtils {
    public static void logPluginInfo(MiTierGen plugin) {
        LogBuilder logBuilder = new LogBuilder(plugin);
        logBuilder.addSeparator();
        logBuilder.addCentered("MiTierGen", ChatColor.GREEN);
        logBuilder.addCentered("Version: " + plugin.getDescription().getVersion(), ChatColor.GREEN);
        logBuilder.addCentered("Author: " + plugin.getDescription().getAuthors().get(0), ChatColor.GREEN);
        logBuilder.addSeparator();

        logBuilder.build();
    }

    public static void checkingDependencies(MiTierGen plugin) {
        LogBuilder logBuilder = new LogBuilder(plugin);
        logBuilder.addNewLine();
        logBuilder.add("Checking dependencies...", ChatColor.YELLOW);

        // MythicLib Checking
        if (plugin.getServer().getPluginManager().getPlugin("MythicLib") == null) {
            logBuilder.finalAdd("MythicLib not found! Disabling plugin...", ChatColor.RED);
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        } else {
            logBuilder.add("MythicLib found! Hooking...", ChatColor.AQUA);
        }

        // MMOItems Checking
        if (plugin.getServer().getPluginManager().getPlugin("MMOItems") == null) {
            logBuilder.finalAdd("MMOItems not found! Disabling plugin...", ChatColor.RED);
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        } else {
            logBuilder.add("MMOItems found! Hooking...", ChatColor.AQUA);
        }

        logBuilder.add("All dependencies found!", ChatColor.GREEN);
        logBuilder.build();
    }

    public static void validateConfig(MiTierGen plugin, boolean reloading) {
        if (reloading) {
            plugin.reloadConfig();
        }

        LogBuilder logBuilder = new LogBuilder(plugin);
        logBuilder.addNewLine();
        logBuilder.add("Validating config...", ChatColor.YELLOW);

        plugin.setMiTierConfig(new MiTierConfig(plugin));
        plugin.setMiMenuConfig(new MiMenuConfig(plugin));

        logBuilder.add("Total tiers loaded: " + plugin.getMiTierConfig().getTierList().size(), ChatColor.AQUA);
        logBuilder.add("Menu Editor has loaded: " + (plugin.getMiMenuConfig().isValid() ? "Successfully" : "Failed"), (plugin.getMiMenuConfig().isValid() ? ChatColor.GREEN : ChatColor.RED));
        logBuilder.add("MiTierConfig: " + plugin.getMiTierConfig().toString(), ChatColor.AQUA);

        String rawPrefix = plugin.getConfig().getString("Prefix", plugin.getDescription().getPrefix());
        plugin.setPrefix(org.bukkit.ChatColor.translateAlternateColorCodes('&', rawPrefix));
        logBuilder.add("Config validation finished!", ChatColor.GREEN);
        logBuilder.addSeparator();
        logBuilder.build();
    }

    public static void registerEvents(MiTierGen plugin) {
    }

    public static void registerCommands(MiTierGen plugin) {
        CommandRouter commandRouter = new CommandRouter(plugin);
        plugin.getCommand("mitiergen").setExecutor(commandRouter);
        plugin.getCommand("mitiergen").setTabCompleter(commandRouter);
    }

    public static void registerTabCompleters(MiTierGen plugin) {
    }

    public static void registerTasks(MiTierGen plugin) {
    }
}
