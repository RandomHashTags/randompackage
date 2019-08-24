package me.randomhashtags.randompackage.utils.supported.standalone;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.addons.Title;
import me.randomhashtags.randompackage.api.Envoy;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.addons.objects.CoinFlipStats;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class PAPI extends PlaceholderExpansion {
    private static PAPI instance;
    public static PAPI getPAPI() {
        if(instance == null) instance = new PAPI();
        return instance;
    }
    private PAPI() {
        api.sendConsoleMessage("&6[RandomPackage] &aHooked PlaceholderAPI");
        register();
    }

    @Override
    public String getIdentifier() {
        return "randompackage";
    }

    @Override
    public String getAuthor() {
        return "RandomHashTags";
    }

    @Override
    public String getVersion() {
        return RandomPackage.getPlugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        final UUID u = player != null ? player.getUniqueId() : null;
        final RPPlayer pdata = u != null ? RPPlayer.get(u) : null;
        if(pdata == null) {
            return "";
        } else if(identifier.startsWith("coinflip_")) {
            final CoinFlipStats s = pdata.getCoinFlipStats();
            if(identifier.endsWith("_wins")) return s.wins.toString();
            else if(identifier.endsWith("_won$")) return s.wonCash.toString();
            else if(identifier.endsWith("_losses")) return s.losses.toString();
            else if(identifier.endsWith("_lost$")) return s.lostCash.toString();
            else if(identifier.endsWith("_notifications")) return Boolean.toString(pdata.coinflipNotifications);
        } else if(identifier.startsWith("title_")) {
            final Title t = pdata.getActiveTitle();
            return t != null ? identifier.equals("title_chat") ? " " + t.getChatTitle() : t.getTabTitle() : "";
        } else {
            switch (identifier) {
                case "jackpot_countdown": return Boolean.toString(pdata.jackpotCountdown);
                case "jackpot_wins": return Integer.toString(pdata.jackpotWins);
                case "jackpot_won$": return api.formatBigDecimal(pdata.jackpotWonCash);
                case "jackpot_tickets": return api.formatBigDecimal(pdata.jackpotTickets);
                case "until_next_envoy":
                    final Envoy e = Envoy.getEnvoy();
                    return e.isEnabled() ? e.getUntilNextNaturalEnvoy() : null;
                default:
                    return null;
            }
        }
        return null;
    }
}