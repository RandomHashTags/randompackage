package me.randomhashtags.randompackage.supported.regional;

import me.randomhashtags.randompackage.supported.WGFlag;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public enum WorldGuardAPI implements UVersionableSpigot {
    INSTANCE;

    public final byte version;

    WorldGuardAPI() {
        final Plugin p = PLUGIN_MANAGER.getPlugin("WorldGuard");
        final String version = p != null && p.isEnabled() ? p.getDescription().getVersion() : null;
        this.version = (byte) (version != null ? version.startsWith("6") ? 6 : 7 : -1);
    }

    public boolean allowsPvP(@NotNull Player player, @NotNull Location l) {
        return allows(player, l, WGFlag.PVP);
    }
    public boolean allowsBlockBreak(@NotNull Player player, @NotNull Location l) {
        return allows(player, l, WGFlag.BLOCK_BREAK);
    }
    public boolean allowsBlockPlace(@NotNull Player player, @NotNull Location l) {
        return allows(player, l, WGFlag.BLOCK_PLACE);
    }
    public boolean allows(@NotNull Player player, @NotNull Location l, @NotNull WGFlag...flags) {
        if(version == 1 || hasBypass(player, l)) {
            return true;
        } else if(version == 6) {
            return allows_wg6(player, l, flags);
        } else {
            return allows_wg7(player, l, flags);
        }
    }

    public boolean hasBypass(@NotNull Player player, @NotNull Location l) {
        final World w = l.getWorld();
        if(version == 6) {
            return com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().getSessionManager().hasBypass(player, w);
        } else {
            final com.sk89q.worldedit.world.World wew = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(w);
            final com.sk89q.worldguard.session.SessionManager m = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getSessionManager();
            try {
                final Method method = m.getClass().getDeclaredMethod("hasBypass", com.sk89q.worldguard.LocalPlayer.class, com.sk89q.worldedit.world.World.class);
                return (Boolean) method.invoke(m, getLocalPlayer(player), wew);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
    private com.sk89q.worldguard.LocalPlayer getLocalPlayer(Player player) {
        return player != null ? com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(player) : null;
    }

    private boolean allows_wg6(Player player, Location l, WGFlag...flags) {
        final com.sk89q.worldguard.LocalPlayer p = getLocalPlayer(player);
        final com.sk89q.worldguard.protection.ApplicableRegionSet s = com.sk89q.worldguard.bukkit.WGBukkit.getPlugin().getRegionManager(l.getWorld()).getApplicableRegions(l);
        final com.sk89q.worldguard.protection.flags.StateFlag.State deny = com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        for(WGFlag flag : flags) {
            final com.sk89q.worldguard.protection.flags.StateFlag state = flag.getFlag();
            if(s.queryState(p, state) == deny) {
                return false;
            }
        }
        return true;
    }
    private boolean allows_wg7(Player player, Location l, WGFlag...flags) {
        final com.sk89q.worldguard.protection.regions.RegionContainer c = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        final com.sk89q.worldguard.protection.regions.RegionQuery q = c.createQuery();
        final com.sk89q.worldguard.protection.ApplicableRegionSet s = q.getApplicableRegions(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(l));
        final List<com.sk89q.worldguard.protection.flags.StateFlag> states = new ArrayList<>();
        for(WGFlag f : flags) {
            states.add(f.getFlag());
        }
        final com.sk89q.worldguard.protection.flags.StateFlag[] stateFlags = states.toArray(new com.sk89q.worldguard.protection.flags.StateFlag[states.size()]);
        return s.testState(getLocalPlayer(player), stateFlags);
    }

    // unfinished
    public List<Chunk> getChunks(String regionName) {
        final List<Chunk> chunks = new ArrayList<>();
        final List<World> worlds = Bukkit.getWorlds();
        if(version == 6) {
            final com.sk89q.worldguard.bukkit.WorldGuardPlugin wg = com.sk89q.worldguard.bukkit.WGBukkit.getPlugin();
            for(World w : worlds) {
                final com.sk89q.worldguard.protection.regions.ProtectedRegion region = wg.getRegionManager(w).getRegion(regionName);
                chunks.addAll(getchunks(region, w));
            }
        } else  {
            final com.sk89q.worldguard.protection.regions.RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
            for(World w : worlds) {
                final com.sk89q.worldguard.protection.regions.ProtectedRegion region = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(w)).getRegion(regionName);
                chunks.addAll(getchunks(region, w));
            }
        }
        return chunks;
    }
    private List<Chunk> getchunks(com.sk89q.worldguard.protection.regions.ProtectedRegion region, World w) {
        final List<Chunk> c = new ArrayList<>();
        if(region != null && w != null) {
            final List<com.sk89q.worldedit.BlockVector2D> points = region.getPoints();
            if(points != null) {
                for(com.sk89q.worldedit.BlockVector2D p : points) {
                    final Chunk ch = w.getChunkAt(new Location(w, p.getBlockX(), 0, p.getBlockZ()));
                    if(!c.contains(ch)) {
                        c.add(ch);
                    }
                }
            }
        }
        return c;
    }
}

