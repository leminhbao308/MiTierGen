package me.leminhbao.mitiergen.commands;

import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import me.leminhbao.mitiergen.commands.commandList.CommandHelp;
import me.leminhbao.mitiergen.commands.commandList.CommandIdentify;
import me.leminhbao.mitiergen.commands.commandList.CommandReload;
import me.leminhbao.mitiergen.commands.commandList.create.CommandCreateStat;
import me.leminhbao.mitiergen.commands.commandList.create.CommandCreateTier;
import me.leminhbao.mitiergen.commands.commandList.create.CommandCreateWeapon;
import me.leminhbao.mitiergen.commands.commandList.delete.CommandDeleteStat;
import me.leminhbao.mitiergen.commands.commandList.delete.CommandDeleteTier;
import me.leminhbao.mitiergen.commands.commandList.delete.CommandDeleteWeapon;
import me.leminhbao.mitiergen.commands.commandList.update.CommandUpdateMax;
import me.leminhbao.mitiergen.commands.commandList.update.CommandUpdateMin;
import me.leminhbao.mitiergen.commands.commandList.update.CommandUpdateType;
import me.leminhbao.mitiergen.config.ConfigConstants;
import me.leminhbao.mitiergen.config.MiTierConfig;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            case "help":
                new CommandHelp().onCommand(sender, command, label, args);
                break;

            // Create tier, type, and stat
            case "create_tier":
                new CommandCreateTier(plugin).onCommand(sender, command, label, args);
                break;
            case "create_weapon":
                new CommandCreateWeapon(plugin).onCommand(sender, command, label, args);
                break;
            case "create_stat":
                new CommandCreateStat(plugin).onCommand(sender, command, label, args);
                break;

            // Delete tier, type, and stat
            case "delete_tier":
                new CommandDeleteTier(plugin).onCommand(sender, command, label, args);
                break;
            case "delete_weapon":
                new CommandDeleteWeapon(plugin).onCommand(sender, command, label, args);
                break;
            case "delete_stat":
                new CommandDeleteStat(plugin).onCommand(sender, command, label, args);
                break;

            // Update stat
            case "update_stat_type":
                new CommandUpdateType(plugin).onCommand(sender, command, label, args);
                break;
            case "update_stat_min":
                new CommandUpdateMin(plugin).onCommand(sender, command, label, args);
                break;
            case "update_stat_max":
                new CommandUpdateMax(plugin).onCommand(sender, command, label, args);
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
            List<String> firstArgs = List.of("identify",
                    // Essential commands
                    "help", "reload",
                    // Create tier, type, and stat
                    "create_tier", "create_weapon", "create_stat",
                    // Delete tier, type, and stat
                    "delete_tier", "delete_weapon", "delete_stat",
                    // Update stat
                    "update_stat_type", "update_stat_min", "update_stat_max");
            if (args.length == 1) {
                // Get matching first arguments
                List<String> result = new ArrayList<>();
                for (String firstArg : firstArgs) {
                    if (firstArg.startsWith(args[0].toLowerCase())) {
                        result.add(firstArg);
                    }
                }
                return result;
            }

            if (args.length >= 2) {
                // create section
                if (args[0].startsWith("create")) {
                    return generateCreateTabComplete(args);
                }

                // delete section
                if (args[0].startsWith("delete")) {
                    return generateDeleteTabComplete(args);
                }

                // update section
                if (args[0].startsWith("update")) {
                    return generateUpdateTabComplete(args);
                }
            }
        }
        return null;
    }

    //<editor-fold desc="Create Tab Completer">
    // /mitiergen create_tier <tier name>
    // /mitiergen create_weapon <tier name> <weapon name>
    // /mitiergen create_stat <tier name> <weapon name> <stat name>
    private List<String> generateCreateTabComplete(String[] args) {
        List<String> result = new ArrayList<>();

        if (args[0].equalsIgnoreCase("create_tier")) {
            if (args.length != 2)
                return null;
            addTier(result);
            return result.stream().filter(s -> s.startsWith(args[args.length - 1])).toList();
        } else if (args[0].equalsIgnoreCase("create_weapon")) {
            if (args.length > 3)
                return null;

            if (args.length == 2) {
                addOldTier(result);
            }
            if (args.length == 3) {
                addType(result, args[1]);
            }
            return result.stream().filter(s -> s.startsWith(args[args.length - 1])).toList();
        } else if (args[0].equalsIgnoreCase("create_stat")) {
            if (args.length > 4)
                return null;
            if (args.length == 2) {
                addOldTier(result);
            }
            if (args.length == 3) {
                addOldType(result, args[1]);
            }
            if (args.length == 4) {
                addStat(result, args[1], args[2]);
            }
            return result.stream().filter(s -> s.startsWith(args[args.length - 1])).toList();
        }
        return null;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Delete Tab Completer">
    // /mitiergen delete_tier <tier name>
    // /mitiergen delete_weapon <tier name> <weapon name>
    // /mitiergen delete_stat <tier name> <weapon name> <stat name>
    private List<String> generateDeleteTabComplete(String[] args) {
        List<String> result = new ArrayList<>();

        if (args[0].equalsIgnoreCase("delete_tier")) {
            if (args.length != 2)
                return null;
            addOldTier(result);
            return result.stream().filter(s -> s.startsWith(args[args.length - 1])).toList();
        } else if (args[0].equalsIgnoreCase("delete_weapon")) {
            if (args.length > 3)
                return null;

            if (args.length == 2) {
                addOldTier(result);
            }
            if (args.length == 3) {
                addOldType(result, args[1]);
            }
            return result.stream().filter(s -> s.startsWith(args[args.length - 1])).toList();
        } else if (args[0].equalsIgnoreCase("delete_stat")) {
            if (args.length > 4)
                return null;
            if (args.length == 2) {
                addOldTier(result);
            }
            if (args.length == 3) {
                addOldType(result, args[1]);
            }
            if (args.length == 4) {
                addOldStat(result, args[1], args[2]);
            }
            return result.stream().filter(s -> s.startsWith(args[args.length - 1])).toList();
        }
        return null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Update Tab Completer">
    private List<String> generateUpdateTabComplete(String[] args) {
        List<String> result = new ArrayList<>();

        if (args[0].equalsIgnoreCase("update_stat_type")) {
            if (args.length > 5)
                return null;
            if (args.length == 2) {
                addOldTier(result);
            }
            if (args.length == 3) {
                addOldType(result, args[1]);
            }
            if (args.length == 4) {
                addOldStat(result, args[1], args[2]);
            }
            if (args.length == 5) {
                result.add(ConfigConstants.DATA_TYPE_PERCENT);
                result.add(ConfigConstants.DATA_TYPE_NUMBER);
            }
            return result.stream().filter(s -> s.startsWith(args[args.length - 1])).toList();
        }

        if (args[0].equalsIgnoreCase("update_stat_min") || args[0].equalsIgnoreCase("update_stat_max")) {
            if (args.length > 5)
                return null;
            if (args.length == 2) {
                addOldTier(result);
            }
            if (args.length == 3) {
                addOldType(result, args[1]);
            }
            if (args.length == 4) {
                addOldStat(result, args[1], args[2]);
            }
            if (args.length == 5) {
                result.add("<new value>");
            }
            return result.stream().filter(s -> s.startsWith(args[args.length - 1])).toList();
        }
        return null;
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Function Adding Args">
    private void addTier(List<String> result) {
        result.clear();
        for (ItemTier tier : MMOItems.plugin.getTiers().getAll()) {
            if (!plugin.getMiTierConfig().hasTier(tier.getId()))
                result.add(tier.getId());
        }
    }

    private void addOldTier(List<String> result) {
        result.clear();
        for (Map.Entry<String, MiTierConfig.Tier> tier : plugin.getMiTierConfig().getTierList().entrySet()) {
            result.add(tier.getKey());
        }
    }

    private void addType(List<String> result, String tierID) {
        result.clear();
        for (Type weapon : MMOItems.plugin.getTypes().getAll()) {
            if (!this.plugin.getMiTierConfig().hasItem(tierID, weapon.getId()))
                result.add(weapon.getId());
        }
    }

    private void addOldType(List<String> result, String tierID) {
        result.clear();
        for (Map.Entry<String, MiTierConfig.Item> weapon : plugin.getMiTierConfig().getTierList().get(tierID).getAppliedItems().entrySet()) {
            result.add(weapon.getKey());
        }
    }

    private void addStat(List<String> result, String tierID, String weaponID) {
        result.clear();
        for (ItemStat stat : MMOItems.plugin.getStats().getAll()) {
            if (!(stat instanceof DoubleStat))
                continue;

            if (!plugin.getMiTierConfig().hasStat(tierID, weaponID, stat))
                result.add(stat.getId());
        }
    }

    private void addOldStat(List<String> result, String tierID, String weaponID) {
        result.clear();
        for (Map.Entry<ItemStat, MiTierConfig.Data> stat : plugin.getMiTierConfig().getTierList().get(tierID).getAppliedItems().get(weaponID).getAppliedStats().entrySet()) {
            result.add(stat.getKey().getId());
        }
    }
    //</editor-fold>
}