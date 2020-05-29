/**
 * 
 */
package edu.iastate.metnet.metaomgraph.ui;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

/**
 * @author sumanth
 *
 */
public class CustomFileSaveDialog {
	private String fileName;
	private HashMap<String, String> fileTypes = new HashMap<String, String>();
	private String dialogBoxTitle;
	
	public CustomFileSaveDialog(String defaultFileName, String dialogBoxTitle) {
		this.fileName = defaultFileName;
		this.dialogBoxTitle = dialogBoxTitle;
		fileTypes = null;
	}
	
	public CustomFileSaveDialog(String defaultFileName, String dialogBoxTitle, HashMap<String, String> fileTypes) {
		this.fileName = defaultFileName;
		this.dialogBoxTitle = dialogBoxTitle;
		this.fileTypes = fileTypes;
	}
	
	public File showSaveDialog() {
		JFileChooser saveDialog = new JFileChooser();
		//saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
		saveDialog.setDialogTitle(dialogBoxTitle);
		saveDialog.setSelectedFile(new File(fileName));
		if(fileTypes != null) {
			fileTypes.forEach((key, value) -> saveDialog.addChoosableFileFilter(
					new FileNameExtensionFilter(key, value)));
		}
		
		int userSelectedOption = saveDialog.showSaveDialog(MetaOmGraph.getMainWindow());
		File savedFile = null;
		if(userSelectedOption == JFileChooser.APPROVE_OPTION) {
			savedFile = saveDialog.getSelectedFile();
		}
		return savedFile;
	}

}
