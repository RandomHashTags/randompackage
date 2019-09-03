package me.randomhashtags.randompackage.utils;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.randomhashtags.randompackage.addons.*;
import me.randomhashtags.randompackage.addons.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.attributes.*;
import me.randomhashtags.randompackage.attributes.event.SetDamage;
import me.randomhashtags.randompackage.events.*;
import me.randomhashtags.randompackage.events.customenchant.AlchemistExchangeEvent;
import me.randomhashtags.randompackage.events.customenchant.CustomEnchantApplyEvent;
import me.randomhashtags.randompackage.events.customenchant.EnchanterPurchaseEvent;
import me.randomhashtags.randompackage.events.customenchant.PvAnyEvent;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public abstract class EventAttributes extends RPFeature implements Listener {
    /*
        Read https://gitlab.com/RandomHashTags/randompackage-multi/wikis/Event-Attributes for all event attribute info
            * Event specific entity placeholders
            * Allowed conditions for specific entity types
            * Available event attributes with their identifier, and what they do
     */
    // TODO: Support individual event conditions
    private static List<UUID> spawnedFromSpawner = new ArrayList<>();

    static {
        if(eventattributes == null) {
            eventattributes = new LinkedHashMap<>();
        }
        final List<EventAttribute> list = Arrays.asList(
                // event attributes
                new SetDamage(),
                // attributes
                new AddPotionEffect(),
                new Damage(),
                new DepleteRarityGem(),
                new DropItem(),
                new ExecuteCommand(),
                //new Explode(),
                new GiveItem(),
                new Ignite(),
                new KickWithReason(),
                new PerformCommand(),
                new PlaySound(),
                new RemovePotionEffect(),
                new SendMessage(),
                new SetAir(),
                new SetCancelled(),
                new SetDroppedXp(),
                new SetHealth(),
                new SetHunger(),
                new SetNoDamageTicks(),
                new SetXp(),
                new Smite(),
                new StealXp(),
                new Wait()
        );
        for(EventAttribute e : list) {
            e.load();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void creatureSpawnEvent(CreatureSpawnEvent event) {
        if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            final UUID u = event.getEntity().getUniqueId();
            if(!spawnedFromSpawner.contains(u)) {
                spawnedFromSpawner.add(u);
            }
        }
    }

    public boolean didPassConditions(HashMap<String, Entity> entities, List<String> conditions, boolean cancelled) {
        boolean passed = true;

        final boolean eight = version.contains("1.8"), nine = version.contains("1.9"), ten = version.contains("1.10"), eleven = version.contains("1.11"), thirteen = version.contains("1.13"), legacy = isLegacy;
        for(String c : conditions) {
            final String condition = c.toLowerCase();
            if(condition.startsWith("chance=")) {
                passed = random.nextInt(100) < getRemainingInt(condition.split("=")[1]);
            }
            if(passed) {
                for(String s : entities.keySet()) {
                    final String value = condition.contains("=") ? condition.split("=")[1] : "false";
                    final Entity e = entities.get(s);
                    s = s.toLowerCase();

                    if(condition.startsWith(s)) {
                        if(condition.startsWith(s + "isfromspawner=")) {
                            passed = spawnedFromSpawner.contains(e.getUniqueId()) == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isplayer=")) {
                            passed = e instanceof Player == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "ismob=")) {
                            passed = e instanceof Mob == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "iscreature=")) {
                            passed = e instanceof Creature == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "isflying=")) {
                            passed = e instanceof Flying || e instanceof Player && ((Player) e).isFlying() == Boolean.parseBoolean(value);
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
                            Horse (+MushroomCow, Llama, Colorable) conditions
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
                            } else if(e instanceof Colorable) {
                                passed = ((Colorable) e).getColor().name().equalsIgnoreCase(value);
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
                        } else if(condition.startsWith(s + "health<=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).getHealth() <= Double.parseDouble(value);
                        } else if(condition.startsWith(s + "health>=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).getHealth() >= Double.parseDouble(value);
                        } else if(condition.startsWith(s + "haspotioneffect=")) {
                            final PotionEffectType t = getPotionEffectType(value);
                            passed = t != null && e instanceof LivingEntity && ((LivingEntity) e).hasPotionEffect(t);
                        } else if(condition.startsWith(s + "nodamageticks<=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).getNoDamageTicks() <= Double.parseDouble(value);
                        } else if(condition.startsWith(s + "nodamageticks>=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).getNoDamageTicks() >= Double.parseDouble(value);
                        } else if(condition.startsWith(s + "remainingair=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).getRemainingAir() == Integer.parseInt(value);
                        } else if(condition.startsWith(s + "remainingair<=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).getRemainingAir() <= Integer.parseInt(value);
                        } else if(condition.startsWith(s + "remainingair>=")) {
                            passed = e instanceof LivingEntity && ((LivingEntity) e).getRemainingAir() >= Integer.parseInt(value);
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
                        } else if(condition.startsWith(s + "totalexp=")) {
                            passed = e instanceof Player && getTotalExperience((Player) e) == Integer.parseInt(value);
                        } else if(condition.startsWith(s + "totalexp<=")) {
                            passed = e instanceof Player && getTotalExperience((Player) e) <= Integer.parseInt(value);
                        } else if(condition.startsWith(s + "totalexp>=")) {
                            passed = e instanceof Player && getTotalExperience((Player) e) >= Integer.parseInt(value);
                        } else if(condition.startsWith(s + "explevel=")) {
                            passed = e instanceof Player && ((Player) e).getLevel() == Integer.parseInt(value);
                        } else if(condition.startsWith(s + "explevel<=")) {
                            passed = e instanceof Player && ((Player) e).getLevel() <= Integer.parseInt(value);
                        } else if(condition.startsWith(s + "explevel>=")) {
                            passed = e instanceof Player && ((Player) e).getLevel() >= Integer.parseInt(value);
                        } else if(condition.startsWith(s + "foodlevel=")) {
                            passed = e instanceof Player && ((Player) e).getFoodLevel() == Integer.parseInt(value);
                        } else if(condition.startsWith(s + "foodlevel<=")) {
                            passed = e instanceof Player && ((Player) e).getFoodLevel() <= Integer.parseInt(value);
                        } else if(condition.startsWith(s + "foodlevel>=")) {
                            passed = e instanceof Player && ((Player) e).getFoodLevel() >= Integer.parseInt(value);
                        } else if(condition.startsWith(s + "saturation=")) {
                            passed = e instanceof Player && ((Player) e).getSaturation() == Float.parseFloat(value);
                        } else if(condition.startsWith(s + "saturation<=")) {
                            passed = e instanceof Player && ((Player) e).getSaturation() <= Float.parseFloat(value);
                        } else if(condition.startsWith(s + "saturation>=")) {
                            passed = e instanceof Player && ((Player) e).getSaturation() >= Float.parseFloat(value);
                        } else if(condition.startsWith(s + "viewdistance=")) {
                            passed = legacy || e instanceof Player && ((Player) e).getClientViewDistance() == Integer.parseInt(value);
                        } else if(condition.startsWith(s + "viewdistance<=")) {
                            passed = legacy || e instanceof Player && ((Player) e).getClientViewDistance() <= Integer.parseInt(value);
                        } else if(condition.startsWith(s + "viewdistance>=")) {
                            passed = legacy || e instanceof Player && ((Player) e).getClientViewDistance() >= Integer.parseInt(value);
                        } else if(condition.startsWith(s + "language=")) {
                            passed = eight || nine || ten || eleven || e instanceof Player && ((Player) e).getLocale().equalsIgnoreCase(value);
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
                            Raider (+Ravager) conditions
                         */
                        } else if(condition.startsWith(s + "ispatrolleader=")) {
                            passed = legacy || thirteen ? false : e instanceof Raider && ((Raider) e).isPatrolLeader() == Boolean.parseBoolean(value);
                        } else if(condition.startsWith(s + "patroltargetblock=")) {
                            passed = legacy ? false : e instanceof Raider && UMaterial.match(((Raider) e).getPatrolTarget().getType().name()).name().toLowerCase().endsWith(value);
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
                            Slime (+Phantom) conditions
                         */
                        } else if(condition.startsWith(s + "size=")) {
                            final int v = Integer.parseInt(value);
                            passed = e instanceof Slime && ((Slime) e).getSize() == v || !legacy && e instanceof Phantom && ((Phantom) e).getSize() == v;
                        } else if(condition.startsWith(s + "size<=")) {
                            final int v = Integer.parseInt(value);
                            passed = e instanceof Slime && ((Slime) e).getSize() <= v || !legacy && e instanceof Phantom && ((Phantom) e).getSize() <= v;
                        } else if(condition.startsWith(s + "size>=")) {
                            final int v = Integer.parseInt(value);
                            passed = e instanceof Slime && ((Slime) e).getSize() >= v || !legacy && e instanceof Phantom && ((Phantom) e).getSize() >= v;
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
                            Villager (+ZombieVillager) conditions
                         */
                        } else if(condition.startsWith(s + "profession=")) {
                            passed = e instanceof Zombie && ((Zombie) e).isVillager() ? ((Zombie) e).getVillagerProfession().name().equalsIgnoreCase(value) : e instanceof Villager && ((Villager) e).getProfession().name().equalsIgnoreCase(value);
                        } else if(condition.startsWith(s + "villagertype=")) {
                            passed = e instanceof Villager && !(legacy || thirteen) && ((Villager) e).getVillagerType().name().equalsIgnoreCase(value);
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
                        /*
                            RandomPackage conditions
                         */
                        } else if(condition.startsWith(s + "equippedarmorset=")) {
                            final ArmorSet a = e instanceof Player ? valueOfArmorSet((Player) e) : null;
                            passed = a != null && a.getIdentifier().equals(value);
                        } else if(condition.startsWith(s + "equippedmask=")) {
                            final EntityEquipment eq = e instanceof Player ? ((Player) e).getEquipment() : null;
                            final Mask m = eq != null ? valueOfMask(eq.getHelmet()) : null;
                            passed = m != null && m.getIdentifier().equals(value);
                        } else if(condition.startsWith(s + "equippedtitle=")) {
                            final Title t = RPPlayer.get(e.getUniqueId()).getActiveTitle();
                            passed = t != null &&  e instanceof Player && t.getIdentifier().equals(value);
                        } else if(condition.startsWith(s + "hasactivefilter=")) {
                            passed = e instanceof Player && RPPlayer.get(e.getUniqueId()).filter;
                        } else if(condition.startsWith(s + "hasactiveplayerquest=")) {
                            final PlayerQuest q = e instanceof Player ? getPlayerQuest(value) : null;
                            final HashMap<PlayerQuest, ActivePlayerQuest> a = q != null ? RPPlayer.get(e.getUniqueId()).getQuests() : null;
                            passed = a != null && a.containsKey(q) && !a.get(q).isExpired();
                        } else if(condition.startsWith(s + "hasactiveraritygem=")) {
                            passed = e instanceof Player && RPPlayer.get(e.getUniqueId()).hasActiveRarityGem(getRarityGem(value));
                        } else if(condition.startsWith(s + "hasactivetitle=")) {
                            passed = e instanceof Player && RPPlayer.get(e.getUniqueId()).getActiveTitle() != null;
                        } else if(condition.startsWith(s + "hascustomentities=")) {
                            passed = e instanceof Player && !RPPlayer.get(e.getUniqueId()).getCustomEnchantEntities().isEmpty();
                        } else if(condition.startsWith(s + "hasequippedarmorset=")) {
                            passed = e instanceof Player && valueOfArmorSet((Player) e) != null;
                        } else if(condition.startsWith(s + "hasequippedmask=")) {
                            final EntityEquipment eq = e instanceof Player ? ((Player) e).getEquipment() : null;
                            passed = eq != null && valueOfMask(eq.getHelmet()) != null;
                        } else if(condition.startsWith(s + "hasfiltereditem=")) {
                            final List<UMaterial> m = e instanceof Player ? RPPlayer.get(e.getUniqueId()).getFilteredItems() : null;
                            passed = m != null && m.contains(UMaterial.match(value));
                        } else if(condition.startsWith(s + "ownstitle=")) {
                            final Title t = e instanceof Player ? getTitle(value) : null;
                            passed = t != null && RPPlayer.get(e.getUniqueId()).getTitles().contains(t);
                        }
                    } else if(condition.startsWith("cancelled=")) {
                        passed = cancelled == Boolean.parseBoolean(value);
                    }
                    if(!passed) break;
                }
            }
        }
        return passed;
    }


    protected HashMap<String, Entity> getEntities(Object...values) {
        final HashMap<String, Entity> e = new HashMap<>();
        for(int i = 0; i < values.length; i++) {
            if(i%2 == 1) {
                e.put((String) values[i-1], (Entity) values[i]);
            }
        }
        return e;
    }
    protected HashMap<String, String> getReplacements(String...replacements) {
        final HashMap<String, String> r = new HashMap<>();
        if(replacements != null) {
            for(int i = 0; i < replacements.length; i++) {
                if(i%2 == 1) {
                    r.put(replacements[i-1], replacements[i]);
                }
            }
        }
        return !r.isEmpty() ? r : null;
    }
    private HashMap<String, String> mergeReplacements(HashMap<String, String> left, String...right) {
        if(right != null) left.putAll(getReplacements(right));
        return left;
    }
    public HashMap<String, Entity> getNearbyEntities(Location center, double radius) { return getNearbyEntities(center, radius, radius, radius); }
    public HashMap<String, Entity> getNearbyEntities(Location center, double radiusX, double radiusY, double radiusZ) {
        final HashMap<String, Entity> e = new HashMap<>();
        final List<Entity> nearby = new ArrayList<>(center.getWorld().getNearbyEntities(center, radiusX, radiusY, radiusZ));
        for(int i = 1; i <= nearby.size(); i++) {
            e.put("Nearby" + i, nearby.get(i));
        }
        return e;
    }

    private HashMap<RPPlayer, String> getData(HashMap<String, Entity> entities, HashMap<String, String> entityValues) {
        final HashMap<RPPlayer, String> a = new HashMap<>();
        for(String s : entities.keySet()) {
            if(entityValues.containsKey(s)) {
                final Entity e = entities.get(s);
                if(e instanceof Player) {
                    a.put(RPPlayer.get(e.getUniqueId()), entityValues.get(s));
                }
            }
        }
        return a;
    }

    public boolean executeAll(Event event, HashMap<String, Entity> entities, List<String> conditions, boolean cancelled, HashMap<String, String> entityValues, LinkedHashMap<EventAttribute, HashMap<Entity, String>> values) {
        final boolean passed = didPassConditions(entities, conditions, cancelled);
        if(passed) {
            final Player player = (Player) entities.getOrDefault("Player", entities.getOrDefault("Killer", entities.getOrDefault("Damager", entities.getOrDefault("Owner", null))));
            final Entity entity = entities.getOrDefault("Victim", entities.getOrDefault("Entity", null));
            final HashMap<RPPlayer, String> data = getData(entities, entityValues);
            final boolean dadda = !data.isEmpty(), playerNN = player != null, entityNN = entity != null;
            for(EventAttribute a : values.keySet()) {
                final HashMap<Entity, String> valuez = values.get(a);
                final String defaultValue = valuez.getOrDefault(null, null);
                if(a.getIdentifier().equals("WAIT")) {
                    final int ticks = (int) evaluate(defaultValue);
                    final LinkedHashMap<EventAttribute, HashMap<Entity, String>> attributes = new LinkedHashMap<>(values);
                    attributes.remove(a);
                    scheduler.scheduleSyncDelayedTask(randompackage, () -> executeAll(event, entities, conditions, cancelled, entityValues, attributes), ticks);
                    break;
                } else {
                    if(dadda) a.executeData(data);
                    valuez.remove(null);
                    a.execute(valuez);
                    if(defaultValue != null) {
                        a.execute(event, defaultValue);
                        if(playerNN && entityNN) {
                            a.execute(player, entity, defaultValue);
                        }
                    }
                }
            }
        }
        return passed;
    }

    private boolean tryGeneric(Event event, HashMap<String, Entity> entities, List<String> attributes) {
        return tryGeneric(event, entities, attributes, null);
    }
    private boolean tryGeneric(Event event, HashMap<String, Entity> entities, List<String> attributes, HashMap<String, String> replacements) {
        if(event != null && attributes != null && !attributes.isEmpty()) {
            final List<Boolean> checks = new ArrayList<>();
            final String e = event.getEventName().split("Event")[0].toLowerCase();
            final boolean cancellable = event instanceof Cancellable, cancelled = cancellable && ((Cancellable) event).isCancelled(), hasReplacements = replacements != null && !replacements.isEmpty();
            for(String s : attributes) {
                final String[] semi = s.split(";");
                final String first = semi[0].toLowerCase();
                if(first.equals(e)) {
                    final List<String> conditions = new ArrayList<>();
                    final HashMap<String, String> entityValues = new HashMap<>();
                    final LinkedHashMap<EventAttribute, HashMap<Entity, String>> execute = new LinkedHashMap<>();

                    for(String c : s.split(semi[0] + ";")[1].split(";")) {
                        if(c.contains("=")) {
                            final String[] values = c.split("=");
                            String value1 = values[1], string = values[0].toUpperCase();
                            if(hasReplacements) {
                                for(String r : replacements.keySet()) {
                                    value1 = value1.replace(r, replacements.get(r));
                                }
                            }
                            for(String entity : entities.keySet()) {
                                final String E = entity.toUpperCase();
                                if(string.contains(E)) {
                                    string = string.replace(E, "");
                                    entityValues.put(entity, value1);
                                }
                            }
                            final EventAttribute a = getEventAttribute(string);
                            if(a == null) {
                                conditions.add(c);
                            } else {
                                execute.put(a, new HashMap<>());
                                final HashMap<Entity, String> E = execute.get(a);
                                E.put(null, value1);
                                for(String ss : entities.keySet()) {
                                    E.put(entities.get(ss), entityValues.get(ss));
                                }
                            }
                        }
                    }
                    checks.add(executeAll(event, entities, conditions, cancelled, entityValues, execute));
                }
            }
            return checks.contains(true);
        }
        return false;
    }

    private boolean didPass(Event event, List<String> attributes) {
        return event != null && attributes != null && !attributes.isEmpty();
    }
    public boolean trigger(Event event, HashMap<String, Entity> entities, List<String> attributes) {
        return trigger(event, entities, attributes, null);
    }
    public boolean trigger(Event event, HashMap<String, Entity> entities, List<String> attributes, HashMap<String, String> replacements) {
        if(didPass(event, attributes) && entities != null) {
            return tryGeneric(event, entities, attributes, replacements);
        }
        return false;
    }
    /*
        Bukkit Events
     */
    public boolean trigger(EntityDeathEvent event, List<String> attributes, String...replacements) {
        if(didPass(event, attributes)) {
            final LivingEntity e = event.getEntity();
            final Player k = e.getKiller();
            if(k != null) {
                return tryGeneric(event, getEntities("Victim", e, "Killer", k), attributes, getReplacements(replacements));
            }
        }
        return false;
    }
    public void trigger(BlockPlaceEvent event, List<String> attributes) { trigger(event, getEntities("Player", event.getPlayer()), attributes); }
    public void trigger(BlockBreakEvent event, List<String> attributes) { trigger(event, getEntities("Player", event.getPlayer()), attributes); }
    public void trigger(EntityDamageEvent event, List<String> attributes) { trigger(event, getEntities("Victim", event.getEntity()), attributes, getReplacements("dmg", Double.toString(event.getDamage()))); }
    public void trigger(EntityDamageByEntityEvent event, List<String> attributes) { trigger(event, getEntities("Damager", event.getDamager(), "Victim", event.getEntity()), attributes, getReplacements("dmg", Double.toString(event.getDamage()))); }
    public void trigger(EntityTameEvent event, List<String> attributes) { trigger(event, getEntities("Entity", event.getEntity(), "Owner", event.getOwner()), attributes); }
    public void trigger(FoodLevelChangeEvent event, List<String> attributes) { trigger(event, getEntities("Player", event.getEntity()), attributes); }
    /*
        RandomPackage Events
     */
    public boolean trigger(AlchemistExchangeEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(ArmorSetEquipEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(ArmorSetUnequipEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(EnchanterPurchaseEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(FallenHeroSlainEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Killer", event.killer), attributes, getReplacements(replacements));
    }
    public boolean trigger(PvAnyEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Damager", event.damager, "Victim", event.victim), attributes, getReplacements(replacements));
    }
    public boolean trigger(CustomEnchantApplyEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(PlayerArmorEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(PlayerClaimEnvoyCrateEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(RandomizationScrollUseEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(JackpotPurchaseTicketsEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, mergeReplacements(getReplacements("amount", event.amount.toBigInteger().toString()), replacements));
    }
    public boolean trigger(MysteryMobSpawnerOpenEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(ServerCrateOpenEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(ShopPurchaseEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(ShopSellEvent event, List<String> attributes, String...replacements) {
        return trigger(event, getEntities("Player", event.player), attributes, getReplacements(replacements));
    }
    public boolean trigger(McMMOPlayerXpGainEvent event, List<String> attributes, String...replacements) { // TODO: fix this if they don't have mcmmo installed
        return trigger(event, getEntities("Player", event.getPlayer()), attributes, getReplacements(replacements));
    }
}
