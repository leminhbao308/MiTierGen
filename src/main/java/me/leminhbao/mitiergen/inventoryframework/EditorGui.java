package me.leminhbao.mitiergen.inventoryframework;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import lombok.Setter;
import me.leminhbao.mitiergen.MiTierGen;
import me.leminhbao.mitiergen.config.ConfigConstants;
import me.leminhbao.mitiergen.config.MiMenuConfig;
import me.leminhbao.mitiergen.config.MiTierConfig;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorGui {
    private boolean isOpening = false;
    private static final int MAX_ITEMS_PER_PAGE = 21;
    private static final int LOC_TIER = 0;
    private static final int LOC_WEAPON_TYPE = 1;
    private static final int LOC_STAT = 2;
    private static final int LOC_STAT_MODIFIER = 3;

    public EditorGui(MiTierGen plugin) {
        this.plugin = plugin;
        this.menuConfig = this.plugin.getMiMenuConfig();
        this.tierConfig = this.plugin.getMiTierConfig();

        prepareData();

        setupActionButtons();
        setupGui();
    }

    private void setupGui() {
        this.gui = new ChestGui(menuConfig.getRows(), menuConfig.getTitle());
        this.paginatedZone = new PaginatedPane(Slot.fromXY(1, 1), 7, 3);

        for (int i = 0; i < tierEditorPanes.size(); i++) {
            paginatedZone.addPane(i, tierEditorPanes.get(i));
        }
        paginatedZone.setVisible(true);

        nextPageButton.setVisible(paginatedZone.getPage() < paginatedZone.getPages() - 1);
        previousPageButton.setVisible(paginatedZone.getPage() > 0);
        backButton.setVisible(currentLocation != LOC_TIER);

        StaticPane decorPane = new StaticPane(9, 6);
        decorPane.setPriority(Pane.Priority.LOWEST);
        // Add black glass panes to the decor pane, skip paginated and navigation zones
        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 6; row++) {
                if (col < 1 || col > 7 || row < 1 || row > 3) {
                    decorPane.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)), Slot.fromXY(col, row));
                }
            }
        }

        gui.addPane(decorPane);
        gui.addPane(paginatedZone);
        gui.addPane(navigationZone);
        gui.setOnGlobalDrag(event -> event.setCancelled(true));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
    }

    private void setupActionButtons() {
        MiMenuConfig.Button nextButton = menuConfig.getButtons().get(ConfigConstants.PLACEHOLDERS.NEXT_PAGE_ITEM);
        MiMenuConfig.Button previousButton = menuConfig.getButtons().get(ConfigConstants.PLACEHOLDERS.PREVIOUS_PAGE_ITEM);
        MiMenuConfig.Button backButton = menuConfig.getButtons().get(ConfigConstants.PLACEHOLDERS.BACK_ITEM);
        MiMenuConfig.Button addTierButton = menuConfig.getButtons().get(ConfigConstants.PLACEHOLDERS.ADD_TIER_ITEM);
        MiMenuConfig.Button addWeaponTypeButton = menuConfig.getButtons().get(ConfigConstants.PLACEHOLDERS.ADD_WEAPON_TYPE_ITEM);
        MiMenuConfig.Button addStatButton = menuConfig.getButtons().get(ConfigConstants.PLACEHOLDERS.ADD_STAT_ITEM);

        ItemStack nextButtonStack = new ItemStack(nextButton.getMaterial());
        ItemStack previousButtonStack = new ItemStack(previousButton.getMaterial());
        ItemStack backButtonStack = new ItemStack(backButton.getMaterial());
        ItemStack addTierButtonStack = new ItemStack(addTierButton.getMaterial());
        ItemStack addWeaponTypeButtonStack = new ItemStack(addWeaponTypeButton.getMaterial());
        ItemStack addStatButtonStack = new ItemStack(addStatButton.getMaterial());

        ItemMeta nextButtonMeta = nextButtonStack.getItemMeta();
        ItemMeta previousButtonMeta = previousButtonStack.getItemMeta();
        ItemMeta backButtonMeta = backButtonStack.getItemMeta();
        ItemMeta addTierButtonMeta = addTierButtonStack.getItemMeta();
        ItemMeta addWeaponTypeButtonMeta = addWeaponTypeButtonStack.getItemMeta();
        ItemMeta addStatButtonMeta = addStatButtonStack.getItemMeta();

        nextButtonMeta.setDisplayName(nextButton.getItemNameFormat());
        previousButtonMeta.setDisplayName(previousButton.getItemNameFormat());
        backButtonMeta.setDisplayName(backButton.getItemNameFormat());
        addTierButtonMeta.setDisplayName(addTierButton.getItemNameFormat());
        addWeaponTypeButtonMeta.setDisplayName(addWeaponTypeButton.getItemNameFormat());
        addStatButtonMeta.setDisplayName(addStatButton.getItemNameFormat());

        nextButtonMeta.setLore(nextButton.getItemLoreFormat());
        previousButtonMeta.setLore(previousButton.getItemLoreFormat());
        backButtonMeta.setLore(backButton.getItemLoreFormat());
        addTierButtonMeta.setLore(addTierButton.getItemLoreFormat());
        addWeaponTypeButtonMeta.setLore(addWeaponTypeButton.getItemLoreFormat());
        addStatButtonMeta.setLore(addStatButton.getItemLoreFormat());

        nextButtonStack.setItemMeta(nextButtonMeta);
        previousButtonStack.setItemMeta(previousButtonMeta);
        backButtonStack.setItemMeta(backButtonMeta);
        addTierButtonStack.setItemMeta(addTierButtonMeta);
        addWeaponTypeButtonStack.setItemMeta(addWeaponTypeButtonMeta);
        addStatButtonStack.setItemMeta(addStatButtonMeta);

        this.nextPageButton = new GuiItem(nextButtonStack);
        this.previousPageButton = new GuiItem(previousButtonStack);
        this.backButton = new GuiItem(backButtonStack);
        this.addTierButton = new GuiItem(addTierButtonStack);
        this.addWeaponTypeButton = new GuiItem(addWeaponTypeButtonStack);
        this.addStatButton = new GuiItem(addStatButtonStack);

        this.nextPageButton.setAction(event -> {
            if (currentLocation == LOC_TIER) {
                if (currentTierEditorPage < tierEditorPanes.size() - 1) {
                    currentTierEditorPage++;
                    paginatedZone.setPage(currentTierEditorPage);
                }
            } else if (currentLocation == LOC_WEAPON_TYPE) {
                if (currentWeaponTypeEditorPage < weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).size() - 1) {
                    currentWeaponTypeEditorPage++;
                    paginatedZone.setPage(currentWeaponTypeEditorPage);
                }
            } else if (currentLocation == LOC_STAT) {
                if (currentStatEditorPage < statEditorPanes.get(weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).get(currentWeaponTypeEditorPage)).size() - 1) {
                    currentStatEditorPage++;
                    paginatedZone.setPage(currentStatEditorPage);
                }
            } else if (currentLocation == LOC_STAT_MODIFIER) {
                paginatedZone.setPage(currentStatModifierEditorPage);
            }
            this.update();
        });

        this.previousPageButton.setAction(event -> {
            if (currentLocation == LOC_TIER) {
                if (currentTierEditorPage > 0) {
                    currentTierEditorPage--;
                    paginatedZone.setPage(currentTierEditorPage);

                    this.update();
                }
            } else if (currentLocation == LOC_WEAPON_TYPE) {
                if (currentWeaponTypeEditorPage > 0) {
                    currentWeaponTypeEditorPage--;
                    paginatedZone.setPage(currentWeaponTypeEditorPage);
                }
            } else if (currentLocation == LOC_STAT) {
                if (currentStatEditorPage > 0) {
                    currentStatEditorPage--;
                    paginatedZone.setPage(currentStatEditorPage);
                }
            } else if (currentLocation == LOC_STAT_MODIFIER) {
                if (currentStatModifierEditorPage > 0) {
                    currentStatModifierEditorPage--;
                    paginatedZone.setPage(currentStatModifierEditorPage);
                }
            }
            this.update();
        });

        this.backButton.setAction(event -> {
            if (currentLocation == LOC_WEAPON_TYPE) {
                currentLocation = LOC_TIER;
                currentWeaponTypeEditorPage = 0;
                paginatedZone = new PaginatedPane(Slot.fromXY(1, 1), 7, 3);
                for (int i = 0; i < tierEditorPanes.size(); i++) {
                    paginatedZone.addPane(i, tierEditorPanes.get(i));
                }
                paginatedZone.setPage(currentTierEditorPage);
            } else if (currentLocation == LOC_STAT) {
                currentLocation = LOC_WEAPON_TYPE;
                currentStatEditorPage = 0;
                paginatedZone = new PaginatedPane(Slot.fromXY(1, 1), 7, 3);
                for (int i = 0; i < weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).size(); i++) {
                    paginatedZone.addPane(i, weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).get(i));
                }
                paginatedZone.setPage(currentWeaponTypeEditorPage);
            } else if (currentLocation == LOC_STAT_MODIFIER) {
                currentLocation = LOC_STAT;
                currentStatModifierEditorPage = 0;
                paginatedZone = new PaginatedPane(Slot.fromXY(1, 1), 7, 3);
                for (int i = 0; i < statEditorPanes.get(weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).get(currentWeaponTypeEditorPage)).size(); i++) {
                    paginatedZone.addPane(i, statEditorPanes.get(weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).get(currentWeaponTypeEditorPage)).get(i));
                }
                paginatedZone.setPage(currentStatEditorPage);
            }
            this.update();
        });

        this.navigationZone = new StaticPane(Slot.fromXY(1, 5), 7, 1);
        navigationZone.addItem(this.nextPageButton, Slot.fromIndex(6));
        navigationZone.addItem(this.previousPageButton, Slot.fromIndex(0));
        navigationZone.addItem(this.backButton, Slot.fromIndex(1));
        navigationZone.addItem(this.addTierButton, Slot.fromIndex(3));
    }

    public void open(HumanEntity player) {
        if (isOpening) {
            return;
        }

        isOpening = true;
        gui.show(player);
    }

    public void update() {
        if (isOpening) {
            paginatedZone = new PaginatedPane(Slot.fromXY(1, 1), 7, 3);
            if (currentLocation == LOC_TIER) {
                this.backButton.setVisible(false);

                for (int i = 0; i < tierEditorPanes.size(); i++) {
                    paginatedZone.addPane(i, tierEditorPanes.get(i));
                }
                paginatedZone.setPage(currentTierEditorPage);

                navigationZone.addItem(addTierButton, Slot.fromIndex(3));
            } else if (currentLocation == LOC_WEAPON_TYPE) {
                this.backButton.setVisible(true);

                for (int i = 0; i < weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).size(); i++) {
                    paginatedZone.addPane(i, weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).get(i));
                }
                paginatedZone.setPage(currentWeaponTypeEditorPage);

                navigationZone.addItem(addWeaponTypeButton, Slot.fromIndex(3));
            } else if (currentLocation == LOC_STAT) {
                this.backButton.setVisible(true);

                for (int i = 0; i < statEditorPanes.get(weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).get(currentWeaponTypeEditorPage)).size(); i++) {
                    paginatedZone.addPane(i, statEditorPanes.get(weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).get(currentWeaponTypeEditorPage)).get(i));
                }
                paginatedZone.setPage(currentStatEditorPage);

                navigationZone.addItem(addStatButton, Slot.fromIndex(3));
            } else if (currentLocation == LOC_STAT_MODIFIER) {
                this.backButton.setVisible(true);

                paginatedZone.addPane(0, statModifierEditorPanes.get(statEditorPanes.get(weaponTypeEditorPanes.get(tierEditorPanes.get(currentTierEditorPage)).get(currentWeaponTypeEditorPage)).get(currentStatEditorPage)));
                paginatedZone.setPage(currentStatModifierEditorPage);

                navigationZone.removeItem(Slot.fromIndex(3));
            }

            plugin.getLogger().info("===================================");
            plugin.getLogger().info("Page: " + paginatedZone.getPage());
            plugin.getLogger().info("Pages: " + paginatedZone.getPages());
            plugin.getLogger().info("===================================");

            nextPageButton.setVisible(paginatedZone.getPage() < paginatedZone.getPages() - 1);
            previousPageButton.setVisible(paginatedZone.getPage() > 0);

            gui.update();
        }
    }

    private final MiTierGen plugin;
    private final MiMenuConfig menuConfig;
    private final MiTierConfig tierConfig;
    private MiTierConfig newDataConfig = null;

    private ChestGui gui;
    private PaginatedPane paginatedZone;
    private StaticPane navigationZone;
    private GuiItem nextPageButton;
    private GuiItem previousPageButton;
    private GuiItem backButton;

    private GuiItem addTierButton;
    private GuiItem addWeaponTypeButton;
    private GuiItem addStatButton;



    private @Getter @Setter int currentLocation = LOC_TIER;
    // Tier Editor (Each Tier Editor has its own Weapon Type Editor)
    private @Getter List<StaticPane> tierEditorPanes = new ArrayList<>();
    private @Getter int currentTierEditorPage = 0;

    // Weapon Type Editor (Each Tier Editor has its own Weapon Type Editor)
    private @Getter HashMap<StaticPane, List<StaticPane>> weaponTypeEditorPanes = new HashMap<>();
    private @Getter int currentWeaponTypeEditorPage = 0;

    // Stat Editor (Each Weapon Type Editor has its own Stat Editor)
    private @Getter HashMap<StaticPane, List<StaticPane>> statEditorPanes = new HashMap<>();
    private @Getter int currentStatEditorPage = 0;

    // Stat Modifier Editor (Each Stat Editor has its own Stat Modifier Editor)
    private @Getter HashMap<StaticPane,StaticPane> statModifierEditorPanes = new HashMap<>();
    private @Getter int currentStatModifierEditorPage = 0;

    private void prepareData() {
        MiMenuConfig.ItemFormat tierItemFormat = menuConfig.getItemFormats().get(ConfigConstants.PLACEHOLDERS.TIER_DISPLAY);
        MiMenuConfig.ItemFormat weaponTypeItemFormat = menuConfig.getItemFormats().get(ConfigConstants.PLACEHOLDERS.WEAPON_TYPE_DISPLAY);
        MiMenuConfig.ItemFormat statItemFormat = menuConfig.getItemFormats().get(ConfigConstants.PLACEHOLDERS.STAT_DISPLAY);

        // Tier Editor
        StaticPane tierEditorPane = new StaticPane(7, 3);
        int tierIndex = 0;
        for (String key : tierConfig.getTierList().keySet()) {
            if (tierIndex == MAX_ITEMS_PER_PAGE) {
                tierEditorPanes.add(tierEditorPane);
                tierEditorPane.clear();
                tierIndex = 0;
            }
            MiTierConfig.Tier tier = tierConfig.getTierList().get(key);


            // Weapon Type Editor
            StaticPane weaponTypeEditorPane = new StaticPane(7, 3);
            int weaponTypeIndex = 0;
            for (MiTierConfig.Item weaponType : tier.getAppliedItems().values()) {
                if (weaponTypeIndex == MAX_ITEMS_PER_PAGE) {
                    List<StaticPane> savedPanes = weaponTypeEditorPanes.get(tierEditorPane);
                    if (savedPanes == null) {
                        savedPanes = new ArrayList<>();
                    }
                    savedPanes.add(weaponTypeEditorPane);
                    weaponTypeEditorPanes.put(tierEditorPane, savedPanes);
                    weaponTypeEditorPane.clear();
                    weaponTypeIndex = 0;
                }

                // Stat Editor
                StaticPane statEditorPane = new StaticPane(7, 3);
                int statIndex = 0;
                for (Map.Entry<ItemStat, MiTierConfig.Data> stat : weaponType.getAppliedStats().entrySet()) {
                    if (statIndex == MAX_ITEMS_PER_PAGE) {
                        List<StaticPane> savedPanes = statEditorPanes.get(weaponTypeEditorPane);
                        if (savedPanes == null) {
                            savedPanes = new ArrayList<>();
                        }
                        savedPanes.add(statEditorPane);
                        statEditorPanes.put(weaponTypeEditorPane, savedPanes);
                        statEditorPane.clear();
                        statIndex = 0;
                    }
                    MiTierConfig.Data statData = stat.getValue();

                    // Stat Modifier Editor
                    StaticPane statModifierEditorPane = new StaticPane(7, 3);

                    MiMenuConfig.Button addStatTypeButton = menuConfig.getButtons().get(ConfigConstants.PLACEHOLDERS.ADD_STAT_TYPE_ITEM);
                    MiMenuConfig.Button addStatMinButton = menuConfig.getButtons().get(ConfigConstants.PLACEHOLDERS.ADD_STAT_MIN_ITEM);
                    MiMenuConfig.Button addStatMaxButton = menuConfig.getButtons().get(ConfigConstants.PLACEHOLDERS.ADD_STAT_MAX_ITEM);

                    ItemStack addStatTypeButtonStack = new ItemStack(addStatTypeButton.getMaterial());
                    ItemStack addStatMinButtonStack = new ItemStack(addStatMinButton.getMaterial());
                    ItemStack addStatMaxButtonStack = new ItemStack(addStatMaxButton.getMaterial());

                    ItemMeta addStatTypeButtonMeta = addStatTypeButtonStack.getItemMeta();
                    ItemMeta addStatMinButtonMeta = addStatMinButtonStack.getItemMeta();
                    ItemMeta addStatMaxButtonMeta = addStatMaxButtonStack.getItemMeta();

                    addStatTypeButtonMeta.setDisplayName(addStatTypeButton.getItemNameFormat());
                    addStatMinButtonMeta.setDisplayName(addStatMinButton.getItemNameFormat());
                    addStatMaxButtonMeta.setDisplayName(addStatMaxButton.getItemNameFormat());

                    addStatMaxButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    addStatMinButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    addStatTypeButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                    List<String> addStatTypeButtonLore = addStatTypeButton.getItemLoreFormat();
                    List<String> addStatMinButtonLore = addStatMinButton.getItemLoreFormat();
                    List<String> addStatMaxButtonLore = addStatMaxButton.getItemLoreFormat();

                    addStatTypeButtonLore.replaceAll(
                            stringToReplace -> stringToReplace.replace("%stat_type%", statData.getType())
                    );
                    addStatMinButtonLore.replaceAll(
                            stringToReplace -> stringToReplace.replace("%stat_min%", statData.getMinPercent() + (statData.getType().equals(ConfigConstants.DATA_TYPE_PERCENT) ? "%" : ""))
                    );
                    addStatMaxButtonLore.replaceAll(
                            stringToReplace -> stringToReplace.replace("%stat_max%", statData.getMaxPercent() + (statData.getType().equals(ConfigConstants.DATA_TYPE_PERCENT) ? "%" : ""))
                    );

                    addStatTypeButtonMeta.setLore(addStatTypeButtonLore);
                    addStatMinButtonMeta.setLore(addStatMinButtonLore);
                    addStatMaxButtonMeta.setLore(addStatMaxButtonLore);

                    addStatTypeButtonStack.setItemMeta(addStatTypeButtonMeta);
                    addStatMinButtonStack.setItemMeta(addStatMinButtonMeta);
                    addStatMaxButtonStack.setItemMeta(addStatMaxButtonMeta);

                    GuiItem addStatTypeGuiItem = new GuiItem(addStatTypeButtonStack,
                        event -> {

                    });
                    GuiItem addStatMinGuiItem = new GuiItem(addStatMinButtonStack,
                        event -> {

                    });
                    GuiItem addStatMaxGuiItem = new GuiItem(addStatMaxButtonStack,
                        event -> {

                    });

                    statModifierEditorPane.addItem(addStatTypeGuiItem, Slot.fromIndex(8));
                    statModifierEditorPane.addItem(addStatMinGuiItem, Slot.fromIndex(10));
                    statModifierEditorPane.addItem(addStatMaxGuiItem, Slot.fromIndex(12));

                    statModifierEditorPanes.put(statEditorPane, statModifierEditorPane);
                    // End of Stat Modifier Editor

                    List<String> lores = statItemFormat.getLore();
                    lores.replaceAll(stringToReplace -> stringToReplace
                        .replace("%stat_type%", statData.getType())
                        .replace("%stat_min%", statData.getMinPercent() + (statData.getType().equals(ConfigConstants.DATA_TYPE_PERCENT) ? "%" : ""))
                        .replace("%stat_max%", statData.getMaxPercent() + (statData.getType().equals(ConfigConstants.DATA_TYPE_PERCENT) ? "%" : "")));
                    ItemStack statItemStack = new ItemStack(MMOItems.plugin.getStats().get(stat.getKey().getId()).getDisplayMaterial());
                    ItemMeta statItemMeta = statItemStack.getItemMeta();
                    statItemMeta.setDisplayName(statItemFormat.getName().replace("%stat_name%", stat.getKey().getId()));
                    statItemMeta.setLore(lores);
                    statItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    statItemStack.setItemMeta(statItemMeta);
                    GuiItem statGuiItem = new GuiItem(statItemStack,
                        event -> {
                            // Left click to edit
                            if (event.getClick().isLeftClick()) {
                                currentLocation = LOC_STAT_MODIFIER;
                                currentStatEditorPage = 0;

                                paginatedZone = new PaginatedPane(Slot.fromXY(1, 1), 7, 3);
                                paginatedZone.addPane(0, statModifierEditorPanes.get(statEditorPane));
                                paginatedZone.setPage(currentStatEditorPage);

                                this.update();
                                event.getWhoClicked().sendMessage("Stat " + stat.getKey().getId() + " has been selected.");
                            }

                            // Right click to delete
                            if (event.getClick().isRightClick()) {
//                                    if (newDataConfig != null) {
//                                        newDataConfig.getTierList().get(key).getAppliedItems().get(weaponType).getAppliedStats().remove(stat);
//                                    } else {
//                                        newDataConfig = tierConfig;
//                                        newDataConfig.getTierList().get(key).getAppliedItems().get(weaponType).getAppliedStats().remove(stat);
//                                    }
                                event.getWhoClicked().sendMessage("Stat " + stat.getKey().getId() + " has been deleted.");
                            }
                        });

                    statEditorPane.addItem(statGuiItem, Slot.fromIndex(statIndex));
                    statIndex++;
                    plugin.getLogger().info("Stat " + stat.getKey().getId() + " of " + weaponType.getItemType() + " in tier " + key + " has been added.");
                    plugin.getLogger().info("Current Stat added: " + statIndex);
                    plugin.getLogger().info("Max: " + statData.getMaxPercent());
                }
                if (statIndex > 0) {
                    List<StaticPane> savedPanes = statEditorPanes.get(weaponTypeEditorPane);
                    if (savedPanes == null) {
                        savedPanes = new ArrayList<>();
                    }
                    savedPanes.add(statEditorPane);
                    statEditorPanes.put(weaponTypeEditorPane, savedPanes);
                }

                ItemStack weaponTypeItemStack = new ItemStack(MMOItems.plugin.getTypes().get(weaponType.getItemType()).getItem().getType());
                ItemMeta weaponTypeItemMeta = weaponTypeItemStack.getItemMeta();
                weaponTypeItemMeta.setDisplayName(weaponTypeItemFormat.getName().replace("%weapon_type%", weaponType.getItemType()));
                weaponTypeItemMeta.setLore(weaponTypeItemFormat.getLore());
                weaponTypeItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                weaponTypeItemStack.setItemMeta(weaponTypeItemMeta);
                GuiItem weaponTypeGuiItem = new GuiItem(weaponTypeItemStack,
                    event -> {
                        // Left click to edit
                        if (event.getClick().isLeftClick()) {
                            currentLocation = LOC_STAT;
                            currentTierEditorPage = paginatedZone.getPage();
                            currentStatEditorPage = 0;

                            paginatedZone = new PaginatedPane(Slot.fromXY(1, 1), 7, 3);
                            for (int i = 0; i < statEditorPanes.get(weaponTypeEditorPane).size(); i++) {
                                paginatedZone.addPane(i, statEditorPanes.get(weaponTypeEditorPane).get(i));
                            }
                            paginatedZone.setPage(currentStatEditorPage);

                            this.update();
                            event.getWhoClicked().sendMessage("Weapon Type " + weaponType.getItemType() + " has been selected.");
                        }

                        // Right click to delete
                        if (event.getClick().isRightClick()) {
//                                if (newDataConfig != null) {
//                                    newDataConfig.getTierList().get(key).getAppliedItems().remove(weaponType);
//                                } else {
//                                    newDataConfig = tierConfig;
//                                    newDataConfig.getTierList().get(key).getAppliedItems().remove(weaponType);
//                                }
                            event.getWhoClicked().sendMessage("Weapon Type " + weaponType.getItemType() + " has been deleted.");
                        }
                    });

                weaponTypeEditorPane.addItem(weaponTypeGuiItem, Slot.fromIndex(weaponTypeIndex));
                weaponTypeIndex++;
            }
            if (weaponTypeIndex > 0) {
                List<StaticPane> savedPanes = weaponTypeEditorPanes.get(tierEditorPane);
                if (savedPanes == null) {
                    savedPanes = new ArrayList<>();
                }
                savedPanes.add(weaponTypeEditorPane);
                weaponTypeEditorPanes.put(tierEditorPane, savedPanes);
            }

            ItemStack itemStack = new ItemStack(Material.DIAMOND);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(tierItemFormat.getName().replace("%tier_name%", key));
            itemMeta.setLore(tierItemFormat.getLore());
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(itemMeta);
            GuiItem guiItem = new GuiItem(itemStack,
                event -> {
                    // Left click to edit
                    if (event.getClick().isLeftClick()) {
                        currentLocation = LOC_WEAPON_TYPE;
                        currentTierEditorPage = paginatedZone.getPage();
                        currentWeaponTypeEditorPage = 0;

                        paginatedZone = new PaginatedPane(Slot.fromXY(1, 1), 7, 3);
                        for (int i = 0; i < weaponTypeEditorPanes.get(tierEditorPane).size(); i++) {
                            paginatedZone.addPane(i, weaponTypeEditorPanes.get(tierEditorPane).get(i));
                        }
                        paginatedZone.setPage(currentWeaponTypeEditorPage);

                        this.update();
                        event.getWhoClicked().sendMessage("Tier " + key + " has been selected.");
                    }

                    // Right click to delete
                    if (event.getClick().isRightClick()) {
//                            if (newDataConfig != null) {
//                                newDataConfig.getTierList().remove(key);
//                            } else {
//                                newDataConfig = tierConfig;
//                                newDataConfig.getTierList().remove(key);
//                            }
                        event.getWhoClicked().sendMessage("Tier " + key + " has been deleted.");
                    }
                });

            tierEditorPane.addItem(guiItem, Slot.fromIndex(tierIndex));
            tierIndex++;
        }
        if (tierIndex > 0) {
            tierEditorPanes.add(tierEditorPane);
        }
    }
}
