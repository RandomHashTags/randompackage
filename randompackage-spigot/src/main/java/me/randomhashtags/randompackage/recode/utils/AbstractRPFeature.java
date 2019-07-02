package me.randomhashtags.randompackage.recode.utils;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.recode.RPStorage;

import java.util.Random;

public abstract class AbstractRPFeature extends RPStorage {
    protected static RandomPackageAPI api = RandomPackageAPI.api;
    protected Random random = new Random();

    public abstract String getIdentifier();
    public abstract void initilize();
}
