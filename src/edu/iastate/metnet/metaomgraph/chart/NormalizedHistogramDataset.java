package edu.iastate.metnet.metaomgraph.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

/**
 * A dataset that can be used for creating histograms.
 *
 * @see SimpleHistogramDataset
 */
public class NormalizedHistogramDataset extends HistogramDataset {

    /** For serialization. */
    private static final long serialVersionUID = -6341668077370231153L;

    /** A list of maps. */
    private List list;

    /** The histogram type. */
    private HistogramType type;

    /**
     * Creates a new (empty) dataset with a default type of
     * {@link HistogramType}.FREQUENCY.
     */
    public NormalizedHistogramDataset() {
        this.list = new ArrayList();
        this.type = HistogramType.FREQUENCY;
    }

    /**
     * Returns the histogram type.
     *
     * @return The type (never {@code null}).
     */
    public HistogramType getType() {
        return this.type;
    }

    /**
     * Sets the histogram type and sends a {@link DatasetChangeEvent} to all
     * registered listeners.
     *
     * @param type  the type ({@code null} not permitted).
     */
    public void setType(HistogramType type) {
        Args.nullNotPermitted(type, "type");
        this.type = type;
        fireDatasetChanged();
    }

    /**
     * Adds a series to the dataset, using the specified number of bins,
     * and sends a {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param key  the series key ({@code null} not permitted).
     * @param values the values ({@code null} not permitted).
     * @param bins  the number of bins (must be at least 1).
     */
    public void addSeries(Comparable key, double[] values, int bins) {
        // defer argument checking...
        double minimum = getMinimum(values);
        double maximum = getMaximum(values);
        addSeries(key, values, bins, minimum, maximum);
    }

    /**
     * Adds a series to the dataset. Any data value less than minimum will be
     * assigned to the first bin, and any data value greater than maximum will
     * be assigned to the last bin.  Values falling on the boundary of
     * adjacent bins will be assigned to the higher indexed bin.
     *
     * @param key  the series key ({@code null} not permitted).
     * @param values  the raw observations.
     * @param bins  the number of bins (must be at least 1).
     * @param minimum  the lower bound of the bin range.
     * @param maximum  the upper bound of the bin range.
     */
    public void addSeries(Comparable key, double[] values, int bins,
            double minimum, double maximum) {

        Args.nullNotPermitted(key, "key");
        Args.nullNotPermitted(values, "values");
        if (bins < 1) {
            throw new IllegalArgumentException(
                    "The 'bins' value must be at least 1.");
        }
        double binWidth = (maximum - minimum) / bins;

        double lower = minimum;
        double upper;
        List binList = new ArrayList(bins);
        List normalizedBinList = new ArrayList(bins);
        int binCount = 0;
        for (int i = 0; i < bins; i++) {
        	NormalizedHistogramBin bin;
        	NormalizedHistogramBin normalBin;
            
            // make sure bins[bins.length]'s upper boundary ends at maximum
            // to avoid the rounding issue. the bins[0] lower boundary is
            // guaranteed start from min
            if (i == bins - 1) {
                bin = new NormalizedHistogramBin(lower, maximum);
                normalBin = new NormalizedHistogramBin(lower, maximum);
            }
            else {
                upper = minimum + (i + 1) * binWidth;
                bin = new NormalizedHistogramBin(lower, upper);
                normalBin = new NormalizedHistogramBin(lower, upper);
                lower = upper;
            }
            binList.add(bin);
            normalizedBinList.add(normalBin);
        }
        // fill the bins
        for (int i = 0; i < values.length; i++) {
            int binIndex = bins - 1;
            if (values[i] < maximum) {
                double fraction = (values[i] - minimum) / (maximum - minimum);
                if (fraction < 0.0) {
                    fraction = 0.0;
                }
                binIndex = (int) (fraction * bins);
                // rounding could result in binIndex being equal to bins
                // which will cause an IndexOutOfBoundsException - see bug
                // report 1553088
                if (binIndex >= bins) {
                    binIndex = bins - 1;
                }
            }
            NormalizedHistogramBin bin = (NormalizedHistogramBin) binList.get(binIndex);
            bin.incrementCount();
        }
        
        for(int j = 0; j < bins ; j++) {
        	NormalizedHistogramBin bin = (NormalizedHistogramBin) binList.get(j);
        	binCount += bin.getCount();
        }
        
        for(int j = 0; j < bins ; j++) {
        	NormalizedHistogramBin bin = (NormalizedHistogramBin) binList.get(j);
        	NormalizedHistogramBin normalBin = (NormalizedHistogramBin) normalizedBinList.get(j);
        	double newBinSize = bin.getCount()/binCount;
        	
        	normalBin.setCount(newBinSize);

        }
        
        
        // generic map for each series
        Map map = new HashMap();
        map.put("key", key);
        map.put("bins", normalizedBinList);
        map.put("values.length", new Integer(values.length));
        map.put("bin width", new Double(binWidth));
        this.list.add(map);
        fireDatasetChanged();
    }

