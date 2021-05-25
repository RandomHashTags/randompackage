package me.randomhashtags.randompackage.universal;

public final class CustomSound {
    private USound usound;
    private float volume, pitch;
    public CustomSound(String input) {
        if(input != null) {
            final String[] values = input.split(":");
            usound = USound.match(values[0].toUpperCase());
            volume = Float.parseFloat(values[1]);
            pitch = Float.parseFloat(values[2]);
        }
    }
    public USound getUSound() {
        return usound;
    }
    public float getVolume() {
        return volume;
    }
    public float getPitch() {
        return pitch;
    }
}
