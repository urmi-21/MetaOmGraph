package edu.iastate.metnet.arrayexpress;

import org.jdom.Element;

public class Bibliography implements TableDataNode, edu.iastate.metnet.metaomgraph.XMLizable {
    String authors;
    String publication;
    String issue;
    String volume;
    String year;
    String title;
    String pages;

    public Bibliography() {
    }

    public Object[][] getTableData() {
        Object[][] result = new Object[1][7];
        result[0][0] = authors;
        result[0][1] = title;
        result[0][2] = publication;
        result[0][3] = issue;
        result[0][4] = volume;
        result[0][5] = year;
        result[0][6] = pages;
        return result;
    }

    public String[] getTableHeaders() {
        return new String[]{"Authors", "Title", "Publication", "Issue", "Volume", "Year", "Pages"};
    }

    public void fromXML(Element source) {
        if ((this.authors = source.getAttributeValue("authors")) == null) {
            authors = "-";
        }
        if ((this.publication = source.getAttributeValue("publication")) == null) {
            publication = "-";
        }
        if ((this.issue = source.getAttributeValue("issue")) == null) {
            issue = "-";
        }
        if ((this.volume = source.getAttributeValue("volume")) == null) {
            volume = "-";
        }
        if ((this.year = source.getAttributeValue("year")) == null) {
            year = "-";
        }
        if ((this.title = source.getAttributeValue("title")) == null) {
            title = "-";
        }
        if ((this.pages = source.getAttributeValue("pages")) == null) {
            pages = "-";
        }
    }


    public Element toXML() {
        return null;
    }

    public String toString() {
        return "Bibliography";
    }

    public static Bibliography createFromXML(Element source) {
        Bibliography result = new Bibliography();
        result.fromXML(source);
        return result;
    }
}
