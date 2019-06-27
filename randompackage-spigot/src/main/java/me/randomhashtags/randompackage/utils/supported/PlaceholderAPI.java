package me.randomhashtags.randompackage.utils.supported;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.utils.classes.Title;
import me.randomhashtags.randompackage.utils.classes.coinflip.CoinFlipStats;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlaceholderAPI extends PlaceholderExpansion {

    public PlaceholderAPI() {}

    @Override
    public String getIdentifier() {
        return "randompackage";
    }

    @Override
    public String getAuthor() {
        return "RandomHashTags & GMatrixGames";
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
            if(identifier.endsWith("_wins")) return Long.toString(s.wins);
            else if(identifier.endsWith("_won$")) return Long.toString(s.wonCash);
            else if(identifier.endsWith("_losses")) return Long.toString(s.losses);
            else if(identifier.endsWith("_lost$")) return Long.toString(s.lostCash);
        } else if(identifier.equals("coinflip_notifications")) {
            return Boolean.toString(pdata.coinflipNotifications);

        } else if(identifier.equals("jackpot_countdown")) {
            return Boolean.toString(pdata.jackpotCountdown);
        } else if(identifier.equals("jackpot_wins")) {
            return Integer.toString(pdata.jackpotWins);
        } else if(identifier.equals("jackpot_won$")) {
            return Long.toString(pdata.jackpotWonCash);
        } else if(identifier.equals("jackpot_tickets")) {
            return Integer.toString(pdata.jackpotTickets);

        } else if(identifier.startsWith("title_")) {
            final Title t = pdata.getActiveTitle();
            return t != null ? identifier.equals("title_chat") ? " " + t.getChatTitle() : t.getTabTitle() : "";
        }
        return null;
    }
}