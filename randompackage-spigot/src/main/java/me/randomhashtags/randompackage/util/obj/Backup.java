package me.randomhashtags.randompackage.util.obj;

import me.randomhashtags.randompackage.universal.UVersionableSpigot;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class Backup implements UVersionableSpigot {
    public Backup() {
        final String folder = DATA_FOLDER.getAbsolutePath() + "_backups";
        final File[] total = new File(folder).listFiles();
        if(total != null && total.length == 10) {
            total[0].delete();
        }
        SCHEDULER.runTaskAsynchronously(RANDOM_PACKAGE, () -> {
            try {
                final String a = toReadableDate(new Date(), "MMMM-dd-yyyy HH_mm_ss z");
                copyDirectory(DATA_FOLDER, new File(folder, a));
                System.out.println("[RandomPackage] Successfully backed up data to folder \"" + a + "\"!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /*
        Code below is taken from package "org.apache.commons.io" in class "FileUtils" from library "Spigot.jar" to support other bukkit versions (like PaperSpigot)
     */

    private void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        } else {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            FileChannel input = null;
            FileChannel output = null;

            long srcLen;
            long dstLen;
            try {
                fis = new FileInputStream(srcFile);
                fos = new FileOutputStream(destFile);
                input = fis.getChannel();
                output = fos.getChannel();
                srcLen = input.size();
                dstLen = 0L;

                long bytesCopied;
                for(long count = 0L; dstLen < srcLen; dstLen += bytesCopied) {
                    long remain = srcLen - dstLen;
                    count = Math.min(remain, 31457280L);
                    bytesCopied = output.transferFrom(input, dstLen, count);
                    if (bytesCopied == 0L) {
                        break;
                    }
                }
            } finally {
                closeQuietly(output, fos, input, fis);
            }

            srcLen = srcFile.length();
            dstLen = destFile.length();
            if (srcLen != dstLen) {
                throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "' Expected length: " + srcLen + " Actual: " + dstLen);
            } else {
                if (preserveFileDate) {
                    destFile.setLastModified(srcFile.lastModified());
                }
            }
        }
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException var2) {
        }
    }

    private void closeQuietly(Closeable... closeables) {
        if (closeables != null) {
            Closeable[] arr$ = closeables;
            int len$ = closeables.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Closeable closeable = arr$[i$];
                closeQuietly(closeable);
            }
        }
    }

    private void copyDirectory(File srcDir, File destDir) throws IOException {
        copyDirectory(srcDir, destDir, true);
    }

    private void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }

    private void copyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate) throws IOException {
        checkFileRequirements(srcDir, destDir);
        if (!srcDir.isDirectory()) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        } else if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        } else {
            List<String> exclusionList = null;
            if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
                File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
                if (srcFiles != null && srcFiles.length > 0) {
                    exclusionList = new ArrayList(srcFiles.length);
                    File[] arr$ = srcFiles;
                    int len$ = srcFiles.length;

                    for(int i$ = 0; i$ < len$; ++i$) {
                        File srcFile = arr$[i$];
                        File copiedFile = new File(destDir, srcFile.getName());
                        exclusionList.add(copiedFile.getCanonicalPath());
                    }
                }
            }

            doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
        }
    }

    private void checkFileRequirements(File src, File dest) throws FileNotFoundException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        } else if (dest == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (!src.exists()) {
            throw new FileNotFoundException("Source '" + src + "' does not exist");
        }
    }

    private void doCopyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate, List<String> exclusionList) throws IOException {
        File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
        if (srcFiles == null) {
            throw new IOException("Failed to list contents of " + srcDir);
        } else {
            if (destDir.exists()) {
                if (!destDir.isDirectory()) {
                    throw new IOException("Destination '" + destDir + "' exists but is not a directory");
                }
            } else if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }

            if (!destDir.canWrite()) {
                throw new IOException("Destination '" + destDir + "' cannot be written to");
            } else {
                File[] arr$ = srcFiles;
                int len$ = srcFiles.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    File srcFile = arr$[i$];
                    File dstFile = new File(destDir, srcFile.getName());
                    if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                        if (srcFile.isDirectory()) {
                            doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
                        } else {
                            doCopyFile(srcFile, dstFile, preserveFileDate);
                        }
                    }
                }

                if (preserveFileDate) {
                    destDir.setLastModified(srcDir.lastModified());
                }

            }
        }
    }
}
