package me.randomhashtags.randompackage.util.obj;

import me.randomhashtags.randompackage.util.universal.UVersion;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Date;

public final class Backup extends UVersion {
    public Backup() {
        final String folder = dataFolder.getAbsolutePath() + "_backups";
        final File[] total = new File(folder).listFiles();
        if(total != null && total.length == 10) {
            total[0].delete();
        }
        scheduler.runTaskAsynchronously(randompackage, () -> {
            try {
                final String a = toReadableDate(new Date(), "MMMM-dd-yyyy HH_mm_ss z");
                FileUtils.copyDirectory(dataFolder, new File(folder, a));
                System.out.println("[RandomPackage] Successfully backed up data to folder \"" + a + "\"!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
