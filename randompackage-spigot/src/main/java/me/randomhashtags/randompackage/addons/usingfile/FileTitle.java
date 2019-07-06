package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.Title;

public class FileTitle extends Title {
    private String title;
    public FileTitle(String title) {
        this.title = title;
        addTitle(title, this);
    }
    public String getIdentifier() { return title; }
}
