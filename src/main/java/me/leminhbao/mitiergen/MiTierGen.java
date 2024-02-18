package me.leminhbao.mitiergen;

import lombok.Getter;
import lombok.Setter;
import me.leminhbao.mitiergen.config.MiMenuConfig;
import me.leminhbao.mitiergen.config.MiTierConfig;
import me.leminhbao.mitiergen.utils.mainUtils.DisableUtils;
import me.leminhbao.mitiergen.utils.mainUtils.EnableUtils;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Setter
@Getter
public final class MiTierGen extends JavaPlugin {

    private String prefix;
    private MiTierConfig miTierConfig;
    private MiMenuConfig miMenuConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        long startTime = System.currentTimeMillis();

        EnableUtils.logPluginInfo(this);
        EnableUtils.checkingDependencies(this);

        // Config section
        this.saveDefaultConfig();
        EnableUtils.validateConfig(this, false);

        // Register events
        EnableUtils.registerEvents(this);

        // Register commands
        EnableUtils.registerCommands(this);

        // Register tab completers
        EnableUtils.registerTabCompleters(this);

        // Register tasks
        EnableUtils.registerTasks(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        DisableUtils.disableMessage(this);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        String rawPrefix = getConfig().getString("Prefix", getDescription().getPrefix());
        setPrefix(ChatColor.translateAlternateColorCodes('&', rawPrefix));
        setMiTierConfig(new MiTierConfig(this));
        setMiMenuConfig(new MiMenuConfig(this));
    }

    public static Logger getLoggerInstance() {
        return getPlugin(MiTierGen.class).getLogger();
    }
}
