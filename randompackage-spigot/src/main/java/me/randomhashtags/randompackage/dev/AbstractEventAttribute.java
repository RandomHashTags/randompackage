package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.utils.RPStorage;

public abstract class AbstractEventAttribute extends RPStorage implements EventAttribute {
    private boolean cancelled;
    public void load() { addEventAttribute(getIdentifier(), this); }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
