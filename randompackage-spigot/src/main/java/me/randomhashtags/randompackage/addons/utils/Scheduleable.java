package me.randomhashtags.randompackage.addons.utils;

import me.randomhashtags.randompackage.addons.enums.ScheduleableType;

import java.util.Date;
import java.util.List;

public interface Scheduleable extends Identifiable {
    Date getEventDay();
    ScheduleableType getScheduleType();
    default long getTimeUntilEventStarts() {
        final long t = getEventDay().getTime();
        return t-System.currentTimeMillis();
    }
    long getEventDuration();
    long getEventTimeLeft();

    void start();
    void stop(boolean actuallyEnded);

    List<String> getEventStartedMsg();
    List<String> getEventEndedMsg();
}
