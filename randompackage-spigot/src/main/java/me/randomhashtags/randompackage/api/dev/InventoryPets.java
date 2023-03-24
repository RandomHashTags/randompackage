package me.randomhashtags.randompackage.api.dev;

import me.randomhashtags.randompackage.addon.InventoryPet;
import me.randomhashtags.randompackage.addon.file.FileInventoryPet;
import me.randomhashtags.randompackage.attribute.GivePetExp;
import me.randomhashtags.randompackage.attributesys.EventAttributeListener;
import me.randomhashtags.randompackage.attributesys.EventExecutor;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.PvAnyEvent;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.Packeter;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.RPItemStack;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum InventoryPets implements RPFeatureSpigot, EventExecutor, EventAttributeListener, RPItemStack, Packeter {
    INSTANCE;

    public YamlConfiguration config;
    public ItemStack leash, rarecandy;
    public static List<String> INVENTORY_PET_GLOBAL_ATTRIBUTES;
    private String leashedLore, expCharacter, expAchievedColor, expUnachievedColor;
    private int expCharacterLength;

    private HashMap<UUID, List<ItemStack>> leashedUponDeath;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.INVENTORY_PET;
    }

    @Override
    public void load() {
        new GivePetExp().load();
        save("inventory pets", "_settings.yml");
        if(!OTHER_YML.getBoolean("saved default inventory pets")) {
            generateDefaultInventoryPets();
            OTHER_YML.set("saved default inventory pets", true);
            saveOtherData();
        }
        final String folder = DATA_FOLDER + SEPARATOR + "inventory pets";

        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));
        leash = createItemStack(config, "items.leash");
        leashedLore = colorize(config.getString("items.leash.applied lore"));
        rarecandy = createItemStack(config, "items.rare candy");
        GivedpItem.INSTANCE.items.put("rarecandy", rarecandy);
        GivedpItem.INSTANCE.items.put("petleash", leash);

        leashedUponDeath = new HashMap<>();
        expCharacter = config.getString("settings.exp.character");
        expCharacterLength = config.getInt("settings.exp.character length");
        expAchievedColor = colorize(config.getString("settings.exp.achieved color"));
        expUnachievedColor = colorize(config.getString("settings.exp.unachieved color"));

        INVENTORY_PET_GLOBAL_ATTRIBUTES = config.getStringList("global attributes");

        final List<ItemStack> pets = new ArrayList<>();
        pets.add(leash);
        pets.add(rarecandy);
        for(File f : getFilesInFolder(folder)) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final InventoryPet p = new FileInventoryPet(f);
                pets.add(p.getItem(1, 0, 0));
            }
        }
        addGivedpCategory(pets, UMaterial.PLAYER_HEAD_ITEM, "Inventory Pets", "Givedp: Inventory Pets");
    }
    @Override
    public void unload() {
        for(UUID u : leashedUponDeath.keySet()) {
        }
    }

    public boolean is_pet(@NotNull ItemStack itemstack) {
        return getRPItemStackValue(itemstack, "InventoryPetInfo") != null;
    }

    public String getExpRegex(int currentXp, int maxXp) {
        final StringBuilder builder = new StringBuilder();
        final int percent = currentXp >= maxXp ? 100 : (int) ((((double) currentXp)/((double) maxXp))*100);
        for(int i = 1; i <= expCharacterLength; i++) {
            final String color = percent >= i ? expAchievedColor : expUnachievedColor;
            builder.append(color).append(expCharacter);
        }
        return builder.toString();
    }
    @Nullable
    public HashMap<InventoryPet, String> isInventoryPet(@NotNull ItemStack is) {
        final String info = getRPItemStackValue(is, "InventoryPetInfo");
        final boolean isPet = info != null;
        final HashMap<InventoryPet, String> pet = new HashMap<>();
        if(isPet) {
            pet.put(getInventoryPet(info.split(":")[0]), info);
        }
        return isPet ? pet : null;
    }
    public List<HashMap<ItemStack, HashMap<InventoryPet, String>>> getPets(@NotNull Player player) {
        final List<HashMap<ItemStack, HashMap<InventoryPet, String>>> pets = new ArrayList<>();
        for(ItemStack is : player.getInventory()) {
            if(is != null) {
                final String material = is.getType().name();
                if(material.contains("SKULL") || material.contains("HEAD")) {
                    final HashMap<InventoryPet, String> pet = isInventoryPet(is);
                    if(pet != null) {
                        final HashMap<ItemStack, HashMap<InventoryPet, String>> map = new HashMap<>();
                        map.put(is, pet);
                        pets.add(map);
                    }
                }
            }
        }
        return pets;
    }
    @NotNull
    public List<ItemStack> getLeashed(@NotNull Player player) {
        final List<ItemStack> list = new ArrayList<>();
        for(HashMap<ItemStack, HashMap<InventoryPet, String>> pet : getPets(player)) {
            for(ItemStack is : pet.keySet()) {
                if(isLeashed(is)) {
                    list.add(is);
                }
            }
        }
        return list;
    }
    public boolean isLeashed(@NotNull ItemStack is) {
        return is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().contains(leashedLore);
    }
    public boolean tryLeashing(@NotNull ItemStack is) {
        if(!isLeashed(is) && is_pet(is)) {
            final ItemMeta itemMeta = is.getItemMeta();
            final List<String> lore = new ArrayList<>(itemMeta.getLore());
            lore.add(leashedLore);
            itemMeta.setLore(lore);
            is.setItemMeta(itemMeta);
            return true;
        }
        return false;
    }
    public boolean tryUsingRareCandy(@NotNull ItemStack is) {
        if(is.hasItemMeta() && is.getItemMeta().hasLore()) {
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
    private byte didTriggerPet(Event event, @NotNull ItemStack is, @NotNull Player player) {
        final String info = getRPItemStackValue(is, "InventoryPetInfo");
        if(info != null) {
            final String[] infoValues = info.split(":");
            final String identifier = infoValues[0];
            final InventoryPet pet = getInventoryPet(identifier);
            if(pet != null) {
                final String lvl = infoValues[1];
                final int level = Integer.parseInt(lvl), exp = Integer.parseInt(infoValues[2]);
                final long expiration = Long.parseLong(infoValues[3]), time = System.currentTimeMillis(), remainingtime = expiration-time;

                if(remainingtime <= 0) {
                    final String targetValue = pet.getValue(level);
                    final String[] petValues = targetValue != null ?  targetValue.split(";") : null;
                    final List<String> replacedAttributes = new ArrayList<>();
                    for(String s : pet.getAttributes()) {
                        if(petValues != null) {
                            int number = 1;
                            for(String value : petValues) {
                                if(number == 1) {
                                    s = s.replace("{VALUE}", value);
                                }
                                s = s.replace("{VALUE_" + number + "}", value);
                                number++;
                            }
                        }
                        replacedAttributes.add(s.replace("{LEVEL}", lvl));
                    }

                    if(trigger(event, replacedAttributes)) {
                        pet.didUse(is, identifier, level, exp);
                        final int cooldown_ticks = 20 * ((int) pet.getCooldown(level));
                        sendItemCooldownPacket(player, is.getType(), cooldown_ticks);
                        return 1;
                    }
                } else {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{TIME}", getRemainingTime(remainingtime));
                    sendStringListMessage(player, getStringList(config, "messages.on cooldown"), replacements);
                    return 0;
                }
            }
        }
        return -1;
    }

    public void called(@NotNull Event event) {
        trigger(event, INVENTORY_PET_GLOBAL_ATTRIBUTES, getReplacements(event));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerDeathEvent(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final List<ItemStack> leashed = getLeashed(player);
        if(!leashed.isEmpty()) {
            final List<ItemStack> drops = event.getDrops();
            final UUID u = player.getUniqueId();
            if(!leashedUponDeath.containsKey(u)) {
                leashedUponDeath.put(u, new ArrayList<>());
            }
            for(ItemStack is : leashed) {
                drops.remove(is);
                leashedUponDeath.get(u).add(is);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerRespawnEvent(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if(leashedUponDeath.containsKey(uuid)) {
            for(ItemStack is : leashedUponDeath.get(uuid)) {
                final ItemMeta itemMeta = is.getItemMeta();
                final List<String> lore = itemMeta.getLore();
                lore.remove(leashedLore);
                itemMeta.setLore(lore);
                is.setItemMeta(itemMeta);
                giveItem(player, is);
            }
            leashedUponDeath.remove(uuid);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
        if(current != null && cursor != null && (cursor.isSimilar(leash) && tryLeashing(current) || cursor.isSimilar(rarecandy) && tryUsingRareCandy(current))) {
            didApply(event, (Player) event.getWhoClicked(), current, cursor);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null) {
            final Player player = event.getPlayer();
            if(didTriggerPet(event, is, player) >= 0 || is.isSimilar(leash) || is.isSimilar(rarecandy)) {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void isDamagedEvent(isDamagedEvent event) {
        triggerInventoryPets(event, event.getEntity());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void pvAnyEvent(PvAnyEvent event) {
        triggerInventoryPets(event, event.getDamager());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        if(killer != null) {
            triggerInventoryPets(event, event.getEntity().getKiller());
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        triggerInventoryPets(event, event.getPlayer());
    }

    private void triggerInventoryPets(Event event, Player player) {
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
