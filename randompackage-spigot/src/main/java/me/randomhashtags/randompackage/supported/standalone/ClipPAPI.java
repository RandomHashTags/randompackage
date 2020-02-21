package me.randomhashtags.randompackage.supported.standalone;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.randomhashtags.randompackage.addon.Title;
import me.randomhashtags.randompackage.addon.stats.CoinFlipStats;
import me.randomhashtags.randompackage.api.Envoy;
import me.randomhashtags.randompackage.universal.UVersionable;
import me.randomhashtags.randompackage.util.RPPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClipPAPI extends PlaceholderExpansion implements UVersionable {
    private static ClipPAPI instance;
    public static ClipPAPI getPAPI() {
        if(instance == null) instance = new ClipPAPI();
        return instance;
    }
    private ClipPAPI() {
        final long started = System.currentTimeMillis();
        register();
        sendConsoleMessage("&6[RandomPackage] &aHooked PlaceholderAPI &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public String getIdentifier() {
        return "randompackage";
    }
    public String getAuthor() {
        return "RandomHashTags";
    }
    public String getVersion() {
        return RP_VERSION;
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
            else if(identifier.endsWith("_notifications")) return Boolean.toString(pdata.doesReceiveCoinFlipNotifications());
        } else if(identifier.startsWith("title_")) {
            final Title t = pdata.getActiveTitle();
            return t != null ? identifier.equals("title_chat") ? " " + t.getChatTitle() : t.getTabTitle() : "";
        } else {
            switch (identifier) {
                case "jackpot_countdown": return Boolean.toString(pdata.doesReceiveJackpotNotifications());
                case "jackpot_wins": return Integer.toString(pdata.jackpotWins);
                case "jackpot_won$": return formatBigDecimal(pdata.jackpotWonCash);
                case "jackpot_tickets": return formatBigDecimal(pdata.jackpotTickets);
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