package me.randomhashtags.randompackage.addon.obj;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

public final class FactionTopInfo {
    private UUID richestMember;
    private BigInteger factionPoints;
    private final BigInteger mobSpawners;
    private BigInteger kothWins;
    private BigInteger raidEventWins;
    private BigDecimal factionWealth, memberWealth, spawnerValue, blockValue, factionUpgrades;
    public FactionTopInfo(UUID richestMember, BigInteger factionPoints, BigDecimal factionWealth, BigDecimal spawnerValue, BigInteger mobSpawners, BigDecimal blockValue, BigDecimal factionUpgrades, BigInteger kothWins, BigInteger raidEventWins) {
        this.richestMember = richestMember;
        this.factionPoints = factionPoints;
        this.factionWealth = factionWealth;
        this.spawnerValue = spawnerValue;
        this.mobSpawners = mobSpawners;
        this.blockValue = blockValue;
        this.factionUpgrades = factionUpgrades;
        this.kothWins = kothWins;
        this.raidEventWins = raidEventWins;
    }
    public UUID getRichestMember() {
        return richestMember;
    }
    public void setRichestMember(UUID richestMember) {
        this.richestMember = richestMember;
    }

    public BigInteger getFactionPoints() {
        return factionPoints;
    }
    public void setFactionPoints(BigInteger factionPoints) {
        this.factionPoints = factionPoints;
    }

    public BigDecimal getFactionWealth() {
        return factionWealth;
    }
    public void setFactionWealth(BigDecimal factionWealth) {
        this.factionWealth = factionWealth;
    }

    public BigDecimal getMemberWealth() {
        return memberWealth;
    }
    public void setMemberWealth(BigDecimal memberWealth) {
        this.memberWealth = memberWealth;
    }

    public BigDecimal getSpawnerValue() {
        return spawnerValue;
    }
    public void setSpawnerValue(BigDecimal spawnerValue) {
        this.spawnerValue = spawnerValue;
    }

    public BigDecimal getBlockValue() {
        return blockValue;
    }
    public void setBlockValue(BigDecimal blockValue) {
        this.blockValue = blockValue;
    }

    public BigDecimal getFactionUpgrades() {
        return factionUpgrades;
    }
    public void setFactionUpgrades(BigDecimal factionUpgrades) {
        this.factionUpgrades = factionUpgrades;
    }

    public BigInteger getKOTHWins() {
        return kothWins;
    }
    public void setKOTHWins(BigInteger kothWins) {
        this.kothWins = kothWins;
    }

    public BigInteger getRaidEventWins() {
        return raidEventWins;
    }
    public void setRaidEventWins(BigInteger raidEventWins) {
        this.raidEventWins = raidEventWins;
    }
}
