package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.Kits;
import me.randomhashtags.randompackage.addon.living.LivingFallenHero;
import me.randomhashtags.randompackage.addon.CustomKitGlobal;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.util.addon.FileKitGlobal;
import me.randomhashtags.randompackage.util.universal.UInventory;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.io.File;
import java.util.*;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class KitsGlobal extends Kits {
    private static KitsGlobal instance;
    public static KitsGlobal getKitsGlobal() {
        if(instance == null) instance = new KitsGlobal();
        return instance;
    }

    private UInventory gkit, preview;
    private ItemStack previewBackground, cooldown, omniGem;
    public ItemStack gkitFallenHeroBundle;
    public boolean heroicEnchantedEffect, tierZeroEnchantEffect;
    private List<String> permissionsUnlocked, permissionsLocked, permissionsPreview;
    private TreeMap<Integer, Float> tiermultipliers;

    public String getIdentifier() { return "KITS_GLOBAL"; }
    protected RPFeature getFeature() { return getKitsGlobal(); }
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) { return false; }
    public Class<? extends CustomKit> getCustomKit() { return CustomKitGlobal.class; }
    public String getPath() { return "gkits"; }
    public void load() {
        loadKitUtils();
        final long started = System.currentTimeMillis();

        if(!otherdata.getBoolean("saved default gkits")) {
            final String[] g = new String[] {
                    "ADMIRAL", "ARCANE", "ARENA_CHAMPION", "BUTCHER", "CANNONER", "COSMONAUT",
                    "DESTROYER", "DIABLO", "ENCHANTMENT", "GLADIATOR", "GRAND_MASTER", "GRIM_REAPER", "GUARDIAN",
                    "HYPERDRIVE", "INDEPENDENCE", "LOKI", "MASTER_BUILDER", "PALADIN", "PARTY_ANIMAL", "SPOOKY",
                    "TEMPLAR", "TINKERMASTER", "TRICKSTER", "VALENTINES", "VIKING", "VOIDWALKER", "WARLOCK",
            };
            for(String s : g) save("kits", "GKIT_" + s + ".yml");
            otherdata.set("saved default gkits", true);
            saveOtherData();
        }

        gkitFallenHeroBundle = d(config, "gkits.items.fallen hero bundle");
        givedpitem.items.put("gkitfallenherobundle", gkitFallenHeroBundle);
        heroicEnchantedEffect = config.getBoolean("gkits.items.heroic.enchanted effect");
        tierZeroEnchantEffect = config.getBoolean("gkits.gui.settings.tier zero enchant effect");
        previewBackground = d(config, "gkits.items.preview");
        gkit = new UInventory(null, config.getInt("gkits.gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gkits.gui.title")));
        preview = new UInventory(null, 54, ChatColor.translateAlternateColorCodes('&', config.getString("gkits.items.preview.title")));
        omniGem = d(config, "gkits.items.omni gem");
        cooldown = d(config, "gkits.items.cooldown");
        permissionsUnlocked = colorizeListString(config.getStringList("gkits.permissions.unlocked"));
        permissionsLocked = colorizeListString(config.getStringList("gkits.permissions.locked"));
        permissionsPreview = colorizeListString(config.getStringList("gkits.permissions.preview"));
        tiermultipliers = new TreeMap<>();
        for(String s : config.getConfigurationSection("gkits.gui.settings.tier custom enchant multiplier").getKeys(false)) {
            tiermultipliers.put(Integer.parseInt(s), (float) config.getDouble("gkits.gui.settings.tier custom enchant multiplier." + s));
        }

        FileKitGlobal.heroicprefix = ChatColor.translateAlternateColorCodes('&', config.getString("gkits.items.heroic.prefix"));

        final Inventory gi = gkit.getInventory();
        final List<ItemStack> gems = new ArrayList<>(), fallenheroes = new ArrayList<>();
        final File folder = new File(rpd + separator + "kits");
        int loaded = 0;
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                if(f.getName().startsWith("GKIT_")) {
                    final FileKitGlobal g = new FileKitGlobal(f);
                    gi.setItem(g.getSlot(), g.getItem());
                    gems.add(g.getFallenHeroItem(g, false));
                    fallenheroes.add(g.getFallenHeroItem(g, true));
                    loaded++;
                }
            }
        }
        addGivedpCategory(gems, UMaterial.DIAMOND, "Gkit Gems", "Givedp: Gkit Gems");
        addGivedpCategory(fallenheroes, UMaterial.BONE, "Gkit Fallen Heroes", "Givedp: Gkit Fallen Heroes");

        sendConsoleMessage("&6[RandomPackage] &aLoaded " + loaded + " Global Kits &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        final HashMap<UUID, LivingFallenHero> f = LivingFallenHero.living;
        if(f != null) {
            for(LivingFallenHero l : new ArrayList<>(f.values())) {
                if(l.getKit() instanceof CustomKitGlobal) {
                    l.delete();
                }
            }
        }
        if(kits != null) {
            for(CustomKit k : new ArrayList<>(kits.values())) {
                if(k instanceof CustomKitGlobal) kits.remove(k.getIdentifier());
            }
        }
        FileKitGlobal.heroicprefix = null;
        unloadKitUtils();
    }
    public boolean usesTiers() { return config.getBoolean("gkits.gui.settings.use tiers"); }
    public TreeMap<Integer, Float> getCustomEnchantLevelMultipliers() { return tiermultipliers; }
    public UInventory getPreview() { return preview; }
    public ItemStack getOmniGem() { return omniGem != null ? omniGem.clone() : null; }
    public List<String> getNotInWarzoneMsg() { return config.getStringList("gkits.messages.not in warzone"); }
    public List<String> getAlreadyHaveMaxTierMsg() { return config.getStringList("gkits.messages.already have max"); }
    public List<String> getRedeemFallenHeroGemMsg() { return config.getStringList("gkits.messages.redeem"); }
    public List<String> getResetTargetDoesntExist() { return config.getStringList("gkits.messages.target doesnt exist"); }
    public List<String> getResetSuccess() { return config.getStringList("gkits.messages.success"); }
    public ItemStack getPreviewBackground() { return previewBackground.clone(); }
    public ItemStack getCooldown() { return cooldown.clone(); }
    public List<String> getPermissionsUnlocked() { return permissionsUnlocked; }
    public List<String> getPermissionsLocked() { return permissionsLocked; }
    public List<String> getPermissionsPreview() { return permissionsPreview; }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR) && top.getHolder() == player) {
            final String t = event.getView().getTitle(), preview = this.preview.getTitle();
            final int r = event.getRawSlot();
            if(t.equals(gkit.getTitle()) || t.equals(preview)) {
                event.setCancelled(true);
                player.updateInventory();
                final CustomKit k = valueOfCustomKit(r, CustomKitGlobal.class);
                if(gkit == null || r < 0 || r >= top.getSize() || k == null) return;

                final CustomKitGlobal gkit = (CustomKitGlobal) k;
                final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                final int level = pdata.getKitLevel(gkit), tier = level <= 0 ? 1 : level;
                if(t.equals(preview)) {
                    player.closeInventory();
                    sendStringListMessage(player, config.getStringList("gkits.messages.cannot withdraw"), null);
                } else if(event.getClick().name().contains("RIGHT")) {
                    preview(player, gkit, gkit.getMaxLevel());
                } else {
                    final String n = gkit.getIdentifier();
                    final HashMap<CustomKit, Long> cooldowns = pdata.getKitCooldowns();
                    final HashMap<CustomKit, Integer> tiers = pdata.getKitLevels();
                    final boolean hasPerm = hasPermissionToObtain(player, gkit);
                    if(!hasPerm) {
                        sendStringListMessage(player, config.getStringList("gkits.messages.not unlocked kit"), null);
                    } else if(tiers.containsKey(gkit) && !cooldowns.containsKey(gkit)
                            || !tiers.containsKey(gkit) && player.hasPermission("RandomPackage.kit." + n) && !cooldowns.containsKey(gkit)
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
            final List<String> s = config.getStringList("gkits.items.fallen hero bundle.reveals");
            final int size = s.size(), amount = config.getInt("gkits.items.fallen hero bundle.reveal amount");
            for(int i = 1; i <= amount; i++) {
                final CustomKit k = getKit(s.get(random.nextInt(size)));
                giveItem(player, k.getFallenHeroItem(k, true));
            }
        }
    }

    public void view(Player player) {
        player.closeInventory();
        final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
        player.openInventory(Bukkit.createInventory(player, gkit.getSize(), gkit.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(gkit.getInventory().getContents());
        player.updateInventory();
        final HashMap<CustomKit, Integer> tiers = pdata.getKitLevels();
        final HashMap<CustomKit, Long> cooldowns = pdata.getKitCooldowns();
        final boolean usesTiers = usesTiers();
        for(int i = 0; i < top.getSize(); i++) {
            final CustomKit k = valueOfCustomKit(i, CustomKitGlobal.class);
            if(k != null) {
                item = top.getItem(i); itemMeta = item.getItemMeta(); lore.clear();
                final String identifier = k.getIdentifier();
                final boolean has = tiers.containsKey(k) || player.hasPermission("RandomPackage.kit." + identifier);
                if(cooldowns.containsKey(k) && cooldowns.get(k) > System.currentTimeMillis()) {
                    setCooldown(player, k);
                } else {
                    final CustomKitGlobal gkit = (CustomKitGlobal) k;
                    final int tier = tiers.containsKey(k) ? tiers.get(k) : has ? 1 : 0;
                    final boolean isheroic = gkit.isHeroic(), q = isheroic && heroicEnchantedEffect && (has || tierZeroEnchantEffect && tiers.containsKey(k) && !(tier < 1));
                    if(usesTiers) {
                        for(String s : config.getStringList("gkits.gui.settings.pre lore"))
                            lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("{TIER}", tier != 0 ? toRoman(tier) : "0").replace("{MAX_TIER}", toRoman(k.getMaxLevel()))));
                    }
                    if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                    for(String s : config.getStringList("gkits.gui.settings." + (has ? "un" : "") + "locked")) lore.add(ChatColor.translateAlternateColorCodes('&', s));
                    for(String s : config.getStringList("gkits.items.preview.added gui lore")) lore.add(ChatColor.translateAlternateColorCodes('&', s));
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    if(q) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    if(q) item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                }
            }
        }
        player.updateInventory();
    }
}
