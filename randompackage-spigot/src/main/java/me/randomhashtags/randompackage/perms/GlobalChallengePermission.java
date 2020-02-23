package me.randomhashtags.randompackage.perms;

public interface GlobalChallengePermission extends RPPermission {
    String COMMAND_STOP_CHALLENGE = PREFIX + "globalchallenges.stop";
    String COMMAND_RELOAD = PREFIX + "globalchallenges.reload";
    String COMMAND_GIVE_PRIZE = PREFIX + "globalchallenges.giveprize";
    String VIEW_PRIZES = PREFIX + "globalchallenges.claim";
    String VIEW = PREFIX + "globalchallenges";
}
