package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.addon.living.LivingCustomBoss;
import me.randomhashtags.randompackage.addon.util.Mathable;
import me.randomhashtags.randompackage.attribute.Combo;
import me.randomhashtags.randompackage.attribute.EventCondition;
import me.randomhashtags.randompackage.util.RPStorage;
import me.randomhashtags.randompackage.event.EnchanterPurchaseEvent;
import me.randomhashtags.randompackage.event.PlayerClaimEnvoyCrateEvent;
import me.randomhashtags.randompackage.event.RandomizationScrollUseEvent;
import me.randomhashtags.randompackage.event.ServerCrateOpenEvent;
import me.randomhashtags.randompackage.event.booster.BoosterActivateEvent;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantApplyEvent;
import me.randomhashtags.randompackage.event.enchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.event.kit.KitClaimEvent;
import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import me.randomhashtags.randompackage.supported.mechanics.MCMMOAPI;
import me.randomhashtags.randompackage.util.RPItemStack;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionable;
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

public interface EventConditions extends Combo, RPItemStack, Mathable, UVersionable, RPStorage {
    List<UUID> SPAWNED_FROM_SPAWNER = new ArrayList<>();
    HashMap<UUID, EntityShootBowEvent> PROJECTILE_EVENTS = new HashMap<>();

    default boolean passedAllConditions(Event event, HashMap<String, Entity> entities, String entityKey, Entity entity, String condition, String key, String value) {
        final boolean pre = passedCustomCondition(event, entities, entityKey, entity, condition, key, value) && passedEvent(event, entity, condition, value), isEntity = condition.startsWith(key);
        condition = condition.substring(key.length()).split("=")[0];
        return pre && (!isEntity
                || passedBasic(entity, condition, value)
                && passedAgeable(entity, condition, value)
                && passedAnimals(entity, condition, value)
                && passedArmorStand(entity, condition, value)
                && passedBat(entity, condition, value)
                && passedCat(entity, condition, value)
                && passedChestedHorse(entity, condition, value)
                && passedCreeper(entity, condition, value)
                && passedEnderCrystal(entity, condition, value)
                && passedEnderDragon(entity, condition, value)
                && passedEnderman(entity, condition, value)
                && passedEndermite(entity, condition, value)
                && passedEntity(entity, condition, value)
                && passedEvoker(entity, condition, value)
                && passedExplosive(entity, condition, value)
                && passedFallenBlock(event, entity, condition, value)
                && passedFirework(entity, condition, value)
                && passedFox(entity, condition, value)
                && passedGuardian(entity, condition, value)
                && passedHorse(entity, condition, value)
                && passedHusk(entity, condition, value)
                && passedIronGolem(entity, condition, value)
                && passedLightingStrike(entity, condition, value)
                && passedLivingEntity(entity, condition, value)
                && passedMinecart(entity, condition, value)
                && passedMob(entity, condition, value)
                && passedPainting(entity, condition, value)
                && passedPanda(entity, condition, value)
                && passedPig(entity, condition, value)
                && passedPigZombie(entity, condition, value)
                && passedPlayer(entity, condition, value)
                && passedProjectile(entity, condition, value)
                && passedRabbit(entity, condition, value)
                && passedRaider(entity, condition, value)
                && passedRandomPackage(entity, condition, value)
                && passedSheep(entity, condition, value)
                && passedSittable(entity, condition, value)
                && passedSkeleton(entity, condition, value)
                && passedSlime(entity, condition, value)
                && passedSnowman(entity, condition, value)
                && passedTameable(entity, condition, value)
                && passedTropicalFish(entity, condition, value)
                && passedVex(entity, condition, value)
                && passedVillager(entity, condition, value)
                && passedWitherSkull(entity, condition, value)
                && passedZombie(entity, condition, value))
        ;
    }

