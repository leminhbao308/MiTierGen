package me.leminhbao.mitiergen.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.leminhbao.mitiergen.MiTierGen;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.manager.TierManager;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

@Getter
@ToString(exclude = "plugin")
public class MiTierConfig {

    private final HashMap<String, Tier> tierList = new HashMap<>();
    private final double defaultIfNotSet;

    private final MiTierGen plugin;

    /**
     * Use for updating the config only
     *
     * @param tierList        Data should update to the config.yml
     * @param defaultIfNotSet Default value if the tier is not set
     * @param plugin          The plugin instance
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
            for (String item : config.getConfigurationSection("StatsUp." + tier).getKeys(false)) {
                if (!MMOItems.plugin.getTypes().has(item)) {
                    MiTierGen.getLoggerInstance().warning("Item type " + item + " does not exist in MMOItems. Skipping...");
                    continue;
                }

                Item itemObj = new Item(item, new HashMap<>());

                for (String stat : config.getConfigurationSection("StatsUp." + tier + "." + item).getKeys(false)) {
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
        if (!hasTier(tier)) {
            return false;
        }
        return tierList.get(tier).getAppliedItems().containsKey(itemType);
    }

    public boolean hasStat(String tier, String itemType, ItemStat stat) {
        if (!hasItem(tier, itemType)) {
            return false;
        }

        return tierList.get(tier).getAppliedItems().get(itemType).getAppliedStats().containsKey(stat);
    }

    public @Nullable Item getItem(String tier, String itemType) {
        return tierList.get(tier).getAppliedItems().getOrDefault(itemType, null);
    }

    public void saveSettings() {
        FileConfiguration config = plugin.getConfig();

        config.set("defaultIfNotSet", defaultIfNotSet);

        for (String tier : tierList.keySet()) {
            if (!config.contains("StatsUp." + tier)) {
                config.createSection("StatsUp." + tier);
            }
            for (Item item : tierList.get(tier).getAppliedItems().values()) {
                if (!config.contains("StatsUp." + tier + "." + item.getItemType())) {
                    config.createSection("StatsUp." + tier + "." + item.getItemType());
                }
                for (ItemStat stat : item.getAppliedStats().keySet()) {
                    String path = "StatsUp." + tier + "." + item.getItemType() + "." + normalize(stat.getId());
                    if (!config.contains(path)) {
                        config.createSection(path);
                    }
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

    public boolean addTier(String id) {
        // Check if the tier already exists
        if (tierList.containsKey(id)) {
            return false;
        }

        // Check if the tier exists in MMOItems
        if (!MMOItems.plugin.getTiers().has(id)) {
            return false;
        }

        // Add the tier to the list
        tierList.put(id, new Tier(new HashMap<>()));

        // Add the tier to the config
        plugin.getConfig().createSection("StatsUp." + id);
        plugin.saveConfig();

        return true;
    }

    public boolean addItem(String tier, String itemType) {
        // Check if the tier exists
        if (!tierList.containsKey(tier)) {
            // Create the tier if it doesn't exist
            addTier(tier);
        }

        // Check if the item already exists
        if (tierList.get(tier).getAppliedItems().containsKey(itemType)) {
            return false;
        }

        // Check if the item exists in MMOItems
        if (!MMOItems.plugin.getTypes().has(itemType)) {
            return false;
        }

        // Add the item to the list
        tierList.get(tier).getAppliedItems().put(itemType, new Item(itemType, new HashMap<>()));

        // Add the item to the config
        plugin.getConfig().createSection("StatsUp." + tier + "." + itemType);
        plugin.saveConfig();

        return true;
    }

    public boolean addStat(String tier, String itemType, String stat) {
        // Check if the tier exists
        if (!tierList.containsKey(tier)) {
            plugin.getLogger().warning("Tier " + tier + " does not exist. Creating...");
            if (!addTier(tier)) {
                plugin.getLogger().warning("Failed to create tier " + tier + ". Skipping...");
                return false;
            }
        }

        // Check if the item exists
        if (!tierList.get(tier).getAppliedItems().containsKey(itemType)) {
            // Create the item if it doesn't exist
            addItem(tier, itemType);
        }

        // Check if the stat exists
        if (!MMOItems.plugin.getStats().has(stat)) {
            return false;
        }

        // Check if the item stat exists in MMOItems
        Item item = tierList.get(tier).getAppliedItems().get(itemType);
        ItemStat itemStat = MMOItems.plugin.getStats().get(stat);
        if (itemStat == null) {
            return false;
        }

        // Check if the stat is already applied
        if (item.getAppliedStats().containsKey(itemStat)) {
            return false;
        }

        // Add the stat to the item
        item.getAppliedStats().put(itemStat, new Data());

        // Add the stat to the config
        plugin.getConfig().set("StatsUp." + tier + "." + itemType + "." + normalize(stat) + ".type", ConfigConstants.DATA_TYPE_PERCENT);
        plugin.getConfig().set("StatsUp." + tier + "." + itemType + "." + normalize(stat) + ".min", ConfigConstants.DISABLED_MIN);
        plugin.getConfig().set("StatsUp." + tier + "." + itemType + "." + normalize(stat) + ".max", ConfigConstants.DISABLED_MAX);
        plugin.saveConfig();

        return true;
    }

    public boolean removeTier(String id) {
        if (!tierList.containsKey(id)) {
            return false;
        }

        tierList.remove(id);

        // Remove the tier from the config
        plugin.getConfig().set("StatsUp." + id, null);
        plugin.saveConfig();

        return true;
    }

    public boolean removeItem(String tier, String itemType) {
        if (!tierList.containsKey(tier)) {
            return false;
        }

        if (!tierList.get(tier).getAppliedItems().containsKey(itemType)) {
            return false;
        }

        tierList.get(tier).getAppliedItems().remove(itemType);

        // Remove the item from the config
        plugin.getConfig().set("StatsUp." + tier + "." + itemType, null);
        plugin.saveConfig();

        return true;
    }

    public boolean removeStat(String tier, String itemType, String stat) {
        if (!tierList.containsKey(tier)) {
            return false;
        }

        if (!tierList.get(tier).getAppliedItems().containsKey(itemType)) {
            return false;
        }

        Item item = tierList.get(tier).getAppliedItems().get(itemType);
        ItemStat itemStat = MMOItems.plugin.getStats().get(stat);
        if (itemStat == null) {
            return false;
        }

        if (!item.getAppliedStats().containsKey(itemStat)) {
            return false;
        }

        item.getAppliedStats().remove(itemStat);

        // Remove the stat from the config
        plugin.getConfig().set("StatsUp." + tier + "." + itemType + "." + normalize(stat), null);

        return true;
    }

    public boolean updateStatType(String tierID, String weaponID, String statID, String newType) {
        // Check if the tier exists
        if (!tierList.containsKey(tierID)) {
            return false;
        }

        // Check if the item exists
        if (!tierList.get(tierID).getAppliedItems().containsKey(weaponID)) {
            return false;
        }

        // Check if the stat exists
        Item item = tierList.get(tierID).getAppliedItems().get(weaponID);
        ItemStat itemStat = MMOItems.plugin.getStats().get(statID);
        if (itemStat == null) {
            return false;
        }

        // Check if the stat is already applied
        if (!item.getAppliedStats().containsKey(itemStat)) {
            return false;
        }

        if (!newType.equals(ConfigConstants.DATA_TYPE_PERCENT) && !newType.equals(ConfigConstants.DATA_TYPE_NUMBER)) {
            return false;
        }

        item.getAppliedStats().get(itemStat).setType(newType);

        // Update the stat type in the config
        plugin.getConfig().set("StatsUp." + tierID + "." + weaponID + "." + normalize(statID) + ".type", newType);
        plugin.saveConfig();

        return true;
    }

    public boolean updateStatMin(String tierID, String weaponID, String statID, double newMin) {
        // Check if the tier exists
        if (!tierList.containsKey(tierID)) {
            return false;
        }

        // Check if the item exists
        if (!tierList.get(tierID).getAppliedItems().containsKey(weaponID)) {
            return false;
        }

        // Check if the stat exists
        Item item = tierList.get(tierID).getAppliedItems().get(weaponID);
        ItemStat itemStat = MMOItems.plugin.getStats().get(statID);
        if (itemStat == null) {
            return false;
        }

        // Check if the stat is already applied
        if (!item.getAppliedStats().containsKey(itemStat)) {
            return false;
        }

        // Check if the new min is greater than the max
        if (newMin > item.getAppliedStats().get(itemStat).getMaxPercent()) {
            return false;
        }

        item.getAppliedStats().get(itemStat).setMinPercent(newMin);

        // Update the stat min in the config
        plugin.getConfig().set("StatsUp." + tierID + "." + weaponID + "." + normalize(statID) + ".min", newMin);
        plugin.saveConfig();

        return true;
    }

    public boolean updateStatMax(String tierID, String weaponID, String statID, double newMax) {
        // Check if the tier exists
        if (!tierList.containsKey(tierID)) {
            return false;
        }

        // Check if the item exists
        if (!tierList.get(tierID).getAppliedItems().containsKey(weaponID)) {
            return false;
        }

        // Check if the stat exists
        Item item = tierList.get(tierID).getAppliedItems().get(weaponID);
        ItemStat itemStat = MMOItems.plugin.getStats().get(statID);
        if (itemStat == null) {
            return false;
        }

        // Check if the stat is already applied
        if (!item.getAppliedStats().containsKey(itemStat)) {
            return false;
        }

        // Check if the new max is less than the min
        if (newMax < item.getAppliedStats().get(itemStat).getMinPercent()) {
            return false;
        }

        item.getAppliedStats().get(itemStat).setMaxPercent(newMax);

        // Update the stat max in the config
        plugin.getConfig().set("StatsUp." + tierID + "." + weaponID + "." + normalize(statID) + ".max", newMax);
        plugin.saveConfig();

        return true;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class Tier {
        HashMap<String, Item> appliedItems;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class Item {
        String itemType;
        HashMap<ItemStat, Data> appliedStats;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class Data {
        String type;
        double minPercent;
        double maxPercent;

        public Data() {
            this.type = ConfigConstants.DATA_TYPE_PERCENT;
            this.minPercent = ConfigConstants.DISABLED_MIN;
            this.maxPercent = ConfigConstants.DISABLED_MAX;
        }
    }
}
