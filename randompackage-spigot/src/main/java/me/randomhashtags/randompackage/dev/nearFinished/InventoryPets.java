package me.randomhashtags.randompackage.dev.nearFinished;

import me.randomhashtags.randompackage.attribute.GivePetExp;
import me.randomhashtags.randompackage.attribute.condition.ItemIsInventoryPet;
import me.randomhashtags.randompackage.dev.InventoryPet;
import me.randomhashtags.randompackage.util.EventAttributes;
import me.randomhashtags.randompackage.util.RPItemStack;
import me.randomhashtags.randompackage.util.addon.FileInventoryPet;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InventoryPets extends EventAttributes implements RPItemStack {
    private static InventoryPets instance;
    public static InventoryPets getInventoryPets() {
        if(instance == null) instance = new InventoryPets();
        return instance;
    }
    public YamlConfiguration config;

    public ItemStack leash, rarecandy;
    private String leashedLore;

    public String getIdentifier() { return "INVENTORY_PETS"; }
    protected RPFeature getFeature() { return getInventoryPets(); }
    public void load() {
        final long started = System.currentTimeMillis();
        new GivePetExp().load();
        new ItemIsInventoryPet().load();
        save(null, "inventory pets.yml");
        if(!otherdata.getBoolean("saved default inventory pets")) {
            final String[] p = new String[] {
                    "ALCHEMIST", "ANTI_TELEBLOCK",
                    "BANNER", "BLACKSCROLL",
                    "ENCHANTER",
                    "FEIGN_DEATH",
                    "GAIA",
                    "LAVA_ELEMENTAL",
                    "RAID_CREEPER",
                    "SMITE",
                    "STRONGHOLD_SELL",
                    "TESLA",
                    "VILE_CREEPER",
                    "WATER_ELEMENTAL",
                    "XP_BOOSTER",
            };
            for(String s : p) save("inventory pets", s + ".yml");
            otherdata.set("saved default inventory pets", true);
            saveOtherData();
        }
        final List<ItemStack> pets = new ArrayList<>();
        final File folder = new File(rpd + separator + "inventory pets");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                pets.add(new FileInventoryPet(f).getItem(1, 0));
            }
        }
        addGivedpCategory(pets, UMaterial.PLAYER_HEAD_ITEM, "Inventory Pets", "Givedp: Inventory Pets");

        config = YamlConfiguration.loadConfiguration(new File(rpd, "inventory pets.yml"));
        leash = d(config, "items.leash");
        leashedLore = ChatColor.translateAlternateColorCodes('&', config.getString("items.leash.applied lore"));
        rarecandy = d(config, "items.rare candy");

        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (inventorypets != null ? inventorypets.size() : 0) + " Inventory Pets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        inventorypets = null;
    }

    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
    }
    @EventHandler
    private void playerDeathEvent(PlayerDeathEvent event) {
    }
    @EventHandler
    private void playerRespawnEvent(PlayerRespawnEvent event) {
    }

    public boolean isInventoryPet(ItemStack is) { return getRPItemStackValue(is, "InventoryPetIdentifier") != null; }


    public boolean tryLeashing(ItemStack is) {
        if(inventorypets != null && is != null && !is.getType().equals(Material.AIR) && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final String id = getRPItemStackValue(is, "InventoryPetIdentifier");
            if(id != null) {
                itemMeta = is.getItemMeta();
                final List<String> l = new ArrayList<>(itemMeta.getLore());
                if(!l.contains(leashedLore)) {
                    l.add(leashedLore);
                    itemMeta.setLore(l);
                    is.setItemMeta(itemMeta);
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null) {
            final Player player = event.getPlayer();
            final String id = getRPItemStackValue(is, "InventoryPetInfo");
            if(id != null) {
                final String[] info = id.split(":");
                final int level = Integer.parseInt(info[1]), exp = Integer.parseInt(info[2]);
                final long expiration = Long.parseLong(info[3]), time = System.currentTimeMillis();
                final InventoryPet pet = getInventoryPet(info[0]);
                if(time >= expiration) {
                    pet.setItem(is, level, exp, time+pet.getCooldown(level));
                } else {
                }
                player.sendMessage("Is inventory pet! Identifier=" + id);
            } else if(is.isSimilar(leash) || is.isSimilar(rarecandy)) {
            } else return;
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}