    default boolean passedBasic(Entity entity, String condition, String value) {
        switch (condition) {
            case "isfromspawner": return SPAWNED_FROM_SPAWNER.contains(entity.getUniqueId()) == Boolean.parseBoolean(value);
            case "isplayer": return entity instanceof Player == Boolean.parseBoolean(value);
            case "ismob": return LEGACY ? entity instanceof Creature : entity instanceof Mob == Boolean.parseBoolean(value);
            case "ismonster": return entity instanceof Monster == Boolean.parseBoolean(value);
            case "iscreature": return entity instanceof Creature == Boolean.parseBoolean(value);
            case "isanimal": return entity instanceof Animals == Boolean.parseBoolean(value);
            case "isflying": return entity instanceof Flying || entity instanceof Player && ((Player) entity).isFlying() == Boolean.parseBoolean(value);
            case "istype": return entity.getType().name().equalsIgnoreCase(value);
            case "isfacing": return getFacing(entity).name().toLowerCase().startsWith(value);
            case "isop": return entity.isOp() == Boolean.parseBoolean(value);
            case "isinsidevehicle": return entity.isInsideVehicle() == Boolean.parseBoolean(value);
            case "isriding": return entity.isInsideVehicle() && entity.getVehicle().getType().name().equalsIgnoreCase(value);
            case "iscustomnamevisible": return entity.isCustomNameVisible() == Boolean.parseBoolean(value);
            case "isaggressive": return isAggressive(entity.getType()) == Boolean.parseBoolean(value);
            case "isneutral": return isNeutral(entity.getType()) == Boolean.parseBoolean(value);
            case "ispassive": return isPassive(entity.getType()) == Boolean.parseBoolean(value);
            case "isonground": return entity.isOnGround() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedAgeable(Entity entity, String condition, String value) {
        switch (condition) {
            case "isadult": return entity instanceof Ageable && ((Ageable) entity).isAdult() == Boolean.parseBoolean(value);
            case "isbaby": return entity instanceof Zombie && ((Zombie) entity).isBaby() || entity instanceof Ageable && ((Ageable) entity).isAdult() != Boolean.parseBoolean(value);
            case "canbreed": return entity instanceof Ageable && ((Ageable) entity).canBreed() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedAnimals(Entity entity, String condition, String value) {
        switch (condition) {
            case "inlovemode": return !LEGACY && !THIRTEEN && entity instanceof Animals && ((Animals) entity).isLoveMode() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedArmorStand(Entity entity, String condition, String value) {
        switch (condition) {
            case "hasbaseplate": return entity instanceof ArmorStand && ((ArmorStand) entity).hasBasePlate() == Boolean.parseBoolean(value);
            case "hasarms": return entity instanceof ArmorStand && ((ArmorStand) entity).hasArms() == Boolean.parseBoolean(value);
            case "ismarker": return entity instanceof ArmorStand && ((ArmorStand) entity).isMarker() == Boolean.parseBoolean(value);
            case "issmall": return entity instanceof ArmorStand && ((ArmorStand) entity).isSmall() == Boolean.parseBoolean(value);
            case "isvisible": return entity instanceof ArmorStand && ((ArmorStand) entity).isVisible() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedBat(Entity entity, String condition, String value) {
        switch (condition) {
            case "isawake": return entity instanceof Bat && ((Bat) entity).isAwake() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedCat(Entity entity, String condition, String value) {
        switch (condition) {
            case "cattype": return LEGACY ? entity instanceof Ocelot && ((Ocelot) entity).getCatType().name().equalsIgnoreCase(value) : entity instanceof Cat && ((Cat) entity).getCatType().name().equalsIgnoreCase(value);
            case "collarcolor":
                if(entity instanceof Wolf) {
                    return ((Wolf) entity).getCollarColor().name().equalsIgnoreCase(value);
                } else if(!LEGACY && !THIRTEEN) {
                    return entity instanceof Cat && ((Cat) entity).getCollarColor().name().equalsIgnoreCase(value);
                } else {
                    return false;
                }
            default:
                return true;
        }
    }
    default boolean passedChestedHorse(Entity entity, String condition, String value) {
        switch (condition) {
            case "iscarryingchest":
                if(EIGHT || NINE || TEN) {
                    return entity instanceof Horse && ((Horse) entity).isCarryingChest() == Boolean.parseBoolean(value);
                } else {
                    return entity instanceof ChestedHorse && ((ChestedHorse) entity).isCarryingChest() == Boolean.parseBoolean(value);
                }
            default: return true;
        }
    }
    default boolean passedCreeper(Entity entity, String condition, String value) {
        switch (condition) {
            case "ispowered": return entity instanceof Creeper && ((Creeper) entity).isPowered() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedEnderCrystal(Entity entity, String condition, String value) {
        switch (condition) {
            case "isshowingbottom": return EIGHT ? true : entity instanceof EnderCrystal && ((EnderCrystal) entity).isShowingBottom() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedEnderDragon(Entity entity, String condition, String value) {
        switch (condition) {
            case "phase": return EIGHT ? true : entity instanceof EnderDragon && ((EnderDragon) entity).getPhase().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedEnderman(Entity entity, String condition, String value) {
        switch (condition) {
            case "iscarrying": return entity instanceof Enderman && UMaterial.match(((Enderman) entity).getCarriedMaterial().getItemType().name()).name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedEndermite(Entity entity, String condition, String value) {
        switch (condition) {
            case "isplayerspawned": return LEGACY || THIRTEEN ? false : entity instanceof Endermite && ((Endermite) entity).isPlayerSpawned() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedEntity(Entity entity, String condition, String value) {
        switch (condition) {
            case "inbiome":
                final Chunk chunk = entity.getLocation().getChunk();
                return entity.getWorld().getBiome(chunk.getX(), chunk.getZ()).name().equalsIgnoreCase(value);
            case "inworld": return entity.getWorld().getName().equals(value);
            case "isglowing": return !EIGHT && entity.isGlowing() == Boolean.parseBoolean(value);
            case "isinvulnerable": return !EIGHT && entity.isInvulnerable() == Boolean.parseBoolean(value);
            case "issilent": return !EIGHT && !NINE && entity.isSilent() == Boolean.parseBoolean(value);
            case "isitem": return entity instanceof Item == Boolean.parseBoolean(value);
            case "hasgravity": return entity instanceof ArmorStand && ((ArmorStand) entity).hasGravity() || !EIGHT && !NINE && !TEN && entity.hasGravity() == Boolean.parseBoolean(value);
            case "worlddifficulty": return entity.getWorld().getDifficulty().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedEvoker(Entity entity, String condition, String value) {
        switch (condition) {
            case "currentspell": return EIGHT || NINE | TEN ? false : entity instanceof Evoker && ((Evoker) entity).getCurrentSpell().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedExplosive(Entity entity, String condition, String value) {
        switch (condition) {
            case "isincendiary": return entity instanceof Explosive && ((Explosive) entity).isIncendiary() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedFallenBlock(Event event, Entity entity, String condition, String value) {
        switch (condition) {
            case "material":
                return entity instanceof FallingBlock && value.equalsIgnoreCase(UMaterial.match(((FallingBlock) entity).getMaterial().name()).name())
                    || event instanceof BlockPlaceEvent && value.equalsIgnoreCase(UMaterial.match(((BlockPlaceEvent) event).getBlock().getType().name()).name())
                    || event instanceof BlockBreakEvent && value.equalsIgnoreCase(UMaterial.match(((BlockBreakEvent) event).getBlock().getType().name()).name());
            case "canhurtentities": return entity instanceof FallingBlock && ((FallingBlock) entity).canHurtEntities() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedFirework(Entity entity, String condition, String value) {
        switch (condition) {
            case "isshotatangle": return LEGACY || THIRTEEN ? false : entity instanceof Firework && ((Firework) entity).isShotAtAngle() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedFox(Entity entity, String condition, String value) {
        switch (condition) {
            case "foxtype": return LEGACY || THIRTEEN ? false : entity instanceof Fox && ((Fox) entity).getFoxType().name().equalsIgnoreCase(value);
            case "iscrouching": return LEGACY || THIRTEEN ? false : entity instanceof Fox && ((Fox) entity).isCrouching() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedGuardian(Entity entity, String condition, String value) {
        switch (condition) {
            case "iselder": return entity instanceof Guardian && ((Guardian) entity).isElder() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedHorse(Entity entity, String condition, String value) {
        switch (condition) {
            case "isvariant":
                if(entity instanceof Horse) {
                    return ((Horse) entity).getVariant().name().equalsIgnoreCase(value);
                } else if(entity instanceof MushroomCow && (!LEGACY || THIRTEEN)) {
                    return ((MushroomCow) entity).getVariant().name().equalsIgnoreCase(value);
                } else if(!(EIGHT || NINE || TEN || ELEVEN)) {
                    return entity instanceof Parrot && ((Parrot) entity).getVariant().name().equalsIgnoreCase(value);
                } else {
                    return false;
                }
            case "color":
                if(entity instanceof Horse) {
                    return ((Horse) entity).getColor().name().equalsIgnoreCase(value);
                } else if(!(EIGHT || NINE || TEN)) {
                    return entity instanceof Llama && ((Llama) entity).getColor().name().equalsIgnoreCase(value);
                } else if(entity instanceof Colorable) {
                    return ((Colorable) entity).getColor().name().equalsIgnoreCase(value);
                } else {
                    return false;
                }
            case "style": return entity instanceof Horse && ((Horse) entity).getStyle().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedHusk(Entity entity, String condition, String value) {
        switch (condition) {
            case "isconverting":
                if(LEGACY || THIRTEEN) {
                    return false;
                } else {
                    final boolean b = Boolean.parseBoolean(value);
                    return b && (entity instanceof Husk && ((Husk) entity).isConverting() || entity instanceof PigZombie && ((PigZombie) entity).isConverting() || entity instanceof Zombie && ((Zombie) entity).isConverting());
                }
            default:
                return true;
        }
    }
    default boolean passedIronGolem(Entity entity, String condition, String value) {
        switch (condition) {
            case "isplayercreated": return entity instanceof IronGolem && ((IronGolem) entity).isPlayerCreated() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedLightingStrike(Entity entity, String condition, String value) {
        switch (condition) {
            case "iseffect": return entity instanceof LightningStrike && ((LightningStrike) entity).isEffect() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedLivingEntity(Entity entity, String condition, String value) {
        switch (condition) {
            case "isleashed": return entity instanceof LivingEntity && ((LivingEntity) entity).isLeashed() == Boolean.parseBoolean(value);
            case "isswimming": return LEGACY ? false : entity instanceof LivingEntity && ((LivingEntity) entity).isSwimming() == Boolean.parseBoolean(value);
            case "isgliding": return EIGHT ? false : entity instanceof LivingEntity && ((LivingEntity) entity).isGliding() == Boolean.parseBoolean(value);
            case "isholding": return entity instanceof LivingEntity && ((LivingEntity) entity).getEquipment().getItemInHand().getType().name().toLowerCase().endsWith(value);
            case "issleeping": return LEGACY || THIRTEEN ? false : entity instanceof LivingEntity && ((LivingEntity) entity).isSleeping() == Boolean.parseBoolean(value);
            case "hasai": return EIGHT || entity instanceof LivingEntity && ((LivingEntity) entity).hasAI() == Boolean.parseBoolean(value);
            case "iscollideable": return EIGHT || entity instanceof LivingEntity && ((LivingEntity) entity).isCollidable() == Boolean.parseBoolean(value);
            case "health<": // health<=
                return entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= evaluate(value);
            case "health>": // health>=
                return entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() >= evaluate(value);
            case "haspotioneffect":
                final PotionEffectType t = getPotionEffectType(value);
                return t != null && entity instanceof LivingEntity && ((LivingEntity) entity).hasPotionEffect(t);
            case "nodamageticks<": // nodamageticks<=
                return entity instanceof LivingEntity && ((LivingEntity) entity).getNoDamageTicks() <= evaluate(value);
            case "nodamageticks>": // nodamageticks>=
                return entity instanceof LivingEntity && ((LivingEntity) entity).getNoDamageTicks() >= evaluate(value);
            case "remainingair": return entity instanceof LivingEntity && ((LivingEntity) entity).getRemainingAir() == evaluate(value);
            case "remainingair<": // remainingair<=
                return entity instanceof LivingEntity && ((LivingEntity) entity).getRemainingAir() <= evaluate(value);
            case "remainingair>": // remainingair>=
                return entity instanceof LivingEntity && ((LivingEntity) entity).getRemainingAir() >= evaluate(value);
            default: return true;
        }
    }
    default boolean passedMinecart(Entity entity, String condition, String value) {
        switch (condition) {
            case "isslowwhenempty": return entity instanceof Minecart && ((Minecart) entity).isSlowWhenEmpty() == Boolean.parseBoolean(value);
            case "displayedblock": return entity instanceof Minecart && UMaterial.match(((Minecart) entity).getDisplayBlock().getItemType().name()).name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedMob(Entity entity, String condition, String value) {
        switch (condition) {
            case "hastarget":
                if(!LEGACY) {
                    return entity instanceof Mob && ((Mob) entity).getTarget() != null == Boolean.parseBoolean(value);
                } else {
                    return false;
                }
            default:
                return true;
        }
    }
    default boolean passedPainting(Entity entity, String condition, String value) {
        switch (condition) {
            case "art": return entity instanceof Painting && ((Painting) entity).getArt().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedPanda(Entity entity, String condition, String value) {
        switch (condition) {
            case "maingene": return LEGACY || THIRTEEN ? false : entity instanceof Panda && ((Panda) entity).getMainGene().name().equalsIgnoreCase(value);
            case "maingeneisrecessive": return LEGACY || THIRTEEN ? false : entity instanceof Panda && ((Panda) entity).getMainGene().isRecessive() == Boolean.parseBoolean(value);
            case "hiddengene": return LEGACY || THIRTEEN ? false : entity instanceof Panda && ((Panda) entity).getHiddenGene().name().equalsIgnoreCase(value);
            case "hiddengeneisrecessive": return LEGACY || THIRTEEN ? false : entity instanceof Panda && ((Panda) entity).getHiddenGene().isRecessive() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedPig(Entity entity, String condition, String value) {
        switch (condition) {
            case "hassaddle": return entity instanceof Pig && ((Pig) entity).hasSaddle() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedPigZombie(Entity entity, String condition, String value) {
        switch (condition) {
            case "isangry":
                if(entity instanceof PigZombie) {
                    return ((PigZombie) entity).isAngry() == Boolean.parseBoolean(value);
                } else if(entity instanceof Wolf) {
                    return ((Wolf) entity).isAngry() == Boolean.parseBoolean(value);
                } else {
                    return false;
                }
            default:
                return true;
        }
    }
    default boolean passedPlayer(Entity entity, String condition, String value) {
        switch (condition) {
            case "issneaking": return entity instanceof Player && ((Player) entity).isSneaking() == Boolean.parseBoolean(value);
            case "isblocking": return entity instanceof Player && ((Player) entity).isBlocking() == Boolean.parseBoolean(value);
            case "isflying": return entity instanceof Player && ((Player) entity).isFlying() == Boolean.parseBoolean(value);
            case "issprinting": return entity instanceof Player && ((Player) entity).isSprinting() == Boolean.parseBoolean(value);
            case "isriptiding": return !LEGACY && entity instanceof Player && ((Player) entity).isRiptiding() == Boolean.parseBoolean(value);
            case "issleepignored": return entity instanceof Player && ((Player) entity).isSleepingIgnored() == Boolean.parseBoolean(value);
            case "allowsflight": return entity instanceof Player && ((Player) entity).getAllowFlight() == Boolean.parseBoolean(value);
            case "ishealthscaled": return entity instanceof Player && ((Player) entity).isHealthScaled() == Boolean.parseBoolean(value);
            case "weather": return entity instanceof Player && ((Player) entity).getPlayerWeather().name().equalsIgnoreCase(value);
            case "totalexp": return entity instanceof Player && getTotalExperience((Player) entity) == (int) evaluate(value);
            case "totalexp<": // totalexp<=
                return entity instanceof Player && getTotalExperience((Player) entity) <= (int) evaluate(value);
            case "totalexp>": // totalexp>=
                return entity instanceof Player && getTotalExperience((Player) entity) >= (int) evaluate(value);
            case "explevel": return entity instanceof Player && ((Player) entity).getLevel() == (int) evaluate(value);
            case "explevel<": // explevel<=
                return entity instanceof Player && ((Player) entity).getLevel() <= (int) evaluate(value);
            case "explevel>": // explevel>=
                return entity instanceof Player && ((Player) entity).getLevel() >= (int) evaluate(value);
            case "foodlevel": return entity instanceof Player && ((Player) entity).getFoodLevel() == (int) evaluate(value);
            case "foodlevel<": // foodlevel<=
                return entity instanceof Player && ((Player) entity).getFoodLevel() <= (int) evaluate(value);
            case "foodlevel>": // foodlevel>=
                return entity instanceof Player && ((Player) entity).getFoodLevel() >= (int) evaluate(value);
            case "saturation": return entity instanceof Player && ((Player) entity).getSaturation() == Float.parseFloat(value);
            case "saturation<": // saturation<=
                return entity instanceof Player && ((Player) entity).getSaturation() <= Float.parseFloat(value);
            case "saturation>": // saturation>=
                return entity instanceof Player && ((Player) entity).getSaturation() >= Float.parseFloat(value);
            case "viewdistance": return LEGACY || entity instanceof Player && ((Player) entity).getClientViewDistance() == (int) evaluate(value);
            case "viewdistance<": // viewdistance<=
                return LEGACY || entity instanceof Player && ((Player) entity).getClientViewDistance() <= (int) evaluate(value);
            case "viewdistance>": // viewdistance>=
                return LEGACY || entity instanceof Player && ((Player) entity).getClientViewDistance() >= (int) evaluate(value);
            case "language": return EIGHT || NINE || TEN || ELEVEN || entity instanceof Player && ((Player) entity).getLocale().equalsIgnoreCase(value);
            case "walkspeed": return entity instanceof Player && ((Player) entity).getWalkSpeed() == Float.parseFloat(value);
            case "walkspeed<": // walkspeed<=
                return entity instanceof Player && ((Player) entity).getWalkSpeed() <= Float.parseFloat(value);
            case "walkspeed>": // walkspeed>=
                return entity instanceof Player && ((Player) entity).getWalkSpeed() >= Float.parseFloat(value);
            case "flyspeed": return entity instanceof Player && ((Player) entity).getFlySpeed() == Float.parseFloat(value);
            case "flyspeed<": // flyspeed<=
                return entity instanceof Player && ((Player) entity).getFlySpeed() <= Float.parseFloat(value);
            case "flyspeed>": // flyspeed>=
                return entity instanceof Player && ((Player) entity).getFlySpeed() >= Float.parseFloat(value);
            default:
                return true;
        }
    }
    default boolean passedProjectile(Entity entity, String condition, String value) {
        switch (condition) {
            case "doesbounce": return entity instanceof Projectile && ((Projectile) entity).doesBounce() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedRabbit(Entity entity, String condition, String value) {
        switch (condition) {
            case "rabbittype": return entity instanceof Rabbit && ((Rabbit) entity).getRabbitType().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedRaider(Entity entity, String condition, String value) {
        switch (condition) {
            case "ispatrolleader": return LEGACY || THIRTEEN ? false : entity instanceof Raider && ((Raider) entity).isPatrolLeader() == Boolean.parseBoolean(value);
            case "patroltargetblock": return LEGACY ? false : entity instanceof Raider && UMaterial.match(((Raider) entity).getPatrolTarget().getType().name()).name().toLowerCase().endsWith(value);
            default: return true;
        }
    }
    default boolean passedSheep(Entity entity, String condition, String value) {
        switch (condition) {
            case "issheared": return entity instanceof Sheep && ((Sheep) entity).isSheared() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedSittable(Entity entity, String condition, String value) {
        switch (condition) {
            case "issitting":
                final boolean first = Boolean.parseBoolean(value);
                if(EIGHT || NINE || TEN || ELEVEN) {
                    return first == entity instanceof Wolf && ((Wolf) entity).isSitting()/* || e instanceof Ocelot && ((Ocelot) e).isSitting()*/;
                } else {
                    return first == entity instanceof Sittable && ((Sittable) entity).isSitting();
                }
            default:
                return true;
        }
    }
    default boolean passedSkeleton(Entity entity, String condition, String value) {
        switch (condition) {
            case "skeletontype": return entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedSlime(Entity entity, String condition, String value) {
        switch (condition) {
            case "size":
                int v = (int) evaluate(value);
                return entity instanceof Slime && ((Slime) entity).getSize() == v || !LEGACY && entity instanceof Phantom && ((Phantom) entity).getSize() == v;
            case "size<": // size<=
                v = (int) evaluate(value);
                return entity instanceof Slime && ((Slime) entity).getSize() <= v || !LEGACY && entity instanceof Phantom && ((Phantom) entity).getSize() <= v;
            case "size>": // size>=
                v = (int) evaluate(value);
                return entity instanceof Slime && ((Slime) entity).getSize() >= v || !LEGACY && entity instanceof Phantom && ((Phantom) entity).getSize() >= v;
            default:
                return true;
        }
    }
    default boolean passedSnowman(Entity entity, String condition, String value) {
        switch (condition) {
            case "isderp": return EIGHT || NINE ? false : entity instanceof Snowman && ((Snowman) entity).isDerp() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedTameable(Entity entity, String condition, String value) {
        switch (condition) {
            case "istamed": return entity instanceof Tameable && ((Tameable) entity).isTamed() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedTropicalFish(Entity entity, String condition, String value) {
        switch (condition) {
            case "patterncolor": return LEGACY ? false : entity instanceof TropicalFish && ((TropicalFish) entity).getPatternColor().name().equalsIgnoreCase(value);
            case "bodycolor": return LEGACY ? false : entity instanceof TropicalFish && ((TropicalFish) entity).getBodyColor().name().equalsIgnoreCase(value);
            case "pattern": return LEGACY ? false : entity instanceof TropicalFish && ((TropicalFish) entity).getPattern().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedVex(Entity entity, String condition, String value) {
        switch (condition) {
            case "ischarging": return EIGHT || NINE || TEN ? false : entity instanceof Vex && ((Vex) entity).isCharging() == Boolean.valueOf(value);
            default: return true;
        }
    }
    default boolean passedVillager(Entity entity, String condition, String value) {
        switch (condition) {
            case "profession": return entity instanceof Zombie && ((Zombie) entity).isVillager() ? ((Zombie) entity).getVillagerProfession().name().equalsIgnoreCase(value) : entity instanceof Villager && ((Villager) entity).getProfession().name().equalsIgnoreCase(value);
            case "villagertype": return entity instanceof Villager && !(LEGACY || THIRTEEN) && ((Villager) entity).getVillagerType().name().equalsIgnoreCase(value);
            default: return true;
        }
    }
    default boolean passedWitherSkull(Entity entity, String condition, String value) {
        switch (condition) {
            case "ischarged": return entity instanceof WitherSkull && ((WitherSkull) entity).isCharged() == Boolean.parseBoolean(value);
            default: return true;
        }
    }
    default boolean passedZombie(Entity entity, String condition, String value) {
        switch (condition) {
            case "isvillager": return entity instanceof Zombie && ((Zombie) entity).isVillager() == Boolean.parseBoolean(value);
            default: return true;
        }
    }



    default boolean passedEvent(Event event, Entity entity, String condition, String value) {
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
                return event instanceof EnchanterPurchaseEvent && valueOfCustomEnchantRarity(valueOfCustomEnchant(((EnchanterPurchaseEvent) event).purchased)) != null;
            case "result":
                return event instanceof CustomEnchantApplyEvent && ((CustomEnchantApplyEvent) event).result.equalsIgnoreCase(value);
            case "rarity":
                String identifier = null;
                if(event instanceof CustomEnchantApplyEvent) {
                    identifier = valueOfCustomEnchantRarity(((CustomEnchantApplyEvent) event).enchant).getIdentifier();
                } else if(event instanceof EnchanterPurchaseEvent) {
                    final EnchanterPurchaseEvent epe = (EnchanterPurchaseEvent) event;
                    final CustomEnchant enchant = valueOfCustomEnchant(epe.purchased);
                    final EnchantRarity rarity = enchant != null ? valueOfCustomEnchantRarity(enchant) : null;
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
                final CustomKit kit = entity instanceof KitPreClaimEvent ? ((KitPreClaimEvent) entity).getKit() : entity instanceof KitClaimEvent ? ((KitClaimEvent) entity).getKit() : null;
                return kit != null && kit.getIdentifier().startsWith(value);

            case "skill":
                final com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent ev = (com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent) event;
                final String skill = MCMMOAPI.getMCMMOAPI().getSkillName(ev);
                return skill != null && skill.equalsIgnoreCase(value);
            default:
                return true;
        }
    }
    default boolean passedCustomCondition(Event event, HashMap<String, Entity> entities, String entityKey, Entity entity, String condition, String key, String value) {
        String target = condition.startsWith(key) ? condition.split(key)[1] : condition;
        if(target.contains("=")) target = target.split("=")[0];
        final EventCondition con = getEventCondition(target.toUpperCase());
        return con == null || con.check(value) && con.check(event) && con.check(event, value) && con.check(event, entity) && con.check(entity, value) && con.check(entityKey, entities, value);
    }
    default boolean passedRandomPackage(Entity entity, String condition, String value) {
        switch (condition) {
            case "equippedarmorset":
                final ArmorSet armorset = entity instanceof Player ? valueOfArmorSet((Player) entity) : null;
                return armorset != null && armorset.getIdentifier().equals(value);
            case "equippedmask":
                final EntityEquipment ee = entity instanceof Player ? ((Player) entity).getEquipment() : null;
                final Mask mask = ee != null ? valueOfMask(ee.getHelmet()) : null;
                return mask != null && mask.getIdentifier().equals(value);
            case "equippedtitle":
                Title t = RPPlayer.get(entity.getUniqueId()).getActiveTitle();
                return t != null &&  entity instanceof Player && t.getIdentifier().equals(value);
            case "ownstitle":
                t = entity instanceof Player ? getTitle(value) : null;
                return t != null && RPPlayer.get(entity.getUniqueId()).getTitles().contains(t);
            case "hasactivefilter":
                return entity instanceof Player && RPPlayer.get(entity.getUniqueId()).hasActiveFilter() == Boolean.parseBoolean(value);
            case "hasactiveplayerquest":
                final PlayerQuest q = entity instanceof Player ? getPlayerQuest(value) : null;
                final HashMap<PlayerQuest, ActivePlayerQuest> pquests = q != null ? RPPlayer.get(entity.getUniqueId()).getQuests() : null;
                return pquests != null && pquests.containsKey(q) && !pquests.get(q).isExpired();
            case "hasactiveraritygem":
                final String[] values = value.split(":");
                final int l = values.length;
                return entity instanceof Player && RPPlayer.get(entity.getUniqueId()).hasActiveRarityGem(getRarityGem(values[0])) == (l < 2 || Boolean.parseBoolean(values[1]));
            case "hasactivetitle":
                return entity instanceof Player && RPPlayer.get(entity.getUniqueId()).getActiveTitle() != null == Boolean.parseBoolean(value);
            case "hascustomentities":
                return entity instanceof Player && !RPPlayer.get(entity.getUniqueId()).getCustomEnchantEntities().isEmpty() == Boolean.parseBoolean(value);
            case "hasequippedarmorset":
                return entity instanceof Player && valueOfArmorSet((Player) entity) != null == Boolean.parseBoolean(value);
            case "hasequippedmask":
                final EntityEquipment eq = entity instanceof Player ? ((Player) entity).getEquipment() : null;
                return eq != null && valueOfMask(eq.getHelmet()) != null == Boolean.parseBoolean(value);
            case "hasfiltereditem":
                final List<UMaterial> m = entity instanceof Player ? RPPlayer.get(entity.getUniqueId()).getFilteredItems() : null;
                return m != null && m.contains(UMaterial.match(value));
            case "iscustomboss":
                return LivingCustomBoss.living != null && LivingCustomBoss.living.containsKey(entity.getUniqueId()) == Boolean.parseBoolean(value);
            default:
                return true;
        }
    }
}
