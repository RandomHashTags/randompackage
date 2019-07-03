package me.randomhashtags.randompackage.recode.api.addons.usingFile;

import me.randomhashtags.randompackage.recode.api.addons.Title;

public class FileTitle extends Title {
    private String title;
    public FileTitle(String title) {
        this.title = title;
    }
    public void initilize() { addTitle(title, this); }
    public String getTitle() { return title; }
}
