package me.randomhashtags.randompackage.data;

public interface SecondaryData {
    boolean isXPExhausted();
    long getXPExhaustionExpiration();
    void setXPExhaustionExpiration(long expiration);
}
