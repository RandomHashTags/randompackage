package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.ArmorSocket;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public final class PathArmorSocket extends RPAddonSpigot implements ArmorSocket {
    private final String identifier;
    public PathArmorSocket(String identifier) {
        this.identifier = identifier;
        register(Feature.ARMOR_SOCKET, this);
    }
    @NotNull
    @Override
    public String getIdentifier() {
        return identifier;
    }

    private YamlConfiguration getArmorSocketsConfig() {
        return getRPConfig(null, "armor sockets.yml");
    }
    public String getName() {
        return getString(getArmorSocketsConfig(), "types." + identifier + ".name");
    }
    public String getItemType() {
        return getString(getArmorSocketsConfig(), "types." + identifier + ".item type");
    }
    public int getLimit() {
        return getArmorSocketsConfig().getInt("types." + identifier + ".limit");
    }
}
