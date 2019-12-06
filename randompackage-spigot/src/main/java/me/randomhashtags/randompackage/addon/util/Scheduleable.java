package me.randomhashtags.randompackage.addon.util;

import me.randomhashtags.randompackage.enums.ScheduleableType;

import java.util.Date;
import java.util.List;

public interface Scheduleable extends Identifiable {
    List<Date> getEventDays();
    ScheduleableType getScheduleType();
    default long getTimeUntilEventStarts() {
        final long t = getEventDays().get(0).getTime();
        return t-System.currentTimeMillis();
    }
    long getEventDuration();
    long getEventRuntime();
    long getEventTimeLeft();

    void start();
    void stop(boolean actuallyEnded);

    List<String> getEventStartedMsg();
    List<String> getEventEndedMsg();
}
