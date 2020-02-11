package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;


public class BlockingProgressDialog
        extends JDialog
        implements ActionListener {
    public static final String CANCEL_COMMAND = "cancel";
    private JProgressBar progress;
    private JLabel messageLabel;
    private JLabel progressLabel;
    private boolean canceled;
    private JButton cancelButton;
    private long min;
    private long max;
    private long progressval;
    private int percentComplete;

    public BlockingProgressDialog(Dialog owner, String title, String message, long min, long max, boolean modal) {
        super(owner, title, modal);
        init(title, message, min, max, modal);
    }

    public BlockingProgressDialog(Frame owner, String title, String message, long min, long max, boolean modal) {
        super(owner, title, modal);
        init(title, message, min, max, modal);
    }

    private void init(String title, String message, long min, long max, boolean modal) {
        canceled = false;
        this.min = min;
        //this.max = max;
        //urmi scale values by 1/100
        this.max = max;
        progressval = 0L;
        setDefaultCloseOperation(0);
        if (message == null) {
            messageLabel = new JLabel("");
        } else {
            messageLabel = new JLabel(message);
        }


        if ((min > -2147483648L) && (max < 2147483647L)) {
            progress = new JProgressBar((int) min, (int) max);
            progress.setStringPainted(true);
        } else {
            progressLabel = new JLabel("0% complete");
        }
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1.0D;
        c.fill = 2;
        c.anchor = 10;
        getContentPane().add(messageLabel, c);
        c.gridy = 1;
        c.weighty = 1.0D;
        if (progress != null) {
            getContentPane().add(progress, c);
        } else {
            c.fill = 0;
            getContentPane().add(progressLabel, c);
        }
        c.weighty = 0.0D;
        c.gridy = 2;
        c.fill = 0;
        getContentPane().add(cancelButton, c);
        addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent arg0) {
                if (!cancelButton.isEnabled()) {
                    return;
                }
                actionPerformed(new ActionEvent(this,
                        1001, "cancel"));
            }
        });
        pack();
        setLocationRelativeTo(getOwner());
    }

    public void setProgress(long value) {
        if (progress != null) {
            progress.setValue((int) value);
        } else {
            progressval = value;
            updateProgressLabel();
        }
        if (value >= max) {
            dispose();
        }
    }

    @Override
	public void actionPerformed(ActionEvent arg0) {
        if ("cancel".equals(arg0.getActionCommand())) {
            canceled = true;
            dispose();
            return;
        }
    }

    public void setMessage(String message) {
        messageLabel.setText(message);

        if (this.getSize().width < this.getPreferredSize().width
                || this.getSize().height < this.getPreferredSize().height) {
            this.pack();
//		this.setLocationRelativeTo(this.getParent());
        }
    }

    public void setMaximum(long max) {
        if (progress != null) {
            progress.setMaximum((int) max);
        } else {
            this.max = max;
            updateProgressLabel();
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

    public long getProgress() {
        if (progress != null) {
            return progress.getValue();
        }
        return progressval;
    }

    public void increaseProgress(long amount) {
        if (progress != null) {
            progress.setValue(progress.getValue() + (int) amount);
        } else {
            progressval += amount;
            updateProgressLabel();
        }
    }

    public void setCancelable(boolean cancelable) {
        cancelButton.setEnabled(cancelable);
    }

    private void updateProgressLabel() {
        percentComplete = ((int) ((progressval) / max * 100.0D));

        progressLabel.setText(percentComplete + "% complete");
        repaint(progressLabel.getX(), progressLabel.getY(), progressLabel
                .getWidth(), progressLabel.getHeight());
    }
}
