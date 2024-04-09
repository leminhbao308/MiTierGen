package me.leminhbao.mitiergen.commands.commandList;

import io.lumine.mythic.lib.api.item.NBTItem;
import lombok.AllArgsConstructor;
import me.leminhbao.mitiergen.MiTierGen;
import me.leminhbao.mitiergen.config.MiTierConfig;
import me.leminhbao.mitiergen.utils.RandomStatGenerator;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@AllArgsConstructor
public class CommandIdentify {

    private final MiTierGen plugin;

    public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only for players.");
            return;
        }

        if (!sender.hasPermission("mtg.identify") || !sender.hasPermission("mtg.admin")) {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
            return;
        }

        Player player = (Player) sender;
        NBTItem item = NBTItem.get(player.getInventory().getItemInMainHand());

        String tag = item.getString("MMOITEMS_UNIDENTIFIED_ITEM");
        if (tag.isEmpty()) {
            sender.sendMessage(plugin.getPrefix() + "The item you are holding is already identified.");
            return;
        }

        final int amount = player.getInventory().getItemInMainHand().getAmount();

        ItemStack identifiedItem = this.deserialize(tag);

        identifiedItem.setAmount(amount);

        player.getInventory().setItemInMainHand(identifiedItem);
        sender.sendMessage(plugin.getPrefix() + "Successfully identified the item you are holding.");
    }

    private ItemStack deserialize(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack stack = (ItemStack) dataInput.readObject();
            dataInput.close();
            NBTItem toRebuild = NBTItem.get(stack);
            if (toRebuild.hasType()) {
                LiveMMOItem rebuilt = new LiveMMOItem(stack);

                if (rebuilt.getTier() == null) {
                    return rebuilt.newBuilder().build();
                }

                if (!plugin.getMiTierConfig().hasTier(rebuilt.getTier().getId())) {
                    plugin.getLogger().warning("The item you are holding does not have a tier. Skipping...");
                }

                MiTierConfig.Tier tier = plugin.getMiTierConfig().getTierList().get(rebuilt.getTier().getId());

                if (tier == null) {
                    return rebuilt.newBuilder().build();
                }

                if (!tier.getAppliedItems().isEmpty() && !plugin.getMiTierConfig().hasItem(rebuilt.getTier().getId(), rebuilt.getType().getId())) { // Error here
                    plugin.getLogger().warning("The item you are holding does not have the correct item type. Skipping...");
                }

                MiTierConfig.Item item = plugin.getMiTierConfig().getItem(rebuilt.getTier().getId(), rebuilt.getType().getId());

                item.getAppliedStats().forEach((stat, sData) -> {
                    if (rebuilt.getStats().contains(stat)) {
                        if (stat instanceof DoubleStat) {
                            RandomStatGenerator rsg = new RandomStatGenerator(item.getAppliedStats().get(stat).getMinPercent(), item.getAppliedStats().get(stat).getMaxPercent());
                            DoubleData dData = rsg.randomize(rebuilt.getData(stat), item.getAppliedStats().get(stat).getType());
                            rebuilt.setData(stat, dData);

                            plugin.getLogger().info("Stat " + stat.getId() + " of " + rebuilt.getType().getId() + " in tier " + rebuilt.getTier().getId() + " has been changed to " + sData);
                        }
                    } else {
                        // make sure this stat can be applied to the item
                        if (stat.isCompatible(rebuilt.getType())) {
                            RandomStatGenerator rsg = new RandomStatGenerator(item.getAppliedStats().get(stat).getMinPercent(), item.getAppliedStats().get(stat).getMaxPercent());
                            StatData statData = rebuilt.getData(stat) != null ? rebuilt.getData(stat) : new DoubleData(plugin.getMiTierConfig().getDefaultIfNotSet());
                            DoubleData dData = rsg.randomize(statData, item.getAppliedStats().get(stat).getType());
                            rebuilt.setData(stat, dData);

                            plugin.getLogger().info("Stat " + stat.getId() + " of " + rebuilt.getType().getId() + " in tier " + rebuilt.getTier().getId() + " has been added with value " + dData.getValue());
                        } else {
                            plugin.getLogger().warning("Stat " + stat.getId() + " of " + rebuilt.getType().getId() + " in tier " + rebuilt.getTier().getId() + " cannot be applied to this item. Skipping...");
                        }
                    }
                });

                return rebuilt.newBuilder().build();
            } else {
                return stack;
            }
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
