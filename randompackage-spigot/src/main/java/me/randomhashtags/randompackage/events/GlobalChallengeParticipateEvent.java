package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActiveGlobalChallenge;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.math.BigDecimal;

public class GlobalChallengeParticipateEvent extends AbstractCancellable {
	public final Event event;
	public final Player player;
	public final ActiveGlobalChallenge challenge;
	public BigDecimal value;
	public GlobalChallengeParticipateEvent(Event event, Player player, ActiveGlobalChallenge challenge, BigDecimal value) {
		this.challenge = challenge;
		this.player = player;
		this.event = event;
		this.value = value;
	}
}