    /**
     * Returns the minimum value in an array of values.
     *
     * @param values  the values ({@code null} not permitted and
     *                zero-length array not permitted).
     *
     * @return The minimum value.
     */
    private double getMinimum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException(
                    "Null or zero length 'values' argument.");
        }
        double min = Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Returns the maximum value in an array of values.
     *
     * @param values  the values ({@code null} not permitted and
     *                zero-length array not permitted).
     *
     * @return The maximum value.
     */
    private double getMaximum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException(
                    "Null or zero length 'values' argument.");
        }
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Returns the bins for a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     *
     * @return A list of bins.
     *
     * @throws IndexOutOfBoundsException if {@code series} is outside the
     *     specified range.
     */
    List getBins(int series) {
        Map map = (Map) this.list.get(series);
        return (List) map.get("bins");
    }

    /**
     * Returns the total number of observations for a series.
     *
     * @param series  the series index.
     *
     * @return The total.
     */
    private int getTotal(int series) {
        Map map = (Map) this.list.get(series);
        return ((Integer) map.get("values.length")).intValue();
    }

    /**
     * Returns the bin width for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The bin width.
     */
    private double getBinWidth(int series) {
        Map map = (Map) this.list.get(series);
        return ((Double) map.get("bin width")).doubleValue();
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return The series count.
     */
    @Override
    public int getSeriesCount() {
        return this.list.size();
    }

    /**
     * Returns the key for a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     *
     * @return The series key.
     *
     * @throws IndexOutOfBoundsException if {@code series} is outside the
     *     specified range.
     */
    @Override
    public Comparable getSeriesKey(int series) {
        Map map = (Map) this.list.get(series);
        return (Comparable) map.get("key");
    }

    /**
     * Returns the number of data items for a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     *
     * @return The item count.
     *
     * @throws IndexOutOfBoundsException if {@code series} is outside the
     *     specified range.
     */
    @Override
    public int getItemCount(int series) {
        return getBins(series).size();
    }

    /**
     * Returns the X value for a bin.  This value won't be used for plotting
     * histograms, since the renderer will ignore it.  But other renderers can
     * use it (for example, you could use the dataset to create a line
     * chart).
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (zero based).
     *
     * @return The start value.
     *
     * @throws IndexOutOfBoundsException if {@code series} is outside the
     *     specified range.
     */
    @Override
    public Number getX(int series, int item) {
        List bins = getBins(series);
        NormalizedHistogramBin bin = (NormalizedHistogramBin) bins.get(item);
        double x = (bin.getStartBoundary() + bin.getEndBoundary()) / 2.;
        return new Double(x);
    }

    /**
     * Returns the y-value for a bin (calculated to take into account the
     * histogram type).
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (zero based).
     *
     * @return The y-value.
     *
     * @throws IndexOutOfBoundsException if {@code series} is outside the
     *     specified range.
     */
    @Override
    public Number getY(int series, int item) {
        List bins = getBins(series);
        NormalizedHistogramBin bin = (NormalizedHistogramBin) bins.get(item);
        double total = getTotal(series);
        double binWidth = getBinWidth(series);

        if (this.type == HistogramType.FREQUENCY) {
            return new Double(bin.getCount());
        }
        else if (this.type == HistogramType.RELATIVE_FREQUENCY) {
            return new Double(bin.getCount() / total);
        }
        else if (this.type == HistogramType.SCALE_AREA_TO_1) {
            return new Double(bin.getCount() / (binWidth * total));
        }
        else { // pretty sure this shouldn't ever happen
            throw new IllegalStateException();
        }
    }

    /**
     * Returns the start value for a bin.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (zero based).
     *
     * @return The start value.
     *
     * @throws IndexOutOfBoundsException if {@code series} is outside the
     *     specified range.
     */
    @Override
    public Number getStartX(int series, int item) {
        List bins = getBins(series);
        NormalizedHistogramBin bin = (NormalizedHistogramBin) bins.get(item);
        return new Double(bin.getStartBoundary());
    }

    /**
     * Returns the end value for a bin.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (zero based).
     *
     * @return The end value.
     *
     * @throws IndexOutOfBoundsException if {@code series} is outside the
     *     specified range.
     */
    @Override
    public Number getEndX(int series, int item) {
        List bins = getBins(series);
        NormalizedHistogramBin bin = (NormalizedHistogramBin) bins.get(item);
        return new Double(bin.getEndBoundary());
    }

    /**
     * Returns the start y-value for a bin (which is the same as the y-value,
     * this method exists only to support the general form of the
     * {@link IntervalXYDataset} interface).
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (zero based).
     *
     * @return The y-value.
     *
     * @throws IndexOutOfBoundsException if {@code series} is outside the
     *     specified range.
     */
    @Override
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Returns the end y-value for a bin (which is the same as the y-value,
     * this method exists only to support the general form of the
     * {@link IntervalXYDataset} interface).
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (zero based).
     *
     * @return The Y value.
     *
     * @throws IndexOutOfBoundsException if {@code series} is outside the
     *     specified range.
     */
    @Override
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Tests this dataset for equality with an arbitrary object.
     *
     * @param obj  the object to test against ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NormalizedHistogramDataset)) {
            return false;
        }
        NormalizedHistogramDataset that = (NormalizedHistogramDataset) obj;
        if (!ObjectUtils.equal(this.type, that.type)) {
            return false;
        }
        if (!ObjectUtils.equal(this.list, that.list)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of the dataset.
     *
     * @return A clone of the dataset.
     *
     * @throws CloneNotSupportedException if the object cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        NormalizedHistogramDataset clone = (NormalizedHistogramDataset) super.clone();
        int seriesCount = getSeriesCount();
        clone.list = new java.util.ArrayList(seriesCount);
        for (int i = 0; i < seriesCount; i++) {
            clone.list.add(new HashMap((Map) this.list.get(i)));
        }
        return clone;
    }

}