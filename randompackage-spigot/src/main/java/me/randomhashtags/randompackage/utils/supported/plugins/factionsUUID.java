package me.randomhashtags.randompackage.utils.supported.plugins;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.struct.Relation;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class factionsUUID implements Listener {
	private static factionsUUID instance;
	public static final factionsUUID getInstance() {
		if(instance == null) instance = new factionsUUID();
		return instance;
	}
	
	private final FPlayers fi = FPlayers.getInstance();
	private final Factions f = Factions.getInstance();
	private final Board b = Board.getInstance();
	private final HashMap<String, HashMap<String, List<UUID>>> relations = new HashMap<>();
	
	public String getFaction(OfflinePlayer player) { return fi.getByOfflinePlayer(player).getFaction().getTag(); }
	public void sendMessageToAllies(OfflinePlayer player, BaseComponent...components) {
		final String f = fi.getByOfflinePlayer(player).getFaction().getTag();
		final List<UUID> m = getAlliesOf(f);
		if(player.isOnline()) m.add(player.getUniqueId());
		send(m, components);
	}
	public void sendMessageToMembers(OfflinePlayer player, BaseComponent...components) {
		final String f = fi.getByOfflinePlayer(player).getFaction().getTag();
		final List<UUID> m = getMembersOf(f);
		if(player.isOnline()) m.add(player.getUniqueId());
		send(m, components);
	}
	public void sendMessageToEnemies(OfflinePlayer player, BaseComponent...components) {
		final String f = fi.getByOfflinePlayer(player).getFaction().getTag();
		final List<UUID> m = getEnemiesOf(f);
		if(player.isOnline()) m.add(player.getUniqueId());
		send(m, components);
	}
	public void sendMessageToTruces(OfflinePlayer player, BaseComponent...components) {
		final String f = fi.getByOfflinePlayer(player).getFaction().getTag();
		final List<UUID> m = getTrucesOf(f);
		if(player.isOnline()) m.add(player.getUniqueId());
		send(m, components);
	}
	public void sendMessageToNeutrals(OfflinePlayer player, BaseComponent...components) {
		final String f = fi.getByOfflinePlayer(player).getFaction().getTag();
		final List<UUID> m = getNeutralsOf(f);
		if(player.isOnline()) m.add(player.getUniqueId());
		send(m, components);
	}
	public String getChatMode(OfflinePlayer player) { return fi.getByOfflinePlayer(player).getChatMode().name(); }

	private void send(List<UUID> members, BaseComponent...components) {
		for(UUID u : members) {
			final OfflinePlayer op = Bukkit.getOfflinePlayer(u);
			if(op.isOnline())
				op.getPlayer().spigot().sendMessage(components);
		}
	}

	public boolean canModify(Player player, Location blockLocation) {
		final Faction p = fi.getByPlayer(player).getFaction(), f = b.getFactionAt(new FLocation(blockLocation));
		return f.isWilderness() || p != null && p.equals(f);
	}


	public List<UUID> getMembersOf(String faction) { return getMembers(faction, "MEMBERS"); }
	public List<UUID> getEnemiesOf(String faction) { return getMembers(faction, "ENEMIES"); }
	public List<UUID> getAlliesOf(String faction) { return getMembers(faction, "ALLIES"); }
	public List<UUID> getTrucesOf(String faction) { return getMembers(faction, "TRUCES"); }
	public List<UUID> getNeutralsOf(String faction) { return getMembers(faction, "NEUTRAL"); }
	private List<UUID> getMembers(String faction, String TYPE) {
		if(!relations.keySet().contains(faction)) relations.put(faction, new HashMap<>());
		if(relations.get(faction).keySet().contains(TYPE)) {
			return relations.get(faction).get(TYPE);
		} else {
			final List<UUID> members = new ArrayList<>();
			for(FPlayer fp : fi.getAllFPlayers()) {
				final Relation t = fp.getRelationTo(f.getByTag(faction));
				if(TYPE.equals("MEMBERS") && t.isMember()
						|| TYPE.equals("ENEMIES") && t.isEnemy()
						|| TYPE.equals("ALLIES") && t.isAlly()
						|| TYPE.equals("TRUCES") && t.isTruce()
						|| TYPE.equals("NEUTRAL") && t.isNeutral()
				)
					members.add(fp.getPlayer().getUniqueId());
			}
			relations.get(faction).put(TYPE, members);
			return members;
		}
	}

	public String getPlayerRole(Player player) { return fi.getByPlayer(player).getRole().getPrefix(); }
	public ChatColor getRelation(OfflinePlayer player, Player target) { return fi.getByOfflinePlayer(player).getFaction().getColorTo(fi.getByPlayer(target)); }
	public boolean locationIsWarZone(Block block) {
		final Chunk c = block.getLocation().getChunk();
		return f.getWarZone().getAllClaims().toString().contains("[" + block.getWorld().getName() + "," + c.getX() + "," +c.getZ() + "]");
	}
	public boolean isNotWarZoneOrSafeZone(Location l) {
		final Chunk c = l.getChunk();
		return !getWarzoneClaims().contains(c) && !getSafezoneClaims().contains(c);
	}
	public List<Chunk> getWarzoneClaims() {
		List<Chunk> claims = new ArrayList<>();
		for(FLocation l : f.getWarZone().getAllClaims())
			claims.add(l.getWorld().getChunkAt((int) l.getX(), (int) l.getZ()));
		return claims;
	}
	public List<Chunk> getSafezoneClaims() {
		List<Chunk> claims = new ArrayList<>();
		for(FLocation l : f.getSafeZone().getAllClaims())
			claims.add(l.getWorld().getChunkAt((int) l.getX(), (int) l.getZ()));
		return claims;
	}
	public boolean relationIsEnemyOrNull(Player player1, Player player2) {
		final Relation rel = fi.getByPlayer(player1).getRelationTo(fi.getByPlayer(player2));
		return rel == null || rel.equals(Relation.ENEMY);
	}
	public boolean relationIsNeutral(Player player1, Player player2) {
		return fi.getByPlayer(player1).getRelationTo(fi.getByPlayer(player2)).equals(Relation.NEUTRAL);
	}
	public boolean relationIsAlly(Player player1, Player player2) {
		return fi.getByPlayer(player1).getRelationTo(fi.getByPlayer(player2)).equals(Relation.ALLY);
	}
	public boolean relationIsTruce(Player player1, Player player2) {
		return fi.getByPlayer(player1).getRelationTo(fi.getByPlayer(player2)).equals(Relation.TRUCE);
	}
	public boolean relationIsMember(Player player1, Player player2) {
		return fi.getByPlayer(player1).getRelationTo(fi.getByPlayer(player2)).equals(Relation.MEMBER);
	}
	public String getFactionAt(Location l) {
		return ChatColor.stripColor(b.getFactionAt(new FLocation(l)).getTag());
	}

	public void resetPowerBoost(Player player) {
		final Faction f = fi.getByPlayer(player).getFaction();
		if(f != null)
			f.setPowerBoost(0);
	}
	public void increasePower(String factionName, double by) {
		final Faction fa = f.getByTag(factionName);
		if(fa != null) fa.setPowerBoost(fa.getPowerBoost()+by);
	}
	public void setPowerBoost(String factionName, double value) {
		final Faction fa = f.getByTag(factionName);
		if(fa != null) fa.setPowerBoost(value);
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	private void factionDisbandEvent(FactionDisbandEvent event) {
		if(!event.isCancelled()) {
			final me.randomhashtags.randompackage.api.events.FactionDisbandEvent e = new me.randomhashtags.randompackage.api.events.FactionDisbandEvent(event.getPlayer(), event.getFaction().getTag());
			Bukkit.getPluginManager().callEvent(e);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void factinRenameEvent(FactionRenameEvent event) {
		if(!event.isCancelled()) {
			final me.randomhashtags.randompackage.api.events.FactionRenameEvent e = new me.randomhashtags.randompackage.api.events.FactionRenameEvent(event.getPlayer(), event.getOldFactionTag(), event.getFactionTag());
			Bukkit.getPluginManager().callEvent(e);
		}
	}
}