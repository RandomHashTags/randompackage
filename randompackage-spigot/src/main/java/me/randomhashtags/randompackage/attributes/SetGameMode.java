package me.randomhashtags.randompackage.attributes;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetGameMode extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                ((Player) e).setGameMode(GameMode.valueOf(recipientValues.get(e).toUpperCase()));
            }
        }
    }
}
