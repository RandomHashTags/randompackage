package me.randomhashtags.randompackage.perms;

public interface EnvoyPermission extends RPPermission {
    String START = PREFIX + "envoy.start";
    String STOP = PREFIX + "envoy.stop";
    String TOGGLE_EDIT_PRESET = PREFIX + "envoy.preset";
    String VIEW_HELP = PREFIX + "envoy.help";
}
