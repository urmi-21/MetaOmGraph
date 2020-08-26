/**
 * 
 */
package edu.iastate.metnet.metaomgraph.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;

/**
 * @author sumanth
 * Class to create the import sample data list dialog and filter out the sample data lists from the 
 * user given input text.
 */
public class SampleDataImportListDialog extends JDialog implements ActionListener{
	
	private JTextArea textArea;
	private TreeSet<String> result;
	private MetadataCollection metaDataColl;
	
	/**
	 * Constructor
	 * @param metaDataColl MetadataCollection
	 */
	public SampleDataImportListDialog(MetadataCollection metaDataColl) {
		super(MetaOmGraph.getMainWindow(), "Import SampleData List", true);
		this.metaDataColl = metaDataColl;
	}
	
	// Create the import dialog
	private void createImportDialog() {
		textArea = new JTextArea();
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		JLabel instructions = new JLabel();
		instructions.setText("<html>" + "Type/paste the entries you want to add, one per line.  Entered values must match a row value of the column: " + 
		"<B>" + metaDataColl.getDatacol() + "</B>" + " exactly." + "</html>");
		getContentPane().add(instructions, "First");
		getContentPane().add(textArea, "Center");
		getContentPane().add(buttonPanel, "Last");
		setDefaultCloseOperation(2);
	}
	
	/**
	 * parse the given input text string and return it as array of strings.
	 * only valid data columns will be returned
	 * @return array of data column names
	 */
	public String[] getResultArray() {
        if (result != null) {
            String[] resultArr = new String[result.size()];
            int index = 0;
            for (String datColName : result) {
                resultArr[index++] = datColName;
            }
            return resultArr;
        }
        return null;
    }

	/**
	 * display the import dialog
	 * @return
	 */
    public void displayDialog() {
    	createImportDialog();
        this.setSize(MetaOmGraph.getMainWindow().getWidth() / 2,
                MetaOmGraph.getMainWindow().getHeight() / 2);
        this.setVisible(true);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("cancel")) {
			result = null;
			dispose();
			return;
		}
		if (e.getActionCommand().equals("ok")) {
			textArea.setText(textArea.getText().trim());
			if (textArea.getText().length() <= 0) {
				JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(),
						"You must enter some value!", "Error",
						0);
				return;
			}
			result = new TreeSet<String>();
			StringTokenizer st = new StringTokenizer(textArea.getText(), "\n");
			List<String> notFound=new ArrayList<>();
			int totalMatches=0;
			while (st.hasMoreTokens()) {
				String thisToken = st.nextToken().trim();
				if(metaDataColl.getDataColumnRow(thisToken) != null) {
					result.add(thisToken);
					totalMatches++;
				}else {
					notFound.add(thisToken);
				}
			}

			if (result.size() <= 0) {
				JOptionPane.showMessageDialog(
						MetaOmGraph.getMainWindow(),
						"None of the values you entered correspond to any row names.",
						"No matches found", 0);
			} else {
				JOptionPane.showMessageDialog(
						MetaOmGraph.getMainWindow(),totalMatches+" values matched", totalMatches+" matches found", JOptionPane.INFORMATION_MESSAGE);

				if(notFound.size()>0) {
					JPanel listPanel=new JPanel();
					listPanel.setLayout(new BorderLayout());
					DefaultListModel<String> listmod = new DefaultListModel<String>();
					for(String s:notFound) {
						listmod.addElement(s);
					}
					JList<String> list= new JList<String>(listmod);
					list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					listPanel.add(new JScrollPane(list),BorderLayout.CENTER);

					JOptionPane.showConfirmDialog(null, listPanel, "Following ids were not matched",JOptionPane.PLAIN_MESSAGE);
				}

				dispose();
			}
			return;
		}		
	}
	
}
