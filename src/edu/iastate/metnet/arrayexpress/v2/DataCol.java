package edu.iastate.metnet.arrayexpress.v2;

public class DataCol implements Comparable<DataCol> {
    String header;
    int col;

    public DataCol(int col, String header) {
        this.col = col;
        if (header.endsWith(".CEL")) {
            header.startsWith("GSM");
        }


        if (this.header == null) {
            this.header = header;
        }
    }

    public int compareTo(DataCol o) {
        return header.compareTo(header);
    }

    public String toString() {
        return col + " - " + header;
    }
}
