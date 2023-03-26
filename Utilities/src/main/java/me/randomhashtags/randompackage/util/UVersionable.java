package me.randomhashtags.randompackage.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public interface UVersionable extends RomanNumerals, DefaultFileGeneration {
    String SEPARATOR = File.separator;
    Random RANDOM = new Random();

    @NotNull
    default String toReadableDate(@NotNull Date date, @NotNull String format) {
        return new SimpleDateFormat(format).format(date);
    }

    @NotNull
    default File[] getFilesInFolder(@NotNull String folder) {
        final File file = new File(folder);
        return file.exists() ? file.listFiles() : new File[]{};
    }

    @NotNull
    default String getRemainingTime(long time) {
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(time), min = sec/60, hr = min/60, d = hr/24;
        hr -= d*24;
        min -= (hr*60)+(d*60*24);
        sec -= (min*60)+(hr*60*60)+(d*60*60*24);
        final String dys = d > 0 ? d + "d" + (hr != 0 ? " " : "") : "";
        final String hrs = hr > 0 ? hr + "h" + (min != 0 ? " " : "") : "";
        final String mins = min != 0 ? min + "m" + (sec != 0 ? " " : "") : "";
        final String secs = sec != 0 ? sec + "s" : "";
        return dys + hrs + mins + secs;
    }

    void sendConsoleMessage(String message);

    default String formatBigDecimal(BigDecimal b) {
        return formatNumber(b, false);
    }
    default String formatNumber(Object number, boolean currency) {
        return (currency ? NumberFormat.getCurrencyInstance() : NumberFormat.getInstance()).format(number);
    }
    default BigDecimal valueOfBigDecimal(@NotNull String input) {
        final long m = input.endsWith("k") ? 1000 : input.endsWith("m") ? 1000000 : input.endsWith("b") ? 1000000000 : 1;
        return BigDecimal.valueOf(getRemainingDouble(input)*m);
    }
    default BigDecimal getBigDecimal(String value) {
        return BigDecimal.valueOf(Double.parseDouble(value));
    }
    default BigDecimal getRandomBigDecimal(BigDecimal min, BigDecimal max) {
        final BigDecimal range = max.subtract(min);
        return min.add(range.multiply(new BigDecimal(Math.random())));
    }
    default String formatDouble(double d) {
        String decimals = Double.toString(d).split("\\.")[1];
        if(decimals.equals("0")) { decimals = ""; } else { decimals = "." + decimals; }
        return formatInt((int) d) + decimals;
    }
    default String formatLong(long l) {
        final String f = Long.toString(l);
        final boolean c = f.contains(".");
        String decimals = c ? f.split("\\.")[1] : f;
        decimals = c ? decimals.equals("0") ? "" : "." + decimals : "";
        return formatInt((int) l) + decimals;
    }
    default String formatInt(int integer) {
        return String.format("%,d", integer);
    }
    default int getIntegerFromString(String input, int minimum) {
        final boolean hasHyphen = input.contains("-");
        final String[] values = input.split("-");
        final int min = hasHyphen ? Integer.parseInt(values[0]) : minimum;
        return hasHyphen ? min+RANDOM.nextInt(Integer.parseInt(values[1])-min+1) : Integer.parseInt(input);
    }
    default int parseInt(String input) {
        input = input.toLowerCase();
        return input.equals("random") ? RANDOM.nextInt(101) : Integer.parseInt(input);
    }

    int getRemainingInt(String input);
    Double getRemainingDouble(String input);

    default long getDelay(String input) {
        input = input.toLowerCase();
        long value = 0;
        if(input.contains("d")) {
            final String[] values = input.split("d");
            value += getRemainingDouble(values[0])*1000*60*60*24;
            input = values.length > 1 ? values[1] : input;
        }
        if(input.contains("h")) {
            final String[] values = input.split("h");
            value += getRemainingDouble(values[0])*1000*60*60;
            input = values.length > 1 ? values[1] : input;
        }
        if(input.contains("m")) {
            final String[] values = input.split("m");
            value += getRemainingDouble(values[0])*1000*60;
            input = values.length > 1 ? values[1] : input;
        }
        if(input.contains("s")) {
            value += getRemainingDouble(input.split("s")[0])*1000;
        }
        return value;
    }

    default double round(double input, int decimals) {
        // From http://www.baeldung.com/java-round-decimal-number
        if(decimals < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(input));
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    default String roundDoubleString(double input, int decimals) {
        final double roundedValue = round(input, decimals);
        return Double.toString(roundedValue);
    }

    default String center(String s, int size) {
        // Credit to "Sahil Mathoo" from StackOverFlow at https://stackoverflow.com/questions/8154366
        return center(s, size, ' ');
    }
    default String center(String string, int size, char pad) {
        if(string == null || size <= string.length()) {
            return string;
        }
        final StringBuilder buider = new StringBuilder(size);
        for(int i = 0; i < (size - string.length()) / 2; i++) {
            buider.append(pad);
        }
        buider.append(string);
        while(buider.length() < size) {
            buider.append(pad);
        }
        return buider.toString();
    }

    @NotNull String colorize(@NotNull String input);
    @NotNull
    default List<String> colorizeListString(@NotNull List<String> input) {
        final List<String> i = new ArrayList<>();
        for(String s : input) {
            i.add(colorize(s));
        }
        return i;
    }

    default String toMaterial(String input, boolean realitem) {
        if(input.contains(":")) {
            input = input.split(":")[0];
        }
        if(input.contains(" ")) {
            input = input.replace(" ", "");
        }
        if(input.contains("_")) {
            input = input.replace("_", " ");
        }
        StringBuilder builder = new StringBuilder();
        if(input.contains(" ")) {
            final String[] spaces = input.split(" ");
            final int length = spaces.length;
            for(int i = 0; i < length; i++) {
                final String string = spaces[i];
                builder.append(string.substring(0, 1).toUpperCase()).append(string.substring(1).toLowerCase()).append(i != length - 1 ? (realitem ? "_" : " ") : "");
            }
        } else {
            builder = new StringBuilder(input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase());
        }
        return builder.toString();
    }
    default long parseTime(String fromString) {
        /*
            TODO: if called from Spigot, strip color (ChatColor.stripColor)
         */
        long time = 0;
        if(fromString != null) {
            final boolean hasDays = fromString.contains("d"), hasHrs = fromString.contains("h"), hasMins = fromString.contains("m"), hasSecs = fromString.contains("s");
            if(hasDays) {
                final String[] values = fromString.split("d");
                time += getRemainingDouble(values[0])*24*60*60;
                if(hasHrs || hasMins || hasSecs) {
                    fromString = values[1];
                }
            }
            if(hasHrs) {
                final String[] values = fromString.split("h");
                time += getRemainingDouble(values[0])*60*60;
                if(hasMins || hasSecs) {
                    fromString = values[1];
                }
            }
            if(hasMins) {
                final String[] values = fromString.split("m");
                time += getRemainingDouble(values[0])*60;
                if(hasSecs) {
                    fromString = values[1];
                }
            }
            if(hasSecs) {
                final String[] values = fromString.split("s");
                time += getRemainingDouble(values[0]);
                //fromString = fromString.split("s")[0];
            }
        }
        return time*1000;
    }

    default int indexOf(Set<?> collection, Object value) {
        int i = 0;
        for(Object o : collection) {
            if(value.equals(o)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
