package me.randomhashtags.randompackage.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public interface DefaultFileGeneration {
    void save(String folder, String file);

    default void generate_all_default_json(String folder, String...values) {
        for(String s : values) {
            save(folder, s + ".json");
        }
    }
    default void generateDefaultBoosters() {
        generate_all_default_json("boosters", "FACTION_MCMMO", "FACTION_XP");
    }
    default void generateDefaultCustomArmor() {
        generate_all_default_json("custom armor", "DRAGON", "ENGINEER", "KOTH", "PHANTOM", "RANGER", "SUPREME", "TRAVELER", "YETI", "YIJKI");
    }
    default void generateDefaultConquests() {
        generate_all_default_json("conquests", "NORMAL");
    }
    default void generateDefaultCustomBosses() {
        generate_all_default_json("custom bosses", "BROOD_MOTHER", "KING_SLIME", "PLAGUE_BLOATER", "SOUL_REAPER");
    }
    default void generateDefaultCustomEnchants() {
        final String[] mastery = new String[] {
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
                "DIVINE_IMMOLATION",
                "HERO_KILLER",
                "IMMORTAL",
                "NATURES_WRATH",
                "PARADOX", "PHOENIX",
                "SABOTAGE", "SOUL_TRAP",
                "TELEBLOCK",
        };
        final String[] legendary = new String[] {
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
        final String SEPARATOR = File.separator;
        final HashMap<String, String[]> rarity_types = new HashMap<>() {{
            put("MASTERY", mastery);
            put("HEROIC", heroic);
            put("SOUL", soul);
            put("LEGENDARY", legendary);
            put("ULTIMATE", ultimate);
            put("ELITE", elite);
            put("UNIQUE", unique);
            put("SIMPLE", simple);
            put("RANDOM", null);
        }};
        for(Map.Entry<String, String[]> types : rarity_types.entrySet()) {
            final String rarity_type = types.getKey();
            generate_all_default_json("custom enchants" + SEPARATOR + rarity_type, "_settings");
            final String[] enchants = types.getValue();
            if(enchants != null) {
                generate_all_default_json("custom enchants" + SEPARATOR + rarity_type, enchants);
            }
        }
    }
    default void generateDefaultCustomCreepers() {
        generate_all_default_json("custom creepers", "ARCANE", "GIGANTIC", "LUCKY", "STUN", "TACTICAL");
    }
    default void generateDefaultCustomTNT() {
        generate_all_default_json("custom tnt", "GIGANTIC", "LETHAL", "LUCKY", "MIMIC", "TACTICAL");
    }
    default void generateDefaultDuelArenas() {
        generate_all_default_json("duel arenas", "DEMON", "DRAGON", "FORGOTTEN", "ICE", "JUNGLE", "MAGIC", "MONSTER", "PIRATE", "VOID");
    }
    default void generateDefaultEnvoyTiers() {
        generate_all_default_json("envoy tiers", "ELITE", "LEGENDARY", "SIMPLE", "ULTIMATE", "UNIQUE");
    }
    default void generateDefaultFactionQuests() {
        generate_all_default_json("faction quests",
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
        generate_all_default_json("faction upgrades",
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
        generate_all_default_json("fat buckets", "LAVA");
    }
    default void generateDefaultGlobalChallenges() {
        generate_all_default_json("global challenges",
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
        generate_all_default_json("filter categories", "EQUIPMENT", "FOOD", "ORES", "OTHER", "POTION_SUPPLIES", "RAIDING", "SPECIALTY");
    }
    default void generateDefaultInventoryPets() {
        generate_all_default_json("inventory pets",
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
        generate_all_default_json("item skins",
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
        generate_all_default_json("lootboxes",
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
        generate_all_default_json("masks",
                "BUNNY", "DEATH_KNIGHT", "DRAGON", "DUNGEON", "GHOST", "GLITCH", "HEADLESS", "JOKER",
                "LOVER", "MONOPOLY", "NECROMANCER", "PARTY_HAT", "PILGRIM", "PUMPKIN_MONSTER",
                "PURGE", "REINDEER", "RIFT", "SANTA", "SCARECROW", "SPECTRAL", "TURKEY", "ZEUS"
        );
    }
    default void generateDefaultMonthlyCrates() {
        generate_all_default_json("monthly crates",
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
        generate_all_default_json("outposts", "HERO", "SERVONAUT", "TRAINEE", "VANILLA");
    }
    default void generateDefaultPlayerQuests() {
        generate_all_default_json("player quests",
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
        generate_all_default_json("server crates", "ELITE", "GODLY", "LEGENDARY", "SIMPLE", "ULTIMATE", "UNIQUE");
    }
    default void generateDefaultShopCategories() {
        generate_all_default_json("shops",
                "BASE_GRIND",
                "BREWING",
                "BUILDING_BLOCKS",
                "CLAY",
                "FLOWERS",
                "FOOD_AND_FARMING",
                "GLASS",
                "MENU", "MOB_DROPS",
                "ORES_AND_GEMS",
                "POTIONS",
                "RAID",
                "SPAWNERS",
                "SPECIALTY",
                "WOOL"
        );
    }
    default void generateDefaultStrongholds() {
        generate_all_default_json("strongholds", "FROZEN", "INFERNAL");
    }
    default void generateDefaultTitanAttributes() {
        generate_all_default_json("titan attributes", "ATLAS", "KRONOS", "OURANOS");
    }
    default void generateDefaultTrinkets() {
        generate_all_default_json("trinkets", "ANTI_PROJECTILE_FORCEFIELD", "BATTLESTAFF_OF_YIJKI", "EMP_PULSE", "FACTION_BANNER", "PHOENIX_FEATHER", "SOUL_ANVIL", "SOUL_PEARL", "SPEED");
    }
}
