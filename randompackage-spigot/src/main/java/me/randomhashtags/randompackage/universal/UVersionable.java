package me.randomhashtags.randompackage.universal;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.mechanics.SpawnerAPI;
import me.randomhashtags.randompackage.util.Versionable;
import me.randomhashtags.randompackage.util.YamlUpdater;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public interface UVersionable extends Versionable {
    HashMap<Feature, LinkedHashMap<String, Identifiable>> FEATURES = new HashMap<>();
    RandomPackage RANDOM_PACKAGE = RandomPackage.GET_PLUGIN;

    String SEPARATOR = File.separator;
    File DATA_FOLDER = RANDOM_PACKAGE.getDataFolder();
    FileConfiguration RP_CONFIG = RANDOM_PACKAGE.getConfig();
    String RP_VERSION = RANDOM_PACKAGE.getDescription().getVersion();
    PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    Server SERVER = Bukkit.getServer();
    Random RANDOM = new Random();

    BukkitScheduler SCHEDULER = Bukkit.getScheduler();
    ScoreboardManager SCOREBOARD_MANAGER = Bukkit.getScoreboardManager();
    ConsoleCommandSender CONSOLE = Bukkit.getConsoleSender();

    ItemStack AIR = new ItemStack(Material.AIR);

    BlockFace[] BLOCK_FACES = new BlockFace[] { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
    EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND, EIGHT ? null : EquipmentSlot.OFF_HAND };
    TreeMap<Integer, String> ROMAN_MAP = new TreeMap<Integer, String>() {{
        put(1000, "M");
        put(900, "CM");
        put(500, "D");
        put(400, "CD");
        put(100, "C");
        put(90, "XC");
        put(50, "L");
        put(40, "XL");
        put(10, "X");
        put(9, "IX");
        put(5, "V");
        put(4, "IV");
        put(1, "I");
    }};
    Set<String> INTERACTABLE_MATERIALS = new HashSet<String>() {{
        addAll(Arrays.asList(
            "BELL",
            "BREWING_STAND",
            "BLAST_FURNACE",
            "CAULDRON",
            "CHEST",
            "COMPOSTER",
            "CRAFTING_TABLE", "WORKBENCH",
            "DISPENSER",
            "DROPPER",
            "ENDER_CHEST",
            "ENCHANTMENT_TABLE", "ENCHANTING_TABLE",
            "FENCE_GATE",
            "FURNACE",
            "GRINDSTONE",
            "HOPPER",
            "JUKEBOX",
            "LECTERN",
            "LOOM",
            "LEVER",
            "NOTE_BLOCK",
            "SMOKER",
            "STONECUTTER",
            "TNT",
            "TRAPDOOR",
            "TRAPPED_CHEST",
            //
            "BED"
        ));
    }};

    HashMap<FileConfiguration, HashMap<String, List<String>>> FEATURE_MESSAGES = new HashMap<>();
    HashMap<FileConfiguration, HashMap<String, String>> FEATURE_STRINGS = new HashMap<>();

    default List<String> getStringList(FileConfiguration yml, String identifier) {
        FEATURE_MESSAGES.putIfAbsent(yml, new HashMap<>());
        final HashMap<String, List<String>> messages = FEATURE_MESSAGES.get(yml);
        messages.putIfAbsent(identifier, colorizeListString(yml.getStringList(identifier)));
        return messages.get(identifier);
    }
    default String getString(FileConfiguration yml, String identifier) {
        FEATURE_STRINGS.putIfAbsent(yml, new HashMap<>());
        final HashMap<String, String> strings = FEATURE_STRINGS.get(yml);
        strings.putIfAbsent(identifier, colorize(yml.getString(identifier)));
        return strings.get(identifier);
    }

    default void save(String folder, String file) {
        final boolean hasFolder = folder != null && !folder.equals("");
        final File f = new File(DATA_FOLDER + SEPARATOR + (hasFolder ? folder + SEPARATOR : ""), file);
        if(!f.exists()) {
            f.getParentFile().mkdirs();
            RANDOM_PACKAGE.saveResource(hasFolder ? folder + SEPARATOR + file : file, false);
        }
        if(folder == null || !folder.equals("_Data")) {
            YamlUpdater.INSTANCE.updateYaml(folder, f);
        }
    }

    default ItemStack getClone(ItemStack is) {
        return getClone(is, null);
    }
    default ItemStack getClone(ItemStack is, ItemStack def) {
        return is != null ? is.clone() : def;
    }

    default File[] getFilesInFolder(@NotNull String folder) {
        final File f = new File(folder);
        return f.exists() ? f.listFiles() : new File[]{};
    }
    default HashSet<String> getConfigurationSectionKeys(YamlConfiguration yml, String key, boolean includeKeys, String...excluding) {
        final ConfigurationSection section = yml.getConfigurationSection(key);
        if(section != null) {
            final HashSet<String> set = new HashSet<>(section.getKeys(includeKeys));
            set.removeAll(Arrays.asList(excluding));
            return set;
        } else {
            return new HashSet<>();
        }
    }
    default String toRoman(int number) {
        /* This code is from "bhlangonijr" at https://stackoverflow.com/questions/12967896 */
        if(number <= 0) {
            return "";
        }
        int l = ROMAN_MAP.floorKey(number);
        if(number == l) {
            return ROMAN_MAP.get(number);
        }
        return ROMAN_MAP.get(l) + toRoman(number-l);
    }
    enum RomanNumeralValues {
        I(1), X(10), C(100), M(1000), V(5), L(50), D(500);
        private final int value;
        RomanNumeralValues(int value) {
            this.value = value;
        }
        public int asInt() {
            return value;
        }
    }
    default int fromRoman(String num) {
        /* This code is from "batman" at https://stackoverflow.com/questions/9073150 */
        num = ChatColor.stripColor(num.toUpperCase());
        int intNum = 0, prev = 0;
        for(int i = num.length()-1; i >= 0; i--) {
            final String character = num.substring(i, i+1);
            int temp = RomanNumeralValues.valueOf(character).asInt();
            if(temp < prev) {
                intNum -= temp;
            } else {
                intNum += temp;
            }
            prev = temp;
        }
        return intNum;
    }

    default boolean allowsPvP(Player player, Location location) {
        return RegionalAPI.INSTANCE.allowsPvP(player, location);
    }

    default int getTotalExperience(Player player) {
        final double levelxp = LevelToExp(player.getLevel()), nextlevelxp = LevelToExp(player.getLevel() + 1), difference = nextlevelxp - levelxp;
        final double p = (levelxp + (difference * player.getExp()));
        return (int) Math.round(p);
    }
    default void setTotalExperience(Player player, int total) {
        player.setTotalExperience(0);
        player.setExp(0f);
        player.setLevel(0);
        player.giveExp(total);
    }
    default double LevelToExp(int level) {
        return level <= 16 ? (level * level) + (level * 6) : level <= 31 ? (2.5 * level * level) - (40.5 * level) + 360 : (4.5 * level * level) - (162.5 * level) + 2220;
    }

    default String getRemainingTime(long time) {
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(time), min = sec/60, hr = min/60, d = hr/24;
        hr -= d*24;
        min -= (hr*60)+(d*60*24);
        sec -= (min*60)+(hr*60*60)+(d*60*60*24);
        final String dys = d > 0 ? d + "d" + (hr != 0 ? " " : "") : "";
        final String hrs = hr > 0 ? hr + "h" + (min != 0 ? " " : "") : "";
        final String mins = min != 0 ? min + "m" + (sec != 0 ? " " : "") : "";
        final String secs = sec != 0 ? sec + "s" : "";
        return dys + hrs + mins + secs;
    }
    default void sendConsoleMessage(String msg) {
        CONSOLE.sendMessage(colorize(msg));
    }
    default void sendConsoleDidLoadFeature(String what, long started) {
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + what + " &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    default void sendConsoleDidLoadAsyncFeature(String what) {
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + what + " &e[async]");
    }
    default String formatBigDecimal(BigDecimal b) {
        return formatNumber(b, false);
    }
    default String formatNumber(Object number, boolean currency) {
        return (currency ? NumberFormat.getCurrencyInstance() : NumberFormat.getInstance()).format(number);
    }
    default BigDecimal valueOfBigDecimal(@NotNull String input) {
        final long m = input.endsWith("k") ? 1000 : input.endsWith("m") ? 1000000 : input.endsWith("b") ? 1000000000 : 1;
        return BigDecimal.valueOf(getRemainingDouble(input)*m);
    }
    default BigDecimal getBigDecimal(String value) {
        return BigDecimal.valueOf(Double.parseDouble(value));
    }
    default BigDecimal getRandomBigDecimal(BigDecimal min, BigDecimal max) {
        final BigDecimal range = max.subtract(min);
        return min.add(range.multiply(new BigDecimal(Math.random())));
    }
    default String formatDouble(double d) {
        String decimals = Double.toString(d).split("\\.")[1];
        if(decimals.equals("0")) { decimals = ""; } else { decimals = "." + decimals; }
        return formatInt((int) d) + decimals;
    }
    default String formatLong(long l) {
        final String f = Long.toString(l);
        final boolean c = f.contains(".");
        String decimals = c ? f.split("\\.")[1] : f;
        decimals = c ? decimals.equals("0") ? "" : "." + decimals : "";
        return formatInt((int) l) + decimals;
    }
    default String formatInt(int integer) {
        return String.format("%,d", integer);
    }
    default int getIntegerFromString(String input, int minimum) {
        final boolean hasHyphen = input.contains("-");
        final String[] values = input.split("-");
        final int min = hasHyphen ? Integer.parseInt(values[0]) : minimum;
        return hasHyphen ? min+RANDOM.nextInt(Integer.parseInt(values[1])-min+1) : Integer.parseInt(input);
    }
    default int parseInt(String input) {
        input = input.toLowerCase();
        return input.equals("random") ? RANDOM.nextInt(101) : Integer.parseInt(input);
    }
    default int getRemainingInt(String string) {
        string = ChatColor.stripColor(colorize(string)).replaceAll("\\p{L}", "").replaceAll("\\s", "").replaceAll("\\p{P}", "").replaceAll("\\p{S}", "");
        return string.isEmpty() ? -1 : Integer.parseInt(string);
    }
    default Double getRemainingDouble(String string) {
        string = ChatColor.stripColor(colorize(string).replaceAll("\\p{L}", "").replaceAll("\\p{Z}", "").replaceAll("\\.", "d").replaceAll("\\p{P}", "").replaceAll("\\p{S}", "").replace("d", "."));
        return string.isEmpty() ? -1.00 : Double.parseDouble(string.contains(".") && string.split("\\.").length > 1 && string.split("\\.")[1].length() > 2 ? string.substring(0, string.split("\\.")[0].length() + 3) : string);
    }
    default long getDelay(String input) {
        input = input.toLowerCase();
        long l = 0;
        if(input.contains("d")) {
            final String[] values = input.split("d");
            l += getRemainingDouble(values[0])*1000*60*60*24;
            input = values.length > 1 ? values[1] : input;
        }
        if(input.contains("h")) {
            final String[] values = input.split("h");
            l += getRemainingDouble(values[0])*1000*60*60;
            input = values.length > 1 ? values[1] : input;
        }
        if(input.contains("m")) {
            final String[] values = input.split("m");
            l += getRemainingDouble(values[0])*1000*60;
            input = values.length > 1 ? values[1] : input;
        }
        if(input.contains("s")) {
            l += getRemainingDouble(input.split("s")[0])*1000;
        }
        return l;
    }

    default EquipmentSlot getRespectiveSlot(String material) {
        return material.contains("HELMET") || material.contains("SKULL") || material.contains("HEAD") ? EquipmentSlot.HEAD
                : material.contains("CHESTPLATE") || material.contains("ELYTRA") ? EquipmentSlot.CHEST
                : material.contains("LEGGINGS") ? EquipmentSlot.LEGS
                : material.contains("BOOTS") ? EquipmentSlot.FEET
                : null;
    }
    default double round(double input, int decimals) {
        // From http://www.baeldung.com/java-round-decimal-number
        if(decimals < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(input));
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    default String roundDoubleString(double input, int decimals) {
        final double roundedValue = round(input, decimals);
        return Double.toString(roundedValue);
    }

    default String center(String s, int size) {
        // Credit to "Sahil Mathoo" from StackOverFlow at https://stackoverflow.com/questions/8154366
        return center(s, size, ' ');
    }
    default String center(String string, int size, char pad) {
        if(string == null || size <= string.length()) {
            return string;
        }
        final StringBuilder buider = new StringBuilder(size);
        for(int i = 0; i < (size - string.length()) / 2; i++) {
            buider.append(pad);
        }
        buider.append(string);
        while(buider.length() < size) {
            buider.append(pad);
        }
        return buider.toString();
    }

    default List<String> colorizeListString(List<String> input) {
        final List<String> i = new ArrayList<>();
        if(input != null) {
            for(String s : input) {
                i.add(colorize(s));
            }
        }
        return i;
    }
    default String colorize(String input) {
        return input != null ? ChatColor.translateAlternateColorCodes('&', input) : "NULL";
    }
    default HashMap<String, String> getReplacements(@NotNull Object...values) {
        final HashMap<String, String> replacements = new HashMap<>();
        boolean isKey = false;
        int index = 0;
        for(Object obj : values) {
            if(isKey) {
                final Object key = values[index-1];
                if(key instanceof String) {
                    replacements.put((String) key, obj.toString());
                }
            }
            isKey = !isKey;
            index++;
        }
        return replacements;
    }
    default void sendStringListMessage(CommandSender sender, List<String> message, HashMap<String, String> replacements) {
        if(message != null && message.size() > 0 && !message.get(0).equals("")) {
            final boolean papi = RANDOM_PACKAGE.placeholderapi, isPlayer = sender instanceof Player;
            final Player player = isPlayer ? (Player) sender : null;
            for(String s : message) {
                if(replacements != null) {
                    for(String r : replacements.keySet()) {
                        final String replacement = replacements.get(r);
                        s = s.replace(r, replacement != null ? replacement : "null");
                    }
                }
                if(s != null) {
                    if(papi && isPlayer) {
                        s = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, s);
                    }
                    sender.sendMessage(colorize(s));
                }
            }
        }
    }

    default Entity getHitEntity(ProjectileHitEvent event) {
        if(EIGHT || NINE || TEN) {
            final List<Entity> n = event.getEntity().getNearbyEntities(0.1, 0.1, 0.1);
            return n.size() > 0 ? n.get(0) : null;
        } else {
            return event.getHitEntity();
        }
    }

    default boolean isArmorPiece(@NotNull Material material) {
        final String type = material.name();
        return type.endsWith("_HELMET") || type.endsWith("_CHESTPLATE") || type.endsWith("_LEGGINGS") || type.endsWith("_BOOTS");
    }

    default boolean isPassive(EntityType type) {
        if(type.isSpawnable()) {
            switch (type.name().toLowerCase()) {
                case "bat":
                case "cat":
                case "chicken":
                case "cod":
                case "cow":
                case "dolphin":
                case "donkey":
                case "fox":
                case "horse":
                case "player":
                case "llama":
                case "mule":
                case "mushroom_cow":
                case "ocelot":
                case "panda":
                case "parrot":
                case "pig":
                case "pufferfish":
                case "rabbit":
                case "salmon":
                case "sheep":
                case "squid":
                case "tropical_fish":
                case "turtle":
                case "villager":
                case "wandering_trader":
                case "zombie_horse": return true;
                default: return false;
            }
        } else {
            return false;
        }
    }
    default boolean isAggressive(EntityType type) {
        return !isPassive(type);
    }
    default boolean isNeutral(EntityType type) {
        if(type.isSpawnable() && !isPassive(type)) {
            switch (type.name()) {
                case "enderman":
                case "iron_golem":
                case "polar_bear":
                case "wolf":
                    return true;
            }
        }
        return false;
    }

    default PotionEffectType getPotionEffectType(String input) {
        if(input != null && !input.isEmpty()) {
            switch (input.toUpperCase()) {
                case "STRENGTH": return PotionEffectType.INCREASE_DAMAGE;
                case "MINING_FATIGUE": return PotionEffectType.SLOW_DIGGING;
                case "SLOWNESS": return PotionEffectType.SLOW;
                case "HASTE": return PotionEffectType.FAST_DIGGING;
                case "JUMP": return PotionEffectType.JUMP;
                case "INSTANT_HEAL":
                case "INSTANT_HEALTH": return PotionEffectType.HEAL;
                case "INSTANT_HARM":
                case "INSTANT_DAMAGE": return PotionEffectType.HARM;
                default:
                    for(PotionEffectType p : PotionEffectType.values()) {
                        if(p != null && input.equalsIgnoreCase(p.getName())) {
                            return p;
                        }
                    }
                    return null;
            }
        } else return null;
    }

    default String toString(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }
    default Location toLocation(String string) {
        if(string != null && string.contains(";")) {
            final String[] a = string.split(";");
            return new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]), Float.parseFloat(a[4]), Float.parseFloat(a[5]));
        } else {
            return null;
        }
    }

    default void giveItem(Player player, ItemStack is) {
        if(is == null || is.getType().equals(Material.AIR)) {
            return;
        }
        final UMaterial m = UMaterial.match(is);
        final ItemMeta meta = is.getItemMeta();
        final PlayerInventory inv = player.getInventory();
        final int firstMaterialSlot = inv.first(is.getType()), firstEmptySlot = inv.firstEmpty(), max = is.getMaxStackSize();
        int amountLeft = is.getAmount();

        if(firstMaterialSlot != -1) {
            for(int i = 0; i < inv.getSize(); i++) {
                final ItemStack targetItemStack = inv.getItem(i);
                if(amountLeft > 0 && targetItemStack != null && targetItemStack.getItemMeta().equals(meta) && UMaterial.match(targetItemStack) == m) {
                    final int amount = targetItemStack.getAmount(), toMax = max-amount, given = Math.min(amountLeft, toMax);
                    if(given > 0) {
                        targetItemStack.setAmount(amount+given);
                        amountLeft -= given;
                    }
                }
            }
            player.updateInventory();
        }
        if(amountLeft > 0) {
            is.setAmount(amountLeft);
            if(firstEmptySlot >= 0) {
                inv.addItem(is);
                player.updateInventory();
            } else {
                player.getWorld().dropItem(player.getLocation(), is);
            }
        }
    }

    default BlockFace getFacing(Entity entity) {
        return LEGACY || THIRTEEN ? BLOCK_FACES[Math.round(entity.getLocation().getYaw() / 45f) & 0x7] : entity.getFacing();
    }
    default String toReadableDate(Date d, String format) {
        return new SimpleDateFormat(format).format(d);
    }
    default Entity getEntity(UUID uuid) {
        if(uuid != null) {
            if(EIGHT || NINE || TEN) {
                for(World w : Bukkit.getWorlds()) {
                    for(LivingEntity le : w.getLivingEntities()) {
                        if(uuid.equals(le.getUniqueId())) {
                            return le;
                        }
                    }
                }
            } else {
                return Bukkit.getEntity(uuid);
            }
        }
        return null;
    }
    default LivingEntity getEntity(String type, Location l, boolean spawn) {
        final boolean baby = type.contains(":") && type.toLowerCase().endsWith(":true");
        type = type.toUpperCase().split(":")[0];
        final LivingEntity mob = getEntity(type, l);
        if(mob instanceof Zombie) {
            ((Zombie) mob).setBaby(baby);
        }
        if(!spawn) {
            mob.remove();
        } else if(mob instanceof Ageable && baby) {
            final Ageable a = (Ageable) mob;
            a.setBaby();
            a.setAgeLock(true);
        }
        return mob;
    }

    default Color getColor(String path) {
        if(path == null) {
            return null;
        }
        switch (path.toLowerCase()) {
            case "aqua": return Color.AQUA;
            case "black": return Color.BLACK;
            case "blue": return Color.BLUE;
            case "fuchsia": return Color.FUCHSIA;
            case "gray": return Color.GRAY;
            case "green": return Color.GREEN;
            case "lime": return Color.LIME;
            case "maroon": return Color.MAROON;
            case "navy": return Color.NAVY;
            case "olive": return Color.OLIVE;
            case "orange": return Color.ORANGE;
            case "purple": return Color.PURPLE;
            case "red": return Color.RED;
            case "silver": return Color.SILVER;
            case "teal": return Color.TEAL;
            case "white": return Color.WHITE;
            case "yellow": return Color.YELLOW;
            default: return null;
        }
    }
    default LivingEntity getEntity(String type, Location location) {
        final World world = location.getWorld();
        final LivingEntity le;
        switch (type.toUpperCase()) {
            case "BAT": return world.spawn(location, Bat.class);
            case "BLAZE": return world.spawn(location, Blaze.class);
            case "CAVE_SPIDER": return world.spawn(location, CaveSpider.class);
            case "CHICKEN": return world.spawn(location, Chicken.class);
            case "COW": return world.spawn(location, Cow.class);
            case "CREEPER": return world.spawn(location, Creeper.class);
            case "ENDER_DRAGON": return world.spawn(location, EnderDragon.class);
            case "ENDERMAN": return world.spawn(location, Enderman.class);
            case "GHAST": return world.spawn(location, Ghast.class);
            case "GIANT": return world.spawn(location, Giant.class);
            case "GUARDIAN": return world.spawn(location, Guardian.class);
            case "HORSE": return world.spawn(location, Horse.class);
            case "IRON_GOLEM": return world.spawn(location, IronGolem.class);
            case "LLAMA": return EIGHT || NINE || TEN ? null : world.spawn(location, Llama.class);
            case "MAGMA_CUBE": return world.spawn(location, MagmaCube.class);
            case "MUSHROOM_COW": return world.spawn(location, MushroomCow.class);
            case "OCELOT": return world.spawn(location, Ocelot.class);
            case "PARROT": return EIGHT || NINE || TEN || ELEVEN ? null : world.spawn(location, Parrot.class);
            case "PIG": return world.spawn(location, Pig.class);
            case "PIG_ZOMBIE": return world.spawn(location, PigZombie.class);
            case "RABBIT": return world.spawn(location, Rabbit.class);
            case "SHEEP": return world.spawn(location, Sheep.class);
            case "SHULKER": return EIGHT ? null : world.spawn(location, Shulker.class);
            case "SILVERFISH": return world.spawn(location, Silverfish.class);
            case "SKELETON": return world.spawn(location, Skeleton.class);
            case "SLIME": return world.spawn(location, Slime.class);
            case "SNOWMAN": return world.spawn(location, Snowman.class);
            case "SQUID": return world.spawn(location, Squid.class);
            case "SPIDER": return world.spawn(location, Spider.class);
            case "STRAY": return EIGHT || NINE ? null : world.spawn(location, Stray.class);
            case "VEX": return EIGHT || NINE || TEN ? null : world.spawn(location, Vex.class);
            case "VILLAGER": return world.spawn(location, Villager.class);
            case "VINDICATOR": return EIGHT || NINE || TEN ? null : world.spawn(location, Vindicator.class);
            case "WITHER_SKELETON":
                if(EIGHT || NINE || TEN) {
                    le = world.spawn(location, Skeleton.class);
                    ((Skeleton) le).setSkeletonType(Skeleton.SkeletonType.WITHER);
                    return le;
                } else {
                    return world.spawn(location, WitherSkeleton.class);
                }
            case "ZOMBIE": return world.spawn(location, Zombie.class);
            case "ZOMBIE_HORSE": return EIGHT ? null : world.spawn(location, ZombieHorse.class);
            case "ZOMBIE_VILLAGER": return EIGHT ? null : world.spawn(location, ZombieVillager.class);
            default: return null;
        }
    }
    default boolean isInteractable(Material material) {
        final String m = material.name();
        if(!LEGACY) {
            return material.isInteractable();
        } else {
            return INTERACTABLE_MATERIALS.contains(m) || m.contains("ANVIL") || m.endsWith("_BED")
                    || m.endsWith("DOOR") && !m.equals("IRON_DOOR")
                    ;
        }
    }

    default void didApply(InventoryClickEvent event, Player player, ItemStack current, ItemStack cursor) {
        event.setCancelled(true);
        final int a = cursor.getAmount();
        if(a == 1) event.setCursor(new ItemStack(Material.AIR));
        else {
            cursor.setAmount(a-1);
            event.setCursor(cursor);
        }
        player.updateInventory();
    }

    default void spawnFirework(Firework firework, Location loc) {
        if(firework != null) {
            final Firework fw = loc.getWorld().spawn(new Location(loc.getWorld(), loc.getX()+0.5, loc.getY(), loc.getZ()+0.5), Firework.class);
            fw.setFireworkMeta(firework.getFireworkMeta());
        }
    }
    default Firework createFirework(FireworkEffect.Type explosionType, Color trailColor, Color explodeColor, int power) {
        final World w = Bukkit.getWorlds().get(0);
        final Firework fw = w.spawn(w.getSpawnLocation(), Firework.class);
        final FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        fwm.addEffect(FireworkEffect.builder().trail(true).flicker(true).with(explosionType).withColor(trailColor).withFade(explodeColor).withFlicker().withTrail().build());
        fw.setFireworkMeta(fwm);
        return fw;
    }

    default String toMaterial(String input, boolean realitem) {
        if(input.contains(":")) input = input.split(":")[0];
        if(input.contains(" ")) input = input.replace(" ", "");
        if(input.contains("_")) input = input.replace("_", " ");
        String e = "";
        if(input.contains(" ")) {
            final String[] spaces = input.split(" ");
            final int l = spaces.length;
            for(int i = 0; i < l; i++) {
                e = e + spaces[i].substring(0, 1).toUpperCase() + spaces[i].substring(1).toLowerCase() + (i != l-1 ? (realitem ? "_" : " ") : "");
            }
        } else {
            e = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
        }
        return e;
    }
    default long parseTime(String fromString) {
        long time = 0;
        if(fromString != null) {
            fromString = ChatColor.stripColor(fromString);
            final boolean hasDays = fromString.contains("d"), hasHrs = fromString.contains("h"), hasMins = fromString.contains("m"), hasSecs = fromString.contains("s");
            if(hasDays) {
                final String[] values = fromString.split("d");
                time += getRemainingDouble(values[0])*24*60*60;
                if(hasHrs || hasMins || hasSecs) {
                    fromString = values[1];
                }
            }
            if(hasHrs) {
                final String[] values = fromString.split("h");
                time += getRemainingDouble(values[0])*60*60;
                if(hasMins || hasSecs) {
                    fromString = values[1];
                }
            }
            if(hasMins) {
                final String[] values = fromString.split("m");
                time += getRemainingDouble(values[0])*60;
                if(hasSecs) {
                    fromString = values[1];
                }
            }
            if(hasSecs) {
                final String[] values = fromString.split("s");
                time += getRemainingDouble(values[0]);
                //fromString = fromString.split("s")[0];
            }
        }
        return time*1000;
    }
    default Enchantment getEnchantment(String string) {
        if(string != null) {
            string = string.toLowerCase().replace("_", "");
            for(Enchantment enchant : Enchantment.values()) {
                final String name = enchant != null ? enchant.getName() : null;
                if(name != null && string.startsWith(name.toLowerCase().replace("_", ""))) {
                    return enchant;
                }
            }
            switch (string.substring(0, 3)) {
                case "pow": return Enchantment.ARROW_DAMAGE;
                case "fla": return Enchantment.ARROW_FIRE;
                case "inf": return Enchantment.ARROW_INFINITE;
                case "pun": return Enchantment.ARROW_KNOCKBACK;
                case "bin": return !EIGHT && !NINE && !TEN ? Enchantment.getByName("BINDING_CURSE") : null;
                case "sha": return Enchantment.DAMAGE_ALL;
                case "ban": return Enchantment.DAMAGE_ARTHROPODS;
                case "smi": return Enchantment.DAMAGE_UNDEAD;
                case "dep": return Enchantment.DEPTH_STRIDER;
                case "eff": return Enchantment.DIG_SPEED;
                case "unb": return Enchantment.DURABILITY;
                case "fir": return string.startsWith("firep") ? Enchantment.PROTECTION_FIRE : Enchantment.FIRE_ASPECT;
                case "fro": return !EIGHT ? Enchantment.getByName("FROST_WALKER") : null;
                case "kno": return Enchantment.KNOCKBACK;
                case "for": return Enchantment.LOOT_BONUS_BLOCKS;
                case "loo": return Enchantment.LOOT_BONUS_MOBS;
                case "luc": return Enchantment.LUCK;
                case "lur": return Enchantment.LURE;
                case "men": return !EIGHT ? Enchantment.getByName("MENDING") : null;
                case "res": return Enchantment.OXYGEN;
                case "pro": return string.startsWith("proj") ? Enchantment.PROTECTION_PROJECTILE : Enchantment.PROTECTION_ENVIRONMENTAL;
                case "bla": return Enchantment.PROTECTION_EXPLOSIONS;
                case "fea": return Enchantment.PROTECTION_FALL;
                case "sil": return Enchantment.SILK_TOUCH;
                case "tho": return Enchantment.THORNS;
                case "van": return !EIGHT && !NINE && !TEN ? Enchantment.getByName("VANISHING_CURSE") : null;
                case "aqu": return Enchantment.WATER_WORKER;
                default: return null;
            }
        }
        return null;
    }

    default int indexOf(Set<?> collection, Object value) {
        int i = 0;
        for(Object o : collection) {
            if(value.equals(o)) return i;
            i++;
        }
        return -1;
    }

    default void removeItem(Player player, ItemStack itemstack, int amount) {
        final PlayerInventory inv = player.getInventory();
        int nextslot = getNextSlot(player, itemstack);
        for(int i = 1; i <= amount; i++) {
            if(nextslot >= 0) {
                final ItemStack is = inv.getItem(nextslot);
                final int a = is.getAmount();
                if(a == 1) {
                    inv.setItem(nextslot, new ItemStack(Material.AIR));
                    nextslot = getNextSlot(player, itemstack);
                } else {
                    is.setAmount(a-1);
                }
            }
        }
        player.updateInventory();
    }
    default boolean isSimilar(ItemStack is, ItemStack target) {
        return isSimilar(is, target, false);
    }
    default boolean isSimilar(ItemStack is, ItemStack target, boolean matchAbsoluteMeta) {
        if(matchAbsoluteMeta) {
            return is.isSimilar(target);
        } else if(is != null && target != null && is.getType().equals(target.getType()) && is.hasItemMeta() == target.hasItemMeta()
                && (!LEGACY || is.getData().getData() == target.getData().getData())
                && is.getDurability() == target.getDurability()) {
            final ItemMeta m1 = is.getItemMeta(), m2 = target.getItemMeta();

            return m1 == m2
                    || m1 != null && m2 != null
                    && Objects.equals(m1.getLore(), m2.getLore())
                    && Objects.equals(m1.getDisplayName(), m2.getDisplayName())
                    && Objects.equals(m1.getEnchants(), m2.getEnchants())
                    && Objects.equals(m1.getItemFlags(), m2.getItemFlags())
                    && (EIGHT || m1.isUnbreakable() == m2.isUnbreakable());
        }
        return false;
    }
    default int getNextSlot(Player player, ItemStack itemstack) {
        final PlayerInventory inv = player.getInventory();
        for(int i = 0; i < inv.getSize(); i++) {
            final ItemStack item = inv.getItem(i);
            if(item != null && isSimilar(item, itemstack)) {
                return i;
            }
        }
        return -1;
    }
    default int getTotalAmount(Inventory inventory, UMaterial umat) {
        final ItemStack i = umat.getItemStack();
        int amount = 0;
        for(ItemStack is : inventory.getContents()) {
            if(is != null && is.isSimilar(i)) {
                amount += is.getAmount();
            }
        }
        return amount;
    }

    default ItemStack getSpawner(String input) {
        String pi = input.toLowerCase(), type = null;
        if(pi.equals("mysterymobspawner")) {
            return GivedpItem.INSTANCE.valueOfRPItem("mysterymobspawner").clone();
        } else if(RandomPackage.SPAWNER_PLUGIN != null) {
            for(EntityType entitytype : EntityType.values()) {
                final String s = entitytype.name().toLowerCase().replace("_", "");
                if(pi.startsWith(s + "spawner")) {
                    type = s;
                }
            }
            if(type == null) {
                if(pi.contains("pig") && pi.contains("zombie")) type = "pigzombie";
            }
            if(type == null) return null;
            final ItemStack is = SpawnerAPI.INSTANCE.getItem(type);
            if(is != null) {
                return is;
            } else {
                CONSOLE.sendMessage("[RandomPackage] SilkSpawners or EpicSpawners is required to use this feature!");
            }
        }
        return null;
    }

    default void playParticle(FileConfiguration config, String path, Location location, int count) {
        if(config != null && config.get(path) != null) {
            final String target = config.getString(path);
            final UParticle up = UParticle.matchParticle(target.toUpperCase());
            if(up != null) {
                up.play(location, count);
            }
        }
    }
    default void playSound(FileConfiguration config, String path, Player player, Location location, boolean globalsound) {
        if(config != null && config.get(path) != null) {
            final String[] p = config.getString(path).split(":");
            final String s = p[0].toUpperCase();
            final int v = Integer.parseInt(p[1]), pp = Integer.parseInt(p[2]);
            try {
                final Sound sound = USound.matchSound(s);
                if(sound != null) {
                    if(player != null) {
                        if(!globalsound) {
                            player.playSound(location, sound, v, pp);
                        } else {
                            player.getWorld().playSound(location, sound, v, pp);
                        }
                    } else {
                        location.getWorld().playSound(location, sound, v, pp);
                    }
                } else {
                    sendConsoleMessage("&6[RandomPackage] &cERROR! Invalid sound name: &f" + s + "&c! Try using the actual sound name for your Server version! (" + VERSION + ")");
                }
            } catch (Exception e) {
                sendConsoleMessage("&6[RandomPackage] &cERROR! Invalid sound name: &f" + s + "&c! Try using the actual sound name for your Server version! (" + VERSION + ")");
            }
        }
    }

    default List<Location> getChunkLocations(Chunk chunk) {
        final List<Location> l = new ArrayList<>();
        final int x = chunk.getX()*16, z = chunk.getZ()*16;
        final World world = chunk.getWorld();
        for(int xx = x; xx < x+16; xx++) {
            for(int zz = z; zz < z+16; zz++) {
                l.add(new Location(world, xx, 0, zz));
            }
        }
        return l;
    }
    default ItemStack getItemInHand(LivingEntity entity) {
        if(entity == null) {
            return null;
        } else {
            final EntityEquipment e = entity.getEquipment();
            return EIGHT ? e.getItemInHand() : e.getItemInMainHand();
        }
    }

    default void generateAllDefault(String folder, String...values) {
        for(String s : values) {
            save(folder, s + ".yml");
        }
    }
    default void generateDefaultBoosters() {
        generateAllDefault("boosters", "FACTION_MCMMO", "FACTION_XP");
    }
    default void generateDefaultCustomArmor() {
        generateAllDefault("custom armor", "DRAGON", "ENGINEER", "KOTH", "PHANTOM", "RANGER", "SUPREME", "TRAVELER", "YETI", "YIJKI");
    }
    default void generateDefaultConquests() {
        generateAllDefault("conquests", "NORMAL");
    }
    default void generateDefaultCustomBosses() {
        generateAllDefault("custom bosses", "BROOD_MOTHER", "KING_SLIME", "PLAGUE_BLOATER", "SOUL_REAPER");
    }
    default void generateDefaultCustomEnchants() {
        final String[] mastery = new String[] {
                "_settings",
                "AUTO_SELL",
                "CHAIN_LIFESTEAL",
                "DEATH_PACT",
                "EXPLOSIVES_EXPERT",
                "FEIGN_DEATH",
                "HORRIFY",
                "LAVA_STRIDER",
                "MARK_OF_THE_BEAST",
                "NEUTRALIZE",
                "PERMAFROST", "POLTERGEIST",
                "TOMBSTONE",
                "WEB_WALKER"
        };
        final String[] heroic = new String[] {
                "_settings",
                "ALIEN_IMPLANTS", "ATOMIC_DETONATE",
                "BIDIRECTIONAL_TELEPORTATION",
                "DEEP_BLEED", "DEMONIC_LIFESTEAL", "DIVINE_ENLIGHTED",
                "ETHEREAL_DODGE",
                "GHOSTLY_GHOST", "GODLY_OVERLOAD", "GUIDED_ROCKET_ESCAPE",
                "HEROIC_ENCHANT_REFLECT",
                "INFINITE_LUCK",
                "LETHAL_SNIPER",
                "MASTER_BLACKSMITH", "MASTER_INQUISITIVE", "MIGHTY_CACTUS", "MIGHTY_CLEAVE",
                "PLANETARY_DEATHBRINGER", "POLYMORPHIC_METAPHYSICAL",
                "REFLECTIVE_BLOCK",
                "SHADOW_ASSASSIN",
                "TITAN_TRAP",
                "VENGEFUL_DIMINISH",
        };
        final String[] soul = new String[] {
                "_settings",
                "DIVINE_IMMOLATION",
                "HERO_KILLER",
                "IMMORTAL",
                "NATURES_WRATH",
                "PARADOX", "PHOENIX",
                "SABOTAGE", "SOUL_TRAP",
                "TELEBLOCK",
        };
        final String[] legendary = new String[] {
                "_settings",
                "AEGIS", "ANTI_GANK", "ARMORED",
                "BARBARIAN", "BLACKSMITH", "BLOOD_LINK", "BLOOD_LUST", "BOSS_SLAYER",
                "CLARITY",
                "DEATH_GOD", "DEATHBRINGER", "DESTRUCTION", "DEVOUR", "DIMINISH", "DISARMOR", "DOUBLE_STRIKE", "DRUNK",
                "ENCHANT_REFLECT", "ENLIGHTED",
                "GEARS",
                "HEX",
                "INQUISITIVE", "INSANITY", "INVERSION",
                "KILL_AURA",
                "LEADERSHIP", "LIFESTEAL",
                "OVERLOAD",
                "PROTECTION",
                "RAGE",
                "SILENCE", "SNIPER",
        };
        final String[] ultimate = new String[] {
                "_settings",
                "ANGELIC", "ARROW_BREAK", "ARROW_DEFLECT", "ARROW_LIFESTEAL", "ASSASSIN", "AVENGING_ANGEL",
                "BLEED", "BLESSED", "BLOCK",
                "CLEAVE", "CORRUPT", "CREEPER_ARMOR",
                "DETONATE", "DIMENSION_RIFT", "DISINTEGRATE", "DODGE", "DOMINATE",
                "EAGLE_EYE", "ENDER_WALKER", "ENRAGE",
                "FUSE",
                "GHOST", "GUARDIANS",
                "HEAVY", "HELLFIRE",
                "ICEASPECT", "IMPLANTS",
                "OBSIDIANSHIELD",
                "LONGBOW", "LUCKY",
                "MARKSMAN", "METAPHYSICAL",
                "PACIFY", "PIERCING",
                "SPIRITS", "STICKY",
                "TANK",
                "UNFOCUS",
                "VALOR",
        };
        final String[] elite = new String[] {
                "_settings",
                "ANTI_GRAVITY",
                "BLIND",
                "CACTUS",
                "DEMONFORGED",
                "EXECUTE",
                "FARCAST", "FROZEN",
                "GREATSWORD",
                "HARDENED", "HIJACK",
                "ICE_FREEZE", "INFERNAL",
                "PARALYZE", "POISON", "POISONED", "PUMMEL",
                "REFORGED", "REPAIR_GUARD", "RESILIENCE", "ROCKET_ESCAPE",
                "SHACKLE", "SHOCKWAVE", "SMOKE_BOMB", "SNARE", "SOLITUDE", "SPIRIT_LINK", "SPRINGS", "STORMCALLER",
                "TELEPORTATION", "TRAP", "TRICKSTER",
                "UNDEAD_RUSE",
                "VAMPIRE", "VENOM", "VOODOO",
                "WITHER",
        };
        final String[] unique = new String[] {
                "_settings",
                "BERSERK",
                "COMMANDER", "COWIFICATION", "CURSE",
                "DEEP_WOUNDS",
                "ENDER_SHIFT", "EXPLOSIVE",
                "FEATHERWEIGHT",
                "LIFEBLOOM",
                "MOLTEN",
                "NIMBLE", "NUTRITION",
                "OBSIDIAN_DESTROYER",
                "PLAGUE_CARRIER",
                "RAGDOLL", "RAVENOUS",
                "SELF_DESTRUCT", "SKILL_SWIPE", "SKILLING",
                "TELEPATHY", "TRAINING",
                "VIRUS",
        };
        final String[] simple = new String[] {
                "_settings",
                "AQUATIC", "AUTO_SMELT",
                "CONFUSION",
                "DECAPITATION",
                "EPICNESS", "EXPERIENCE",
                "OXYGENATE",
                "GLOWING",
                "HASTE", "HEADLESS", "HEALING",
                "INSOMNIA",
                "LIGHTNING",
                "OBLITERATE",
                "TARGET_TRACKING", "THUNDERING_BLOW",
        };
        generateAllDefault("custom enchants" + SEPARATOR + "MASTERY", mastery);
        generateAllDefault("custom enchants" + SEPARATOR + "HEROIC", heroic);
        generateAllDefault("custom enchants" + SEPARATOR + "SOUL", soul);
        generateAllDefault("custom enchants" + SEPARATOR + "LEGENDARY", legendary);
        generateAllDefault("custom enchants" + SEPARATOR + "ULTIMATE", ultimate);
        generateAllDefault("custom enchants" + SEPARATOR + "ELITE", elite);
        generateAllDefault("custom enchants" + SEPARATOR + "UNIQUE", unique);
        generateAllDefault("custom enchants" + SEPARATOR + "SIMPLE", simple);
        generateAllDefault("custom enchants" + SEPARATOR + "RANDOM", "_settings");
    }
    default void generateDefaultCustomCreepers() {
        generateAllDefault("custom creepers", "ARCANE", "GIGANTIC", "LUCKY", "STUN", "TACTICAL");
    }
    default void generateDefaultCustomTNT() {
        generateAllDefault("custom tnt", "GIGANTIC", "LETHAL", "LUCKY", "MIMIC", "TACTICAL");
    }
    default void generateDefaultDuelArenas() {
        generateAllDefault("duel arenas", "DEMON", "DRAGON", "FORGOTTEN", "ICE", "JUNGLE", "MAGIC", "MONSTER", "PIRATE", "VOID");
    }
    default void generateDefaultEnvoyTiers() {
        generateAllDefault("envoy tiers", "ELITE", "LEGENDARY", "SIMPLE", "ULTIMATE", "UNIQUE");
    }
    default void generateDefaultFactionQuests() {
        generateAllDefault("faction quests",
                "CONQUEST_BREAKER_I",
                "DAILY_CHALLENGE_MASTER_I",
                "DUNGEON_MASTER_I",
                "DUNGEON_PORTALS_I",
                "DUNGEON_RUNNER_I", "DUNGEON_RUNNER_II",
                "HOLD_COSMONAUT_OUTPOST_I",
                "HOLD_HERO_OUTPOST_I",
                "HOLD_TRAINEE_OUTPOST_I",
                "IRON_KOTH_MERCHANT_I",
                "KILL_BLAZE_I",
                "KILL_BOSS_BROOD_MOTHER",
                "KILL_BOSS_KING_SLIME",
                "KILL_BOSS_PLAGUE_BLOATER",
                "KILL_BOSS_UNDEAD_ASSASSIN",
                "KILL_CONQUEST_BOSSES_I",
                "KOTH_CAPTURER_I",
                "LEGENDARY_ENCHANTER_I",
                "LMS_DEFENDER_I",
                "TOP_DOG",
                "ULTIMATE_ENCHANTER_I"
        );
    }
    default void generateDefaultFactionUpgrades() {
        generateAllDefault("faction upgrades",
                "BOSS_MASTERY",
                "CONQUEST_MASTERY",
                "DUNGEON_LOOTER", "DUNGEON_MASTER", "DUNGEON_RUNNER",
                "ENDER_FARMING", "ENHANCED_FLIGHT", "ESCAPE_ARTIST", "EXPLOSIVES_EXPERT",
                "FACTION_POWER_BOOST", "FAST_ENDERPEARL",
                "HEROIC_BOSS_MASTERY", "HEROIC_SOUL_MASTERY", "HEROIC_WELL_FED",
                "HOME_ADVANTAGE",
                "KIT_EVOLUTION",
                "MAVERICK", "MAX_FACTION_SIZE", "MCMMO_MASTERY", "MONSTER_FARM",
                "NATURAL_GROWTH",
                "OUTPOST_CONTROL",
                "SOUL_MASTERY",
                "TP_MASTERY",
                "WARP_MASTER", "WARZONE_CONTROL", "WELL_FED",
                "XP_HARVEST"
        );
    }
    default void generateDefaultFatBuckets() {
        generateAllDefault("fat buckets", "LAVA");
    }
    default void generateDefaultGlobalChallenges() {
        generateAllDefault("global challenges",
                "AGGRESSIVE_MOBS_KILLED", "ALCHEMIST_EXCHANGES", "ALL_ORES_MINED",
                "BIRCH_LOGS_CUT", "BLOCKS_MINED_BY_PICKAXE", "BLOCKS_PLACED",
                "COINFLIPS_WON", "CUSTOM_ENCHANTS_REVEALED",
                "DIAMOND_ORE_MINED",
                "EMERALD_ORE_MINED", "END_MOBS_KILLED", "ENVOY_CHESTS_LOOTED", "EXP_GAINED",
                "FISH_CAUGHT",
                "GOLD_ORE_MINED",
                "JACKPOT_MONEY_SPENT", "JACKPOT_TICKETS_BOUGHT",
                "LAPIS_ORE_MINED",
                "MCMMO_XP_GAINED_IN_ACROBATICS", "MCMMO_XP_GAINED_IN_SWORDS", "MCMMO_XP_GAINED_IN_UNARMED",
                "MOBS_KILLED",
                "MONEY_LOST_IN_COINFLIPS", "MONEY_WON_IN_COINFLIPS",
                "PASSIVE_MOBS_KILLED", "PVP_DAMAGE",
                "RANKED_DUEL_WINS", "REDSTONE_ORE_MINED",
                "TIME_SPENT_IN_END", "TIME_SPENT_IN_MAIN_WARZONE",
                "UNIQUE_PLAYER_HEADS_COLLECTED", "UNIQUE_PLAYER_KILLS"
        );
    }
    default void generateDefaultFilterCategories() {
        generateAllDefault("filter categories", "EQUIPMENT", "FOOD", "ORES", "OTHER", "POTION_SUPPLIES", "RAIDING", "SPECIALTY");
    }
    default void generateDefaultInventoryPets() {
        generateAllDefault("inventory pets",
                "ABOMINABLE_SNOWMAN",
                "ALCHEMIST",
                "ANTI_TELEBLOCK",
                "BANNER",
                "BLACKSCROLL",
                "BLESS",
                "ENCHANTER",
                "EVOLUTION",
                "EXPLODING_TURKEY",
                "FEIGN_DEATH",
                "GAIA",
                "LAVA_ELEMENTAL",
                "RAID_CREEPER",
                "SMITE",
                "STRONGHOLD_SELL",
                "TESLA",
                "VILE_CREEPER",
                "WATER_ELEMENTAL",
                "XP_BOOSTER"
        );
    }
    default void generateDefaultItemSkins() {
        generateAllDefault("item skins",
                "DEATH_KNIGHT_SKULL_BLADE",
                "FLAMING_HALO",
                "JOLLY_CANDY_SWORD",
                "KOALA",
                "MEAT_CLEAVER_AXE",
                "REINDEER_ANTLERS",
                "SANTA_HAT"
        );
    }
    default void generateDefaultLootboxes() {
        generateAllDefault("lootboxes",
                "BAKED",
                "BOX_OF_CHOCOLATES",
                "DETENTION",
                "ICY_ADVENTURES",
                "LUCKY",
                "PET_COLLECTOR",
                "RAINBOW",
                "SNOW_DAY",
                "SUGAR_DADDY",
                "SURVIVAL_KIT"
        );
    }
    default void generateDefaultMasks() {
        generateAllDefault("masks",
                "BUNNY", "DEATH_KNIGHT", "DRAGON", "DUNGEON", "GHOST", "GLITCH", "HEADLESS", "JOKER",
                "LOVER", "MONOPOLY", "NECROMANCER", "PARTY_HAT", "PILGRIM", "PUMPKIN_MONSTER",
                "PURGE", "REINDEER", "RIFT", "SANTA", "SCARECROW", "SPECTRAL", "TURKEY", "ZEUS"
        );
    }
    default void generateDefaultMonthlyCrates() {
        generateAllDefault("monthly crates",
                "APRIL_2016", "APRIL_2017", "APRIL_2018",
                "AUGUST_2016", "AUGUST_2017", "AUGUST_2018",
                "BLACK_FRIDAY_2016",
                "DECEMBER_2015", "DECEMBER_2016", "DECEMBER_2017", "DECEMBER_2018",
                "FEBRUARY_2016", "FEBRUARY_2018",
                "HALLOWEEN_2016", "HALLOWEEN_2017", "HALLOWEEN_2018",
                "HOLIDAY_2016", "HOLIDAY_2017",
                "JANUARY_2016", "JANUARY_2017", "JANUARY_2018",
                "JULY_2016", "JULY_2017", "JULY_2018",
                "JUNE_2017", "JUNE_2018",
                "MARCH_2016", "MARCH_2017", "MARCH_2018",
                "MAY_2017", "MAY_2018",
                "NOVEMBER_2016", "NOVEMBER_2017", "NOVEMBER_2018",
                "OCTOBER_2016", "OCTOBER_2017", "OCTOBER_2018",
                "SCHOOL_2016", "SCHOOL_2017",
                "SEPTEMBER_2017", "SEPTEMBER_2018",
                "THANKSGIVING_2017",
                "VALENTINES_2017", "VALENTINES_2018"
        );
    }
    default void generateDefaultOutposts() {
        generateAllDefault("outposts", "HERO", "SERVONAUT", "TRAINEE", "VANILLA");
    }
    default void generateDefaultPlayerQuests() {
        generateAllDefault("player quests",
                "A_LITTLE_GRIND", "A_MEDIUM_GRIND", "A_BIG_GRIND",
                "BEGINNERS_LUCK",
                "BIGGER_SPENDER", "BIGGEST_SPENDER",
                "DEFINITELY_AFK",
                "DISGUISED",
                "DUNGEON_NOOB",
                "DUNGEON_RUNNER",
                "ELITE_ENCHANTER",
                "ENDER_LORD",
                "ENVOY_LOOTER_II",
                "ENVOY_SUMMONER_III",
                "EQUIPMENT_LOOTER",
                "GAMBLER_I", "GAMBLER_II", "GAMBLER_III",
                "HANGING_ON",
                "HERO_DOMINATOR",
                "HEROIC_ENCHANTER",
                "HEROIC_ENVOY_LOOTER_II",
                "ITEM_CUSTOMIZATION",
                "KOTH_KILLER_II",
                "LAST_NOOB_STANDING", "LAST_MASTER_STANDING",
                "LEGENDARY_LOOTER",
                "MASTER_KIT_LEVELING",
                "MASTER_MINER",
                "MOB_EXAMINER_II",
                "NOVICE_ALCHEMIST",
                "NOVICE_EXCAVATOR",
                "NOVICE_MERCHANT", "SKILLED_MERCHANT",
                "NOVICE_MINER",
                "NOVICE_TINKERER",
                "OUTPOST_DEFENDER",
                "QUEST_MASTER",
                "RANDOMIZER_II", "RANDOMIZER_III",
                "RIGGED",
                "SIMPLE_ENCHANTER",
                "SIMPLE_LOOTER",
                "SKILL_BOOSTER_I", "SKILL_BOOSTER_III",
                "SLAUGHTER_HOUSE_I", "SLAUGHTER_HOUSE_II", "SLAUGHTER_HOUSE_III",
                "SOUL_COLLECTOR_I", "SOUL_ENCHANTER",
                "SPIDER_SLAYER",
                "STRONGHOLD_LOOTER_I",
                "THIRSTY",
                "ULTIMATE_ENCHANTER",
                "ULTIMATE_LOOTER",
                "UNIQUE_ENCHANTER",
                "VERY_UNLUCKY",
                "XP_BOOSTED_I"
        );
    }
    default void generateDefaultServerCrates() {
        generateAllDefault("server crates", "ELITE", "GODLY", "LEGENDARY", "SIMPLE", "ULTIMATE", "UNIQUE");
    }
    default void generateDefaultShopCategories() {
        generateAllDefault("shops",
                "BASE_GRIND", "BREWING", "BUILDING_BLOCKS",
                "CLAY",
                "FLOWERS", "FOOD_AND_FARMING",
                "GLASS",
                "MENU", "MOB_DROPS",
                "ORES_AND_GEMS",
                "POTIONS",
                "RAID",
                "SPAWNERS", "SPECIALTY",
                "WOOL"
        );
    }
    default void generateDefaultStrongholds() {
        generateAllDefault("strongholds", "FROZEN", "INFERNAL");
    }
    default void generateDefaultTitanAttributes() {
        generateAllDefault("titan attributes", "ATLAS", "KRONOS", "OURANOS");
    }
    default void generateDefaultTrinkets() {
        generateAllDefault("trinkets", "ANTI_PROJECTILE_FORCEFIELD", "BATTLESTAFF_OF_YIJKI", "EMP_PULSE", "FACTION_BANNER", "PHOENIX_FEATHER", "SOUL_ANVIL", "SOUL_PEARL", "SPEED");
    }
}