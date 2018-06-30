package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


public class CheckBoxHeader
        extends JCheckBox
        implements TableCellRenderer, MouseListener {
    protected CheckBoxHeader rendererComponent;
    protected int column;
    protected boolean mousePressed = false;


    public CheckBoxHeader(ItemListener itemListener) {
        rendererComponent = this;
        rendererComponent.addItemListener(itemListener);
        setBorderPainted(true);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    }


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                rendererComponent.setForeground(header.getForeground());
                rendererComponent.setBackground(header.getBackground());
                rendererComponent.setFont(header.getFont());

                header.addMouseListener(rendererComponent);
            }
        }

        setColumn(column);
        rendererComponent.setText(value == null ? "" : value.toString());
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));

        return rendererComponent;
    }

    protected void setColumn(int column) {
        this.column = column;
    }

    public int getColumn() {
        return column;
    }


    protected void handleClickEvent(MouseEvent e) {
        if (mousePressed) {
            mousePressed = false;

            JTableHeader header = (JTableHeader) e.getSource();
            JTable tableView = header.getTable();
            TableColumnModel columnModel = tableView.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = tableView.convertColumnIndexToModel(viewColumn);

            if ((viewColumn == this.column) && (e.getClickCount() == 1) && (column != -1)) {
                doClick();
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        handleClickEvent(e);

        ((JTableHeader) e.getSource()).repaint();
    }

    public void mousePressed(MouseEvent e) {
        mousePressed = true;
    }


    public void mouseReleased(MouseEvent e) {
    }


    public void mouseEntered(MouseEvent e) {
    }


    public void mouseExited(MouseEvent e) {
    }


    public static void main(String[] args)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        String[] headers = {"col1", "col2", "col3"};
        String[][] data = {{"entry1", "entry2", "entry3"}};
        NoneditableTableModel model = new NoneditableTableModel(data, headers);
        JTable table = new JTable(model);
        final CheckBoxHeader[] boxes = new CheckBoxHeader[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            boxes[i] = new CheckBoxHeader(null);
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setHeaderRenderer(boxes[i]);
        }
        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (CheckBoxHeader box : boxes) {
                    System.out.println(box.isSelected());
                }

            }
        });
        JFrame f = new JFrame("Checkbox header test");
        f.getContentPane().add(new JScrollPane(table));
        f.getContentPane().add(checkButton, "South");
        f.setSize(800, 600);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(3);
        f.setVisible(true);
    }
}
