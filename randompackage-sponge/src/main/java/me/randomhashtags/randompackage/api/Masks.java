package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.api.events.PlayerArmorEvent;
import me.randomhashtags.randompackage.api.events.customarmor.ArmorSetEquipEvent;
import me.randomhashtags.randompackage.api.events.customarmor.ArmorSetUnequipEvent;
import me.randomhashtags.randompackage.api.events.customboss.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.api.events.customenchant.*;
import me.randomhashtags.randompackage.api.events.mask.MaskEquipEvent;
import me.randomhashtags.randompackage.api.events.mask.MaskUnequipEvent;
import me.randomhashtags.randompackage.api.events.mobstacker.MobStackDepleteEvent;
import me.randomhashtags.randompackage.utils.classes.Mask;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Masks extends CustomEnchants {

    private static Masks instance;
    public static final Masks getMasks() {
        if(instance == null) instance = new Masks();
        return instance;
    }

    public boolean isEnabled = false;
    public YamlConfiguration config;
    private HashMap<Player, ItemStack> equippedMasks;
    public ItemStack maskgenerator;
    private List<String> maskCanObtain;

    public void enable() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        save(null, "masks.yml");
        pluginmanager.registerEvents(this, randompackage);
        isEnabled = true;
        config = YamlConfiguration.loadConfiguration(new File(rpd, "masks.yml"));

        equippedMasks = new HashMap<>();
        maskgenerator = d(config, "items.generator");
        givedpitem.items.put("maskgenerator", maskgenerator);
        maskCanObtain = config.getStringList("items.generator.can obtain");

        final ArrayList<ItemStack> ms = new ArrayList<>();
        final YamlConfiguration a = otherdata;
        if(!a.getBoolean("saved default masks")) {
            final String[] m = new String[] {
                    "BUNNY", "DEATH_KNIGHT", "DRAGON", "DUNGEON", "GHOST", "GLITCH", "HEADLESS", "JOKER",
                    "LOVER", "MONOPOLY", "NECROMANCER", "PARTY_HAT", "PILGRIM", "PUMPKIN_MONSTER",
                    "PURGE", "REINDEER", "RIFT", "SANTA", "SCARECROW", "SPECTRAL", "TURKEY", "ZEUS"
            };
            for(String s : m) save("masks", s + ".yml");
            a.set("saved default masks", true);
            saveOtherData();
        }

        for(File f : new File(rpd + separator + "masks").listFiles()) {
            final Mask m = new Mask(f);
            ms.add(m.getItem());
        }
        addGivedpCategory(ms, UMaterial.PLAYER_HEAD_ITEM, "Masks", "Givedp: Masks");
        final HashMap<String, Mask> M = Mask.masks;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (M != null ? M.size() : 0) + " masks &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        for(Player p : equippedMasks.keySet()) {
            p.getInventory().setHelmet(equippedMasks.get(p));
            p.updateInventory();
        }
        equippedMasks = null;
        Mask.deleteAll();
        HandlerList.unregisterAll(this);
    }

    @Listener
    private void playerQuitEvent(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if(equippedMasks.containsKey(player)) {
            player.getInventory().setHelmet(equippedMasks.get(player));
            player.updateInventory();
            equippedMasks.remove(player);
        }
    }
    @Listener
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled()) {
            final int r = event.getRawSlot();
            final String click = event.getClick().name();
            if(r < 0 || !click.equals("RIGHT") && !event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) return;
            final ItemStack current = event.getCurrentItem();
            if(current == null || !current.getType().name().endsWith("HELMET")) return;
            final ItemStack mask = event.getCursor();
            final Mask m = Mask.valueOf(mask), onitem = Mask.getOnItem(current);
            final Player player = (Player) event.getWhoClicked();
            if(m != null && onitem == null) {
                event.setCancelled(true);
                apply(m, current);
                item = m.getItem();
                final int a = item.getAmount()-mask.getAmount();
                if(a <= 0) item = new ItemStack(Material.AIR);
                else       item.setAmount(a);
                event.setCursor(item);
            } else if(click.equals("RIGHT") && onitem != null) {
                item = current; itemMeta = item.getItemMeta(); lore.clear();
                lore.addAll(itemMeta.getLore());
                lore.removeAll(onitem.getAddedLore());
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                event.setCancelled(true);
                event.setCurrentItem(item);
                event.setCursor(onitem.getItem());
            } else return;
            player.updateInventory();
        }
    }

    @Listener(priority = EventPriority.LOW)
    private void entityDamageEvent(EntityDamageEvent event) {
        if(!event.isCancelled()) {
            final Player victim = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
            final ItemStack i = equippedMasks.getOrDefault(victim, null);
            if(victim != null && i != null) {
                final PlayerInventory vi = victim.getInventory();
                final Mask m = Mask.valueOf(vi.getHelmet());
                if(m != null) {
                    vi.setHelmet(i);
                    victim.updateInventory();
                    equippedMasks.remove(victim);
                }
            }
        }
    }
    @Listener(priority = EventPriority.HIGHEST)
    private void pvAnyEvent(PvAnyEvent event) {
        if(!event.isCancelled()) {
            tryToProcMask(event.damager, event);
        }
    }
    @Listener(priority = EventPriority.HIGH)
    private void isDamagedEvent(isDamagedEvent event) {
        if(!event.isCancelled())
            tryToProcMask(event.victim, event);
    }
    @Listener
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity();
        if(!(e instanceof Player)) tryToProcMask(e.getKiller(), event);
    }
    @Listener
    private void playerDeathEvent(PlayerDeathEvent event) {
        tryToProcMask(event.getEntity().getKiller(), event);
    }
    @Listener(priority = EventPriority.HIGH)
    private void blockBreakEvent(BlockBreakEvent event) {
        if(!event.isCancelled()) tryToProcMask(event.getPlayer(), event);
    }
    @Listener(priority = EventPriority.HIGH)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        if(!event.isCancelled()) {
            final Player player = event.getPlayer();
            final Mask m = Mask.valueOf(event.getItemInHand());
            if(m != null) {
                event.setCancelled(true);
                player.updateInventory();
            } else {
                tryToProcMask(player, event);
            }
        }
    }
    @Listener(priority = EventPriority.HIGH)
    private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        if(!event.isCancelled())
            tryToProcMask((Player) event.getEntity(), event);
    }
    @Listener(priority = EventPriority.HIGH)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if(!event.isCancelled()) tryToProcMask(player, event);
        final ItemStack is = event.getItem();
        if(is != null && is.isSimilar(maskgenerator)) {
            event.setCancelled(true);
            removeItem(player, is, 1);
            giveItem(player, Mask.masks.get(maskCanObtain.get(random.nextInt(maskCanObtain.size()))).getItem());
        }
    }
    @Listener(priority = EventPriority.HIGH)
    private void entityShootBowEvent(EntityShootBowEvent event) {
        if(!event.isCancelled() && event.getEntity() instanceof Player)
            tryToProcMask((Player) event.getEntity(), event);
    }

    public void tryToProcMask(Player player, Event event) {
        if(player != null && event != null) {
            final ItemStack hel = player.getInventory().getHelmet();
            final Mask m = Mask.valueOf(hel), mm = m == null ? Mask.getOnItem(hel) : null;
            if(m != null) procMaskAttributes(player, event, m);
            else if(mm != null) procMaskAttributes(player, event, mm);
        }
    }
    public void procMaskAttributes(Player player, Event event, Mask mask) {
        for(String attr : mask.getAttributes()) {
            final String A = attr.split(";")[0].toLowerCase();
            if(event instanceof PlayerArmorEvent && (A.equals("armorequip") && ((PlayerArmorEvent) event).reason.name().contains("_EQUIP") || A.equals("armorunequip") && ((PlayerArmorEvent) event).reason.name().contains("_UNEQUIP") || A.equals("armorpiecebreak") && ((PlayerArmorEvent) event).reason.equals(PlayerArmorEvent.ArmorEventReason.BREAK))
                    || event instanceof PvAnyEvent && (A.equals("pva") || A.equals("pvp") && ((PvAnyEvent) event).victim instanceof Player || A.equals("pve") && !(((PvAnyEvent) event).victim instanceof Player))

                    || event instanceof isDamagedEvent && (A.equals("isdamaged") || A.equals("hitbyarrow") && ((isDamagedEvent) event).damager instanceof Arrow || A.startsWith("damagedby(") && ((isDamagedEvent) event).cause != null && A.toUpperCase().contains(((isDamagedEvent) event).cause.name()))

                    || event instanceof CustomEnchantEntityDamageByEntityEvent && A.startsWith("ceentityisdamaged")
                    || event instanceof CustomBossDamageByEntityEvent && A.startsWith("custombossisdamaged")

                    || event instanceof ArmorSetEquipEvent && A.equals("armorsetequip")
                    || event instanceof ArmorSetUnequipEvent && A.equals("armorsetunequip")

                    || event instanceof BlockPlaceEvent && A.equals("blockplace")
                    || event instanceof BlockBreakEvent && A.equals("blockbreak")

                    || event instanceof FoodLevelChangeEvent && (A.equals("foodlevelgained") && ((FoodLevelChangeEvent) event).getFoodLevel() > ((Player) ((FoodLevelChangeEvent) event).getEntity()).getFoodLevel() || A.equals("foodlevellost") && ((FoodLevelChangeEvent) event).getFoodLevel() < ((Player) ((FoodLevelChangeEvent) event).getEntity()).getFoodLevel())

                    || event instanceof PlayerItemDamageEvent && A.equals("isdurabilitydamaged")

                    || event instanceof PlayerInteractEvent && A.equals("playerinteract")
                    || event instanceof ProjectileHitEvent && (A.equals("arrowhit") && ((ProjectileHitEvent) event).getEntity() instanceof Arrow && (((ProjectileHitEvent) event).getEntity()).getShooter() instanceof Player && shotbows.keySet().contains(((ProjectileHitEvent) event).getEntity().getUniqueId()) || A.equals("arrowland") && ((ProjectileHitEvent) event).getEntity() instanceof Arrow && getHitEntity((ProjectileHitEvent) event) == null)
                    || event instanceof EntityShootBowEvent && A.equals("shootbow")

                    || event instanceof PlayerDeathEvent && (A.equals("playerdeath") || A.equals("killedplayer"))
                    || event instanceof EntityDeathEvent && A.equals("killedentity") && !(((EntityDeathEvent) event).getEntity() instanceof Player)

                    || event instanceof CustomEnchantProcEvent && A.equals("enchantproc")
                    || event instanceof CEAApplyPotionEffectEvent && A.equals("ceapplypotioneffect")

                    || event instanceof MobStackDepleteEvent && A.equals("mobstackdeplete")

                    || event instanceof MaskEquipEvent && A.equals("maskequip")
                    || event instanceof MaskUnequipEvent && A.equals("maskunequip")

                    || mcmmoIsEnabled && event instanceof com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent && (A.equals("mcmmoxpgained") || A.equals("mcmmoxpgained:" + ((com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent) event).getSkill().name().toLowerCase()))
            ) {
                executeAttributes(player, event, attr);
            }
        }
    }

    private void executeAttributes(Player player, Event event, String attribute) {
        for(String a : attribute.substring(attribute.split(";")[0].length()).split(";")) {
            if(event != null && a.toLowerCase().startsWith("cancel")) {
                ((Cancellable) event).setCancelled(true);
                if(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Arrow) {
                    ((EntityDamageByEntityEvent) event).getDamager().remove();
                }
                return;
            } else {
                w(null, event, null, getRecipients(event, a.contains("[") ? a.split("\\[")[1].split("]")[0] : a, null), a, attribute, -1, player);
            }
        }
    }
    @Listener(priority = EventPriority.HIGH)
    private void playerArmorEvent(PlayerArmorEvent event) {
        if(!event.isCancelled()) {
            final PlayerArmorEvent.ArmorEventReason reason = event.reason;
            final String r = reason.name();
            final Player player = event.player;
            final boolean contains = equippedMasks.keySet().contains(player);
            final ItemStack i = event.getItem().copy(), o = contains ? equippedMasks.get(player) : new ItemStack(Material.AIR);
            if(!contains && r.contains("_EQUIP")) {
                final Mask m = Mask.getOnItem(i);
                if(m != null) {
                    final MaskEquipEvent e = new MaskEquipEvent(player, m, o, reason);
                    pluginmanager.callEvent(e);
                    if(!e.isCancelled()) {
                        equippedMasks.put(player, i);
                        procMaskAttributes(player, e, m);
                        scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                            player.getInventory().setHelmet(m.getItem().copy());
                            player.updateInventory();
                        }, 0);
                    }
                } else return;
            } else if(contains && r.contains("_UNEQUIP")) {
                final Mask m = Mask.valueOf(i);
                if(m != null) {
                    final MaskUnequipEvent e = new MaskUnequipEvent(player, m, o, reason);
                    pluginmanager.callEvent(e);
                    if(!e.isCancelled()) {
                        final ItemStack h = e.helmet;
                        event.setCurrentItem(h);
                        equippedMasks.remove(player);
                        procMaskAttributes(player, e, e.mask);
                        procPlayerItem(event, player, h);
                    }
                } else return;
            } else return;
            player.updateInventory();
        }
    }

    public void apply(Mask m, ItemStack is) {
        if(m != null && is != null) {
            itemMeta = is.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            lore.addAll(m.getAddedLore());
            itemMeta.setLore(lore); lore.clear();
            is.setItemMeta(itemMeta);
        }
    }
}
