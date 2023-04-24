package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.ConquestChest;
import me.randomhashtags.randompackage.addon.obj.ConquestMob;
import me.randomhashtags.randompackage.addon.living.LivingConquestChest;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public final class FileConquestChest extends RPAddonSpigot implements ConquestChest {
    private final String spawn_region, reward_size;
    private final int spawn_interval, max_hp, spawned_hp, damage_per_hit, distance_check_interval;
    private final double boss_max_distance_from_conquest;
    private final int health_radius, first_announced;
    private final int announce_interval_after_spawned, despawn_delay;
    private final double damage_delay;
    private final HashMap<ConquestMob, String> spawned_bosses;
    private final UMaterial placedBlock;
    private final List<String> rewards, hit_attributes;
    private final List<String> spawn, will_spawn, still_alive, health, unlocked;

    public FileConquestChest(File f) {
        super(f);

        final JSONObject json = parse_json_from_file(f);
        final JSONObject settings_json = json.getJSONObject("settings");
        spawn_region = parse_string_in_json(settings_json, "spawn region").toUpperCase();
        reward_size = parse_string_in_json(settings_json, "reward size");
        spawn_interval = parse_int_in_json(settings_json, "spawn interval");
        max_hp = parse_int_in_json(settings_json, "max hp");
        spawned_hp = parse_int_in_json(settings_json, "spawned hp");
        damage_per_hit = parse_int_in_json(settings_json, "dmg per hit");
        damage_delay = parse_double_in_json(settings_json, "dmg delay");
        placedBlock = UMaterial.match(parse_string_in_json(settings_json, "placed block", "ENDER_CHEST").toUpperCase());

        final JSONObject bosses_json = settings_json.getJSONObject("bosses");
        boss_max_distance_from_conquest = parse_double_in_json(bosses_json, "max distance from conquest", 50);
        distance_check_interval = parse_int_in_json(bosses_json, "distance check interval", 60);

        rewards = parse_list_string_in_json(json, "rewards");
        hit_attributes = parse_list_string_in_json(json, "hit attributes");

        spawned_bosses = new HashMap<>();
        for(String s : parse_list_string_in_json(json, "spawned bosses")) {
            final String[] a = s.split(":");
            spawned_bosses.put(ConquestMob.BOSSES.get(a[0]), a[1]);
        }

        final JSONObject messages_json = json.getJSONObject("messages");
        announce_interval_after_spawned = parse_int_in_json(messages_json, "announce interval after spawned");
        despawn_delay = parse_int_in_json(messages_json, "despawn delay");

        health_radius = parse_int_in_json(messages_json, "health radius");
        first_announced = parse_int_in_json(messages_json, "first announced");
        spawn = parse_list_string_in_json(messages_json, "spawn");
        will_spawn = parse_list_string_in_json(messages_json, "will spawn");
        still_alive = parse_list_string_in_json(messages_json, "still alive");
        health = parse_list_string_in_json(messages_json, "health");
        unlocked = parse_list_string_in_json(messages_json, "unlocked");

        register(Feature.CONQUEST_CHEST, this);
    }

    public String getSpawnRegion() {
        return spawn_region;
    }
    public String getRewardSize() {
        return reward_size;
    }
    public HashMap<ConquestMob, String> getSpawnedBosses() {
        if(spawned_bosses == null) {

        }
        return spawned_bosses;
    }
    public int getSpawnInterval() {
        return spawn_interval;
    }
    public int getHealthMsgRadius() {
        return health_radius;
    }
    public int getMaxHP() {
        return max_hp;
    }
    public int getSpawnedHP() {
        return spawned_hp;
    }
    public int getDamagePerHit() {
        return damage_per_hit;
    }
    public int getFirstAnnounced() {
        return first_announced;
    }
    public int getAnnounceIntervalAfterSpawned() {
        return announce_interval_after_spawned;
    }
    public int getDespawnDelay() {
        return despawn_delay;
    }
    public double getDamageDelay() {
        return damage_delay;
    }
    @NotNull
    public UMaterial getPlacedBlock() {
        return placedBlock;
    }
    public @NotNull List<String> getRewards() {
        return rewards;
    }
    public List<String> getHitAttributes() {
        return hit_attributes;
    }
    public List<String> getSpawnMsg() {
        return spawn;
    }
    public List<String> getWillSpawnMsg() {
        return will_spawn;
    }
    public List<String> getStillAliveMsg() {
        return still_alive;
    }
    public List<String> getHealthMsg() {
        return health;
    }
    public List<String> getUnlockedMsg() {
        return unlocked;
    }

    public LivingConquestChest spawn(Location l) {
        final Chunk c = l.getChunk();
        if(!c.isLoaded()) {
            c.load();
        }
        return new LivingConquestChest(l, this, System.currentTimeMillis(), true, true);
    }

    public double getBossMaxDistanceFromConquest() {
        return boss_max_distance_from_conquest;
    }
    public int getBossDistanceCheckInterval() {
        return distance_check_interval;
    }
}