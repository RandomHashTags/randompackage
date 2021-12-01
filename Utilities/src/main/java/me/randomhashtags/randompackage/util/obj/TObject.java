package me.randomhashtags.randompackage.util.obj;

public class TObject {
    protected Object first, second, third;
    public TObject(Object first, Object second, Object third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
    public final Object getFirst() {
        return first;
    }
    public final Object getSecond() {
        return second;
    }
    public final Object getThird() {
        return third;
    }
}
