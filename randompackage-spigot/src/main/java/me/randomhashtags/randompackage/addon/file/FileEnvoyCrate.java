package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.EnvoyCrate;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class FileEnvoyCrate extends RPAddonSpigot implements EnvoyCrate {
    private final Firework firework;
    private final int chance;
    private final UMaterial block, falling_block;
    private final String reward_size;
    private final boolean can_repeat_rewards, drops_from_sky;
    private final List<UMaterial> cannotLandAbove, cannotLandIn;
    private final List<String> rewards;
    private final ItemStack item;

    public FileEnvoyCrate(File file) {
        super(file);
        final JSONObject json = parse_json_from_file(file);

        final String[] f = json.getString("firework").split(":");
        firework = createFirework(FireworkEffect.Type.valueOf(f[0].toUpperCase()), getColor(f[1]), getColor(f[2]), Integer.parseInt(f[3]));

        chance = parse_int_in_json(json, "chance");
        rewards = parse_list_string_in_json(json, "rewards");

        final JSONObject settings_json = parse_json_in_json(json, "settings");
        final String target_block = parse_string_in_json(settings_json, "block"), target_falling_block = parse_string_in_json(settings_json, "falling block");
        block = UMaterial.match(target_block);
        falling_block = UMaterial.match(target_falling_block);
        reward_size = parse_string_in_json(settings_json, "reward size");
        can_repeat_rewards = parse_boolean_in_json(settings_json, "can repeat rewards");
        drops_from_sky = parse_boolean_in_json(settings_json, "drops from sky");

        cannotLandAbove = new ArrayList<>();
        for(String s : parse_list_string_in_json(settings_json, "cannot land above")) {
            cannotLandAbove.add(UMaterial.match(s));
        }

        cannotLandIn = new ArrayList<>();
        for(String s : parse_list_string_in_json(settings_json, "cannot land in")) {
            cannotLandIn.add(UMaterial.match(s));
        }

        item = create_item_stack(json, "item");

        register(Feature.ENVOY_CRATE, this);
    }
    @NotNull
    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Nullable
    public Firework getFirework() {
        return firework;
    }
    public int getChance() {
        return chance;
    }
    public @NotNull UMaterial getBlock() {
        return block;
    }
    public boolean canRepeatRewards() {
        return can_repeat_rewards;
    }
    public boolean dropsFromSky() {
        return drops_from_sky;
    }
    public UMaterial getFallingBlock() {
        return falling_block;
    }
    public @NotNull String getRewardSize() {
        return reward_size;
    }
    public @NotNull List<UMaterial> cannotLandAbove() {
        return cannotLandAbove;
    }
    public @NotNull List<UMaterial> cannotLandIn() {
        return cannotLandIn;
    }
    public @NotNull ItemStack getItem() {
        return getClone(item);
    }
    @NotNull
    public List<String> getRewards() {
        return rewards;
    }

    public int getRandomRewardSize() {
        final String[] s = reward_size.split("-");
        final boolean c = reward_size.contains("-");
        final int min = c ? Integer.parseInt(s[0]) : Integer.parseInt(reward_size), max = c ? Integer.parseInt(s[1]) : -1;
        return min + (max == -1 ? 0 : new Random().nextInt(max-min+1));
    }
    public @NotNull List<String> getRandomRewards() {
        final List<String> rewards = new ArrayList<>(this.getRewards()), actual_rewards = new ArrayList<>();
        final Random random = new Random();
        final boolean canRepeatRewards = canRepeatRewards();
        for(int i = 1; i <= getRandomRewardSize(); i++) {
            if(rewards.size() != 0) {
                final String reward = rewards.get(random.nextInt(rewards.size()));
                final boolean hasChance = reward.toLowerCase().contains(";chance=");
                final String[] a = reward.split(";chance=");
                if(!hasChance || random.nextInt(100) <= getRemainingInt(a[1])) {
                    actual_rewards.add(a[0]);
                    if(!canRepeatRewards) {
                        rewards.remove(reward);
                    }
                } else {
                    i -= 1;
                }
            }
        }
        return actual_rewards;
    }
    public @NotNull List<ItemStack> getRandomizedRewards() {
        final List<String> r = getRandomRewards();
        final List<ItemStack> a = new ArrayList<>();
        for(String s : r) {
            final ItemStack i = createItemStack(null, s);
            if(i != null && !i.getType().equals(Material.AIR)) {
                a.add(i);
            }
        }
        return a;
    }
}
