package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.Metadata;
import edu.iastate.metnet.metaomgraph.utils.FilterableTreeModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExpressionFilterConstructionPanel extends JPanel
        implements ActionListener {

    private JDialog window;
    private JTextField max;
    private JLabel maxDesc;
    private JTextField min;
    private JLabel minDesc;
    private JTextField minTotal;
    private JLabel minTotalDesc;
    private JButton filter;
    private JButton cancel;
    private JPanel buttonPanel;
    private JRadioButton normalFilter;
    private JRadioButton meanFilter;
    private ButtonGroup bg;
    private boolean minPopulated = false;
    private boolean maxPopulated = false;
    private boolean minTotalPopulated = false;
    private boolean isNormalFilter = true;
    private boolean isMeanFilter = false;
    private boolean queryReady;

    private MetaOmProject project;

    public ExpressionFilterConstructionPanel(MetaOmProject project) {
        this.project = project;
        window = new JDialog(MetaOmGraph.getMainWindow(), "Expression Filter", true);
        Container mypane = window.getContentPane();
        window.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;

        // Align Labels
        JPanel entryPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();

        c2.insets = new Insets(0,0,10,0);
        setMyConstraints(c2,0,0,GridBagConstraints.NORTH);
        normalFilter = new JRadioButton("by Minimum Expression", true);
        normalFilter.setActionCommand("normalFilter");
        entryPanel.add(normalFilter, c2);

        c2.insets = new Insets(0,0,10,0);
        setMyConstraints(c2,1,0,GridBagConstraints.NORTH);
        meanFilter = new JRadioButton("by Mean Expression Range");
        meanFilter.setActionCommand("meanFilter");
        entryPanel.add(meanFilter, c2);

        normalFilter.addActionListener(this);
        meanFilter.addActionListener(this);

        bg = new ButtonGroup();
        bg.add(normalFilter);
        bg.add(meanFilter);

        c2.insets = new Insets(30,0,10,0);
        setMyConstraints(c2,0,0,GridBagConstraints.EAST);
        minDesc = new JLabel("Minimum Expression Value:   ", SwingConstants.RIGHT);
        minDesc.setSize(new Dimension(100, 70));
        entryPanel.add(minDesc, c2);

        setMyConstraints(c2,1,0,GridBagConstraints.WEST);
        min = new JTextField();
        min.setPreferredSize(new Dimension(200, 30));
        entryPanel.add(min,c2);

        c2.insets = new Insets(0,0,10,0);
        setMyConstraints(c2,0,1,GridBagConstraints.EAST);
        maxDesc = new JLabel("# Samples with Min Expression Value:   ", SwingConstants.RIGHT);
        maxDesc.setSize(new Dimension(100, 70));
        entryPanel.add(maxDesc,c2);

        setMyConstraints(c2,1,1,GridBagConstraints.WEST);
        max = new JTextField();
        max.setPreferredSize(new Dimension(200, 30));
        entryPanel.add(max,c2);

        c2.insets = new Insets(0,0,10,0);
        setMyConstraints(c2,0,2,GridBagConstraints.EAST);
        minTotalDesc = new JLabel("Min expression for a gene summed over all samples (Optional):   ", SwingConstants.RIGHT);
        minTotalDesc.setSize(new Dimension(100, 70));
        entryPanel.add(minTotalDesc,c2);

        setMyConstraints(c2,1,2,GridBagConstraints.WEST);
        minTotal = new JTextField();
        minTotal.setPreferredSize(new Dimension(200, 30));
        entryPanel.add(minTotal,c2);

        mypane.add(entryPanel, c);

        // Align Buttons
        buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c3 = new GridBagConstraints();

        c3.insets = new Insets(0,0,0,5);
        setMyConstraints(c3,0,0,GridBagConstraints.EAST);
        filter = new JButton("Filter");
        filter.setEnabled(false);
        filter.setActionCommand("filter");
        filter.addActionListener(this);
        filter.setPreferredSize(new Dimension(90,50));
        buttonPanel.add(filter,c3);

        setMyConstraints(c3,1,0,GridBagConstraints.WEST);
        cancel = new JButton("Cancel");
        cancel.setActionCommand("cancel");
        cancel.setPreferredSize(new Dimension(90,50));
        cancel.addActionListener(this);
        buttonPanel.add(cancel,c3);

        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 0,0,0);
        c.anchor = GridBagConstraints.CENTER;
        mypane.add(buttonPanel, c);

        max.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (!max.getText().equals("")) {
                    maxPopulated = true;
                    if (minPopulated) {
                        filter.setEnabled(true);
                    }
                }
                else {
                    maxPopulated = false;
                    filter.setEnabled(false);
                }
            }
        });

        min.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (!min.getText().equals("")) {
                    minPopulated = true;
                    if (maxPopulated || isNormalFilter) {
                        filter.setEnabled(true);
                    }
                }
                else {
                    minPopulated = false;
                    filter.setEnabled(false);
                }
            }
        });

        minTotal.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (!min.getText().equals("")) {
                    minTotalPopulated = true;
                }
                else {
                    minTotalPopulated = false;
                }

            }
        });

        normalFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isNormalFilter = true;
                isMeanFilter = false;
                minTotal.setVisible(true);
                minTotalDesc.setVisible(true);
            }
        });

        meanFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isMeanFilter = true;
                isNormalFilter = false;
                minTotal.setVisible(false);
                minTotalDesc.setVisible(false);
            }
        });

        // Align Frame
        window.setSize(700, 250);
        System.out.println(MetaOmGraph.getMainWindow());
        int width = MetaOmGraph.getMainWindow().getWidth();
        int height = MetaOmGraph.getMainWindow().getHeight();
        // align window to the middle of the screen
        window.setLocation((width - window.getWidth()) / 2, (height - window.getHeight()) / 2);
        window.setVisible(true);
    }

    private static void setMyConstraints(GridBagConstraints c, int gridx, int gridy, int anchor) {
        c.gridx = gridx;
        c.gridy = gridy;
        c.anchor = anchor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Double maxValue = 0d;
        Double minValue = 0d;
        Double minTotalValue = 0d;
        boolean maxError = false;
        boolean minError = false;
        boolean minTotalError = false;
        MetaOmProject myProject = MetaOmGraph.getActiveProject();
        int rowListLength = myProject.getGeneListRowNumbers(MetaOmGraph.getActiveTable().getSelectedListName()).length;
        if ("filter".equals(e.getActionCommand())) {
            try {
                maxValue = Double.parseDouble(max.getText());
            } catch (NumberFormatException ne) {
                if (isMeanFilter) {
                    maxError = true;
                } else if (isNormalFilter) {
                    Integer defaultInt = Double.valueOf(rowListLength * .1).intValue();
                    maxValue = defaultInt.doubleValue();
                    String defaultString = defaultInt.toString();
                    JOptionPane.showMessageDialog(window, "Error reading N value. Set to default.\n(Selected rows * 10%)");
                    max.setText(defaultString);
                }
            }
            try {
                minValue = Double.parseDouble(min.getText());
            } catch (NumberFormatException ne) {
                minError = true;
            }
            if (minTotalPopulated) {
                try {
                    minTotalValue = Double.parseDouble(minTotal.getText());
                } catch (NumberFormatException ne) {
                    minTotalError = true;
                }
            }
            String errorMsg = "";
            if (maxError) {
                errorMsg += "Maximum field must be a number\n";
            }
            if (minError) {
                errorMsg += "Minimum field must be a number\n";
            }
            if (minTotalError) {
                errorMsg += "Minimum total count field must be a number";
            }
            if (maxError || minError) {
                JOptionPane.showMessageDialog(window, errorMsg);
                setQueryReady(false);
            }else if (isMeanFilter && maxValue <= minValue) {
                JOptionPane.showMessageDialog(window, "Maximum must be greater than minimum");
                setQueryReady(false);
            } else if (isNormalFilter && maxValue < 0) {
                JOptionPane.showMessageDialog(window, "Minimum value total count cannot be negative");
                setQueryReady(false);
            } else {
                setQueryReady(true);
                window.dispose();
            }
            return;
        } else if ("cancel".equals(e.getActionCommand())) {
            window.dispose();
            return;
        } else if ("normalFilter".equals(e.getActionCommand())) {
            maxDesc.setText("# Samples with Min Expression Value:   ");
        } else if ("meanFilter".equals(e.getActionCommand())) {
            maxDesc.setText("Maximum Expression Value: ");
        }
    }

    public Metadata.MetadataQuery showQuery() {
        Metadata.MetadataQuery result = new Metadata.MetadataQuery();
        if (queryReady) {
            //Expression range query in format 'EXPR{RANGE:::minValue:::maxValue};'
            String type = "";
            String minTotal = "";
            if (isNormalFilter) {
                type = "EXPR";
            } else if (isMeanFilter) {
                type = "MEAN";
            }
            if (minTotalPopulated) {
                minTotal = ":::" + getMinTotal();
            }
            result.setField(type + "{" + getMin() + ":::" + getMax() + minTotal + "};");
            return result;
        }
        return null;
    }

    public String getMin() {
        return min.getText();
    }

    public String getMax() {
        return max.getText();
    }

    public String getMinTotal() {
        return minTotal.getText();
    }

    public boolean isQueryReady() {
        return queryReady;
    }

    public void setQueryReady(boolean queryReady) {
        this.queryReady = queryReady;
    }
    //listDisplay
/*
    public void filterByExpressionRange(Double max, Double min) {
        List<String> result = new ArrayList<>();
        List<double[]> data = new ArrayList<>();
        try {
            data = project.getAllData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FilterableTreeModel.Filter
        for (double[] row : data) {
            for (double col : row) {
                if (col <= max && col >= min) {
                    result.add(row.);
                }
            }
        }
    }

 */
}
