package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.CustomKitEvolution;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.addon.file.FileKitEvolution;
import me.randomhashtags.randompackage.addon.living.LivingFallenHero;
import me.randomhashtags.randompackage.addon.obj.KitItem;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.KitData;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.kit.KitClaimEvent;
import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class KitsEvolution extends Kits {
    private static KitsEvolution instance;
    public static KitsEvolution getKitsEvolution() {
        if(instance == null) instance = new KitsEvolution();
        return instance;
    }

    private UInventory vkit, preview;
    private ItemStack cooldown, previewBackground, locked, omniGem;
    public ItemStack vkitFallenHeroBundle;
    private TreeMap<Integer, Float> tiermultipliers;

    public boolean executeCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return false;
    }
    @NotNull
    @Override
    public String getIdentifier() {
        return "KITS_EVOLUTION";
    }
    public Class<? extends CustomKit> getCustomKit() {
        return CustomKitEvolution.class;
    }
    public String getPath() {
        return "vkits";
    }

    @Override
    public void load() {
        enableKits();
        final long started = System.currentTimeMillis();
        if(!OTHER_YML.getBoolean("saved default vkits")) {
            final String[] defaultFiles = new String[] {"ALCHEMIST", "JUDGEMENT", "LUCKY", "MIMIC", "OGRE", "PHOENIX", "SLAYER", "TROLL"};
            for(String s : defaultFiles) {
                save("kits", "VKIT_" + s + ".yml");
            }
            OTHER_YML.set("saved default vkits", true);
            saveOtherData();
        }

        vkitFallenHeroBundle = createItemStack(KITS_CONFIG, "vkits.items.fallen hero bundle");
        GivedpItem.INSTANCE.items.put("vkitfallenherobundle", vkitFallenHeroBundle);
        cooldown = createItemStack(KITS_CONFIG, "vkits.items.cooldown");
        vkit = new UInventory(null, KITS_CONFIG.getInt("vkits.gui.size"), colorize(KITS_CONFIG.getString("vkits.gui.title")));
        preview = new UInventory(null, 54, colorize(KITS_CONFIG.getString("vkits.items.preview.title")));
        omniGem = createItemStack(KITS_CONFIG, "vkits.items.omni gem");
        previewBackground = createItemStack(KITS_CONFIG, "vkits.items.preview");
        locked = createItemStack(KITS_CONFIG, "vkits.permissions.locked");
        tiermultipliers = new TreeMap<>();
        for(String s : KITS_CONFIG.getConfigurationSection("vkits.gui.settings.tier custom enchant multiplier").getKeys(false)) {
            tiermultipliers.put(Integer.parseInt(s), (float) KITS_CONFIG.getDouble("vkits.gui.settings.tier custom enchant multiplier." + s));
        }

        final Inventory inv = vkit.getInventory();
        final List<ItemStack> gems = new ArrayList<>(), fallenheroes = new ArrayList<>();
        int loaded = 0;
        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "kits")) {
            if(f.getName().startsWith("VKIT_")) {
                final FileKitEvolution kit = new FileKitEvolution(f);
                inv.setItem(kit.getSlot(), kit.getItem());
                gems.add(kit.getFallenHeroItem(kit, false));
                fallenheroes.add(kit.getFallenHeroItem(kit, true));
                loaded++;
            }
        }
        addGivedpCategory(gems, UMaterial.DIAMOND, "Vkit Gems", "Givedp: Vkit Gems");
        addGivedpCategory(fallenheroes, UMaterial.BONE, "Vkit Fallen Heroes", "Givedp: Vkit Fallen Heroes");
        sendConsoleDidLoadFeature(loaded + " Evolution Kits", started);
    }
    public void unload() {
        final HashMap<UUID, LivingFallenHero> living = LivingFallenHero.LIVING;
        if(living != null) {
            for(LivingFallenHero fallenHero : new ArrayList<>(living.values())) {
                if(fallenHero.getKit() instanceof CustomKitEvolution) {
                    fallenHero.delete();
                }
            }
        }
        final LinkedHashMap<String, Identifiable> features = FEATURES.get(Feature.CUSTOM_KIT);
        for(CustomKit kit : new ArrayList<>(getAllCustomKits().values())) {
            if(kit instanceof CustomKitEvolution) {
                features.remove(kit.getIdentifier());
            }
        }
        disableKits();
    }
    public boolean usesTiers() {
        return KITS_CONFIG.getBoolean("vkits.gui.settings.use tiers");
    }
    public TreeMap<Integer, Float> getCustomEnchantLevelMultipliers() {
        return tiermultipliers;
    }
    public UInventory getPreview() {
        return preview;
    }
    public ItemStack getOmniGem() {
        return getClone(omniGem);
    }
    public List<String> getNotInWarzoneMsg() {
        return getStringList(KITS_CONFIG, "vkits.messages.not in warzone");
    }
    public List<String> getAlreadyHaveMaxTierMsg() {
        return getStringList(KITS_CONFIG, "vkits.messages.already have max");
    }
    public List<String> getRedeemFallenHeroGemMsg() {
        return getStringList(KITS_CONFIG, "vkits.messages.redeem");
    }
    public List<String> getUpgradeMsg() {
        return getStringList(KITS_CONFIG, "vkits.messages.upgrade");
    }
    public List<String> getResetTargetDoesntExist() {
        return getStringList(KITS_CONFIG, "vkits.messages.target doesnt exist");
    }
    public List<String> getResetSuccess() {
        return getStringList(KITS_CONFIG, "vkits.messages.success");
    }
    public ItemStack getPreviewBackground() {
        return getClone(previewBackground);
    }
    public ItemStack getCooldown() {
        return getClone(cooldown);
    }
    public List<String> getPermissionsUnlocked() {
        return getStringList(KITS_CONFIG, "vkits.permissions.unlocked");
    }
    public List<String> getPermissionsLocked() {
        return getStringList(KITS_CONFIG, "vkits.permissions.locked");
    }
    public List<String> getPermissionsPreview() {
        return getStringList(KITS_CONFIG, "vkits.permissions.preview");
    }

    public void view(@NotNull Player player) {
        player.closeInventory();
        player.openInventory(Bukkit.createInventory(player, vkit.getSize(), vkit.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(vkit.getInventory().getContents());
        player.updateInventory();
        final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
        final KitData data = pdata.getKitData();
        final HashMap<CustomKit, Integer> tiers = data.getLevels();
        final HashMap<CustomKit, Long> cooldowns = data.getCooldowns();
        for(int i = 0; i < top.getSize(); i++) {
            ItemStack item = top.getItem(i);
            if(item != null) {
                item = item.clone();
                final CustomKit kit = valueOfCustomKit(i, CustomKitEvolution.class);
                if(kit != null) {
                    final boolean hasPerm = hasPermissionToObtain(player, kit), cooldown = cooldowns.containsKey(kit) && cooldowns.get(kit) > System.currentTimeMillis();
                    final int lvl = hasPerm ? tiers.getOrDefault(kit, 1) : 0;
                    if(!hasPerm) {
                        item = locked.clone();
                    } else if(cooldown) {
                        setCooldown(player, kit);
                    }
                    if(!cooldown) {
                        final ItemMeta itemMeta = item.getItemMeta();
                        final List<String> lore = new ArrayList<>();
                        if(!hasPerm) {
                            final ItemMeta is = top.getItem(i).getItemMeta();
                            itemMeta.setDisplayName(is.getDisplayName());
                            itemMeta.setLore(is.getLore());
                        }
                        if(itemMeta.hasLore())
                            for(String s : itemMeta.getLore()) {
                                lore.add(s.replace("{LEVEL}", Integer.toString(lvl)));
                            }
                        if(hasPerm) {
                            if(!cooldown) {
                                lore.addAll(getPermissionsUnlocked());
                            }
                            lore.addAll(getPermissionsPreview());
                        } else {
                            for(String s : locked.getItemMeta().getLore()) {
                                lore.add(colorize(s));
                            }
                        }
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                        top.setItem(i, item);
                    }
                }
            }
        }
    }
    public void give(@NotNull Player player, @NotNull CustomKitEvolution vkit, boolean preview) {
        if(player == null || vkit == null) {
            return;
        }
        final UUID uuid = player.getUniqueId();
        final FileRPPlayer pdata = FileRPPlayer.get(uuid);
        final HashMap<CustomKit, Integer> levels = pdata.getKitData().getLevels();
        final int vkitlvl = preview ? vkit.getMaxLevel() : hasPermissionToObtain(pdata, player, vkit) ? levels.getOrDefault(vkit, 1) : 0;
        final List<ItemStack> rewards = new ArrayList<>();
        final String playerName = player.getName();
        final float multiplier = vkit.getKitClass().getCustomEnchantLevelMultipliers().get(vkitlvl);
        for(KitItem ki : vkit.getItems()) {
            final ItemStack is = ki.getItemStack(playerName, vkitlvl, multiplier);
            if(is != null) {
                rewards.add(is);
            }
        }
        if(preview) {
            int s = rewards.size();
            s = s > 54 ? 54 : s%9 == 0 ? s : ((s+9)/9)*9;
            player.openInventory(Bukkit.createInventory(player, s, this.preview.getTitle()));
            PREVIEWING.add(player);
        }
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(ItemStack is : new ArrayList<>(rewards)) {
            if(is != null) {
                final ItemStack item = is.clone();
                final ItemMeta itemMeta = item.getItemMeta();
                if(item.hasItemMeta()) {
                    if(itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PLAYER}", playerName).replace("{LEVEL}", Integer.toString(vkitlvl)));
                    }
                    if(itemMeta.hasLore()) {
                        final List<String> lore = new ArrayList<>();
                        for(String itemLoreString : itemMeta.getLore()) {
                            if(itemLoreString.startsWith("{") && itemLoreString.contains("reqlevel=")) {
                                final String[] values = itemLoreString.split(":");
                                final int level = getRemainingInt(values[0]), reqlevel = getRemainingInt(itemLoreString.split("reqlevel=")[1].split(":")[0]), chance = values.length == 3 ? getRemainingInt(values[2]) : 100;
                                final CustomEnchantSpigot enchant = valueOfCustomEnchant(itemLoreString.split("\\{")[1].split("}")[0].replace("" + level, ""));
                                if(RANDOM.nextInt(100) <= chance && enchant != null && vkitlvl >= reqlevel) {
                                    lore.add(valueOfCustomEnchantRarity(enchant).getApplyColors() + enchant.getName() + " " + toRoman(level != -1 ? level : 1+ RANDOM.nextInt(enchant.getMaxLevel())));
                                }
                            } else if(itemLoreString.startsWith("{") && itemLoreString.contains(":") && itemLoreString.endsWith("}")) {
                                final String randomAmount = itemLoreString.split(":")[RANDOM.nextInt(itemLoreString.split(":").length)];
                                int level = getRemainingInt(itemLoreString.split("\\{")[1].split("}")[0]);
                                final CustomEnchantSpigot enchant = valueOfCustomEnchant(randomAmount.split("\\{")[1].split("}")[0].replace("" + level, ""));
                                if(enchant != null) {
                                    if(level == -1) {
                                        level = RANDOM.nextInt(enchant.getMaxLevel());
                                    }
                                    lore.add(valueOfCustomEnchantRarity(enchant).getApplyColors() + enchant.getName() + " " + toRoman(level != 0 ? level : 1));
                                }
                            } else {
                                lore.add(itemLoreString.replace("{LEVEL}", Integer.toString(vkitlvl)));
                            }
                        }
                        itemMeta.setLore(lore);
                    }
                    item.setItemMeta(itemMeta);
                }
                if(preview) {
                    top.addItem(item);
                } else {
                    rewards.remove(is);
                    rewards.add(item);
                }
            }
        }
        final int firstEmpty = top.firstEmpty();
        if(preview && firstEmpty > -1) {
            for(int i = firstEmpty; i < top.getSize(); i++) {
                top.setItem(i, previewBackground.clone());
            }
        }
        player.updateInventory();

        if(!preview) {
            final KitData data = pdata.getKitData();
            data.getCooldowns().put(vkit, System.currentTimeMillis() + (vkit.getCooldown()*1000));
            final KitPreClaimEvent preClaimEvent = new KitPreClaimEvent(pdata, player, vkit, vkitlvl);
            preClaimEvent.setLevelupChance(vkit.getUpgradeChance());
            PLUGIN_MANAGER.callEvent(preClaimEvent);
            if(!preClaimEvent.isCancelled()) {
                final KitClaimEvent claimEvent = new KitClaimEvent(pdata, player, vkit, vkitlvl, rewards);
                PLUGIN_MANAGER.callEvent(claimEvent);
                if(!claimEvent.isCancelled()) {
                    for(ItemStack is : rewards) {
                        giveItem(player, is);
                    }
                    if(RANDOM.nextInt(100) <= preClaimEvent.getLevelupChance()) {
                        final int newLevel = vkitlvl + 1;
                        if(newLevel > vkit.getMaxLevel()) {
                            return;
                        }
                        final String name = vkit.getItem().getItemMeta().getDisplayName(), level = Integer.toString(newLevel);
                        data.getLevels().put(vkit, newLevel);
                        for(String string : getUpgradeMsg()) {
                            player.sendMessage(string.replace("{LEVEL}", level).replace("{VKIT}", name).replace("{NAME}", name));
                        }
                        for(String string : getStringList(KITS_CONFIG, "vkits.messages.upgrade broadcast")) {
                            Bukkit.broadcastMessage(string.replace("{PLAYER}", playerName).replace("{VKIT}", name).replace("{LEVEL}", level));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player) {
            final boolean inPreview = PREVIEWING.contains(player);
            final String title = event.getView().getTitle();
            if(title.equals(vkit.getTitle()) || inPreview) {
                event.setCancelled(true);
                player.updateInventory();
                final int slot = event.getRawSlot();
                if(slot >= top.getSize()) {
                    return;
                }

                final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
                if(inPreview) {
                    player.closeInventory();
                    sendStringListMessage(player, getStringList(KITS_CONFIG, "vkits.messages.cannot withdraw"), null);
                } else if(event.getClick().name().contains("RIGHT")) {
                    player.closeInventory();
                    final CustomKitEvolution vkit = (CustomKitEvolution) valueOfCustomKit(slot, CustomKitEvolution.class);
                    give(player, vkit, true);
                } else {
                    final CustomKitEvolution vkit = (CustomKitEvolution) valueOfCustomKit(slot, CustomKitEvolution.class);
                    if(vkit == null) {
                        return;
                    }
                    final KitData data = pdata.getKitData();
                    final HashMap<CustomKit, Long> cooldowns = data.getCooldowns();
                    final HashMap<CustomKit, Integer> levels = data.getLevels();
                    final boolean hasPerm = hasPermissionToObtain(player, vkit);
                    final long time = System.currentTimeMillis();
                    if(!hasPerm) {
                        sendStringListMessage(player, getStringList(KITS_CONFIG, "vkits.messages.not unlocked kit"), null);
                    } else if(!cooldowns.containsKey(vkit) && (levels.containsKey(vkit) || !levels.containsKey(vkit) && player.hasPermission("RandomPackage.kit." + vkit.getIdentifier()))
                            || cooldowns.containsKey(vkit) && cooldowns.get(vkit) <= time) {
                        give(player, vkit, false);
                        cooldowns.put(vkit, time+(vkit.getCooldown()*1000));
                        setCooldown(player, vkit);
                    }
                }
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack is = event.getItem();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomKitEvolution vkit = valueOfCustomKitUpgradeGem(is);
            if(vkit != null) {
                event.setCancelled(true);
                player.updateInventory();
                final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
                final HashMap<CustomKit, Integer> kits = pdata.getKitData().getLevels();
                if(!kits.containsKey(vkit)) {
                    sendStringListMessage(player, getStringList(KITS_CONFIG, "vkits.messages.not unlocked kit"), null);
                } else {
                    final int lvl = kits.get(vkit);
                    final String name = vkit.getItem().getItemMeta().getDisplayName(), newLevel = Integer.toString(lvl+1);
                    if(lvl < vkit.getMaxLevel()) {
                        kits.put(vkit, lvl+1);
                    }
                    removeItem(player, is, 1);
                    for(String s : getUpgradeMsg()) {
                        player.sendMessage(s.replace("{LEVEL}", newLevel).replace("{VKIT}", name));
                    }
                    final String playerName = player.getName();
                    for(String s : getStringList(KITS_CONFIG, "vkits.messages.upgrade broadcast")) {
                        Bukkit.broadcastMessage(s.replace("{PLAYER}", playerName).replace("{VKIT}", name).replace("{LEVEL}", newLevel));
                    }
                }
                player.updateInventory();
            } else if(is.isSimilar(vkitFallenHeroBundle)) {
                event.setCancelled(true);
                removeItem(player, is, 1);
                final List<String> rewards = getStringList(KITS_CONFIG, "vkits.items.fallen hero bundle.reveals");
                final int size = rewards.size(), amount = KITS_CONFIG.getInt("vkits.items.fallen hero bundle.reveal amount");
                for(int i = 1; i <= amount; i++) {
                    final CustomKit kit = getCustomKit(rewards.get(RANDOM.nextInt(size)));
                    giveItem(player, kit.getFallenHeroItem(kit, true));
                }
            }
        }
    }
}
