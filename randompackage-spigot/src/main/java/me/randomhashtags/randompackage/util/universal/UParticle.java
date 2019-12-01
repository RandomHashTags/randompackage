package me.randomhashtags.randompackage.util.universal;

import com.sun.istack.internal.NotNull;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;

/*
    UParticle Version: 1

    This software is created and owned by RandomHashTags, and is licensed under the GNU Affero General Public License v3.0 (https://choosealicense.com/licenses/agpl-3.0/)
    You can only find this software at https://gitlab.com/RandomHashTags/uparticle
    You can find RandomHashTags (me) at
        Discord - RandomHashTags#1948
        Email - imrandomhashtags@gmail.com
        GitHub - https://github.com/RandomHashTags
        GitLab - https://gitlab.com/RandomHashTags
        MCMarket - https://www.mc-market.org/members/20858/
        SpigotMC - https://www.spigotmc.org/members/76364/
        Twitch - https://www.twitch.tv/randomhashtags
        Twitter - https://twitter.com/irandomhashtags
 */
public enum UParticle {
    /*
        <particle>(1.8.8, 1.9.4, 1.10.2, 1.11.2, 1.12.2, 1.13.2, 1.14.4)
    */
    BARRIER(null, "BARRIER"),
    BLOCK_CRACK("TILE_BREAK", "BLOCK_CRACK"),
    BLOCK_DUST("TILE_DUST", "BLOCK_DUST"),
    BUBBLE_COLUMN_UP(null, null, null, null, null, "BUBBLE_COLUMN_UP"),
    BUBBLE_POP(null, null, null, null, null, "BUBBLE_POP"),
    CLOUD("CLOUD"),
    CRIT("CRIT"),
    CRIT_MAGIC("MAGIC_CRIT", "CRIT_MAGIC"),
    CURRENT_DOWN(null, null, null, null, null, "CURRENT_DOWN"),
    DAMAGE_INDICATOR(null, "DAMAGE_INDICATOR"),
    DRAGON_BREATH(null, "DRAGON_BREATH"),
    DOLPHIN(null, null, null, null, null, "DOLPHIN"),
    DRIP_LAVA("LAVADRIP", "DRIP_LAVA"),
    DRIP_WATER("WATERDRIP", "DRIP_WATER"),
    ENCHANTMENT_TABLE("FLYING_GLYPH", "ENCHANTMENT_TABLE"),
    END_ROD(null, "END_ROD"),
    EXPLOSION_NORMAL("EXPLOSION", "EXPLOSION_NORMAL"),
    EXPLOSION_HUGE("EXPLOSION_HUGE"),
    EXPLOSION_LARGE("EXPLOSION_LARGE"),
    FALLING_DUST(null, null, "FALLING_DUST"),
    FIREWORKS_SPARK("FIREWORKS_SPARK"),
    FLAME("FLAME"),
    FOOTSTEP("FOOTSTEP", null, null, null, null, "STEP_SOUND"),
    HEART("HEART"),
    ITEM_CRACK("ITEM_BREAK", "ITEM_CRACK"),
    ITEM_TAKE(null, "ITEM_TAKE"),
    LAVA("LAVA_POP", "LAVA"),
    MOB_APPEARANCE(null, "MOB_APPEARANCE"),
    NAUTILUS(null, null, null, null, null, "NAUTILUS"),
    NOTE("NOTE"),
    PORTAL("PORTAL"),
    REDSTONE("COLOURED_DUST", "REDSTONE"),
    SMOKE_NORMAL("SMOKE", "SMOKE_NORMAL"),
    SMOKE_LARGE("LARGE_SMOKE", "SMOKE_LARGE"),
    SNOWBALL("SNOWBALL_BREAK", "SNOWBALL"),
    SNOW_SHOVEL("SNOW_SHOVEL"),
    SLIME("SLIME"),
    SPELL("SPELL", null),
    SPELL_INSTANT("INSTANT_SPELL", "SPELL_INSTANT"),
    SPELL_MOB("POTION_SWIRL", "SPELL_MOB"),
    SPELL_MOB_AMBIENT("POTION_SWIRL_TRANSPARENT", "SPELL_MOB_AMBIENT"),
    SPELL_WITCH("WITCH_MAGIC", "SPELL_WITCH"),
    SPIT(null, null, null, "SPIT"),
    SQUID_INK(null, null, null, null, null, "SQUID_INK"),
    SUSPENDED(null, "SUSPENDED"),
    SUSPENDED_DEPTH("SMALL_SMOKE", "SUSPENDED_DEPTH"),
    SWEEP_ATTACK(null, "SWEEP_ATTACK"),
    TOWN_AURA("SMALL_SMOKE", "TOWN_AURA"),
    TOTEM(null, null, null, "TOTEM"),
    VILLAGER_ANGRY("VILLAGER_THUNDERCLOUD", "VILLAGER_ANGRY"),
    VILLAGER_HAPPY("HAPPY_VILLAGER", "VILLAGER_HAPPY"),
    WATER_BUBBLE(null, "WATER_BUBBLE"),
    WATER_DROP(null, "WATER_DROP"),
    WATER_SPLASH(null, "WATER_SPLASH"),
    WATER_WAKE(null, "WATER_WAKE"),
    ;
    private static final HashMap<String, UParticle> CACHE = new HashMap<>();
    private final String v = Bukkit.getVersion();
    private final String[] names;
    private final String versionName;
    private Object particle;
    UParticle(String...names) {
        this.names = names;
        this.versionName = getName();
        if(versionName != null) {
            if(v.contains("1.8")) {
                this.particle = Effect.valueOf(versionName);
            } else {
                try {
                    this.particle = Particle.valueOf(versionName);
                } catch (Exception e) {
                    try {
                        this.particle = Effect.valueOf(versionName);
                    } catch (Exception ee) {
                        if(!versionName.equals("ITEM_TAKE") || versionName.equals("ITEM_TAKE") && !v.contains("1.13"))
                            Bukkit.broadcastMessage("[UParticle] Particle/Effect \"" + versionName + "\" doesn't exist in " + v + "!");
                    }
                }
            }
        }
    }
    public String getVersionName() { return versionName; }
    public String[] getNames() { return names; }
    public Object getParticle() { return particle; }

