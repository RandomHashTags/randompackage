package me.randomhashtags.randompackage.utils.supported;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import me.randomhashtags.randompackage.utils.supported.regional.WorldGuardAPI;

public enum WGFlag {
    PASSTHROUGH,
    BUILD,
    BLOCK_BREAK,
    BLOCK_PLACE,
    USE,
    INTEERACT,
    DAMAGE_ANIMALS,
    PVP,
    SLEEP,
    TNT,
    CHEST_ACCESS,
    PLACE_VEHICLE,
    DESTROY_VEHICLE,
    LIGHTER,
    RIDE,
    POTION_SPLASH,
    ITEM_FRAME_ROTATE(false),
    TRAMPLE_BLOCKS(false),
    ITEM_PICKUP,
    ITEM_DROP,
    EXP_DROPS,
    MOB_DAMAGE,
    CREEPER_EXPLOSION,
    ENDERDRAGON_BLOCK_DAMAGE,
    GHAST_FIREBALL,
    FIREWORK_DAMAGE(false),
    OTHER_EXPLOSION,
    WITHER_DAMAGE(false),
    ENDER_BUILD,
    SNOWMAN_TRAILS(false),
    RAVAGER_RAVAGE(false),
    ENTITY_PAINTING_DESTROY,
    ENTITY_ITEM_FAME_DESTROY,
    MOB_SPAWNING,
    DENY_SPAWN,
    PISTONS,
    FIRE_SPREAD,
    LAVA_FIRE,
    LIGHTNING,
    SNOW_FALL,
    SNOW_MELT,
    ICE_FORM,
    ICE_MELT,
    FROSTED_ICE_MELT(false),
    FROSTED_ICE_FORM(false),
    MUSHROOMS,
    LEAF_DECAY,
    GRASS_SPREAD,
    MYCELIUM_SPREAD,
    VINE_GROWTH,
    CROP_GROWTH(false),
    SOIL_DRY,
    WATER_FLOW,
    LAVA_FLOW,
    WEATHER_LOCK,
    TIME_LOCK,
    SEND_CHAT,
    RECEIVE_CHAT,
    BLOCKED_CMDS,
    ALLOWED_CMDS,
    TELE_LOC,
    SPAWN_LOC,
    INVINCIBILITY,
    FALL_DAMAGE,
    ENTRY,
    EXIT,
    EXIT_OVERRIDE,
    EXIT_VIA_TELEPORT,
    ENDERPEARL,
    CHORUS_TELEPORT,
    GREET_MESSAGE,
    FAREWELL_MESSAGE,
    GREET_TITLE,
    FAREWELL_TITLE,
    NOTIFY_ENTER,
    NOTIFY_LEAVE,
    GAME_MODE,
    HEAL_DELAY,
    HEAL_AMOUNT,
    MIN_HEAL,
    MAX_HEAL,
    FEED_DELAY,
    FEED_AMOUNT,
    MIN_FOOD,
    MAX_FOOD,
    DENY_MESSAGE,
    ENTRY_DENY_MESSAGE,
    EXIT_DENY_MESSAGE
    ;
    private static byte version = WorldGuardAPI.getWorldGuardAPI().version;
    private StateFlag flag;
    private boolean supportsLegacy;
    WGFlag() { supportsLegacy = true; }
    WGFlag(boolean supportsLegacy) { this.supportsLegacy = supportsLegacy; }

    public StateFlag getFlag() {
        if(flag != null) return flag;
        final String n = name();
        final boolean six = version == 6;
        if(six && !supportsLegacy) {
            throw new UnsupportedOperationException("Flag " + n + " doesn't exist using WorldGuard " + version + "!");
        }
        try {
            flag = (StateFlag) ((six ? com.sk89q.worldguard.protection.flags.DefaultFlag.class : Flags.class).getDeclaredField(n).get(null));
        } catch(Exception e) {
            throw new UnsupportedOperationException("Flag " + n + " doesn't exist using WorldGuard " + version + "!");
        }
        return flag;
    }
}
