package edu.iastate.metnet.metaomgraph.utils;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.ui.CustomFileSaveDialog;
import edu.iastate.metnet.metaomgraph.ui.CustomMessagePane;

import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
	private Frame fp = null;
	private Dialog dp = null;
	private JInternalFrame ipf = null;
	private JDialog dialog;
	private boolean useBuffer;
	private StringOutputStream buffer;
	private ArrayList<ExceptionListener> listeners;
	private static PrintStream originalOut;
	private static PrintStream newOut;
	private static PrintStream originalErr;
	private static ExceptionHandler instance;

	private ExceptionHandler() {
	}

	/**
	 * @author urmi
	 * @param parent
	 * @return
	 */
	public static ExceptionHandler getInstance(JInternalFrame parent) {
		if (instance == null) {
			instance = new ExceptionHandler();
		}
		instance.fp = null;
		instance.dp = null;
		instance.ipf = parent;
		return instance;
	}

	public static ExceptionHandler getInstance(Frame parent) {
		if (instance == null) {
			instance = new ExceptionHandler();
		}
		instance.fp = parent;
		instance.dp = null;
		return instance;
	}

	public static ExceptionHandler getInstance(Dialog parent) {
		if (instance == null) {
			instance = new ExceptionHandler();
		}
		instance.dp = parent;
		instance.fp = null;
		return instance;
	}

	public static ExceptionHandler getInstance() {
		if (instance == null) {
			instance = new ExceptionHandler();
		}
		return instance;
	}

	public void setUseBuffer(boolean use) {
		useBuffer = use;
		if (use) {
			if (buffer == null) {
				buffer = new StringOutputStream();
				newOut = new PrintStream(new BufferedOutputStream(buffer));
				originalOut = System.out;
				originalErr = System.err;
			}
			System.setOut(newOut);
			System.setErr(newOut);
		} else if (buffer != null) {
			System.setOut(originalOut);
			System.setErr(originalErr);
		}
	}

	private static class StringOutputStream extends OutputStream {
		private StringBuilder output;
		private int maxLength;

		public StringOutputStream() {
			maxLength = 20000;
			output = new StringBuilder();
		}

		@Override
		public void write(int b) throws IOException {
			output.append((char) b);
			if (output.length() > maxLength) {
				output.deleteCharAt(0);
			}
		}

		@Override
		public String toString() {
			return output.toString();
		}
	}
	
	// Convert map to string in Json Format.
	private String toJsonFormat(Map<String, String> map) {
		String json = "{\r\n" + map.entrySet().stream()
			    .map(ent -> "\""+ ent.getKey() + "\"" + ": " + String.valueOf(ent.getValue()) + "")
			    .collect(Collectors.joining(",\r\n")) + "\r\n}" ;
		return json;
	}
	
	/**
	 * Save the error log to a file.
	 * @param errorLog
	 */
	private void saveErrorLogToFile(String errorLog) {
		HashMap<String, String> fileFilters = new HashMap<String, String>();
		fileFilters.put("Log files", "log");
		fileFilters.put("Text files", "txt");
		String appendDate = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(new Date());
		String fileName = "error_" + appendDate + ".log";
		
		CustomFileSaveDialog saveFileDailog = new CustomFileSaveDialog(fileName, "Save error log", fileFilters);
		File savedFile = saveFileDailog.showSaveDialog();
		if(savedFile == null)
			return;
		try {
			FileWriter fileWriter = new FileWriter(savedFile);
			fileWriter.write(errorLog);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void displaySuccessMessage(String url) {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		String message = "<body>" + "Error is reported to the developer." + "<br>" + 
				"You may follow the the issue at: " + "</br>" +
				"<a href=" + url + ">" + url + "</a>" + "</body>";
		editorPane.setText(message);
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType().equals(EventType.ACTIVATED)) {
					try {
						Desktop.getDesktop().browse(new URI(url));
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JOptionPane.showConfirmDialog(MetaOmGraph.getMainWindow(), editorPane, 
				"Report error", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);	
	}
	
	/**
	 * @author sumanth
	 * Create the git issues from error logs.
	 * @param errorLog
	 */
	private void createGitIssue(String errorLog) {
		PropertyFileReader properties = null;
		try {
			properties = new PropertyFileReader("/resource/config.properties");
		} catch (IOException e1) {
			errorLog += "\n" + e1.getMessage();
		}
		
		// get token and url from config.properties file.
		String token = "token " + properties.getProperty("gittoken");
		String url = properties.getProperty("gitissueUrl");
		
		Map<String, String> postContentsMap = new HashMap<String, String>();
		postContentsMap.put("title", "\"MetaOmBot: Bug report from user\"");
		postContentsMap.put("body", '\"' + errorLog.toString().replaceAll("[\r\n]+", "\\\\n") + '\"');
		postContentsMap.put("labels", "[\"bug\"]");
	
		String jsonFormatedMap = toJsonFormat(postContentsMap);
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpPost postRequest = new HttpPost(url);
		postRequest.addHeader("Content-Type", "application/json");
		postRequest.addHeader("Authorization", token);
		try {
			postRequest.setEntity(new StringEntity(jsonFormatedMap));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		HttpResponse response = null;
		try {
			response = httpClient.execute(postRequest);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
				
		if(response == null || response.getStatusLine().getStatusCode() != 201) {
			CustomMessagePane messageBox = new CustomMessagePane("Report error", 
					"Cannot report the error, please check your internet connection." + 
					"\nDo you want to save the error log file and report it later?",
					CustomMessagePane.MessageBoxType.ERROR, 
					CustomMessagePane.MessageBoxButtons.YES_NO);
			
			if(messageBox.displayMessageBox() == CustomMessagePane.UserClickedButton.YES)
				saveErrorLogToFile(errorLog);
		}
		else {
			try {
				BufferedReader responseReader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				
				String output;
				StringBuffer responseOutput = new StringBuffer();

				while ((output = responseReader.readLine()) != null) {
					responseOutput.append(output);
				}
				responseReader.close();
				JsonObject jsonObj = JsonParser.parseString(responseOutput.toString()).getAsJsonObject();
				String urlTemp = jsonObj.get("html_url").getAsString();
				displaySuccessMessage(urlTemp);
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {

		e.printStackTrace();
		JLabel message = new JLabel(
				"<html>MetaOmGraph has encountered an error.<br>Click \"Notify\" to send the error information to the developers.  No personal information will be sent.</html>");

		message.setIcon(new ImageIcon(getClass().getResource("/resource/tango/32x32/actions/process-stop.png")));

		if (fp == null) {
			dialog = new JDialog(dp, "Error", true);
		} else {
			dialog = new JDialog(fp, "Error", true);
		}
		dialog.setModal(true);
		dialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		dialog.getContentPane().setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = 21;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weightx = 0.5D;
		c.weighty = 0.0D;
		c.anchor = 10;
		c.insets = new Insets(2, 5, 2, 5);
		dialog.getContentPane().add(message, c);

		JLabel emailLabel = new JLabel("Email address (optonal):");
		final JTextField emailField = new JTextField();
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 0.0D;
		c.fill = 0;
		c.anchor = 13;
		dialog.getContentPane().add(emailLabel, c);
		c.gridx = 1;
		c.weightx = 1.0D;
		c.fill = 2;
		dialog.getContentPane().add(emailField, c);

		JLabel commentLabel = new JLabel("Comments (optional):");
		final JTextArea commentArea = new JTextArea();
		JScrollPane commentPane = new JScrollPane(commentArea);
		commentPane.setPreferredSize(
				new Dimension(commentArea.getPreferredSize().width, commentLabel.getPreferredSize().height * 3));
		c.gridy = 3;
		c.gridx = 0;
		c.anchor = 12;
		c.fill = 0;
		c.weightx = 0.0D;
		dialog.getContentPane().add(commentLabel, c);
		c.gridx = 1;
		c.weightx = 1.0D;
		c.fill = 1;
		dialog.getContentPane().add(commentPane, c);

		final JLabel emptyLabel = new JLabel("");
		c.gridy = 4;
		c.gridwidth = 2;
		c.weightx = 1.0D;
		c.gridx = 0;
		c.anchor = 10;
		dialog.getContentPane().add(emptyLabel, c);
		
		/////////// add error message urmi
		boolean shown = false;
		// urmi changed to ture easy for debugging
		// boolean shown = true;
		JScrollPane pane;

		if (!shown) {
			JTree tree = new JTree(Utils.createTreeFromThrowable(e));
			pane = new JScrollPane(tree);
			// pane.setPreferredSize(new Dimension(600, 300));
			pane.setPreferredSize(new Dimension(1000, 300));
			c.gridy = 1;
			c.weighty = 1.0D;
			dialog.add(pane, c);
			dialog.pack();

			if (fp != null) {
				dialog.setLocationRelativeTo(dp);
			} else {
				dialog.setLocationRelativeTo(fp);
			}
			if (ipf != null) {
				dialog.setLocationRelativeTo(ipf);
			} else if (fp != null) {
				dialog.setLocationRelativeTo(fp);
			} else {
				dialog.setLocationRelativeTo(dp);
			}
			// detailsButton.setText("Details");
			shown = true;
		} else {
			// dialog.remove(pane);
			dialog.pack();
			if (fp == null) {
				dialog.setLocationRelativeTo(dp);
			} else {
				dialog.setLocationRelativeTo(fp);
			}
			// detailsButton.setText("Details");
			shown = false;
		}

		//////////////// end error message

		JPanel buttonPanel = new JPanel();

		final Throwable thrown = e;
		JButton okButton = new JButton("Notify");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				StringBuffer text = new StringBuffer("MetaOmGraph Error:\n\n");
				text.append("Email: " + emailField.getText() + "\nComments:\n" + commentArea.getText());
				text.append("\nMOG version: " + System.getProperty("MOG.version") + "\nMOG date: "
						+ System.getProperty("MOG.date") + "\nOS: " + MetaOmGraph.getOsName() + "\n\n");
				
				text.append("Error log:\n```\n");
				StackTraceElement[] trace = thrown.getStackTrace();
				text.append(thrown.toString());
				for (StackTraceElement ste : trace) {
					text.append("\n" + ste);
				}
								
				createGitIssue(text.toString());

			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}

		});
		final JButton detailsButton = new JButton("Details");
		detailsButton.addActionListener(new ActionListener() {
			boolean shown = false;
			// urmi changed to ture easy for debugging
			// boolean shown = true;
			JScrollPane pane;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!shown) {
					JTree tree = new JTree(Utils.createTreeFromThrowable(thrown));
					pane = new JScrollPane(tree);
					// pane.setPreferredSize(new Dimension(800, 600));
					pane.setPreferredSize(new Dimension(1000, 600));
					c.gridy = 1;
					c.weighty = 1.0D;
					dialog.add(pane, c);
					dialog.pack();

					if (fp == null) {
						dialog.setLocationRelativeTo(dp);
					} else {
						dialog.setLocationRelativeTo(fp);
					}
					detailsButton.setText("Details");
					shown = true;
				} else {
					dialog.remove(pane);
					dialog.pack();
					if (fp == null) {
						dialog.setLocationRelativeTo(dp);
					} else {
						dialog.setLocationRelativeTo(fp);
					}
					detailsButton.setText("Details");
					shown = false;
				}

			}
		});
		
		final JButton saveLogButton = new JButton("Save log");
		saveLogButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuffer text = new StringBuffer("MetaOmGraph Error:\n\n");
				text.append("Email: " + emailField.getText() + "\nComments:\n" + commentArea.getText());
				text.append("\nMOG version: " + System.getProperty("MOG.version") + "\nMOG date: "
						+ System.getProperty("MOG.date") + "\nOS: " + MetaOmGraph.getOsName() + "\n\n");
				
				text.append("Error log:\n");
				StackTraceElement[] trace = thrown.getStackTrace();
				text.append(thrown.toString());
				for (StackTraceElement ste : trace) {
					text.append("\n" + ste);
				}
				saveErrorLogToFile(text.toString());
			}
		});
		
		buttonPanel.add(okButton);
		//urmi remove details no need
		//buttonPanel.add(detailsButton);
		buttonPanel.add(saveLogButton);
		buttonPanel.add(cancelButton);
		c.gridy = 5;
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 0.5;
		dialog.getContentPane().add(buttonPanel, c);
		dialog.pack();
		if (dp != null) {
			dialog.setLocationRelativeTo(dp);
		} else if (fp != null) {
			dialog.setLocationRelativeTo(fp);
			// fp.dispose();
		} else {
			dialog.setLocationRelativeTo(ipf);
			
			ipf.dispose();
		}
		dialog.setDefaultCloseOperation(2);
		// dispose charts
		dialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);

		// System.exit(1);
		notifyListeners(e);

	}

	public void contact() {
		JLabel message = new JLabel(
				"<html>Feedback is always appreciated. If you've encountered a problem or have any questions, please let us know!<br>Please enter your comments/questions below. In addition to this, some debugging information will be included.<br>No personal information will be sent unless you provide it.</html>");

		message.setIcon(new ImageIcon(getClass().getResource("/resource/tango/32x32/actions/mail-send-receive.png")));

		if (fp == null) {
			dialog = new JDialog(dp, "Contact the Developer", true);
		} else {
			dialog = new JDialog(fp, "Contact the Developer", true);
		}
		dialog.setModal(true);
		dialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		dialog.getContentPane().setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(2, 5, 2, 5);
		dialog.getContentPane().add(message, c);
		// c.insets = new Insets(0, 0, 0, 0);

		JLabel emailLabel = new JLabel("Email address (optonal):");
		final JTextField emailField = new JTextField();
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		dialog.getContentPane().add(emailLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		dialog.getContentPane().add(emailField, c);

		JLabel commentLabel = new JLabel("Comments (optional):");
		final JTextArea commentArea = new JTextArea();
		JScrollPane commentPane = new JScrollPane(commentArea);
		commentPane.setPreferredSize(
				new Dimension(commentArea.getPreferredSize().width, commentLabel.getPreferredSize().height * 3));
		c.gridy = 3;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		dialog.getContentPane().add(commentLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		dialog.getContentPane().add(commentPane, c);

		final JCheckBox shotBox = new JCheckBox("Include a screenshot of MetaOmGraph");
		shotBox.setSelected(true);
		c.gridy = 4;
		c.gridwidth = 2;
		c.weightx = 1;
		c.gridx = 0;
		c.anchor = GridBagConstraints.CENTER;
		dialog.getContentPane().add(shotBox, c);

		JPanel buttonPanel = new JPanel();

		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				StringBuffer text = new StringBuffer("MetaOmGraph Comments:\n\n");

				String host = "mailhub.iastate.edu";
				String from = "mhhur@iastate.edu";
				String to = "mhhur@iastate.edu";

				Properties props = System.getProperties();

				props.put("mail.smtp.host", host);

				Session session = Session.getDefaultInstance(props, null);

				try {
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(from));
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
					message.setSubject("Automail: MOG Comments");
					StringBuilder messageText = new StringBuilder(text.toString() + "\n\n");
					if (useBuffer) {
						System.out.flush();
						messageText.append("Buffer: " + buffer.toString() + "\n\n");
					}
					messageText.append("Email: " + emailField.getText() + "\nComments:" + commentArea.getText());
					BodyPart messageBodyPart = new MimeBodyPart();

					messageBodyPart.setText(messageText.toString());

					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(messageBodyPart);

					File dest = null;
					if (shotBox.isSelected()) {
						messageBodyPart = new MimeBodyPart();
						Robot robot = new Robot();
						Frame f = MetaOmGraph.getMainWindow();
						Rectangle captureSize = new Rectangle(f.getX(), f.getY(), f.getWidth(), f.getHeight());
						BufferedImage shot = robot.createScreenCapture(captureSize);
						dest = File.createTempFile("mog", ".png");
						ImageIO.write(shot, "png", dest);
						DataSource source = new FileDataSource(dest);
						messageBodyPart.setDataHandler(new DataHandler(source));
						messageBodyPart.setFileName(dest.getName());
						multipart.addBodyPart(messageBodyPart);
					}

					message.setContent(multipart);

					Transport.send(message);
					if (dest != null) {
						dest.delete();
					}
					System.out.println("Mail sent!");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}

		});
		buttonPanel.add(sendButton);
		buttonPanel.add(cancelButton);
		c.gridy = 5;
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 0.5D;
		dialog.getContentPane().add(buttonPanel, c);
		dialog.pack();
		if (dp != null) {
			dialog.setLocationRelativeTo(dp);
		} else if (fp != null) {
			dialog.setLocationRelativeTo(fp);
			// fp.dispose();
		} else {
			dialog.setLocationRelativeTo(ipf);
			ipf.dispose();
		}
		
		dialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setDefaultCloseOperation(2);
		dialog.setVisible(true);
	}

	private void notifyListeners(Throwable e) {
		if (listeners == null) {
			return;
		}
		for (ExceptionListener l : listeners) {
			l.exception(e);
		}
	}

	public void addExceptionListener(ExceptionListener l) {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		listeners.add(l);
	}

	public boolean removeExceptionListener(ExceptionListener l) {
		if (l == null) {
			return false;
		}
		return listeners.remove(l);
	}

	public static void main(String[] args) throws Exception {
	}
}
