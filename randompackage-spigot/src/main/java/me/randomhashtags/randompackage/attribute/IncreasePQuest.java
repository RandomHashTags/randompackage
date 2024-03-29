package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.event.PlayerQuestCompleteEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public final class IncreasePQuest extends AbstractEventAttribute {
    @Override
    public void executeData(@NotNull HashMap<String, Entity> entities, @NotNull HashMap<RPPlayer, String> recipientValues, @NotNull HashMap<String, String> valueReplacements) {
        final List<String> msg = getRPConfig("player quests", "_settings.yml").getStringList("messages.completed");
        for(RPPlayer pdata : recipientValues.keySet()) {
            final String value = replaceValue(entities, recipientValues.get(pdata), valueReplacements);
            if(value != null) {
                final HashMap<PlayerQuest, ActivePlayerQuest> quests = pdata.getPlayerQuestData().getQuests();
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
                                PLUGIN_MANAGER.callEvent(e);
                                final HashMap<String, String> replacements = new HashMap<>();
                                replacements.put("{NAME}", getLocalizedName(q));
                                sendStringListMessage(player, msg, replacements);
                            }
                        }
                    }
                }
            }
        }
    }
}
