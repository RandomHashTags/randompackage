package me.randomhashtags.randompackage.utils.objects;

import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Date;

public class Backup extends UVersion {

    public Backup() {
        scheduler.runTaskAsynchronously(randompackage, () -> {
            try {
                final String a = toReadableDate(new Date(), "MMMM-dd-yyyy HH_mm_ss z");
                FileUtils.copyDirectory(randompackage.getDataFolder(), new File(randompackage.getDataFolder().getAbsolutePath() + "_backups", a));
                System.out.println("[RandomPackage] Successfully backed up data to folder \"" + a + "\"!");
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
