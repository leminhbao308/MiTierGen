package me.leminhbao.mitiergen.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.leminhbao.mitiergen.MiTierGen;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

@Getter
public class MiMenuConfig {
    private boolean valid = true;

    private final HashMap<String, List<String>> placeholdersList;
    private final HashMap<String, Button> buttons;
    private final HashMap<String, ItemFormat> itemFormats;

    private final int rows;
    private final String title;

    private final MiTierGen plugin;

    public MiMenuConfig(MiTierGen plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();

        this.placeholdersList = new HashMap<>();
        this.buttons = new HashMap<>();
        this.itemFormats = new HashMap<>();
        setupPlaceholders();
        setupButtons(config);
        setupItemFormats(config);

        rows = 6;

        title = ChatColor.translateAlternateColorCodes('&', config.getString("menuSettings.title", "&6&lMi Tier Gen Editor"));
    }

    private void setupButtons(FileConfiguration config) {
        ConfigurationSection buttonSection = config.getConfigurationSection("menuSettings.buttons");

        if (buttonSection == null) {
            MiTierGen.getLoggerInstance().warning("No buttons found in the config. GUI features will not work.");
            valid = false;
            return;
        }

        for (String button : buttonSection.getKeys(false)) {
            if (ConfigConstants.PLACEHOLDERS.notMatches(button)) {
                MiTierGen.getLoggerInstance().warning("Button " + button + " is not a valid placeholder. GUI features will not work.");
                valid = false;
                return;
            }
            String name = ChatColor.translateAlternateColorCodes('&', buttonSection.getString(button + ".name", "Button"));
            Material material = Material.matchMaterial(buttonSection.getString(button + ".material", "ARROW")) != null ?
                    Material.matchMaterial(buttonSection.getString(button + ".material", "ARROW")) : Material.ARROW;
            List<String> lore = buttonSection.getStringList(button + ".lore");
            lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));

            this.buttons.put(button, new Button(name, lore, material));
        }
    }

    private void setupItemFormats(FileConfiguration config) {
        ConfigurationSection itemFormatSection = config.getConfigurationSection("menuSettings.itemFormats");

        if (itemFormatSection == null) {
            MiTierGen.getLoggerInstance().warning("No item formats found in the config. GUI features will not work.");
            valid = false;
            return;
        }

        for (String itemFormat : itemFormatSection.getKeys(false)) {
            if (ConfigConstants.PLACEHOLDERS.notMatches(itemFormat)) {
                MiTierGen.getLoggerInstance().warning("Item format " + itemFormat + " is not a valid placeholder. GUI features will not work.");
                valid = false;
                return;
            }

            String name = ChatColor.translateAlternateColorCodes('&', itemFormatSection.getString(itemFormat + ".name", "Item"));
            List<String> lore = itemFormatSection.getStringList(itemFormat + ".lore");
            lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));

            this.itemFormats.put(itemFormat, new ItemFormat(name, lore));
        }
    }


    public List<String> getPlaceholder(String placeholder) {
        return placeholdersList.get(placeholder);
    }


    private void setupPlaceholders() {
        this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.NEXT_PAGE_ITEM, List.of("%current_page%", "%total_page%"));
        this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.PREVIOUS_PAGE_ITEM, List.of("%current_page%", "%total_page%"));

        // Back has no placeholders
        // this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.BACK_ITEM, List.of());

        // Add tier has no placeholders
        // this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.ADD_TIER_ITEM, List.of("%tier_name%"));

        // Add stat has no placeholders
        // this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.ADD_WEAPON_TYPE_ITEM, List.of("%weapon_type%"));

        this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.ADD_STAT_TYPE_ITEM, List.of("%stat_type%"));
        this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.ADD_STAT_MIN_ITEM, List.of("%stat_min%"));
        this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.ADD_STAT_MAX_ITEM, List.of("%stat_max%"));

        this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.TIER_DISPLAY, List.of("%tier_name%"));
        this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.WEAPON_TYPE_DISPLAY, List.of("%weapon_type%"));
        this.placeholdersList.put(ConfigConstants.PLACEHOLDERS.STAT_DISPLAY, List.of("%stat_name%", "%stat_type%", "%stat_min%", "%stat_max%"));
    }

    public ItemStack getMenuItem(String name, String displayFor, @Nullable String... placeholders) {
        ItemFormat itemFormat = itemFormats.get(displayFor);

        ItemStack item = new ItemStack(Material.DIAMOND);

        ItemMeta meta = item.getItemMeta();
        switch (displayFor) {
            case ConfigConstants.PLACEHOLDERS.TIER_DISPLAY:
                meta.setDisplayName(itemFormat.getName().replace("%tier_name%", name));
                meta.setLore(itemFormat.getLore());
                break;
            case ConfigConstants.PLACEHOLDERS.WEAPON_TYPE_DISPLAY:
                meta.setDisplayName(itemFormat.getName().replace("%weapon_type%", name));
                meta.setLore(itemFormat.getLore());
                break;
            case ConfigConstants.PLACEHOLDERS.STAT_DISPLAY:
                meta.setDisplayName(itemFormat.getName().replace("%stat_name%", name));
                List<String> lore = itemFormat.getLore();
                lore.replaceAll(textToTranslate -> textToTranslate
                        .replace("%stat_type%", placeholders[0])
                        .replace("%stat_min%", placeholders[1])
                        .replace("%stat_max%", placeholders[2]));
                meta.setLore(lore);
                break;
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        return item;
    }

    @Getter @AllArgsConstructor
    public static class ItemFormat {
        String name;
        List<String> lore;
    }

    @AllArgsConstructor @Getter
    public static class Button {
        private final String itemNameFormat;
        private final List<String> itemLoreFormat;
        private final Material material;
    }
}
