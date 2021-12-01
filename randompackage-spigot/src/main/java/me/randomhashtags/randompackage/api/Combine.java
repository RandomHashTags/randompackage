package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.perms.CombinePermission;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Combine extends RPFeatureSpigot implements CommandExecutor {
    public static final Combine INSTANCE = new Combine();

    public YamlConfiguration config;
    private List<String> combineores;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && hasPermission(sender, CombinePermission.COMMAND, true)) {
            combine((Player) sender);
        }
        return true;
    }

    @Override
    public String getIdentifier() {
        return "COMBINE";
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "combine.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "combine.yml"));
        combineores = new ArrayList<>();
        for(String string : config.getStringList("combine ores")) {
            combineores.add(string.toUpperCase());
        }
        sendConsoleDidLoadFeature("Combine", started);
    }
    public void unload() {
    }

    public void combine(@NotNull Player player) {
        final Block target = player.getTargetBlock(null, 5);
        final Material targetType = target.getType();
        final boolean isChest = targetType.equals(Material.CHEST) || targetType.equals(Material.TRAPPED_CHEST);
        final Chest chest = isChest ? (Chest) target.getState() : null;
        final Inventory inventory = isChest ? chest.getBlockInventory() : player.getInventory();
        final String format = getString(config, "messages.format");
        final int size = combineores.size();
        for(String string : getStringList(config, "messages.success")) {
            if(string.equals("{SUCCESS}")) {
                for(int i = 0; i < size; i++) {
                    final Material material = Material.valueOf(combineores.get(i).toUpperCase());
                    final String name = material.name();
                    final Material block = !name.replace("INGOT", "BLOCK").endsWith("BLOCK") ? Material.valueOf(name + "_BLOCK") : Material.valueOf(name.replace("INGOT", "BLOCK"));
                    final String blockName = block.name();
                    final UMaterial umaterial = UMaterial.match(name);
                    final int amount = (getTotalAmount(inventory, umaterial) / 9) * 9;
                    if(amount != 0) {
                        final int blockAmount = amount/9;
                        player.sendMessage(format.replace("{AMOUNT_ITEM}", "" + amount).replace("{ITEM_ORE}", name).replace("{AMOUNT_BLOCK}", "" + blockAmount).replace("{ITEM_BLOCK}", blockName));
                        for(int z = 1; z <= amount; z++) {
                            inventory.removeItem(new ItemStack(material, 1, (byte) 0));
                        }
                        inventory.addItem(new ItemStack(block, blockAmount));
                        if(chest != null) {
                            chest.update();
                        } else {
                            player.updateInventory();
                        }
                    }
                }
            } else {
                player.sendMessage(string);
            }
        }
    }
}
