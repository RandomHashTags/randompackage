package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public enum Reputation implements RPFeatureSpigot {
    INSTANCE;

    public YamlConfiguration config;

    @Override
    public String getIdentifier() {
        return "REPUTATION";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "reputation.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "reputation.yml"));
        sendConsoleMessage("&6[RandomPackage] &aLoaded Reputation &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }

    public BigDecimal getReputationPoints(@NotNull Player player) {
        return getReputationPoints(player.getUniqueId());
    }
    public BigDecimal getReputationPoints(@NotNull UUID player) {
        return FileRPPlayer.get(player).getReputationData().getPoints();
    }

    public void view(@NotNull CommandSender sender, @Nullable String target) {
        final boolean isSelf = target == null;
        if(hasPermission(sender, "RandomPackage.reputation.view" + (isSelf ? "" : ".other"), true)) {
            final List<String> msg;
            final BigInteger level;
            final HashMap<String, String> replacements = new HashMap<>();
            if(isSelf || target.equals(sender.getName())) {
                if(!(sender instanceof Player)) return;
                msg = getStringList(config, "messages.view");
                level = getReputationPoints(((Player) sender).getUniqueId()).toBigInteger();
            } else {
                msg = getStringList(config, "messages.view player");
                replacements.put("{TARGET}", target);
                final OfflinePlayer op = Bukkit.getOfflinePlayer(target);
                level = getReputationPoints(op.getUniqueId()).toBigInteger();
            }
            replacements.put("{LEVEL}", formatNumber(level, false));
            sendStringListMessage(sender, msg, replacements);
        }
    }
}
