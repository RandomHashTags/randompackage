package me.randomhashtags.randompackage.recode.classes;

import me.randomhashtags.randompackage.recode.utils.AbstractBooster;

public class Booster extends AbstractBooster {

    public void initilize() {
        addBooster(getYamlName(), this);
    }
}
