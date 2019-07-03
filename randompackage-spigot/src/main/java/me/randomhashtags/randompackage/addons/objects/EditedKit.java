package me.randomhashtags.randompackage.addons.objects;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class EditedKit {

    public static HashMap<Player, EditedKit> editing;

    public Player player;
    public List<KitItem> original, edited;
    public int selected = -1;
    public EditedKit(Player player, List<KitItem> original, List<KitItem> edited) {
        if(editing == null) {
            editing = new HashMap<>();
        }
        this.player = player;
        this.original = original;
        this.edited = edited;
        editing.put(player, this);
    }
    public void delete() {
        editing.remove(player);
        player = null;
        original = null;
        edited = null;
        selected = 0;
        if(editing.isEmpty()) {
            editing = null;
        }
    }
}
