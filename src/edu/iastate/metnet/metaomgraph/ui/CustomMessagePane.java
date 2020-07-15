/**
 * 
 */
package edu.iastate.metnet.metaomgraph.ui;

import javax.swing.JOptionPane;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;

/**
 * @author sumanth
 * Creates custom message box with the options provided by the user.
 * Set icon using the enum MessageBoxType.
 * Set buttons using the enum MessageBoxButtons.
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
	
	/**
	 * Constructor to display default message box with Ok button.
	 * @param messageBoxTitle
	 * @param textToDisplay
	 */
	public CustomMessagePane(String messageBoxTitle, String textToDisplay) {
		this.messageBoxTitle = messageBoxTitle;
		this.textToDisplay = textToDisplay;
		this.buttonsType = MessageBoxButtons.OK;
		this.messageType = MessageBoxType.PLAIN;
	}
	
	/**
	 * Constructor to specify the icon/message type with Ok button.
	 * @param messageBoxTitle
	 * @param textToDisplay
	 * @param messageType
	 */
	public CustomMessagePane(String messageBoxTitle, String textToDisplay, MessageBoxType messageType) {
		this.messageBoxTitle = messageBoxTitle;
		this.textToDisplay = textToDisplay;
		this.messageType = messageType;
		this.buttonsType = MessageBoxButtons.OK;
	}
	
	/**
	 * Constructor to specify the buttons of message box.
	 * @param messageBoxTitle
	 * @param textToDisplay
	 * @param buttonsType
	 */
	public CustomMessagePane(String messageBoxTitle, String textToDisplay, MessageBoxButtons buttonsType) {
		this.messageBoxTitle = messageBoxTitle;
		this.textToDisplay = textToDisplay;
		this.buttonsType = buttonsType;
		this.messageType = MessageBoxType.PLAIN;
	}
	
	/**
	 * Constructor to create custom message box with all the custom options.
	 * @param messageBoxTitle
	 * @param textToDisplay
	 * @param messageType
	 * @param buttonsType
	 */
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
	
	/**
	 * Displays message box.
	 * @return enum UserClickedButton
	 */
	public UserClickedButton displayMessageBox() {
		int selectedButton = JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(), textToDisplay, messageBoxTitle, 
				buttonsType.ordinal() - 1, messageType.ordinal());
		return determineButtonClicked(selectedButton);
	}
	
	/**
	 * Displays message box with the text provided.
	 * @param textToDisplay
	 * @return enum UserClickedButton
	 */
	public UserClickedButton displayMessageBox(String textToDisplay) {
		this.textToDisplay = textToDisplay;
		return displayMessageBox();
	}
	
	/**
	 * Displays message box with the text provided and the message.
	 * @param messageBoxTitle
	 * @param textToDisplay
	 * @return
	 */
	public UserClickedButton displayMessageBox(String messageBoxTitle, String textToDisplay) {
		this.textToDisplay = textToDisplay;
		this.messageBoxTitle = messageBoxTitle;
		return displayMessageBox();
	}
}
