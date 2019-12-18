package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.CustomKitEvolution;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.addon.file.FileKitEvolution;
import me.randomhashtags.randompackage.addon.living.LivingFallenHero;
import me.randomhashtags.randompackage.addon.obj.KitItem;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.kit.KitClaimEvent;
import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPPlayer;
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

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

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

    public String getIdentifier() { return "KITS_EVOLUTION"; }
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) { return false; }
    public Class<? extends CustomKit> getCustomKit() { return CustomKitEvolution.class; }
    public String getPath() { return "vkits"; }

    public void load() {
        loadKitUtils();
        final long started = System.currentTimeMillis();
        if(!otherdata.getBoolean("saved default vkits")) {
            final String[] v = new String[] {"ALCHEMIST", "JUDGEMENT", "LUCKY", "MIMIC", "OGRE", "PHOENIX", "SLAYER", "TROLL"};
            for(String s : v) save("kits", "VKIT_" + s + ".yml");
            otherdata.set("saved default vkits", true);
            saveOtherData();
        }

        vkitFallenHeroBundle = d(config, "vkits.items.fallen hero bundle");
        givedpitem.items.put("vkitfallenherobundle", vkitFallenHeroBundle);
        cooldown = d(config, "vkits.items.cooldown");
        vkit = new UInventory(null, config.getInt("vkits.gui.size"), colorize(config.getString("vkits.gui.title")));
        preview = new UInventory(null, 54, colorize(config.getString("vkits.items.preview.title")));
        omniGem = d(config, "vkits.items.omni gem");
        previewBackground = d(config, "vkits.items.preview");
        locked = d(config, "vkits.permissions.locked");
        tiermultipliers = new TreeMap<>();
        for(String s : config.getConfigurationSection("vkits.gui.settings.tier custom enchant multiplier").getKeys(false)) {
            tiermultipliers.put(Integer.parseInt(s), (float) config.getDouble("vkits.gui.settings.tier custom enchant multiplier." + s));
        }

        final Inventory vi = vkit.getInventory();
        final List<ItemStack> gems = new ArrayList<>(), fallenheroes = new ArrayList<>();
        final File folder = new File(DATA_FOLDER + SEPARATOR + "kits");
        int loaded = 0;
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                if(f.getName().startsWith("VKIT_")) {
                    final FileKitEvolution e = new FileKitEvolution(f);
                    vi.setItem(e.getSlot(), e.getItem());
                    gems.add(e.getFallenHeroItem(e, false));
                    fallenheroes.add(e.getFallenHeroItem(e, true));
                    loaded++;
                }
            }
        }
        addGivedpCategory(gems, UMaterial.DIAMOND, "Vkit Gems", "Givedp: Vkit Gems");
        addGivedpCategory(fallenheroes, UMaterial.BONE, "Vkit Fallen Heroes", "Givedp: Vkit Fallen Heroes");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + loaded + " Evolution Kits &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        final HashMap<UUID, LivingFallenHero> f = LivingFallenHero.living;
        if(f != null) {
            for(LivingFallenHero l : new ArrayList<>(f.values())) {
                if(l.getKit() instanceof CustomKitEvolution) {
                    l.delete();
                }
            }
        }
        for(CustomKit k : new ArrayList<>(getAllCustomKits().values())) {
            if(k instanceof CustomKitEvolution) {
                FEATURES.get(Feature.CUSTOM_KIT).remove(k.getIdentifier());
            }
        }
        unloadKitUtils();
    }
    public boolean usesTiers() { return config.getBoolean("vkits.gui.settings.use tiers"); }
    public TreeMap<Integer, Float> getCustomEnchantLevelMultipliers() { return tiermultipliers; }
    public UInventory getPreview() { return preview; }
    public ItemStack getOmniGem() { return getClone(omniGem); }
    public List<String> getNotInWarzoneMsg() { return getStringList(config, "vkits.messages.not in warzone"); }
    public List<String> getAlreadyHaveMaxTierMsg() { return getStringList(config, "vkits.messages.already have max"); }
    public List<String> getRedeemFallenHeroGemMsg() { return getStringList(config, "vkits.messages.redeem"); }
    public List<String> getUpgradeMsg() { return getStringList(config, "vkits.messages.upgrade"); }
    public List<String> getResetTargetDoesntExist() { return getStringList(config, "vkits.messages.target doesnt exist"); }
    public List<String> getResetSuccess() { return getStringList(config, "vkits.messages.success"); }
    public ItemStack getPreviewBackground() { return getClone(previewBackground); }
    public ItemStack getCooldown() { return getClone(cooldown); }
    public List<String> getPermissionsUnlocked() { return getStringList(config, "vkits.permissions.unlocked"); }
    public List<String> getPermissionsLocked() { return getStringList(config, "vkits.permissions.locked"); }
    public List<String> getPermissionsPreview() { return getStringList(config, "vkits.permissions.preview"); }

    public void view(Player player) {
        player.closeInventory();
        player.openInventory(Bukkit.createInventory(player, vkit.getSize(), vkit.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(vkit.getInventory().getContents());
        player.updateInventory();
        final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
        final HashMap<CustomKit, Integer> tiers = pdata.getKitLevels();
        final HashMap<CustomKit, Long> cooldowns = pdata.getKitCooldowns();
        for(int i = 0; i < top.getSize(); i++) {
            item = top.getItem(i);
            if(item != null) {
                item = item.clone();
                final CustomKit v = valueOfCustomKit(i, CustomKitEvolution.class);
                if(v != null) {
                    final String identifier = v.getIdentifier();
                    final int lvl = tiers.getOrDefault(v, player.hasPermission("RandomPackage.kit." + identifier) ? 1 : 0);
                    final boolean hasPerm = hasPermissionToObtain(player, v), cooldown = cooldowns.containsKey(v) && cooldowns.get(v) > System.currentTimeMillis();
                    if(!hasPerm) item = locked.clone();
                    else if(cooldown) setCooldown(player, v);
                    if(!cooldown) {
                        itemMeta = item.getItemMeta(); lore.clear();
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
                            if(!cooldown)
                                lore.addAll(getPermissionsUnlocked());
                            lore.addAll(getPermissionsPreview());
                        } else {
                            for(String s : locked.getItemMeta().getLore())
                                lore.add(colorize(s));
                        }
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                        top.setItem(i, item);
                    }

                }
            }
        }
    }
    public void give(Player player, CustomKitEvolution vkit, boolean preview) {
        if(vkit == null) return;
        final UUID u = player.getUniqueId();
        final RPPlayer pdata = RPPlayer.get(u);
        final HashMap<CustomKit, Integer> lvls = pdata.getKitLevels();
        final int vkitlvl = preview ? vkit.getMaxLevel() : lvls.containsKey(vkit) ? lvls.get(vkit) : player.hasPermission("RandomPackage.kit." + vkit.getIdentifier()) ? 1 : 0;
        final List<ItemStack> rewards = new ArrayList<>();
        final String p = player.getName();
        final float multiplier = vkit.getKitClass().getCustomEnchantLevelMultipliers().get(vkitlvl);
        for(KitItem ki : vkit.getItems()) {
            final ItemStack is = ki.getItemStack(p, vkitlvl, multiplier);
            if(is != null) {
                rewards.add(is);
            }
        }
        if(preview) {
            int s = rewards.size();
            s = s > 54 ? 54 : s%9 == 0 ? s : ((s+9)/9)*9;
            player.openInventory(Bukkit.createInventory(player, s, this.preview.getTitle()));
            previewing.add(player);
        }
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(ItemStack is : new ArrayList<>(rewards)) {
            if(is != null) {
                item = is.clone(); itemMeta = item.getItemMeta(); lore.clear();
                if(item.hasItemMeta()) {
                    if(itemMeta.hasDisplayName()) {
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PLAYER}", player.getName()).replace("{LEVEL}", Integer.toString(vkitlvl)));
                    }
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore()) {
                            if(s.startsWith("{") && s.contains("reqlevel=")) {
                                final String[] a = s.split(":");
                                final int level = getRemainingInt(a[0]), reqlevel = getRemainingInt(s.split("reqlevel=")[1].split(":")[0]), chance = a.length == 3 ? getRemainingInt(a[2]) : 100;
                                final CustomEnchant enchant = valueOfCustomEnchant(s.split("\\{")[1].split("}")[0].replace("" + level, ""));
                                if(RANDOM.nextInt(100) <= chance && enchant != null && vkitlvl >= reqlevel) {
                                    lore.add(valueOfCustomEnchantRarity(enchant).getApplyColors() + enchant.getName() + " " + toRoman(level != -1 ? level : 1+ RANDOM.nextInt(enchant.getMaxLevel())));
                                }
                            } else if(s.startsWith("{") && s.contains(":") && s.endsWith("}")) {
                                final String r = s.split(":")[RANDOM.nextInt(s.split(":").length)];
                                int level = getRemainingInt(s.split("\\{")[1].split("}")[0]);
                                final CustomEnchant enchant = valueOfCustomEnchant(r.split("\\{")[1].split("}")[0].replace("" + level, ""));

                                if(enchant != null) {
                                    if(level == -1) level = RANDOM.nextInt(enchant.getMaxLevel());
                                    lore.add(valueOfCustomEnchantRarity(enchant).getApplyColors() + enchant.getName() + " " + toRoman(level != 0 ? level : 1));
                                }
                            } else {
                                lore.add(s.replace("{LEVEL}", Integer.toString(vkitlvl)));
                            }
                        }
                        itemMeta.setLore(lore); lore.clear();
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
        final int fe = top.firstEmpty();
        if(preview && fe > -1) {
            for(int i = fe; i < top.getSize(); i++) {
                top.setItem(i, previewBackground.clone());
            }
        }
        if(!preview) {
            pdata.getKitCooldowns().put(vkit, System.currentTimeMillis() + (vkit.getCooldown() * 1000));
        }
        player.updateInventory();
        int upgradechance = vkit.getUpgradeChance();
        final KitPreClaimEvent event = new KitPreClaimEvent(pdata, player, vkit, vkitlvl);
        event.setLevelupChance(upgradechance);
        PLUGIN_MANAGER.callEvent(event);
        if(!event.isCancelled()) {
            final KitClaimEvent e = new KitClaimEvent(pdata, player, vkit, vkitlvl, rewards);
            PLUGIN_MANAGER.callEvent(e);
            if(!e.isCancelled() && !preview) {
                for(ItemStack is : rewards) {
                    giveItem(player, is);
                }
                if(RANDOM.nextInt(100) < event.getLevelupChance()) {
                    final int newlvl = vkitlvl+1;
                    if(newlvl > vkit.getMaxLevel()) return;
                    final String name = vkit.getItem().getItemMeta().getDisplayName();
                    pdata.getKitLevels().put(vkit, newlvl);
                    for(String s : getUpgradeMsg()) {
                        player.sendMessage(colorize(s.replace("{LEVEL}", Integer.toString(newlvl)).replace("{VKIT}", name).replace("{NAME}", name)));
                    }
                    for(String s : getStringList(config, "vkits.messages.upgrade broadcast")) {
                        Bukkit.broadcastMessage(colorize(s.replace("{PLAYER}", player.getName()).replace("{VKIT}", name).replace("{LEVEL}", Integer.toString(newlvl))));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if(player.getOpenInventory().getTopInventory().getHolder() == player) {
            final boolean inPreview = previewing.contains(player);
            final String t = event.getView().getTitle();
            if(t.equals(vkit.getTitle()) || inPreview) {
                event.setCancelled(true);
                player.updateInventory();
                final int r = event.getRawSlot();
                if(r >= player.getOpenInventory().getTopInventory().getSize()) return;

                final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                if(inPreview) {
                    player.closeInventory();
                    sendStringListMessage(player, getStringList(config, "vkits.messages.cannot withdraw"), null);
                } else if(event.getClick().name().contains("RIGHT")) {
                    player.closeInventory();
                    final CustomKitEvolution vkit = (CustomKitEvolution) valueOfCustomKit(r, CustomKitEvolution.class);
                    give(player, vkit, true);
                } else {
                    final CustomKitEvolution vkit = (CustomKitEvolution) valueOfCustomKit(r, CustomKitEvolution.class);
                    if(vkit == null) return;
                    final HashMap<CustomKit, Long> cooldowns = pdata.getKitCooldowns();
                    final HashMap<CustomKit, Integer> levels = pdata.getKitLevels();
                    final boolean hasPerm = hasPermissionToObtain(player, vkit);
                    final long time = System.currentTimeMillis();
                    if(!hasPerm) {
                        sendStringListMessage(player, getStringList(config, "vkits.messages.not unlocked kit"), null);
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
            final CustomKitEvolution e = valueOfCustomKitUpgradeGem(is);
            if(e != null) {
                event.setCancelled(true);
                player.updateInventory();
                final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                final HashMap<CustomKit, Integer> kits = pdata.getKitLevels();
                if(!kits.containsKey(e)) {
                    sendStringListMessage(player, getStringList(config, "vkits.messages.not unlocked kit"), null);
                } else {
                    final int lvl = kits.get(e);
                    final String name = e.getItem().getItemMeta().getDisplayName(), newl = Integer.toString(lvl+1);
                    if(lvl < e.getMaxLevel()) {
                        kits.put(e, lvl+1);
                    }
                    removeItem(player, is, 1);
                    for(String s : getUpgradeMsg())
                        player.sendMessage(colorize(s.replace("{LEVEL}", newl).replace("{VKIT}", name)));
                    for(String s : getStringList(config, "vkits.messages.upgrade broadcast"))
                        Bukkit.broadcastMessage(colorize(s.replace("{PLAYER}", player.getName()).replace("{VKIT}", name).replace("{LEVEL}", newl)));
                }
                player.updateInventory();
            } else if(is.isSimilar(vkitFallenHeroBundle)) {
                event.setCancelled(true);
                removeItem(player, is, 1);
                final List<String> s = getStringList(config, "vkits.items.fallen hero bundle.reveals");
                final int size = s.size(), amount = config.getInt("vkits.items.fallen hero bundle.reveal amount");
                for(int i = 1; i <= amount; i++) {
                    final CustomKit k = getCustomKit(s.get(RANDOM.nextInt(size)));
                    giveItem(player, k.getFallenHeroItem(k, true));
                }
            }
        }
    }
}
