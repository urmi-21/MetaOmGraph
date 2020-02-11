package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class DualListPanel
        extends JPanel
        implements ActionListener {
    private Object[] originalActiveData;
    private Object[] originalInactiveData;
    private JList inactiveList;
    private JList activeList;
    private JButton add;
    private JButton remove;
    private JButton reset;
    private DefaultListModel activeModel;
    private DefaultListModel inactiveModel;
    private JLabel inactiveLabel;
    private JLabel activeLabel;
    private Vector<ChangeListener> changeListeners;

    public DualListPanel(Object[] inactiveData) {
        this(inactiveData, null);
    }


    public DualListPanel(Object[] inactiveData, Object[] activeData) {
        originalInactiveData = inactiveData;
        originalActiveData = activeData;
        inactiveModel = new DefaultListModel();
        if (inactiveData != null)
            for (int x = 0; x < inactiveData.length; x++)
                inactiveModel.addElement(inactiveData[x]);
        inactiveList = new JList(inactiveModel);
        inactiveList.setDragEnabled(true);
        activeModel = new DefaultListModel();
        if (activeData != null)
            for (int x = 0; x < activeData.length; x++)
                activeModel.addElement(activeData[x]);
        activeList = new JList(activeModel);
        if ((activeData == null) && (inactiveData != null))
            activeList.setPrototypeCellValue(inactiveModel.getElementAt(0));
        JPanel inactivePanel = new JPanel(new BorderLayout());
        JPanel activePanel = new JPanel(new BorderLayout());
        inactiveLabel = new JLabel("");
        activeLabel = new JLabel("");
        inactivePanel.add(inactiveLabel, "First");
        inactivePanel.add(new JScrollPane(inactiveList), "Center");
        activePanel.add(activeLabel, "First");
        activePanel.add(new JScrollPane(activeList), "Center");
        add = new JButton("Add >>");
        add.setActionCommand("add");
        add.addActionListener(this);
        remove = new JButton("<< Remove");
        remove.setActionCommand("remove");
        remove.addActionListener(this);
        reset = new JButton("Reset");
        reset.setActionCommand("reset");
        reset.addActionListener(this);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = 1;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.gridheight = 3;
        add(inactivePanel, c);
        c.weightx = 0.0D;
        c.weighty = 1.0D;
        c.gridx = 1;
        c.gridheight = 1;
        c.fill = 2;
        c.anchor = 20;
        add(add, c);
        c.gridy = 1;
        c.weighty = 0.0D;
        c.anchor = 10;
        add(remove, c);
        c.gridy = 2;
        c.weighty = 1.0D;
        c.anchor = 19;
        add(reset, c);
        c.weightx = 1.0D;
        c.gridx = 2;
        c.gridy = 0;
        c.fill = 1;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.gridheight = 3;
        add(activePanel, c);
    }


    public Object[] getActiveValues() {
        Object[] result = new Object[activeModel.size()];
        for (int x = 0; x < result.length; x++)
            result[x] = activeModel.getElementAt(x);
        return result;
    }


    public boolean hasActiveValues() {
        return activeModel.size() > 0;
    }


    public void setActiveLabel(String label) {
        activeLabel.setText(label);
    }


    public void setInactiveLabel(String label) {
        inactiveLabel.setText(label);
    }


    public void addChangeListener(ChangeListener addMe) {
        if (changeListeners == null)
            changeListeners = new Vector();
        changeListeners.add(addMe);
    }

    private void fireChange(ChangeEvent event) {
        Iterator iter = changeListeners.iterator();
        while (iter.hasNext()) {
            ((ChangeListener) iter.next()).stateChanged(event);
        }
    }


    public void makeActive(Object[] addUs) {
        if ((addUs == null) || (addUs.length <= 0)) {
            return;
        }


        int[] selectedIndex = new int[addUs.length];
        int misses = 0;
        for (int i = 0; i < selectedIndex.length; i++) {
            boolean found = false;
            int x = 0;
            while ((x < inactiveModel.size()) && (!found))
                found = inactiveModel.get(x++).equals(addUs[i]);
            if (found) {
                selectedIndex[i] = (x - 1);
            } else {
                misses++;
                selectedIndex[i] = -1;
            }
        }
        int[] removeUs;
        Object[] selectedValues;
        if (misses > 0) {
            removeUs = new int[selectedIndex.length - misses];
            selectedValues = new Object[selectedIndex.length - misses];
            int index = 0;
            for (int i = 0; i < removeUs.length; i++) {
                if (selectedIndex[i] != -1) {
                    removeUs[i] = selectedIndex[index];
                    selectedValues[i] = addUs[index];
                } else {
                    index++;
                }
                index++;
            }
        } else {
            removeUs = selectedIndex;
            selectedValues = addUs;
        }
        if (removeUs.length <= 0)
            return;
        Object[] newActiveValues = new Object[activeModel.size() +
                selectedValues.length];
        activeModel.copyInto(newActiveValues);
        for (int i = 0; i < selectedValues.length; i++)
            newActiveValues[(i + activeModel.size())] = selectedValues[i];
        Object[] newInactiveValues = new Object[inactiveModel.size() -
                selectedValues.length];
        int currentValueIndex = 0;
        for (int i = 0; i < newInactiveValues.length; i++) {
            while (Utils.isIn(currentValueIndex, removeUs))
                currentValueIndex++;
            newInactiveValues[i] = inactiveModel.get(currentValueIndex);
            currentValueIndex++;
        }
        activeModel = new DefaultListModel();
        activeModel.ensureCapacity(newActiveValues.length);
        inactiveModel = new DefaultListModel();
        inactiveModel.ensureCapacity(newInactiveValues.length);
        for (int i = 0; i < newActiveValues.length; i++)
            activeModel.addElement(newActiveValues[i]);
        for (int i = 0; i < newInactiveValues.length; i++)
            inactiveModel.addElement(newInactiveValues[i]);
        activeList.setModel(activeModel);
        inactiveList.setModel(inactiveModel);
        activeList.ensureIndexIsVisible(activeModel.getSize() - 1);
        fireChange(new ChangeEvent("add"));
    }


    public void makeInactive(Object[] addUs) {
        if ((addUs == null) || (addUs.length <= 0))
            return;
        int[] selectedIndex = new int[addUs.length];
        int misses = 0;
        for (int i = 0; i < selectedIndex.length; i++) {
            boolean found = false;
            int x = 0;
            while ((x < activeModel.size()) && (!found))
                found = activeModel.get(x++).equals(addUs[i]);
            if (found) {
                selectedIndex[i] = (x - 1);
            } else {
                misses++;
                selectedIndex[i] = -1;
            }
        }
        int[] removeUs;
        Object[] selectedValues;
        if (misses > 0) {
            removeUs = new int[selectedIndex.length - misses];
            selectedValues = new Object[selectedIndex.length - misses];
            int index = 0;
            for (int i = 0; i < removeUs.length; i++) {
                if (selectedIndex[i] != -1) {
                    removeUs[i] = selectedIndex[index];
                    selectedValues[i] = addUs[index];
                } else {
                    index++;
                }
                index++;
            }
        } else {
            removeUs = selectedIndex;
            selectedValues = addUs;
        }
        if (removeUs.length <= 0)
            return;
        Object[] newInactiveValues = new Object[inactiveModel.size() +
                selectedValues.length];
        inactiveModel.copyInto(newInactiveValues);
        for (int i = 0; i < selectedValues.length; i++)
            newInactiveValues[(i + inactiveModel.size())] = selectedValues[i];
        Object[] newActiveValues = new Object[activeModel.size() -
                selectedValues.length];
        int currentValueIndex = 0;
        for (int i = 0; i < newActiveValues.length; i++) {
            while (Utils.isIn(currentValueIndex, removeUs))
                currentValueIndex++;
            newActiveValues[i] = activeModel.get(currentValueIndex);
            currentValueIndex++;
        }
        activeModel = new DefaultListModel();
        inactiveModel = new DefaultListModel();
        for (int i = 0; i < newActiveValues.length; i++)
            activeModel.addElement(newActiveValues[i]);
        for (int i = 0; i < newInactiveValues.length; i++)
            inactiveModel.addElement(newInactiveValues[i]);
        activeList.setModel(activeModel);
        inactiveList.setModel(inactiveModel);
        activeList.ensureIndexIsVisible(activeModel.getSize() - 1);
        fireChange(new ChangeEvent("remove"));
    }


    private void doAdd() {
        if (inactiveList.isSelectionEmpty())
            return;
        Object[] selectedValues = inactiveList.getSelectedValues();
        makeActive(selectedValues);
        fireChange(new ChangeEvent("add"));
    }


    private void doRemove() {
        if (activeList.isSelectionEmpty())
            return;
        Object[] selectedValues = activeList.getSelectedValues();
        makeInactive(selectedValues);
        fireChange(new ChangeEvent("remove"));
    }


    private void doReset() {
        activeModel = new DefaultListModel();
        inactiveModel = new DefaultListModel();
        if (originalInactiveData != null)
            for (int x = 0; x < originalInactiveData.length; x++)
                inactiveModel.addElement(originalInactiveData[x]);
        if (originalActiveData != null)
            for (int x = 0; x < originalActiveData.length; x++)
                activeModel.addElement(originalActiveData[x]);
        activeList.setModel(activeModel);
        inactiveList.setModel(inactiveModel);
        fireChange(new ChangeEvent("reset"));
    }

    @Override
	public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand().equals("add")) {
            doAdd();
            return;
        }
        if (arg0.getActionCommand().equals("remove")) {
            doRemove();
            return;
        }
        if (arg0.getActionCommand().equals("reset")) {
            doReset();
            return;
        }
    }
}
