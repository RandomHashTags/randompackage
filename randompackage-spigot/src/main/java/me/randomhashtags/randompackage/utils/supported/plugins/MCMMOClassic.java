package me.randomhashtags.randompackage.utils.supported.plugins;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.randomhashtags.randompackage.events.MCMMOXpGainEvent;
import me.randomhashtags.randompackage.utils.supported.mechanics.MCMMOAPI;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;

public class MCMMOClassic extends MCMMOAPI {
    private static MCMMOClassic instance;
    public static MCMMOClassic getMCMMOClassic() {
        if(instance == null) instance = new MCMMOClassic();
        return instance;
    }

    private boolean isEnabled = false;

    public void enable() {
        if(isEnabled) return;
        isEnabled = true;
        pluginmanager.registerEvents(this, randompackage);
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        HandlerList.unregisterAll(this);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    private void mcmmoPlayerXpGainEvent(McMMOPlayerXpGainEvent event) {
        if(!event.isCancelled()) {
            final MCMMOXpGainEvent m = new MCMMOXpGainEvent(event.getPlayer(), event.getSkill(), event.getRawXpGained());
            pluginmanager.callEvent(m);
            if(!m.isCancelled()) {
                event.setRawXpGained(m.xp);
            }
        }
    }

    public String valueOf(String input, String o) {
        for(SkillType type : SkillType.values()) {
            if(input.equals(o.replace("{SKILL}", "" + getSkillName(type)))) {
                return type.name();
            }
        }
        return null;
    }
    public SkillType getSkill(String skillname) {
        for(SkillType type : SkillType.values()) {
            final String s = itemsConfig.getString("mcmmo vouchers.skill names." + type.name().toLowerCase().replace("_skills", ""));
            if(s != null && skillname.equalsIgnoreCase(ChatColor.stripColor(s)) || skillname.equalsIgnoreCase(type.name())) return type;
        }
        return null;
    }
    public SkillType getRandomSkill() {
        final SkillType[] a = SkillType.values();
        return a[random.nextInt(a.length)];
    }
    public String getSkillName(SkillType skilltype) {
        final String a = itemsConfig.getString("mcmmo vouchers.skill names." + skilltype.name().toLowerCase().replace("_skills", ""));
        return a != null ? ChatColor.translateAlternateColorCodes('&', a) : null;
    }
}
