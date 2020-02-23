package me.randomhashtags.randompackage.perms;

public interface WildPvPPermission extends RPPermission {
    String VIEW_QUEUE = PREFIX + "wildpvp.view";
    String VIEW_QUEUE_INVENTORY = PREFIX + "wildpvp.viewinventory";
    String JOIN_QUEUE = PREFIX + "wildpvp.create";
    String LEAVE_QUEUE = PREFIX + "wildpvp.leave";
    String LEAVE_QUEUE_DURING_COUNTDOWN = PREFIX + "wildpvp.leave.countdown";
    String CHALLENGE = PREFIX + "wildpvp.challenge";
}
