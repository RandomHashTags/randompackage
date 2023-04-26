package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.CustomKitGlobal;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.addon.file.FileKitGlobal;
import me.randomhashtags.randompackage.addon.living.LivingFallenHero;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.KitData;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class KitsGlobal extends Kits {
    private static KitsGlobal instance;
    public static KitsGlobal getKitsGlobal() {
        if(instance == null) instance = new KitsGlobal();
        return instance;
    }

    private UInventory gkit, preview;
    private ItemStack previewBackground, cooldown, omniGem;
    public ItemStack gkitFallenHeroBundle;
    public String heroicPrefix;
    public boolean heroicEnchantedEffect, tierZeroEnchantEffect;
    private TreeMap<Integer, Float> tiermultipliers;

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return false;
    }
    @Override
    public Class<? extends CustomKit> getCustomKit() {
        return CustomKitGlobal.class;
    }
    @Override
    public String getPath() {
        return "gkits";
    }

    @Override
    public void load() {
        enableKits();
        final long started = System.currentTimeMillis();

        if(!OTHER_YML.getBoolean("saved default gkits")) {
            final String[] g = new String[] {
                    "ADMIRAL", "ARCANE", "ARENA_CHAMPION", "BUTCHER", "CANNONER", "COSMONAUT",
                    "DESTROYER", "DIABLO", "ENCHANTMENT", "GLADIATOR", "GRAND_MASTER", "GRIM_REAPER", "GUARDIAN",
                    "HYPERDRIVE", "INDEPENDENCE", "LOKI", "MASTER_BUILDER", "PALADIN", "PARTY_ANIMAL", "SPOOKY",
                    "TEMPLAR", "TINKERMASTER", "TRICKSTER", "VALENTINES", "VIKING", "VOIDWALKER", "WARLOCK",
            };
            for(String s : g) {
                save("kits", "GKIT_" + s + ".json");
            }
            OTHER_YML.set("saved default gkits", true);
            saveOtherData();
        }

        gkitFallenHeroBundle = createItemStack(KITS_CONFIG, "gkits.items.fallen hero bundle");
        GivedpItem.INSTANCE.items.put("gkitfallenherobundle", gkitFallenHeroBundle);
        heroicEnchantedEffect = KITS_CONFIG.getBoolean("gkits.items.heroic.enchanted effect");
        tierZeroEnchantEffect = KITS_CONFIG.getBoolean("gkits.gui.settings.tier zero enchant effect");
        previewBackground = createItemStack(KITS_CONFIG, "gkits.items.preview");
        gkit = new UInventory(null, KITS_CONFIG.getInt("gkits.gui.size"), colorize(KITS_CONFIG.getString("gkits.gui.title")));
        preview = new UInventory(null, 54, colorize(KITS_CONFIG.getString("gkits.items.preview.title")));
        omniGem = createItemStack(KITS_CONFIG, "gkits.items.omni gem");
        cooldown = createItemStack(KITS_CONFIG, "gkits.items.cooldown");
        tiermultipliers = new TreeMap<>();
        for(String s : getConfigurationSectionKeys(KITS_CONFIG, "gkits.gui.settings.tier custom enchant multiplier", false)) {
            tiermultipliers.put(Integer.parseInt(s), (float) KITS_CONFIG.getDouble("gkits.gui.settings.tier custom enchant multiplier." + s));
        }

        heroicPrefix = colorize(KITS_CONFIG.getString("gkits.items.heroic.prefix"));

        final Inventory inv = gkit.getInventory();
        final List<ItemStack> gems = new ArrayList<>(), fallenheroes = new ArrayList<>();
        int loaded = 0;
        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "kits")) {
            if(f.getName().startsWith("GKIT_")) {
                final FileKitGlobal kit = new FileKitGlobal(f);
                inv.setItem(kit.getSlot(), kit.getItem());
                gems.add(kit.getFallenHeroItem(kit, false));
                fallenheroes.add(kit.getFallenHeroItem(kit, true));
                loaded++;
            }
        }
        addGivedpCategory(gems, UMaterial.DIAMOND, "Gkit Gems", "Givedp: Gkit Gems");
        addGivedpCategory(fallenheroes, UMaterial.BONE, "Gkit Fallen Heroes", "Givedp: Gkit Fallen Heroes");

        sendConsoleDidLoadFeature(loaded + " Global Kits", started);
    }
    @Override
    public void unload() {
        final HashMap<UUID, LivingFallenHero> f = LivingFallenHero.LIVING;
        if(f != null) {
            for(LivingFallenHero l : new ArrayList<>(f.values())) {
                if(l.getKit() instanceof CustomKitGlobal) {
                    l.delete();
                }
            }
        }
        final LinkedHashMap<String, Identifiable > features = FEATURES.get(Feature.CUSTOM_KIT);
        for(CustomKit k : new ArrayList<>(getAllCustomKits().values())) {
            if(k instanceof CustomKitGlobal) {
                features.remove(k.getIdentifier());
            }
        }
        disableKits();
    }
    public boolean usesTiers() {
        return KITS_CONFIG.getBoolean("gkits.gui.settings.use tiers");
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
        return getStringList(KITS_CONFIG, "gkits.messages.not in warzone");
    }
    public List<String> getAlreadyHaveMaxTierMsg() {
        return getStringList(KITS_CONFIG, "gkits.messages.already have max");
    }
    public List<String> getRedeemFallenHeroGemMsg() {
        return getStringList(KITS_CONFIG, "gkits.messages.redeem");
    }
    public List<String> getUpgradeMsg() {
        return getStringList(KITS_CONFIG, "gkits.messages.upgrade");
    }
    public List<String> getResetTargetDoesntExist() {
        return getStringList(KITS_CONFIG, "gkits.messages.target doesnt exist");
    }
    public List<String> getResetSuccess() {
        return getStringList(KITS_CONFIG, "gkits.messages.success");
    }
    public ItemStack getPreviewBackground() {
        return getClone(previewBackground);
    }
    public ItemStack getCooldown() {
        return getClone(cooldown);
    }
    public List<String> getPermissionsUnlocked() {
        return getStringList(KITS_CONFIG, "gkits.permissions.unlocked");
    }
    public List<String> getPermissionsLocked() {
        return getStringList(KITS_CONFIG, "gkits.permissions.locked");
    }
    public List<String> getPermissionsPreview() {
        return getStringList(KITS_CONFIG, "gkits.permissions.preview");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR) && top.getHolder() == player) {
            final boolean inPreview = PREVIEWING.contains(player);
            final String t = event.getView().getTitle();
            if(t.equals(gkit.getTitle()) || inPreview) {
                event.setCancelled(true);
                player.updateInventory();
                final int slot = event.getRawSlot();
                final CustomKit kit = valueOfCustomKit(slot, CustomKitGlobal.class);
                if(gkit == null || slot < 0 || slot >= top.getSize() || kit == null) {
                    return;
                }

                final CustomKitGlobal gkit = (CustomKitGlobal) kit;
                final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
                final KitData kitData = pdata.getKitData();
                final int level = kitData.getLevel(gkit), tier = level <= 0 ? 1 : level;
                if(inPreview) {
                    player.closeInventory();
                    sendStringListMessage(player, getStringList(KITS_CONFIG, "gkits.messages.cannot withdraw"), null);
                } else if(event.getClick().name().contains("RIGHT")) {
                    preview(player, gkit, gkit.getMaxLevel());
                } else {
                    final HashMap<CustomKit, Long> cooldowns = kitData.getCooldowns();
                    final HashMap<CustomKit, Integer> tiers = kitData.getLevels();
                    final boolean hasPerm = hasPermissionToObtain(player, gkit);
                    if(!hasPerm) {
                        sendStringListMessage(player, getStringList(KITS_CONFIG, "gkits.messages.not unlocked kit"), null);
                    } else if(tiers.containsKey(gkit) && !cooldowns.containsKey(gkit)
                            || !tiers.containsKey(gkit) && player.hasPermission("RandomPackage.kit." + gkit.getIdentifier()) && !cooldowns.containsKey(gkit)
                            || cooldowns.containsKey(gkit) && cooldowns.get(gkit) <= System.currentTimeMillis()) {
                        tryGiving(pdata, player, gkit, tier, 100, true);
                        setCooldown(player, gkit);
                    }
                }
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack is = event.getItem();
        if(is != null && is.hasItemMeta() && is.isSimilar(gkitFallenHeroBundle)) {
            event.setCancelled(true);
            removeItem(player, is, 1);
            final List<String> s = getStringList(KITS_CONFIG, "gkits.items.fallen hero bundle.reveals");
            final int size = s.size(), amount = KITS_CONFIG.getInt("gkits.items.fallen hero bundle.reveal amount");
            for(int i = 1; i <= amount; i++) {
                final CustomKit k = getCustomKit(s.get(RANDOM.nextInt(size)));
                giveItem(player, k.getFallenHeroItem(k, true));
            }
        }
    }

    public void view(@NotNull Player player) {
        player.closeInventory();
        final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
        player.openInventory(Bukkit.createInventory(player, gkit.getSize(), gkit.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(gkit.getInventory().getContents());
        player.updateInventory();

        final KitData kitData = pdata.getKitData();
        final HashMap<CustomKit, Integer> tiers = kitData.getLevels();
        final HashMap<CustomKit, Long> cooldowns = kitData.getCooldowns();
        final boolean usesTiers = usesTiers();
        final List<String> preLore = getStringList(KITS_CONFIG, "gkits.gui.settings.pre lore"), addedLore = getStringList(KITS_CONFIG, "gkits.items.preview.added gui lore");
        final List<String> locked = getStringList(KITS_CONFIG, "gkits.gui.settings.locked"), unlocked = getStringList(KITS_CONFIG, "gkits.gui.settings.unlocked");
        for(int i = 0; i < top.getSize(); i++) {
            final CustomKit kit = valueOfCustomKit(i, CustomKitGlobal.class);
            if(kit != null) {
                final ItemStack item = top.getItem(i);
                final ItemMeta itemMeta = item.getItemMeta();
                final boolean has = hasPermissionToObtain(pdata, player, kit);
                if(cooldowns.containsKey(kit) && cooldowns.get(kit) > System.currentTimeMillis()) {
                    setCooldown(player, kit);
                } else {
                    final List<String> lore = new ArrayList<>();
                    final CustomKitGlobal gkit = (CustomKitGlobal) kit;
                    final int tier = tiers.containsKey(kit) ? tiers.get(kit) : has ? 1 : 0;
                    final boolean isHeroic = gkit.isHeroic(), isEnchanted = isHeroic && heroicEnchantedEffect && (has || tierZeroEnchantEffect && tiers.containsKey(kit) && !(tier < 1));
                    if(usesTiers) {
                        final String romanTier = tier != 0 ? toRoman(tier) : "0", romanMax = toRoman(kit.getMaxLevel());
                        for(String s : preLore) {
                            lore.add(s.replace("{TIER}", romanTier).replace("{MAX_TIER}", romanMax));
                        }
                    }
                    if(itemMeta.hasLore()) {
                        lore.addAll(itemMeta.getLore());
                    }
                    lore.addAll(has ? unlocked : locked);
                    lore.addAll(addedLore);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    if(isEnchanted) {
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                    if(isEnchanted) {
                        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                    }
                }
            }
        }
        player.updateInventory();
    }
}
