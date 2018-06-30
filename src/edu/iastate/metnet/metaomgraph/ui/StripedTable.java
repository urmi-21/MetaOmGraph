package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class StripedTable
        extends JTable {
    public static final ColorUIResource alternateRowColor = new ColorUIResource(
            216, 236, 213);


    public StripedTable() {
    }

    public StripedTable(TableModel model) {
        super(model);
        setDefaultEditor(Color.class, new ColorEditor());
        setDefaultRenderer(Color.class, new ColorRenderer(true));
    }


    public Color colorForRow(int row) {
        return row % 2 == 0 ? alternateRowColor : getBackground();
    }


    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if ((c instanceof ColorRenderer)) {
            return c;
        }
        if (!isCellSelected(row, column)) {
            c.setBackground(colorForRow(row));
            c.setForeground(UIManager.getColor("Table.foreground"));
        } else {
            c.setBackground(UIManager.getColor("Table.selectionBackground"));
            c.setForeground(UIManager.getColor("Table.selectionForeground"));
        }
        return c;
    }


    public class ColorEditor
            extends AbstractCellEditor
            implements TableCellEditor, ActionListener {
        Color currentColor;
        JButton button;
        JColorChooser colorChooser;
        JDialog dialog;
        protected static final String EDIT = "edit";

        public ColorEditor() {
            button = new JButton();
            button.setActionCommand("edit");
            button.addActionListener(this);
            button.setBorderPainted(false);
        }


        public void actionPerformed(ActionEvent e) {
            if ("edit".equals(e.getActionCommand())) {

                colorChooser = new JColorChooser();
                dialog = JColorChooser.createDialog(button, "Pick a Color", true,
                        colorChooser, this,
                        null);
                button.setBackground(currentColor);
                colorChooser.setColor(currentColor);
                dialog.setVisible(true);

                fireEditingStopped();
            } else {
                currentColor = colorChooser.getColor();
                dialog.dispose();
                colorChooser = null;
                dialog = null;
            }
        }

        public Object getCellEditorValue() {
            return currentColor;
        }


        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentColor = ((Color) value);
            return button;
        }
    }

    public class myColorRenderer extends JComponent
            implements TableCellRenderer {
        private Color myColor;

        public myColorRenderer() {
            setOpaque(true);
        }

        protected void paintComponent(Graphics g) {
            g.setColor(myColor);
            g.fillRect(getX(), getY(), getWidth(), getHeight());
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            myColor = ((Color) value);
            return new ColorChooseButton(myColor, null);
        }
    }

    public class ColorRenderer extends JLabel implements TableCellRenderer {
        Border unselectedBorder1 = null;

        Border unselectedBorder2 = null;

        Border selectedBorder = null;

        boolean isBordered = true;

        public ColorRenderer(boolean isBordered) {
            this.isBordered = isBordered;
            setOpaque(true);
        }


        public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
            Color newColor = (Color) color;
            setBackground(newColor);
            if (isBordered) {
                if (isSelected) {
                    if (selectedBorder == null) {
                        selectedBorder = BorderFactory.createMatteBorder(2, 5,
                                2, 5, table.getSelectionBackground());
                    }
                    setBorder(selectedBorder);
                } else {
                    if (unselectedBorder1 == null) {
                        unselectedBorder1 = BorderFactory.createMatteBorder(2,
                                5, 2, 5, colorForRow(0));
                    }
                    if (unselectedBorder2 == null) {
                        unselectedBorder2 = BorderFactory.createMatteBorder(2,
                                5, 2, 5, colorForRow(1));
                    }
                    setBorder(row % 2 == 0 ? unselectedBorder1 : unselectedBorder2);
                }
            }

            setToolTipText("RGB value: " + newColor.getRed() + ", " +
                    newColor.getGreen() + ", " + newColor.getBlue());
            return this;
        }
    }
}
