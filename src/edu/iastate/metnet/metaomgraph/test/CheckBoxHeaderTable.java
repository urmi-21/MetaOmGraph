package edu.iastate.metnet.metaomgraph.test;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class CheckBoxHeaderTable extends JTable {
    private CheckBoxHeader[] boxes;
    private String description;

    public CheckBoxHeaderTable(TableModel model) {
        super(model);
        boxes = new CheckBoxHeader[getColumnCount()];
        for (int i = 0; i < getColumnCount(); i++) {
            boxes[i] = new CheckBoxHeader(null);
            TableColumn col = getColumnModel().getColumn(i);
            col.setHeaderRenderer(boxes[i]);
        }
    }

    public boolean isSelected(int col) {
        if ((col < 0) || (col > getColumnCount())) {
            throw new IllegalArgumentException("Invalid column: " + col);
        }
        return boxes[col].isSelected();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
