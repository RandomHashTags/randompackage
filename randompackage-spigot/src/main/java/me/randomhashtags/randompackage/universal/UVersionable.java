package me.randomhashtags.randompackage.universal;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.Versionable;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
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

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public interface UVersionable extends Versionable {
    HashMap<Feature, LinkedHashMap<String, Identifiable>> FEATURES = new HashMap<>();
    File DATA_FOLDER = getPlugin.getDataFolder();
    String SEPARATOR = File.separator;

    RandomPackage RANDOM_PACKAGE = RandomPackage.getPlugin;
    FileConfiguration RP_CONFIG = RANDOM_PACKAGE.getConfig();
    String RP_VERSION = RANDOM_PACKAGE.getDescription().getVersion();
    PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    Random RANDOM = new Random();

    BukkitScheduler SCHEDULER = Bukkit.getScheduler();
    ScoreboardManager SCOREBOARD_MANAGER = Bukkit.getScoreboardManager();
    ConsoleCommandSender CONSOLE = Bukkit.getConsoleSender();

    BlockFace[] BLOCK_FACES = new BlockFace[] { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
    EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND, EIGHT ? null : EquipmentSlot.OFF_HAND };
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

    default List<String> getStringList(FileConfiguration yml, String identifier) {
        if(!FEATURE_MESSAGES.containsKey(yml)) {
            FEATURE_MESSAGES.put(yml, new HashMap<>());
        }
        final HashMap<String, List<String>> messages = FEATURE_MESSAGES.get(yml);
        if(!messages.containsKey(identifier)) {
            messages.put(identifier, colorizeListString(yml.getStringList(identifier)));
        }
        return messages.get(identifier);
    }

    default ItemStack getClone(ItemStack is) {
        return getClone(is, null);
    }
    default ItemStack getClone(ItemStack is, ItemStack def) {
        return is != null ? is.clone() : def;
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

    default void sendConsoleMessage(String msg) {
        CONSOLE.sendMessage(colorize(msg));
    }
    default String formatBigDecimal(BigDecimal b) {
        return formatBigDecimal(b, false);
    }
    default String formatBigDecimal(BigDecimal b, boolean currency) {
        return (currency ? NumberFormat.getCurrencyInstance() : NumberFormat.getInstance()).format(b);
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
    default String formatInt(int integer) { return String.format("%,d", integer); }
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
            final String[] s = input.split("d");
            l += getRemainingDouble(s[0])*1000*60*60*24;
            input = s.length > 1 ? s[1] : input;
        }
        if(input.contains("h")) {
            final String[] s = input.split("h");
            l += getRemainingDouble(s[0])*1000*60*60;
            input = s.length > 1 ? s[1] : input;
        }
        if(input.contains("m")) {
            final String[] s = input.split("m");
            l += getRemainingDouble(s[0])*1000*60;
            input = s.length > 1 ? s[1] : input;
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
        final double d = round(input, decimals);
        return Double.toString(d);
    }

    default String center(String s, int size) {
        // Credit to "Sahil Mathoo" from StackOverFlow at https://stackoverflow.com/questions/8154366
        return center(s, size, ' ');
    }
    default String center(String s, int size, char pad) {
        if(s == null || size <= s.length()) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(size);
        for(int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while(sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
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
    default void sendStringListMessage(CommandSender sender, List<String> message, HashMap<String, String> replacements) {
        if(message != null && message.size() > 0 && !message.get(0).equals("")) {
            for(String s : message) {
                if(replacements != null) {
                    for(String r : replacements.keySet()) {
                        s = s.replace(r, replacements.get(r));
                    }
                }
                if(s != null) {
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
                case "wolf": return true;
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

    default BlockFace getFacing(Entity entity) {
        return LEGACY || THIRTEEN ? BLOCK_FACES[Math.round(entity.getLocation().getYaw() / 45f) & 0x7] : entity.getFacing();
    }
    default String toReadableDate(Date d, String format) {
        return new SimpleDateFormat(format).format(d);
    }
    default Entity getEntity(UUID uuid) {
        if(uuid != null) {
            if(EIGHT || NINE) {
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
    default LivingEntity getEntity(String type, Location l) {
        final World w = l.getWorld();
        final LivingEntity le;
        switch (type.toUpperCase()) {
            case "BAT": return w.spawn(l, Bat.class);
            case "BLAZE": return w.spawn(l, Blaze.class);
            case "CAVE_SPIDER": return w.spawn(l, CaveSpider.class);
            case "CHICKEN": return w.spawn(l, Chicken.class);
            case "COW": return w.spawn(l, Cow.class);
            case "CREEPER": return w.spawn(l, Creeper.class);
            case "ENDER_DRAGON": return w.spawn(l, EnderDragon.class);
            case "ENDERMAN": return w.spawn(l, Enderman.class);
            case "GHAST": return w.spawn(l, Ghast.class);
            case "GIANT": return w.spawn(l, Giant.class);
            case "GUARDIAN": return w.spawn(l, Guardian.class);
            case "HORSE": return w.spawn(l, Horse.class);
            case "IRON_GOLEM": return w.spawn(l, IronGolem.class);
            case "LLAMA": return EIGHT || NINE || TEN ? null : w.spawn(l, Llama.class);
            case "MAGMA_CUBE": return w.spawn(l, MagmaCube.class);
            case "MUSHROOM_COW": return w.spawn(l, MushroomCow.class);
            case "OCELOT": return w.spawn(l, Ocelot.class);
            case "PARROT": return EIGHT || NINE || TEN || ELEVEN ? null : w.spawn(l, Parrot.class);
            case "PIG": return w.spawn(l, Pig.class);
            case "PIG_ZOMBIE": return w.spawn(l, PigZombie.class);
            case "RABBIT": return w.spawn(l, Rabbit.class);
            case "SHEEP": return w.spawn(l, Sheep.class);
            case "SHULKER": return EIGHT ? null : w.spawn(l, Shulker.class);
            case "SILVERFISH": return w.spawn(l, Silverfish.class);
            case "SKELETON": return w.spawn(l, Skeleton.class);
            case "SLIME": return w.spawn(l, Slime.class);
            case "SNOWMAN": return w.spawn(l, Snowman.class);
            case "SQUID": return w.spawn(l, Squid.class);
            case "SPIDER": return w.spawn(l, Spider.class);
            case "STRAY": return EIGHT || NINE ? null : w.spawn(l, Stray.class);
            case "VEX": return EIGHT || NINE || TEN ? null : w.spawn(l, Vex.class);
            case "VILLAGER": return w.spawn(l, Villager.class);
            case "VINDICATOR": return EIGHT || NINE || TEN ? null : w.spawn(l, Vindicator.class);
            case "WITHER_SKELETON":
                if(EIGHT || NINE || TEN) {
                    le = w.spawn(l, Skeleton.class);
                    ((Skeleton) le).setSkeletonType(Skeleton.SkeletonType.WITHER);
                    return le;
                } else {
                    return w.spawn(l, WitherSkeleton.class);
                }
            case "ZOMBIE": return w.spawn(l, Zombie.class);
            case "ZOMBIE_HORSE": return EIGHT ? null : w.spawn(l, ZombieHorse.class);
            case "ZOMBIE_VILLAGER": return EIGHT ? null : w.spawn(l, ZombieVillager.class);
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
}
