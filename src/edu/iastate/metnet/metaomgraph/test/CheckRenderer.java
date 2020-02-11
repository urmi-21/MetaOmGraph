package edu.iastate.metnet.metaomgraph.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;

class CheckRenderer extends JPanel implements TreeCellRenderer {
    protected JCheckBox check;
    protected TreeLabel label;

    public CheckRenderer() {
        setLayout(null);
        add(this.check = new JCheckBox());
        add(this.label = new TreeLabel());
        check.setBackground(UIManager.getColor("Tree.textBackground"));
        label.setForeground(UIManager.getColor("Tree.textForeground"));
    }

    @Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean isSelected, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, isSelected,
                expanded, leaf, row, hasFocus);
        setEnabled(tree.isEnabled());
        check.setSelected(((CheckNode) value).isSelected());
        label.setFont(tree.getFont());
        label.setText(stringValue);
        label.setSelected(isSelected);
        label.setFocus(hasFocus);
        if (leaf) {
            label.setIcon(UIManager.getIcon("Tree.leafIcon"));
        } else if (expanded) {
            label.setIcon(UIManager.getIcon("Tree.openIcon"));
        } else {
            label.setIcon(UIManager.getIcon("Tree.closedIcon"));
        }
        return this;
    }

    @Override
	public Dimension getPreferredSize() {
        Dimension d_check = check.getPreferredSize();
        Dimension d_label = label.getPreferredSize();

        return new Dimension(d_check.width + d_label.width,
                d_check.height < d_label.height ? d_label.height
                        : d_check.height);
    }

    @Override
	public void doLayout() {
        Dimension d_check = this.check.getPreferredSize();
        Dimension d_label = this.label.getPreferredSize();
        int y_check = 0;
        int y_label = 0;
        if (d_check.height < d_label.height) {
            y_check = (d_label.height - d_check.height) / 2;
        } else {
            y_label = (d_check.height - d_label.height) / 2;
        }
        this.check.setLocation(0, y_check);
        this.check.setBounds(0, y_check, d_check.width, d_check.height);
        this.label.setLocation(d_check.width, y_label);
        this.label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
    }

    @Override
	public void setBackground(Color color) {
        if ((color instanceof ColorUIResource))
            color = null;
        super.setBackground(color);
    }

    public class TreeLabel extends JLabel {
        boolean isSelected;
        boolean hasFocus;

        public TreeLabel() {
        }

        @Override
		public void setBackground(Color color) {
            if ((color instanceof ColorUIResource))
                color = null;
            super.setBackground(color);
        }

        @Override
		public void paint(Graphics g) {
             /*     */
            String str;
             /* 274 */
            if (((str = getText()) != null) &&
			 /* 275 */         (str.length() > 0)) {
			 /* 276 */
                if (this.isSelected) {
			 /* 277 */
                    g.setColor(
			 /* 278 */             UIManager.getColor("Tree.selectionBackground"));
			 /*     */
                } else {
			 /* 280 */
                    g.setColor(UIManager.getColor("Tree.textBackground"));
			 /*     */
                }
			 /* 282 */
                Dimension d = getPreferredSize();
			 /* 283 */
                int imageOffset = 0;
			 /* 284 */
                Icon currentI = getIcon();
			 /* 285 */
                if (currentI != null) {
			 /* 286 */
                    imageOffset = currentI.getIconWidth() +
			 /* 287 */             Math.max(0, getIconTextGap() - 1);
			 /*     */
                }
			 /* 289 */
                g.fillRect(imageOffset, 0, d.width - 1 - imageOffset,
			 /* 290 */           d.height);
			 /* 291 */
                if (this.hasFocus) {
			 /* 292 */
                    g.setColor(
			 /* 293 */             UIManager.getColor("Tree.selectionBorderColor"));
			 /* 294 */
                    g.drawRect(imageOffset, 0, d.width - 1 - imageOffset,
			 /* 295 */             d.height - 1);
			 /*     */
                }
			 /*     */
            }
			 /*     */       
			 /* 299 */
            super.paint(g);
			 /*     */
        }

        @Override
		public Dimension getPreferredSize() {
            Dimension retDimension = super.getPreferredSize();
            if (retDimension != null) {

                retDimension = new Dimension(retDimension.width + 3, retDimension.height);
            }
            return retDimension;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public void setFocus(boolean hasFocus) {
            this.hasFocus = hasFocus;
        }
    }
}
