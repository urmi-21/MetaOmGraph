package edu.iastate.metnet.metaomgraph.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.JCheckBox;


class CheckBoxHandler
        implements ItemListener {
    CheckBoxHandler() {
    }

    public void itemStateChanged(ItemEvent e) {
        JCheckBox box = (JCheckBox) e.getSource();
        Integer id = new Integer(box.getName());
        ArrayList<Integer> list = PathwaySelectionFrame.getPathwayList();
        ArrayList<String> nameList = PathwaySelectionFrame.getPathwayNameList();
        if (e.getStateChange() == 1) {
            list.add(id);
            nameList.add(box.getText());
        } else if (e.getStateChange() == 2) {
            list.remove(id);
            nameList.add(box.getText());
        }
    }
}
