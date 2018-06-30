package edu.iastate.metnet.metaomgraph.chart;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmAnalyzer;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata;
import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.SortableData;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;

import java.awt.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.biomage.Array.Array;

public class DataSorter {
	private Vector<RangeMarker> rangeMarkers;
	private MetaOmChartPanel myChartPanel;

	public DataSorter(MetaOmChartPanel mcp) {
		myChartPanel = mcp;
	}

	public int[] sortByColumnName() {
		String[] sampleNames = myChartPanel.getSampleNames();
		String[] names = new String[sampleNames.length];
		int[] result = new int[names.length];
		//JOptionPane.showMessageDialog(null, "sampnames:" + Arrays.toString(sampleNames));
		// Make the sort case-insensitive, and add the original index of the
		// column names to the end of each entry.
		for (int i = 0; i < names.length; i++) {
			names[i] = sampleNames[i].toLowerCase();
			names[i] += "<" + i;
		}
		Arrays.sort(names);
		// names will now be sorted alphabetically. Now we just need to populate
		// result with the original indices of the column names, which appear
		// after the last '<' character in each name.
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(names[i].substring(names[i].lastIndexOf("<") + 1));
		}
		rangeMarkers = null;
		//JOptionPane.showMessageDialog(null, "res:" + Arrays.toString(result));
		return result;
	}

	/**
	 * @author urmi Changed the previous implementation Sort the sort order array
	 *         according to names NOT used ANYMORE
	 * @return
	 */
	public int[] sortByColumnName2() {
		String[] sampleNames = myChartPanel.getSampleNames();
		// String[] names = new String[sampleNames.length];
		int[] so = myChartPanel.sortOrder;
		// sort so and sampleNames together and return so

		/* Bubble Sort */
		for (int n = 0; n < sampleNames.length; n++) {
			for (int m = 0; m < sampleNames.length - 1 - n; m++) {
				if ((sampleNames[m].compareTo(sampleNames[m + 1])) > 0) {
					String tempString = sampleNames[m];
					sampleNames[m] = sampleNames[m + 1];
					sampleNames[m + 1] = tempString;
					int tempInt = so[m];
					so[m] = so[m + 1];
					so[m + 1] = tempInt;
				}
			}
		}

		rangeMarkers = null;
		return so;
	}

	/**
	 * @author urmi Return sort order in ascending order Changed previous
	 *         implementation
	 * @return
	 */
	public int[] defaultOrder() {
		int[] so = myChartPanel.sortOrder;
		Arrays.sort(so);
		rangeMarkers = null;
		return so;
	}

	public int[] sortByYValue(double[] yvalues) {
		// JOptionPane.showMessageDialog(null, "currSO:" +
		// Arrays.toString(myChartPanel.sortOrder));

		int[] result = new int[yvalues.length];
		if (yvalues.length != result.length)
			return null;
		SortableData[] sortMe = new SortableData[result.length];
		for (int x = 0; x < result.length; x++) {
			sortMe[x] = new SortableData(yvalues[x], x, false);
		}
		Arrays.sort(sortMe);
		for (int x = 0; x < result.length; x++) {
			result[x] = sortMe[x].getIndex();
		}
		rangeMarkers = null;

		// JOptionPane.showMessageDialog(null, "res:" + Arrays.toString(result));
		return result;
	}

	// to change
	public int[] sortByMetadata() {
		final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(myChartPanel.getProject(),false);
		final MetadataQuery[] queries;
		queries = tsp.showSearchDialog();
		if (tsp.getQueryCount() <= 0) {
			System.out.println("Search dialog cancelled");
			// User didn't enter any queries
			return null;
		}
		final int[] result = new int[myChartPanel.getProject().getDataColumnCount()];
		final boolean nohits;
		new AnimatedSwingWorker("Searching...", true) {

			@Override
			public Object construct() {
				ArrayList<Integer> toAdd = new ArrayList<Integer>(result.length);
				for (int i = 0; i < result.length; i++) {
					toAdd.add(i);
				}
				// Integer[] hits = myChartPanel.getProject().getMetadata().search(queries,
				// tsp.matchAll());
				/**
				 * Changed urmi
				 */
				Integer[] hits = myChartPanel.getProject().getMetadataHybrid().search(queries, tsp.matchAll());
				
				//remove excluded cols from list to display corect range markers
				//urmi
				boolean [] excluded= MetaOmAnalyzer.getExclude();
				if(excluded!=null) {
					java.util.List<Integer> temp= new ArrayList<>();
					for(Integer i: hits) {
						if(!excluded[i]) {
							temp.add(i);
						}
					}
					hits= new Integer[temp.size()];
					hits=temp.toArray(hits);
				}

				// return if no hits
				if (hits.length == 0) {
					// JOptionPane.showMessageDialog(null, "hits len:"+hits.length);
					// nohits=true;
					result[0] = -1;
					rangeMarkers.removeAllElements();
					return null;
				}
				int index;
				for (index = 0; index < hits.length; index++) {
					result[index] = hits[index];
					toAdd.remove(hits[index]);
				}

				for (int i = 0; i < toAdd.size(); i++) {
					result[index++] = toAdd.get(i);
				}
				
				RangeMarker marker = new RangeMarker(0, hits.length - 1, "Hits", RangeMarker.HORIZONTAL);
				rangeMarkers = new Vector<RangeMarker>();
				rangeMarkers.add(marker);
				return null;
			}

		}.start();
		return result;
	}

	public int[] sortCustom() {
		NewCustomSortDialog dialog = new NewCustomSortDialog(myChartPanel.getSortOrder(), myChartPanel);
		NewCustomSortDialog.CustomSortObject result = dialog.showSortDialog();
		if (result == null)
			return null;
		rangeMarkers = result.getRangeMarkers();
		return result.getSortOrder();
	}

	/**
	 * @author urmi changed to new metadata class
	 * 
	 */
	public int[] clusterByMetadata(String field) {
		// Map<String, Collection<Integer>> clusters =
		// myChartPanel.getProject().getMetadata().cluster(field, 0);
		Map<String, Collection<Integer>> clusters = myChartPanel.getProject().getMetadataHybrid().cluster(field, 0);
		if (clusters.size() == 0) {
			JOptionPane.showMessageDialog(null, "Cluster size 0.Error in search...DataSorter:140");
			return null;
		}
		// JOptionPane.showMessageDialog(null, "Cluster size "+clusters.size());
		Set<String> groups = clusters.keySet();
		int[] result = new int[myChartPanel.getProject().getDataColumnCount()];
		if (result.length == 0) {
			JOptionPane.showMessageDialog(null, "Error in search...");
			return null;
		}
		rangeMarkers = new Vector<RangeMarker>();
		int index = 0;

		ArrayList<Integer> toAdd = new ArrayList<Integer>();
		for (int i = 0; i < result.length; i++) {
			toAdd.add(i);
		}

		for (String groupName : groups) {
			if ("".equals(groupName))
				continue;

			Collection<Integer> members = clusters.get(groupName);
			//JOptionPane.showMessageDialog(null, "gname:" + groupName);
			//JOptionPane.showMessageDialog(null, "membr:" + members.toString());
			/**
			 * @author urmi Remove the excluded columns to create range markers only for
			 *         shown columns
			 */
			boolean[] excluded = MetaOmAnalyzer.getExclude();
			if (excluded != null) {
				java.util.List<Integer> temp = new ArrayList<>();
				// remove excluded members
				for (int m : members) {
					if (excluded[m]) {
						//JOptionPane.showMessageDialog(null, "Excluding:" + m);
						temp.add(m);
					}
				}
				members.removeAll(temp);
			}
			//JOptionPane.showMessageDialog(null, "membrnow:" + members.toString());
			if (members.size() > 0) {
				rangeMarkers.add(new RangeMarker(index, index + members.size() - 1, groupName, RangeMarker.VERTICAL));
			}
			for (int col : members) {
				// todo
				// change known cols to include only those cols which are in datafile
				result[index++] = col;
				toAdd.remove((Object) col);
			}
		}

		for (int col : toAdd)
			result[index++] = col;

		return result;
	}

	public Vector<RangeMarker> getRangeMarkers() {
		return rangeMarkers;
	}

	public void setRangeMarkers(Vector<RangeMarker> rangeMarkers) {
		this.rangeMarkers = rangeMarkers;
	}
}
