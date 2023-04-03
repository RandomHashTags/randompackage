package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.FallenHero;
import me.randomhashtags.randompackage.addon.living.LivingFallenHero;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileFallenHero extends RPFallenHeroSpigot implements FallenHero {
    private final int gem_drop_chance;
    private final ItemStack spawn_item, gem;
    private final List<String> summon_message, receive_kit_message;
    private final String type;

    public FileFallenHero(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        spawn_item = create_item_stack(json, "spawn item");
        gem = create_item_stack(json, "gem");
        final JSONObject gem_json = json.getJSONObject("gem"), messages_json = json.getJSONObject("messages"), settings_json = json.getJSONObject("settings");
        gem_drop_chance = parse_int_in_json(gem_json, "chance");
        type = parse_string_in_json(settings_json, "type").toUpperCase();
        summon_message = parse_list_string_in_json(messages_json, "summon");
        receive_kit_message = parse_list_string_in_json(messages_json, "receive kit");
        register(Feature.FALLEN_HERO, this);
    }

    @Override
    public int getGemDropChance() {
        return gem_drop_chance;
    }
    @Override
    public @NotNull List<String> getSummonMsg() {
        return summon_message;
    }
    @Override
    public @NotNull List<String> getReceiveKitMsg() {
        return receive_kit_message;
    }
    @Override
    public String getType() {
        return type;
    }
    public List<PotionEffect> getPotionEffects() {
        return new ArrayList<>();
    }
    @Override
    @NotNull
    public ItemStack getSpawnItem() {
        return getClone(spawn_item);
    }
    @Override
    @NotNull
    public ItemStack getGem() {
        return getClone(gem);
    }
    public void spawn(LivingEntity summoner, Location loc, CustomKit kit) {
        if(loc != null && kit != null) {
            new LivingFallenHero(kit, this, summoner != null ? summoner.getUniqueId() : null, loc);
        }
    }
}
