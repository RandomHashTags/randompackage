package me.randomhashtags.randompackage.universal;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.addon.util.Identifiable;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.mechanics.SpawnerAPI;
import me.randomhashtags.randompackage.util.UVersionable;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public interface UVersionableSpigot extends Versionable, UVersionable {
    HashMap<Feature, LinkedHashMap<String, Identifiable>> FEATURES = new HashMap<>();
    RandomPackage RANDOM_PACKAGE = RandomPackage.INSTANCE;

    File DATA_FOLDER = RANDOM_PACKAGE.getDataFolder();
    FileConfiguration RP_CONFIG = RANDOM_PACKAGE.getConfig();
    String RP_VERSION = RANDOM_PACKAGE.getDescription().getVersion();
    PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    Server SERVER = Bukkit.getServer();

    BukkitScheduler SCHEDULER = Bukkit.getScheduler();
    ScoreboardManager SCOREBOARD_MANAGER = Bukkit.getScoreboardManager();
    ConsoleCommandSender CONSOLE = Bukkit.getConsoleSender();

    BlockFace[] BLOCK_FACES = new BlockFace[] { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
    EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND, EIGHT ? null : EquipmentSlot.OFF_HAND };
    Set<String> INTERACTABLE_MATERIALS = new HashSet<>() {{
        addAll(List.of(
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

    @Override
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

    default List<String> getStringList(@NotNull FileConfiguration yml, @NotNull String identifier) {
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

    default ItemStack getClone(ItemStack is) {
        return getClone(is, null);
    }
    default ItemStack getClone(ItemStack is, ItemStack def) {
        return is != null ? is.clone() : def;
    }

    default Set<String> getConfigurationSectionKeys(YamlConfiguration yml, String key, boolean includeKeys, String...excluding) {
        final ConfigurationSection section = yml.getConfigurationSection(key);
        if(section != null) {
            final Set<String> set = section.getKeys(includeKeys);
            List.of(excluding).forEach(set::remove);
            return set;
        } else {
            return new HashSet<>();
        }
    }

    default boolean allowsPvP(Player player, Location location) {
        return RegionalAPI.INSTANCE.allowsPvP(player, location);
    }

    default int getTotalExperience(@NotNull Player player) {
        final int level = player.getLevel();
        final double levelxp = convertLevelToExp(level), nextlevelxp = convertLevelToExp(level + 1), difference = nextlevelxp - levelxp;
        final double p = (levelxp + (difference * player.getExp()));
        return (int) Math.round(p);
    }
    default void setTotalExperience(@NotNull Player player, int total) {
        player.setTotalExperience(0);
        player.setExp(0f);
        player.setLevel(0);
        player.giveExp(total);
    }
    default double convertLevelToExp(int level) {
        return level <= 16 ? (level * level) + (level * 6) : level <= 31 ? (2.5 * level * level) - (40.5 * level) + 360 : (4.5 * level * level) - (162.5 * level) + 2220;
    }

    default void sendConsoleDidLoadFeature(String what, long started) {
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + what + " &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    default void sendConsoleDidLoadAsyncFeature(String what) {
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + what + " &e[async]");
    }

    default EquipmentSlot getRespectiveSlot(@NotNull String material) {
        return material.contains("HELMET") || material.contains("SKULL") || material.contains("HEAD") ? EquipmentSlot.HEAD
                : material.contains("CHESTPLATE") || material.contains("ELYTRA") ? EquipmentSlot.CHEST
                : material.contains("LEGGINGS") ? EquipmentSlot.LEGS
                : material.contains("BOOTS") ? EquipmentSlot.FEET
                : null;
    }

    @Override
    default void sendConsoleMessage(String msg) {
        CONSOLE.sendMessage(colorize(msg));
    }
    @Override
    default int getRemainingInt(@NotNull String string) {
        string = ChatColor.stripColor(colorize(string)).replaceAll("\\p{L}", "").replaceAll("\\s", "").replaceAll("\\p{P}", "").replaceAll("\\p{S}", "");
        return string.isEmpty() ? -1 : Integer.parseInt(string);
    }
    @Override
    default Double getRemainingDouble(@NotNull String string) {
        string = ChatColor.stripColor(colorize(string).replaceAll("\\p{L}", "").replaceAll("\\p{Z}", "").replaceAll("\\.", "d").replaceAll("\\p{P}", "").replaceAll("\\p{S}", "").replace("d", "."));
        return string.isEmpty() ? -1.00 : Double.parseDouble(string.contains(".") && string.split("\\.").length > 1 && string.split("\\.")[1].length() > 2 ? string.substring(0, string.split("\\.")[0].length() + 3) : string);
    }

    @Override
    default @NotNull String colorize(@NotNull String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
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
    default void sendStringListMessage(@NotNull CommandSender sender, @NotNull List<String> message, @Nullable HashMap<String, String> replacements) {
        final boolean placeholder_api = RANDOM_PACKAGE.placeholder_api, isPlayer = sender instanceof Player;
        final Player player = isPlayer ? (Player) sender : null;
        for(String string : message) {
            if(replacements != null) {
                for(Map.Entry<String, String> entry : replacements.entrySet()) {
                    final String replacement = entry.getValue();
                    string = string.replace(entry.getKey(), replacement != null ? replacement : "null");
                }
            }
            if(string != null) {
                if(placeholder_api && isPlayer) {
                    string = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
                }
                sender.sendMessage(colorize(string));
            }
        }
    }

    default Entity getHitEntity(@NotNull ProjectileHitEvent event) {
        if(EIGHT || NINE || TEN) {
            final List<Entity> n = event.getEntity().getNearbyEntities(0.1, 0.1, 0.1);
            return n.size() > 0 ? n.get(0) : null;
        } else {
            return event.getHitEntity();
        }
    }

    default boolean material_is_armor_piece(@NotNull Material material) {
        final String type = material.name();
        return type.endsWith("_HELMET") || type.endsWith("_CHESTPLATE") || type.endsWith("_LEGGINGS") || type.endsWith("_BOOTS");
    }

    default boolean entity_type_is_passive(@NotNull EntityType type) {
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
                case "zombie_horse":
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
    default boolean entity_type_is_aggressive(@NotNull EntityType type) {
        return !entity_type_is_passive(type);
    }
    default boolean entity_type_is_neutral(@NotNull EntityType type) {
        if(type.isSpawnable() && !entity_type_is_passive(type)) {
            switch (type.name()) {
                case "enderman":
                case "iron_golem":
                case "polar_bear":
                case "wolf":
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    @Nullable
    default PotionEffectType get_potion_effect_type(@NotNull String input) {
        if(!input.isEmpty()) {
            switch (input.toUpperCase()) {
                case "STRENGTH":
                    return PotionEffectType.INCREASE_DAMAGE;
                case "MINING_FATIGUE":
                    return PotionEffectType.SLOW_DIGGING;
                case "SLOWNESS":
                    return PotionEffectType.SLOW;
                case "HASTE":
                    return PotionEffectType.FAST_DIGGING;
                case "JUMP":
                    return PotionEffectType.JUMP;
                case "INSTANT_HEAL":
                case "INSTANT_HEALTH":
                    return PotionEffectType.HEAL;
                case "INSTANT_HARM":
                case "INSTANT_DAMAGE":
                    return PotionEffectType.HARM;
                default:
                    for(PotionEffectType type : PotionEffectType.values()) {
                        if (type != null && input.equalsIgnoreCase(type.getName())) {
                            return type;
                        }
                    }
                    return null;
            }
        } else {
            return null;
        }
    }

    default String location_to_string(@NotNull Location loc) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }
    default Location string_to_location(@Nullable String string) {
        if(string != null && string.contains(";")) {
            final String[] a = string.split(";");
            return new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]), Float.parseFloat(a[4]), Float.parseFloat(a[5]));
        } else {
            return null;
        }
    }

    default void giveItem(@NotNull Player player, ItemStack is) {
        if(is == null || is.getType().equals(Material.AIR)) {
            return;
        }
        final UMaterial umaterial = UMaterial.match(is);
        final ItemMeta meta = is.getItemMeta();
        final PlayerInventory inv = player.getInventory();
        final int firstMaterialSlot = inv.first(is.getType()), firstEmptySlot = inv.firstEmpty(), max = is.getMaxStackSize();
        int amountLeft = is.getAmount();

        if(firstMaterialSlot != -1) {
            for(int i = 0; i < inv.getSize(); i++) {
                final ItemStack targetItemStack = inv.getItem(i);
                if(amountLeft > 0 && targetItemStack != null && targetItemStack.getItemMeta().equals(meta) && UMaterial.match(targetItemStack) == umaterial) {
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

    default BlockFace getFacing(@NotNull Entity entity) {
        return LEGACY || THIRTEEN ? BLOCK_FACES[Math.round(entity.getLocation().getYaw() / 45f) & 0x7] : entity.getFacing();
    }
    default Entity get_entity_from_uuid(@NotNull UUID uuid) {
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
        return null;
    }
    default LivingEntity getEntity(@NotNull String type, Location l, boolean spawn) {
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

    @Nullable
    default Color getColor(@NotNull String path) {
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
    default LivingEntity getEntity(@NotNull String type, @NotNull Location location) {
        final World world = location.getWorld();
        if(world == null) {
            return null;
        }
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
    default boolean isInteractable(@NotNull Material material) {
        if(!LEGACY) {
            return material.isInteractable();
        } else {
            final String m = material.name();
            return INTERACTABLE_MATERIALS.contains(m) || m.contains("ANVIL") || m.endsWith("_BED")
                    || m.endsWith("DOOR") && !m.equals("IRON_DOOR")
                    ;
        }
    }

    default void didApply(InventoryClickEvent event, Player player, ItemStack current, ItemStack cursor) {
        event.setCancelled(true);
        final int amount = cursor.getAmount();
        if(amount == 1) {
            event.setCursor(new ItemStack(Material.AIR));
        } else {
            cursor.setAmount(amount-1);
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

    default void playParticle(@NotNull FileConfiguration config, @NotNull String path, @NotNull Location location, int count) {
        final String target = config.getString(path);
        if(target != null) {
            final UParticleSpigot up = UParticleSpigot.matchParticle(target.toUpperCase());
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

    @NotNull
    default List<Location> getChunkLocations(@NotNull Chunk chunk) {
        final List<Location> locations = new ArrayList<>();
        final int chunk_x = chunk.getX() * 16, chunk_z = chunk.getZ() * 16;
        final World world = chunk.getWorld();
        for(int x = chunk_x; x < chunk_x+16; x++) {
            for(int z = chunk_z; z < chunk_z+16; z++) {
                locations.add(new Location(world, x, 0, z));
            }
        }
        return locations;
    }
    @NotNull
    default List<Player> getWorldPlayers(@NotNull Location location) {
        final World world = location.getWorld();
        return world != null ? world.getPlayers() : new ArrayList<>();
    }
    @NotNull
    default ItemStack getItemInHand(@NotNull LivingEntity entity) {
        final EntityEquipment e = entity.getEquipment();
        return EIGHT ? e.getItemInHand() : e.getItemInMainHand();
    }
}