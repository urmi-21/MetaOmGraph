package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JProgressBar;

public class LongProgressBar extends JProgressBar {
    private long min;
    private long max;
    private long value;

    public LongProgressBar(long min, long max) {
        super(0, 1000);
        this.min = min;
        this.max = max;
        value = min;
    }

    public void setValue(long value) {
        if ((value > max) || (value < min)) {
            throw new java.security.InvalidParameterException("min=" + min + " max=" + max + " value=" + value);
        }
        long range = max - min;
        int newValue = (int) ((value - min) / range * 1000L);
        super.setValue(newValue);
        paintImmediately(getBounds());
    }

    @Override
	public void setMinimum(int n) {
        min = n;
    }

    @Override
	public void setMaximum(int n) {
        max = n;
    }

    public void setMinimum(long n) {
        min = n;
    }

    public void setMaximum(long n) {
        max = n;
    }
}
