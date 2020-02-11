package edu.iastate.metnet.metaomgraph;


public class SortableData extends Number  implements Comparable {
    private int index;
    private double value;
    private boolean ascending;
    private boolean sortByData;

    public SortableData(double value, int index) {
        this(value, index, true, true);
    }

    public SortableData(double value, int index, boolean ascending) {
        this(value, index, ascending, true);
    }

    public SortableData(double value, int index, boolean ascending, boolean sortByData) {
        this.value = value;
        this.index = index;
        this.ascending = ascending;
        this.sortByData = sortByData;
    }

    @Override
	public int compareTo(Object arg0) {
        double compareMe;
        if ((arg0 instanceof SortableData)) {
            compareMe = ((SortableData) arg0).getValueToCompare();
        } else { //double compareMe;
            if ((arg0 instanceof Number)) {
                compareMe = ((Number) arg0).doubleValue();
            } else
                return 0;
        }
        double myValue = getValueToCompare();
        if (ascending) {
            if (Double.isNaN(myValue)) {
                if (Double.isNaN(compareMe)) {
                    return 0;
                }
                return -1;
            }
            if (Double.isNaN(compareMe)) return 1;
            if (myValue > compareMe) return 1;
            if (myValue < compareMe) return -1;
            if (myValue == compareMe) return 0;
        } else {
            if (Double.isNaN(myValue)) {
                if (Double.isNaN(compareMe)) return 0;

                return 1;
            }
            if (Double.isNaN(compareMe)) return -1;
            if (myValue < compareMe) return 1;
            if (myValue > compareMe) return -1;
            if (myValue == compareMe) return 0;
        }
        return 0;
    }


    public double getValueToCompare() {
        return sortByData ? value : index;
    }

    @Override
	public double doubleValue() {
        return value;
    }

    @Override
	public float floatValue() {
        return (float) value;
    }

    @Override
	public int intValue() {
        return (int) value;
    }

    @Override
	public long longValue() {
        return (long) value;
    }


    public boolean isAscending() {
        return ascending;
    }


    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }


    public int getIndex() {
        return index;
    }


    public void setIndex(int index) {
        this.index = index;
    }


    public boolean isSortByData() {
        return sortByData;
    }


    public void setSortByData(boolean sortByData) {
        this.sortByData = sortByData;
    }


    public double getValue() {
        return value;
    }


    public void setValue(double value) {
        this.value = value;
    }
}
