package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.file.FileKitMastery;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.KitData;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
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

public class KitsMastery extends Kits {
    private static KitsMastery instance;
    public static KitsMastery getKitsMastery() {
        if(instance == null) instance = new KitsMastery();
        return instance;
    }

    private UInventory gui, preview;
    private ItemStack background, cooldown;

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return false;
    }
    @Override
    public Class<? extends CustomKit> getCustomKit() {
        return CustomKitMastery.class;
    }
    @Override
    public String getPath() {
        return "mkits";
    }

    @Override
    public void load() {
        enableKits();
        final long started = System.currentTimeMillis();
        if(!OTHER_YML.getBoolean("saved default mkits")) {
            final String[] v = new String[] {"DEATH_KNIGHT", "GHOST", "NECROMANCER"};
            for(String s : v) {
                save("kits", "MKIT_" + s + ".yml");
            }
            OTHER_YML.set("saved default mkits", true);
            saveOtherData();
        }

        gui = new UInventory(null, KITS_CONFIG.getInt("mkits.gui.size"), colorize(KITS_CONFIG.getString("mkits.gui.title")));
        preview = new UInventory(null, 54, colorize(KITS_CONFIG.getString("mkits.items.preview.title")));
        background = createItemStack(KITS_CONFIG, "mkits.gui.background");

        final List<ItemStack> gems = new ArrayList<>();
        final Inventory inv = gui.getInventory();
        int loaded = 0;
        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "kits")) {
            if(f.getName().startsWith("MKIT_")) {
                final FileKitMastery m = new FileKitMastery(f);
                inv.setItem(m.getSlot(), m.getItem());
                gems.add(m.getRedeem());
                loaded++;
            }
        }
        addGivedpCategory(gems, UMaterial.DIAMOND, "Mkit Gems", "Givedp: Mkit Gems");
        for(int i = 0; i < gui.getSize(); i++) {
            if(inv.getItem(i) == null) {
                inv.setItem(i, background);
            }
        }
        sendConsoleDidLoadFeature(loaded + " Mastery Kits", started);
    }
    @Override
    public void unload() {
        final LinkedHashMap<String, Identifiable> features = FEATURES.get(Feature.CUSTOM_KIT);
        for(CustomKit k : new ArrayList<>(getAllCustomKits().values())) {
            if(k instanceof CustomKitMastery) {
                features.remove(k.getIdentifier());
            }
        }
        disableKits();
    }

    public boolean usesTiers() {
        return false;
    }
    public TreeMap<Integer, Float> getCustomEnchantLevelMultipliers() {
        return null;
    }
    public UInventory getPreview() {
        return preview;
    }
    public ItemStack getOmniGem() {
        return null;
    }
    public List<String> getNotInWarzoneMsg() {
        return null;
    }
    public List<String> getAlreadyHaveMaxTierMsg() {
        return null;
    }
    public List<String> getRedeemFallenHeroGemMsg() {
        return null;
    }
    public List<String> getUpgradeMsg() {
        return null;
    }
    public List<String> getResetTargetDoesntExist() {
        return null;
    }
    public List<String> getResetSuccess() {
        return null;
    }
    public ItemStack getPreviewBackground() {
        return null;
    }
    public ItemStack getCooldown() {
        return getClone(cooldown);
    }
    public List<String> getPermissionsUnlocked() {
        return getStringList(KITS_CONFIG, "mkits.permissions.unlocked");
    }
    public List<String> getPermissionsLocked() {
        return getStringList(KITS_CONFIG, "mkits.permissions.locked.lore");
    }
    public List<String> getPermissionsPreview() {
        return getStringList(KITS_CONFIG, "mkits.permissions.preview");
    }

    public void view(@NotNull Player player) {
        player.closeInventory();
        player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(gui.getInventory().getContents());
        player.updateInventory();
        for(int i = 0; i < top.getSize(); i++) {
            final CustomKit mkit = valueOfCustomKit(i, CustomKitMastery.class);
            if(mkit != null) {
                final ItemStack item = top.getItem(i);
                final ItemMeta itemMeta = item.getItemMeta();
                final List<String> lore = new ArrayList<>();
                if(itemMeta.hasLore()) {
                    for(String string : itemMeta.getLore()) {
                        if(string.contains("{") && string.contains("}")) {
                            final String target = string.split("\\{")[1].split("}")[0];
                            final CustomKit kit = getCustomKit(target);
                            if(kit != null) {
                                string = string.replace("{" + kit.getIdentifier() + "}", kit.getFallenHeroName());
                            }
                        }
                        lore.add(string);
                    }
                }
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
            }
        }
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player) {
            final String title = event.getView().getTitle();
            if(title.equals(gui.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                final int slot = event.getRawSlot();
                final String click = event.getClick().name();
                final CustomKit kit = valueOfCustomKit(slot, CustomKitMastery.class);
                final CustomKitMastery mkit = kit != null ? (CustomKitMastery) kit : null;
                if(slot < 0 || slot >= top.getSize() || !click.contains("LEFT") && !click.contains("RIGHT") || event.getCurrentItem() == null || mkit == null) {
                    return;
                }
                final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
                if(click.contains("RIGHT")) {
                    preview(player, kit, kit.getMaxLevel());
                } else {
                    if(pdata.getKitData().getLevels().containsKey(mkit)) {
                    } else {
                        sendStringListMessage(player, getStringList(KITS_CONFIG, "mkits.messages.not unlocked"), null);
                    }
                }
            } else if(PREVIEWING.contains(player)) {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null) {
            final Player player = event.getPlayer();
            final CustomKitMastery mkit = valueOfCustomKitRedeem(is);
            if(mkit != null) {
                event.setCancelled(true);
                player.updateInventory();

                final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
                final HashMap<CustomKit, Integer> required = mkit.getRequiredKits();
                final List<CustomKitGlobal> gkits = new ArrayList<>();
                final List<CustomKitEvolution> vkits = new ArrayList<>();
                for(CustomKit o : required.keySet()) {
                    if(o instanceof CustomKitGlobal) {
                        gkits.add((CustomKitGlobal) o);
                    } else if(o instanceof CustomKitEvolution) {
                        vkits.add((CustomKitEvolution) o);
                    }
                }
                CustomKitGlobal missingGkit = null;
                CustomKitEvolution missingVkit = null;
                final KitData data = pdata.getKitData();
                final HashMap<CustomKit, Integer> levels = data.getLevels();
                final HashMap<CustomKit, Long> cooldowns = data.getCooldowns();
                if(!gkits.isEmpty()) {
                    for(CustomKitGlobal gkit : gkits) {
                        if(missingGkit == null && (!levels.containsKey(gkit) || levels.get(gkit) < required.get(gkit))) {
                            missingGkit = gkit;
                        }
                    }
                }
                if(!vkits.isEmpty()) {
                    for(CustomKitEvolution vkit : vkits) {
                        if(missingVkit == null && (!levels.containsKey(vkit) || levels.get(vkit) < required.get(vkit))) {
                            missingVkit = vkit;
                        }
                    }
                }
                final HashMap<String, String> replacements = new HashMap<>();
                if(missingGkit != null) {
                    replacements.put("{KIT}", missingGkit.getItem().getItemMeta().getDisplayName());
                    replacements.put("{TIER}", toRoman(required.get(missingGkit)));
                    sendStringListMessage(player, getStringList(KITS_CONFIG, "mkits.messages.unlock missing required gkit"), replacements);
                } else if(missingVkit != null) {
                    replacements.put("{KIT}", missingVkit.getItem().getItemMeta().getDisplayName());
                    replacements.put("{TIER}", toRoman(required.get(missingVkit)));
                    sendStringListMessage(player, getStringList(KITS_CONFIG, "mkits.messages.unlock missing required vkit"), replacements);
                } else {
                    if(!gkits.isEmpty()) {
                        for(String s : getStringList(KITS_CONFIG, "mkits.messages.unlocked lost gkits")) {
                            if(s.contains("{KIT}")) {
                                for(CustomKitGlobal k : gkits) {
                                    player.sendMessage(s.replace("{KIT}", k.getItem().getItemMeta().getDisplayName()));
                                }
                            } else {
                                player.sendMessage(s);
                            }
                        }
                        for(CustomKitGlobal gkit : gkits) {
                            levels.remove(gkit);
                            cooldowns.remove(gkit);
                        }
                    }
                    if(!vkits.isEmpty()) {
                        for(String string : getStringList(KITS_CONFIG, "mkits.messages.unlocked lost vkits")) {
                            if(string.contains("{KIT}")) {
                                for(CustomKitEvolution vkit : vkits) {
                                    player.sendMessage(string.replace("{KIT}", vkit.getItem().getItemMeta().getDisplayName()));
                                }
                            } else {
                                player.sendMessage(string);
                            }
                        }
                        for(CustomKitEvolution vkit : vkits) {
                            levels.remove(vkit);
                            cooldowns.remove(vkit);
                        }
                    }
                    removeItem(player, is, 1);
                    levels.put(mkit, 1);
                    replacements.put("{KIT}", mkit.getName());
                    sendStringListMessage(player, getStringList(KITS_CONFIG, "mkits.messages.unlocked"), replacements);
                    player.updateInventory();
                }
            }
        }
    }
}