    public void play(Player player, Location l, int count) {
        if(v.contains("1.8")) {
            for(int i = 1; i <= count; i++) player.playEffect(l, (Effect) particle, 0);
        } else {
            player.spawnParticle((Particle) particle, l, count);
        }
    }
    public void play(Player player, Location l, int count, Material material) {
        if(v.contains("1.8")) {
            for(int i = 1; i <= count; i++) player.playEffect(l, (Effect) particle, material);
        } else {
            player.spawnParticle((Particle) particle, l, count, material);
        }
    }
    public void play(Location l, int count) {
        final World w = l.getWorld();
        if(v.contains("1.8")) {
            for(int i = 1; i <= count; i++) w.playEffect(l, (Effect) particle, 0);
        } else {
            w.spawnParticle((Particle) particle, l, count);
        }
    }
    public void play(Location l, int count, Material material) {
        final World w = l.getWorld();
        if(v.contains("1.8")) {
            w.playEffect(l, (Effect) particle, count);
        } else {
            w.spawnParticle((Particle) particle, l, count, material);
        }
    }

    private String getName() {
        final int ver = v.contains("1.8") ? 0 : v.contains("1.9") ? 1 : v.contains("1.10") ? 2 : v.contains("1.11") ? 3 : v.contains("1.12") ? 4 : v.contains("1.13") ? 5 : v.contains("1.14") ? 6 : names.length-1;
        int realver = names.length <= ver ? names.length-1 : ver;
        if(names[realver] == null) {
            boolean did = false;
            for(int i = realver; i >= 0; i--) {
                if(!did && names[i] != null) {
                    realver = i;
                    did = true;
                }
            }
        }
        final String t = names[realver], t2 = names.length > ver ? names[ver] : names[names.length-1];
        return t != null ? t : t2;
    }
    public static UParticle matchParticle(@NotNull String name) {
        name = name.toUpperCase();
        if(CACHE.containsKey(name)) return CACHE.get(name);
        for(UParticle particle : UParticle.values()) {
            for(String target : particle.names) {
                if(name.equals(target)) {
                    CACHE.put(target, particle);
                    return particle;
                }
            }
        }
        return null;
    }
}
