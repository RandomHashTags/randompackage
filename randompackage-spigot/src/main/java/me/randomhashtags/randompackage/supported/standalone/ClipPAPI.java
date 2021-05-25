package me.randomhashtags.randompackage.supported.standalone;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.randomhashtags.randompackage.addon.Title;
import me.randomhashtags.randompackage.api.Envoy;
import me.randomhashtags.randompackage.data.CoinFlipData;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.JackpotData;
import me.randomhashtags.randompackage.universal.UVersionable;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClipPAPI extends PlaceholderExpansion implements UVersionable {
    private static ClipPAPI INSTANCE;
    public static ClipPAPI getPAPI() {
        if(INSTANCE == null) INSTANCE = new ClipPAPI();
        return INSTANCE;
    }
    private ClipPAPI() {
        final long started = System.currentTimeMillis();
        register();
        sendConsoleMessage("&6[RandomPackage] &aHooked PlaceholderAPI &e(took " + (System.currentTimeMillis()-started) + "ms)");
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
        return RP_VERSION;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        final UUID uuid = player != null ? player.getUniqueId() : null;
        final FileRPPlayer pdata = uuid != null ? FileRPPlayer.get(uuid) : null;
        if(pdata == null) {
            return "";
        } else if(identifier.startsWith("coinflip_")) {
            final CoinFlipData data = pdata.getCoinFlipData();
            final String[] values = identifier.split("_");
            switch (values[values.length-1]) {
                case "wins": return data.getWins().toString();
                case "won$": return data.getWonCash().toString();
                case "losses": return data.getLosses().toString();
                case "lost$": return data.getLostCash().toString();
                case "notifications": return Boolean.toString(data.receivesNotifications());
                default: return null;
            }
        } else if(identifier.startsWith("title_")) {
            final Title title = pdata.getTitleData().getActive();
            return title != null ? identifier.equals("title_chat") ? " " + title.getChatTitle() : title.getTabTitle() : "";
        } else {
            final JackpotData jackpot = pdata.getJackpotData();
            switch (identifier) {
                case "jackpot_countdown": return Boolean.toString(jackpot.receivesNotifications());
                case "jackpot_wins": return formatNumber(jackpot.getTotalWins(), false);
                case "jackpot_won$": return formatBigDecimal(jackpot.getTotalWonCash());
                case "jackpot_tickets": return formatNumber(jackpot.getTotalTicketsBought(), false);
                case "until_next_envoy":
                    final Envoy e = Envoy.INSTANCE;
                    return e.isEnabled() ? e.getUntilNextNaturalEnvoy() : null;
                default:
                    return null;
            }
        }
    }
}