package edu.iastate.metnet.metaomgraph;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jdom.Element;


public class GeneList implements Transferable, Serializable {
    private String name;
    private int[] correlations;
    private int[] rows;
    private boolean correlationList;
    private boolean fullList;
    private static GeneList COMPLETE_LIST;
    private static DataFlavor geneListFlavor;

    public GeneList(String name, boolean full) {
        fullList = full;
        this.name = name;
        correlationList = false;
    }

    public GeneList(String name, int[] rows) {
        this.rows = rows;
        this.name = name;
        correlationList = false;
        Arrays.sort(this.rows);
    }

    public boolean isCorrelationList() {
        return correlationList;
    }

    public String toString() {
        return name;
    }

    public int[] getCorrelations() {
        return correlations;
    }

    public int[] getRows() {
        if (!isFullList())   return rows;

        return getCompleteList().getRows();
    }

    public static GeneList getCompleteList() {
        if (COMPLETE_LIST == null) {
            int[] allRows = new int[100];
            for (int x = 0; x < allRows.length; ) {
                allRows[x] = (x++);
            }
            COMPLETE_LIST = new GeneList("Complete List", allRows);
        }
        return COMPLETE_LIST;
    }

    public static DataFlavor getGeneListFlavor() {
        if (geneListFlavor == null) geneListFlavor = new DataFlavor(GeneList.class, "Gene List");
        return geneListFlavor;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{getGeneListFlavor()};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(getGeneListFlavor());
    }

    public Object getTransferData() {
        Object result = null;
        try {
            result = getTransferData(getGeneListFlavor());
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this;
    }

    public void setName(String newName) {
        if ((newName != null) && (!newName.trim().equals("")))
            name = newName;
    }

    public void merge(GeneList mergeMe) {
        if (fullList) return;
        if (mergeMe.isFullList()) {
            rows = null;
            fullList = true;
            return;
        }
        int[] mergeRows = mergeMe.getRows();
        HashMap<Integer, Boolean> rowHash = new HashMap();
        for (int x = 0; x < rows.length; x++) {
            rowHash.put(new Integer(rows[x]), new Boolean(true));
        }
        for (int x = 0; x < mergeRows.length; x++) {
            rowHash.put(new Integer(mergeRows[x]), new Boolean(true));
        }
        Set newRows = rowHash.keySet();
        rows = new int[newRows.size()];
        Iterator iter = newRows.iterator();
        for (int x = 0; iter.hasNext(); x++) {
            rows[x] = ((Integer) iter.next()).intValue();
        }
        Arrays.sort(rows);
    }

    public boolean isFullList() {
        return fullList;
    }

    public Element exportList(File dest, int idCol) {
        Element root = new Element("List").setAttribute("name", name);

        return root;
    }
}
