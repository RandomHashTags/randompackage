package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActiveGlobalChallenge;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.math.BigDecimal;

public class GlobalChallengeParticipateEvent extends RPEventCancellable {
	public final Event event;
	public final ActiveGlobalChallenge challenge;
	public BigDecimal value;
	public GlobalChallengeParticipateEvent(Event event, Player player, ActiveGlobalChallenge challenge, BigDecimal value) {
		super(player);
		this.challenge = challenge;
		this.event = event;
		this.value = value;
	}
}