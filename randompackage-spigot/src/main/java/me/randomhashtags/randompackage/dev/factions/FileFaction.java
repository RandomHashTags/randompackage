package me.randomhashtags.randompackage.dev.factions;

import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileFaction extends RPAddonSpigot implements Faction {
    public static HashMap<UUID, FileFaction> FACTIONS = new HashMap<>();

    private boolean isLoaded;
    private UUID uuid;
    private FactionBankObj bank;
    private List<Chunk> claims;
    private HashMap<FPlayer, FactionRole> members;
    private HashMap<String, Boolean> fsettings;
    private HashMap<FPlayer, HashMap<String, Boolean>> psettings;
    private List<UUID> banned;

    public FileFaction(@NotNull File f) {
        load(f);
    }
    public FileFaction(@NotNull String tag) {
        this.uuid = UUID.randomUUID();
        final File folder = new File(DATA_FOLDER + SEPARATOR + "_Data" + SEPARATOR + "factions");
        final File f = new File(folder, uuid.toString() + ".yml");
        try {
            if(!folder.exists()) {
                folder.mkdirs();
            }
            f.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        load(f);

        isLoaded = true;
        yml.set("info.creation time", System.currentTimeMillis());
        yml.set("info.tag", tag);
        yml.set("info.description", "&eDefault faction description :(");
        save();

        FACTIONS.put(uuid, this);
    }

    @Override
    public void load() {
        if(!isLoaded) {
            isLoaded = true;
        }
    }
    @Override
    public void unload() {
        if(isLoaded) {
            isLoaded = false;
            backup();
        }
    }

    @Override
    public void backup() {
        final List<String> claimz = new ArrayList<>();
        for(Chunk c : getClaims()) {
            claimz.add(toString(c.getBlock(0, 0, 0).getLocation()));
        }
        yml.set("claims", claimz);

        yml.set("info.bank.balance", getBank().getBalance().doubleValue());

        final HashMap<String, Boolean> fsettings = getFactionSettings();
        for(String s : fsettings.keySet()) {
            yml.set("fsettings." + s, fsettings.get(s));
        }

        final HashMap<FPlayer, HashMap<String, Boolean>> psettings = getPlayerSettings();
        for(FPlayer player : members.keySet()) {
            final String path = "members." + player.getUUID().toString() + ".";
            yml.set(path + "joined at", player.getJoinedFactionTime());
            yml.set(path + "role", members.get(player).getIdentifier());

            final HashMap<String, Boolean> settings = psettings.get(player);
            for(String s : settings.keySet()) {
                yml.set(path + "settings." + s, settings.get(s));
            }
        }

        save();
    }

    @Override
    public String getIdentifier() {
        return uuid.toString();
    }

    public UUID getUUID() {
        if(uuid == null) uuid = UUID.fromString(getYamlName());
        return uuid;
    }
    public long getCreationTime() { return yml.getLong("info.creation time"); }
    public Location getHome() { return toLocation(yml.getString("info.home")); }
    public String getTag() { return yml.getString("info.tag"); }
    public String getDescription() { return yml.getString("info.description"); }

    public List<Chunk> getClaims() {
        if(claims == null) {
            claims = new ArrayList<>();
            for(String s : yml.getStringList("claims")) {
                claims.add(toLocation(s).getChunk());
            }
        }
        return claims;
    }
    public HashMap<FPlayer, FactionRole> getMembers() {
        if(members == null) {
            members = new HashMap<>();
            for(String s : yml.getConfigurationSection("members").getKeys(false)) {
            }
        }
        return members;
    }
    public FactionBank getBank() {
        if(bank == null) {
            bank = new FactionBankObj(BigDecimal.valueOf(yml.getDouble("info.bank.balance")));
        }
        return null;
    }
    public HashMap<Relation, FactionWarp> getWarps() { return null; }

    public HashMap<String, Boolean> getFactionSettings() {
        if(fsettings == null) {
            fsettings = new HashMap<>();
            for(String s : yml.getConfigurationSection("fsettings").getKeys(false)) {
                fsettings.put(s, yml.getBoolean("fsettings." + s));
            }
        }
        return fsettings;
    }

    public HashMap<FPlayer, HashMap<String, Boolean>> getPlayerSettings() {
        if(psettings == null) {
            psettings = new HashMap<>();
            for(FPlayer player : getMembers().keySet()) {
                psettings.put(player, new HashMap<>());
                final HashMap<String, Boolean> settings = psettings.get(player);
                final String path = "members." + player.getUUID().toString() + ".settings";
                for(String s : yml.getConfigurationSection(path).getKeys(false)) {
                    settings.put(s, yml.getBoolean(path + "." + s));
                }
            }
        }
        return psettings;
    }

    public List<UUID> getBanned() {
        if(banned == null) {
            banned = new ArrayList<>();
            for(String s : yml.getStringList("banned")) {
                banned.add(UUID.fromString(s));
            }
        }
        return banned;
    }

    public HashMap<UUID, Relationship> getRelations() { return null; }
}
