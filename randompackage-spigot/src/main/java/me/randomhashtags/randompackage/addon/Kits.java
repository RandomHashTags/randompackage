package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.file.FileFallenHero;
import me.randomhashtags.randompackage.addon.living.LivingFallenHero;
import me.randomhashtags.randompackage.addon.obj.KitItem;
import me.randomhashtags.randompackage.attribute.SetLevelupChance;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.KitData;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.kit.KitClaimEvent;
import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.KitEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public abstract class Kits implements RPFeatureSpigot, CommandExecutor {
    private static byte LOADED_INSTANCES = 0;
    public static YamlConfiguration KITS_CONFIG;
    public static List<HumanEntity> PREVIEWING;

    @Override
    public final boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final boolean preCommand = executeCommand(sender, cmd, commandLabel, args);
        if(preCommand) {
            return true;
        }
        final Class<? extends CustomKit> type = getCustomKit();
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String c = cmd.getName();
        final int l = args.length;
        if(l == 0 && player != null) {
            if(hasPermission(sender, "RandomPackage." + c, true)) {
                view(player);
            }
        } else if(l >= 1 && args[0].equals("reset") && type != null) {
            if(l == 2 && hasPermission(sender, "RandomPackage." + c + ".reset", true)) {
                resetAll(player, args[1], type);
            } else if(l == 3 && hasPermission(sender, "RandomPackage." + c + ".reset-kit", true)) {
                reset(player, args[1], getCustomKit(args[2]));
            }
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
    public abstract ItemStack getOmniGem();
    public abstract List<String> getNotInWarzoneMsg();
    public abstract List<String> getAlreadyHaveMaxTierMsg();
    public abstract List<String> getRedeemFallenHeroGemMsg();
    public abstract List<String> getUpgradeMsg();
    public abstract List<String> getResetTargetDoesntExist();
    public abstract List<String> getResetSuccess();
    public abstract ItemStack getPreviewBackground();
    public abstract ItemStack getCooldown();
    public abstract List<String> getPermissionsUnlocked();
    public abstract List<String> getPermissionsLocked();
    public abstract List<String> getPermissionsPreview();

    public void enableKits() {
        LOADED_INSTANCES++;
        if(LOADED_INSTANCES == 1) {
            final long started = System.currentTimeMillis();
            new SetLevelupChance().load();
            save("kits", "_settings.yml");
            KITS_CONFIG = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "kits", "_settings.yml"));
            PLUGIN_MANAGER.registerEvents(KitEvents.INSTANCE, RANDOM_PACKAGE);

            if(!OTHER_YML.getBoolean("saved default fallen heroes")) {
                final String[] f = new String[] {"GKIT", "VKIT", "MKIT"};
                for(String s : f) save("fallen heroes", s + ".yml");
                OTHER_YML.set("saved default fallen heroes", true);
                saveOtherData();
            }
            for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "fallen heroes")) {
                new FileFallenHero(f);
            }
            PREVIEWING = new ArrayList<>();
            sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.FALLEN_HERO).size() + " Fallen Heroes &e(took " + (System.currentTimeMillis()-started) + "ms)");
        }
    }

    public void disableKits() {
        if(LOADED_INSTANCES > 0) {
            LOADED_INSTANCES--;
        }
        if(LOADED_INSTANCES == 0) {
            unloadKitsData();
        }
    }
    private void unloadKitsData() {
        LOADED_INSTANCES = 0;
        KITS_CONFIG = null;
        unregister(Feature.FALLEN_HERO, Feature.CUSTOM_KIT);
        LivingFallenHero.deleteAll();
        KitEvents.INSTANCE.unload();
    }

    public final boolean hasPermissionToObtain(@NotNull Player player, @NotNull CustomKit kit) {
        return hasPermissionToObtain(FileRPPlayer.get(player.getUniqueId()), player, kit);
    }
    public final boolean hasPermissionToObtain(@NotNull RPPlayer pdata, @NotNull Player player, @NotNull CustomKit kit) {
        final KitData kitData = pdata.getKitData();
        return kitData != null && (kitData.getLevels().containsKey(kit) || player.hasPermission("RandomPackage.kit." + kit.getIdentifier()));
    }
    public final void trySpawningFallenHero(Player player, ItemStack is, CustomKit kit, Location l) {
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
    public final void tryIncreaseTier(Player player, ItemStack is, CustomKit kit) {
        final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
        final FallenHero fh = kit.getFallenHero();
        final String n = kit.getIdentifier(), name = fh != null ? kit.getFallenHeroName() : kit.getItem().getItemMeta().getDisplayName();
        final HashMap<String, String> replacements = new HashMap<>();
        final HashMap<CustomKit, Integer> tiers = pdata.getKitData().getLevels();
        boolean diduse = false;
        if(!tiers.containsKey(kit) && player.hasPermission("RandomPackage.kit." + n)) {
            diduse = true;
            tiers.put(kit, 0);
        }
        replacements.put("{NAME}", name);
        if(!diduse && tiers.containsKey(kit)) {
            final int l = tiers.get(kit);
            if(l < kit.getMaxLevel()) {
                final int newlevel = l+1;
                final String s = Integer.toString(newlevel);
                replacements.put("{LEVEL}", s);
                replacements.put("{TIER}", s);
                tiers.put(kit, newlevel);
                sendStringListMessage(player, getUpgradeMsg(), replacements);
            } else {
                sendStringListMessage(player, getAlreadyHaveMaxTierMsg(), null);
                return;
            }
        } else {
            tiers.put(kit, 1);
            sendStringListMessage(player, getRedeemFallenHeroGemMsg(), replacements);
        }
        removeItem(player, is, 1);
        player.updateInventory();
    }
    public final void setCooldown(RPPlayer pdata, CustomKit kit) {
        if(pdata != null && kit != null) {
            pdata.getKitData().getCooldowns().put(kit, System.currentTimeMillis()+kit.getCooldown()*1000);
        }
    }
    public final void setCooldown(Player player, CustomKit kit) {
        if(player != null && kit != null) {
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final KitData data = pdata.getKitData();
            final boolean hasPerm = hasPermissionToObtain(player, kit);
            final HashMap<CustomKit, Long> cooldowns = data.getCooldowns();
            final long remaining = cooldowns.get(kit)-System.currentTimeMillis();
            final boolean onCooldown = remaining > 0;
            final int slot = kit.getSlot(), tier = data.getLevel(kit);
            final ItemStack displayed = kit.getItem();
            final String remainingTime = getRemainingTime(remaining);
            final ItemStack item = getCooldown();
            final ItemMeta itemMeta = item.getItemMeta();
            final List<String> lore = new ArrayList<>();
            for(String string : itemMeta.getLore()) {
                if(string.equals("{LORE}")) {
                    for(String q : displayed.getItemMeta().getLore()) {
                        lore.add(q.replace("{LEVEL}", Integer.toString(tier)));
                    }
                } else {
                    lore.add(string.replace("{LEVEL}", Integer.toString(tier)).replace("{TIME}", remainingTime));
                }
            }
            if(hasPerm) {
                if(!onCooldown) {
                    lore.addAll(getPermissionsUnlocked());
                }
            } else {
                lore.addAll(getPermissionsLocked());
            }
            lore.addAll(getPermissionsPreview());
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            player.getOpenInventory().getTopInventory().setItem(slot, item);
            player.updateInventory();
        }
    }
    public final boolean tryGiving(RPPlayer pdata, Player player, CustomKit kit, int tier, int chanceOfGiving, boolean addCooldown) {
        if(pdata != null && player != null && kit != null && RANDOM.nextInt(100) < chanceOfGiving) {
            final KitPreClaimEvent event = new KitPreClaimEvent(pdata, player, kit, tier, chanceOfGiving);
            PLUGIN_MANAGER.callEvent(event);
            if(RANDOM.nextInt(100) < event.getChance()) {
                final String pn = player.getName();
                final float multiplier = kit.getKitClass().getCustomEnchantLevelMultipliers().getOrDefault(tier, 1f);
                final List<ItemStack> loot = new ArrayList<>();
                for(KitItem ki : kit.getItems()) {
                    final ItemStack is = ki.getItemStack(pn, tier, multiplier);
                    if(is != null) {
                        loot.add(is);
                    }
                }
                final KitClaimEvent e = new KitClaimEvent(pdata, player, kit, tier, loot);
                PLUGIN_MANAGER.callEvent(e);
                if(!e.isCancelled()) {
                    for(ItemStack is : e.getLootObtained()) {
                        giveItem(player, is);
                    }
                    if(addCooldown) {
                        setCooldown(pdata, kit);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    public final void resetAll(CommandSender sender, String target, Class type) {
        final RPPlayer pdata = r(sender, target);
        if(pdata != null) {
            final HashMap<CustomKit, Long> cooldowns = pdata.getKitData().getCooldowns();
            for(CustomKit k : new ArrayList<>(cooldowns.keySet())) {
                final boolean isInstance = Arrays.asList(k.getClass().getInterfaces()).contains(type);
                if(isInstance) {
                    cooldowns.remove(k);
                }
            }
        }
    }
    public final void reset(CommandSender sender, String target, CustomKit kit) {
        final RPPlayer pdata = r(sender, target);
        if(pdata != null) {
            pdata.getKitData().getCooldowns().remove(kit);
        }
    }
    private RPPlayer r(CommandSender sender, String target) {
        final OfflinePlayer p = Bukkit.getOfflinePlayer(target);
        final FileRPPlayer pdata = FileRPPlayer.get(p.getUniqueId());
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{PLAYER}", p.getName());
        sendStringListMessage(sender, pdata == null ? getResetTargetDoesntExist() : getResetSuccess(), replacements);
        return pdata;
    }

    public final void preview(Player player, CustomKit kit, int tier) {
        if(player == null || kit == null) {
            return;
        }
        player.closeInventory();
        final List<ItemStack> rewards = new ArrayList<>();
        final String pn = player.getName();
        final List<KitItem> items = kit.getItems();
        for(KitItem kitItem : items) {
            final ItemStack is = kitItem.getItemStack(pn, tier, 1.00f);
            if(is != null) {
                rewards.add(is);
            }
        }
        int rewardsSize = rewards.size();
        rewardsSize = rewardsSize > 54 ? 54 : rewardsSize % 9 == 0 ? rewardsSize : ((rewardsSize+9)/9)*9;
        player.openInventory(Bukkit.createInventory(player, rewardsSize, getPreview().getTitle().replace("{KIT}", kit.getIdentifier())));
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(ItemStack i : rewards) {
            top.setItem(top.firstEmpty(), i);
        }
        final ItemStack previewBackground = getPreviewBackground();
        for(int i = 0; i < top.getSize(); i++) {
            final ItemStack item = top.getItem(i);
            if(item == null || item.getType().name().contains("AIR")) {
                top.setItem(i, previewBackground);
            }
        }
        player.updateInventory();
        PREVIEWING.add(player);
    }
}
