package me.randomhashtags.randompackage.utils.supported;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class WorldGuardAPI {
    private static WorldGuardAPI instance;
    protected byte version;
    public static WorldGuardAPI getWorldGuardAPI() {
        if(instance == null) {
            instance = new WorldGuardAPI();
            final Plugin p = Bukkit.getPluginManager().getPlugin("WorldGuard");
            final String version = p != null && p.isEnabled() ? p.getDescription().getVersion() : null;
            instance.version = (byte) (version != null ? version.startsWith("6") ? 6 : 7 : -1);
        }
        return instance;
    }

    public boolean allows(Location l, StateFlag...flags) {
        return version == -1 || version == 6 ? allows_wg6(l, flags) : allows_wg7(l, flags);
    }

    private boolean allows_wg6(Location l, StateFlag...flags) {
        final ApplicableRegionSet s = com.sk89q.worldguard.bukkit.WGBukkit.getPlugin().getRegionManager(l.getWorld()).getApplicableRegions(l);
        for(StateFlag flag : flags) {
            if(s.queryState(null, flag) == StateFlag.State.DENY) {
                return false;
            }
        }
        return true;
    }
    private boolean allows_wg7(Location l, StateFlag...flags) {
        final com.sk89q.worldguard.protection.regions.RegionContainer c = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        final com.sk89q.worldguard.protection.regions.RegionQuery q = c.createQuery();
        final ApplicableRegionSet s = q.getApplicableRegions(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(l));
        return s.testState(null, flags);
    }
}
enum WGFlag {
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
    ITEM_FRAME_ROTATE,
    TRAMPLE_BLOCKS,
    ITEM_PICKUP,
    ITEM_DROP,
    EXP_DROPS,
    MOB_DAMAGE,
    CREEPER_EXPLOSION,
    ENDERDRAGON_BLOCK_DAMAGE,
    GHAST_FIREBALL,
    FIREWORK_DAMAGE,
    OTHER_EXPLOSION,
    WITHER_DAMAGE,
    ENDER_BUILD,
    SNOWMAN_TRAILS,
    RAVAGER_RAVAGE,
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
    FROSTED_ICE_MELT,
    FROSTED_ICE_FORM,
    MUSHROOMS,
    LEAF_DECAY,
    GRASS_SPREAD,
    MYCELIUM_SPREAD,
    VINE_GROWTH,
    CROP_GROWTH,
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
    HEAL_DECAY,
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
    private Flag flag;
    private boolean supportsLegacy;
    WGFlag() { supportsLegacy = true; }
    WGFlag(boolean supportsLegacy) { this.supportsLegacy = supportsLegacy; }

    public Flag getFlag() {
        if(flag != null) return flag;
        final String n = name();
        try {
            flag = (Flag) ((version == 6 ? DefaultFlag.class : Flags.class).getDeclaredField(n).get(null));
        } catch(Exception e) {
            System.out.println("[WGFlag] Unsupported Flag! WorldGuard version=" + version + ";Flag=" + n);
            e.printStackTrace();
        }
        return flag;
    }
}
