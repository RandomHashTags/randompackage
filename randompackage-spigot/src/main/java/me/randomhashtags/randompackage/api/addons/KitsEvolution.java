package me.randomhashtags.randompackage.api.addons;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.CustomKit;
import me.randomhashtags.randompackage.addons.Kits;
import me.randomhashtags.randompackage.addons.living.LivingFallenHero;
import me.randomhashtags.randompackage.addons.objects.KitItem;
import me.randomhashtags.randompackage.api.nearFinished.FactionUpgrades;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.addons.FileKitEvolution;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
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

import static me.randomhashtags.randompackage.utils.listeners.GivedpItem.givedpitem;

public class KitsEvolution extends Kits {
    private static KitsEvolution instance;
    public static KitsEvolution getKitsEvolution() {
        if(instance == null) instance = new KitsEvolution();
        return instance;
    }

    private UInventory vkit, preview;
    private ItemStack cooldown, previewBackground, locked;
    public ItemStack vkitFallenHeroBundle;
    private List<String> permissionsUnlocked, permissionsLocked, permissionsPreview;
    private TreeMap<Integer, Double> tiermultipliers;

    public String getIdentifier() { return "KITS_EVOLUTION"; }

    public boolean executeCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) { return false; }
    public Class<? extends CustomKit> getCustomKit() { return FileKitEvolution.class; }
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
        vkit = new UInventory(null, config.getInt("vkits.gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("vkits.gui.title")));
        preview = new UInventory(null, 54, ChatColor.translateAlternateColorCodes('&', config.getString("vkits.items.preview.title")));
        previewBackground = d(config, "vkits.items.preview");
        locked = d(config, "vkits.permissions.locked");
        permissionsUnlocked = colorizeListString(config.getStringList("vkits.permissions.unlocked"));
        permissionsLocked = colorizeListString(config.getStringList("vkits.permissions.locked"));
        permissionsPreview = colorizeListString(config.getStringList("vkits.permissions.preview"));
        tiermultipliers = new TreeMap<>();
        for(String s : config.getConfigurationSection("vkits.gui.settings.tier custom enchant multiplier").getKeys(false)) {
            tiermultipliers.put(Integer.parseInt(s), config.getDouble("vkits.gui.settings.tier custom enchant multiplier." + s));
        }

        final Inventory vi = vkit.getInventory();
        final List<ItemStack> gems = new ArrayList<>(), fallenheroes = new ArrayList<>();
        final File folder = new File(rpd + separator + "kits");
        int loaded = 0;
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                if(f.getName().startsWith("VKIT_")) {
                    final FileKitEvolution e = new FileKitEvolution(f);
                    vi.setItem(e.getSlot(), e.getItem());
                    gems.add(e.getFallenHeroGemItem(e));
                    fallenheroes.add(e.getFallenHeroSpawnItem(e));
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
                if(l.getKit() instanceof FileKitEvolution) {
                    l.delete();
                }
            }
        }
        if(kits != null) {
            for(CustomKit k : new ArrayList<>(kits.values())) {
                if(k instanceof FileKitEvolution) kits.remove(k.getIdentifier());
            }
        }
        unloadKitUtils();
        instance = null;
    }
    public boolean usesTiers() { return config.getBoolean("vkits.gui.settings.use tiers"); }
    public TreeMap<Integer, Double> getTierCustomEnchantMultiplier() { return tiermultipliers; }
    public UInventory getPreview() { return preview; }
    public List<String> getNotInWarzoneMsg() { return config.getStringList("vkits.messages.not in warzone"); }
    public List<String> getAlreadyHaveMaxTierMsg() { return config.getStringList("vkits.messages.already have max"); }
    public List<String> getRedeemFallenHeroGemMsg() { return config.getStringList("vkits.messages.redeem"); }
    public List<String> getResetTargetDoesntExist() { return config.getStringList("vkits.messages.target doesnt exist"); }
    public List<String> getResetSuccess() { return config.getStringList("vkits.messages.success"); }
    public ItemStack getPreviewBackground() { return previewBackground.clone(); }
    public ItemStack getCooldown() { return cooldown.clone(); }
    public List<String> getPermissionsUnlocked() { return permissionsUnlocked; }
    public List<String> getPermissionsLocked() { return permissionsLocked; }
    public List<String> getPermissionsPreview() { return permissionsPreview; }


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
                final CustomKit v = CustomKit.valueOf(i, FileKitEvolution.class);
                if(v != null) {
                    final String identifier = v.getIdentifier();
                    final int lvl = tiers.getOrDefault(tiers.get(v), player.hasPermission("RandomPackage.kit." + identifier) ? 1 : 0);
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
                                lore.add(ChatColor.translateAlternateColorCodes('&', s));
                        }
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                        top.setItem(i, item);
                    }

                }
            }
        }
    }
    public void give(Player player, FileKitEvolution vkit, boolean preview) {
        if(vkit == null) return;
        final UUID u = player.getUniqueId();
        final RPPlayer pdata = RPPlayer.get(u);
        final HashMap<CustomKit, Integer> lvls = pdata.getKitLevels();
        final int vkitlvl = lvls.containsKey(vkit) ? lvls.get(vkit) : player.hasPermission("RandomPackage.kit." + vkit.getIdentifier()) ? 1 : 0;
        final List<ItemStack> rewards = new ArrayList<>();
        final YamlConfiguration yml = vkit.getYaml();
        for(KitItem ki : vkit.getItems())
            if(preview || ki.reqLevel <= 0 || vkitlvl >= ki.reqLevel)
                rewards.add(d(yml, "items." + ki.path, vkitlvl));
        if(preview) {
            int s = rewards.size();
            s = s == 9 || s == 18 || s == 27 || s == 36 || s == 45 || s == 54 ? s : s > 54 ? 54 : ((s+9)/9)*9;
            player.openInventory(Bukkit.createInventory(player, s, this.preview.getTitle()));
        }
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(ItemStack is : rewards) {
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
                                if(random.nextInt(100) <= chance && enchant != null && vkitlvl >= reqlevel) {
                                    lore.add(valueOfEnchantRarity(enchant).getApplyColors() + enchant.getName() + " " + toRoman(level != -1 ? level : 1+random.nextInt(enchant.getMaxLevel())));
                                }
                            } else if(s.startsWith("{") && s.contains(":") && s.endsWith("}")) {
                                final String r = s.split(":")[random.nextInt(s.split(":").length)];
                                int level = getRemainingInt(s.split("\\{")[1].split("}")[0]);
                                final CustomEnchant enchant = valueOfCustomEnchant(r.split("\\{")[1].split("}")[0].replace("" + level, ""));

                                if(enchant != null) {
                                    if(level == -1) level = random.nextInt(enchant.getMaxLevel());
                                    lore.add(valueOfEnchantRarity(enchant).getApplyColors() + enchant.getName() + " " + toRoman(level != 0 ? level : 1));
                                }
                            } else
                                lore.add(s.replace("{LEVEL}", Integer.toString(vkitlvl)));
                        }
                        itemMeta.setLore(lore); lore.clear();
                    }
                    item.setItemMeta(itemMeta);
                }
                if(preview) top.addItem(item);
                else        giveItem(player, item);
            }
        }
        final int fe = top.firstEmpty();
        if(preview && fe > -1)
            for(int i = fe; i < top.getSize(); i++)
                top.setItem(i, previewBackground.clone());
        if(!preview)
            pdata.getKitCooldowns().put(vkit, System.currentTimeMillis()+(vkit.getCooldown()*1000));
        player.updateInventory();
        final FactionUpgrades fu = FactionUpgrades.getFactionUpgrades();
        int upgradechance = vkit.getUpgradeChance(), a = fu.isEnabled() && hookedFactionsUUID() ? (int) (fu.getVkitLevelingChance(factions.getFactionTag(u))*100) : 0;
        upgradechance += a;
        if(!preview && random.nextInt(100) <= upgradechance) {
            final int newlvl = vkitlvl+1;
            if(newlvl > vkit.getMaxLevel()) return;
            final String name = vkit.getItem().getItemMeta().getDisplayName();
            pdata.getKitLevels().put(vkit, newlvl);
            for(String s : config.getStringList("vkits.messages.upgrade"))
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{LEVEL}", Integer.toString(newlvl)).replace("{VKIT}", name)));
            for(String s : config.getStringList("vkits.messages.upgrade broadcast"))
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{PLAYER}", player.getName()).replace("{VKIT}", name).replace("{LEVEL}", Integer.toString(newlvl))));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if(player.getOpenInventory().getTopInventory().getHolder() == player) {
            final String t = event.getView().getTitle(), preview = this.preview.getTitle();
            if(t.equals(vkit.getTitle()) || t.equals(preview)) {
                event.setCancelled(true);
                player.updateInventory();
                final int r = event.getRawSlot();
                if(r >= player.getOpenInventory().getTopInventory().getSize()) return;

                final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                if(t.equals(preview)) {
                    player.closeInventory();
                    sendStringListMessage(player, config.getStringList("vkits.messages.cannot withdraw"), null);
                } else if(event.getClick().name().contains("RIGHT")) {
                    player.closeInventory();
                    final FileKitEvolution vkit = (FileKitEvolution) CustomKit.valueOf(r, FileKitEvolution.class);
                    give(player, vkit, true);
                } else {
                    final FileKitEvolution vkit = (FileKitEvolution) CustomKit.valueOf(r, FileKitEvolution.class);
                    if(vkit == null) return;
                    final HashMap<CustomKit, Long> cooldowns = pdata.getKitCooldowns();
                    final HashMap<CustomKit, Integer> levels = pdata.getKitLevels();
                    final boolean hasPerm = hasPermissionToObtain(player, vkit);
                    final long time = System.currentTimeMillis();
                    if(!hasPerm) {
                        sendStringListMessage(player, config.getStringList("vkits.messages.not unlocked kit"), null);
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
            final FileKitEvolution e = FileKitEvolution.valueOfUpgradeGem(is);
            if(e != null) {
                event.setCancelled(true);
                player.updateInventory();
                final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                final HashMap<CustomKit, Integer> kits = pdata.getKitLevels();
                if(!kits.containsKey(e)) {
                    sendStringListMessage(player, config.getStringList("vkits.messages.not unlocked kit"), null);
                } else {
                    final int lvl = kits.get(e);
                    final String name = e.getItem().getItemMeta().getDisplayName(), newl = Integer.toString(lvl+1);
                    if(lvl < e.getMaxLevel()) {
                        kits.put(e, lvl+1);
                    }
                    removeItem(player, is, 1);
                    for(String s : config.getStringList("vkits.messages.upgrade"))
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{LEVEL}", newl).replace("{VKIT}", name)));
                    for(String s : config.getStringList("vkits.messages.upgrade broadcast"))
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{PLAYER}", player.getName()).replace("{VKIT}", name).replace("{LEVEL}", newl)));
                }
                player.updateInventory();
            } else if(is.isSimilar(vkitFallenHeroBundle)) {
                event.setCancelled(true);
                removeItem(player, is, 1);
                final List<String> s = config.getStringList("vkits.items.fallen hero bundle.reveals");
                final int size = s.size(), amount = config.getInt("vkits.items.fallen hero bundle.reveal amount");
                for(int i = 1; i <= amount; i++) {
                    final CustomKit k = getKit(s.get(random.nextInt(size)));
                    giveItem(player, k.getFallenHeroItem(k, true));
                }
            }
        }
    }
}
