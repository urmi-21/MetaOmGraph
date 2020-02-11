package edu.iastate.metnet.metaomgraph.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

public class ChartAppearanceMenu extends JPopupMenu implements ActionListener {
    public static final String TOGGLE_SHAPES_COMMAND = "shapes";
    public static final String TOGGLE_LINES_COMMAND = "lines";
    JCheckBoxMenuItem showShapesItem;
    JCheckBoxMenuItem showLinesItem;
    JMenu colorSchemeMenu;
    JRadioButtonMenuItem defaultColorSchemeItem;
    JRadioButtonMenuItem grayColorSchemeItem;
    JRadioButtonMenuItem customColorSchemeItem;

    public ChartAppearanceMenu() {
        showLinesItem = new JCheckBoxMenuItem("Show Lines");
        showShapesItem = new JCheckBoxMenuItem("Show Shapes");
        defaultColorSchemeItem = new JRadioButtonMenuItem("Default");
        grayColorSchemeItem = new JRadioButtonMenuItem("Grayscale");
        customColorSchemeItem = new JRadioButtonMenuItem("Custom");
        colorSchemeMenu = new JMenu("Color Scheme");
        add(showLinesItem);
        add(showShapesItem);
        colorSchemeMenu.add(defaultColorSchemeItem);
        colorSchemeMenu.add(grayColorSchemeItem);
        colorSchemeMenu.add(customColorSchemeItem);
        add(colorSchemeMenu);
    }

    public boolean isShowShapes() {
        return showShapesItem.isSelected();
    }

    public boolean isShowLines() {
        return showLinesItem.isSelected();
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        "shapes".equals(e.getActionCommand());
    }
}
