/**
 * 
 */
package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JOptionPane;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;

/**
 * @author sumanth
 *
 */
public class CustomMessagePane {
	public enum MessageBoxType{
		ERROR,
		INFORMATION,
		WARNING,
		QUESTION,
		PLAIN
	}

	public enum MessageBoxButtons{
		OK,
		YES_NO,
		YES_NO_CANCEL,
		OK_CANCEL
	}

	public enum UserClickedButton{
		OK,
		YES,
		NO,
		CANCEL
	}
	
	private MessageBoxType messageType;
	private MessageBoxButtons buttonsType;
	private String textToDisplay;
	private String messageBoxTitle;
	
	public CustomMessagePane(String messageBoxTitle, String textToDisplay) {
		this.messageBoxTitle = messageBoxTitle;
		this.textToDisplay = textToDisplay;
		this.buttonsType = MessageBoxButtons.OK;
		this.messageType = MessageBoxType.PLAIN;
	}
	
	public CustomMessagePane(String messageBoxTitle, String textToDisplay, MessageBoxType messageType) {
		this.messageBoxTitle = messageBoxTitle;
		this.textToDisplay = textToDisplay;
		this.messageType = messageType;
		this.buttonsType = MessageBoxButtons.OK;
	}
	
	public CustomMessagePane(String messageBoxTitle, String textToDisplay, MessageBoxButtons buttonsType) {
		this.messageBoxTitle = messageBoxTitle;
		this.textToDisplay = textToDisplay;
		this.buttonsType = buttonsType;
		this.messageType = MessageBoxType.PLAIN;
	}
	
	public CustomMessagePane(String messageBoxTitle, String textToDisplay, MessageBoxType messageType, 
			MessageBoxButtons buttonsType) {
		this.messageType = messageType;
		this.buttonsType = buttonsType;
		this.messageBoxTitle = messageBoxTitle;
		this.textToDisplay = textToDisplay;
	}
	
	private UserClickedButton determineButtonClicked(int resultReturned) {
		if(buttonsType == MessageBoxButtons.OK)
			return UserClickedButton.OK;
		else if(buttonsType == MessageBoxButtons.YES_NO) {
			return (resultReturned == 0) ? UserClickedButton.YES :  UserClickedButton.NO;
		}
		else if(buttonsType == MessageBoxButtons.OK_CANCEL) {
			return (resultReturned == 0) ? UserClickedButton.OK :  UserClickedButton.CANCEL;
		}
		else {
			if(resultReturned == 0)
				return UserClickedButton.YES;
			else if(resultReturned == 1)
				return UserClickedButton.NO;
			else
				return UserClickedButton.CANCEL;
		}
	}
		
	public UserClickedButton displayMessageBox() {
		int selectedButton = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(), textToDisplay, messageBoxTitle, 
				buttonsType.ordinal() - 1, messageType.ordinal());
		return determineButtonClicked(selectedButton);
	}
	
	public UserClickedButton displayMessageBox(String textToDisplay) {
		this.textToDisplay = textToDisplay;
		return displayMessageBox();
	}
	
	public UserClickedButton displayMessageBox(String messageBoxTitle, String textToDisplay) {
		this.textToDisplay = textToDisplay;
		this.messageBoxTitle = messageBoxTitle;
		return displayMessageBox();
	}
}
