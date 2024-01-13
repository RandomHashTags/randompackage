package me.randomhashtags.randompackage.addon.util;

import me.randomhashtags.randompackage.addon.enums.ScheduleableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public interface Scheduleable extends Identifiable {
    @NotNull List<Date> getEventDays();
    @NotNull ScheduleableType getScheduleType();
    default long getTimeUntilEventStarts() {
        final long t = getEventDays().get(0).getTime();
        return t-System.currentTimeMillis();
    }
    long getEventDuration();
    long getEventRuntime();
    long getEventTimeLeft();

    void start();
    void stop(boolean actuallyEnded);

    @NotNull List<String> getEventStartedMsg();
    @NotNull List<String> getEventEndedMsg();
}
