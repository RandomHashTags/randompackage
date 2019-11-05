package me.randomhashtags.randompackage.util.obj;

import me.randomhashtags.randompackage.addon.util.Identifiable;

import java.util.List;

public class CustomCommand implements Identifiable {
    private String identifier;
    private List<String> aliases;

    public CustomCommand(String identifier, List<String> aliases) {
        this.identifier = identifier;
        this.aliases = aliases;
    }

    public String getIdentifier() { return identifier; }

    public List<String> getAliases() { return aliases; }
}
