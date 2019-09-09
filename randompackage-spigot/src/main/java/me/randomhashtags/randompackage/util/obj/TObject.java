package me.randomhashtags.randompackage.util.obj;

public class TObject {
    private Object first, second, third;
    public TObject(Object first, Object second, Object third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
    public Object first() { return first; }
    public Object second() { return second; }
    public Object third() { return third; }
}
