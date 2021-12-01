package me.randomhashtags.randompackage.perms;

public interface CoinFlipPermission extends RPPermission {
    String VIEW_MATCHES = PREFIX + "coinflip.view";
    String VIEW_STATS = PREFIX + "coinflip.stats";
    String TOGGLE_NOTIFICATIONS = PREFIX + "coinflip.toggle";
    String VIEW_HELP = PREFIX + "coinflip.help";
    String CREATE_MATCH = PREFIX + "coinflip.create";
    String CANCEL_MATCH = PREFIX + "coinflip.cancel";
    String CHALLENGE_MATCH = PREFIX + "coinflip.challenge";
}
