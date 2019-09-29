package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.addon.living.LivingCustomBoss;
import me.randomhashtags.randompackage.attribute.Combo;
import me.randomhashtags.randompackage.event.booster.BoosterActivateEvent;
import me.randomhashtags.randompackage.event.PlayerClaimEnvoyCrateEvent;
import me.randomhashtags.randompackage.event.RandomizationScrollUseEvent;
import me.randomhashtags.randompackage.event.ServerCrateOpenEvent;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantApplyEvent;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.event.enchant.EnchanterPurchaseEvent;
import me.randomhashtags.randompackage.event.kit.KitClaimEvent;
import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Chunk;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class EventConditions extends RPFeature implements Combo, RPItemStack {
    protected static List<UUID> spawnedFromSpawner = new ArrayList<>();
    protected static HashMap<UUID, EntityShootBowEvent> projectileEvents = new HashMap<>();

    protected boolean passedAllConditions(Event event, Entity entity, String condition, String s, String value, boolean legacy, boolean eight, boolean nine, boolean ten, boolean eleven, boolean thirteen) {
        final boolean pre = passedCustomCondition(event, entity, condition, s, value) && passedEvent(event, entity, condition, s, value), isEntity = condition.startsWith(s);
        condition = condition.substring(s.length()).split("=")[0];
        return pre && (!isEntity
                || passedBasic(entity, condition, s, value)
                && passedAgeable(entity, condition, s, value)
                && passedAnimals(entity, condition, s, value, legacy, thirteen)
                && passedArmorStand(entity, condition, s, value)
                && passedBat(entity, condition, s, value)
                && passedCat(entity, condition, s, value, legacy, thirteen)
                && passedChestedHorse(entity, condition, s, value, eight, nine, ten)
                && passedCreeper(entity, condition, s, value)
                && passedEnderCrystal(entity, condition, s, value, eight)
                && passedEnderDragon(entity, condition, s, value, eight)
                && passedEnderman(entity, condition, s, value)
                && passedEndermite(entity, condition, s, value, legacy, thirteen)
                && passedEntity(entity, condition, s, value, eight, nine, ten)
                && passedFallenBlock(event, entity, condition, s, value)
                && passedFirework(entity, condition, s, value, legacy, thirteen)
                && passedFox(entity, condition, s, value, legacy, thirteen)
                && passedGuardian(entity, condition, s, value)
                && passedHorse(entity, condition, s, value, legacy, eight, nine, ten, eleven, thirteen)
                && passedHusk(entity, condition, s, value, legacy, thirteen)
                && passedIronGolem(entity, condition, s, value)
                && passedLightingStrike(entity, condition, s, value)
                && passedLivingEntity(entity, condition, s, value, legacy, eight, thirteen)
                && passedMinecart(entity, condition, s, value)
                && passedMob(entity, condition, s, value, legacy)
                && passedPainting(entity, condition, s, value)
                && passedPanda(entity, condition, s, value, legacy, thirteen)
                && passedPig(entity, condition, s, value)
                && passedPigZombie(entity, condition, s, value)
                && passedPlayer(entity, condition, s, value, legacy, eight, nine, ten, eleven)
                && passedProjectile(entity, condition, s, value)
                && passedRabbit(entity, condition, s, value)
                && passedRaider(entity, condition, s, value, legacy, thirteen)
                && passedRandomPackage(entity, condition, s, value)
                && passedSheep(entity, condition, s, value)
                && passedSittable(entity, condition, s, value, eight, nine, ten, eleven)
                && passedSkeleton(entity, condition, s, value)
                && passedSlime(entity, condition, s, value, legacy)
                && passedSnowman(entity, condition, s, value, eight, nine)
                && passedTameable(entity, condition, s, value)
                && passedTropicalFish(entity, condition, s, value, legacy)
                && passedVex(entity, condition, s, value, eight, nine, ten)
                && passedVillager(entity, condition, s, value, legacy, thirteen)
                && passedWitherSkull(entity, condition, s, value)
                && passedZombie(entity, condition, s, value))
        ;
    }
    private boolean passedBasic(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "isfromspawner": return spawnedFromSpawner.contains(e.getUniqueId()) == Boolean.parseBoolean(value);
            case "isplayer": return e instanceof Player == Boolean.parseBoolean(value);
            case "ismob": return e instanceof Mob == Boolean.parseBoolean(value);
            case "ismonster": return e instanceof Monster == Boolean.parseBoolean(value);
            case "iscreature": return e instanceof Creature == Boolean.parseBoolean(value);
            case "isanimal": return e instanceof Animals == Boolean.parseBoolean(value);
            case "isflying": return e instanceof Flying || e instanceof Player && ((Player) e).isFlying() == Boolean.parseBoolean(value);
            case "istype": return e.getType().name().toLowerCase().equals(value);
            case "isfacing": return e.getFacing().name().toLowerCase().startsWith(value);
            case "isop": return e.isOp() == Boolean.parseBoolean(value);
            case "isinsidevehicle": return e.isInsideVehicle() == Boolean.parseBoolean(value);
            case "isriding": return e.isInsideVehicle() && e.getVehicle().getType().name().equalsIgnoreCase(value);
            case "iscustomnamevisible": return e.isCustomNameVisible() == Boolean.parseBoolean(value);
            case "isonground": return e.isOnGround() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedAgeable(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "isadult": return e instanceof Ageable && ((Ageable) e).isAdult() == Boolean.parseBoolean(value);
            case "isbaby": return e instanceof Zombie && ((Zombie) e).isBaby() || e instanceof Ageable && ((Ageable) e).isAdult() != Boolean.parseBoolean(value);
            case "canbreed": return e instanceof Ageable && ((Ageable) e).canBreed() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedAnimals(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        switch (condition) {
            case "inlovemode": return !legacy && !thirteen && e instanceof Animals && ((Animals) e).isLoveMode() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedArmorStand(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "hasbaseplate": return e instanceof ArmorStand && ((ArmorStand) e).hasBasePlate() == Boolean.parseBoolean(value);
            case "hasarms": return e instanceof ArmorStand && ((ArmorStand) e).hasArms() == Boolean.parseBoolean(value);
            case "ismarker": return e instanceof ArmorStand && ((ArmorStand) e).isMarker() == Boolean.parseBoolean(value);
            case "issmall": return e instanceof ArmorStand && ((ArmorStand) e).isSmall() == Boolean.parseBoolean(value);
            case "isvisible": return e instanceof ArmorStand && ((ArmorStand) e).isVisible() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedBat(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "isawake": return e instanceof Bat && ((Bat) e).isAwake() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedCat(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        switch (condition) {
            case "cattype": return LEGACY ? e instanceof Ocelot && ((Ocelot) e).getCatType().name().equalsIgnoreCase(value) : e instanceof Cat && ((Cat) e).getCatType().name().equalsIgnoreCase(value);
            case "collarcolor":
                if(e instanceof Wolf) {
                    return ((Wolf) e).getCollarColor().name().equalsIgnoreCase(value);
                } else if(!legacy && !thirteen) {
                    return e instanceof Cat && ((Cat) e).getCollarColor().name().equalsIgnoreCase(value);
                } else {
                    return false;
                }
            default:
                return true;
        }
    }
    private boolean passedChestedHorse(Entity e, String condition, String s, String value, boolean eight, boolean nine, boolean ten) {
        switch (condition) {
            case "iscarryingchest":
                if(eight || nine || ten) {
                    return e instanceof Horse && ((Horse) e).isCarryingChest() == Boolean.parseBoolean(value);
                } else {
                    return e instanceof ChestedHorse && ((ChestedHorse) e).isCarryingChest() == Boolean.parseBoolean(value);
                }
            default: return true;
        }
    }
    private boolean passedCreeper(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "ispowered": return e instanceof Creeper && ((Creeper) e).isPowered() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedEnderCrystal(Entity e, String condition, String s, String value, boolean eight) {
        switch (condition) {
            case "isshowingbottom": return eight ? true : e instanceof EnderCrystal && ((EnderCrystal) e).isShowingBottom() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedEnderDragon(Entity e, String condition, String s, String value, boolean eight) {
        switch (condition) {
            case "phase": return eight ? true : e instanceof EnderDragon && ((EnderDragon) e).getPhase().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedEnderman(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "iscarrying": return e instanceof Enderman && UMaterial.match(((Enderman) e).getCarriedMaterial().getItemType().name()).name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedEndermite(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        switch (condition) {
            case "isplayerspawned": return legacy || thirteen ? false : e instanceof Endermite && ((Endermite) e).isPlayerSpawned() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedEntity(Entity e, String condition, String s, String value, boolean eight, boolean nine, boolean ten) {
        switch (condition) {
            case "inbiome":
                final Chunk chunk = e.getLocation().getChunk();
                return e.getWorld().getBiome(chunk.getX(), chunk.getZ()).name().equalsIgnoreCase(value);
            case "inworld": return e.getWorld().getName().equals(value);
            case "isglowing": return !eight && e.isGlowing() == Boolean.parseBoolean(value);
            case "isinvulnerable": return !eight && e.isInvulnerable() == Boolean.parseBoolean(value);
            case "issilent": return !eight && !nine && e.isSilent() == Boolean.parseBoolean(value);
            case "isitem": return e instanceof Item == Boolean.parseBoolean(value);
            case "hasgravity": return e instanceof ArmorStand && ((ArmorStand) e).hasGravity() || !eight && !nine && !ten && e.hasGravity() == Boolean.parseBoolean(value);
            case "worlddifficulty": return e.getWorld().getDifficulty().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedEvoker(Entity e, String condition, String s, String value, boolean eight, boolean nine, boolean ten) {
        switch (condition) {
            case "currentspell": return eight || nine | ten ? false : e instanceof Evoker && ((Evoker) e).getCurrentSpell().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedExplosive(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "isincendiary": return e instanceof Explosive && ((Explosive) e).isIncendiary() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedFallenBlock(Event event, Entity e, String condition, String s, String value) {
        switch (condition) {
            case "material":
                return e instanceof FallingBlock && UMaterial.match(((FallingBlock) e).getMaterial().name()).name().equalsIgnoreCase(value)
                    || event instanceof BlockPlaceEvent && UMaterial.match(((BlockPlaceEvent) event).getBlock().getType().name()).name().equalsIgnoreCase(value)
                    || event instanceof BlockBreakEvent && UMaterial.match(((BlockBreakEvent) event).getBlock().getType().name()).name().equalsIgnoreCase(value);
            case "canhurtentities": return e instanceof FallingBlock && ((FallingBlock) e).canHurtEntities() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedFirework(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        switch (condition) {
            case "isshotatangle": return legacy || thirteen ? false : e instanceof Firework && ((Firework) e).isShotAtAngle() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedFox(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        switch (condition) {
            case "foxtype": return legacy || thirteen ? false : e instanceof Fox && ((Fox) e).getFoxType().name().equalsIgnoreCase(value);
            case "iscrouching": return legacy || thirteen ? false : e instanceof Fox && ((Fox) e).isCrouching() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedGuardian(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "iselder": return e instanceof Guardian && ((Guardian) e).isElder() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedHorse(Entity e, String condition, String s, String value, boolean legacy, boolean eight, boolean nine, boolean ten, boolean eleven, boolean thirteen) {
        switch (condition) {
            case "isvariant":
                if(e instanceof Horse) {
                    return ((Horse) e).getVariant().name().equalsIgnoreCase(value);
                } else if(e instanceof MushroomCow && (!legacy || thirteen)) {
                    return ((MushroomCow) e).getVariant().name().equalsIgnoreCase(value);
                } else if(!(eight || nine || ten || eleven)) {
                    return e instanceof Parrot && ((Parrot) e).getVariant().name().equalsIgnoreCase(value);
                } else {
                    return false;
                }
            case "color":
                if(e instanceof Horse) {
                    return ((Horse) e).getColor().name().equalsIgnoreCase(value);
                } else if(!(eight || nine || ten)) {
                    return e instanceof Llama && ((Llama) e).getColor().name().equalsIgnoreCase(value);
                } else if(e instanceof Colorable) {
                    return ((Colorable) e).getColor().name().equalsIgnoreCase(value);
                } else {
                    return false;
                }
            case "style": return e instanceof Horse && ((Horse) e).getStyle().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedHusk(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        switch (condition) {
            case "isconverting":
                if(legacy || thirteen) {
                    return false;
                } else {
                    final boolean b = Boolean.parseBoolean(value);
                    return b && (e instanceof Husk && ((Husk) e).isConverting() || e instanceof PigZombie && ((PigZombie) e).isConverting() || e instanceof Zombie && ((Zombie) e).isConverting());
                }
            default:
                return true;
        }
    }
    private boolean passedIronGolem(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "isplayercreated": return e instanceof IronGolem && ((IronGolem) e).isPlayerCreated() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedLightingStrike(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "iseffect": return e instanceof LightningStrike && ((LightningStrike) e).isEffect() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedLivingEntity(Entity e, String condition, String s, String value, boolean legacy, boolean eight, boolean thirteen) {
        switch (condition) {
            case "isleashed": return e instanceof LivingEntity && ((LivingEntity) e).isLeashed() == Boolean.parseBoolean(value);
            case "isswimming": return e instanceof LivingEntity && ((LivingEntity) e).isSwimming() == Boolean.parseBoolean(value);
            case "isgliding": return e instanceof LivingEntity && ((LivingEntity) e).isGliding() == Boolean.parseBoolean(value);
            case "isholding": return e instanceof LivingEntity && ((LivingEntity) e).getEquipment().getItemInHand().getType().name().toLowerCase().endsWith(value);
            case "issleeping": return legacy || thirteen ? false : e instanceof LivingEntity && ((LivingEntity) e).isSleeping() == Boolean.parseBoolean(value);
            case "hasai": return eight ? true : e instanceof LivingEntity && ((LivingEntity) e).hasAI() == Boolean.parseBoolean(value);
            case "iscollideable": return eight ? true : e instanceof LivingEntity && ((LivingEntity) e).isCollidable() == Boolean.parseBoolean(value);
            case "health<": // health<=
                return e instanceof LivingEntity && ((LivingEntity) e).getHealth() <= Double.parseDouble(value);
            case "health>": // health>=
                return e instanceof LivingEntity && ((LivingEntity) e).getHealth() >= Double.parseDouble(value);
            case "haspotioneffect":
                final PotionEffectType t = getPotionEffectType(value);
                return t != null && e instanceof LivingEntity && ((LivingEntity) e).hasPotionEffect(t);
            case "nodamageticks<": // nodamageticks<=
                return e instanceof LivingEntity && ((LivingEntity) e).getNoDamageTicks() <= Double.parseDouble(value);
            case "nodamageticks>": // nodamageticks>=
                return e instanceof LivingEntity && ((LivingEntity) e).getNoDamageTicks() >= Double.parseDouble(value);
            case "remainingair": return e instanceof LivingEntity && ((LivingEntity) e).getRemainingAir() == Integer.parseInt(value);
            case "remainingair<": // remainingair<=
                return e instanceof LivingEntity && ((LivingEntity) e).getRemainingAir() <= Integer.parseInt(value);
            case "remainingair>": // remainingair>=
                return e instanceof LivingEntity && ((LivingEntity) e).getRemainingAir() >= Integer.parseInt(value);
            default: return true;
        }
    }
    private boolean passedMinecart(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "isslowwhenempty": return e instanceof Minecart && ((Minecart) e).isSlowWhenEmpty() == Boolean.parseBoolean(value);
            case "displayedblock": return e instanceof Minecart && UMaterial.match(((Minecart) e).getDisplayBlock().getItemType().name()).name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedMob(Entity e, String condition, String s, String value, boolean legacy) {
        switch (condition) {
            case "hastarget":
                if(!legacy) {
                    return e instanceof Mob && ((Mob) e).getTarget() != null == Boolean.parseBoolean(value);
                } else {
                    return false;
                }
            default:
                return true;
        }
    }
    private boolean passedPainting(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "art": return e instanceof Painting && ((Painting) e).getArt().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedPanda(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        switch (condition) {
            case "maingene": return legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getMainGene().name().equalsIgnoreCase(value);
            case "maingeneisrecessive": return legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getMainGene().isRecessive() == Boolean.parseBoolean(value);
            case "hiddengene": return legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getHiddenGene().name().equalsIgnoreCase(value);
            case "hiddengeneisrecessive": return legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getHiddenGene().isRecessive() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedPig(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "hassaddle": return e instanceof Pig && ((Pig) e).hasSaddle() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedPigZombie(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "isangry":
                if(e instanceof PigZombie) {
                    return ((PigZombie) e).isAngry() == Boolean.parseBoolean(value);
                } else if(e instanceof Wolf) {
                    return ((Wolf) e).isAngry() == Boolean.parseBoolean(value);
                } else {
                    return false;
                }
            default:
                return true;
        }
    }
    private boolean passedPlayer(Entity e, String condition, String s, String value, boolean legacy, boolean eight, boolean nine, boolean ten, boolean eleven) {
        switch (condition) {
            case "issneaking": return e instanceof Player && ((Player) e).isSneaking() == Boolean.parseBoolean(value);
            case "isblocking": return e instanceof Player && ((Player) e).isBlocking() == Boolean.parseBoolean(value);
            case "isflying": return e instanceof Player && ((Player) e).isFlying() == Boolean.parseBoolean(value);
            case "issprinting": return e instanceof Player && ((Player) e).isSprinting() == Boolean.parseBoolean(value);
            case "isriptiding": return !legacy && e instanceof Player && ((Player) e).isRiptiding() == Boolean.parseBoolean(value);
            case "issleepignored": return e instanceof Player && ((Player) e).isSleepingIgnored() == Boolean.parseBoolean(value);
            case "allowsflight": return e instanceof Player && ((Player) e).getAllowFlight() == Boolean.parseBoolean(value);
            case "ishealthscaled": return e instanceof Player && ((Player) e).isHealthScaled() == Boolean.parseBoolean(value);
            case "weather": return e instanceof Player && ((Player) e).getPlayerWeather().name().equalsIgnoreCase(value);
            case "totalexp": return e instanceof Player && getTotalExperience((Player) e) == Integer.parseInt(value);
            case "totalexp<": // totalexp<=
                return e instanceof Player && getTotalExperience((Player) e) <= Integer.parseInt(value);
            case "totalexp>": // totalexp>=
                return e instanceof Player && getTotalExperience((Player) e) >= Integer.parseInt(value);
            case "explevel": return e instanceof Player && ((Player) e).getLevel() == Integer.parseInt(value);
            case "explevel<": // explevel<=
                return e instanceof Player && ((Player) e).getLevel() <= Integer.parseInt(value);
            case "explevel>": // explevel>=
                return e instanceof Player && ((Player) e).getLevel() >= Integer.parseInt(value);
            case "foodlevel": return e instanceof Player && ((Player) e).getFoodLevel() == Integer.parseInt(value);
            case "foodlevel<": // foodlevel<=
                return e instanceof Player && ((Player) e).getFoodLevel() <= Integer.parseInt(value);
            case "foodlevel>": // foodlevel>=
                return e instanceof Player && ((Player) e).getFoodLevel() >= Integer.parseInt(value);
            case "saturation": return e instanceof Player && ((Player) e).getSaturation() == Float.parseFloat(value);
            case "saturation<": // saturation<=
                return e instanceof Player && ((Player) e).getSaturation() <= Float.parseFloat(value);
            case "saturation>": // saturation>=
                return e instanceof Player && ((Player) e).getSaturation() >= Float.parseFloat(value);
            case "viewdistance": return legacy || e instanceof Player && ((Player) e).getClientViewDistance() == Integer.parseInt(value);
            case "viewdistance<": // viewdistance<=
                return legacy || e instanceof Player && ((Player) e).getClientViewDistance() <= Integer.parseInt(value);
            case "viewdistance>": // viewdistance>=
                return legacy || e instanceof Player && ((Player) e).getClientViewDistance() >= Integer.parseInt(value);
            case "language": return eight || nine || ten || eleven || e instanceof Player && ((Player) e).getLocale().equalsIgnoreCase(value);
            case "walkspeed": return e instanceof Player && ((Player) e).getWalkSpeed() == Float.parseFloat(value);
            case "walkspeed<": // walkspeed<=
                return e instanceof Player && ((Player) e).getWalkSpeed() <= Float.parseFloat(value);
            case "walkspeed>": // walkspeed>=
                return e instanceof Player && ((Player) e).getWalkSpeed() >= Float.parseFloat(value);
            case "flyspeed": return e instanceof Player && ((Player) e).getFlySpeed() == Float.parseFloat(value);
            case "flyspeed<": // flyspeed<=
                return e instanceof Player && ((Player) e).getFlySpeed() <= Float.parseFloat(value);
            case "flyspeed>": // flyspeed>=
                return e instanceof Player && ((Player) e).getFlySpeed() >= Float.parseFloat(value);
            default:
                return true;
        }
    }
    private boolean passedProjectile(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "doesbounce": return e instanceof Projectile && ((Projectile) e).doesBounce() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedRabbit(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "rabbittype": return e instanceof Rabbit && ((Rabbit) e).getRabbitType().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedRaider(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        switch (condition) {
            case "ispatrolleader": return legacy || thirteen ? false : e instanceof Raider && ((Raider) e).isPatrolLeader() == Boolean.parseBoolean(value);
            case "patroltargetblock": return legacy ? false : e instanceof Raider && UMaterial.match(((Raider) e).getPatrolTarget().getType().name()).name().toLowerCase().endsWith(value);
            default: return true;
        }
    }
    private boolean passedSheep(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "issheared": return e instanceof Sheep && ((Sheep) e).isSheared() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedSittable(Entity e, String condition, String s, String value, boolean eight, boolean nine, boolean ten, boolean eleven) {
        switch (condition) {
            case "issitting":
                final boolean first = Boolean.parseBoolean(value);
                if(eight || nine || ten || eleven) {
                    return first == e instanceof Wolf && ((Wolf) e).isSitting()/* || e instanceof Ocelot && ((Ocelot) e).isSitting()*/;
                } else {
                    return first == e instanceof Sittable && ((Sittable) e).isSitting();
                }
            default:
                return true;
        }
    }
    private boolean passedSkeleton(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "skeletontype": return e instanceof Skeleton && ((Skeleton) e).getSkeletonType().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedSlime(Entity e, String condition, String s, String value, boolean legacy) {
        switch (condition) {
            case "size":
                int v = Integer.parseInt(value);
                return e instanceof Slime && ((Slime) e).getSize() == v || !legacy && e instanceof Phantom && ((Phantom) e).getSize() == v;
            case "size<": // size<=
                v = Integer.parseInt(value);
                return e instanceof Slime && ((Slime) e).getSize() <= v || !legacy && e instanceof Phantom && ((Phantom) e).getSize() <= v;
            case "size>": // size>=
                v = Integer.parseInt(value);
                return e instanceof Slime && ((Slime) e).getSize() >= v || !legacy && e instanceof Phantom && ((Phantom) e).getSize() >= v;
            default:
                return true;
        }
    }
    private boolean passedSnowman(Entity e, String condition, String s, String value, boolean eight, boolean nine) {
        switch (condition) {
            case "isderp": return eight || nine ? false : e instanceof Snowman && ((Snowman) e).isDerp() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedTameable(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "istamed": return e instanceof Tameable && ((Tameable) e).isTamed() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedTropicalFish(Entity e, String condition, String s, String value, boolean legacy) {
        switch (condition) {
            case "patterncolor": return legacy ? false : e instanceof TropicalFish && ((TropicalFish) e).getPatternColor().name().equalsIgnoreCase(value);
            case "bodycolor": return legacy ? false : e instanceof TropicalFish && ((TropicalFish) e).getBodyColor().name().equalsIgnoreCase(value);
            case "pattern": return legacy ? false : e instanceof TropicalFish && ((TropicalFish) e).getPattern().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedVex(Entity e, String condition, String s, String value, boolean eight, boolean nine, boolean ten) {
        switch (condition) {
            case "ischarging": return eight || nine || ten ? false : e instanceof Vex && ((Vex) e).isCharging() == Boolean.valueOf(value);
            default: return true;
        }
    }
    private boolean passedVillager(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        switch (condition) {
            case "profession": return e instanceof Zombie && ((Zombie) e).isVillager() ? ((Zombie) e).getVillagerProfession().name().equalsIgnoreCase(value) : e instanceof Villager && ((Villager) e).getProfession().name().equalsIgnoreCase(value);
            case "villagertype": return e instanceof Villager && !(legacy || thirteen) && ((Villager) e).getVillagerType().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    private boolean passedWitherSkull(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "ischarged": return e instanceof WitherSkull && ((WitherSkull) e).isCharged() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    private boolean passedZombie(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "isvillager": return e instanceof Zombie && ((Zombie) e).isVillager() == Boolean.parseBoolean(value);
            default: return true;
        }
    }



    private boolean passedEvent(Event event, Entity e, String condition, String s, String value) {
        switch (condition) {
            case "action":
                final String action = event instanceof PlayerInteractEvent ? ((PlayerInteractEvent) event).getAction().name() : null;
                return action != null && action.startsWith(value.toUpperCase());
            case "cause":
                final EntityDamageEvent d = event instanceof EntityDamageEvent ? (EntityDamageEvent) event : null;
                if(d != null) {
                    final String cause = d.getCause().name();
                    final String[] v = value.split("\\|\\|");
                    final List<Boolean> did = new ArrayList<>();
                    for(String ss : v) {
                        did.add(cause.equalsIgnoreCase(ss));
                    }
                    return did.contains(true);
                }
                return false;
            case "tier":
                return event instanceof PlayerClaimEnvoyCrateEvent && ((PlayerClaimEnvoyCrateEvent) event).type.getType().getIdentifier().equals(value);
            case "israritybook":
                return event instanceof EnchanterPurchaseEvent && valueOfEnchantRarity(valueOfCustomEnchant(((EnchanterPurchaseEvent) event).purchased)) != null;
            case "result":
                return event instanceof CustomEnchantApplyEvent && ((CustomEnchantApplyEvent) event).result.equalsIgnoreCase(value);
            case "rarity":
                String identifier = null;
                if(event instanceof CustomEnchantApplyEvent) {
                    identifier = valueOfEnchantRarity(((CustomEnchantApplyEvent) event).enchant).getIdentifier();
                } else if(event instanceof EnchanterPurchaseEvent) {
                    final EnchanterPurchaseEvent epe = (EnchanterPurchaseEvent) event;
                    final CustomEnchant enchant = valueOfCustomEnchant(epe.purchased);
                    final EnchantRarity rarity = enchant != null ? valueOfEnchantRarity(enchant) : null;
                    identifier = rarity != null ? rarity.getIdentifier() : null;
                } else if(event instanceof RandomizationScrollUseEvent) {
                    identifier = ((RandomizationScrollUseEvent) event).scroll.getIdentifier();
                } else if(event instanceof ServerCrateOpenEvent) {
                    identifier = ((ServerCrateOpenEvent) event).crate.getIdentifier();
                }
                return identifier != null && identifier.equals(value);
            case "enchant":
                CustomEnchant enchant = null;
                if(event instanceof CustomEnchantApplyEvent) {
                    enchant = ((CustomEnchantApplyEvent) event).enchant;
                } else if(event instanceof CustomEnchantProcEvent) {
                    enchant = ((CustomEnchantProcEvent) event).getEnchant();
                }
                return enchant != null && enchant.getIdentifier().equals(value);
            case "success<": // success<=
                return event instanceof CustomEnchantApplyEvent && ((CustomEnchantApplyEvent) event).success <= evaluate(value);
            case "destroy<": // destroy<=
                return event instanceof CustomEnchantApplyEvent && ((CustomEnchantApplyEvent) event).destroy <= evaluate(value);
            case "booster":
                return event instanceof BoosterActivateEvent && ((BoosterActivateEvent) event).booster.getIdentifier().equals(value);
            case "inventorypetoncooldown":
                ItemStack is = null;
                if(event instanceof PlayerInteractEvent) {
                    is = ((PlayerInteractEvent) event).getItem();
                }
                String info = is != null ? getRPItemStackValue(is, "InventoryPetInfo") : null;
                return info != null && System.currentTimeMillis() >= Long.parseLong(info.split(":")[3]) == Boolean.parseBoolean(value);
            case "trinketoncooldown":
                is = null;
                if(event instanceof PlayerInteractEvent) {
                    is = ((PlayerInteractEvent) event).getItem();
                }
                info = is != null ? getRPItemStackValue(is, "TrinketInfo") : null;
                return info != null && System.currentTimeMillis() >= Long.parseLong(info.split(":")[1]) == Boolean.parseBoolean(value);
            case "kittype":
                final CustomKit kit = e instanceof KitPreClaimEvent ? ((KitPreClaimEvent) e).getKit() : e instanceof KitClaimEvent ? ((KitClaimEvent) e).getKit() : null;
                return kit != null && kit.getIdentifier().startsWith(value);
            default:
                return true;
        }
    }
    private boolean passedCustomCondition(Event event, Entity e, String condition, String s, String value) {
        final String target = condition.startsWith(s) ? condition.split(s)[1] : condition;
        final EventCondition con = getEventCondition(target.toUpperCase());
        if(con != null) {
            return con.check(value) && con.check(event) && con.check(event, value) && con.check(event, e) && con.check(e, value);
        }
        return true;
    }
    private boolean passedRandomPackage(Entity e, String condition, String s, String value) {
        switch (condition) {
            case "equippedarmorset":
                final ArmorSet armorset = e instanceof Player ? valueOfArmorSet((Player) e) : null;
                return armorset != null && armorset.getIdentifier().equals(value);
            case "equippedmask":
                final EntityEquipment ee = e instanceof Player ? ((Player) e).getEquipment() : null;
                final Mask mask = ee != null ? valueOfMask(ee.getHelmet()) : null;
                return mask != null && mask.getIdentifier().equals(value);
            case "equippedtitle":
                Title t = RPPlayer.get(e.getUniqueId()).getActiveTitle();
                return t != null &&  e instanceof Player && t.getIdentifier().equals(value);
            case "ownstitle":
                t = e instanceof Player ? getTitle(value) : null;
                return t != null && RPPlayer.get(e.getUniqueId()).getTitles().contains(t);
            case "hasactivefilter":
                return e instanceof Player && RPPlayer.get(e.getUniqueId()).filter == Boolean.parseBoolean(value);
            case "hasactiveplayerquest":
                final PlayerQuest q = e instanceof Player ? getPlayerQuest(value) : null;
                final HashMap<PlayerQuest, ActivePlayerQuest> pquests = q != null ? RPPlayer.get(e.getUniqueId()).getQuests() : null;
                return pquests != null && pquests.containsKey(q) && !pquests.get(q).isExpired();
            case "hasactiveraritygem":
                final String[] values = value.split(":");
                final int l = values.length;
                return e instanceof Player && RPPlayer.get(e.getUniqueId()).hasActiveRarityGem(getRarityGem(values[0])) == (l < 2 || Boolean.parseBoolean(values[1]));
            case "hasactivetitle":
                return e instanceof Player && RPPlayer.get(e.getUniqueId()).getActiveTitle() != null == Boolean.parseBoolean(value);
            case "hascustomentities":
                return e instanceof Player && !RPPlayer.get(e.getUniqueId()).getCustomEnchantEntities().isEmpty() == Boolean.parseBoolean(value);
            case "hasequippedarmorset":
                return e instanceof Player && valueOfArmorSet((Player) e) != null == Boolean.parseBoolean(value);
            case "hasequippedmask":
                final EntityEquipment eq = e instanceof Player ? ((Player) e).getEquipment() : null;
                return eq != null && valueOfMask(eq.getHelmet()) != null == Boolean.parseBoolean(value);
            case "hasfiltereditem":
                final List<UMaterial> m = e instanceof Player ? RPPlayer.get(e.getUniqueId()).getFilteredItems() : null;
                return m != null && m.contains(UMaterial.match(value));
            case "iscustomboss":
                return LivingCustomBoss.living != null && LivingCustomBoss.living.containsKey(e.getUniqueId()) == Boolean.parseBoolean(value);
            default:
                return true;
        }
    }
}
