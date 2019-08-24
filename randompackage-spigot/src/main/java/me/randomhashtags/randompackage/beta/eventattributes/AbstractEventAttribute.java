package me.randomhashtags.randompackage.beta.eventattributes;

import me.randomhashtags.randompackage.beta.EventAttributeCallEvent;
import me.randomhashtags.randompackage.utils.RPStorage;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class AbstractEventAttribute extends RPStorage implements EventAttribute, Listener {
    private boolean cancelled;
    private HashMap<String, Entity> recipients;
    private static List<UUID> spawnedFromSpawner = new ArrayList<>();

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public void load() { addEventAttribute(getIdentifier(), this); }
    public boolean didPassConditions(List<String> conditions) {
        boolean passed = true;
        final HashMap<String, Entity> entities = new HashMap<>();
        for(String r : recipients.keySet()) entities.put(r.toLowerCase(), recipients.get(r));

        final boolean eight = version.contains("1.8"), nine = version.contains("1.9"), ten = version.contains("1.10"), eleven = version.contains("1.11"), thirteen = version.contains("1.13"), legacy = isLegacy;
        for(String c : conditions) {
            final String condition = c.toLowerCase();
            if(condition.startsWith("chance=")) {
                passed = random.nextInt(100) < getRemainingInt(condition.split("=")[1]);
            }
            if(passed) {
                for(String s : entities.keySet()) {
                    final Entity e = entities.get(s);
                    String value = null;
                    try {
                        value = condition.split("=")[1];
                    } catch (Exception ignored) {}

                    if(condition.startsWith(s)) {
                        if(condition.equals(s + "isfromspawner=")) {
                            passed = spawnedFromSpawner.contains(e.getUniqueId()) == Boolean.parseBoolean(value);
                        } else if(condition.equals(s + "isplayer")) {
                            passed = e instanceof Player;
                        } else if(condition.equals(s + "!isplayer")) {
                            passed = !(e instanceof Player);
                        } else if(condition.equals(s + "ismob")) {
                            passed = e instanceof Mob;
                        } else if(condition.equals(s + "iscreature")) {
                            passed = e instanceof Creature;
                        } else if(condition.startsWith(s + "istype=")) {
                            passed = e.getType().name().toLowerCase().equals(value);
                        } else if(condition.startsWith(s + "isfacing=")) {
                            passed = e.getFacing().name().toLowerCase().startsWith(value);
                        } else if(condition.startsWith(s + "isop=")) {
                            passed = e.isOp() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isinsidevehicle=")) {
                            passed = e.isInsideVehicle() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "iscustomnamevisible=")) {
                            passed = e.isCustomNameVisible() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isonground=")) {
                            passed = e.isOnGround() == Boolean.parseBoolean(value);
                        /*
                            Ageable conditions
                         */
                        } else if(condition.startsWith(s + "isadult=")) {
                            passed = e instanceof Ageable && ((Ageable) e).isAdult() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isbaby=")) {
                            passed = e instanceof Zombie && ((Zombie) e).isBaby() || e instanceof Ageable && ((Ageable) e).isAdult() != Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "canbreed=")) {
                            passed = e instanceof Ageable && ((Ageable) e).canBreed() == Boolean.parseBoolean(value);
                        /*
                            Animals conditions
                         */
                        } else if(condition.startsWith(s + "inlovemode=")) {
                            passed = !legacy && !thirteen && e instanceof Animals && ((Animals) e).isLoveMode() == Boolean.parseBoolean(value);
                        /*
                            ArmorStand conditions
                         */
                        } else if(condition.startsWith(s + "hasbaseplate=")) {
                            passed = e instanceof ArmorStand && ((ArmorStand) e).hasBasePlate() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "hasarms=")) {
                            passed = e instanceof ArmorStand && ((ArmorStand) e).hasArms() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "ismarker=")) {
                            passed = e instanceof ArmorStand && ((ArmorStand) e).isMarker() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "issmall=")) {
                            passed = e instanceof ArmorStand && ((ArmorStand) e).isSmall() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isvisible=")) {
                            passed = e instanceof ArmorStand && ((ArmorStand) e).isVisible() == Boolean.parseBoolean(value);
                        /*
                            Bat conditions
                         */
                        } else if(condition.startsWith(s + "isawake=")) {
                            passed = e instanceof Bat && ((Bat) e).isAwake() == Boolean.parseBoolean(value);
                        /*
                            Cat conditions
                         */
                        } else if(condition.startsWith(s + "cattype=")) {
                            passed = isLegacy ? e instanceof Ocelot && ((Ocelot) e).getCatType().name().equalsIgnoreCase(value) : e instanceof Cat && ((Cat) e).getCatType().name().equalsIgnoreCase(value);
                        } else if(condition.startsWith(s + "collarcolor=")) {
                            if(e instanceof Wolf) {
                                passed = ((Wolf) e).getCollarColor().name().equalsIgnoreCase(value);
                            } else if(!legacy && !thirteen) {
                                passed = e instanceof Cat && ((Cat) e).getCollarColor().name().equalsIgnoreCase(value);
                            } else {
                                passed = false;
                            }

                        /*
                            ChestedHorse conditions
                         */
                        } else if(condition.startsWith(s + "iscarryingchest=")) {
                            if(eight || nine || ten) {
                                passed = e instanceof Horse && ((Horse) e).isCarryingChest() == Boolean.parseBoolean(value);
                            } else {
                                passed = e instanceof ChestedHorse && ((ChestedHorse) e).isCarryingChest() == Boolean.parseBoolean(value);
                            }
                        /*
                            Creeper conditions
                         */
                        } else if(condition.startsWith(s + "ispowered=")) {
                            passed = e instanceof Creeper && ((Creeper) e).isPowered() == Boolean.parseBoolean(value);
                        /*
                            EnderCrystal conditions
                         */
                        } else if(condition.startsWith(s + "isshowingbottom=")) {
                            passed = eight ? true : e instanceof EnderCrystal && ((EnderCrystal) e).isShowingBottom() == Boolean.parseBoolean(value);
                        /*
                            EnderDragon conditions
                         */
                        } else if(condition.startsWith(s + "phase=")) {
                            passed = eight ? true : e instanceof EnderDragon && ((EnderDragon) e).getPhase().name().equalsIgnoreCase(value);
                        /*
                            Enderman conditions
                         */
                        } else if(condition.startsWith(s + "iscarrying=")) {
                            passed = e instanceof Enderman && UMaterial.match(((Enderman) e).getCarriedMaterial().getItemType().name()).name().equalsIgnoreCase(value);
                        /*
                            Endermite conditions
                         */
                        } else if(condition.startsWith(s + "isplayerspawned=")) {
                            passed = legacy || thirteen ? false : e instanceof Endermite && ((Endermite) e).isPlayerSpawned() == Boolean.parseBoolean(value);
                        /*
                            Version dependent Entity conditions
                         */
                        } else if(condition.startsWith(s + "isglowing=")) {
                            passed = !eight && e.isGlowing() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isinvulnerable=")) {
                            passed = !eight && e.isInvulnerable() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "issilent=")) {
                            passed = !eight && !nine && e.isSilent() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "hasgravity=")) {
                            passed = e instanceof ArmorStand && ((ArmorStand) e).hasGravity() || !eight && !nine && !ten && e.hasGravity() == Boolean.parseBoolean(value);
                        /*
                            Evoker conditions
                         */
                        } else if(condition.startsWith(s + "currentspell=")) {
                            passed = eight || nine | ten ? false : e instanceof Evoker && ((Evoker) e).getCurrentSpell().name().equalsIgnoreCase(value);
                        /*
                            Explosive conditions
                         */
                        } else if(condition.startsWith(s + "isincendiary=")) {
                            passed = e instanceof Explosive && ((Explosive) e).isIncendiary() == Boolean.parseBoolean(value);
                        /*
                            FallingBlock conditions
                         */
                        } else if(condition.startsWith(s + "material=")) {
                            passed = e instanceof FallingBlock && UMaterial.match(((FallingBlock) e).getMaterial().name()).name().equalsIgnoreCase(value);
                        } else if(condition.startsWith(s + "canhurtentities=")) {
                            passed = e instanceof FallingBlock && ((FallingBlock) e).canHurtEntities() == Boolean.parseBoolean(value);
                        /*
                            Firework conditions
                         */
                        } else if(condition.startsWith(s + "isshotatangle=")) {
                            passed = isLegacy || thirteen ? false : e instanceof Firework && ((Firework) e).isShotAtAngle() == Boolean.parseBoolean(value);
                        /*
                            Fox conditions
                         */
                        } else if(condition.startsWith(s + "foxtype=")) {
                            passed = isLegacy || thirteen ? false : e instanceof Fox && ((Fox) e).getFoxType().name().equalsIgnoreCase(value);
                        } else if(condition.startsWith(s + "iscrouching=")) {
                            passed = isLegacy || thirteen ? false : e instanceof Fox && ((Fox) e).isCrouching() == Boolean.parseBoolean(value);
                        /*
                            Guardian conditions
                         */
                        } else if(condition.startsWith(s + "iselder=")) {
                            passed = e instanceof Guardian && ((Guardian) e).isElder() == Boolean.parseBoolean(value);
                        /*
                            Horse conditions
                         */
                        } else if(condition.startsWith(s + "isvariant=")) {
                            if(e instanceof Horse) {
                                passed = ((Horse) e).getVariant().name().equalsIgnoreCase(value);
                            } else if(e instanceof MushroomCow && (!legacy || thirteen)) {
                                passed = ((MushroomCow) e).getVariant().name().equalsIgnoreCase(value);
                            } else if(!(eight || nine || ten || eleven)) {
                                passed = e instanceof Parrot && ((Parrot) e).getVariant().name().equalsIgnoreCase(value);
                            } else {
                                passed = false;
                            }
                        } else if(condition.startsWith(s + "color=")) {
                            if(e instanceof Horse) {
                                passed = ((Horse) e).getColor().name().equalsIgnoreCase(value);
                            } else if(!(eight || nine || ten)) {
                                passed = e instanceof Llama && ((Llama) e).getColor().name().equalsIgnoreCase(value);
                            } else {
                                passed = false;
                            }
                        } else if(condition.startsWith(s + "style=")) {
                            passed = e instanceof Horse && ((Horse) e).getStyle().name().equalsIgnoreCase(value);
                        /*
                            Husk conditions
                         */
                        } else if(condition.startsWith(s + "isconverting=")) {
                            if(legacy || thirteen) {
                                passed = false;
                            } else {
                                final boolean b = Boolean.parseBoolean(value);
                                passed = b && (e instanceof Husk && ((Husk) e).isConverting() || e instanceof PigZombie && ((PigZombie) e).isConverting() || e instanceof Zombie && ((Zombie) e).isConverting());
                            }
                        /*
                            IronGolem conditions
                         */
                        } else if(condition.startsWith(s + "isplayercreated=")) {
                            passed = e instanceof IronGolem && ((IronGolem) e).isPlayerCreated() == Boolean.parseBoolean(value);
                        /*
                            LightningStrike conditions
                         */
                        } else if(condition.startsWith(s + "iseffect=")) {
                            passed = e instanceof LightningStrike && ((LightningStrike) e).isEffect() == Boolean.parseBoolean(value);
                        /*
                            LivingEntity conditions
                         */
                        } else if(condition.startsWith(s + "isleashed=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).isLeashed() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isswimming=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).isSwimming() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isgliding=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).isGliding() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isholding=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).getEquipment().getItemInHand().getType().name().toLowerCase().endsWith(value);
                        } else if(condition.startsWith(s + "issleeping=")) {
                            passed = legacy || thirteen ? false : e instanceof LivingEntity && ((LivingEntity) e).isSleeping() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "hasai=")) {
                            passed = eight ? true : e instanceof LivingEntity && ((LivingEntity) e).hasAI() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "iscollideable=")) {
                            passed = eight ? true : e instanceof LivingEntity && ((LivingEntity) e).isCollidable() == Boolean.parseBoolean(value);
                        /*
                            Minecart conditions
                         */
                        } else if(condition.startsWith(s + "isslowwhenempty=")) {
                            passed = e instanceof Minecart && ((Minecart) e).isSlowWhenEmpty() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "displayedblock=")) {
                            passed = e instanceof Minecart && UMaterial.match(((Minecart) e).getDisplayBlock().getItemType().name()).name().equalsIgnoreCase(value);
                        /*
                            Mob conditions
                         */
                        } else if(condition.startsWith(s + "hastarget=")) {
                            if(!legacy) {
                                passed = e instanceof Mob && ((Mob) e).getTarget() != null;
                            } else {
                                passed = false;
                            }
                        /*
                            Painting conditions
                         */
                        } else if(condition.startsWith(s + "art=")) {
                            passed = e instanceof Painting && ((Painting) e).getArt().name().equalsIgnoreCase(value);
                        /*
                            Panda conditions
                         */
                        } else if(condition.startsWith(s + "maingene=")) {
                            passed = legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getMainGene().name().equalsIgnoreCase(value);
                        } else if(condition.startsWith(s + "maingeneisrecessive=")) {
                            passed = legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getMainGene().isRecessive() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "hiddengene=")) {
                            passed = legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getHiddenGene().name().equalsIgnoreCase(value);
                        } else if(condition.startsWith(s + "hiddengeneisrecessive=")) {
                            passed = legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getHiddenGene().isRecessive() == Boolean.parseBoolean(value);
                        /*
                            Pig conditions
                         */
                        } else if(condition.startsWith(s + "hassaddle=")) {
                            passed = e instanceof Pig && ((Pig) e).hasSaddle() == Boolean.parseBoolean(value);
                        /*
                            PigZombie conditions
                         */
                        } else if(condition.startsWith(s + "isangry=")) {
                            if(e instanceof PigZombie) {
                                passed = ((PigZombie) e).isAngry() == Boolean.parseBoolean(value);
                            } else if(e instanceof Wolf) {
                                passed = ((Wolf) e).isAngry() == Boolean.parseBoolean(value);
                            } else {
                                passed = false;
                            }
                        /*
                            Player conditions
                         */
                        } else if(condition.startsWith(s + "issneaking=")) {
                            passed = e instanceof Player && ((Player) e).isSneaking() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isblocking=")) {
                            passed = e instanceof Player && ((Player) e).isBlocking() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isflying=")) {
                            passed = e instanceof Player && ((Player) e).isFlying() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "issprinting=")) {
                            passed = e instanceof Player && ((Player) e).isSprinting() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isriptiding=")) {
                            passed = !legacy && e instanceof Player && ((Player) e).isRiptiding() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "issleepignored=")) {
                            passed = e instanceof Player && ((Player) e).isSleepingIgnored() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "allowsflight=")) {
                            passed = e instanceof Player && ((Player) e).getAllowFlight() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "ishealthscaled=")) {
                            passed = e instanceof Player && ((Player) e).isHealthScaled() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "weather=")) {
                            passed = e instanceof Player && ((Player) e).getPlayerWeather().name().equalsIgnoreCase(value);
                        /*
                            Projectile conditions
                         */
                        } else if(condition.startsWith(s + "doesbounce=")) {
                            passed = e instanceof Projectile && ((Projectile) e).doesBounce() == Boolean.parseBoolean(value);
                        /*
                            Rabbit conditions
                         */
                        } else if(condition.startsWith(s + "rabbittype=")) {
                            passed = e instanceof Rabbit && ((Rabbit) e).getRabbitType().name().equalsIgnoreCase(value);
                        /*
                            Raider conditions
                         */
                        } else if(condition.startsWith(s + "ispatrolleader=")) {
                            passed = legacy || thirteen ? false : e instanceof Raider && ((Raider) e).isPatrolLeader() == Boolean.parseBoolean(value);
                        /*
                            Sheep conditions
                         */
                        } else if(condition.startsWith(s + "issheared=")) {
                            passed = e instanceof Sheep && ((Sheep) e).isSheared() == Boolean.parseBoolean(value);
                        /*
                            Sittable conditions
                         */
                        } else if(condition.startsWith(s + "issitting=")) {
                            if(eight || nine || ten || eleven) {
                                passed = Boolean.parseBoolean(value) == e instanceof Wolf && ((Wolf) e).isSitting()/* || e instanceof Ocelot && ((Ocelot) e).isSitting()*/;
                            } else {
                                passed = Boolean.parseBoolean(value) == e instanceof Sittable && ((Sittable) e).isSitting();
                            }
                        /*
                            Skeleton conditions
                         */
                        } else if(condition.startsWith(s + "skeletontype=")) {
                            passed = e instanceof Skeleton && ((Skeleton) e).getSkeletonType().name().equalsIgnoreCase(value);
                        /*
                            Snowman conditions
                         */
                        } else if(condition.startsWith(s + "isderp=")) {
                            passed = eight || nine ? false : e instanceof Snowman && ((Snowman) e).isDerp() == Boolean.parseBoolean(value);
                        /*
                            Tameable conditions
                         */
                        } else if(condition.startsWith(s + "istamed=")) {
                            passed = e instanceof Tameable && ((Tameable) e).isTamed() == Boolean.parseBoolean(value);
                        /*
                            TropicalFish conditions
                         */
                        } else if(condition.startsWith(s + "patterncolor=")) {
                            passed = legacy ? false : e instanceof TropicalFish && ((TropicalFish) e).getPatternColor().name().equalsIgnoreCase(value);
                        } else if(condition.startsWith(s + "bodycolor=")) {
                            passed = legacy ? false : e instanceof TropicalFish && ((TropicalFish) e).getBodyColor().name().equalsIgnoreCase(value);
                        } else if(condition.startsWith(s + "pattern=")) {
                            passed = legacy ? false : e instanceof TropicalFish && ((TropicalFish) e).getPattern().name().equalsIgnoreCase(value);
                        /*
                            Vex conditions
                         */
                        } else if(condition.startsWith(s + "ischarging=")) {
                            passed = eight || nine || ten ? false : e instanceof Vex && ((Vex) e).isCharging() == Boolean.valueOf(value);
                        /*
                            Villager conditions
                         */
                        } else if(condition.startsWith(s + "profession=")) {
                            passed = e instanceof Zombie && ((Zombie) e).isVillager() ? ((Zombie) e).getVillagerProfession().name().equalsIgnoreCase(value) : e instanceof Villager && ((Villager) e).getProfession().name().equalsIgnoreCase(value);
                        } else if(condition.startsWith(s + "villagertype=")) {
                            if(e instanceof Villager) {
                                if(legacy || thirteen) {
                                    throw new UnsupportedOperationException("Event if attribute \"<E>VillagerType=\" unsupported in this Server Version; only supports 1.14.4!");
                                } else {
                                    passed = ((Villager) e).getVillagerType().name().equalsIgnoreCase(value);
                                }
                            } else {
                                passed = false;
                            }
                        /*
                            WitherSkill conditions
                         */
                        } else if(condition.startsWith(s + "ischarged=")) {
                            passed = e instanceof WitherSkull && ((WitherSkull) e).isCharged() == Boolean.parseBoolean(value);
                        /*
                            Zombie conditions
                         */
                        } else if(condition.startsWith(s + "isvillager=")) {
                            passed = e instanceof Zombie && ((Zombie) e).isVillager() == Boolean.parseBoolean(value);
                        }
                    } else if(condition.startsWith("cancelled=")) {
                        passed = isCancelled() == Boolean.parseBoolean(value);
                    }
                    if(!passed) break;
                }
            }
        }
        return passed;
    }
    public void execute(Event event, HashMap<String, Entity> recipients, List<String> conditions, HashMap<Entity, List<EventAttribute>> attributes, Object value, HashMap<String, String> valueReplacements) {
        this.recipients = recipients;
        if(didPassConditions(conditions)) {
            for(Entity entity : attributes.keySet()) {
                for(EventAttribute a : attributes.get(entity)) {
                    final EventAttributeCallEvent e = new EventAttributeCallEvent(entity, a);
                    pluginmanager.callEvent(e);
                    if(!e.isCancelled()) {
                        //call(entity, value);
                    }
                }
            }
        }
    }
    public abstract void call(HashMap<Entity, Object> recipientValues);


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void creatureSpawnEvent(CreatureSpawnEvent event) {
        if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            final UUID u = event.getEntity().getUniqueId();
            if(!spawnedFromSpawner.contains(u)) {
                spawnedFromSpawner.add(u);
            }
        }
    }
}
