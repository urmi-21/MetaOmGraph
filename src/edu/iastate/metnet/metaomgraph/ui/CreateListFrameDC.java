package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.logging.ActionProperties;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Harsha
 * 
 * This is the class for the Create List and Edit list feature in the 
 * Differential Correlation frame. Users can choose the features that 
 * can go into a list and click on 'OK' to create the list with a 
 * particular name.
 * 
 * The list created will be added to the availabe lists, and will be 
 * visible in the project data, Differential Expression and Differential
 * correlation frames.
 *
 */
public class CreateListFrameDC
        extends TaskbarInternalFrame
        implements ActionListener, ChangeListener {
    public static final String OK_COMMAND = "OK";
    public static final String CANCEL_COMMAND = "Cancel";
    public static final String IMPORT_COMMAND = "Import";
    private final DualTablePanel dtp;
    private String listName;
    private MetaOmProject myProject;
    private JButton createButton;
    private JButton cancelButton;
    private JButton importButton;
    private CreateListFrameDC myself;
    private boolean editList=false;

    public CreateListFrameDC(MetaOmProject project) {
        this(project, null, null);
    }


    public CreateListFrameDC(MetaOmProject project, String listName, DiffCorrResultsTable DCObj) {
        myself = this;
        this.listName = listName;
        myProject = project;
        JPanel buttonPanel = new JPanel();
        createButton = new JButton("Create");
        createButton.setActionCommand("OK");
        createButton.addActionListener(this);
        createButton.setEnabled(false);
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        importButton = new JButton("Import");
        importButton.setActionCommand("Import");
        importButton.addActionListener(this);
        buttonPanel.add(importButton);
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
      //urmi changed
        String[] headers = new String[myProject.getInfoColumnCount() + 1];
        headers[0] = "row number";
        for (int x = 1; x < headers.length; x++)
            headers[x] = myProject.getInfoColumnNames()[(x - 1)];
        /*
        String[] headers = new String[myProject.getInfoColumnCount()];
        for (int x = 0; x < headers.length; x++)
            headers[x] = myProject.getInfoColumnNames()[(x)];
            */
        int[] activeEntries;
        if (listName != null) {

            createButton.setText("OK");
            activeEntries = myProject.getGeneListRowNumbers(listName);
            editList = true;
        } else {
            
                activeEntries = DCObj.getSelectedRowsIndices();
                
            if (activeEntries.length > 0) {
                createButton.setEnabled(true);
            }
        }
        int[] inactiveEntries = new int[myProject.getRowNames().length -
                activeEntries.length];
        int thisEntry = 0;
        for (int i = 0; i < inactiveEntries.length; i++) {
            while (Utils.isIn(thisEntry, activeEntries))
                thisEntry++;
            inactiveEntries[i] = thisEntry;
            thisEntry++;
        }
        Object[][] active = new Object[activeEntries.length][headers.length];
        Object[][] inactive = new Object[inactiveEntries.length][headers.length];
        for (int x = 0; x < active.length; x++) {
        	//urmi removed
            active[x][0] = activeEntries[x];
            Object[] thisRowName = myProject.getRowName(activeEntries[x]);
            for (int y = 1; y < active[x].length; y++)
                active[x][y] = thisRowName[(y - 1)];
        }
        for (int x = 0; x < inactive.length; x++) {
        	//urmi removed
            inactive[x][0] = inactiveEntries[x];
            Object[] thisRowName = myProject.getRowName(inactiveEntries[x]);
            for (int y = 1; y < inactive[x].length; y++)
                inactive[x][y] = thisRowName[(y - 1)];
        }
        dtp = new DualTablePanel(inactive, active, headers, true);
        dtp.addChangeListener(this);
        dtp.hideColumn(0);
        dtp.setActiveLabel("In List");
        dtp.setInactiveLabel("Not In List");
        getContentPane().add(dtp, "Center");
        getContentPane().add(buttonPanel, "Last");
        setDefaultCloseOperation(2);
        
        
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
			Object[][] activeValues = dtp.getActiveValues();
			int[] result = new int[activeValues.length];



			for (int i = 0; i < result.length; i++)
				result[i] = ((Integer) activeValues[i][0]).intValue();

			if(editList) {
				if (myProject.addGeneList(listName, result, true, false)) {

					try {
						//Harsha - reproducibility log
						HashMap<String,Object> actionMap = new HashMap<String,Object>();
						actionMap.put("parent",MetaOmGraph.getCurrentProjectActionId());
						actionMap.put("section", "Feature Metadata");

						HashMap<String,Object> dataMap = new HashMap<String,Object>();
						dataMap.put("List Name", listName);
						dataMap.put("List Elements Count", result.length);
						Map<Integer,String> selectedItems = new HashMap<Integer,String>();

						for(int rowNum: result) {
							selectedItems.put(rowNum, myProject.getDefaultRowNames(rowNum));
						}
						dataMap.put("Selected Rows", selectedItems);
						HashMap<String,Object> resultLog = new HashMap<String,Object>();
						resultLog.put("result", "OK");

						ActionProperties createListAction = new ActionProperties("edit-list",actionMap,dataMap,resultLog,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()));
						createListAction.logActionProperties();
					}
					catch(Exception e1) {

					}
					
					dispose();
					MetaOmGraph.getTaskBar().removeFromTaskbar(this);
				}
			}
			else {

				if (myProject.addGeneList(listName, result, true, true)) {	
					dispose();
					MetaOmGraph.getTaskBar().removeFromTaskbar(this);
				}
			}

			return;
		}
        if ("Cancel".equals(e.getActionCommand())) {
            dispose();
            MetaOmGraph.getTaskBar().removeFromTaskbar(this);
            return;
        }
        if ("Import".equals(e.getActionCommand())) {


            int[] importUs = ImportListDialog.doImport(myProject);
            //JOptionPane.showMessageDialog(null, "iu"+Arrays.toString(importUs));
            if ((importUs == null) || (importUs.length <= 0)) {
                return;
            }
            Vector<Integer> v = new Vector();
            Object[][] inactiveValues = dtp.getInactiveValues();
            for (int x = 0; x < importUs.length; x++) {
                boolean found = false;
                for (int y = 0; (y < inactiveValues.length) && (!found); y++) {
                    if (importUs[x] == ((Integer) inactiveValues[y][0])
                            .intValue()) {
                        v.add(y);
                        found = true;
                    }
                }
            }
            int[] result = new int[v.size()];
            for (int i = 0; i < result.length; i++)
                result[i] = v.get(i).intValue();
            dtp.makeActive(result);
            return;
        }
    }

    @Override
	public void stateChanged(ChangeEvent e) {
        if ("reset".equals(e.getSource())) {
            dtp.hideColumn(0);
        }
        if (dtp.hasActiveValues()) {
            createButton.setEnabled(true);
        } else {
            createButton.setEnabled(false);
        }
    }
}
