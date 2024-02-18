package me.leminhbao.mitiergen.utils.mainUtils;

import me.leminhbao.mitiergen.MiTierGen;
import me.leminhbao.mitiergen.utils.ChatColor;
import me.leminhbao.mitiergen.utils.LogBuilder;

public class DisableUtils {

    public static void disableMessage(MiTierGen plugin) {
        LogBuilder logBuilder = new LogBuilder(plugin);
        logBuilder.addSeparator();
        logBuilder.addNewLine();
        logBuilder.addCentered("Disabling MiTierGen...", ChatColor.RED);
        logBuilder.addCentered("Thank you for using MiTierGen!", ChatColor.RED);
        logBuilder.addNewLine();
        logBuilder.addSeparator();

        logBuilder.build();
    }
}
