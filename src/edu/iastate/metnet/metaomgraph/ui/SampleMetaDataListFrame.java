/**
 * 
 */
package edu.iastate.metnet.metaomgraph.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dizitart.no2.Document;

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
			ArrayList<String> unSelectedRows, String[] headers) {
		this(metaDataCol, null, selectedRows, unSelectedRows, headers);
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
			ArrayList<String> unSelectedRows, String[] headers) {
		
				
		this.metaDataCol = metaDataCol;
		this.listName = listName;
		JPanel buttonPanel = createButtonPanel();
		if(selectedRows.size() > 0)
			createButton.setEnabled(true);
		String[] finalHeaders = getHeaders(headers);
		Object[][] activeData = getActiveData(selectedRows, headers);
		
		Object[][] inActiveData = getActiveData(unSelectedRows, headers);
		
		//urmi: headers are not showing up correct
		dtp = new DualTablePanel(inActiveData, activeData, finalHeaders, false);
		
        dtp.addChangeListener(this);
        dtp.hideColumn(0);
        dtp.setActiveLabel("In List");
        dtp.setInactiveLabel("Not In List");
        getContentPane().add(dtp, "Center");
        getContentPane().add(buttonPanel, "Last");
        setDefaultCloseOperation(2);
        
        
	}
	
	// get headers to specify the column names.
	private String[] getHeaders(String[] headers) {
		String[] actualHeaders = headers;
		String[] modifiedHeaders = new String[actualHeaders.length + 1];
		modifiedHeaders[0] = "row name";
		for(int i = 1; i < modifiedHeaders.length; i++) {
			modifiedHeaders[i] = actualHeaders[i-1];
		}
		return modifiedHeaders;
	}
	
	
	/**
	 * Return object[][] for selected rows to display in dual table panel
	 * @author urmi 
	 * @param selectedRows
	 * @param headers
	 * @return Object[][]
	 */
	private Object[][] getActiveData(ArrayList<String> selectedRows, String[] headers) {
		//get all metadata rows for selectedRows
		List<Document> selectedRowsMetadata = metaDataCol.getRowsByDatacols(selectedRows);
		//if return size doesn't match
		if(selectedRowsMetadata.size()!=selectedRows.size()) {
			JOptionPane.showMessageDialog(null, "Error Occured", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}		
		
		Object[][] activeData = new Object[selectedRows.size()][headers.length+1];
		//add selectedrows as first column
		for(int i = 0; i < selectedRows.size(); i++) {
			activeData[i][0] = selectedRows.get(i);
		}			
		//add rest of columns
		for (int i = 0; i < selectedRowsMetadata.size(); i++) {
			for (int j = 0; j < headers.length; j++) {
				//0th column is already added
				activeData[i][j+1] = selectedRowsMetadata.get(i).get(headers[j]).toString();					
			}
		}		
	
		return activeData;
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
	
	public String getCreatedListName() {
		return this.listName;
	}

}
