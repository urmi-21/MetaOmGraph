package edu.iastate.metnet.metaomgraph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class HashtableSavePanel
        extends JPanel
        implements ListSelectionListener, ActionListener {
    private static final String SAVE_COMMAND = "save";
    private static final String LOAD_COMMAND = "load";
    private static final String DELETE_COMMAND = "delete";
    private Hashtable storage;
    private DefaultListModel listModel;
    private JList savedList;
    private JButton loadButton;
    private JButton saveButton;
    private JButton deleteButton;
    private HashLoadable loader;
    private String noun;

    public HashtableSavePanel(Hashtable storage, HashLoadable loadable) {
        noun = loadable.getNoun();
        if (storage != null) {
            this.storage = storage;
        } else {
            this.storage = new Hashtable();
        }
        loader = loadable;
        listModel = new DefaultListModel();
        if (this.storage.size() > 0) {
            Object[] keys = new Object[this.storage.size()];
            Enumeration keyEnum = this.storage.keys();
            int index = 0;
            while (keyEnum.hasMoreElements()) {
                keys[(index++)] = keyEnum.nextElement();
            }
            Arrays.sort(keys);
            for (int x = 0; x < keys.length; x++) {
                listModel.addElement(keys[x]);
            }
        }
        savedList = new JList(listModel);
        savedList.setSelectionMode(0);
        savedList.addListSelectionListener(this);
        //urmi
        savedList.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent me) {
        		if(me.getClickCount()==2) {
        			loadData();
        		}
        	}
		});
        JToolBar listToolBar = new JToolBar();
        listToolBar.setFloatable(false);
        IconTheme theme = MetaOmGraph.getIconTheme();
        loadButton = new JButton(theme.getListLoad());
        saveButton = new JButton(theme.getListSave());
        deleteButton = new JButton(theme.getListDelete());
        loadButton.setActionCommand("load");
        saveButton.setActionCommand("save");
        deleteButton.setActionCommand("delete");
        loadButton.setToolTipText("Load");
        saveButton.setToolTipText("Save");
        deleteButton.setToolTipText("Delete");
        loadButton.addActionListener(this);
        saveButton.addActionListener(this);
        deleteButton.addActionListener(this);
        loadButton.setEnabled(false);
        deleteButton.setEnabled(false);
        listToolBar.add(saveButton);
        listToolBar.add(loadButton);
        listToolBar.add(deleteButton);
        setLayout(new BorderLayout());
        add(listToolBar, "First");
        JScrollPane listPane = new JScrollPane(savedList);
        add(listPane, "Center");
        setPreferredSize(new Dimension(savedList.getFontMetrics(
                savedList.getFont()).charWidth('X') * 20,
                getPreferredSize().height));
        listPane
                .setHorizontalScrollBarPolicy(31);
    }

    public void setNoun(String noun) {
        this.noun = noun;
    }

    public void setLoadable(HashLoadable loadable) {
        loader = loadable;
    }

    @Override
	public void valueChanged(ListSelectionEvent e) {
        if (savedList.getSelectedIndex() > -1) {
            deleteButton.setEnabled(true);
            loadButton.setEnabled(true);
        }
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        if ("save".equals(e.getActionCommand())) {
            saveData();
            return;
        }
        if ("load".equals(e.getActionCommand())) {
            loadData();
            return;
        }
        if ("delete".equals(e.getActionCommand())) {
            deleteData();
            return;
        }
    }

    private void deleteData() {
        int result = JOptionPane.showConfirmDialog(getParent(),
                "Are you sure you want to delete the selected " + noun + "?",
                "Confirm", 0,
                3);
        if (result != 0) {
            return;
        }
        Object selected = savedList.getSelectedValue();
        storage.remove(selected);
        listModel.removeElement(selected);
        deleteButton.setEnabled(false);
        loadButton.setEnabled(false);
        
      //setting project changed as true
        MetaOmGraph.getActiveProject().setChanged(true);
    }

    private void loadData() {
    	
    	try {
        if (loader == null) {
            return;
        }
        Object data = storage.get(savedList.getSelectedValue());
        loader.loadData(data);
        
      //setting project changed as true
        MetaOmGraph.getActiveProject().setChanged(true);
    	}
    	catch(NullPointerException npe) {
    		npe.printStackTrace();
    	}
    }

    private void saveData() {
        boolean proceed = false;
        boolean overwriting = false;
        String name = "";
        while (!proceed) {
            Object typedName = JOptionPane.showInputDialog(null,
                    "Please name this " + noun + ":", "Saving " + noun,
                    3, null, null, null);
            if (typedName == null)
                return;
            name = (typedName + "").trim();
            //name = typedName.trim();
            if (name.equals("")) {
                JOptionPane.showMessageDialog(null, "You must enter a name!",
                        "Error", 0);
            } else if (storage.get(name) != null) {
                int result = JOptionPane.showConfirmDialog(null, "A " +
                                noun + " named " + name +
                                " already exists.  Overwrite?", "Overwrite",
                        1,
                        2);
                if (result == 2) {
                    return;
                }
                if (result == 0) {
                    proceed = true;
                    overwriting = true;
                }
            } else {
                proceed = true;
            }
        }

        Object data = loader.getSaveData();
        storage.put(name, data);
        if (!overwriting) {
            int insertHere = 0;
            String lcaseName = name.toLowerCase();
            while ((insertHere < listModel.getSize()) &&

                    (lcaseName.compareTo(listModel.getElementAt(insertHere) + "".toLowerCase()) > 0)) {
                insertHere++;
            }
            listModel.add(insertHere, name);
            
            //setting project changed as true
            MetaOmGraph.getActiveProject().setChanged(true);
        }
    }

    public Hashtable getHashtable() {
        return storage;
    }

    public void setSaveToolTipText(String text) {
        saveButton.setToolTipText(text);
    }

    public void setLoadToolTipText(String text) {
        loadButton.setToolTipText(text);
    }

    public void setDeleteToolTipText(String text) {
        deleteButton.setToolTipText(text);
    }
}
