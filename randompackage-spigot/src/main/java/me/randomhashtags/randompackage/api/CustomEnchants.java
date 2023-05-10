package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.addon.file.FileCustomEnchantSpigot;
import me.randomhashtags.randompackage.addon.file.FileEnchantRarity;
import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.addon.obj.CustomEnchantEntity;
import me.randomhashtags.randompackage.api.addon.Scrolls;
import me.randomhashtags.randompackage.attribute.EventAttribute;
import me.randomhashtags.randompackage.attribute.SetSuccessRate;
import me.randomhashtags.randompackage.attribute.SpawnEntity;
import me.randomhashtags.randompackage.attribute.StopEnchant;
import me.randomhashtags.randompackage.attributesys.EventAttributes;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.PvAnyEvent;
import me.randomhashtags.randompackage.event.armor.*;
import me.randomhashtags.randompackage.event.enchant.*;
import me.randomhashtags.randompackage.event.enums.ArmorEventReason;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import me.randomhashtags.randompackage.event.mob.CustomBossDamageByEntityEvent;
import me.randomhashtags.randompackage.event.mob.MobStackDepleteEvent;
import me.randomhashtags.randompackage.perms.CustomEnchantPermission;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import me.randomhashtags.randompackage.supported.regional.FactionsUUID;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import me.randomhashtags.randompackage.util.obj.EquippedCustomEnchants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public enum CustomEnchants implements EventAttributes, CommandExecutor, Listener {
    INSTANCE;

    private YamlConfiguration config;
    public boolean levelZeroRemoval;
    public static List<String> CUSTOM_ENCHANT_GLOBAL_ATTRIBUTES;

    private HashMap<CustomEnchantSpigot, Integer> timedEnchants;
    private HashMap<UUID, EquippedCustomEnchants> playerEnchants;
    private HashMap<Player, List<CustomEnchantSpigot>> equippedTimedEnchants;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String cmdName = cmd.getName();
        if(cmdName.equals("disabledenchants") && hasPermission(player, CustomEnchantPermission.COMMAND_DISABLED_ENCHANTS, true)) {
            sender.sendMessage(colorize(getAll(Feature.CUSTOM_ENCHANT_DISABLED).keySet().toString()));
        } else if(cmdName.equals("enchants") && hasPermission(sender, CustomEnchantPermission.COMMAND_ENCHANTS, true)) {
            if(args.length == 0) {
                viewEnchants(sender, 1);
            } else {
                final int page = getRemainingInt(args[0]);
                viewEnchants(sender, page > 0 ? page : 1);
            }
        }
        return true;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("custom enchants", "_settings.yml");
        final String folderString = DATA_FOLDER + SEPARATOR + "custom enchants";
        config = YamlConfiguration.loadConfiguration(new File(folderString, "_settings.yml"));
        levelZeroRemoval = config.getBoolean("settings.level zero removal");

        save("custom enchants", "global attributes.yml");
        CUSTOM_ENCHANT_GLOBAL_ATTRIBUTES = getStringList(YamlConfiguration.loadConfiguration(new File(folderString, "global attributes.yml")), "attributes");

        final EventAttribute[] attributes = new EventAttribute[] {
                new SetSuccessRate(),
                new SpawnEntity(),
                new StopEnchant()
        };
        for(EventAttribute attribute : attributes) {
            attribute.load();
        }

        if(!OTHER_YML.getBoolean("saved default custom enchants")) {
            generateDefaultCustomEnchants();
            OTHER_YML.set("saved default custom enchants", true);
            saveOtherData();
        }

        timedEnchants = new HashMap<>();
        final List<ItemStack> raritybooks = new ArrayList<>();
        final HashMap<String, Integer> enchantTicks = new HashMap<>();

        playerEnchants = new HashMap<>();
        equippedTimedEnchants = new HashMap<>();

        for(File enchant_rarity_file : getFilesInFolder(folderString)) {
            if(enchant_rarity_file.isDirectory()) {
                final File[] files = new File(folderString + SEPARATOR + enchant_rarity_file.getName()).listFiles();
                if(files != null) {
                    FileEnchantRarity rarity = null;
                    final File[] all_files = files;
                    for(File f : all_files) {
                        if(f.getName().contains("_settings")) {
                            rarity = new FileEnchantRarity(enchant_rarity_file, f);
                            raritybooks.add(rarity.getRevealItem());
                        }
                    }
                    if(rarity != null) {
                        for(File enchant_file : files) {
                            if(!enchant_file.getName().startsWith("_settings")) {
                                final FileCustomEnchantSpigot custom_enchant = new FileCustomEnchantSpigot(enchant_file);
                                rarity.getEnchants().add(custom_enchant);
                                for(String s : custom_enchant.getAttributes()) {
                                    final String[] split = s.split(";");
                                    final String l = split[0].toLowerCase();
                                    if(l.equals("customenchanttimer")) {
                                        final int ticks = (int) evaluate(split[1].split("=")[1]);
                                        final int id = SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, () -> {
                                            for(Player player : equippedTimedEnchants.keySet()) {
                                                final EquippedCustomEnchants enchants = getEnchants(player);
                                                final LinkedHashMap<ItemStack, LinkedHashMap<CustomEnchantSpigot, Integer>> enchant = enchants.getInfo();
                                                if(!enchant.isEmpty()) {
                                                    final CustomEnchantTimerEvent event = new CustomEnchantTimerEvent(player, enchant);
                                                    PLUGIN_MANAGER.callEvent(event);
                                                    triggerCustomEnchants(event, getEntities("Player", player), enchants, CUSTOM_ENCHANT_GLOBAL_ATTRIBUTES);
                                                }
                                            }
                                        }, ticks, ticks);
                                        timedEnchants.put(custom_enchant, id);
                                        enchantTicks.put(rarity.getApplyColors() + getLocalizedName(custom_enchant), ticks);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            updateEquippedTimedEnchants(player);
        }

        sendConsoleMessage("&aStarted Custom Enchant Timers for enchants &e" + enchantTicks);
        addGivedpCategory(raritybooks, UMaterial.BOOK, "Rarity Books", "Givedp: Rarity Books");
        load_custom_enchant_entities();

        sendConsoleMessage("&aLoaded [&f" + getAll(Feature.CUSTOM_ENCHANT_ENABLED).size() + "e, &c" + getAll(Feature.CUSTOM_ENCHANT_DISABLED).size() + "d&a] Custom Enchants &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
        for(CustomEnchantSpigot e : timedEnchants.keySet()) {
            SCHEDULER.cancelTask(timedEnchants.get(e));
        }
        GivedpItem.INSTANCE.items.remove("transmogscroll");
        GivedpItem.INSTANCE.items.remove("whitescroll");
        CustomEnchantEntity.deleteAll();
        unregister(Feature.CUSTOM_ENCHANT_ENABLED, Feature.CUSTOM_ENCHANT_RARITY);
    }

    private void load_custom_enchant_entities() {
        final long started = System.currentTimeMillis();
        final boolean defaultDropsItemsUponDeath = config.getBoolean("entities.settings.default drops items upon death"), defaultCanTargetSummoner = config.getBoolean("entities.settings.default can target summoner");
        final HashMap<String, CustomEnchantEntity> entities = CustomEnchantEntity.PATHS;
        for(String string : getConfigurationSectionKeys(config, "entities", false)) {
            if(!string.startsWith("settings")) {
                final String identifier = string.split("\\.")[0];
                if(entities == null || !entities.containsKey(identifier)) {
                    final String path = "entities." + identifier + ".";
                    final boolean canTargetSummoner = config.getBoolean(path + "can target summoner", defaultCanTargetSummoner);
                    final boolean dropsItemsUponDeath = config.getBoolean(path + "drops items upon death", defaultDropsItemsUponDeath);

                    final EntityType type = EntityType.valueOf(config.getString(path + "type", "ZOMBIE").toUpperCase());
                    final String name = config.getString(path + "name");
                    final List<String> attributes = config.getStringList(path + "attributes");
                    new CustomEnchantEntity(type, identifier, name, attributes, canTargetSummoner, dropsItemsUponDeath);
                }
            }
        }
        sendConsoleDidLoadFeature((entities != null ? entities.size() : 0) + " Custom Enchant Entities", started);
    }
    public void viewEnchants(@NotNull CommandSender sender, int page) {
        final ChatEvents chatEvents = ChatEvents.INSTANCE;
        final String format = getString(RP_CONFIG, "enchants.format");
        final List<String> hoverLore = getStringList(RP_CONFIG, "enchants.hover");
        final HashMap<String, CustomEnchantSpigot> enabled = getAllCustomEnchants(true);
        final Object[] enchants = enabled.values().toArray();
        final int size = enabled.size(), maxpage = size/10;
        page = Math.min(page, maxpage);
        final int starting = page * 10;
        final String max = Integer.toString(maxpage), page_string = Integer.toString(page);
        for(String s : getStringList(RP_CONFIG, "enchants.msg")) {
            if(s.equals("{ENCHANTS}")) {
                for(int i = starting; i <= starting+10; i++) {
                    if(size > i) {
                        final CustomEnchantSpigot enchant = (CustomEnchantSpigot) enchants[i];
                        final EnchantRarity rarity = valueOfCustomEnchantRarity(enchant);
                        final HashMap<String, List<String>> replacements = new HashMap<>();
                        replacements.put("{TIER}", List.of(rarity.getApplyColors() + rarity.getIdentifier()));
                        replacements.put("{DESC}", enchant.getLore());
                        final String msg = colorize(format.replace("{MAX}", Integer.toString(enchant.getMaxLevel())).replace("{ENCHANT}", rarity.getApplyColors() + ChatColor.BOLD + enchant.getName()));
                        if(sender instanceof Player) {
                            chatEvents.sendHoverMessage((Player) sender, msg, hoverLore, replacements);
                        } else {
                            sender.sendMessage(msg);
                        }
                    }
                }
            } else {
                sender.sendMessage(s.replace("{MAX_PAGE}", max).replace("{PAGE}", page_string));
            }
        }
    }
    public boolean canProcOn(@NotNull Entity e) {
        return getStringList(config, "settings.can proc on").contains(e.getType().name());
    }
    public int getLevelCap(@NotNull Player player) {
        int cap = 0;
        final String prefix = CustomEnchantPermission.LEVEL_CAP_PREFIX;
        for(int i = 0; i <= 100; i++) {
            if(player.hasPermission(prefix + i)) {
                cap = i;
            }
        }
        return cap;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void projectileHitEvent(EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        final UUID damagerUUID = damager.getUniqueId();
        final EntityShootBowEvent shoot_bow_event = PROJECTILE_EVENTS.getOrDefault(damagerUUID, null);
        if(shoot_bow_event != null) {
            final Projectile projectile = (Projectile) damager;
            final ProjectileSource shooter = projectile.getShooter();
            if(shooter instanceof Player) {
                final Player player = (Player) shooter;
                if(allowsPvP(player, damager.getLocation())) {
                    triggerEnchants(event, getEnchants(player));
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void projectileHitEvent(ProjectileHitEvent event) {
        final Projectile p = event.getEntity();
        final ProjectileSource shooter = p.getShooter();
        if(shooter instanceof Player) {
            final Player player = (Player) shooter;
            if(allowsPvP(player, p.getLocation())) {
                triggerEnchants(event, getEnchants(player));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void projectileLaunchEvent(ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        final ProjectileSource shooter = projectile.getShooter();
        if(shooter instanceof Player) {
            final Player player = (Player) shooter;
            if(allowsPvP(player, projectile.getLocation())) {
                triggerEnchants(event, getEnchants(player));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof LivingEntity && canProcOn(entity)) {
            final Entity damager = event.getDamager();
            Player player = damager instanceof Player ? (Player) damager : null;
            if(player != null && allowsPvP(player, entity.getLocation())) {
                final PvAnyEvent e = new PvAnyEvent(player, (LivingEntity) entity, event.getDamage());
                PLUGIN_MANAGER.callEvent(e);
                triggerEnchants(e, getEnchants(player));
                event.setDamage(e.getDamage());
            }
            if(entity instanceof Player && damager instanceof LivingEntity && !(damager instanceof TNTPrimed) && !(damager instanceof Creeper)) {
                final Player victim = (Player) entity;
                if(allowsPvP(victim, damager.getLocation())) {
                    final LivingEntity d = (LivingEntity) damager;
                    final isDamagedEvent damaged_event = new isDamagedEvent(victim, d, event.getDamage());
                    PLUGIN_MANAGER.callEvent(damaged_event);
                    triggerEnchants(damaged_event, getEnchants(victim));
                    event.setDamage(damaged_event.getDamage());
                }
            }
            final HashMap<UUID, LivingCustomEnchantEntity> living = LivingCustomEnchantEntity.LIVING;
            if(living != null) {
                final LivingCustomEnchantEntity cee = living.getOrDefault(entity.getUniqueId(), null);
                if(cee != null) {
                    final CustomEnchantEntityDamageByEntityEvent entity_damage_by_entity_event = new CustomEnchantEntityDamageByEntityEvent(cee, damager, event.getFinalDamage(), event.getDamage());
                    PLUGIN_MANAGER.callEvent(entity_damage_by_entity_event);
                    if(!entity_damage_by_entity_event.isCancelled()) {
                        event.setDamage(entity_damage_by_entity_event.initial_damage);
                        final LivingEntity summoner = cee.getSummoner();
                        final Player summoner_player = summoner instanceof Player ? (Player) summoner : null;
                        if(summoner_player != null) {
                            triggerEnchants(event, getEnchants(summoner_player));
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void customBossDamageByEntityEvent(CustomBossDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        if(damager instanceof Player) {
            triggerEnchants(event, getEnchants((Player) damager));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void mobStackDepleteEvent(MobStackDepleteEvent event) {
        final Player killer = event.killer instanceof Player ? (Player) event.killer : null;
        if(killer != null) {
            triggerEnchants(event, getEnchants(killer));
        }
    }

    @Nullable
    public ItemStack getRevealedItemFromString(String input) {
        // ce:<enchant>:<level>:<success>:<destroy>
        final String[] values = input.split(":");
        final int length = values.length;
        CustomEnchantSpigot enchant = getCustomEnchant(values[1]);
        final EnchantRarity rarity = enchant == null ? getCustomEnchantRarity(values[1]) : null;
        if(rarity != null) {
            final List<CustomEnchantSpigot> list = rarity.getEnchants();
            enchant = list.get(RANDOM.nextInt(list.size()));
        }
        int level = 1, success = 0, destroy = 0;
        if(enchant != null) {
            switch (length) {
                case 3:
                    level = parseInt(values[2]);
                    success = RANDOM.nextInt(101);
                    destroy = RANDOM.nextInt(101);
                    break;
                case 4:
                    level = parseInt(values[2]);
                    success = parseInt(values[3]);
                    destroy = RANDOM.nextInt(101);
                    break;
                case 5:
                    level = parseInt(values[2]);
                    success = parseInt(values[3]);
                    destroy = parseInt(values[4]);
                    break;
                default:
                    level = 1 + RANDOM.nextInt(enchant.getMaxLevel());
                    break;
            }
        }
        return enchant != null ? getRevealedItem(enchant, level, success, destroy, true, true) : null;
    }
    @NotNull
    public ItemStack getRevealedItem(CustomEnchantSpigot enchant, int level, int success, int destroy, boolean showEnchantType, boolean showOtherLore) {
        final EnchantRarity rarity = valueOfCustomEnchantRarity(enchant);
        final ItemStack item = rarity.getRevealedItem().clone();
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(rarity.getNameColors() + enchant.getName() + " " + toRoman(level));
        final List<String> lore = new ArrayList<>(), enchantLore = enchant.getLore();
        for(String string : rarity.getLoreFormat()) {
            if(string.equals("{SUCCESS}")) {
                if(success != -1) {
                    lore.add(rarity.getSuccess().replace("{PERCENT}", Integer.toString(success)));
                }
            } else if(string.equals("{DESTROY}")) {
                if(destroy != -1) {
                    lore.add(rarity.getDestroy().replace("{PERCENT}", Integer.toString(destroy)));
                }
            } else if(string.equals("{ENCHANT_LORE}")) {
                lore.addAll(enchantLore);
            } else if(string.equals("{ENCHANT_TYPE}") && showEnchantType) {
                final String path = enchant.getAppliesTo().toString().toLowerCase().replace(",", ";").replace("[", "").replace("]", "").replaceAll("\\p{Z}", "");
                lore.add(getString(config, "enchant types." + path));
            } else if(showOtherLore) {
                lore.add(string);
            }
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    @NotNull
    public ItemStack getRandomEnabledEnchant(@NotNull EnchantRarity rarity) {
        final String[] rarities = rarity.getRevealedEnchantRarities();
        final int l = rarities.length;
        final EnchantRarity rar = getCustomEnchantRarity(rarity.getRevealedEnchantRarities()[RANDOM.nextInt(l)]);
        final List<CustomEnchantSpigot> enchants = rar.getEnchants();
        ItemStack item = new ItemStack(Material.BOOK);
        for(int i = 1; i <= 100; i++) {
            final CustomEnchantSpigot enchant = enchants.get(RANDOM.nextInt(enchants.size()));
            if(enchant.isEnabled()) {
                rarity = valueOfCustomEnchantRarity(enchant);
                final int level = RANDOM.nextInt(enchant.getMaxLevel()+1);
                item = rarity.getRevealedItem().clone();
                final ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(rarity.getNameColors() + enchant.getName() + " " + toRoman(level == 0 ? 1 : level));
                final List<String> lore = new ArrayList<>();
                final String appliesto = enchant.getAppliesTo().toString().replace(" ", "").replace(",", ";");
                final int sp = RANDOM.nextInt(101), dp = rarity.percentsAddUpto100() ? 100-sp : RANDOM.nextInt(101);
                for(String s : rarity.getLoreFormat()) {
                    if(s.equals("{SUCCESS}")) {
                        s = rarity.getSuccess().replace("{PERCENT}", Integer.toString(sp));
                    }
                    if(s.equals("{DESTROY}")) {
                        s = rarity.getDestroy().replace("{PERCENT}", Integer.toString(dp));
                    }
                    if(s.equals("{ENCHANT_LORE}")) {
                        lore.addAll(enchant.getLore());
                    }
                    if(s.equals("{ENCHANT_TYPE}")) {
                        s = config.getString("enchant types." + appliesto.substring(1, appliesto.length()-1));
                    }
                    if(s != null && !s.equals("{ENCHANT_LORE}")) {
                        lore.add(colorize(s));
                    }
                }
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                break;
            }
        }
        return item;
    }
    public int getEnchantmentLevel(@NotNull String string) {
        string = ChatColor.stripColor(string.split(" ")[string.split(" ").length - 1].toLowerCase().replace("i", "1").replace("v", "2").replace("x", "3").replaceAll("\\p{L}", "").replace("1", "i").replace("2", "v").replace("3", "x").replaceAll("\\p{N}", "").replaceAll("\\p{P}", "").replaceAll("\\p{S}", "").replaceAll("\\p{M}", "").replaceAll("\\p{Z}", "").toUpperCase());
        return fromRoman(string);
    }
    public boolean isOnCorrectItem(@NotNull CustomEnchantSpigot enchant, @NotNull ItemStack is) {
        final String i = is.getType().name();
        for(String s : enchant.getAppliesTo()) {
            if(i.endsWith(s.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    public EquippedCustomEnchants getEnchants(@NotNull Player player) {
        final UUID uuid = player.getUniqueId();
        if(playerEnchants == null) {
            playerEnchants = new HashMap<>();
        }
        if(playerEnchants.containsKey(uuid)) {
            return playerEnchants.get(uuid);
        } else {
            final EquippedCustomEnchants equipped = new EquippedCustomEnchants(player);
            equipped.update(EQUIPMENT_SLOTS);
            playerEnchants.put(uuid, equipped);
            return equipped;
        }
    }
    public LinkedHashMap<CustomEnchantSpigot, Integer> getEnchantsOnItem(@NotNull ItemStack is) {
        final LinkedHashMap<CustomEnchantSpigot, Integer> enchants = new LinkedHashMap<>();
        if(is.hasItemMeta() && is.getItemMeta().hasLore()) {
            for(String s : is.getItemMeta().getLore()) {
                final CustomEnchantSpigot custom_enchant = valueOfCustomEnchant(s);
                if(custom_enchant != null) {
                    enchants.put(custom_enchant, getEnchantmentLevel(s));
                }
            }
        }
        return enchants;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDamageEvent(EntityDamageEvent event) {
        final EntityDamageEvent.DamageCause cause = event.getCause();
        final Entity entity = event.getEntity();
        if(entity instanceof Player) {
            final Player victim = (Player) entity;
            if(!cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                final isDamagedEvent e = new isDamagedEvent(victim, cause, event.getDamage());
                PLUGIN_MANAGER.callEvent(e);
                triggerEnchants(event, getEnchants(victim));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityTameEvent(EntityTameEvent event) {
        final AnimalTamer player = event.getOwner();
        if(player instanceof Player) {
            triggerEnchants(event, getEnchants((Player) player));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockBreakEvent(BlockBreakEvent event) {
        triggerEnchants(event, getEnchants(event.getPlayer()));
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        triggerEnchants(event, getEnchants(event.getPlayer()));
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        final Player player = event.getPlayer();
        if(!event.isCancelled()) {
            triggerEnchants(event, getEnchants(player));
        }
        final EnchantRarity rarity = valueOfCustomEnchantRarity(item);
        if(rarity != null) {
            final ItemStack enabledEnchant = getRandomEnabledEnchant(rarity);
            final String displayname = enabledEnchant.getItemMeta().getDisplayName();
            final CustomEnchantSpigot enchant = valueOfCustomEnchant(enabledEnchant);
            final PlayerRevealCustomEnchantEvent e = new PlayerRevealCustomEnchantEvent(player, item, enchant, getEnchantmentLevel(displayname));
            PLUGIN_MANAGER.callEvent(e);
            if(!e.isCancelled()) {
                event.setCancelled(true);
                removeItem(player, item, 1);
                giveItem(player, enabledEnchant);
                spawnFirework(rarity.getFirework(), player.getLocation());
                player.updateInventory();
                for(String s : rarity.getRevealedEnchantMsg()) {
                    player.sendMessage(colorize(s.replace("{ENCHANT}", displayname)));
                }
            }
        } else if(item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
            final CustomEnchantSpigot enchant = valueOfCustomEnchant(item);
            if(enchant != null) {
                sendStringListMessage(player, getStringList(config, "messages.apply info"), null);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityShootBowEvent(EntityShootBowEvent event) {
        final Entity shooter = event.getEntity();
        if(shooter instanceof Player) {
            triggerEnchants(event, getEnchants((Player) shooter));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerDeathEvent(PlayerDeathEvent event) {
        final HashMap<String, Entity> entities = getEntities(event);
        final Player victim = event.getEntity(), killer = victim.getKiller();
        triggerEnchants(event, entities, getEnchants(victim));
        if(killer != null) {
            triggerEnchants(event, entities, getEnchants(killer));
        }
        playerEnchants.remove(victim.getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerItemDamageEvent(PlayerItemDamageEvent event) {
        triggerEnchants(event, getEnchants(event.getPlayer()));
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();
        final UUID u = entity.getUniqueId();
        if(!(entity instanceof Player) && killer != null) {
            triggerEnchants(event, getEnchants(killer));
        }
        final HashMap<UUID, LivingCustomEnchantEntity> living = LivingCustomEnchantEntity.LIVING;
        if(living != null) {
            final LivingCustomEnchantEntity ceEntity = living.get(u);
            if(ceEntity != null) {
                if(!ceEntity.getType().dropsItemsUponDeath()) {
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                }
                final LivingEntity summoner = ceEntity.getSummoner();
                if(summoner instanceof Player) {
                    final FileRPPlayer pdata = FileRPPlayer.get(summoner.getUniqueId());
                    pdata.getCustomEnchantData().getEntities().remove(ceEntity);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        triggerEnchants(event, getEnchants((Player) event.getEntity()));
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
        if(event.getRawSlot() >= 0 && cursor != null && current != null && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore()) {
            final ItemMeta cursorMeta = cursor.getItemMeta();
            final String cursorName = cursorMeta.getDisplayName();
            final CustomEnchantSpigot enchant = valueOfCustomEnchant(cursorName);
            if(enchant != null) {
                final int level = getEnchantmentLevel(cursorName);
                int enchantsize = 0;
                final PlayerPreApplyCustomEnchantEvent pre_apply_event = new PlayerPreApplyCustomEnchantEvent(player, enchant, level, current);
                PLUGIN_MANAGER.callEvent(pre_apply_event);
                if(!pre_apply_event.isCancelled() && isOnCorrectItem(enchant, current)) {
                    boolean apply = false;
                    ItemStack item = current.clone();
                    final ItemMeta itemMeta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    if(item.hasItemMeta() && itemMeta.hasLore()) {
                        if(itemMeta.getLore().containsAll(getStringList(config, "settings.no more enchants"))) {
                            pre_apply_event.setCancelled(true);
                            return;
                        }
                        lore.addAll(itemMeta.getLore());
                    }
                    String result = null;
                    final EnchantRarity enchant_rarity = valueOfCustomEnchantRarity(enchant);
                    final List<String> cursor_lore = cursorMeta.getLore();
                    final int success = getRemainingInt(cursor_lore.get(enchant_rarity.getSuccessSlot())), destroy = getRemainingInt(cursor_lore.get(enchant_rarity.getDestroySlot()));
                    final int levelcap = getLevelCap(player);
                    int prevlevel = -1, prevslot = -1, enchantOrbIncrement = 0;
                    for(int z = 0; z < lore.size(); z++) {
                        final String target = lore.get(z);
                        final CustomEnchantSpigot target_enchant = valueOfCustomEnchant(target);
                        if(target_enchant != null) {
                            enchantsize += 1;
                            if(target_enchant.equals(enchant)) {
                                prevslot = z;
                                prevlevel = getEnchantmentLevel(target);
                                if(prevlevel == target_enchant.getMaxLevel()) {
                                    return;
                                }
                            }
                        } else {
                            final EnchantmentOrb enchantment_orb = valueOfEnchantmentOrb(target);
                            if(enchantment_orb != null) {
                                enchantOrbIncrement = enchantment_orb.getIncrement();
                            }
                        }
                    }
                    if(levelcap + enchantOrbIncrement <= enchantsize) {
                        pre_apply_event.setCancelled(true);
                        return;
                    } else {
                        final String required_enchant = enchant.getRequiredEnchant();
                        final CustomEnchantSpigot replaces = required_enchant != null ? valueOfCustomEnchant(required_enchant.split(";")[0]) : null;
                        final int requiredLvl = replaces != null ? Integer.parseInt(required_enchant.split(";")[1]) : -1;
                        final HashMap<CustomEnchantSpigot, Integer> enchants = replaces != null ? getEnchantsOnItem(current) : null;
                        if(enchants != null && (!enchants.containsKey(replaces) || enchants.get(replaces) < requiredLvl)) {
                            return;
                        }
                        //
                        if(RANDOM.nextInt(100) <= success) {
                            final String enchant_rarity_apply_colors = enchant_rarity.getApplyColors(), enchant_name = getLocalizedName(enchant), enchant_tag = enchant_rarity_apply_colors + enchant_name + " " + toRoman(level);
                            if(lore.isEmpty()) {
                                lore.add(enchant_tag);
                            } else if(prevlevel == -1 && prevslot == -1) {
                                String replacedEnchant = null;
                                final ArrayList<String> newlore = new ArrayList<>();
                                for(String s : lore) {
                                    final CustomEnchantSpigot custom_enchant = valueOfCustomEnchant(s);
                                    if(custom_enchant != null) {
                                        if(custom_enchant.equals(replaces)) {
                                            newlore.add(enchant_tag);
                                            replacedEnchant = s;
                                        } else {
                                            newlore.add(s);
                                        }
                                    }
                                }
                                if(!newlore.contains(enchant_tag)) {
                                    newlore.add(enchant_tag);
                                }
                                for(String s : lore) {
                                    if(!newlore.contains(s) && !s.equals(replacedEnchant)) {
                                        newlore.add(s);
                                    }
                                }
                                lore = newlore;
                            } else {
                                lore.set(prevslot, enchant_rarity_apply_colors + enchant_name + " " + toRoman(level > prevlevel ? level : prevlevel+1));
                            }
                            result = lore.isEmpty() || prevlevel == -1 && prevslot == -1 ? "SUCCESS_APPLY" : "SUCCESS_UPGRADE";
                        } else if(RANDOM.nextInt(100) <= destroy) {
                            final WhiteScroll w = getWhiteScroll("REGULAR");
                            final String applied = w != null ? w.getAppliedString() : null;
                            result = applied != null && lore.contains(applied) ? "DESTROY_WHITE_SCROLL" : "DESTROY";
                            if(lore.contains(applied)) {
                                lore.remove(applied);
                            } else {
                                item = new ItemStack(Material.AIR);
                            }
                        }
                        apply = true;
                        final CustomEnchantApplyEvent ce = new CustomEnchantApplyEvent(player, enchant, level, success, destroy, result);
                        PLUGIN_MANAGER.callEvent(ce);
                    }
                    if(!item.getType().equals(Material.AIR)) {
                        if(itemMeta.hasDisplayName()) {
                            final Scrolls scrolls = Scrolls.INSTANCE;
                            if(scrolls.isEnabled() && scrolls.isEnabled(Feature.SCROLL_TRANSMOG)) {
                                final TransmogScroll transmog = scrolls.valueOfTransmogScrollApplied(item);
                                if(transmog != null) {
                                    scrolls.updateTransmogScroll(item, enchantsize, enchantsize+1);
                                }
                            }
                        }
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                    }
                    event.setCancelled(true);
                    event.setCurrentItem(item);
                    final int amount = cursor.getAmount();
                    if(amount == 1) {
                        event.setCursor(new ItemStack(Material.AIR));
                    } else {
                        cursor.setAmount(amount-1);
                    }
                    player.updateInventory();
                } else {
                    pre_apply_event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void anvilClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inv = player.getInventory();
        final ItemStack current = event.getCurrentItem();
        if(player.getOpenInventory().getTopInventory().getType().equals(InventoryType.ANVIL)) {
            if(current != null && current.hasItemMeta() && current.getItemMeta().hasDisplayName() || event.getClick().equals(ClickType.NUMBER_KEY)) {
                CustomEnchantSpigot enchant = null;
                final int hb = event.getHotbarButton();
                final ItemStack item = event.getClick().equals(ClickType.NUMBER_KEY) && inv.getItem(hb) != null ? inv.getItem(hb) : current;
                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    enchant = valueOfCustomEnchant(item.getItemMeta().getDisplayName());
                }
                if(enchant != null) {
                    event.setCancelled(true);
                    player.closeInventory();
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
        final RegionalAPI regions = RegionalAPI.INSTANCE;
        final Entity entity = event.getEntity();
        final LivingEntity target = event.getTarget();
        if(entity instanceof LivingEntity && target instanceof Player) {
            final UUID uuid = entity.getUniqueId();
            final HashMap<UUID, LivingCustomEnchantEntity> living = LivingCustomEnchantEntity.LIVING;
            if(living != null) {
                final LivingCustomEnchantEntity enchantEntity = living.getOrDefault(uuid, null);
                if(enchantEntity != null) {
                    final Player player = (Player) target, summoner = (Player) enchantEntity.getSummoner();
                    final FileRPPlayer pdata = FileRPPlayer.get(target.getUniqueId());
                    if(!enchantEntity.getType().canTargetSummoner() && pdata.getCustomEnchantData().containsEntity(uuid) || regions.hookedFactionsUUID() && (!FactionsUUID.INSTANCE.isEnemy(player, summoner) || FactionsUUID.INSTANCE.isNeutral(player, summoner))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public void updateEquippedTimedEnchants(@NotNull Player player) {
        final EquippedCustomEnchants equipped = getEnchants(player);
        equippedTimedEnchants.remove(player);
        for(EquipmentSlot slot : EQUIPMENT_SLOTS) {
            if(slot != null) {
                final LinkedHashMap<CustomEnchantSpigot, Integer> enchants = equipped.getEnchantsOn(slot);
                final List<CustomEnchantSpigot> timedEnchantments = new ArrayList<>();
                if(enchants != null) {
                    for(CustomEnchantSpigot enchant : enchants.keySet()) {
                        if(timedEnchants.containsKey(enchant)) {
                            timedEnchantments.add(enchant);
                        }
                    }
                    if(!timedEnchantments.isEmpty()) {
                        equippedTimedEnchants.put(player, timedEnchantments);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorEquipEvent(ArmorEquipEvent event) {
        didArmorEvent(event, true);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorUnequipEvent(ArmorUnequipEvent event) {
        didArmorEvent(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorPieceBreakEvent(ArmorPieceBreakEvent event) {
        didArmorEvent(event);
    }
    private void didArmorEvent(ArmorEvent event) {
        didArmorEvent(event, false);
    }
    private void didArmorEvent(ArmorEvent event, boolean isEquip) {
        final ItemStack is = event.getItem();
        final Player player = event.getPlayer();
        if(isEquip) {
            EquippedCustomEnchants.EVENTS.put(player, (ArmorEquipEvent) event);
        }
        final EquippedCustomEnchants equipped = playerEnchants.get(player.getUniqueId());
        final EquipmentSlot slot = event.getSlot();
        if(!isEquip) {
            equipped.update(slot, is);
            triggerEnchants(event, equipped, false, slot);
        }
        equipped.update(slot, isEquip ? is : null);
        updateEquippedTimedEnchants(player);
        if(isEquip) {
            final ArmorEventReason reason = event.getReason();
            if(reason == ArmorEventReason.HOTBAR_EQUIP) {
                equipped.update(EquipmentSlot.HAND, null);
            }
            triggerEnchants(event, equipped, false, true, slot);
            EquippedCustomEnchants.EVENTS.remove(player);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        triggerEnchants(event, getEnchants(player), true);
    }
    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent event) {
        playerEnchants.remove(event.getPlayer().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerItemHeldEvent(PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final EquippedCustomEnchants equipped = playerEnchants.get(player.getUniqueId());
        equipped.update(EquipmentSlot.HAND, player.getInventory().getItem(event.getNewSlot()));
    }
    public void triggerEnchants(Event event, EquippedCustomEnchants equipped) {
        triggerEnchants(event, equipped, false);
    }
    public void triggerEnchants(Event event, EquippedCustomEnchants equipped, boolean recheck) {
        triggerEnchants(event, equipped, recheck, EQUIPMENT_SLOTS);
    }
    public void triggerEnchants(Event event, EquippedCustomEnchants equipped, boolean recheck, EquipmentSlot...slots) {
        triggerEnchants(event, equipped, recheck, false, slots);
    }
    public void triggerEnchants(Event event, EquippedCustomEnchants equipped, boolean recheck, boolean getEventItem, EquipmentSlot...slots) {
        triggerEnchants(event, getEntities(event), equipped, recheck, getEventItem, slots);
    }

    public void triggerEnchants(Event event, HashMap<String, Entity> entities, EquippedCustomEnchants equipped) {
        triggerEnchants(event, entities, equipped, false);
    }
    public void triggerEnchants(Event event, HashMap<String, Entity> entities, EquippedCustomEnchants equipped, boolean recheck) {
        triggerEnchants(event, entities, equipped, recheck, false);
    }
    public void triggerEnchants(Event event, HashMap<String, Entity> entities, EquippedCustomEnchants equipped, boolean recheck, boolean getEventItem) {
        triggerEnchants(event, entities, equipped, recheck, getEventItem, EQUIPMENT_SLOTS);
    }
    public void triggerEnchants(Event event, HashMap<String, Entity> entities, EquippedCustomEnchants equipped, boolean recheck, boolean getEventItem, EquipmentSlot...slots) {
        if(recheck) {
            equipped.update(slots);
            playerEnchants.put(equipped.getPlayer().getUniqueId(), equipped);
        }
        triggerCustomEnchants(event, entities, equipped, CUSTOM_ENCHANT_GLOBAL_ATTRIBUTES, getEventItem, slots);
    }
}
