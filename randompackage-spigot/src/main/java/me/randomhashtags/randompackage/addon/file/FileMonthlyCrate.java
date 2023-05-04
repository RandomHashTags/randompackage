package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.MonthlyCrate;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FileMonthlyCrate extends RPAddonSpigot implements MonthlyCrate {
    private final int category, category_slot;
    private final String gui_title;
    private final ItemStack item, background, redeem, bonus1, bonus2;
    private final List<String> rewards, rewards_bonus;
    private UInventory regular, bonus;
    private final int inventory_size;
    private final List<String> redeem_format, redeem_bonus_format;
    private List<Integer> rewardSlots, bonusRewardSlots;

    public FileMonthlyCrate(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        category = parse_int_in_json(json, "category");
        category_slot = parse_int_in_json(json, "category slot");
        gui_title = parse_string_in_json(json, "title");
        item = create_item_stack(json, "item");
        rewards = parse_list_string_in_json(json, "rewards");
        rewards_bonus = parse_list_string_in_json(json, "bonus");

        final JSONObject inventory_json = json.optJSONObject("inventory", new JSONObject());
        inventory_size = parse_int_in_json(inventory_json, "size");
        redeem_format = parse_list_string_in_json(inventory_json, "redeem format");
        redeem_bonus_format = parse_list_string_in_json(inventory_json, "redeem bonus format");
        background = create_item_stack(inventory_json, "background");
        redeem = create_item_stack(inventory_json, "redeem");
        bonus1 = create_item_stack(inventory_json, "bonus 1");
        bonus2 = create_item_stack(inventory_json, "bonus 2");
        register(Feature.MONTHLY_CRATE, this);
    }

    @Override
    public int getCategory() {
        return category;
    }
    @Override
    public int getCategorySlot() {
        return category_slot;
    }
    @Override
    public @NotNull String getGuiTitle() {
        return gui_title;
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(item);
    }
    @Override
    public @NotNull List<String> getRewards() {
        return rewards;
    }
    @Override
    public @NotNull List<String> getBonusRewards() {
        return rewards_bonus;
    }
    public List<String> getRedeemFormat() {
        return redeem_format;
    }
    public List<String> getBonusFormat() {
        return redeem_bonus_format;
    }
    @Override
    public ItemStack getBackground() {
        return getClone(background);
    }
    @Override
    public ItemStack getRedeem() {
        return getClone(redeem);
    }
    @Override
    public ItemStack getBonus1() {
        return getClone(bonus1);
    }
    @Override
    public ItemStack getBonus2() {
        return getClone(bonus2);
    }
    @Override
    public UInventory getRegular() {
        if(regular == null) {
            final ItemStack air = new ItemStack(Material.AIR);
            regular = new UInventory(null, inventory_size, getGuiTitle());
            final Inventory ri = regular.getInventory();
            final List<String> f = getRedeemFormat();
            for(int i = 0; i < f.size(); i++) {
                final String k = f.get(i);
                for(int o = 0; o < k.length(); o++) {
                    final String target = f.get(i).substring(o, o+1);
                    final ItemStack is = target.equals("=") ? getBackground() : target.equals("-") ? getRedeem() : air;
                    ri.setItem(i*9+o, is);
                }
            }
        }
        return regular;
    }
    @Override
    public UInventory getBonus() {
        if(bonus == null) {
            final ItemStack air = new ItemStack(Material.AIR);
            bonus = new UInventory(null, inventory_size, getGuiTitle());
            final Inventory bi = bonus.getInventory();
            final List<String> f = getBonusFormat();
            for(int i = 0; i < f.size(); i++) {
                final String k = f.get(i);
                for(int o = 0; o < k.length(); o++) {
                    final String target = f.get(i).substring(o, o+1);
                    final ItemStack is = target.equals("=") ? getBonus2() : target.equals("-") ? getBonus1() : air;
                    bi.setItem(i*9+o, is);
                }
            }
        }
        return bonus;
    }
    @Override
    public List<Integer> getRewardSlots() {
        if(rewardSlots == null) {
            rewardSlots = new ArrayList<>();
            final List<String> f = getRedeemFormat();
            for(int i = 0; i < f.size(); i++) {
                final String k = f.get(i);
                for(int o = 0; o < k.length(); o++) {
                    if(f.get(i).charAt(o) == '-') {
                        rewardSlots.add(i*9+o);
                    }
                }
            }
        }
        return rewardSlots;
    }
    @Override
    public List<Integer> getBonusRewardSlots() {
        if(bonusRewardSlots == null) {
            bonusRewardSlots = new ArrayList<>();
            final List<String> f = getBonusFormat();
            for(int i = 0; i < f.size(); i++) {
                final String k = f.get(i);
                for(int o = 0; o < k.length(); o++) {
                    if(f.get(i).charAt(o) == '+') {
                        bonusRewardSlots.add(i*9+o);
                    }
                }
            }
        }
        return bonusRewardSlots;
    }
    @Override
    public ItemStack getRandomReward(Player player, List<String> excluding, boolean canRepeatRewards) {
        return getReward(player, REVEALED_REGULAR, excluding, canRepeatRewards, new ArrayList<>(getRewards()));
    }
    @Override
    public ItemStack getRandomBonusReward(Player player, List<String> excluding, boolean canRepeatRewards) {
        return getReward(player, REVEALED_BONUS, excluding, canRepeatRewards, new ArrayList<>(getBonusRewards()));
    }
    private ItemStack getReward(Player player, HashMap<Player, List<String>> type, List<String> excluding, boolean canRepeatRewards, List<String> rewardType) {
        if(!type.containsKey(player)) {
            type.put(player, new ArrayList<>());
        }
        if(excluding != null) {
            rewardType.removeAll(excluding);
        }
        if(!canRepeatRewards) {
            rewardType.removeAll(type.get(player));
        }
        final String r = rewardType.get(RANDOM.nextInt(rewardType.size())), R = r.contains("||") ? r.split("\\|\\|")[RANDOM.nextInt(r.split("\\|\\|").length)] : r;
        if(!canRepeatRewards) {
            type.get(player).add(r);
        }
        final ItemStack is = createItemStack(null, R);
        if(is != null) {
        }
        return is;
    }
}
