package me.randomhashtags.randompackage.utils;

import me.randomhashtags.randompackage.addons.*;
import me.randomhashtags.randompackage.addons.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.attributes.Combo;
import me.randomhashtags.randompackage.events.BoosterActivateEvent;
import me.randomhashtags.randompackage.events.PlayerClaimEnvoyCrateEvent;
import me.randomhashtags.randompackage.events.RandomizationScrollUseEvent;
import me.randomhashtags.randompackage.events.ServerCrateOpenEvent;
import me.randomhashtags.randompackage.events.customenchant.CustomEnchantApplyEvent;
import me.randomhashtags.randompackage.events.customenchant.CustomEnchantProcEvent;
import me.randomhashtags.randompackage.events.customenchant.EnchanterPurchaseEvent;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Chunk;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class EventConditions extends RPFeature implements Combo {
    protected static List<UUID> spawnedFromSpawner = new ArrayList<>();
    protected static HashMap<UUID, EntityShootBowEvent> projectileEvents = new HashMap<>();

    private boolean passed = false;
    protected boolean passedAllConditions(Event event, Entity entity, String condition, String s, String value, boolean legacy, boolean eight, boolean nine, boolean ten, boolean eleven, boolean thirteen) {
        passed = true;
        passedCustomCondition(event, entity, condition, s, value);
        if(!passed) return false;
        if(condition.startsWith(s)) {
            passedBasic(entity, condition, s, value);
            if(!passed) return false;
            passedAgeable(entity, condition, s, value);
            if(!passed) return false;
            passedAnimals(entity, condition, s, value, legacy, thirteen);
            if(!passed) return false;
            passedArmorStand(entity, condition, s, value);
            if(!passed) return false;
            passedBat(entity, condition, s, value);
            if(!passed) return false;
            passedCat(entity, condition, s, value, legacy, thirteen);
            if(!passed) return false;
            passedChestedHorse(entity, condition, s, value, eight, nine, ten);
            if(!passed) return false;
            passedCreeper(entity, condition, s, value);
            if(!passed) return false;
            passedEnderCrystal(entity, condition, s, value, eight);
            if(!passed) return false;
            passedEnderDragon(entity, condition, s, value, eight);
            if(!passed) return false;
            passedEnderman(entity, condition, s, value);
            if(!passed) return false;
            passedEndermite(entity, condition, s, value, legacy, thirteen);
            if(!passed) return false;
            passedEntity(entity, condition, s, value, eight, nine, ten);
            if(!passed) return false;
            passedFallenBlock(event, entity, condition, s, value);
            if(!passed) return false;
            passedFirework(entity, condition, s, value, legacy, thirteen);
            if(!passed) return false;
            passedFox(entity, condition, s, value, legacy, thirteen);
            if(!passed) return false;
            passedGuardian(entity, condition, s, value);
            if(!passed) return false;
            passedHorse(entity, condition, s, value, legacy, eight, nine, ten, eleven, thirteen);
            if(!passed) return false;
            passedHusk(entity, condition, s, value, legacy, thirteen);
            if(!passed) return false;
            passedIronGolem(entity, condition, s, value);
            if(!passed) return false;
            passedLightingStrike(entity, condition, s, value);
            if(!passed) return false;
            passedLivingEntity(entity, condition, s, value, legacy, eight, thirteen);
            if(!passed) return false;
            passedMinecart(entity, condition, s, value);
            if(!passed) return false;
            passedMob(entity, condition, s, value, legacy);
            if(!passed) return false;
            passedPainting(entity, condition, s, value);
            if(!passed) return false;
            passedPanda(entity, condition, s, value, legacy, thirteen);
            if(!passed) return false;
            passedPig(entity, condition, s, value);
            if(!passed) return false;
            passedPigZombie(entity, condition, s, value);
            if(!passed) return false;
            passedPlayer(entity, condition, s, value, legacy, eight, nine, ten, eleven);
            if(!passed) return false;
            passedProjectile(entity, condition, s, value);
            if(!passed) return false;
            passedRabbit(entity, condition, s, value);
            if(!passed) return false;
            passedRaider(entity, condition, s, value, legacy, thirteen);
            if(!passed) return false;
            passedRandomPackage(entity, condition, s, value);
            if(!passed) return false;
            passedSheep(entity, condition, s, value);
            if(!passed) return false;
            passedSittable(entity, condition, s, value, eight, nine, ten, eleven);
            if(!passed) return false;
            passedSkeleton(entity, condition, s, value);
            if(!passed) return false;
            passedSlime(entity, condition, s, value, legacy);
            if(!passed) return false;
            passedSnowman(entity, condition, s, value, eight, nine);
            if(!passed) return false;
            passedTameable(entity, condition, s, value);
            if(!passed) return false;
            passedTropicalFish(entity, condition, s, value, legacy);
            if(!passed) return false;
            passedVex(entity, condition, s, value, eight, nine, ten);
            if(!passed) return false;
            passedVillager(entity, condition, s, value, legacy, thirteen);
            if(!passed) return false;
            passedWitherSkull(entity, condition, s, value);
            if(!passed) return false;
            passedZombie(entity, condition, s, value);
            if(!passed) return false;
        }
        passedEvent(event, entity, condition, s, value);
        return passed;
    }
    private void passedBasic(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "isfromspawner=")) {
            passed = spawnedFromSpawner.contains(e.getUniqueId()) == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "isplayer=")) {
            passed = e instanceof Player == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "ismob=")) {
            passed = e instanceof Mob == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "ismonster=")) {
            passed = e instanceof Monster == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "iscreature=")) {
            passed = e instanceof Creature == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "isanimal=")) {
            passed = e instanceof Animals == Boolean.parseBoolean(value);
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
        } else if(condition.startsWith(s + "isriding=")) {
            passed = e.isInsideVehicle() && e.getVehicle().getType().name().equalsIgnoreCase(value);
        } else if(condition.startsWith(s + "iscustomnamevisible=")) {
            passed = e.isCustomNameVisible() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "isonground=")) {
            passed = e.isOnGround() == Boolean.parseBoolean(value);
        }
    }
    private void passedAgeable(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "isadult=")) {
            passed = e instanceof Ageable && ((Ageable) e).isAdult() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "isbaby=")) {
            passed = e instanceof Zombie && ((Zombie) e).isBaby() || e instanceof Ageable && ((Ageable) e).isAdult() != Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "canbreed=")) {
            passed = e instanceof Ageable && ((Ageable) e).canBreed() == Boolean.parseBoolean(value);
        }
    }
    private void passedAnimals(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        if(condition.startsWith(s + "inlovemode=")) {
            passed = !legacy && !thirteen && e instanceof Animals && ((Animals) e).isLoveMode() == Boolean.parseBoolean(value);
        }
    }
    private void passedArmorStand(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "hasbaseplate=")) {
            passed = e instanceof ArmorStand && ((ArmorStand) e).hasBasePlate() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "hasarms=")) {
            passed = e instanceof ArmorStand && ((ArmorStand) e).hasArms() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "ismarker=")) {
            passed = e instanceof ArmorStand && ((ArmorStand) e).isMarker() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "issmall=")) {
            passed = e instanceof ArmorStand && ((ArmorStand) e).isSmall() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "isvisible=")) {
            passed = e instanceof ArmorStand && ((ArmorStand) e).isVisible() == Boolean.parseBoolean(value);
        }
    }
    private void passedBat(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "isawake=")) {
            passed = e instanceof Bat && ((Bat) e).isAwake() == Boolean.parseBoolean(value);
        }
    }
    private void passedCat(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        if(condition.startsWith(s + "cattype=")) {
            passed = isLegacy ? e instanceof Ocelot && ((Ocelot) e).getCatType().name().equalsIgnoreCase(value) : e instanceof Cat && ((Cat) e).getCatType().name().equalsIgnoreCase(value);
        } else if(condition.startsWith(s + "collarcolor=")) {
            if(e instanceof Wolf) {
                passed = ((Wolf) e).getCollarColor().name().equalsIgnoreCase(value);
            } else if(!legacy && !thirteen) {
                passed = e instanceof Cat && ((Cat) e).getCollarColor().name().equalsIgnoreCase(value);
            } else {
                passed = false;
            }
        }
    }
    private void passedChestedHorse(Entity e, String condition, String s, String value, boolean eight, boolean nine, boolean ten) {
        if(condition.startsWith(s + "iscarryingchest=")) {
            if(eight || nine || ten) {
                passed = e instanceof Horse && ((Horse) e).isCarryingChest() == Boolean.parseBoolean(value);
            } else {
                passed = e instanceof ChestedHorse && ((ChestedHorse) e).isCarryingChest() == Boolean.parseBoolean(value);
            }
        }
    }
    private void passedCreeper(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "ispowered=")) {
            passed = e instanceof Creeper && ((Creeper) e).isPowered() == Boolean.parseBoolean(value);
        }
    }
    private void passedEnderCrystal(Entity e, String condition, String s, String value, boolean eight) {
        if(condition.startsWith(s + "isshowingbottom=")) {
            passed = eight ? true : e instanceof EnderCrystal && ((EnderCrystal) e).isShowingBottom() == Boolean.parseBoolean(value);
        }
    }
    private void passedEnderDragon(Entity e, String condition, String s, String value, boolean eight) {
        if(condition.startsWith(s + "phase=")) {
            passed = eight ? true : e instanceof EnderDragon && ((EnderDragon) e).getPhase().name().equalsIgnoreCase(value);
        }
    }
    private void passedEnderman(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "iscarrying=")) {
            passed = e instanceof Enderman && UMaterial.match(((Enderman) e).getCarriedMaterial().getItemType().name()).name().equalsIgnoreCase(value);
        }
    }
    private void passedEndermite(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        if(condition.startsWith(s + "isplayerspawned=")) {
            passed = legacy || thirteen ? false : e instanceof Endermite && ((Endermite) e).isPlayerSpawned() == Boolean.parseBoolean(value);
        }
    }
    private void passedEntity(Entity e, String condition, String s, String value, boolean eight, boolean nine, boolean ten) {
        if(condition.startsWith(s + "inbiome=")) {
            final Chunk chunk = e.getLocation().getChunk();
            passed = e.getWorld().getBiome(chunk.getX(), chunk.getZ()).name().equalsIgnoreCase(value);
        } else if(condition.startsWith(s + "inworld=")) {
            passed = e.getWorld().getName().equals(value);
        } else if(condition.startsWith(s + "isglowing=")) {
            passed = !eight && e.isGlowing() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "isinvulnerable=")) {
            passed = !eight && e.isInvulnerable() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "issilent=")) {
            passed = !eight && !nine && e.isSilent() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "isitem=")) {
            passed = e instanceof Item == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "hasgravity=")) {
            passed = e instanceof ArmorStand && ((ArmorStand) e).hasGravity() || !eight && !nine && !ten && e.hasGravity() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "worlddifficulty=")) {
            passed = e.getWorld().getDifficulty().name().equalsIgnoreCase(value);
        }
    }
    private void passedEvoker(Entity e, String condition, String s, String value, boolean eight, boolean nine, boolean ten) {
        if(condition.startsWith(s + "currentspell=")) {
            passed = eight || nine | ten ? false : e instanceof Evoker && ((Evoker) e).getCurrentSpell().name().equalsIgnoreCase(value);
        }
    }
    private void passedExplosive(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "isincendiary=")) {
            passed = e instanceof Explosive && ((Explosive) e).isIncendiary() == Boolean.parseBoolean(value);
        }
    }
    private void passedFallenBlock(Event event, Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "material=")) {
            passed = e instanceof FallingBlock && UMaterial.match(((FallingBlock) e).getMaterial().name()).name().equalsIgnoreCase(value)
                    || event instanceof BlockPlaceEvent && UMaterial.match(((BlockPlaceEvent) event).getBlock().getType().name()).name().equalsIgnoreCase(value)
                    || event instanceof BlockBreakEvent && UMaterial.match(((BlockBreakEvent) event).getBlock().getType().name()).name().equalsIgnoreCase(value);
        } else if(condition.startsWith(s + "canhurtentities=")) {
            passed = e instanceof FallingBlock && ((FallingBlock) e).canHurtEntities() == Boolean.parseBoolean(value);
        }
    }
    private void passedFirework(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        if(condition.startsWith(s + "isshotatangle=")) {
            passed = legacy || thirteen ? false : e instanceof Firework && ((Firework) e).isShotAtAngle() == Boolean.parseBoolean(value);
        }
    }
    private void passedFox(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        if(condition.startsWith(s + "foxtype=")) {
            passed = legacy || thirteen ? false : e instanceof Fox && ((Fox) e).getFoxType().name().equalsIgnoreCase(value);
        } else if(condition.startsWith(s + "iscrouching=")) {
            passed = legacy || thirteen ? false : e instanceof Fox && ((Fox) e).isCrouching() == Boolean.parseBoolean(value);
        }
    }
    private void passedGuardian(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "iselder=")) {
            passed = e instanceof Guardian && ((Guardian) e).isElder() == Boolean.parseBoolean(value);
        }
    }
    private void passedHorse(Entity e, String condition, String s, String value, boolean legacy, boolean eight, boolean nine, boolean ten, boolean eleven, boolean thirteen) {
        if(condition.startsWith(s + "isvariant=")) {
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
        }
    }
    private void passedHusk(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        if(condition.startsWith(s + "isconverting=")) {
            if(legacy || thirteen) {
                passed = false;
            } else {
                final boolean b = Boolean.parseBoolean(value);
                passed = b && (e instanceof Husk && ((Husk) e).isConverting() || e instanceof PigZombie && ((PigZombie) e).isConverting() || e instanceof Zombie && ((Zombie) e).isConverting());
            }
        }
    }
    private void passedIronGolem(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "isplayercreated=")) {
            passed = e instanceof IronGolem && ((IronGolem) e).isPlayerCreated() == Boolean.parseBoolean(value);
        }
    }
    private void passedLightingStrike(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "iseffect=")) {
            passed = e instanceof LightningStrike && ((LightningStrike) e).isEffect() == Boolean.parseBoolean(value);
        }
    }
    private void passedLivingEntity(Entity e, String condition, String s, String value, boolean legacy, boolean eight, boolean thirteen) {
        if(condition.startsWith(s + "isleashed=")) {
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
        }
    }
    private void passedMinecart(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "isslowwhenempty=")) {
            passed = e instanceof Minecart && ((Minecart) e).isSlowWhenEmpty() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "displayedblock=")) {
            passed = e instanceof Minecart && UMaterial.match(((Minecart) e).getDisplayBlock().getItemType().name()).name().equalsIgnoreCase(value);
        }
    }
    private void passedMob(Entity e, String condition, String s, String value, boolean legacy) {
        if(condition.startsWith(s + "hastarget=")) {
            if(!legacy) {
                passed = e instanceof Mob && ((Mob) e).getTarget() != null == Boolean.parseBoolean(value);
            } else {
                passed = false;
            }
        }
    }
    private void passedPainting(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "art=")) {
            passed = e instanceof Painting && ((Painting) e).getArt().name().equalsIgnoreCase(value);
        }
    }
    private void passedPanda(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        if(condition.startsWith(s + "maingene=")) {
            passed = legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getMainGene().name().equalsIgnoreCase(value);
        } else if(condition.startsWith(s + "maingeneisrecessive=")) {
            passed = legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getMainGene().isRecessive() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "hiddengene=")) {
            passed = legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getHiddenGene().name().equalsIgnoreCase(value);
        } else if(condition.startsWith(s + "hiddengeneisrecessive=")) {
            passed = legacy || thirteen ? false : e instanceof Panda && ((Panda) e).getHiddenGene().isRecessive() == Boolean.parseBoolean(value);
        }
    }
    private void passedPig(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "hassaddle=")) {
            passed = e instanceof Pig && ((Pig) e).hasSaddle() == Boolean.parseBoolean(value);
        }
    }
    private void passedPigZombie(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "isangry=")) {
            if(e instanceof PigZombie) {
                passed = ((PigZombie) e).isAngry() == Boolean.parseBoolean(value);
            } else if(e instanceof Wolf) {
                passed = ((Wolf) e).isAngry() == Boolean.parseBoolean(value);
            } else {
                passed = false;
            }
        }
    }
    private void passedPlayer(Entity e, String condition, String s, String value, boolean legacy, boolean eight, boolean nine, boolean ten, boolean eleven) {
        if(condition.startsWith(s + "issneaking=")) {
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
        } else if(condition.startsWith(s + "walkspeed=")) {
            passed = e instanceof Player && ((Player) e).getWalkSpeed() == Float.parseFloat(value);
        } else if(condition.startsWith(s + "walkspeed<=")) {
            passed = e instanceof Player && ((Player) e).getWalkSpeed() <= Float.parseFloat(value);
        } else if(condition.startsWith(s + "walkspeed>=")) {
            passed = e instanceof Player && ((Player) e).getWalkSpeed() >= Float.parseFloat(value);
        } else if(condition.startsWith(s + "flyspeed=")) {
            passed = e instanceof Player && ((Player) e).getFlySpeed() == Float.parseFloat(value);
        } else if(condition.startsWith(s + "flyspeed<=")) {
            passed = e instanceof Player && ((Player) e).getFlySpeed() <= Float.parseFloat(value);
        } else if(condition.startsWith(s + "flyspeed>=")) {
            passed = e instanceof Player && ((Player) e).getFlySpeed() >= Float.parseFloat(value);
        }
    }
    private void passedProjectile(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "doesbounce=")) {
            passed = e instanceof Projectile && ((Projectile) e).doesBounce() == Boolean.parseBoolean(value);
        }
    }
    private void passedRabbit(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "rabbittype=")) {
            passed = e instanceof Rabbit && ((Rabbit) e).getRabbitType().name().equalsIgnoreCase(value);
        }
    }
    private void passedRaider(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        if(condition.startsWith(s + "ispatrolleader=")) {
            passed = legacy || thirteen ? false : e instanceof Raider && ((Raider) e).isPatrolLeader() == Boolean.parseBoolean(value);
        } else if(condition.startsWith(s + "patroltargetblock=")) {
            passed = legacy ? false : e instanceof Raider && UMaterial.match(((Raider) e).getPatrolTarget().getType().name()).name().toLowerCase().endsWith(value);
        }
    }
    private void passedSheep(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "issheared=")) {
            passed = e instanceof Sheep && ((Sheep) e).isSheared() == Boolean.parseBoolean(value);
        }
    }
    private void passedSittable(Entity e, String condition, String s, String value, boolean eight, boolean nine, boolean ten, boolean eleven) {
        if(condition.startsWith(s + "issitting=")) {
            if(eight || nine || ten || eleven) {
                passed = Boolean.parseBoolean(value) == e instanceof Wolf && ((Wolf) e).isSitting()/* || e instanceof Ocelot && ((Ocelot) e).isSitting()*/;
            } else {
                passed = Boolean.parseBoolean(value) == e instanceof Sittable && ((Sittable) e).isSitting();
            }
        }
    }
    private void passedSkeleton(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "skeletontype=")) {
            passed = e instanceof Skeleton && ((Skeleton) e).getSkeletonType().name().equalsIgnoreCase(value);
        }
    }
    private void passedSlime(Entity e, String condition, String s, String value, boolean legacy) {
        if(condition.startsWith(s + "size=")) {
            final int v = Integer.parseInt(value);
            passed = e instanceof Slime && ((Slime) e).getSize() == v || !legacy && e instanceof Phantom && ((Phantom) e).getSize() == v;
        } else if(condition.startsWith(s + "size<=")) {
            final int v = Integer.parseInt(value);
            passed = e instanceof Slime && ((Slime) e).getSize() <= v || !legacy && e instanceof Phantom && ((Phantom) e).getSize() <= v;
        } else if(condition.startsWith(s + "size>=")) {
            final int v = Integer.parseInt(value);
            passed = e instanceof Slime && ((Slime) e).getSize() >= v || !legacy && e instanceof Phantom && ((Phantom) e).getSize() >= v;
        }
    }
    private void passedSnowman(Entity e, String condition, String s, String value, boolean eight, boolean nine) {
        if(condition.startsWith(s + "isderp=")) {
            passed = eight || nine ? false : e instanceof Snowman && ((Snowman) e).isDerp() == Boolean.parseBoolean(value);
        }
    }
    private void passedTameable(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "istamed=")) {
            passed = e instanceof Tameable && ((Tameable) e).isTamed() == Boolean.parseBoolean(value);
        }
    }
    private void passedTropicalFish(Entity e, String condition, String s, String value, boolean legacy) {
        if(condition.startsWith(s + "patterncolor=")) {
            passed = legacy ? false : e instanceof TropicalFish && ((TropicalFish) e).getPatternColor().name().equalsIgnoreCase(value);
        } else if(condition.startsWith(s + "bodycolor=")) {
            passed = legacy ? false : e instanceof TropicalFish && ((TropicalFish) e).getBodyColor().name().equalsIgnoreCase(value);
        } else if(condition.startsWith(s + "pattern=")) {
            passed = legacy ? false : e instanceof TropicalFish && ((TropicalFish) e).getPattern().name().equalsIgnoreCase(value);
        }
    }
    private void passedVex(Entity e, String condition, String s, String value, boolean eight, boolean nine, boolean ten) {
        if(condition.startsWith(s + "ischarging=")) {
            passed = eight || nine || ten ? false : e instanceof Vex && ((Vex) e).isCharging() == Boolean.valueOf(value);
        }
    }
    private void passedVillager(Entity e, String condition, String s, String value, boolean legacy, boolean thirteen) {
        if(condition.startsWith(s + "profession=")) {
            passed = e instanceof Zombie && ((Zombie) e).isVillager() ? ((Zombie) e).getVillagerProfession().name().equalsIgnoreCase(value) : e instanceof Villager && ((Villager) e).getProfession().name().equalsIgnoreCase(value);
        } else if(condition.startsWith(s + "villagertype=")) {
            passed = e instanceof Villager && !(legacy || thirteen) && ((Villager) e).getVillagerType().name().equalsIgnoreCase(value);
        }
    }
    private void passedWitherSkull(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "ischarged=")) {
            passed = e instanceof WitherSkull && ((WitherSkull) e).isCharged() == Boolean.parseBoolean(value);
        }
    }
    private void passedZombie(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "isvillager=")) {
            passed = e instanceof Zombie && ((Zombie) e).isVillager() == Boolean.parseBoolean(value);
        }
    }



    private void passedEvent(Event event, Entity e, String condition, String s, String value) {
        if(condition.startsWith("cause=")) {
            final EntityDamageEvent d = event instanceof EntityDamageEvent ? (EntityDamageEvent) event : null;
            final boolean is = d != null;
            passed = is;
            if(is) {
                final String cause = d.getCause().name();
                final String[] v = value.split("\\|\\|");
                final List<Boolean> did = new ArrayList<>();
                for(String ss : v) {
                    did.add(cause.equalsIgnoreCase(ss));
                }
                passed = did.contains(true);
            }
        } else if(condition.startsWith("tier=")) {
            passed = event instanceof PlayerClaimEnvoyCrateEvent && ((PlayerClaimEnvoyCrateEvent) event).type.getType().getIdentifier().equals(value);
        } else if(condition.startsWith("israritybook=")) {
            passed = event instanceof EnchanterPurchaseEvent && valueOfEnchantRarity(valueOfCustomEnchant(((EnchanterPurchaseEvent) event).purchased)) != null;
        } else if(condition.startsWith("result=")) {
            passed = event instanceof CustomEnchantApplyEvent && ((CustomEnchantApplyEvent) event).result.equalsIgnoreCase(value);
        } else if(condition.startsWith("rarity=")) {
            final EnchanterPurchaseEvent epe = event instanceof EnchanterPurchaseEvent ? (EnchanterPurchaseEvent) event : null;
            final CustomEnchant enchant = epe != null ? valueOfCustomEnchant(epe.purchased) : null;
            final EnchantRarity rarity = enchant != null ? valueOfEnchantRarity(enchant) : null;
            passed = event instanceof CustomEnchantApplyEvent && valueOfEnchantRarity(((CustomEnchantApplyEvent) event).enchant).getIdentifier().equals(value)
                    || event instanceof RandomizationScrollUseEvent && ((RandomizationScrollUseEvent) event).scroll.getIdentifier().equals(value)
                    || event instanceof ServerCrateOpenEvent && ((ServerCrateOpenEvent) event).crate.getIdentifier().equals(value)
                    || rarity != null && rarity.getIdentifier().equals(value);
        } else if(condition.startsWith("success<=")) {
            passed = event instanceof CustomEnchantApplyEvent && ((CustomEnchantApplyEvent) event).success <= evaluate(value);
        } else if(condition.startsWith("destroy<=")) {
            passed = event instanceof CustomEnchantApplyEvent && ((CustomEnchantApplyEvent) event).destroy <= evaluate(value);
        } else if(condition.startsWith("didproc=")) {
            passed = event instanceof CustomEnchantProcEvent && ((CustomEnchantProcEvent) event).didProc == Boolean.parseBoolean(value);
        } else if(condition.startsWith("booster=")) {
            passed = event instanceof BoosterActivateEvent && ((BoosterActivateEvent) event).booster.getIdentifier().equals(value);
        }
    }
    private void passedCustomCondition(Event event, Entity e, String condition, String s, String value) {
        final String target = condition.split("=")[0].split(s)[1];
        final EventCondition con = getEventCondition(target);
        if(con != null) {
            passed = con.check(event) && con.check(event, value) && con.check(event, e) && con.check(e, value);
        }
    }
    private void passedRandomPackage(Entity e, String condition, String s, String value) {
        if(condition.startsWith(s + "equippedarmorset=")) {
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
    }
}
