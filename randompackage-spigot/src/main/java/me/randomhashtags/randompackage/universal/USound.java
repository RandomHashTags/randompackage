package me.randomhashtags.randompackage.universal;

import me.randomhashtags.randompackage.util.Versionable;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/*
    USound Version: 1

    This software is created and owned by RandomHashTags, and is licensed under the GNU Affero General Public License v3.0 (https://choosealicense.com/licenses/agpl-3.0/)
    You can only find this software at https://gitlab.com/RandomHashTags/usound
    You can find RandomHashTags on
        Discord - RandomHashTags#1948
        Discord Server - https://discord.gg/CPTsc5X
        Dlive - https://dlive.tv/RandomHashTags
        Email - imrandomhashtags@gmail.com
        GitHub - https://github.com/RandomHashTags
        GitLab - https://gitlab.com/RandomHashTags
        MCMarket - https://www.mc-market.org/members/20858/
        Minecraft - RandomHashTags
        Mixer - https://mixer.com/randomhashtags
        PayPal - imrandomhashtags@gmail.com
        Reddit - https://www.reddit.com/user/randomhashtags/
        SpigotMC - https://www.spigotmc.org/members/76364/
        Spotify - https://open.spotify.com/user/randomhashtags
        Stackoverflow - https://stackoverflow.com/users/12508938/
        Subnautica Mods - https://www.nexusmods.com/users/77115308
        Twitch - https://www.twitch.tv/randomhashtags/
        Twitter - https://twitter.com/irandomhashtags
        YouTube - https://www.youtube.com/channel/UC3L6Egnt0xuMoz8Ss5k51jw
 */
