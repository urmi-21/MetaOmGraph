package edu.iastate.metnet.metaomgraph.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;


public class MultiComboBox
        extends MenuButton {
    private JCheckBox[] boxes;
    private Object[] options;
    private JPopupMenu menu;
    private static final String DEFAULT_TEXT = "Nothing selected";

    public MultiComboBox(Object[] options) {
        super("Nothing selected", null);
        this.options = options;
        boxes = new JCheckBox[options.length];
        menu = new JPopupMenu();
        for (int i = 0; i < options.length; i++) {
            boxes[i] = new JCheckBox(options[i] + "");
            menu.add(boxes[i]);
            boxes[i].addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent paramActionEvent) {
                    setText(getButtonText());
                }
            });
        }

        setMenu(menu);
    }

    public String getButtonText() {
        int count = 0;
        String result = "Nothing selected";
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i].isSelected()) {

                count++;
                if (count == 1) {
                    result = options[i].toString();
                } else
                    result = count + " options selected";
            }
        }
        return result;
    }

    @Override
	public Object[] getSelectedObjects() {
        ArrayList<Object> result = new ArrayList();
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i].isSelected()) {
                result.add(options[i]);
            }
        }
        return result.toArray(new Object[0]);
    }


    public static void main(String[] args) {
        JFrame f = new JFrame("Multi ComboBox Test");
        JPanel boxPanel = new JPanel();
        boxPanel.add(new JLabel("Select: "));
        boxPanel.add(new MultiComboBox(new String[]{"One", "Two", "Three", "Four", "Five"}));
        f.add(boxPanel, "North");
        f.add(new JPanel(), "Center");
        f.setSize(500, 500);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
