package edu.iastate.metnet.metaomgraph;

import java.text.DecimalFormat;


public class CorrelationValue extends Number implements Comparable {
    protected double myValue;
    protected static DecimalFormat percentFormat;
    protected static DecimalFormat decimalFormat;
    protected boolean asPercent;
    //urmi
    protected int pval;

    public CorrelationValue(double myValue) {
        this.myValue = myValue;
        asPercent = false;
    }

    @Override
	public int compareTo(Object arg0) {
        int result = 100;
        if (arg0 == null) return 1;

        if (!(arg0 instanceof Number)) {
            System.out.println("Not a correlation!");
            return arg0.toString().compareTo(toString());
        }
        double o1 = doubleValue();
        double o2 = ((Number) arg0).doubleValue();

        if (o1 > o2) result = 1;
        else if (o1 < o2) result = -1;
        else result = 0;

        return result;
    }

    public int oldcompareTo(Object arg0) {
        System.out.println("this: " + this + " arg: " + arg0);
        if (arg0 == null) return 1;

        if (!(arg0 instanceof CorrelationValue)) return arg0.toString().compareTo(toString());

        CorrelationValue cv = (CorrelationValue) arg0;
        if (myValue > myValue) return 1;
        if (myValue < myValue) return -1;

        return 0;
    }

    @Override
	public String toString() {
        if (percentFormat == null) {
            percentFormat = new DecimalFormat("###.00%");
        }
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("#.0000");
        }
        if (isAsPercent()) {
            return percentFormat.format(myValue);
        }
        return decimalFormat.format(myValue);
    }

    public static void main(String[] args) {
        CorrelationValue cv1 = new CorrelationValue(0.01D);
        CorrelationValue cv2 = new CorrelationValue(0.1D);
        if (cv1.compareTo(cv2) > 0) System.out.println(cv1 + " is greater than " + cv2);
        else if (cv1.compareTo(cv2) < 0) System.out.println(cv1 + " is less than " + cv2);
        else System.out.println(cv1 + " is equal to " + cv2);

        DecimalFormat format = new DecimalFormat("###.00%");
        System.out.println(format.format(-0.12115D));
    }


    @Override
	public double doubleValue() {
        return myValue;
    }


    @Override
	public float floatValue() {
        return (float) myValue;
    }


    @Override
	public int intValue() {
        return Math.round((float) myValue);
    }


    @Override
	public long longValue() {
        return Math.round(myValue);
    }

    public void setAsPercent(boolean asPercent) {
        this.asPercent = asPercent;
    }

    public boolean isAsPercent() {
        return asPercent;
    }
}
