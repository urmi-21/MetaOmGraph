package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.ui.SimpleChipAnimationPanel;
import edu.iastate.metnet.metaomgraph.utils.ExceptionHandler;
import edu.iastate.metnet.metaomgraph.utils.ExceptionListener;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public abstract class AnimatedSwingWorker implements ExceptionListener {
	private Object value;
	private ThreadVar threadVar;
	private SimpleChipAnimationPanel animPanel;
	private JDialog dialog;
	private JLabel myLabel;
	private Timer repaintTimer;
	private Thread dialogThread;
	private boolean blockCaller;

	private static class ThreadVar {
		private Thread thread;

		ThreadVar(Thread t) {
			thread = t;
		}

		synchronized Thread get() {
			return thread;
		}

		synchronized void clear() {
			thread = null;
		}
	}

	protected synchronized Object getValue() {
		return value;
	}

	private synchronized void setValue(Object x) {
		value = x;
	}

	public abstract Object construct();

	public void finished() {
		ExceptionHandler.getInstance().removeExceptionListener(this);
	}

	private void removeDialog() {
		repaintTimer.stop();
		dialog.setVisible(false);
		dialog.dispose();
	}

	public void interrupt() {
		Thread t = threadVar.get();
		if (t != null) {
			t.interrupt();
		}
		threadVar.clear();
	}

	public Object get() {
		while (true) {
			Thread t = threadVar.get();
			if (t == null)
				return getValue();

			try {
				t.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // propagate
				return null;
			}
		}
	}

	public AnimatedSwingWorker(String label, boolean blockCaller) {
		this.blockCaller = blockCaller;
		final Runnable doFinished = new Runnable() {
			@Override
			public void run() {
				finished();
				AnimatedSwingWorker.this.removeDialog();
			}

		};
		Runnable doConstruct = new Runnable() {
			@Override
			public void run() {
				try {
					AnimatedSwingWorker.this.setValue(construct());
				} finally {
					threadVar.clear();
				}
				SwingUtilities.invokeLater(doFinished);
			}
		};
		Thread t = new Thread(doConstruct);
		threadVar = new ThreadVar(t);

		animPanel = new SimpleChipAnimationPanel();
		dialog = new JDialog(MetaOmGraph.getMainWindow(), "Working...", true);
		myLabel = new JLabel(label);
		myLabel.setForeground(Color.BLACK);
		myLabel.setFont(myLabel.getFont().deriveFont(1));
		JPanel labelPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setComposite(AlphaComposite.getInstance(10, 0.75F));
				super.paintComponent(g2d);
			}
		};
		labelPanel.setBackground(Color.WHITE);
		labelPanel.add(myLabel);
		animPanel.add(labelPanel);
		dialog.getContentPane().add(animPanel, "Center");
		dialog.pack();
		dialog.setLocationRelativeTo(MetaOmGraph.getMainWindow());
		dialog.setDefaultCloseOperation(0);
		ActionListener repainter = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (animPanel != null)
					animPanel.repaint();
			}
		};
		repaintTimer = new Timer(500, repainter);
		repaintTimer.setRepeats(true);

		dialogThread = new Thread() {
			@Override
			public void run() {
				dialog.setVisible(true);
			}
		};
		ExceptionHandler.getInstance().addExceptionListener(this);
	}

	public AnimatedSwingWorker(String label) {
		this(label, false);
	}

	public void start() {
		Thread t = threadVar.get();
		if (t != null) {
			t.start();
			repaintTimer.start();

			// urmi
			if (!blockCaller) {
				dialogThread.start();
			} else {
				try {
				dialog.setVisible(true);
				}catch(Exception e) {
					
				}
			}
		}
	}

	public void setMessage(String message) {
		myLabel.setText(message);
		dialog.pack();
		dialog.setLocationRelativeTo(dialog.getParent());
	}

	@Override
	public void exception(Throwable e) {
		interrupt();
		dialog.dispose();
	}
}
