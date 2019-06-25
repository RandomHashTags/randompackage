package me.randomhashtags.randompackage;

import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.utils.GivedpItem;
import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import me.randomhashtags.randompackage.utils.classes.customenchants.EnchantRarity;
import me.randomhashtags.randompackage.utils.supported.FactionsAPI;
import me.randomhashtags.randompackage.utils.supported.MCMMOAPI;
import me.randomhashtags.randompackage.utils.supported.VaultAPI;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;

import java.io.File;
import java.util.*;

public class RandomPackageAPI extends UVersion {

    private static RandomPackageAPI instance;
    public static final RandomPackageAPI getAPI() {
        if(instance == null) instance = new RandomPackageAPI();
        return instance;
    }

    private boolean isEnabled = false;
    public boolean mcmmoIsEnabled = false;

    private static ConsoleCommandSender console;
    public static GivedpItem givedpitem;
    public static FactionsAPI fapi;
    public static String separator;
    public static File rpd, otherdataF;
    public static YamlConfiguration otherdata;
    public static int spawnerchance = 0;
    private static TreeMap<Integer, String> treemap;

    public static Economy eco;
    public static UInventory givedp;
    public static List<Inventory> givedpCategories;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String n = cmd.getName();
        if(n.equals("randompackage")) {
            if(args.length == 0) {
                if(player != null && player.getName().equals("RandomHashTags") || hasPermission(sender, "RandomPackage.randompackage", true)) {
                    final Plugin spawner = RandomPackage.spawnerPlugin, mcmmo = RandomPackage.mcmmo;
                    for(String string : Arrays.asList(" ",
                            "&6&m&l---------------------------------------------",
                            "&7- Author: &6RandomHashTags",
                            "&7- RandomPackage Version: &b" + randompackage.getDescription().getVersion(),
                            "&7- Server Version: &f" + version,
                            "&7- Faction Plugin: " + (fapi.factions != null ? "&3" + fapi.factions + " &7(&2" + pluginmanager.getPlugin("Factions").getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- mcMMO: " + (mcmmo != null ? "&a" + (MCMMOAPI.getMCMMOAPI().isClassic ? "Classic" : "Overhaul") + " &7(&2" + mcmmo.getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- Spawner Plugin: " + (spawner != null ? "&e" + spawner.getName() + " &7(&2" + spawner.getDescription().getVersion() + "&7)" : "&cfalse"),
                            "&7- Wiki: &9https://gitlab.com/RandomHashTags/randompackage/wikis/Home",
                            "&7- Info: &f%%__USER__%%, &f%%__NONCE__%%",
                            "&7- Purchaser: &a&nhttps://www.spigotmc.org/members/%%__USER__%%/",
                            "&6&m&l---------------------------------------------",
                            " "))
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
                }
            } else if(args[0].equals("reload") && hasPermission(sender, "RandomPackage.randompackage.reload", true)) {
                RandomPackage.getPlugin.reload();
            } else if(args[0].equals("backup") && hasPermission(sender, "RandomPackage.randompackage.backup", true)) {
                RPEvents.getRPEvents().backup();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3[RandomPackage] &aPlayer backup complete!"));
            }
        }
        return true;
    }
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final int l = args.length;
        if(cmd.getName().equals("randompackage") && sender.hasPermission("RandomPackage.customenchant.enchant") && l >= 1) {
            final List<String> lore = new ArrayList<>();
            if(args[0].equals("enchant")) {
                if(l == 2) {
                    for(String s : CustomEnchant.enabled.keySet()) lore.add(s.replace(" ", "_"));
                } else if(l == 3) {
                    final CustomEnchant e = CustomEnchant.enabled.getOrDefault(args[1].toUpperCase().replace("_", " "), null);
                    if(e != null) for(int i = 1; i <= e.getMaxLevel(); i++) lore.add(Integer.toString(i));
                }
            }
            return lore;
        }
        return null;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        save("_Data", "other.yml");
        rpd = RandomPackage.getPlugin.getDataFolder();
        separator = File.separator;
        otherdataF = new File(rpd + separator + "_Data", "other.yml");
        otherdata = YamlConfiguration.loadConfiguration(otherdataF);
        pluginmanager.registerEvents(this, randompackage);
        isEnabled = true;

        mcmmoIsEnabled = pluginmanager.isPluginEnabled("mcMMO");
        fapi = FactionsAPI.getFactionsAPI();
        final Plugin f = pluginmanager.getPlugin("Factions");
        fapi.factions = f != null ? f.getDescription().getAuthors().contains("ProSavage") ? "SavageFactions" : "Factions" : null;


        eco = VaultAPI.getVaultAPI().economy;
        console = Bukkit.getConsoleSender();

        givedpitem = GivedpItem.getGivedpItem();
        givedpitem.load();
        RandomPackage.getPlugin.getCommand("givedp").setExecutor(givedpitem);
        givedp = new UInventory(null, 27, "Givedp Categories");
        givedpCategories = new ArrayList<>();

        treemap = new TreeMap<>();
        treemap.put(1000, "M"); treemap.put(900, "CM"); treemap.put(500, "D"); treemap.put(400, "CD"); treemap.put(100, "C"); treemap.put(90, "XC");
        treemap.put(50, "L"); treemap.put(40, "XL"); treemap.put(10, "X"); treemap.put(9, "IX"); treemap.put(5, "V"); treemap.put(4, "IV"); treemap.put(1, "I");
        sendConsoleMessage("&6[RandomPackage] &aInfo: &e%%__USER__%%, %%__NONCE__%%");
        sendConsoleMessage("&6[RandomPackage] &aLoaded API &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;

        console = null;
        givedpitem = null;
        fapi = null;
        rpd = null;
        separator = null;
        otherdataF = null;
        otherdata = null;
        spawnerchance = 0;
        treemap = null;
        eco = null;
        givedp = null;
        givedpCategories = null;
        eventmanager.unregisterListeners(this);
    }
    public void saveOtherData() {
        try {
            otherdata.save(otherdataF);
            otherdataF = new File(rpd + separator + "_Data", "other.yml");;
            otherdata = YamlConfiguration.loadConfiguration(otherdataF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewGivedp(Player player) {
        player.openInventory(Bukkit.createInventory(player, givedp.getSize(), givedp.getTitle()));
        player.getOpenInventory().getTopInventory().setContents(givedp.getInventory().getContents());
        player.updateInventory();
    }
    public void addGivedpCategory(List<ItemStack> items, UMaterial m, String what, String invtitle) {
        item = m.getItemStack(); itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + what);
        item.setItemMeta(itemMeta);
        givedp.getInventory().addItem(item);
        final int size = items.size();
        final Inventory inv = Bukkit.createInventory(null, size == 9 || size == 18 || size == 27 || size == 36 || size == 45 || size == 54 ? size : ((size+9)/9)*9, invtitle);
        for(ItemStack is : items) if(is != null) inv.addItem(is);
        givedpCategories.add(inv);
    }
    public void sendConsoleMessage(String message) {
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    public boolean hasPermission(Player sender, String permission, boolean sendNoPermMessage) {
        if(sender.hasPermission(permission)) return true;
        else if(sendNoPermMessage) {
            sendStringListMessage(sender, randompackage.getConfig().getStringList("no permission"), null);
        }
        return false;
    }
    public ItemStack d(FileConfiguration config, String path) {
        return d(config, path, 0.00);
    }
    public ItemStack d(FileConfiguration config, String path, double enchantMultiplier) {
        item = null;
        if(config == null && path != null || config != null && config.get(path + ".item") != null) {
            final String PP = config == null ? path : config.getString(path + ".item");
            String P = PP.toLowerCase();

            int amount = config != null && config.get(path + ".amount") != null ? config.getInt(path + ".amount") : 1;
            if(P.toLowerCase().contains(";amount=")) {
                final String A = P.split("=")[1];
                final boolean B = P.contains("-");
                final int min = B ? Integer.parseInt(A.split("-")[0]) : 0;
                amount = B ? min+random.nextInt(Integer.parseInt(A.split("-")[1])-min+1) : Integer.parseInt(A);
                path = path.split(";amount=")[0];
                P = P.split(";")[0];
            }
            if(P.contains("spawner") && !P.startsWith("mob_spawner") && !path.equals("mysterymobspawner")) {
                return getSpawner(P);
            } else if(P.startsWith("enchantedbook:")) {
                final Enchantment e = getEnchantment(P.split(":")[1]);
                if(e != null) {
                    int level = 1;
                    if(P.split(":").length == 3)
                        level = P.split(":")[2].equals("random") ? 1 + random.nextInt(e.getMaxLevel()) : P.split(":")[2].contains("-") ? Integer.parseInt(P.split(":")[2].split("\\-")[0]) + random.nextInt(Integer.parseInt(P.split(":")[2].split("\\-")[1])) : Integer.parseInt(P.split(":")[2]);
                    item = new ItemStack(Material.ENCHANTED_BOOK, amount);
                    final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                    meta.addStoredEnchant(e, level, true);
                    item.setItemMeta(meta);
                    return item;
                }
                return null;
            }
            ItemStack B = givedpitem.valueOf(PP);
            if(B == null) B = givedpitem.valueOf(P);
            if(B != null) {
                item = B.copy();
                item.setQuantity(amount);
                return item;
            }
            boolean enchanted = config != null && config.getBoolean(path + ".enchanted");
            lore.clear();
            SkullMeta m = null;
            String name = config != null ? config.getString(path + ".name") : null;
            final String[] material = P.toUpperCase().split(":");
            final String mat = material[0];
            final byte data = material.length == 2 ? Byte.parseByte(material[1]) : 0;
            final UMaterial U = UMaterial.match(mat + (data != 0 ? ":" + data : ""));
            try {
                item = U.getItemStack();
                final Material skullitem = UMaterial.PLAYER_HEAD_ITEM.getMaterial(), i = item.getType();
                if(!i.equals(Material.AIR)) {
                    item.setAmount(amount);
                    itemMeta = item.getItemMeta();
                    if(i.equals(skullitem)) {
                        m = (SkullMeta) itemMeta;
                        if(item.getData().getData() == 3) m.setOwner(P.split(":").length == 4 ? P.split(":")[3].split("}")[0] : "RandomHashTags");
                    }
                    (i.equals(skullitem) ? m : itemMeta).setDisplayName(name != null ? ChatColor.translateAlternateColorCodes('&', name) : null);

                    if(enchanted) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    final HashMap<Enchantment, Integer> enchants = new HashMap<>();
                    final CustomEnchants ce = CustomEnchants.getCustomEnchants();
                    final boolean levelzeroremoval = ce.levelZeroRemoval;
                    if(config != null && config.get(path + ".lore") != null) {
                        lore.clear();
                        for(String string : config.getStringList(path + ".lore")) {
                            final String sl = string.toLowerCase();
                            if(sl.startsWith("venchants{")) {
                                for(String s : string.split("\\{")[1].split("}")[0].split(";")) {
                                    enchants.put(getEnchantment(s), getRemainingInt(s));
                                }
                            } else if(sl.startsWith("rpenchants{")) {
                                for(String s : string.split("\\{")[1].split("}")[0].split(";")) {
                                    final CustomEnchant e = CustomEnchant.valueOf(s);
                                    if(e != null) {
                                        int l = getRemainingInt(s), x = (int) (e.getMaxLevel()*enchantMultiplier);
                                        l = l != -1 ? l : x+random.nextInt(e.getMaxLevel()-x+1);
                                        if(l != 0 || !levelzeroremoval)
                                            lore.add(EnchantRarity.valueOf(e).getApplyColors() + e.getName() + " " + toRoman(l != 0 ? l : 1));
                                    }
                                }
                            } else {
                                lore.add(ChatColor.translateAlternateColorCodes('&', string));
                            }
                        }
                    }
                    (!i.equals(skullitem) ? itemMeta : m).setLore(lore);
                    item.setItemMeta(!item.getType().equals(skullitem) ? itemMeta : m);
                    lore.clear();
                    if(enchanted) item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                    for(Enchantment enchantment : enchants.keySet()) {
                        if(enchantment != null) {
                            item.addUnsafeEnchantment(enchantment, enchants.get(enchantment));
                        }
                    }
                    if(name != null && name.contains("{ENCHANT_SIZE}")) ce.applyTransmogScroll(item);
                }
            } catch(Exception e) {
                System.out.println("UMaterial null itemstack. mat=" + mat + ";data=" + data + ";versionName=" + (U != null ? U.getVersionName() : null) + ";getMaterial()=" + (U != null ? U.getMaterial() : null));
                return null;
            }
        }
        return item;
    }
    public final String toRoman(int number) {
        /* This code is from "bhlangonijr" at https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java */
        if(number <= 0) return "";
        int l = treemap.floorKey(number);
        if(number == l) return treemap.get(number);
        return treemap.get(l) + toRoman(number - l);
    }
    public double oldevaluate(String chancestring) {
        chancestring = chancestring.replace("\\p{L}", "").replaceAll("\\p{Z}", "");
        String parentheses = null;
        double prockchance = 0;
        if(chancestring.contains("(") && chancestring.contains(")")) {
            for(int z = 1; z <= 5; z++) {
                int startp = -1, endp = -1;
                for(int i = 0; i < chancestring.length(); i++) {
                    if(chancestring.substring(i, i + 1).equals("(")) {
                        startp = i;
                    } else if(chancestring.substring(i, i + 1).equals(")")) {
                        endp = i + 1;
                    }
                    if(startp != -1 && endp != -1) {
                        parentheses = chancestring.substring(startp, endp);
                        prockchance = evaluate(parentheses.substring(1, parentheses.length() - 1));
                        chancestring = chancestring.replace(parentheses, "" + prockchance);
                        if(chancestring.endsWith("+") || chancestring.endsWith("-") || chancestring.endsWith("*") || chancestring.endsWith("/")) {
                            chancestring = chancestring.substring(0, chancestring.length() - 1);
                        }
                        if(chancestring.startsWith("+") || chancestring.startsWith("-") || chancestring.startsWith("*") || chancestring.startsWith("/")) {
                            chancestring = chancestring.substring(1);
                        }
                        startp = -1; endp = -1;
                    }
                }
            }
        }
        return evaluate(chancestring);
    }
    private double evaluate(String input) {
        double chance = 0.00;
        if(input.equals("-1")) return chance;
        for(int i = 1; i <= 5; i++) {
            String sign = null;
            if(input.contains("*")) {
                sign = input.split("\\*")[0] + "*" + input.split("\\*")[1];
                chance = Double.parseDouble(input.split("\\*")[0]) * Double.parseDouble(input.split("\\*")[1]);
            } else if(input.contains("/")) {
                sign = input.split("/")[0] + "/" + input.split("/")[1];
                chance = Double.parseDouble(input.split("\\/")[0]) / Double.parseDouble(input.split("\\/")[1]);
            } else if(input.contains("+")) {
                sign = input.split("\\+")[0] + "+" + input.split("\\+")[1];
                chance = Double.parseDouble(input.split("\\+")[0]) + Double.parseDouble(input.split("\\+")[1]);
            } else if(input.contains("-") && !input.startsWith("-")) {
                sign = input.split("-")[0] + "-" + input.split("-")[1];
                chance = Double.parseDouble(input.split("\\-")[0]) - Double.parseDouble(input.split("\\-")[1]);
            } else if(!input.equals("")) {
                return Double.valueOf(input);
            }
            if(sign != null) input = input.replace(sign, "" + chance);
        }
        return chance;
    }


    @Listener
    private void inventoryClickEvent(ClickInventoryEvent event) {
        final ItemStack c = event.getCurrentItem();
        if(!event.isCancelled() && c != null && !c.getType().equals(Material.AIR)) {
            final Player player = (Player) event.getWhoClicked();
            final InventoryArchetype inv = event.getTargetInventory().getArchetype();
            final String title = getTitle(inv);
            final int size = getSize(inv);
            final int r = event.getRawSlot();
            if(title.equals(givedp.getTitle()) && r < size) {
                player.openInventory(givedpCategories.get(r));
            } else if(givedpCategories.contains(event.getClickedInventory()) && r < size) {
                giveItem(player, c);
            } else return;
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}