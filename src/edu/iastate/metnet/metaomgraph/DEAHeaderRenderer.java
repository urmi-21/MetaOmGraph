package edu.iastate.metnet.metaomgraph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public class DEAHeaderRenderer extends JLabel implements TableCellRenderer
{
    public DEAHeaderRenderer(Color foregroundColor, Color backgroundColor, Font font, Border border, boolean isOpaque)
    {
        setFont(font);
        setOpaque(isOpaque);
        setForeground(foregroundColor);
        setBackground(backgroundColor);
        setBorder(border); 
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) 
    {
        setText(value.toString());
        return this;
    }
}