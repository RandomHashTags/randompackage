package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.CustomBoss;
import me.randomhashtags.randompackage.addon.obj.CustomBossAttack;
import me.randomhashtags.randompackage.addon.obj.CustomMinion;
import me.randomhashtags.randompackage.event.mob.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.event.mob.CustomBossDeathEvent;
import me.randomhashtags.randompackage.event.mob.CustomBossSpawnEvent;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public final class LivingCustomBoss implements RPFeatureSpigot, UVersionableSpigot {
    public static HashMap<UUID, LivingCustomBoss> LIVING;

    public LivingEntity summoner, entity;
    public CustomBoss type;
    public List<LivingCustomMinion> minions;
    public HashMap<UUID, Double> damagers;

    public LivingCustomBoss(LivingEntity summoner, LivingEntity entity, CustomBoss type) {
        if(LIVING == null) {
            LIVING = new HashMap<>();
        }
        minions = new ArrayList<>();
        damagers = new HashMap<>();
        this.summoner = summoner;
        this.entity = entity;
        this.type = type;
        final HashMap<Integer, List<String>> messages = type.getMessages();
        final int messageRadius = type.getMessageRadius();
        final Location L = entity.getLocation();
        final String X = Integer.toString(L.getBlockX()), Y = Integer.toString(L.getBlockY()), Z = Integer.toString(L.getBlockZ());
        for(String s : messages.get(-4)) {
            s = s.replace("{X}", X).replace("{Y}", Y).replace("{Z}", Z);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
        for(String s : messages.get(-5)) {
            s = ChatColor.translateAlternateColorCodes('&', s.replace("{SUMMONER}", summoner.getName()));
            summoner.sendMessage(s);
            for(Entity en : summoner.getNearbyEntities(messageRadius, messageRadius, messageRadius)) if(en instanceof Player) en.sendMessage(s);
        }
        for(PotionEffect t : entity.getActivePotionEffects()) {
            entity.removePotionEffect(t.getType());
        }
        entity.setCustomName(type.getName());
        entity.setCustomNameVisible(true);
        for(String s : type.getAttributes()) {
            final String d = s.toLowerCase();
            if(d.startsWith("maxhealth=")) {
                entity.setMaxHealth(Double.parseDouble(d.split("=")[1]));
                entity.setHealth(entity.getMaxHealth());
            } else if(d.startsWith("addpotioneffect")) {
                final String p = d.split("\\{")[1].split("}")[0];
                final PotionEffectType t = get_potion_effect_type(p.split(":")[0].toUpperCase());
                if(t != null) {
                    entity.addPotionEffect(new PotionEffect(t, Integer.parseInt(p.split(":")[1]), Integer.parseInt(p.split(":")[2])));
                }
            } else if(d.startsWith("size=")) {
                if(entity instanceof Slime) {
                    final int r = Integer.parseInt(d.split("=")[1]);
                    ((Slime) entity).setSize(r);
                }
            }
        }
        if(entity instanceof Creature) {
            ((Creature) entity).setTarget(summoner);
        }
        updateScoreboards(summoner, 0);
        final CustomBossSpawnEvent e = new CustomBossSpawnEvent(summoner, entity.getLocation(), this);
        PLUGIN_MANAGER.callEvent(e);
        LIVING.put(entity.getUniqueId(), this);
    }

    @Override
    public @NotNull String getIdentifier() {
        return null;
    }
    @Override
    public void load() {
    }
    @Override
    public void unload() {
    }

    public void damage(@NotNull LivingEntity customboss, Entity damager, double damage) {
        final CustomBossDamageByEntityEvent e = new CustomBossDamageByEntityEvent(customboss, damager, damage);
        PLUGIN_MANAGER.callEvent(e);
        if(!e.isCancelled()) {
            final double d = e.getDamage();
            final HashMap<Integer, List<String>> messages = type.getMessages();
            UUID i = null;
            if(damager instanceof Player) {
                i = damager.getUniqueId();
            } else if(damager instanceof Projectile) {
                final ProjectileSource ps = ((Projectile) damager).getShooter();
                if(ps instanceof Player) i = ((Player) ps).getUniqueId();
            }
            if(i != null) {
                final double prev = damagers.getOrDefault(i, 0.00);
                damagers.put(i, prev+d);
            }
            updateScoreboards(customboss, d);
            final int maxMinions = type.getMaxMinions();
            for(CustomBossAttack boss_attack : type.getAttacks()) {
                if(RANDOM.nextInt(100) <= boss_attack.getChance()) {
                    final int boss_attack_radius = boss_attack.getRadius();
                    for(String attack : boss_attack.getAttacks()) {
                        final String attackLowercase = attack.toLowerCase();
                        if(attackLowercase.startsWith("delay=")) {
                            final int r = Integer.parseInt(attackLowercase.split("delay=")[1].split("\\{")[0]);
                            final List<Location> locations = new ArrayList<>();
                            for(Entity entity : customboss.getNearbyEntities(boss_attack_radius, boss_attack_radius, boss_attack_radius)) {
                                if(entity instanceof Player) {
                                    locations.add(entity.getLocation());
                                }
                            }

                            SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> {
                                String ss = attackLowercase.replace("delay=" + r, ""), sss = null;
                                int x1, y1, z1, x2, y2, z2, a;
                                List<String> message;
                                if(ss.startsWith("{surround")) {
                                    sss = ss.replace("{surround:", "").replace("}", "");
                                    final String[] values = sss.split(":");
                                    x1 = Integer.parseInt(values[0]);
                                    y1 = Integer.parseInt(values[1]);
                                    z1 = Integer.parseInt(values[2]);
                                    x2 = Integer.parseInt(values[3]);
                                    y2 = Integer.parseInt(values[4]);
                                    z2 = Integer.parseInt(values[5]);
                                    final Material replaceMaterial = UMaterial.match(values[6]).getMaterial(), replacedMaterial = UMaterial.match(values[7]).getMaterial();
                                    for(Location l : locations) {
                                        Bukkit.dispatchCommand(CONSOLE, "minecraft:fill " + (l.getBlockX() + x1) + " " + (l.getBlockY() + y1) + " " + (l.getBlockZ() + z1) + " " + (l.getBlockX() + x2) + " " + (l.getBlockY() + y2) + " " + (l.getBlockZ() + z2) + " " + replaceMaterial.name().toLowerCase() + (LEGACY ? " " + replaceMaterial.getData() : "") + " replace " + replacedMaterial.name().toLowerCase());
                                    }
                                } else {
                                    String replace = ss.replace("{", "").replace("}", "").replace("/", "");
                                    if(ss.startsWith("{/effect")) {
                                        for(Entity entity : customboss.getNearbyEntities(boss_attack_radius, boss_attack_radius, boss_attack_radius)) {
                                            if(entity instanceof Player) {
                                                Bukkit.dispatchCommand(entity, "/effect " + (LEGACY ? "" : "give ") + replace.replace("~player", entity.getName()));
                                            }
                                        }
                                    } else if(ss.startsWith("{/")) {
                                        for(Entity entity : customboss.getNearbyEntities(boss_attack_radius, boss_attack_radius, boss_attack_radius)) {
                                            if(entity instanceof Player) {
                                                Bukkit.dispatchCommand(entity, replace.replace("~player", entity.getName()));
                                            }
                                        }
                                    } else {
                                        if(ss.startsWith("{message")) {
                                            for(String v  : messages.get(Integer.parseInt(ss.split("message")[1].split("}")[0]))) {
                                                for(Entity entity : customboss.getNearbyEntities(boss_attack_radius, boss_attack_radius, boss_attack_radius)) {
                                                    entity.sendMessage(ChatColor.translateAlternateColorCodes('&', v));
                                                }
                                            }
                                        } else if (ss.startsWith("{summonminions")) {
                                            spawnMinions(customboss, maxMinions);
                                        } else if (ss.startsWith("{minionheal")) {
                                            a = 0;
                                            final int healby = Integer.parseInt(ss.split(":")[1]), ra = Integer.parseInt(ss.split(":")[2]);
                                            message = messages.get(Integer.parseInt(ss.split(":")[3].replace("message", "").replace("}", "")));
                                            for(LivingCustomMinion minion : minions) {
                                                final double hp = customboss.getHealth(), maxhp = customboss.getMaxHealth();
                                                customboss.setHealth(hp+healby > maxhp ? maxhp : hp+healby);
                                                a += healby;
                                            }
                                            if(a != 0) {
                                                for(Entity entity : customboss.getNearbyEntities(ra, ra, ra)) {
                                                    for(String g : message) {
                                                        if(g.contains("{HP}")) g = g.replace("{HP}", Integer.toString(a));
                                                        entity.sendMessage(ChatColor.translateAlternateColorCodes('&', g));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }, r);
                        }
                    }
                }
            }
        }
    }
    public HashMap<UUID, Double> getDamageRankings() {
        return damagers.entrySet().stream().sorted(Map.Entry.<UUID, Double> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
    public void kill(LivingEntity l, EntityDamageEvent damagecause) {
        final CustomBossDeathEvent ev = new CustomBossDeathEvent(this);
        PLUGIN_MANAGER.callEvent(ev);
        if(!ev.isCancelled()) {
            final HashMap<Integer, List<String>> messages = type.getMessages();
            final int messageRadius = type.getMessageRadius();
            for(Entity e : l.getNearbyEntities(messageRadius, messageRadius, messageRadius)) {
                if(e instanceof Player) {
                    ((Player) e).setScoreboard(SCOREBOARD_MANAGER.getNewScoreboard());
                }
            }
            double totaldamage = 0.00;
            for(UUID d : damagers.keySet()) totaldamage += damagers.get(d);
            final HashMap<UUID, Double> top = getDamageRankings();
            final HashMap<Integer, List<ItemStack>> rewards = new HashMap<>();
            final HashMap<Integer, List<String>> rewardNames = new HashMap<>();
            for(String s : type.getRewards()) {
                final int target = getRemainingInt(s.split(";")[0]), amount = getRemainingInt(s.split(";")[s.split(";").length-1]);
                if(!rewards.containsKey(target)) {
                    rewards.put(target, new ArrayList<>());
                    rewardNames.put(target, new ArrayList<>());
                }
                final ItemStack reward = createItemStack(null, s.split("\\{")[1].split("}")[0]);
                reward.setAmount(amount);
                rewards.get(target).add(reward);
                rewardNames.get(target).add(ChatColor.translateAlternateColorCodes('&', s.split(";")[3]));
            }
            for(int i : rewards.keySet()) {
                if(i-1 < top.size()) {
                    final OfflinePlayer p = Bukkit.getOfflinePlayer((UUID) top.keySet().toArray()[i-1]);
                    if(p.isOnline()) {
                        for(ItemStack re : rewards.get(i)) {
                            giveItem(p.getPlayer(), re);
                        }
                    }
                }
            }
            LIVING.remove(entity.getUniqueId());
            final List<String> topdamagers = new ArrayList<>();
            for(int i = 0; i < 10 && i < top.size(); i++) {
                final String OP = Bukkit.getOfflinePlayer((UUID) top.keySet().toArray()[i]).getName();
                if(!topdamagers.contains(OP)) topdamagers.add(OP);
            }
            for(String s : messages.get(-2)) {
                if(s.contains("{TOP_PLAYERS}")) s = s.replace("{TOP_PLAYERS}", topdamagers.toString().substring(1, topdamagers.toString().length()-1));
                for(int i = 1; i <= 10; i++) {
                    final boolean a = topdamagers.size() > i-1;
                    if(s != null && s.contains("{TOP" + i + "}")) {
                        s = a ? s.replace("{TOP" + i + "}", topdamagers.get(i-1)) : null;
                    }
                    if(s != null && s.contains("{TOP" + i + "%}")) {
                        s = a ? s.replace("{TOP" + i + "%}", roundDoubleString((((double) top.values().toArray()[i-1])/totaldamage)*100, 2)) : null;
                    }
                    if(s != null && s.contains("{REWARDS}")) {
                        s = s.replace("{REWARDS}", rewardNames.get(i).toString().substring(1, rewardNames.get(i).toString().length()-1));
                    }
                }
                if(s != null) {
                    Bukkit.broadcastMessage(center(ChatColor.translateAlternateColorCodes('&', s), 60));
                }
            }
            for(String s : messages.get(-3)) {
                s = ChatColor.translateAlternateColorCodes('&', s);
                for(Entity e : l.getNearbyEntities(messageRadius, messageRadius, messageRadius))
                    if(e instanceof Player)
                        e.sendMessage(s);
            }
        }
    }
    public void spawnMinions(LivingEntity boss, int amount) {
        if(boss == null || boss.isDead() || minions.size() + amount > type.getMaxMinions()) {
            return;
        }
        final int messageRadius = type.getMessageRadius();
        LivingEntity target = entity instanceof Creature ? ((Creature) entity).getTarget() : null;
        if(target == null) {
            for(Entity e : boss.getNearbyEntities(messageRadius, messageRadius, messageRadius)) {
                if(target == null && e instanceof Player) {
                    target = (LivingEntity) e;
                }
            }
        }
        final CustomMinion minion = type.getMinion();
        if(target != null) {
            final LivingCustomMinion l = new LivingCustomMinion(getEntity(minion.type, entity.getLocation(), true), target, minion, this);
            minions.add(l);
        }
    }
    private void updateScoreboards(LivingEntity boss, double dmg) {
        final String g = formatDouble(round(boss.getHealth()-dmg, 2)), m = formatInt(minions.size());
        final int messageRadius = type.getMessageRadius();
        final Scoreboard src = type.getScoreboard();
        final List<String> entries = type.getScores();

        final Objective srcObj = src.getObjective("dummy");
        final DisplaySlot ds = srcObj.getDisplaySlot();
        final String dn = srcObj.getDisplayName();
        for(Entity e : boss.getNearbyEntities(messageRadius, messageRadius, messageRadius)) {
            if(e instanceof Player) {
                final Scoreboard sb = SCOREBOARD_MANAGER.getNewScoreboard();
                sb.registerNewObjective("dummy", "dummy");
                final Objective obj = sb.getObjective("dummy");
                obj.setDisplaySlot(ds);
                obj.setDisplayName(dn);

                final UUID u = e.getUniqueId();
                int h = 15;
                for(String s : entries) {
                    s = s.replace("{HEALTH}", g).replace("{MINIONS}", m);
                    s = s.replace("{DAMAGE_DEALT}", formatDouble(round(damagers.getOrDefault(u, 0.00), 0)));
                    s = s.replace("{DAMAGE_DEALT%}", roundDoubleString(getDamagePercentDone(u), 1));
                    obj.getScore(ChatColor.translateAlternateColorCodes('&', s)).setScore(h);
                    h -= 1;
                }
                ((Player) e).setScoreboard(sb);
            }
        }
    }
    public double getDamagePercentDone(UUID damager) {
        double total = 0.00, dmg = 0.00;
        if(damagers.containsKey(damager)) {
            for(UUID u : damagers.keySet()) {
                final double d = damagers.get(u);
                total += d;
                if(u.equals(damager)) dmg = d;
            }
        }
        return total != 0.00 ? (dmg/total)*100 : 0.00;
    }
}