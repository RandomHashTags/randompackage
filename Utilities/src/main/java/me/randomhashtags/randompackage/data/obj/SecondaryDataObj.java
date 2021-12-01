package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.SecondaryData;

public final class SecondaryDataObj implements SecondaryData {

    private long xpExhaustionExpiration;

    public SecondaryDataObj(long xpExhaustionExpiration) {
        this.xpExhaustionExpiration = xpExhaustionExpiration;
    }

    @Override
    public boolean isXPExhausted() {
        return xpExhaustionExpiration < System.currentTimeMillis();
    }

    @Override
    public long getXPExhaustionExpiration() {
        return xpExhaustionExpiration;
    }

    @Override
    public void setXPExhaustionExpiration(long expiration) {
        this.xpExhaustionExpiration = expiration;
    }
}
