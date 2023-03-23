package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.DuelArena;
import me.randomhashtags.randompackage.addon.dev.enums.DuelEndReason;
import me.randomhashtags.randompackage.dev.duels.DuelSetting;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public final class ActiveDuel {
    private Location requesterAcceptLocation, accepterAcceptLocation;
    private PlayerInventory requesterAcceptInventory, accepterAcceptInventory;
    private double requesterHP, accepterHP;
    private Collection<PotionEffect> requesterPEs, accepterPEs;
    private final Player requester;
    private final Player accepter;
    private final List<DuelSetting> settings;
    private final DuelArena arena;

    public ActiveDuel(Player requester, Player accepter, List<DuelSetting> settings, DuelArena arena) {
        this.requester = requester;
        this.accepter = accepter;
        this.settings = settings;
        this.arena = arena;
        start();
    }
    public Player getRequester() {
        return requester;
    }
    public Player getAccepter() {
        return accepter;
    }
    public List<DuelSetting> getSettings() {
        return settings;
    }
    public DuelArena getArena() {
        return arena;
    }

    private void start() {
        requesterAcceptLocation = requester.getLocation();
        accepterAcceptLocation = accepter.getLocation();

        requesterAcceptInventory = requester.getInventory();
        accepterAcceptInventory = accepter.getInventory();

        requesterHP = requester.getHealth();
        accepterHP = accepter.getHealth();

        requesterPEs = requester.getActivePotionEffects();
        accepterPEs = accepter.getActivePotionEffects();

        removePotionEffects(requester);
        removePotionEffects(accepter);
    }

    private void removePotionEffects(@NotNull Player player) {
        for(PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }
    }
    private void resetPotionEffects(@NotNull Player player, Collection<PotionEffect> previous) {
        removePotionEffects(player);
        for(PotionEffect pe : previous) {
            player.addPotionEffect(pe);
        }
    }
    private void setPreviousContents() {
        requester.getInventory().setContents(requesterAcceptInventory.getContents());
        requester.updateInventory();
        requester.setHealth(requesterHP);
        resetPotionEffects(requester, requesterPEs);

        accepter.getInventory().setContents(requesterAcceptInventory.getContents());
        accepter.updateInventory();
        accepter.setHealth(accepterHP);
        resetPotionEffects(accepter, accepterPEs);
    }

    public void end(@NotNull DuelEndReason reason) {
        List<String> msg = null;
        switch (reason) {
            case CHOOSE_WINNER:
            case PLAYER_LEFT_CMD:
            case PLAYER_LEFT_QUIT:
            default:
                setPreviousContents();
                final PlayerTeleportEvent.TeleportCause cause = PlayerTeleportEvent.TeleportCause.PLUGIN;
                requester.teleport(requesterAcceptLocation, cause);
                accepter.teleport(accepterAcceptLocation, cause);
        }
    }
}
