package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.living.LivingFallenHero;
import me.randomhashtags.randompackage.addons.objects.KitItem;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.addons.FileFallenHero;
import me.randomhashtags.randompackage.utils.listeners.KitEvents;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public abstract class Kits extends RPFeature implements CommandExecutor {
    private static boolean isEnabled = false;
    private static byte loadedInstances = 0;
    public static YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final boolean b = executeCommand(sender, cmd, commandLabel, args);
        if(b) return true;
        final Class<? extends CustomKit> type = getCustomKit();
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String c = cmd.getName();
        final int l = args.length;
        if(l == 0 && player != null) {
            if(hasPermission(sender, "RandomPackage." + c, true))
                view(player);
        } else if(args[0].equals("reset") && type != null) {
            if(l == 2 && hasPermission(sender, "RandomPackage." + c + ".reset", true)) resetAll(player, args[1], type);
            else if(l == 3 && hasPermission(sender, "RandomPackage." + c + ".reset-kit", true)) reset(player, args[1], getKit(args[2]));
        }
        return true;
    }

    public abstract boolean executeCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);
    public abstract Class<? extends CustomKit> getCustomKit();
    public abstract String getPath();
    public abstract void view(Player player);
    public abstract boolean usesTiers();
    public abstract TreeMap<Integer, Float> getCustomEnchantLevelMultipliers();
    public abstract UInventory getPreview();
    public abstract List<String> getNotInWarzoneMsg();
    public abstract List<String> getAlreadyHaveMaxTierMsg();
    public abstract List<String> getRedeemFallenHeroGemMsg();
    public abstract List<String> getResetTargetDoesntExist();
    public abstract List<String> getResetSuccess();
    public abstract ItemStack getPreviewBackground();
    public abstract ItemStack getCooldown();
    public abstract List<String> getPermissionsUnlocked();
    public abstract List<String> getPermissionsLocked();
    public abstract List<String> getPermissionsPreview();

    protected void loadKitUtils() {
        loadedInstances++;
        if(!isEnabled) {
            final long started = System.currentTimeMillis();
            isEnabled = true;
            save(null, "kits.yml");
            config = YamlConfiguration.loadConfiguration(new File(rpd, "kits.yml"));
            pluginmanager.registerEvents(KitEvents.getKitEvents(), randompackage);

            if(!otherdata.getBoolean("saved default fallen heroes")) {
                final String[] f = new String[] {"GKIT", "VKIT", "MKIT"};
                for(String s : f) save("fallen heroes", s + ".yml");
                otherdata.set("saved default fallen heroes", true);
                saveOtherData();
            }
            final File folder = new File(rpd + separator + "fallen heroes");
            if(folder.exists()) {
                for(File f : folder.listFiles()) {
                    new FileFallenHero(f);
                }
            }
            sendConsoleMessage("&6[RandomPackage] &aLoaded " + (fallenheroes != null ? fallenheroes.size() : 0) + " Fallen Heroes &e(took " + (System.currentTimeMillis()-started) + "ms)");
        }
    }
    protected void unloadKitUtils() {
        if(loadedInstances > 0) loadedInstances--;
        if(isEnabled && loadedInstances == 0) {
            isEnabled = false;
            config = null;
            fallenheroes = null;
            kits = null;
            LivingFallenHero.deleteAll();
            KitEvents.getKitEvents().unload();
        }
    }

    public boolean hasPermissionToObtain(Player player, CustomKit kit) {
        return player != null && kit != null && (RPPlayer.get(player.getUniqueId()).getKitLevels().containsKey(kit) || player.hasPermission("RandomPackage.kit." + kit.getIdentifier()));
    }
    public void trySpawningFallenHero(Player player, ItemStack is, CustomKit kit, Location l) {
        final FallenHero h = kit.getFallenHero();
        if(h.canSpawnAt(l)) {
            removeItem(player, is, 1);
            h.spawn(player, new Location(l.getWorld(), l.getX(), l.getY()+1, l.getZ()), kit);
            final HashMap<String, String> r = new HashMap<>();
            r.put("{NAME}", kit.getFallenHeroName());
            sendStringListMessage(player, h.getSummonMsg(), r);
        } else {
            sendStringListMessage(player, getNotInWarzoneMsg(), null);
        }
    }
    public void tryIncreaseTier(Player player, ItemStack is, CustomKit kit) {
        final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
        final String n = kit.getIdentifier();
        final HashMap<String, String> replacements = new HashMap<>();
        final HashMap<CustomKit, Integer> tiers = pdata.getKitLevels();
        if(!tiers.containsKey(kit) && player.hasPermission("RandomPackage.kit." + n)) tiers.put(kit, 0);
        if(tiers.containsKey(kit)) {
            final int l = tiers.get(kit);
            if(l != kit.getMaxLevel()) {
                tiers.put(kit, l+1);
            } else {
                sendStringListMessage(player, getAlreadyHaveMaxTierMsg(), null);
                return;
            }
        } else {
            replacements.put("{NAME}", kit.getFallenHeroName());
            tiers.put(kit, 1);
            sendStringListMessage(player, getRedeemFallenHeroGemMsg(), replacements);
        }
        removeItem(player, is, 1);
        player.updateInventory();
    }
    public void setCooldown(RPPlayer pdata, CustomKit kit) {
        if(pdata != null && kit != null) {
            pdata.getKitCooldowns().put(kit, System.currentTimeMillis()+kit.getCooldown()*1000);
        }
    }
    public void setCooldown(Player player, CustomKit kit) {
        if(player != null && kit != null) {
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final boolean hasPerm = hasPermissionToObtain(player, kit);
            final HashMap<CustomKit, Long> cooldowns = pdata.getKitCooldowns();
            final long remaining = cooldowns.get(kit)-System.currentTimeMillis();
            final boolean onCooldown = remaining > 0;
            final int slot = kit.getSlot(), tier = pdata.getKitLevel(kit);
            final ItemStack displayed = kit.getItem();
            final String remainingTime = getRemainingTime(remaining);
            item = getCooldown(); itemMeta = item.getItemMeta(); lore.clear();
            for(String s : itemMeta.getLore()) {
                if(s.equals("{LORE}"))
                    for(String q : displayed.getItemMeta().getLore())
                        lore.add(q.replace("{LEVEL}", Integer.toString(tier)));
                else
                    lore.add(s.replace("{LEVEL}", Integer.toString(tier)).replace("{TIME}", remainingTime));
            }
            if(hasPerm) {
                if(!onCooldown)
                    lore.addAll(getPermissionsUnlocked());
            } else {
                lore.addAll(getPermissionsLocked());
            }
            lore.addAll(getPermissionsPreview());
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            player.getOpenInventory().getTopInventory().setItem(slot, item);
            player.updateInventory();
        }
    }
    public void give(Player player, CustomKit kit, int tier, boolean allItems, boolean addCooldown) {
        if(player != null && kit != null) {
            final int max = kit.getMaxLevel();
            final List<KitItem> kitItems = kit.getItems();
            if(kitItems == null) return;
            final String pn = player.getName(), mt = Integer.toString(max);
            final float multiplier = kit.getKitClass().getCustomEnchantLevelMultipliers().get(tier);
            for(KitItem ki : kitItems) {
                final ItemStack is = allItems ? ki.getItemStack() : ki.getItemStack(pn, tier, multiplier);
                if(is != null) {
                    giveItem(player, is);
                }
            }
            if(addCooldown) setCooldown(RPPlayer.get(player.getUniqueId()), kit);
        }
    }
    public void resetAll(CommandSender sender, String target, Class type) {
        final RPPlayer pdata = r(sender, target);
        if(pdata != null) {
            final HashMap<CustomKit, Long> cooldowns = pdata.getKitCooldowns();
            for(CustomKit k : new ArrayList<>(cooldowns.keySet())) {
                if(k.getClass().equals(type)) {
                    cooldowns.remove(k);
                }
            }
        }
    }
    public void reset(CommandSender sender, String target, CustomKit kit) {
        final RPPlayer pdata = r(sender, target);
        if(pdata != null) pdata.getKitCooldowns().remove(kit);
    }
    private RPPlayer r(CommandSender sender, String target) {
        final OfflinePlayer p = Bukkit.getOfflinePlayer(target);
        final RPPlayer pdata = RPPlayer.get(p.getUniqueId());
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{PLAYER}", p.getName());
        sendStringListMessage(sender, pdata == null ? getResetTargetDoesntExist() : getResetSuccess(), replacements);
        return pdata;
    }


    public void preview(Player player, CustomKit kit, int tier) {
        if(player == null || kit == null) return;
        player.closeInventory();
        final List<ItemStack> rewards = new ArrayList<>();
        final String pn = player.getName();
        final List<KitItem> items = kit.getItems();
        for(KitItem ki : items) {
            final ItemStack is = ki.getItemStack(pn, tier, 1.00f);
            if(is != null) {
                rewards.add(is);
            }
        }
        int s = rewards.size();
        s = s > 54 ? 54 : s%9 == 0 ? s : ((s+9)/9)*9;
        player.openInventory(Bukkit.createInventory(player, s, getPreview().getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(ItemStack i : rewards) top.setItem(top.firstEmpty(), i);
        final ItemStack bg = getPreviewBackground();
        for(int i = 0; i < top.getSize(); i++) {
            item = top.getItem(i);
            if(item == null || item.getType().name().contains("AIR"))
                top.setItem(i, bg);
        }
        player.updateInventory();
    }
}
