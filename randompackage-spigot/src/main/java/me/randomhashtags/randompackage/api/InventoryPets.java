package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.InventoryPet;
import me.randomhashtags.randompackage.attribute.GivePetExp;
import me.randomhashtags.randompackage.event.DamageEvent;
import me.randomhashtags.randompackage.event.customenchant.PvAnyEvent;
import me.randomhashtags.randompackage.event.customenchant.isDamagedEvent;
import me.randomhashtags.randompackage.util.EventAttributes;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPItemStack;
import me.randomhashtags.randompackage.util.addon.FileInventoryPet;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InventoryPets extends EventAttributes implements RPItemStack {
    private static InventoryPets instance;
    public static InventoryPets getInventoryPets() {
        if(instance == null) instance = new InventoryPets();
        return instance;
    }
    public YamlConfiguration config;

    public ItemStack leash, rarecandy;
    private String leashedLore;

    private HashMap<UUID, List<ItemStack>> leashedUponDeath;

    public String getIdentifier() { return "INVENTORY_PETS"; }
    protected RPFeature getFeature() { return getInventoryPets(); }
    public void load() {
        final long started = System.currentTimeMillis();
        new GivePetExp().load();
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
                final InventoryPet p = new FileInventoryPet(f);
                if(p.isEnabled()) {
                    pets.add(p.getItem(1, 0));
                }
            }
        }

        config = YamlConfiguration.loadConfiguration(new File(rpd, "inventory pets.yml"));
        leash = d(config, "items.leash");
        leashedLore = ChatColor.translateAlternateColorCodes('&', config.getString("items.leash.applied lore"));
        rarecandy = d(config, "items.rare candy");
        leashedUponDeath = new HashMap<>();

        pets.add(leash);
        pets.add(rarecandy);
        addGivedpCategory(pets, UMaterial.PLAYER_HEAD_ITEM, "Inventory Pets", "Givedp: Inventory Pets");

        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (inventorypets != null ? inventorypets.size() : 0) + " Inventory Pets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(UUID u : leashedUponDeath.keySet()) {
        }
        inventorypets = null;
    }

    public HashMap<InventoryPet, String> isInventoryPet(ItemStack is) {
        final String info = getRPItemStackValue(is, "InventoryPetInfo");
        final boolean isPet = info != null;
        final HashMap<InventoryPet, String> pet = new HashMap<>();
        if(isPet) pet.put(getInventoryPet(info.split(":")[0]), info);
        return isPet ? pet : null;
    }

    public List<HashMap<ItemStack, HashMap<InventoryPet, String>>> getPets(Player player) {
        final List<HashMap<ItemStack, HashMap<InventoryPet, String>>> a = new ArrayList<>();
        final List<ItemStack> skull = new ArrayList<>();
        for(ItemStack is : player.getInventory()) {
            if(is != null) {
                final String n = is.getType().name();
                if(n.contains("SKULL") || n.contains("HEAD")) {
                    skull.add(is);
                }
            }
        }
        for(ItemStack is : skull) {
            final HashMap<InventoryPet, String> i = isInventoryPet(is);
            if(i != null) {
                final HashMap<ItemStack, HashMap<InventoryPet, String>> p = new HashMap<>();
                p.put(is, i);
                a.add(p);
            }
        }
        return a;
    }
    public List<ItemStack> getLeashed(Player player) {
        final List<ItemStack> l = new ArrayList<>();
        for(HashMap<ItemStack, HashMap<InventoryPet, String>> pet : getPets(player)) {
            for(ItemStack is : pet.keySet()) {
                if(isLeashed(is)) {
                    l.add(is);
                }
            }
        }
        return l;
    }
    public boolean isLeashed(ItemStack is) {
        return is != null && is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().contains(leashedLore);
    }
    public boolean tryLeashing(ItemStack is) {
        if(inventorypets != null && !isLeashed(is) && getRPItemStackValue(is, "InventoryPetInfo") != null) {
            itemMeta = is.getItemMeta();
            final List<String> l = new ArrayList<>(itemMeta.getLore());
            l.add(leashedLore);
            itemMeta.setLore(l);
            is.setItemMeta(itemMeta);
            return true;
        }
        return false;
    }
    public boolean tryUsingRareCandy(ItemStack is) {
        if(inventorypets != null && is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final String i = getRPItemStackValue(is, "InventoryPetInfo");
            if(i != null) {
                final String[] info = i.split(":");
                final InventoryPet pet = getInventoryPet(info[0]);
                final int level = Integer.parseInt(info[1]);
                if(level+1 <= pet.getMaxLevel()) {
                    final ItemStack n = pet.getItem(level+1, 0);
                    is.setItemMeta(n.getItemMeta());
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerDeathEvent(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final List<ItemStack> leashed = getLeashed(player);
        if(!leashed.isEmpty()) {
            final List<ItemStack> drops = event.getDrops();
            final UUID u = player.getUniqueId();
            if(!leashedUponDeath.containsKey(u)) leashedUponDeath.put(u, new ArrayList<>());
            for(ItemStack is : leashed) {
                drops.remove(is);
                leashedUponDeath.get(u).add(is);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerRespawnEvent(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final UUID u = player.getUniqueId();
        if(leashedUponDeath.containsKey(u)) {
            for(ItemStack is : leashedUponDeath.get(u)) {
                itemMeta = is.getItemMeta();
                lore = itemMeta.getLore();
                lore.remove(leashedLore);
                itemMeta.setLore(lore);
                is.setItemMeta(itemMeta);
                giveItem(player, is);
            }
            lore.clear();
            leashedUponDeath.remove(u);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack cur = event.getCurrentItem(), curs = event.getCursor();
        if(cur != null && curs != null && (curs.isSimilar(leash) && tryLeashing(cur) || curs.isSimilar(rarecandy) && tryUsingRareCandy(cur))) {
            didApply(event, (Player) event.getWhoClicked(), cur, curs);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null) {
            final Player player = event.getPlayer();
            final String id = getRPItemStackValue(is, "InventoryPetInfo");
            if(id != null) {
                final String[] info = id.split(":");
                final String identifier = info[0];
                final InventoryPet pet = getInventoryPet(identifier);
                final String lvl = info[1];
                final int level = Integer.parseInt(lvl), exp = Integer.parseInt(info[2]);
                final long expiration = Long.parseLong(info[3]), time = System.currentTimeMillis(), remainingtime = expiration-time;

                if(remainingtime <= 0) {
                    trigger(event, pet.getAttributes(), "level", lvl);
                    pet.didUse(is, identifier, level, exp);
                } else {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{TIME}", getRemainingTime(remainingtime));
                    sendStringListMessage(player, config.getStringList("messages.on cooldown"), replacements);
                }
            } else if(is.isSimilar(leash) || is.isSimilar(rarecandy)) {
            } else return;
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void isDamagedEvent(isDamagedEvent event) {
        tryTriggering(event, event.getEntity());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void pvAnyEvent(PvAnyEvent event) {
        tryTriggering(event, event.getDamager());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        tryTriggering(event, event.getEntity().getKiller());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        tryTriggering(event, event.getPlayer());
    }

    private void tryTriggering(DamageEvent event, Player player) {
        for(HashMap<ItemStack, HashMap<InventoryPet, String>> pets : getPets(player)) {
            for(ItemStack is : pets.keySet()) {
                final HashMap<InventoryPet, String> a = pets.get(is);
                for(InventoryPet pet : a.keySet()) {
                    final String[] info = pets.get(is).get(pet).split(":");
                    trigger(event, pet.getAttributes(), "level", info[1]);
                }
            }
        }
    }
    private void tryTriggering(EntityDeathEvent event, Player player) {
        if(player != null) {
            for(HashMap<ItemStack, HashMap<InventoryPet, String>> pets : getPets(player)) {
                for(ItemStack is : pets.keySet()) {
                    final HashMap<InventoryPet, String> a = pets.get(is);
                    for(InventoryPet pet : a.keySet()) {
                        final String[] info = pets.get(is).get(pet).split(":");
                        trigger(event, pet.getAttributes(), "level", info[1]);
                    }
                }
            }
        }
    }
    private void tryTriggering(PlayerCommandPreprocessEvent event, Player player) {
        if(player != null) {
            for(HashMap<ItemStack, HashMap<InventoryPet, String>> pets : getPets(player)) {
                for(ItemStack is : pets.keySet()) {
                    final HashMap<InventoryPet, String> a = pets.get(is);
                    for(InventoryPet pet : a.keySet()) {
                        final String[] info = pets.get(is).get(pet).split(":");
                        trigger(event, pet.getAttributes(), "level", info[1]);
                    }
                }
            }
        }
    }
}
