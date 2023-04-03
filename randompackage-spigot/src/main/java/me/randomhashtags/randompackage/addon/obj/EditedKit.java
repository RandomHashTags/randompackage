package me.randomhashtags.randompackage.addon.obj;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public final class EditedKit {
    public static HashMap<Player, EditedKit> EDITING;

    public Player player;
    public List<KitItem> original, edited;
    public int selected = -1;
    public EditedKit(Player player, List<KitItem> original, List<KitItem> edited) {
        if(EDITING == null) {
            EDITING = new HashMap<>();
        }
        this.player = player;
        this.original = original;
        this.edited = edited;
        EDITING.put(player, this);
    }
    public void delete() {
        EDITING.remove(player);
        player = null;
        original = null;
        edited = null;
        selected = 0;
        if(EDITING.isEmpty()) {
            EDITING = null;
        }
    }
}
