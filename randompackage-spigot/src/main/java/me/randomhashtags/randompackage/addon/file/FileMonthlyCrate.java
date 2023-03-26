package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.MonthlyCrate;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FileMonthlyCrate extends RPAddonSpigot implements MonthlyCrate {
    private ItemStack item, background, redeem, bonus1, bonus2;
    private UInventory regular, bonus;
    private List<Integer> rewardSlots, bonusRewardSlots;

    public FileMonthlyCrate(File f) {
        super(f);
        register(Feature.MONTHLY_CRATE, this);
    }

    public int getCategory() {
        return yml.getInt("category");
    }
    public int getCategorySlot() {
        return yml.getInt("category slot");
    }
    public @NotNull String getGuiTitle() {
        return getString(yml, "title");
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        if(item == null) item = createItemStack(yml, "item");
        return getClone(item);
    }
    public @NotNull List<String> getRewards() {
        return getStringList(yml, "rewards");
    }
    public @NotNull List<String> getBonusRewards() {
        return getStringList(yml, "bonus");
    }
    public List<String> getRedeemFormat() {
        return getStringList(yml, "inventory.redeem format");
    }
    public List<String> getBonusFormat() {
        return getStringList(yml, "inventory.bonus format");
    }
    public ItemStack getBackground() {
        if(background == null) background = createItemStack(yml, "inventory.background");
        return getClone(background);
    }
    public ItemStack getRedeem() {
        if(redeem == null) redeem = createItemStack(yml, "inventory.redeem");
        return getClone(redeem);
    }
    public ItemStack getBonus1() {
        if(bonus1 == null) bonus1 = createItemStack(yml, "inventory.bonus 1");
        return getClone(bonus1);
    }
    public ItemStack getBonus2() {
        if(bonus2 == null) bonus2 = createItemStack(yml, "inventory.bonus 2");
        return getClone(bonus2);
    }
    public UInventory getRegular() {
        if(regular == null) {
            final ItemStack air = new ItemStack(Material.AIR);
            regular = new UInventory(null, yml.getInt("inventory.size"), getGuiTitle());
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
    public UInventory getBonus() {
        if(bonus == null) {
            final ItemStack air = new ItemStack(Material.AIR);
            bonus = new UInventory(null, yml.getInt("inventory.size"), getGuiTitle());
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
    public ItemStack getRandomReward(Player player, List<String> excluding, boolean canRepeatRewards) {
        return getReward(player, REVEALED_REGULAR, excluding, canRepeatRewards, new ArrayList<>(getRewards()));
    }
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
