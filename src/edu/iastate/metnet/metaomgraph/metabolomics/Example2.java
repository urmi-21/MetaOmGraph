package edu.iastate.metnet.metaomgraph.metabolomics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import ca.ansir.swing.tristate.TriStateCheckBox;
import ca.ansir.swing.tristate.TriStateEvent;
import ca.ansir.swing.tristate.TriStateListener;
import ca.ansir.swing.tristate.TriState;

/**
 * The launching point for example2. This example demonstrates the capabilities
 * of a <code>TriStateCheckBox</code>.
 *
 * @author Dan Andrews
 */
@SuppressWarnings("serial")
public class Example2 extends JFrame implements TriStateListener {

    /**
     * Text for the unselected radio button.
     */
    private static final String UNSELECTED = "Unselected";

    /**
     * Text for the selected radio button.
     */
    private static final String SELECTED = "Selected";

    /**
     * Text for the mixed radio button.
     */
    private static final String MIXED = "Mixed";

    /**
     * The selected radio button.
     */
    private JRadioButton selectedRadio;

    /**
     * The unselected radio button.
     */
    private JRadioButton unselectedRadio;

    /**
     * The mixed radio button.
     */
    private JRadioButton mixedRadio;

    /**
     * The <code>TriStateCheckBox</code> to demo.
     */
    TriStateCheckBox cb = new TriStateCheckBox("Tri-State CheckBox");

    /**
     * Constructor.
     */
    public Example2() {
        super("TriStateCheckBox Bean - www.ansir.ca");
        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        content.add(getTestPanel(), c);
        c.gridx++;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        content.add(getLookAndFeelPanel(), c);
        c.gridx = 0;
        c.gridy++;
        c.gridheight = 1;
        content.add(getStatePanel(), c);
        getContentPane().add(content, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cb.addTriStateListener(this);
        cb.setMnemonic('T');
    }

    /**
     * Gets the test panel.
     *
     * @return The panel.
     */
    private JComponent getTestPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Bean"));
        p.add(cb);
        return p;
    }

    /**
     * Gets the LAF panel.
     *
     * @return The panel.
     */
    private JComponent getLookAndFeelPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Look And Feel"));
        LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
        Arrays.sort(info, new Comparator<LookAndFeelInfo>() {
            public int compare(LookAndFeelInfo o1, LookAndFeelInfo o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        ButtonGroup bg = new ButtonGroup();
        AbstractButton selectedButton = null;
        for (int i = 0; i < info.length; i++) {
            AbstractButton b = new JRadioButton(info[i].getName());
            p.add(b, c);
            bg.add(b);
            b.addItemListener(new LookAndFeelItemListener(info[i]));
            if (info[i].getClassName().equals(
                    UIManager.getSystemLookAndFeelClassName())) {
                selectedButton = b;
            }
            c.gridy++;
        }
        selectedButton.setSelected(true);
        JCheckBox enabledBox = new JCheckBox("Enabled");
        enabledBox.addItemListener(new EnabledListener());
        enabledBox.setSelected(true);
        p.add(enabledBox, c);
        c.gridy++;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        p.add(new JLabel(), c);
        Dimension dim = p.getPreferredSize();
        dim.width *= 1.2;
        dim.height *= 1.2;
        p.setPreferredSize(dim);
        return p;
    }

    /**
     * Gets the state panel.
     *
     * @return The panel.
     */
    private JComponent getStatePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Tri State"));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        ButtonGroup bg = new ButtonGroup();
        ItemListener l = new RadioListener();
        mixedRadio = new JRadioButton(MIXED);
        mixedRadio.addItemListener(l);
        bg.add(mixedRadio);
        p.add(mixedRadio, c);
        c.gridy++;
        selectedRadio = new JRadioButton(SELECTED);
        selectedRadio.addItemListener(l);
        bg.add(selectedRadio);
        p.add(selectedRadio, c);
        c.gridy++;
        unselectedRadio = new JRadioButton(UNSELECTED);
        unselectedRadio.setSelected(true);
        unselectedRadio.addItemListener(l);
        bg.add(unselectedRadio);
        p.add(unselectedRadio, c);
        c.gridy++;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        p.add(new JLabel(), c);
        Dimension dim = p.getPreferredSize();
        dim.width *= 1.2;
        dim.height *= 1.2;
        p.setPreferredSize(dim);
        return p;
    }

    /**
     * Implemented by classes interested in state changes of a
     * <code>TriStateCheckBox</code> object.
     *
     * @param event The <code>TriStateEvent</code> object.
     */
    public void stateChanged(TriStateEvent event) {
        switch (event.getState()) {
            case SELECTED:
                selectedRadio.setSelected(true);
                break;
            case UNSELECTED:
                unselectedRadio.setSelected(true);
                break;
            case MIXED:
                mixedRadio.setSelected(true);
                break;
        }
    }

    /**
     * Inner class - used the set <code>UIManager</code> object's LAF.
     */
    private class LookAndFeelItemListener implements ItemListener {

        LookAndFeelInfo info;

        private LookAndFeelItemListener(LookAndFeelInfo info) {
            this.info = info;
        }

        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                    SwingUtilities.updateComponentTreeUI(Example2.this);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Inner class - used to respond to the state radio buttons.
     */
    private class RadioListener implements ItemListener {

        public void itemStateChanged(ItemEvent event) {
            JRadioButton b = (JRadioButton) event.getSource();
            if (event.getStateChange() == ItemEvent.SELECTED) {
                if (b == mixedRadio) {
                    cb.setState(TriState.MIXED);
                } else if (b == selectedRadio) {
                    cb.setState(TriState.SELECTED);
                } else if (b == unselectedRadio) {
                    cb.setState(TriState.UNSELECTED);
                }
            }
        }

    }

    /**
     * Inner class - used to respond enabled check box.
     */
    private class EnabledListener implements ItemListener {

        public void itemStateChanged(ItemEvent event) {
            cb.setEnabled(event.getStateChange() == ItemEvent.SELECTED);
        }

    }

    /**
     * Main - to show the example.
     *
     * @param args Not used.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new Example2();
        frame.pack();
        frame.setPreferredSize(frame.getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
