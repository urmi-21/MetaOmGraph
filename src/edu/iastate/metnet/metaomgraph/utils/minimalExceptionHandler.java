package edu.iastate.metnet.metaomgraph.utils;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;

import java.awt.AWTException;
import java.awt.Container;
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
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
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
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;

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
	 * 
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

	@Override
	public void uncaughtException(Thread t, Throwable e) {

		return;
	}

	public void contact() {
		return;
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
