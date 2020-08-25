/**
 * 
 */
package edu.iastate.metnet.metaomgraph.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.MetadataCollection;

/**
 * SampleMetaDataListFrame class to create the frame for creating lists.
 * @author sumanth
 *
 */
public class SampleMetaDataListFrame extends JInternalFrame 
									 implements ActionListener, ChangeListener {
	
	private static final String OK_COMMAND = "OK";
    private static final String CANCEL_COMMAND = "Cancel";
    private static final String IMPORT_COMMAND = "Import";
    private final DualTablePanel dtp;
   
    private MetadataCollection metaDataCol;
    private String listName;
    
    private JButton createButton;
    private JButton cancelButton;
    private JButton importButton;

    /**
     * Constructor
     * @param metaDataCol MetadataCollection
     * @param selectedRows rows selected in the table
     * @param unSelectedRows rows that are not selected
     */
	public SampleMetaDataListFrame(MetadataCollection metaDataCol, 
			ArrayList<String> selectedRows,
			ArrayList<String> unSelectedRows) {
		this(metaDataCol, null, selectedRows, unSelectedRows);
	}

	/**
	 * Constructor
	 * @param metaDataCol MetadataCollection
	 * @param listName name of the list to create
	 * @param selectedRows rows selected in the table
	 * @param unSelectedRows rows that are not selected
	 */
	public SampleMetaDataListFrame(MetadataCollection metaDataCol, 
			String listName, 
			ArrayList<String> selectedRows,
			ArrayList<String> unSelectedRows) {
		this.metaDataCol = metaDataCol;
		this.listName = listName;
		JPanel buttonPanel = createButtonPanel();
		if(selectedRows.size() > 0)
			createButton.setEnabled(true);
		String[] headers = getHeaders();
		Object[][] activeData = getInactiveData(selectedRows, headers.length);
		Object[][] inActiveData = getActiveData(unSelectedRows, headers.length);
		dtp = new DualTablePanel(inActiveData, activeData, headers, false);
        dtp.addChangeListener(this);
        dtp.hideColumn(0);
        dtp.setActiveLabel("In List");
        dtp.setInactiveLabel("Not In List");
        getContentPane().add(dtp, "Center");
        getContentPane().add(buttonPanel, "Last");
        setDefaultCloseOperation(2);
	}
	
	
	// get headers to specify the column names.
	private String[] getHeaders() {
		String[] actualHeaders = metaDataCol.getHeaders();
		String[] modifiedHeaders = new String[actualHeaders.length + 1];
		modifiedHeaders[0] = "row name";
		for(int i = 1; i < modifiedHeaders.length; i++) {
			modifiedHeaders[i] = actualHeaders[i-1];
		}
		return modifiedHeaders;
	}
	
	// Get active fields from selected rows to display in the dual table panel.
	private Object[][] getActiveData(ArrayList<String> selectedRows, int headersLen) {
		Object[][] activeData = new Object[selectedRows.size()][headersLen];
		for(int i = 0; i < selectedRows.size(); i++) {
			activeData[i][0] = selectedRows.get(i);
		}
		for(int i = 0; i < selectedRows.size(); i++) {
			HashMap<String, String> rowColVals = 
					metaDataCol.getDataColumnRowMap(selectedRows.get(i));
			int j = 1;
			for(Map.Entry<String, String> entry : rowColVals.entrySet()) {
				activeData[i][j] = entry.getValue();
				j++;
			}
		}
		return activeData;
	}
	
	// Get inActive fields from unselected rows to display in the dual table panel.
	private Object[][] getInactiveData(ArrayList<String> unSelectedRows, int headersLen){
		Object[][] inActiveData = new Object[unSelectedRows.size()][headersLen];
		for(int i = 0; i < unSelectedRows.size(); i++) {
			inActiveData[i][0] = unSelectedRows.get(i);
		}
		for(int i = 0; i < unSelectedRows.size(); i++) {
			HashMap<String, String> rowColVals = 
					metaDataCol.getDataColumnRowMap(unSelectedRows.get(i));
			int j = 1;
			for(Map.Entry<String, String> entry : rowColVals.entrySet()) {
				inActiveData[i][j] = entry.getValue();
				j++;
			}
		}
		return inActiveData;
	}
	
	// create the button panel of the dual table panel.
	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
        createButton = new JButton("Create");
        createButton.setActionCommand(OK_COMMAND);
        createButton.addActionListener(this);
        createButton.setEnabled(false);
        if(listName != null)
        	createButton.setText("OK");
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand(CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        importButton = new JButton("Import");
        importButton.setActionCommand(IMPORT_COMMAND);
        importButton.addActionListener(this);
        buttonPanel.add(importButton);
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(OK_COMMAND.equals(e.getActionCommand())) {
			Object[][] activeValues = dtp.getActiveValues();
			String[] listValues = new String[activeValues.length];
			
			for(int index = 0; index < activeValues.length; index++) {
				listValues[index] = (String) activeValues[index][0];
			}
			
			// Add the list to the project.
			MetaOmGraph.getActiveProject().addSampleDataList(listName, listValues, true, true);
			dispose();
		}
		else if(CANCEL_COMMAND.equals(e.getActionCommand())) {
			dispose();
		}
		else if(IMPORT_COMMAND.equals(e.getActionCommand())) {
			SampleDataImportListDialog importDialog = new SampleDataImportListDialog(metaDataCol);
			importDialog.displayDialog();
			String[] importData = importDialog.getResultArray();
            if ((importData == null) || (importData.length <= 0)) {
                return;
            }
            Vector<Integer> v = new Vector();
            Object[][] inactiveValues = dtp.getInactiveValues();
            for (int x = 0; x < importData.length; x++) {
                for (int y = 0; y < inactiveValues.length; y++) {
                    if (importData[x].equalsIgnoreCase((String)inactiveValues[y][0])) {
                        v.add(y);
                        break;
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

}
