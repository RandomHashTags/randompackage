package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class MonthlyCrate {
    public static HashMap<String, MonthlyCrate> crates;
    public static HashMap<Integer, HashMap<Integer, MonthlyCrate>> categorySlots;
    private static Random random;
    public static HashMap<Player, List<String>> revealedRegular, revealedBonus;

    private YamlConfiguration yml;
    private String ymlName, title;
    private int category, categorySlot;
    private ItemStack item, background, redeem, bonus1, bonus2;
    private UInventory regular, bonus;
    private List<String> redeemFormat, bonusFormat, rewards, bonusRewards;
    private List<Integer> rewardSlots, bonusRewardSlots;
    public MonthlyCrate(File f) {
        if(crates == null) {
            crates = new HashMap<>();
            random = api.random;
            revealedRegular = new HashMap<>();
            revealedBonus = new HashMap<>();
            categorySlots = new HashMap<>();
        }
        yml = YamlConfiguration.loadConfiguration(f);
        ymlName = f.getName().split("\\.yml")[0];
        category = yml.getInt("category");
        categorySlot = yml.getInt("category slot");
        if(!categorySlots.containsKey(category)) {
            final HashMap<Integer, MonthlyCrate> C = new HashMap<>();
            C.put(categorySlot, this);
            categorySlots.put(category, C);
        } else {
            categorySlots.get(category).put(categorySlot, this);
        }
        crates.put(ymlName, this);
    }

    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return ymlName; }
    public int getCategory() { return category; }
    public int getCategorySlot() { return categorySlot; }
    public String getGuiTitle() {
        if(title == null) title = ChatColor.translateAlternateColorCodes('&', yml.getString("title"));
        return title;
    }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }
    public List<String> getRewards() {
        if(rewards == null) rewards = yml.getStringList("rewards");
        return rewards;
    }
    public List<String> getBonusRewards() {
        if(bonusRewards == null) bonusRewards = yml.getStringList("bonus");
        return bonusRewards;
    }
    public List<String> getRedeemFormat() {
        if(redeemFormat == null) redeemFormat = yml.getStringList("inventory.redeem format");
        return redeemFormat;
    }
    public List<String> getBonusFormat() {
        if(bonusFormat == null) bonusFormat = yml.getStringList("inventory.bonus format");
        return bonusFormat;
    }
    public ItemStack getBackground() {
        if(background == null) background = api.d(yml, "inventory.background");
        return background.clone();
    }
    public ItemStack getRedeem() {
        if(redeem == null) redeem = api.d(yml, "inventory.redeem");
        return redeem.clone();
    }
    public ItemStack getBonus1() {
        if(bonus1 == null) bonus1 = api.d(yml, "inventory.bonus 1");
        return bonus1.clone();
    }
    public ItemStack getBonus2() {
        if(bonus2 == null) bonus2 = api.d(yml, "inventory.bonus 2");
        return bonus2.clone();
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
                    if(f.get(i).substring(o, o+1).equals("-")) {
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
                    if(f.get(i).substring(o, o+1).equals("+")) {
                        bonusRewardSlots.add(i*9+o);
                    }
                }
            }
        }
        return bonusRewardSlots;
    }
    public ItemStack getRandomReward(Player player, List<String> excluding, boolean canRepeatRewards) {
        return getReward(player, revealedRegular, excluding, canRepeatRewards, new ArrayList<>(getRewards()));
    }
    public ItemStack getRandomBonusReward(Player player, List<String> excluding, boolean canRepeatRewards) {
        return getReward(player, revealedBonus, excluding, canRepeatRewards, new ArrayList<>(getBonusRewards()));
    }
    private ItemStack getReward(Player player, HashMap<Player, List<String>> type, List<String> excluding, boolean canRepeatRewards, List<String> rewardType) {
        if(!type.containsKey(player)) type.put(player, new ArrayList<>());
        if(excluding != null) {
            for(String e : excluding) rewardType.remove(e);
        }
        if(!canRepeatRewards) {
            for(String s : type.get(player)) rewardType.remove(s);
        }
        final String r = rewardType.get(random.nextInt(rewardType.size())), R = r.contains("||") ? r.split("\\|\\|")[random.nextInt(r.split("\\|\\|").length)] : r;
        if(!canRepeatRewards) type.get(player).add(r);
        final ItemStack is = api.d(null, R);
        if(is != null) {

        }
        return is;
    }

    public static void deleteAll() {
        crates = null;
        random = null;
        revealedRegular = null;
        revealedBonus = null;
    }

    public static MonthlyCrate valueOf(String title) {
        if(crates != null) {
            for(MonthlyCrate m : crates.values()) {
                if(m.getGuiTitle().equals(title)) {
                    return m;
                }
            }
        }
        return null;
    }
    public static MonthlyCrate valueOf(ItemStack item) {
        if(crates != null) {
            for(MonthlyCrate c : crates.values()) {
                if(c.getItem().isSimilar(item)) {
                    return c;
                }
            }
        }
        return null;
    }
    public static MonthlyCrate valueOf(Player player, ItemStack item) {
        if(crates != null && player != null && item != null) {
            final String p = player.getName();
            for(MonthlyCrate c : crates.values()) {
                final ItemStack is = c.getItem(), IS = is.clone();
                final ItemMeta m = is.getItemMeta();
                final List<String> s = new ArrayList<>();
                if(m.hasLore()) {
                    for(String l : m.getLore()) {
                        s.add(l.replace("{UNLOCKED_BY}", p));
                    }
                    m.setLore(s);
                }
                is.setItemMeta(m);
                if(item.isSimilar(is) || item.isSimilar(IS)) {
                    return c;
                }
            }
        }
        return null;
    }
    public static MonthlyCrate valueOf(int category, int slot) {
        if(categorySlots != null && categorySlots.containsKey(category)) {
            return categorySlots.get(category).getOrDefault(slot, null);
        }
        return null;
    }
}