package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.RarityGem;
import me.randomhashtags.randompackage.api.events.PlayerArmorEvent;
import me.randomhashtags.randompackage.api.events.ArmorSetEquipEvent;
import me.randomhashtags.randompackage.api.events.ArmorSetUnequipEvent;
import me.randomhashtags.randompackage.api.events.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.api.events.customenchant.*;
import me.randomhashtags.randompackage.api.events.MaskEquipEvent;
import me.randomhashtags.randompackage.api.events.MaskUnequipEvent;
import me.randomhashtags.randompackage.api.events.MobStackDepleteEvent;
import me.randomhashtags.randompackage.api.nearFinished.FactionUpgrades;
import me.randomhashtags.randompackage.addons.objects.CustomEnchantEntity;
import me.randomhashtags.randompackage.addons.active.LivingCustomEnchantEntity;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public abstract class CustomEnchantUtils extends RPFeature {
    private enum RomanNumeralValues {
        I(1), X(10), C(100), M(1000), V(5), L(50), D(500);
        private int val;
        RomanNumeralValues(int val) { this.val = val; }
        public int asInt() { return val; }
    }
    private int fromRoman(String num) {
        /* This code is from "batman" at https://stackoverflow.com/questions/9073150/converting-roman-numerals-to-decimal */
        num = ChatColor.stripColor(num.toUpperCase());
        int intNum = 0, prev = 0;
        for(int i = num.length() - 1; i >= 0; i--) {
            final String character = num.substring(i, i + 1);
            int temp = RomanNumeralValues.valueOf(character).asInt();
            if(temp < prev) intNum -= temp;
            else            intNum += temp;
            prev = temp;
        }
        return intNum;
    }

    private static boolean isEnabled = false;
    public static YamlConfiguration config;

    public static List<UUID> spawnedFromSpawner;
    public static List<Player> stoppedAllEnchants, frozen;
    public static HashMap<CustomEnchant, Integer> timerenchants;
    public static HashMap<Player, HashMap<CustomEnchant, Integer>> stoppedEnchants;
    public static HashMap<Player, HashMap<CustomEnchant, Double>> combos;
    public static HashMap<Location, HashMap<ItemStack, HashMap<Block, Integer>>> temporaryblocks; // <block location <original block, <temporary new block, ticks>>>>
    public static HashMap<UUID, ItemStack> shotBows;
    public static HashMap<UUID, Player> shotbows;

    public void loadUtils() {
        if(isEnabled) return;
        isEnabled = true;
        save(null, "custom enchants.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "custom enchants.yml"));

        spawnedFromSpawner = new ArrayList<>();
        stoppedAllEnchants = new ArrayList<>();
        frozen = new ArrayList<>();
        timerenchants = new HashMap<>();
        stoppedEnchants = new HashMap<>();
        combos = new HashMap<>();
        temporaryblocks = new HashMap<>();
        shotBows = new HashMap<>();
        shotbows = new HashMap<>();
    }
    public void unloadUtils() {
        if(!isEnabled) return;
        isEnabled = false;
        config = null;
        spawnedFromSpawner = null;
        timerenchants = null;
        stoppedAllEnchants = null;
        for(Player p : frozen) p.setWalkSpeed(0.2f);
        frozen = null;
        stoppedEnchants = null;
        combos = null;
        temporaryblocks = null;
        shotBows = null;
        shotbows = null;
    }

    public int getEnchantmentLevel(String string) {
        string = ChatColor.stripColor(string.split(" ")[string.split(" ").length - 1].toLowerCase().replace("i", "1").replace("v", "2").replace("x", "3").replaceAll("\\p{L}", "").replace("1", "i").replace("2", "v").replace("3", "x").replaceAll("\\p{N}", "").replaceAll("\\p{P}", "").replaceAll("\\p{S}", "").replaceAll("\\p{M}", "").replaceAll("\\p{Z}", "").toUpperCase());
        return fromRoman(string);
    }
    public void procPlayerArmor(Event event, Player player) {
        if(player != null) {
            for(ItemStack is : player.getInventory().getArmorContents()) {
                if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                    for(String s : is.getItemMeta().getLore()) {
                        final CustomEnchant e = CustomEnchant.valueOf(s);
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
                    final CustomEnchant e = CustomEnchant.valueOf(s);
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
                        final CustomEnchant e = CustomEnchant.valueOf(s);
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

    public double oldevaluate(String chancestring) {
        chancestring = chancestring.replace("\\p{L}", "").replaceAll("\\p{Z}", "");
        String parentheses = null;
        double prockchance = 0;
        if(chancestring.contains("(") && chancestring.contains(")")) {
            for(int z = 1; z <= 5; z++) {
                int startp = -1, endp = -1;
                for(int i = 0; i < chancestring.length(); i++) {
                    if(chancestring.substring(i, i + 1).equals("(")) {
                        startp = i;
                    } else if(chancestring.substring(i, i + 1).equals(")")) {
                        endp = i + 1;
                    }
                    if(startp != -1 && endp != -1) {
                        parentheses = chancestring.substring(startp, endp);
                        prockchance = evaluate(parentheses.substring(1, parentheses.length() - 1));
                        chancestring = chancestring.replace(parentheses, "" + prockchance);
                        if(chancestring.endsWith("+") || chancestring.endsWith("-") || chancestring.endsWith("*") || chancestring.endsWith("/")) {
                            chancestring = chancestring.substring(0, chancestring.length() - 1);
                        }
                        if(chancestring.startsWith("+") || chancestring.startsWith("-") || chancestring.startsWith("*") || chancestring.startsWith("/")) {
                            chancestring = chancestring.substring(1);
                        }
                        startp = -1; endp = -1;
                    }
                }
            }
        }
        return evaluate(chancestring);
    }
    private double evaluate(String input) {
        double chance = 0.00;
        if(input.equals("-1")) return chance;
        for(int i = 1; i <= 5; i++) {
            String sign = null;
            if(input.contains("*")) {
                sign = input.split("\\*")[0] + "*" + input.split("\\*")[1];
                chance = Double.parseDouble(input.split("\\*")[0]) * Double.parseDouble(input.split("\\*")[1]);
            } else if(input.contains("/")) {
                sign = input.split("/")[0] + "/" + input.split("/")[1];
                chance = Double.parseDouble(input.split("\\/")[0]) / Double.parseDouble(input.split("\\/")[1]);
            } else if(input.contains("+")) {
                sign = input.split("\\+")[0] + "+" + input.split("\\+")[1];
                chance = Double.parseDouble(input.split("\\+")[0]) + Double.parseDouble(input.split("\\+")[1]);
            } else if(input.contains("-") && !input.startsWith("-")) {
                sign = input.split("-")[0] + "-" + input.split("-")[1];
                chance = Double.parseDouble(input.split("\\-")[0]) - Double.parseDouble(input.split("\\-")[1]);
            } else if(!input.equals("")) {
                return Double.valueOf(input);
            }
            if(sign != null) input = input.replace(sign, "" + chance);
        }
        return chance;
    }

    private void doAttribute(CustomEnchantProcEvent e, String attribute, CustomEnchant enchant, Player P) {
        final int level = e.level;
        if(attribute.contains("level")) attribute = attribute.replace("level", Integer.toString(level));
        int b = -1;
        e.setCancelled(false);
        if(attribute.contains("random{")) {
            final String ee = attribute.split("random\\{")[1].split("}")[0];
            final int min = (int) oldevaluate(ee.split(":")[0]), max = (int) oldevaluate(ee.split(":")[1].split("}")[0]);
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
                            a = a.replace(E.getName(), Integer.toString((int) oldevaluate(E.getEnchantProcValue().replace("level", Integer.toString(o.get(q).get(E))))));
                final int chance = (int) oldevaluate(a.split("=")[1].replaceAll("\\p{L}", "0"));
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
        if(isPVAny) {
            a = a.replace("dmg", Double.toString(((PvAnyEvent) event).damage));
        } else if(event instanceof CustomBossDamageByEntityEvent) {
            a = a.replace("dmg", Double.toString(((CustomBossDamageByEntityEvent) event).damage));
        } else if(event instanceof isDamagedEvent) {
            a = a.replace("dmg", Double.toString(((isDamagedEvent) event).damage));
        } else if(event instanceof EntityDamageByEntityEvent) {
            a = a.replace("dmg", Double.toString(((EntityDamageByEntityEvent) event).getDamage()));
        }

        if(a.contains("random{")) {
            final String e = a.split("random\\{")[1].split("}")[0];
            final int min = (int) oldevaluate(e.split(":")[0]), max = (int) oldevaluate(e.split(":")[1].split("}")[0]);
            int r = min + random.nextInt(max - min + 1);
            a = a.replace("random{" + e + "}", Integer.toString(r));
        }
        if(a.contains("combo{")) {
            final String e = a.split("combo\\{")[1].split("}")[0], o = e.split(":")[0];
            final double combo = combos.keySet().contains(player) && combos.get(player).keySet().contains(o) ? combos.get(player).get(o) : 1.00;
            a = a.replace("combo{" + e + "}", Double.toString(combo));
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
                for(Entity en : who.getNearbyEntities(oldevaluate(e.split(":")[2]), oldevaluate(e.split(":")[3]), oldevaluate(e.split(":")[4]))) {
                    if(en instanceof LivingEntity && en instanceof Damageable && (k == null || k != null && !en.equals(k)))
                        if(!(en instanceof Player)
                                || who instanceof Player && en instanceof Player && (enemies && fapi.relationIsEnemyOrNull((Player) who, (Player) en) || allies && fapi.relationIsAlly((Player) who, (Player) en)))
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
            for(Entity en : who.getNearbyEntities(oldevaluate(e.split(":")[2]), oldevaluate(e.split(":")[3]), oldevaluate(e.split(":")[4]))) {
                if(en instanceof LivingEntity && en instanceof Damageable && (k == null || k != null && !en.equals(k)))
                    if(!allies && !(en instanceof Player)
                            || who instanceof Player && en instanceof Player && (enemies && fapi.relationIsEnemyOrNull((Player) who, (Player) en) || allies && fapi.relationIsAlly((Player) who, (Player) en)))
                        size += 1;
            }
            a = a.replace("nearby" + (allies ? "Allies" : enemies ? "Enemies" : "") + "Size{" + e + "}", Integer.toString(size));
        }

        if(a.toLowerCase().startsWith("addpotioneffect{")) {
            final PotionEffectType type = getPotionEffectType((a.contains("]") ? a.split("]")[1] : a.split("\\{")[1]).split(":")[0].toUpperCase());
            final PotionEffect pe = new PotionEffect(type, (int) oldevaluate(a.split(":")[2].split("}")[0]), (int) oldevaluate(a.split(":")[1]));
            for(LivingEntity l : recipients)
                addPotionEffect(event, ev != null ? player : null, l, pe, a.contains(":true") ? config.getStringList("messages.apply potion effect") : null, enchant, level);
        } else if(a.toLowerCase().startsWith("removepotioneffect{")) {
            final PotionEffectType type = getPotionEffectType((a.contains("]") ? a.split("]")[1] : a.split("\\{")[1]).split(":")[0].toUpperCase());
            for(LivingEntity l : recipients)
                removePotionEffect(l, getPotionEffect(l, type), a.contains(":true") ? config.getStringList("messages.remove potion effect") : null, enchant, level);
        } else if(a.toLowerCase().startsWith("sendmessage{")) {
            for(LivingEntity l : recipients)
                sendMessage(enchant, l, a.split("]")[1]);
        } else if(a.toLowerCase().startsWith("wait{")) {
            wait(ev, event, enchant, attribute, b, (int) oldevaluate(a.split("\\{")[1].split("}")[0]), P);
            return;
        } else if(a.toLowerCase().startsWith("damage{")) {
            a = a.toLowerCase();
            for(LivingEntity l : recipients)
                damage(l, oldevaluate(a.split("]")[1].split("}")[0].replace("h", "")), a.contains("h"));
        } else if(a.toLowerCase().startsWith("ignite{")) {
            a = a.toLowerCase();
            final int time = (int) oldevaluate(a.split("]")[1].split("}")[0]);
            if(a.split("\\[")[1].split("]")[0].contains("arrow")) {
                if(event instanceof EntityShootBowEvent)
                    (((EntityShootBowEvent) event).getProjectile()).setFireTicks(time);
            }
            for(LivingEntity l : recipients)
                if(l != null)
                    l.setFireTicks(time);
        } else if(a.toLowerCase().startsWith("heal{")) {
            a = a.toLowerCase();
            for(LivingEntity l : recipients)
                heal(l, oldevaluate(a.split("]")[1].split("}")[0].replace("h", "")));
        } else if(a.toLowerCase().startsWith("setdamage{")) {
            a = a.toLowerCase();
            final double dmg = oldevaluate(a.split("\\{")[1].split("}")[0]);
            if(isPVAny) {
                ((PvAnyEvent) event).damage = dmg;
            } else if(event instanceof EntityDamageEvent) {
                ((EntityDamageEvent) event).setDamage(dmg);
            }
        } else if(a.toLowerCase().startsWith("setdurability{")) {
            a = a.toLowerCase();
            for(LivingEntity l : recipients) {
                ItemStack p = null;
                double difference = -1;
                if(a.contains("mostdamaged")) {
                    for(ItemStack A : l.getEquipment().getArmorContents())
                        if(A != null && A.getType() != Material.AIR) {
                            double newdif = Double.parseDouble(Short.toString(A.getDurability())) / Double.parseDouble(Short.toString(A.getType().getMaxDurability()));
                            if(difference == -1 || newdif > difference) {
                                difference = newdif;
                                p = A;
                            }
                        }
                } else if(a.contains("helmet") && l.getEquipment().getHelmet() != null) p = l.getEquipment().getHelmet();
                else if(a.contains("chestplate") && l.getEquipment().getChestplate() != null) p = l.getEquipment().getChestplate();
                else if(a.contains("leggings") && l.getEquipment().getLeggings() != null) p = l.getEquipment().getLeggings();
                else if(a.contains("boots") && l.getEquipment().getBoots() != null) p = l.getEquipment().getBoots();
                else if(a.contains("all")) {
                    for(ItemStack q : l.getEquipment().getArmorContents())
                        q.setDurability((short) (oldevaluate(a.split(":")[1].split("}")[0].replace("durability", Short.toString(q.getDurability())))));
                } else if(a.contains("iteminhand")) p = getItemInHand(l);
                else if(a.contains("item")) p = ev.itemWithEnchant;
                else return;
                if(p != null) {
                    a = a.replace("durability", Short.toString(p.getDurability()));
                    final double dura = oldevaluate(a.split(":")[1].split("}")[0]);
                    p.setDurability((short) (dura < 0 ? 0 : dura));
                }
            }
        } else if(a.toLowerCase().startsWith("setdroppedexp{")) {
            a = a.toLowerCase();
            int dropped = event instanceof BlockBreakEvent ? ((BlockBreakEvent) event).getExpToDrop() : event instanceof EntityDeathEvent ? ((EntityDeathEvent) event).getDroppedExp() : 0;
            final int xp = (int) oldevaluate(a.split("\\{")[1].split("}")[0].replace("droppedxp", Integer.toString(dropped)));
            if(event instanceof BlockBreakEvent)        ((BlockBreakEvent) event).setExpToDrop(xp);
            else if(event instanceof EntityDeathEvent)  ((EntityDeathEvent) event).setDroppedExp(xp);
        } else if(a.toLowerCase().startsWith("refillair{")) {
            a = a.toLowerCase();
            for(LivingEntity l : recipients)
                refillAir(l, Integer.parseInt(a.split("]")[1].split("}")[0]));
        } else if(event instanceof PlayerInteractEvent && a.toLowerCase().startsWith("breakhitblock")) {
            breakHitBlock((PlayerInteractEvent) event);
        } else if(a.toLowerCase().startsWith("sethealth{")) {
            for(LivingEntity l : recipients)
                setHealth(l, (int) oldevaluate(a.split("]")[1].split("}")[0]));
        } else if(a.toLowerCase().startsWith("smite{")) {
            final int amount = (int) oldevaluate(a.split("]")[1].split("}")[0]);
            if(a.toLowerCase().split("\\[")[1].split("]")[0].contains("arrowloc")) {
                if(event instanceof ProjectileHitEvent)
                    smite(((ProjectileHitEvent) event).getEntity().getLocation(), amount);
            }
            for(LivingEntity l : recipients)
                smite(l.getLocation(), amount);
        } else if(a.toLowerCase().startsWith("givedrops{")) {
            if(event instanceof BlockBreakEvent) {
                final BlockBreakEvent bb = (BlockBreakEvent) event;
                final Collection<ItemStack> drops = bb.getBlock().getDrops();
                for(LivingEntity l : recipients) {
                    if(l instanceof Player) {
                        for(ItemStack i : drops) {
                            giveItem((Player) l, i);
                        }
                    }
                }
                bb.setCancelled(true);
                bb.getBlock().setType(Material.AIR);
            }
        } else if(a.toLowerCase().startsWith("setxp{")) {
            final int value = (int) oldevaluate(a.toLowerCase().split("setxp\\{")[1].split("}")[0]);
            for(LivingEntity l : recipients)
                setXP(l, value);
        } else if(a.toLowerCase().startsWith("setvelocity{")) {
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
            final Vector knockback = new Vector(oldevaluate(u[0]), oldevaluate(u[1]), oldevaluate(u[2].split("}")[0]));
            for(LivingEntity l : recipients) setVelocity(l, knockback);
        } else if(a.toLowerCase().startsWith("healhunger{")) {
            final int h = (int) oldevaluate(a.split("]")[1].split("}")[0]);
            for(LivingEntity l : recipients)
                healHunger(l, h);
        } else if(a.toLowerCase().startsWith("freeze{")) {
            final int time = (int) oldevaluate(a.split("]")[1].split("}")[0]);
            for(LivingEntity l : recipients)
                freeze(l, time);
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
        } else if(a.startsWith("dropItem{")) {
            String y = a.split("dropItem\\{")[1].split("]")[1];
            if(event instanceof PlayerInteractEvent) y = y.replace("player", ((PlayerInteractEvent) event).getPlayer().getName());
            else if(event instanceof EntityDeathEvent) {
                final EntityDeathEvent e = (EntityDeathEvent) event;
                final LivingEntity v = e.getEntity(), d = v.getKiller();
                if(v instanceof Player) y = y.replace("victim", v.getName());
                if(d != null) y = y.replace("killer", d.getName());
            }
            for(LivingEntity l : recipients)
                dropItem(l, d(null, y));
        } else if(a.toLowerCase().startsWith("setgainedxp{") && mcmmoIsEnabled()) {
            a = a.toLowerCase();
            if(event instanceof com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent) {
                final com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent M = (com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent) event;
                final int xp = (int) oldevaluate(a.split("setgainedxp\\{")[1].split("}")[0].replace("xp", Integer.toString(M.getXpGained())));
                M.setRawXpGained(xp);
                M.setXpGained(xp);
            }
        } else if(a.toLowerCase().startsWith("playsound{")) {
            a = a.toLowerCase().split("]")[1];
            final String sound = a.split(":")[0];
            final int pitch = Integer.parseInt(a.split(":")[1]), volume = Integer.parseInt(a.split(":")[2]), playtimes = Integer.parseInt(a.split(":")[3]);
            final boolean globalsound = a.toLowerCase().endsWith(":true");
            for(LivingEntity l : recipients)
                playSound(sound, pitch, volume, l, playtimes, globalsound);
        } else if(a.toLowerCase().startsWith("spawnentity{")) {
            final String[] s = a.split("]")[1].split("}")[0].split(":");
            final CustomEnchantEntity e = CustomEnchantEntity.paths.get(s[0]);
            if(e != null) {
                for(LivingEntity l : recipients) {
                    e.spawn(l, getRecipient(event, s[2]), event);
                }
            }
        } else if(a.toLowerCase().startsWith("stopenchant{")) {
            final String J = a.split("]")[1].split("}")[0];
            final int seconds = Integer.parseInt(J.split(":")[1]);
            for(LivingEntity l : recipients) {
                if(l instanceof Player) {
                    final Player p = (Player) l;
                    if(J.toLowerCase().startsWith("all")) {
                        stoppedAllEnchants.add(p);
                        scheduler.scheduleSyncDelayedTask(randompackage, () -> stoppedAllEnchants.remove(p), 20*seconds);
                    } else {
                        final CustomEnchant ce = CustomEnchant.valueOf(J.split(":")[0]);
                        if(ce != null) {
                            if(!stoppedEnchants.keySet().contains(p)) stoppedEnchants.put(p, new HashMap<>());
                            if(stoppedEnchants.get(p).keySet().contains(ce)) {
                                scheduler.cancelTask(stoppedEnchants.get(p).get(ce));
                            }
                            int task = scheduler.scheduleSyncDelayedTask(randompackage, () -> stoppedEnchants.get(p).remove(ce), 20*seconds);
                            stoppedEnchants.get(p).put(ce, task);
                        }
                    }
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
        } else if(a.toLowerCase().startsWith("remove{")) {
            for(LivingEntity l : recipients)
                if(!(l instanceof Player))
                    l.remove();
        } else if(a.toLowerCase().startsWith("replaceblock{")) {
            final String args = a.toLowerCase().split("\\{")[1].split("}")[0];
            final String[] aa = args.split(":");
            final World w = player.getWorld();
            final int x = (int) oldevaluate(aa[0]), y = (int) oldevaluate(aa[1]), z = (int) oldevaluate(aa[2]);
            final Location l = new Location(w, x, y, z);
            final Material type = Material.valueOf(aa[3].toUpperCase());
            final Byte data = Byte.parseByte(aa[4]);
            final int ticks = Integer.parseInt(aa[5]);
            setTemporaryBlock(l, type, data, ticks);
        } else if(a.toLowerCase().startsWith("stealxp{")) {
            final String arg = a.split("\\{")[1].split("}")[0];
            final String[] aa = arg.split(":");
            final LivingEntity receiver = getRecipient(event, aa[0]), target = getRecipient(event, aa[1]);
            final int amount = Integer.parseInt(aa[2]);
            if(receiver != null && receiver instanceof Player && target != null && target instanceof Player) {

            }
        } else if(a.toLowerCase().startsWith("depleteraritygem{")) {
            final FactionUpgrades fu = FactionUpgrades.getFactionUpgrades();
            if(fu.isEnabled()) {
                final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
                final RarityGem gem = getRarityGem(a.split("\\{")[1].split(":")[0]);
                if(!pdata.hasActiveRarityGem(gem)) {
                    ev.didProc = false;
                    return;
                }
                final ItemStack g = getRarityGem(gem, player);
                if(g != null) {
                    itemMeta = g.getItemMeta();
                    final int amount = getRemainingInt(itemMeta.getDisplayName());
                    final String fn = fapi.getFaction(player);
                    int depleteAmount = Integer.parseInt(a.split(":")[1].split("}")[0]);
                    depleteAmount -= depleteAmount*fu.getDecreaseRarityGemPercent(fn, gem);
                    if(amount - depleteAmount <= 0) {
                        depleteAmount = amount;
                        pdata.toggleRarityGem(ev, gem);
                    }
                    itemMeta = g.getItemMeta();
                    itemMeta.setDisplayName(gem.getItem().getItemMeta().getDisplayName().replace("{SOULS}", Integer.toString(amount - depleteAmount)));
                    g.setItemMeta(itemMeta);
                    player.updateInventory();
                }
            }
        } else if(a.toLowerCase().startsWith("depletestacksize{") && event instanceof MobStackDepleteEvent) {
            final int amount = Integer.parseInt(a.split("\\{")[1].split("}")[0]);
            ((MobStackDepleteEvent) event).amount = amount;
        } else if(a.toLowerCase().startsWith("createcombo{")) {
            final String path = a.split("\\{")[1].split("}")[0];
            final CustomEnchant n = CustomEnchant.valueOf(path.split(":")[0]);
            createCombo(player, n, Double.parseDouble(path.split(":")[1]));
        } else if(a.toLowerCase().startsWith("addcombo{")) {
            final String path = a.split("\\{")[1].split("}")[0];
            final CustomEnchant n = CustomEnchant.valueOf(path.split(":")[0]);
            addCombo(player, n, Double.parseDouble(path.split(":")[1]));
        } else if(a.toLowerCase().startsWith("depletecombo{")) {
            final String path = a.split("\\{")[1].split("}")[0];
            final CustomEnchant n = CustomEnchant.valueOf(path.split(":")[0]);
            depleteCombo(player, n, Double.parseDouble(path.split(":")[1]));
        } else if(a.toLowerCase().startsWith("stopcombo{")) {
            final CustomEnchant n = CustomEnchant.valueOf(a.split("\\{")[1].split("}")[0]);
            stopCombo(player, n);
        } else if(a.toLowerCase().startsWith("explode{")) {
            final Location l = getRecipientLoc(event, a.split("\\[")[1].split("]")[0]);
            if(l != null)
                explode(l, Float.parseFloat(a.split("]")[1].split(":")[0]), Boolean.parseBoolean(a.split(":")[1]), Boolean.parseBoolean(a.split(":")[2].split("}")[0]));
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
        if(input.startsWith("isHolding("))      return isHolding(entity, input.split("\\(")[1].split("\\)")[0]);
        else if(input.startsWith("isBlocking")) return isBlocking(entity);
        else if(input.startsWith("healthIs<=:")) return healthIsLessThanOrEqualTo(entity, oldevaluate(input.split(":")[1].split(":")[0]));
        else if(input.startsWith("healthIs>=:")) return healthIsGreaterThanOrEqualTo(entity, oldevaluate(input.split(":")[1].split(":")[0]));
        else if(input.startsWith("isUnderwater")) return entity.getRemainingAir() < entity.getMaximumAir();
        else if(input.startsWith("hitBlock(")) return event instanceof PlayerInteractEvent && hitBlock((PlayerInteractEvent) event, input.split("hitBlock\\(")[1].split("\\)")[0].toUpperCase());
        else if(input.startsWith("canBreakHitBlock")) return event instanceof PlayerInteractEvent && ((PlayerInteractEvent) event).getClickedBlock() != null && fapi.canModify(((PlayerInteractEvent) event).getPlayer(), ((PlayerInteractEvent) event).getClickedBlock().getLocation());
        else if(input.startsWith("isHeadshot")) {
            final PvAnyEvent eve = event instanceof PvAnyEvent ? (PvAnyEvent) event : null;
            final Projectile p = eve != null ? eve.proj : null;
            return eve != null && p instanceof Arrow && p.getLocation().getY() > eve.victim.getEyeLocation().getY();
        } else if(input.startsWith("isSneaking")) return entity instanceof Player && ((Player) entity).isSneaking();
        else if(input.startsWith("didproc")) return e.didProc;
        else if(input.startsWith("didntproc")) return !e.didProc;
        else if(input.toLowerCase().startsWith("fromspawner")) return spawnedFromSpawner.contains(entity.getUniqueId());
        else if(input.startsWith("enchantIs(")) {
            if(enchant != null) {
                final String inpu = input.split("enchantIs\\(")[1].split("\\\\")[0];
                for(String s : inpu.split("\\|\\|")) if(s.equals(enchant.getName())) return true;
            }
            return false;
        } else if(input.equals("hitCEEntity")) {
            if(event instanceof PvAnyEvent) {
                final LivingEntity victim = ((PvAnyEvent) event).victim;
                return victim != null && LivingCustomEnchantEntity.living.getOrDefault(victim.getUniqueId(), null) != null;
            }
        } else if(input.toLowerCase().startsWith("eval{") && event instanceof CEAApplyPotionEffectEvent) {
            final CEAApplyPotionEffectEvent A = (CEAApplyPotionEffectEvent) event;
            final int enchantlevel = A.enchantlevel, potionlevel = A.potioneffect.getAmplifier() + 1;
            String f = input.toLowerCase().split("eval\\{")[1].split("}")[0].replace("level", Integer.toString(enchantlevel)).replace("potionlevel", Integer.toString(potionlevel)),
                    s = input.toLowerCase().split("eval\\{")[2].split("}")[0].replace("level", Integer.toString(enchantlevel)).replace("potionlevel", Integer.toString(potionlevel));

        } else if(input.toLowerCase().startsWith("distancebetween(")) {
            final List<LivingEntity> recipients = getRecipients(event, input.split("distanceBetween\\(")[1].split("\\)")[0], null);
            if(recipients.size() == 2)
                return input.split("distanceBetween\\(")[1].split("\\)")[1].startsWith("<=") ? distanceBetween(recipients.get(0), recipients.get(1)) <= oldevaluate(input.split("\\)<=")[1]) : distanceBetween(recipients.get(0), recipients.get(1)) >= oldevaluate(input.split("\\)>=")[1]);
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
    private Location getRecipientLoc(Event event, String input) {
        if(event instanceof PlayerArmorEvent) {
            return ((PlayerArmorEvent) event).player.getLocation();
        } else if(event instanceof PlayerInteractEvent) {
            return ((PlayerInteractEvent) event).getPlayer().getLocation();
        } else if(event instanceof ProjectileHitEvent) {
            final ProjectileHitEvent e = (ProjectileHitEvent) event;
            if(input.toLowerCase().contains("arrow") && e.getEntity() instanceof Arrow) return e.getEntity().getLocation();
            if(input.toLowerCase().contains("shooter") && e.getEntity().getShooter() instanceof LivingEntity) return ((LivingEntity) e.getEntity().getShooter()).getLocation();
        } else if(event instanceof PvAnyEvent) {
            final PvAnyEvent e = (PvAnyEvent) event;
            final LivingEntity v = e.victim;
            final Projectile pro = e.proj;
            if(input.contains("damager")) return e.proj != null ? e.proj.getLocation() : e.damager.getLocation();
            if(input.contains("victim") && v != null) return v.getLocation();
            if(input.contains("shooter") && pro != null) return ((LivingEntity) pro.getShooter()).getLocation();
        }
        return null;
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
    public HashMap<ItemStack, HashMap<CustomEnchant, Integer>> getEnchants(Player player) { // <Inventory Slot, <FileCustomEnchant, FileCustomEnchant level>>
        final HashMap<ItemStack, HashMap<CustomEnchant, Integer>> L = new HashMap<>();
        if(player != null) {
            final PlayerInventory pi = player.getInventory();
            final ItemStack p = pi.getItem(pi.getHeldItemSlot());
            if(p != null) {
                L.put(p, getEnchants(p));
            }
            for(ItemStack is : pi.getArmorContents()) L.put(is, getEnchants(is));
        }
        return L;
    }
    public HashMap<CustomEnchant, Integer> getEnchants(ItemStack is) { // <FileCustomEnchant, FileCustomEnchant level>
        final HashMap<CustomEnchant, Integer> enchants = new HashMap<>();
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            for(String s : is.getItemMeta().getLore()) {
                final CustomEnchant e = CustomEnchant.valueOf(s);
                if(e != null) enchants.put(e, getEnchantmentLevel(s));
            }
        }
        return enchants;
    }
    public boolean isOnCorrectItem(CustomEnchant enchant, ItemStack is) {
        final String i = is != null ? is.getType().name() : null;
        if(enchant != null && i != null) for(String s : enchant.getAppliesTo()) if(i.endsWith(s.toUpperCase())) return true;
        return false;
    }
    public boolean canProcOn(Entity e) {
        return config.getStringList("settings.can proc on").contains(e.getType().name());
    }
    /*
        ATTRIBUTES
     */
    private void wait(CustomEnchantProcEvent ev, Event event, CustomEnchant enchant, String attribute, int number, int ticks, Player P) {
        String t = "";
        for(int i = number+1; i < attribute.split(";").length; i++)
            t = t + attribute.split(";")[i] + ";";
        final String w = t;
        scheduler.scheduleSyncDelayedTask(randompackage, () -> {
            int b = number;
            for(String s : w.split(";")) {
                b += 1;
                executeAttribute(ev, event, enchant, s, attribute, b, P);
            }
        }, ticks);
    }
    public void createCombo(Player player, CustomEnchant source, double base) {
        if(!combos.keySet().contains(player)) combos.put(player, new HashMap<>());
        if(!combos.get(player).keySet().contains(source)) combos.get(player).put(source, base);
    }
    public void addCombo(Player player, CustomEnchant source, double addition) {
        if(!combos.containsKey(player)) combos.put(player, new HashMap<>());
        final double prev = combos.get(player).getOrDefault(source, 0.00);
        combos.get(player).put(source, prev+addition);
    }
    public void depleteCombo(Player player, CustomEnchant source, double depletion) {
        if(combos.containsKey(player) && combos.get(player).containsKey(source)) {
            final double d = combos.get(player).get(source), de = d-depletion;
            combos.get(player).put(source, de < 0.00 ? 0.00 : round(de, 2));
        }
    }
    public void stopCombo(Player player, CustomEnchant source) {
        if(combos.containsKey(player) && combos.get(player).containsKey(source)) {
            combos.get(player).remove(source);
        }
    }
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
    private void damage(LivingEntity entity, double damage, boolean heartDmg) {
        if(entity != null) {
            final double h = entity.getHealth();
            if(!heartDmg && h-damage <= 0.00 || h-((int) damage) <= 0.00) {
                damage = h;
            }
            entity.damage(heartDmg ? ((int) damage) : damage);
        }
    }
    private void explode(Location l, float power, boolean setFire, boolean breakBlocks) {
        l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), power, setFire, breakBlocks);
    }
    private void heal(LivingEntity entity, double by) {
        if(entity.getHealth() != entity.getMaxHealth()) {
            entity.setHealth(entity.getHealth() + by > entity.getMaxHealth() ? entity.getMaxHealth() : entity.getHealth() + by);
        }
    }
    private void healHunger(LivingEntity entity, int by) {
        final Player player = entity instanceof Player ? (Player) entity : null;
        if(player != null && player.getFoodLevel() + by <= 20) {
            player.setFoodLevel(player.getFoodLevel() + by);
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
    private void setHealth(LivingEntity entity, double newHealth) {
        if(newHealth < 0.00) entity.setHealth(0.00);
        else entity.setHealth(newHealth);
    }
    private boolean isHolding(LivingEntity entity, String input) {
        final ItemStack i = getItemInHand(entity);
        return i != null && i.getType().name().endsWith(input.toUpperCase());
    }
    private boolean isBlocking(LivingEntity entity) { return entity instanceof Player && ((Player) entity).isBlocking(); }
    private boolean healthIsLessThanOrEqualTo(LivingEntity entity, double target) { return entity.getHealth() <= target; }
    private boolean healthIsGreaterThanOrEqualTo(LivingEntity entity, double target) { return entity.getHealth() >= target; }
    private boolean hitBlock(PlayerInteractEvent event, String block) {
        final Block c = event.getClickedBlock();
        return c != null && c.getType().name().equals(block.toUpperCase());
    }
    private void breakHitBlock(PlayerInteractEvent event) { event.getClickedBlock().breakNaturally(); }
    private void refillAir(LivingEntity entity, int addedAir) {
        final int r = entity.getRemainingAir(), m = entity.getMaximumAir();
        entity.setRemainingAir(r + addedAir > m ? m : r + addedAir);
    }
    private int getXP(LivingEntity entity) { return entity instanceof Player ? getTotalExperience((Player) entity) : 0; }
    private void setXP(LivingEntity entity, int value) {
        if(entity instanceof Player) {
            setTotalExperience((Player) entity, value);
        }
    }
    private void dropItem(LivingEntity entity, ItemStack is) { entity.getWorld().dropItem(entity.getLocation(), is); }
    private void particle(String particle, Location location, boolean global) {

    }
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
        /*  Code is from "Adrian Sohn" at https://stackoverflow.com/questions/35831619/get-the-direction-a-player-is-looking */
        float yaw = entity.getLocation().getYaw();
        if (yaw < 0) yaw += 360;
        return yaw >= 314 || yaw < 45 ? "SOUTH" : yaw < 135 ? "WEST" : yaw < 225 ? "NORTH" : yaw < 315 ? "EAST" : "NORTH";
    }
    private double distanceBetween(Entity e1, Entity e2) {
        return e1.getLocation().distance(e2.getLocation());
    }
    private void sendMessage(CustomEnchant enchant, LivingEntity entity, String message) {
        if(message.contains("\\n")) for(String s : message.split("\\\\n")) entity.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("}", "").replace("%ENCHANT%", enchant.getName())));
        else                                                               entity.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("}", "").replace("%ENCHANT%", enchant.getName())));
    }
    private void smite(Location loc, int times) {
        for(int i = 1; i <= times; i++)
            loc.getWorld().strikeLightning(loc);
    }
    private void setVelocity(LivingEntity entity, Vector vel) {
        entity.setVelocity(vel);
    }
    private void stopEnchant(Player player, String input, int ticks) {
        final HashMap<ItemStack, HashMap<CustomEnchant, Integer>> enchants = getEnchants(player);
        if(input.toLowerCase().equals("all")) {
        }
    }
    private void freeze(LivingEntity entity, int ticks) {
        if(entity instanceof Player) {
            final Player player = (Player) entity;
            final float walkspeed = player.getWalkSpeed();
            frozen.add(player);
            player.setWalkSpeed(0);
            scheduler.scheduleSyncDelayedTask(randompackage, () -> {
                player.setWalkSpeed(walkspeed);
                frozen.remove(player);
            }, ticks);
        }
    }
}
