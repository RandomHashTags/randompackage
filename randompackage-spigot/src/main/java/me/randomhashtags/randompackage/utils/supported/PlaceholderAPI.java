package me.randomhashtags.randompackage.utils.supported;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.utils.RPPlayer;
import me.randomhashtags.randompackage.addons.usingfile.FileTitle;
import me.randomhashtags.randompackage.addons.objects.CoinFlipStats;
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
            if(identifier.endsWith("_wins")) return s.wins.toString();
            else if(identifier.endsWith("_won$")) return s.wonCash.toString();
            else if(identifier.endsWith("_losses")) return s.losses.toString();
            else if(identifier.endsWith("_lost$")) return s.lostCash.toString();
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
            final FileTitle t = pdata.getActiveTitle();
            return t != null ? identifier.equals("title_chat") ? " " + t.getChatTitle() : t.getTabTitle() : "";
        }
        return null;
    }
}