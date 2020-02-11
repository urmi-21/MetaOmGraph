package edu.iastate.metnet.metaomgraph.test;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.border.Border;


public class IconBorder
        implements Border, Serializable {
    private static final int PAD = 4;
    private Icon icon;
    private int iconPosition;
    private Rectangle iconBounds = new Rectangle();

    public IconBorder() {
        this(null);
    }

    public IconBorder(Icon validIcon) {
        this(validIcon, 11);
    }

    public IconBorder(Icon validIcon, int iconPosition) {
        icon = validIcon;
        this.iconPosition = iconPosition;
    }

    @Override
	public Insets getBorderInsets(Component c) {
        int horizontalInset = icon.getIconWidth() + 8;
        int iconPosition = bidiDecodeLeadingTrailing(c
                .getComponentOrientation(), this.iconPosition);
        if (iconPosition == 3) {
            return new Insets(0, 0, 0, horizontalInset);
        }
        return new Insets(0, horizontalInset, 0, 0);
    }

    public void setIcon(Icon validIcon) {
        icon = validIcon;
    }

    @Override
	public boolean isBorderOpaque() {
        return false;
    }

    @Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        int iconPosition = bidiDecodeLeadingTrailing(c
                .getComponentOrientation(), this.iconPosition);
        if (iconPosition == 2) {
            iconBounds.y = (y + 4);
            iconBounds.x = (x + width - 4 - icon.getIconWidth());
        } else if (iconPosition == 3) {
            iconBounds.y = (y + (height - icon.getIconHeight()) / 2);
            iconBounds.x = (x + width - 4 - icon.getIconWidth());
        } else if (iconPosition == 7) {
            iconBounds.y = (y + (height - icon.getIconHeight()) / 2);
            iconBounds.x = (x + 4);
        }
        iconBounds.width = icon.getIconWidth();
        iconBounds.height = icon.getIconHeight();
        icon.paintIcon(c, g, iconBounds.x, iconBounds.y);
    }


    private int bidiDecodeLeadingTrailing(ComponentOrientation c, int position) {
        if (position == 11) {
            if (!c.isLeftToRight()) {
                return 7;
            }
            return 3;
        }
        if (position == 10) {
            if (!c.isLeftToRight()) {
                return 7;
            }
            return 3;
        }
        return position;
    }
}
