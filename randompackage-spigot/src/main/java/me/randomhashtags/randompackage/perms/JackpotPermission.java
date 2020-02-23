package me.randomhashtags.randompackage.perms;

public interface JackpotPermission extends RPPermission {
    String COMMAND_PICK_WINNER = PREFIX + "jackpot.pickwinner";
    String BUY_TICKETS = PREFIX + "jackpot.buy";
    String VIEW = PREFIX + "jackpot";
    String VIEW_STATS = PREFIX + "jackpot.stats";
    String VIEW_TOP = PREFIX + "jackpot.top";
    String VIEW_HELP = PREFIX + "jackpot.help";
    String TOGGLE_NOTIFICATIONS = PREFIX + "jackpot.toggle";
}
