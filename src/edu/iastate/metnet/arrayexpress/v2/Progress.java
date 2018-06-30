package edu.iastate.metnet.arrayexpress.v2;

public class Progress {
    private long min;
    private long max;
    private long value;

    public Progress(long min, long max, long value) {
        this.min = min;
        this.max = max;
        this.value = value;
    }


    public Progress(int min, int max, int value) {
        this((long) min, (long) max, (long) value);
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void increaseValue(long amount) {
        setValue(getValue() + amount);
    }

    public double getPercent() {
        return (value - min) / (max - min);
    }
}
