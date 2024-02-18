package me.leminhbao.mitiergen.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.leminhbao.mitiergen.MiTierGen;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.manager.TierManager;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter @ToString(exclude = "plugin")
public class MiTierConfig {

    private final HashMap<String, Tier> tierList = new HashMap<>();
    private final double defaultIfNotSet;

    private final MiTierGen plugin;

    /**
     * Use for updating the config only
     *
     * @param tierList Data should update to the config.yml
     * @param defaultIfNotSet Default value if the tier is not set
     * @param plugin The plugin instance
     */
    public MiTierConfig(HashMap<String, Tier> tierList, double defaultIfNotSet, MiTierGen plugin) {
        this.tierList.putAll(tierList);
        this.defaultIfNotSet = defaultIfNotSet;
        this.plugin = plugin;
    }

    public MiTierConfig(MiTierGen plugin) {
        TierManager tiers = MMOItems.plugin.getTiers();
        this.plugin = plugin;

        FileConfiguration config = plugin.getConfig();

        defaultIfNotSet = config.getDouble("defaultIfNotSet", 1.0);

        for (String tier : config.getConfigurationSection("StatsUp").getKeys(false)) {
            if (!tiers.has(tier)) {
                MiTierGen.getLoggerInstance().warning("Tier " + tier + " does not exist in MMOItems. Skipping...");

                continue;
            }

            HashMap<String, Item> appliedItems = new HashMap<>();
            for (String item: config.getConfigurationSection("StatsUp." + tier).getKeys(false)) {
                if (!MMOItems.plugin.getTypes().has(item)) {
                    MiTierGen.getLoggerInstance().warning("Item type " + item + " does not exist in MMOItems. Skipping...");
                    continue;
                }

                Item itemObj = new Item(item, new HashMap<>());

                for (String stat: config.getConfigurationSection("StatsUp." + tier + "." + item).getKeys(false)) {
                    String appliedStat = stat.toUpperCase().replace("-", "_");
                    ItemStat itemStat = MMOItems.plugin.getStats().get(appliedStat);
                    if (!(itemStat instanceof DoubleStat)) {
                        MiTierGen.getLoggerInstance().warning("Stat " + stat + " of " + item + " in tier " + tier + " does not numeric stat. Skipping...");
                        continue;
                    }

                    String dataType = config.getString("StatsUp." + tier + "." + item + "." + stat + ".type", ConfigConstants.DATA_TYPE_PERCENT);
                    double min = config.getDouble("StatsUp." + tier + "." + item + "." + stat + ".min", ConfigConstants.DISABLED_MIN);
                    double max = config.getDouble("StatsUp." + tier + "." + item + "." + stat + ".max", ConfigConstants.DISABLED_MAX);

                    if (min > max) {
                        Data data = new Data(dataType, max, min);
                        itemObj.getAppliedStats().put(itemStat, data);
                        continue;
                    }

                    Data data = new Data(dataType, min, max);
                    itemObj.getAppliedStats().put(itemStat, data);
                }
                appliedItems.put(item, itemObj);
            }
            tierList.put(tier, new Tier(appliedItems));
        }
    }

    public boolean hasTier(String tier) {
        return tierList.containsKey(tier);
    }

    public boolean hasItem(String tier, String itemType) {
        return tierList.get(tier).getAppliedItems().containsKey(itemType);
    }

    public @Nullable Item getItem(String tier, String itemType) {
        return tierList.get(tier).getAppliedItems().getOrDefault(itemType, null);
    }

    public void saveSettings() {
        FileConfiguration config = plugin.getConfig();

        config.set("defaultIfNotSet", defaultIfNotSet);

        for (String tier : tierList.keySet()) {
            for (Item item : tierList.get(tier).getAppliedItems().values()) {
                for (ItemStat stat : item.getAppliedStats().keySet()) {
                    String path = "StatsUp." + tier + "." + item.getItemType() + "." + normalize(stat.getId());
                    config.set(path + ".type", item.getAppliedStats().get(stat).getType());
                    config.set(path + ".min", item.getAppliedStats().get(stat).getMinPercent());
                    config.set(path + ".max", item.getAppliedStats().get(stat).getMaxPercent());
                }
            }
        }

        plugin.saveConfig();
    }

    private String normalize(String statId) {
        return statId.toLowerCase().replace("_", "-");
    }


    @Getter @AllArgsConstructor @ToString
    public static class Tier {
        HashMap<String, Item> appliedItems;
    }

    @Getter @AllArgsConstructor @ToString
    public static class Item {
        String itemType;
        HashMap<ItemStat, Data> appliedStats;
    }

    @Getter @AllArgsConstructor @ToString
    public static class Data {
        String type;
        double minPercent;
        double maxPercent;
    }
}
