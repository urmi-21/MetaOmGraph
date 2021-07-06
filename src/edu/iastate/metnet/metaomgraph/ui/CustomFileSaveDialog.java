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
 * Creates custom save dialog box with the options provided by the user.
 */
public class CustomFileSaveDialog {
	private String fileName;
	private HashMap<String, String> fileTypes = new HashMap<String, String>();
	private String dialogBoxTitle;
	
	/**
	 * Constructor to create default save dialog box with filename and title.
	 * @param defaultFileName
	 * @param dialogBoxTitle
	 */
	public CustomFileSaveDialog(String defaultFileName, String dialogBoxTitle) {
		this.fileName = defaultFileName;
		this.dialogBoxTitle = dialogBoxTitle;
		fileTypes = null;
	}
	
	/**
	 * Constructor to create save dialog box with filename, title and default filetypes.
	 * @param defaultFileName
	 * @param dialogBoxTitle
	 * @param fileTypes
	 */
	public CustomFileSaveDialog(String defaultFileName, String dialogBoxTitle, HashMap<String, String> fileTypes) {
		this.fileName = defaultFileName;
		this.dialogBoxTitle = dialogBoxTitle;
		this.fileTypes = fileTypes;
	}
	
	/**
	 * Display dialog and returns the file saved by user, returns null if not able to save the file.
	 * @return File 
	 */
	public File showSaveDialog() {
		JFileChooser saveDialog = new JFileChooser();
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
	
	/**
	 * Display file folder selection dialog and returns the directory chosen by the user.
	 * @return File 
	 */
	public static File showDirectoryDialog(File currDirectory) {
		JFileChooser fileChooser = new JFileChooser(currDirectory);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle("Select folder");
		int option = fileChooser.showOpenDialog(MetaOmGraph.getMainWindow());
		File file = null;
		if(option == JFileChooser.APPROVE_OPTION){
			file = fileChooser.getSelectedFile();
		}
		return file;
	}

}
