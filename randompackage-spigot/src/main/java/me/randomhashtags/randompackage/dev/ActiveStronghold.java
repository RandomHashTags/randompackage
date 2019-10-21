package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.util.obj.PolyBoundary;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;

public class ActiveStronghold {
    private String controller, attacking;
    private float capturePercent;
    private HashMap<Location, Integer> blockDurability;
    private List<PolyBoundary> repairableWalls;
    public ActiveStronghold(HashMap<Location, Integer> blockDurability, List<PolyBoundary> repairableWalls) {
        this.blockDurability = blockDurability;
        this.repairableWalls = repairableWalls;
    }
    public String getController() { return controller; }
    public void setController(String controller) { this.controller = controller; }
    public String getAttacking() { return attacking; }
    public void setAttacking(String attacking) { this.attacking = attacking; }

    public float getCapturePercent() { return capturePercent; }
    public void setCapturePercent(float percent) { capturePercent = percent; }

    public HashMap<Location, Integer> getBlockDurability() { return blockDurability; }
    public List<PolyBoundary> getRepairableWalls() { return repairableWalls; }
}
