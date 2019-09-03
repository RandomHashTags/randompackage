package me.randomhashtags.randompackage.attributes;

import me.randomhashtags.randompackage.addons.PlayerQuest;
import me.randomhashtags.randompackage.addons.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.events.PlayerQuestCompleteEvent;
import me.randomhashtags.randompackage.utils.RPPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class IncreasePlayerQuest extends AbstractEventAttribute {
    @Override
    public void executeData(HashMap<RPPlayer, String> recipientValues) {
        final List<String> msg = getRPConfig(null, "player quests.yml").getStringList("messages.completed");
        for(RPPlayer pdata : recipientValues.keySet()) {
            final String value = recipientValues.get(pdata);
            if(value != null) {
                final HashMap<PlayerQuest, ActivePlayerQuest> quests = pdata.getQuests();
                if(quests != null && !quests.isEmpty()) {
                    final String[] values = value.split(":");
                    final PlayerQuest q = getPlayerQuest(values[0]);
                    if(q != null && quests.containsKey(q)) {
                        final ActivePlayerQuest quest = quests.get(q);
                        if(!quest.isExpired() && !quest.isCompleted()) {
                            quest.setProgress(quest.getProgress()+evaluate(values[1]));
                            final Player player = pdata.getOfflinePlayer().getPlayer();
                            final double timer = q.getTimedCompletion();
                            if(timer > 0.00) {
                            } else if(quest.getProgress() >= Double.parseDouble(q.getCompletion())) {
                                quest.setCompleted(true);
                                final PlayerQuestCompleteEvent e = new PlayerQuestCompleteEvent(player, quest);
                                pluginmanager.callEvent(e);
                                final HashMap<String, String> replacements = new HashMap<>();
                                replacements.put("{NAME}", q.getName());
                                sendStringListMessage(player, msg, replacements);
                            }
                        }
                    }
                }
            }
        }
    }
}