public enum USound implements Versionable {
    /*
        <sound>(1.8.8, 1.9.4, 1.10.2, 1.11.2, 1.12.2, 1.13.2, 1.14.4, 1.15.0)
    */
    AMBIENT_CAVE("AMBIENCE_CAVE", "AMBIENT_CAVE"),
    AMBIENT_UNDERWATER_ENTER("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_ENTER"),
    AMBIENT_UNDERWATER_EXIT("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_EXIT"),
    AMBIENT_UNDERWATER_LOOP("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_LOOP"),
    AMBIENT_UNDERWATER_LOOP_ADDITIONS("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_LOOP_ADDITIONS"),
    AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE"),
    AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE("AMBIENCE_CAVE", "AMBIENT_CAVE", null, null, null, "AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE"),
    BLOCK_ANVIL_BREAK("ANVIL_BREAK", "BLOCK_ANVIL_BREAK"),
    BLOCK_ANVIL_DESTROY(null, "BLOCK_ANVIL_DESTROY"),
    BLOCK_ANVIL_FALL("ANVIL_LAND", "BLOCK_ANVIL_FALL"),
    BLOCK_ANVIL_HIT(null, "BLOCK_ANVIL_HIT"),
    BLOCK_ANVIL_PLACE("ANVIL_LAND", "BLOCK_ANVIL_PLACE"),
    BLOCK_ANVIL_STEP("ANVIL_LAND", "BLOCK_ANVIL_STEP"),
    BLOCK_ANVIL_USE("ANVIL_USE", "BLOCK_ANVIL_USE"),
    BLOCK_BEACON_ACTIVATE(null, null, null, null, null, "BLOCK_BEACON_ACTIVATE"),
    BLOCK_BEACON_AMBIENT(null, null, null, null, null, "BLOCK_BEACON_AMBIENT"),
    BLOCK_BEACON_DEACTIVATE(null, null, null, null, null, "BLOCK_BEACON_DEACTIVATE"),
    BLOCK_BEACON_POWER_SELECT(null, null, null, null, null, "BLOCK_BEACON_POWER_SELECT"),
    BLOCK_BREWING_STAND_BREW(null, "BLOCK_BREWING_STAND_BREW"),
    BLOCK_BUBBLE_COLUMN_BUBBLE_POP(null, null, null, null, null, "BLOCK_BUBBLE_COLUMN_BUBBLE_POP"),
    BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT(null, null, null, null, null, "BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT"),
    BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE(null, null, null, null, null, "BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE"),
    BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT(null, null, null, null, null, "BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT"),
    BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE(null, null, null, null, null, "BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE"),
    BLOCK_CHEST_CLOSE("CHEST_CLOSE", "BLOCK_CHEST_CLOSE"),
    BLOCK_CHEST_LOCKED(null, "BLOCK_CHEST_LOCKED"),
    BLOCK_CHEST_OPEN("CHEST_OPEN", "BLOCK_CHEST_OPEN"),
    BLOCK_CHORUS_FLOWER_DEATH(null, "BLOCK_CHORUS_FLOWER_DEATH"),
    BLOCK_CHORUS_FLOWER_GROW(null, "BLOCK_CHORUS_FLOWER_GROW"),
    BLOCK_COMPARATOR_CLICK(null, "BLOCK_COMPARATOR_CLICK"),
    BLOCK_CONDUIT_ACTIVATE(null, null, null, null, null, "BLOCK_CONDUIT_ACTIVATE"),
    BLOCK_CONDUIT_AMBIENT(null, null, null, null, null, "BLOCK_CONDUIT_AMBIENT"),
    BLOCK_CONDUIT_AMBIENT_SHORT(null, null, null, null, null, "BLOCK_CONDUIT_AMBIENT_SHORT"),
    BLOCK_CONDUIT_ATTACK_TARGET(null, null, null, null, null, "BLOCK_CONDUIT_ATTACK_TARGET"),
    BLOCK_CONDUIT_DEACTIVATE(null, null, null, null, null, "BLOCK_CONDUIT_DEACTIVATE"),
    BLOCK_CORAL_BLOCK_BREAK(null, null, null, null, null, "BLOCK_CORAL_BLOCK_BREAK"),
    BLOCK_CORAL_BLOCK_FALL(null, null, null, null, null, "BLOCK_CORAL_BLOCK_FALL"),
    BLOCK_CORAL_BLOCK_HIT(null, null, null, null, null, "BLOCK_CORAL_BLOCK_HIT"),
    BLOCK_CORAL_BLOCK_PLACE(null, null, null, null, null, "BLOCK_CORAL_BLOCK_PLACE"),
    BLOCK_CORAL_BLOCK_STEP(null, null, null, null, null, "BLOCK_CORAL_BLOCK_PLACE"),
    BLOCK_DISPENSER_DISPENSE(null, "BLOCK_DISPENSER_DISPENSE"),
    BLOCK_DISPENSER_FAIL(null, "BLOCK_DISPENSER_FAIL"),
    BLOCK_DISPENSER_LAUNCH(null, "BLOCK_DISPENSER_LAUNCH"),
    //
    ENTITY_PLAYER_LEVELUP("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"),
    //
    ENTITY_VILLAGER_AMBIENT("VILLAGER_IDLE", "ENTITY_VILLAGER_AMBIENT"),
    ENTITY_VILLAGER_DEATH("VILLAGER_DEATH", "ENTITY_VILLAGER_DEATH"),
    ENTITY_VILLAGER_HURT("VILLAGER_HIT", "ENTITY_VILLAGER_HURT"),
    ENTITY_VILLAGER_NO("VILLAGER_NO", "ENTITY_VILLAGER_NO"),
    ENTITY_VILLAGER_TRADE("VILLAGER_HAGGLE", "ENTITY_VILLAGER_TRADING", null, null, null, "ENTITY_VILLAGER_TRADE"),
    ENTITY_VILLAGER_YES("VILLAGER_YES", "ENTITY_VILLAGER_YES"),
    //
    WEATHER_RAIN("AMBIENCE_RAIN", "WEATHER_RAIN"),
    WEATHER_RAIN_ABOVE("AMBIENCE_RAIN", "WEATHER_RAIN_ABOVE"),
    ;
    private static final HashMap<String, USound> CACHE = new HashMap<>();
    private static final HashMap<String, Sound> SOUND_CACHE = new HashMap<>();
    private final String[] names;
    private final Sound sound;
    USound(String...names) {
        this.names = names;
        this.sound = getVersionSound();
    }
    public Sound getSound() {
        return sound;
    }
    public void playSound(Player player, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
    public void playSound(Location l, float volume, float pitch) {
        l.getWorld().playSound(l, sound, volume, pitch);
    }

    private Sound getVersionSound() {
        final int ver = EIGHT ? 0 : NINE ? 1 : TEN ? 2 : ELEVEN ? 3 : TWELVE ? 4 : THIRTEEN ? 5 : FOURTEEN ? 6 : FIFTEEN ? 7 : names.length-1;
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
        if(t != null || t2 != null) {
            return Sound.valueOf(t != null ? t : t2);
        }
        return null;
    }
    public static USound match(@NotNull String name) {
        name = name.toUpperCase();
        if(CACHE.containsKey(name)) {
            return CACHE.get(name);
        } else {
            try {
                final USound sound = valueOf(name);
                CACHE.put(name, sound);
                return sound;
            } catch (Exception e) {
                for(USound sound : USound.values()) {
                    for(String target : sound.names) {
                        if(name.equals(target)) {
                            CACHE.put(name, sound);
                            return sound;
                        }
                    }
                }
            }
        }
        return null;
    }
    public static Sound matchSound(@NotNull String name) {
        name = name.toUpperCase();
        if(SOUND_CACHE.containsKey(name)) {
            return SOUND_CACHE.get(name);
        }
        final USound sound = match(name);
        if(sound != null) {
            return sound.getSound();
        } else {
            try {
                final Class clazz = Class.forName("org.bukkit.Sound");
                if(Enum.class.isAssignableFrom(clazz)) {
                    final Enum<?> value = Enum.valueOf(clazz, name);
                    final Sound s = (Sound) value;
                    SOUND_CACHE.put(name, s);
                    return s;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}