package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.RarityGem;
import me.randomhashtags.randompackage.events.PlayerArmorEvent;
import me.randomhashtags.randompackage.events.ArmorSetEquipEvent;
import me.randomhashtags.randompackage.events.ArmorSetUnequipEvent;
import me.randomhashtags.randompackage.events.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.events.customenchant.*;
import me.randomhashtags.randompackage.events.MaskEquipEvent;
import me.randomhashtags.randompackage.events.MaskUnequipEvent;
import me.randomhashtags.randompackage.events.MobStackDepleteEvent;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public abstract class CustomEnchantUtils extends RPFeature {
    public String getIdentifier() { return "CUSTOM_ENCHANT_UTILS"; }

    public static HashMap<CustomEnchant, Integer> timerenchants;
    public static HashMap<Player, HashMap<CustomEnchant, Integer>> stoppedEnchants;
    public static HashMap<Location, HashMap<ItemStack, HashMap<Block, Integer>>> temporaryblocks; // <block location <original block, <temporary new block, ticks>>>>
    public static HashMap<UUID, ItemStack> shotBows;
    public static HashMap<UUID, Player> shotbows;

    public void loadUtils() {
        timerenchants = new HashMap<>();
        stoppedEnchants = new HashMap<>();
        temporaryblocks = new HashMap<>();
        shotBows = new HashMap<>();
        shotbows = new HashMap<>();
    }
    public void unloadUtils() {
        timerenchants = null;
        stoppedEnchants = null;
        temporaryblocks = null;
        shotBows = null;
        shotbows = null;
    }

    public void procPlayerArmor(Event event, Player player) {
        if(player != null) {
            for(ItemStack is : player.getInventory().getArmorContents()) {
                if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                    for(String s : is.getItemMeta().getLore()) {
                        final CustomEnchant e = valueOfCustomEnchant(s);
                        if(e != null) {
                            procEnchant(event, e, getEnchantmentLevel(s), is, player);
                        }
                    }
                }
            }
        }
    }
    public void procPlayerItem(Event event, Player player, ItemStack is) {
        if(player != null) {
            final ItemStack h = is == null ? player.getInventory().getItemInHand() : is;
            if(h != null && h.hasItemMeta() && h.getItemMeta().hasLore()) {
                for(String s : h.getItemMeta().getLore()) {
                    final CustomEnchant e = valueOfCustomEnchant(s);
                    if(e != null) {
                        procEnchant(event, e, getEnchantmentLevel(s), h, player);
                    }
                }
            }
        }
    }
    public void tryProcEnchant(Event event, Player player, CustomEnchant enchant) {
        if(player != null) {
            for(ItemStack is : player.getInventory().getArmorContents()) {
                if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                    for(String s : is.getItemMeta().getLore()) {
                        final CustomEnchant e = valueOfCustomEnchant(s);
                        if(e != null && e.equals(enchant)) {
                            procEnchant(event, e, getEnchantmentLevel(s), is, player);
                        }
                    }
                }
            }
        }
    }

    public void procEnchant(Event event, CustomEnchant enchant, int level, ItemStack itemWithEnchant, Player P) {
        final CustomEnchantProcEvent e = new CustomEnchantProcEvent(event, enchant, level, itemWithEnchant, P);
        pluginmanager.callEvent(e);
        if(!e.isCancelled() && (!(event instanceof Cancellable) || !((Cancellable) event).isCancelled()) || event instanceof PluginEnableEvent) {
            executeAttributes(e, e.player);
        }
    }

    public void executeAttributes(CustomEnchantProcEvent e, Player P) {
        final Event event = e.event;
        final CustomEnchant enchant = e.enchant;
        for(String attr : enchant.getAttributes()) {
            final String A = attr.split(";")[0].toLowerCase();
            if(event instanceof PlayerArmorEvent && (A.equals("armorequip") && ((PlayerArmorEvent) event).reason.name().contains("_EQUIP") || A.equals("armorunequip") && (((PlayerArmorEvent) event).reason.name().contains("_UNEQUIP") || ((PlayerArmorEvent) event).reason.name().contains("DROP")) || A.equals("armorpiecebreak") && ((PlayerArmorEvent) event).reason.equals(PlayerArmorEvent.ArmorEventReason.BREAK))
                    || event instanceof PvAnyEvent && (A.equals("pva") || A.equals("pvp") && ((PvAnyEvent) event).victim instanceof Player || A.equals("pve") && !(((PvAnyEvent) event).victim instanceof Player) || A.equals("arrowhit") && ((PvAnyEvent) event).proj != null && ((PvAnyEvent) event).proj instanceof Arrow && shotbows.keySet().contains(((PvAnyEvent) event).proj.getUniqueId()))

                    || event instanceof isDamagedEvent && (A.equals("isdamaged") || A.equals("hitbyarrow") && ((isDamagedEvent) event).damager instanceof Arrow || A.startsWith("damagedby(") && ((isDamagedEvent) event).cause != null && A.toUpperCase().contains(((isDamagedEvent) event).cause.name()))

                    || event instanceof CustomEnchantEntityDamageByEntityEvent && A.startsWith("ceentityisdamaged")
                    || event instanceof CustomBossDamageByEntityEvent && A.startsWith("custombossisdamaged")

                    || event instanceof ArmorSetEquipEvent && A.equals("armorsetequip")
                    || event instanceof ArmorSetUnequipEvent && A.equals("armorsetunequip")

                    || event instanceof BlockPlaceEvent && A.equals("blockplace")
                    || event instanceof BlockBreakEvent && A.equals("blockbreak")

                    || event instanceof FoodLevelChangeEvent && A.equals("foodlevelgained") && ((FoodLevelChangeEvent) event).getEntity() instanceof Player && ((FoodLevelChangeEvent) event).getFoodLevel() > ((Player) ((FoodLevelChangeEvent) event).getEntity()).getFoodLevel()
                    || event instanceof FoodLevelChangeEvent && A.equals("foodlevellost") && ((FoodLevelChangeEvent) event).getEntity() instanceof Player && ((FoodLevelChangeEvent) event).getFoodLevel() < ((Player) ((FoodLevelChangeEvent) event).getEntity()).getFoodLevel()

                    || event instanceof PlayerItemDamageEvent && A.equals("isdurabilitydamaged")

                    || event instanceof PlayerInteractEvent && A.equals("playerinteract")

                    || event instanceof ProjectileHitEvent && A.equals("arrowland") && ((ProjectileHitEvent) event).getEntity() instanceof Arrow && getHitEntity((ProjectileHitEvent) event) == null
                    || event instanceof EntityShootBowEvent && A.equals("shootbow")

                    || event instanceof PlayerDeathEvent && (A.equals("playerdeath") || A.equals("killedplayer"))
                    || event instanceof EntityDeathEvent && A.equals("killedentity") && !(((EntityDeathEvent) event).getEntity() instanceof Player)

                    || event instanceof CustomEnchantProcEvent && A.equals("enchantproc")
                    || event instanceof CEAApplyPotionEffectEvent && A.equals("ceapplypotioneffect")

                    || event instanceof MobStackDepleteEvent && A.equals("mobstackdeplete")

                    || event instanceof PluginEnableEvent && A.startsWith("timer(")
                    || attr.toLowerCase().contains(";didproc;") && e.didProc

                    || mcmmoIsEnabled() && event instanceof com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent && (A.equals("mcmmoxpgained") || A.equals("mcmmoxpgained:" + ((com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent) event).getSkill().name().toLowerCase()))

            ) {
                doAttribute(e, attr, enchant, P);
            }
        }
    }

    private void doAttribute(CustomEnchantProcEvent e, String attribute, CustomEnchant enchant, Player P) {
        final int level = e.level;
        if(attribute.contains("level")) attribute = attribute.replace("level", Integer.toString(level));
        int b = -1;
        e.setCancelled(false);
        if(attribute.contains("random{")) {
            final String ee = attribute.split("random\\{")[1].split("}")[0];
            final int min = (int) evaluate(ee.split(":")[0]), max = (int) evaluate(ee.split(":")[1].split("}")[0]);
            int r = min + random.nextInt(max - min + 1);
            attribute = attribute.replace("random{" + ee + "}", Integer.toString(r));
        }
        for(String a : attribute.split(";")) {
            b++;
            if(a.toLowerCase().startsWith("didproc") && !e.didProc) {
                return;
            } else if(a.toLowerCase().startsWith("chance=")) {
                HashMap<ItemStack, HashMap<CustomEnchant, Integer>> o = getEnchants(e.player);
                for(ItemStack q : o.keySet())
                    for(CustomEnchant E : o.get(q).keySet())
                        if(a.split("=")[1].contains(E.getName()))
                            a = a.replace(E.getName(), Integer.toString((int) evaluate(E.getEnchantProcValue().replace("level", Integer.toString(o.get(q).get(E))))));
                final int chance = (int) evaluate(a.split("=")[1].replaceAll("\\p{L}", "0"));
                final boolean didproc = random.nextInt(100) <= chance;
                if(!didproc) {
                    e.didProc = false;
                    return;
                }
                e.didProc = true;
            } else if(!a.equals(attribute.split(";")[0]) && !a.toLowerCase().startsWith("chance=") && (!attribute.toLowerCase().contains("chance=") || e.didProc)) {
                if(!attribute.toLowerCase().contains("chance=")) e.didProc = true;
                executeAttribute(e, e.event, enchant, a, attribute, b, P);
                if(a.toLowerCase().startsWith("wait{")) return;
            }
        }
    }

    public void executeAttribute(CustomEnchantProcEvent ev, Event event, CustomEnchant enchant, String a, String attribute, int b, Player P) {
        if(event != null && a.toLowerCase().startsWith("cancel")) {
            if(event instanceof Cancellable) {
                ((Cancellable) event).setCancelled(true);
            }
        } else {
            w(ev, event, enchant, getRecipients(event, a.contains("[") ? a.split("\\[")[1].split("]")[0] : a, P), a, attribute, b, P);
        }
    }
    public void w(CustomEnchantProcEvent ev, Event event, CustomEnchant enchant, List<LivingEntity> recipients, String a, String attribute, int b, Player P) {
        try {
            executeAttributes(ev, event, enchant, recipients, a, attribute, b, P);
        } catch (Exception e) {
            System.out.print(" ");
            System.out.print("[RandomPackage] Custom Enchant Exception caught. Below is the info that caused the error.");
            System.out.print("[RandomPackage] Version: " + randompackage.getDescription().getVersion() + ". User: %%__USER__%%");
            System.out.print("[RandomPackage] CustomEnchantProcEvent = " + ev);
            System.out.print("[RandomPackage] Event = " + event);
            System.out.print("[RandomPackage] Custom Enchant = " + (enchant != null ? enchant.getName() : "null"));
            System.out.print("[RandomPackage] recipients = " + recipients);
            System.out.print("[RandomPackage] a = " + a);
            System.out.print("[RandomPackage] attribute = " + attribute);
            System.out.print("[RandomPackage] b = " + b);
            System.out.print("[RandomPackage] P = " + P);
            System.out.print(" ");
            e.printStackTrace();
        }
    }

    private void executeAttributes(CustomEnchantProcEvent ev, Event event, CustomEnchant enchant, List<LivingEntity> recipients, String a, String attribute, int b, Player P) {
        if(ev != null && !ev.didProc) return;
        final Player player = ev != null ? ev.player : null;
        final int level = ev != null ? ev.level : 0;
        final boolean isPVAny = event instanceof PvAnyEvent;
        final HashMap<String, LivingEntity> recipientss = getRecipients(event, P);
        for(String s : recipientss.keySet()) {
            if(a.contains(s + "X"))
                a = a.replace(s + "X", Integer.toString(recipientss.get(s).getLocation().getBlockX()));
            if(a.contains(s + "Y"))
                a = a.replace(s + "Y", Integer.toString(recipientss.get(s).getLocation().getBlockY()));
            if(a.contains(s + "Z"))
                a = a.replace(s + "Z", Integer.toString(recipientss.get(s).getLocation().getBlockZ()));
        }
        for(int i = 1; i <= 6; i++) {
            if(a.contains("maxHealthOf(") || a.contains("healthOf(")) {
                String value = a.contains("maxHealthOf(") ? "maxHealthOf" : "healthOf";
                value = value + "(" + a.split(value + "\\(")[1].split("\\)")[0] + ")";
                if(isPVAny) {
                    final PvAnyEvent e = (PvAnyEvent) event;
                    final LivingEntity damager = e.damager, victim = e.victim;
                    if(value.contains("DAMAGER"))
                        a = a.replace(value, BigDecimal.valueOf(value.startsWith("max") ? damager.getMaxHealth() : damager.getHealth()).toPlainString());
                    if(value.contains("VICTIM"))
                        a = a.replace(value, victim != null ? BigDecimal.valueOf(value.startsWith("max") ? victim.getMaxHealth() : victim.getHealth()).toPlainString() : "0");
                }
            }
            if(a.contains("getXP(")) {
                String value = a.contains("getXP(") ? "getXP" : "";
                if(!value.equals("")) {
                    value = value + "(" + a.split(value + "\\(")[1].split("\\)")[0] + ")";
                    if(isPVAny) {
                        final PvAnyEvent E = (PvAnyEvent) event;
                        if(value.contains("DAMAGER"))
                            a = a.replace("DAMAGER", Integer.toString(getXP(E.damager)));
                        if(value.contains("VICTIM"))
                            a = a.replace("VICTIM", E != null && E.victim instanceof Player ? Integer.toString(getXP(E.victim)) : "0");
                    }
                }
            }
        }

        if(a.contains("random{")) {
            final String e = a.split("random\\{")[1].split("}")[0];
            final int min = (int) evaluate(e.split(":")[0]), max = (int) evaluate(e.split(":")[1].split("}")[0]);
            int r = min + random.nextInt(max - min + 1);
            a = a.replace("random{" + e + "}", Integer.toString(r));
        }
        if(a.contains("direction")) {
            final String type = "direction" + (a.contains("directionXOf") ? "X" : a.contains("directionYOf") ? "Y" : a.contains("directionZOf") ? "Z" : "") + "Of", r = a.split(type + "\\{")[1].split("}")[0];
            final LivingEntity recip = getRecipient(event, r);
            if(recip != null) {
                final Vector direc = recip.getLocation().getDirection();
                a = a.replace(type + "{" + r + "}", Double.toString(type.contains("X") ? direc.getX() : type.contains("Y") ? direc.getY() : type.contains("Z") ? direc.getZ() : 0.00));
            } else {
                Bukkit.broadcastMessage("[RandomPackage] recipient == null. Event=" + event.getEventName());
                return;
            }
        }
        if(a.contains("nearby{") || a.contains("nearbyAllies{") || a.contains("nearbyEnemies{")) {
            final boolean allies = a.contains("nearbyAllies{"), enemies = a.contains("nearbyEnemies{");
            final String e = a.split("nearby" + (allies ? "Allies" : enemies ? "Enemies" : "") + "\\{")[1].split("}")[0];
            final List<LivingEntity> r = new ArrayList<>();
            final LivingEntity who = event instanceof PluginEnableEvent ? P : getRecipient(event, e.split(":")[1]), k = getRecipient(event, e.split(":")[0]);
            if(who != null) {
                for(Entity en : who.getNearbyEntities(evaluate(e.split(":")[2]), evaluate(e.split(":")[3]), evaluate(e.split(":")[4]))) {
                    if(en instanceof LivingEntity && en instanceof Damageable && (k == null || k != null && !en.equals(k)))
                        if(!(en instanceof Player)
                                || who instanceof Player && en instanceof Player && (enemies && factions.isEnemy((Player) who, (Player) en) || allies && factions.isAlly((Player) who, (Player) en)))
                            r.add((LivingEntity) en);
                }
            }
            a = a.replace("nearby" + (allies ? "Allies" : enemies ? "Enemies" : "") + "{" + e + "}", r.toString().replace("\\p{Z}", ""));
            recipients = r;
        }
        if(a.contains("nearbySize{") || a.contains("nearbyAlliesSize{") || a.contains("nearbyEnemiesSize{")) {
            int size = 0;
            final boolean allies = a.contains("nearbyAlliesSize{"), enemies = a.contains("nearbyEnemiesSize{");
            final String e = a.split("nearby" + (allies ? "Allies" : enemies ? "Enemies" : "") + "Size\\{")[1].split("}")[0];
            final LivingEntity who = event instanceof PluginEnableEvent ? P : getRecipient(event, e.split(":")[1]), k = getRecipient(event, e.split(":")[0]);
            for(Entity en : who.getNearbyEntities(evaluate(e.split(":")[2]), evaluate(e.split(":")[3]), evaluate(e.split(":")[4]))) {
                if(en instanceof LivingEntity && en instanceof Damageable && (k == null || k != null && !en.equals(k)))
                    if(!allies && !(en instanceof Player)
                            || who instanceof Player && en instanceof Player && (enemies && factions.isEnemy((Player) who, (Player) en) || allies && factions.isAlly((Player) who, (Player) en)))
                        size += 1;
            }
            a = a.replace("nearby" + (allies ? "Allies" : enemies ? "Enemies" : "") + "Size{" + e + "}", Integer.toString(size));
        }

        if(a.toLowerCase().startsWith("setvelocity{")) {
            String U = a.toLowerCase().split("]")[1];
            if(isPVAny) {
                final PvAnyEvent E = (PvAnyEvent) event;
                final Player d = E.damager;
                final Vector dv = d.getVelocity();
                final LivingEntity victim = E.victim;
                U = U.replace("velocityxof(damager)", Double.toString(dv.getX())).replace("velocityyof(damager)", Double.toString(dv.getY())).replace("velocityzof(damager)", Double.toString(dv.getZ()));
                U = U.replace("velocityof(damager)", dv.getX() + ":" + dv.getY() + ":" + dv.getZ());
                if(victim != null) {
                    final Vector v = victim.getVelocity();
                    U = U.replace("velocityxof(victim)", Double.toString(v.getX())).replace("velocityyof(victim)", Double.toString(v.getY())).replace("velocityzof(victim)", Double.toString(v.getZ()));
                    U = U.replace("velocityof(victim)", v.getX() + ":" + v.getY() + ":" + v.getZ());
                }
            }
            final String[] u = U.split(":");
            final Vector knockback = new Vector(evaluate(u[0]), evaluate(u[1]), evaluate(u[2].split("}")[0]));
            for(LivingEntity l : recipients) setVelocity(l, knockback);
        } else if(a.toLowerCase().startsWith("procenchants{")) {
            for(LivingEntity l : recipients)
                if(l instanceof Player) {
                    final HashMap<ItemStack, HashMap<CustomEnchant, Integer>> enchants = getEnchants((Player) l);
                    for(ItemStack is : enchants.keySet()) {
                        final HashMap<CustomEnchant, Integer> e = enchants.get(is);
                        for(CustomEnchant ce : e.keySet())
                            if(!ce.getAttributes().toString().toLowerCase().contains("procenchants{"))
                                procEnchant(event, ce, e.get(ce), is, P);
                    }
                }
        } else if(a.toLowerCase().startsWith("breakblocks{")) {
            a = a.toLowerCase();
            final String[] A = a.split("]")[1].split(":");
            final int x1 = Integer.parseInt(A[0]), y1 = Integer.parseInt(A[1]), z1 = Integer.parseInt(A[2]);
            final int x2 = Integer.parseInt(A[3]), y2 = Integer.parseInt(A[4]), z2 = Integer.parseInt(A[5].split("}")[0]);
            for(LivingEntity le : recipients)
                if(event instanceof BlockBreakEvent)
                    breakBlocks(UMaterial.match(getItemInHand(le)), ((BlockBreakEvent) event).getBlock(), x1, y1, z1, x2, y2, z2);
        } else if(a.toLowerCase().startsWith("replaceblock{")) {
            final String args = a.toLowerCase().split("\\{")[1].split("}")[0];
            final String[] aa = args.split(":");
            final World w = player.getWorld();
            final int x = (int) evaluate(aa[0]), y = (int) evaluate(aa[1]), z = (int) evaluate(aa[2]);
            final Location l = new Location(w, x, y, z);
            final Material type = Material.valueOf(aa[3].toUpperCase());
            final Byte data = Byte.parseByte(aa[4]);
            final int ticks = Integer.parseInt(aa[5]);
            setTemporaryBlock(l, type, data, ticks);
        } else if(a.toLowerCase().startsWith("depletestacksize{") && event instanceof MobStackDepleteEvent) {
            final int amount = Integer.parseInt(a.split("\\{")[1].split("}")[0]);
            ((MobStackDepleteEvent) event).amount = amount;
        } else if(a.toLowerCase().startsWith("if{")) {
            doIf(ev, event, enchant, level, a, attribute, b, a.split("\\{")[1], P);
        }
    }
    private void doIf(CustomEnchantProcEvent ev, Event event, CustomEnchant enchant, int level, String a, String attribute, int b, String input, Player P) {
        final ArrayList<Boolean> ifs = new ArrayList<>();
        for(LivingEntity l : getRecipients(event, input, P)) {
            for(String s : (a.split("->")[0].contains("]") ? a.split("->")[0].split("]")[1] : a.split("->")[0].split("\\{")[1]).split("&&"))
                ifs.add(doVariable(ev, event, enchant, l, s));
        }
        if(!ifs.contains(false)) {
            for(String q : (a.split("->")[1].contains("-<") ? a.split("->")[1].split("-<")[0] : a.split("->")[1]).split("&&")) executeAttribute(ev, event, enchant, q, attribute, b, P);
        } else if(a.contains("-<")) {
            for(String q : a.split("-<")[1].split("&&")) executeAttribute(ev, event, enchant, q, attribute, b, P);
        }
    }
    private boolean doVariable(CustomEnchantProcEvent e, Event event, CustomEnchant enchant, LivingEntity entity, String input) {
        if(input.startsWith("canBreakHitBlock")) return event instanceof PlayerInteractEvent && ((PlayerInteractEvent) event).getClickedBlock() != null && factions.canModify(((PlayerInteractEvent) event).getPlayer().getUniqueId(), ((PlayerInteractEvent) event).getClickedBlock().getLocation());
        else if(input.startsWith("isHeadshot")) {
            final PvAnyEvent eve = event instanceof PvAnyEvent ? (PvAnyEvent) event : null;
            final Projectile p = eve != null ? eve.proj : null;
            return eve != null && p instanceof Arrow && p.getLocation().getY() > eve.victim.getEyeLocation().getY();
        } else if(input.startsWith("didproc")) return e.didProc;
        else if(input.startsWith("didntproc")) return !e.didProc;
        else if(input.startsWith("enchantIs(")) {
            if(enchant != null) {
                final String inpu = input.split("enchantIs\\(")[1].split("\\\\")[0];
                for(String s : inpu.split("\\|\\|")) if(s.equals(enchant.getName())) return true;
            }
            return false;
        } else if(input.toLowerCase().startsWith("distancebetween(")) {
            final List<LivingEntity> recipients = getRecipients(event, input.split("distanceBetween\\(")[1].split("\\)")[0], null);
            if(recipients.size() == 2)
                return input.split("distanceBetween\\(")[1].split("\\)")[1].startsWith("<=") ? distanceBetween(recipients.get(0), recipients.get(1)) <= evaluate(input.split("\\)<=")[1]) : distanceBetween(recipients.get(0), recipients.get(1)) >= evaluate(input.split("\\)>=")[1]);
            return false;
        } else if(input.toLowerCase().startsWith("isfacing(")) {
            final String facing = facing(entity).toLowerCase();
            for(String s : input.toLowerCase().split("isfacing\\(")[1].split("\\)")[0].split("\\|\\|")) {
                if(facing.toLowerCase().startsWith(s.toLowerCase())) return true;
            }
            return false;
        }
        return false;
    }
    public HashMap<String, LivingEntity> getRecipients(Event event, Player p) {
        final HashMap<String, LivingEntity> recipients = new HashMap<>();
        if(event instanceof CustomEnchantEntityDamageByEntityEvent) {
            final CustomEnchantEntityDamageByEntityEvent e = (CustomEnchantEntityDamageByEntityEvent) event;
            recipients.put("OWNER", e.getCustomEnchantEntity().getSummoner());
            if(e.damager instanceof LivingEntity) recipients.put("DAMAGER", (LivingEntity) e.damager);
        } else if(event instanceof ArmorSetEquipEvent) {
            recipients.put("PLAYER", ((ArmorSetEquipEvent) event).player);
        } else if(event instanceof ArmorSetUnequipEvent) {
            recipients.put("PLAYER", ((ArmorSetUnequipEvent) event).player);
        } else if(event instanceof PlayerArmorEvent) {
            recipients.put("PLAYER", ((PlayerArmorEvent) event).player);
        } else if(event instanceof PlayerInteractEvent) {
            recipients.put("PLAYER", ((PlayerInteractEvent) event).getPlayer());
        } else if(event instanceof BlockBreakEvent) {
            recipients.put("PLAYER", ((BlockBreakEvent) event).getPlayer());
        } else if(event instanceof PlayerItemDamageEvent) {
            recipients.put("PLAYER", ((PlayerItemDamageEvent) event).getPlayer());
        } else if(event instanceof FoodLevelChangeEvent) {
            recipients.put("PLAYER", ((FoodLevelChangeEvent) event).getEntity());
        } else if(event instanceof EntityDeathEvent) {
            final EntityDeathEvent e = (EntityDeathEvent) event;
            final LivingEntity en = e.getEntity();
            recipients.put("VICTIM", en);
            recipients.put("DAMAGER", en.getKiller());
        } else if(event instanceof CEAApplyPotionEffectEvent) {
            final CEAApplyPotionEffectEvent e = (CEAApplyPotionEffectEvent) event;
            recipients.put("VICTIM", e.appliedto);
            recipients.put("DAMAGER", e.player);
        } else if(event instanceof PvAnyEvent) {
            final PvAnyEvent pva = (PvAnyEvent) event;
            recipients.put("DAMAGER", pva.damager);
            final LivingEntity v = pva.victim;
            if(v != null) {
                if(v instanceof Arrow && ((Arrow) v).getShooter() instanceof Player) recipients.put("SHOOTER", (Player) ((Arrow) v).getShooter());
                recipients.put("VICTIM", v);
            }
        } else if(event instanceof EntityDamageEvent) {
            recipients.put("VICTIM", (LivingEntity) ((EntityDamageEvent) event).getEntity());
        } else if(event instanceof PluginEnableEvent) {
            recipients.put("PLAYER", p);
        }
        return recipients;
    }
    public List<LivingEntity> getRecipients(Event event, String input, Player p) {
        ArrayList<LivingEntity> recipients = new ArrayList<>();
        if(event instanceof CustomEnchantEntityDamageByEntityEvent) {
            final CustomEnchantEntityDamageByEntityEvent e = (CustomEnchantEntityDamageByEntityEvent) event;
            if(input.toLowerCase().contains("owner")) recipients.add(e.getCustomEnchantEntity().getSummoner());
            if(input.toLowerCase().contains("damager") && e.damager instanceof LivingEntity) recipients.add((LivingEntity) e.damager);
        } else if(event instanceof ArmorSetEquipEvent) {
            recipients.add(((ArmorSetEquipEvent) event).player);
        } else if(event instanceof ArmorSetUnequipEvent) {
            recipients.add(((ArmorSetUnequipEvent) event).player);
        } else if(event instanceof PlayerArmorEvent) {
            recipients.add(((PlayerArmorEvent) event).player);
        } else if(event instanceof MobStackDepleteEvent) {
            final MobStackDepleteEvent e = (MobStackDepleteEvent) event;
            final Entity k = e.killer;
            if(k instanceof LivingEntity)
                recipients.add((LivingEntity) k);
        } else if(event instanceof PlayerInteractEvent) {
            recipients.add(((PlayerInteractEvent) event).getPlayer());
        } else if(event instanceof BlockBreakEvent) {
            recipients.add(((BlockBreakEvent) event).getPlayer());
        } else if(event instanceof PlayerItemDamageEvent) {
            recipients.add(((PlayerItemDamageEvent) event).getPlayer());
        } else if(event instanceof FoodLevelChangeEvent) {
            recipients.add(((FoodLevelChangeEvent) event).getEntity());
        } else if(event instanceof EntityDeathEvent) {
            final EntityDeathEvent e = (EntityDeathEvent) event;
            if(input.toLowerCase().contains("victim")) recipients.add(e.getEntity());
            if(input.toLowerCase().contains("damager")) recipients.add(e.getEntity().getKiller());
        } else if(event instanceof CEAApplyPotionEffectEvent) {
            final CEAApplyPotionEffectEvent e = (CEAApplyPotionEffectEvent) event;
            if(input.toLowerCase().contains("victim")) recipients.add(e.appliedto);
            if(input.toLowerCase().contains("damager")) recipients.add(e.player);
        } else if(event instanceof ProjectileHitEvent) {
            final ProjectileHitEvent e = (ProjectileHitEvent) event;
            if(input.toLowerCase().contains("shooter") && e.getEntity().getShooter() instanceof LivingEntity) recipients.add((LivingEntity) e.getEntity().getShooter());
            final LivingEntity hit = getHitEntity(e);
            if(input.toLowerCase().contains("victim") && hit != null) recipients.add(hit);
        } else if(event instanceof PvAnyEvent) {
            final PvAnyEvent e = (PvAnyEvent) event;
            final LivingEntity v = e.victim;
            final Projectile pro = e.proj;
            if(input.toLowerCase().contains("damager")) recipients.add(e.damager);
            if(input.toLowerCase().contains("victim") && v != null) recipients.add(v);
            if(input.toLowerCase().contains("shooter") && pro != null && pro.getShooter() instanceof LivingEntity) recipients.add((LivingEntity) pro.getShooter());
        } else if(event instanceof isDamagedEvent) {
            final isDamagedEvent is = (isDamagedEvent) event;
            if(input.toLowerCase().contains("victim")) recipients.add(is.victim);
            if(input.toLowerCase().contains("damager")) recipients.add(is.damager);
        } else if(event instanceof EntityDamageEvent && !(event instanceof EntityDamageByEntityEvent)) {
            recipients.add((LivingEntity) ((EntityDamageEvent) event).getEntity());
        } else if(event instanceof MaskEquipEvent || event instanceof MaskUnequipEvent) {
            final boolean e = event instanceof MaskEquipEvent;
            recipients.add(e ? ((MaskEquipEvent) event).player : ((MaskUnequipEvent) event).player);
        } else if(event instanceof PluginEnableEvent) {
            recipients.add(p);
        }
        return recipients;
    }
    private LivingEntity getRecipient(Event event, String input) {
        if(event instanceof PlayerArmorEvent) {
            return ((PlayerArmorEvent) event).player;
        } else if(event instanceof PlayerInteractEvent) {
            return ((PlayerInteractEvent) event).getPlayer();
        } else if(event instanceof ProjectileHitEvent) {
            final ProjectileHitEvent e = (ProjectileHitEvent) event;
            if(input.toLowerCase().contains("arrow") && e.getEntity() instanceof Arrow) return (LivingEntity) e.getEntity();
            if(input.toLowerCase().contains("shooter") && e.getEntity().getShooter() != null && e.getEntity().getShooter() instanceof LivingEntity) return (LivingEntity) e.getEntity().getShooter();
        } else if(event instanceof PvAnyEvent) {
            final PvAnyEvent e = (PvAnyEvent) event;
            final LivingEntity v = e.victim;
            final Projectile pro = e.proj;
            if(input.contains("damager")) return pro instanceof LivingEntity ? (LivingEntity) pro : e.damager;
            if(input.contains("victim") && v != null) return v;
            if(input.contains("shooter") && pro != null) return (LivingEntity) pro.getShooter();
        }
        return null;
    }
    public ItemStack getRarityGem(RarityGem gem, Player player) {
        final PlayerInventory pi = player.getInventory();
        final List<String> l = gem.getItem().getItemMeta().getLore();
        for(int i = 0; i < pi.getSize(); i++) {
            final ItemStack a = pi.getItem(i);
            if(a != null && a.hasItemMeta() && a.getItemMeta().hasLore() && a.getItemMeta().getLore().equals(l)) {
                return a;
            }
        }
        return null;
    }


    public boolean canProcOn(Entity e) {
        return config.getStringList("settings.can proc on").contains(e.getType().name());
    }
    /*
        ATTRIBUTES
     */
    public void addPotionEffect(Event event, Player player, LivingEntity entity, PotionEffect potioneffect, List<String> message, CustomEnchant enchant, int level) {
        if(entity != null) {
            final CEAApplyPotionEffectEvent e = new CEAApplyPotionEffectEvent(event, player, entity, enchant, level, potioneffect);
            pluginmanager.callEvent(e);
            if(!e.isCancelled()) {
                entity.addPotionEffect(potioneffect);
                dopotioneffect(entity, potioneffect, message, enchant, level);
            } else if(entity instanceof Player) {
                procPlayerArmor(e, (Player) entity);
                procPlayerItem(e, (Player) entity, null);
            }
        }
    }
    private void removePotionEffect(LivingEntity entity, PotionEffect potioneffect, List<String> message, CustomEnchant enchant, int level) {
        if(potioneffect != null && entity != null && entity.hasPotionEffect(potioneffect.getType())) {
            entity.removePotionEffect(potioneffect.getType());
            dopotioneffect(entity, potioneffect, message, enchant, level);
        }
    }
    private void dopotioneffect(LivingEntity entity, PotionEffect potioneffect, List<String> message, CustomEnchant enchant, int level) {
        if(message != null) {
            for(String s : message) {
                if(s.contains("{ENCHANT}")) s = s.replace("{ENCHANT}", enchant.getName() + " " + toRoman(level));
                if(s.contains("{POTION_EFFECT}")) s = s.replace("{POTION_EFFECT}", potioneffect.getType().getName() + " " + toRoman(potioneffect.getAmplifier() + 1));
                entity.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
    }
    private void setTemporaryBlock(Location l, Material m, byte data, int ticks) {
        final World w = l.getWorld();
        final Block prev = w.getBlockAt(l);
        final Material prevm = prev.getType();
        final byte prevd = prev.getState().getRawData();
        if(!temporaryblocks.containsKey(l)) {
            temporaryblocks.put(l, new HashMap<>());
            final ItemStack a = new ItemStack(prevm, 1, prevd);
            temporaryblocks.get(l).put(a, new HashMap<>());
            final Block b = w.getBlockAt(l);
            b.setType(m);
            b.getState().setRawData(data);
            temporaryblocks.get(l).get(a).put(b, ticks);
            scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                w.getBlockAt(l).setType(prevm);
                w.getBlockAt(l).getState().setRawData(prevd);
                temporaryblocks.remove(l);
            }, ticks);
        }
    }
    private int getXP(LivingEntity entity) { return entity instanceof Player ? getTotalExperience((Player) entity) : 0; }
    private void breakBlocks(UMaterial usedItem, Block b, int x1, int y1, int z1, int x2, int y2, int z2) {
        if(usedItem != null && b != null) {
            final World w = b.getWorld();
            final Location bl = b.getLocation();
            final int B1 = bl.getBlockX(), B2 = bl.getBlockY(), B3 = bl.getBlockZ();
            final int X1 = x1 > x2 ? x2 : x1, X2 = X1 == x2 ? x1 : x2;
            final int Y1 = y1 > y2 ? y2 : y1, Y2 = Y1 == y2 ? y1 : y2;
            final int Z1 = z1 > z2 ? z2 : z1, Z2 = Z1 == z2 ? z1 : z2;
            for(int x = B1 + X1; x <= B1 + X2; x++) {
                for(int y = B2 + Y1; y <= B2 + Y2; y++) {
                    for(int z = B3 + Z1; z <= B3 + Z2; z++) {
                        final Block block = w.getBlockAt(new Location(w, x, y, z));
                        if(canBeBroken(usedItem, block)) block.breakNaturally();
                    }
                }
            }
        }
    }
    private boolean canBeBroken(UMaterial usedItem, Block block) {
        final String b = UMaterial.getItem(block).name(), i = usedItem.name();
        final ConfigurationSection a = config.getConfigurationSection("block break blacklist");
        if(a != null) {
            for(String s : a.getKeys(false)) {
                if(s.equals("global") || i.endsWith(s.toUpperCase())) {
                    for(String ss : config.getStringList("block break blacklist." + s)) {
                        if(b.endsWith(ss.toUpperCase())) return false;
                    }
                }
            }
        }
        return true;
    }
    private String facing(Entity entity) {
        /*  Code is from "Adrian Sohn" at https://stackoverflow.com/questions/35831619 */
        float yaw = entity.getLocation().getYaw();
        if (yaw < 0) yaw += 360;
        return yaw >= 314 || yaw < 45 ? "SOUTH" : yaw < 135 ? "WEST" : yaw < 225 ? "NORTH" : yaw < 315 ? "EAST" : "NORTH";
    }
    private double distanceBetween(Entity e1, Entity e2) {
        return e1.getLocation().distance(e2.getLocation());
    }
    private void setVelocity(LivingEntity entity, Vector vel) {
        entity.setVelocity(vel);
    }
}
