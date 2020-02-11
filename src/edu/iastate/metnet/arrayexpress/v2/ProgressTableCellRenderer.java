package edu.iastate.metnet.arrayexpress.v2;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class ProgressTableCellRenderer implements javax.swing.table.TableCellRenderer {
    public ProgressTableCellRenderer() {
    }

    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final Progress prog = (Progress) value;
        JPanel c = new JPanel() {
            @Override
			protected void paintComponent(Graphics g) {
                BufferedImage nextImage = new BufferedImage(getWidth(), getHeight(),
                        1);
                Graphics2D g2d = (Graphics2D) nextImage.getGraphics();


                Rectangle rect = getBounds();
                if (((getParent() instanceof javax.swing.JViewport)) &&
                        ((getParent().getParent() instanceof JScrollPane))) {
                    rect =
                            ((JScrollPane) getParent().getParent()).getViewportBorderBounds();
                }

                g2d.setBackground(getBackground());
                g2d.clearRect(0, 0, (int) rect.getWidth(), (int) rect.getHeight());
                float barHeight = (float) rect.getHeight();
                float barWidth = (float) rect.getWidth();


                float barX = 0.0F;
                float barY = 0.0F;
                float arcSize = 5.0F;
                int strokeSize = 2;
                RoundRectangle2D progressBounds = new RoundRectangle2D.Float(barX,
                        barY, barWidth, barHeight, arcSize, arcSize);
                RoundRectangle2D innerBounds = new RoundRectangle2D.Float(barX +
                        strokeSize, barY + strokeSize, barWidth - 2 * strokeSize,
                        barHeight - 2 * strokeSize, arcSize - strokeSize - 1.0F,
                        arcSize - strokeSize - 1.0F);

                g2d.setColor(Utils.darker(g2d.getBackground()));
                g2d.setStroke(new java.awt.BasicStroke(strokeSize));
                g2d.draw(progressBounds);
                g2d.setColor(Utils.darker(g2d.getColor()));

                g2d.setColor(Color.BLACK);

                double percentComplete = prog.getPercent();
                String percentText = (int) (percentComplete * 100.0D) + "% complete";
                int strWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(),
                        percentText);
                g2d.drawString(percentText,
                        (int) (progressBounds.getCenterX() - strWidth / 2),
                        (int) (progressBounds.getCenterY() + g2d.getFontMetrics()
                                .getMaxAscent() / 2));

                g2d.setClip(0, 0, (int) (rect.getWidth() * percentComplete),
                        (int) rect.getHeight());
                g2d.setColor(Color.BLUE);
                g2d.fill(progressBounds);
                g2d.setColor(Color.WHITE);
                g2d.drawString(percentText,
                        (int) (progressBounds.getCenterX() - strWidth / 2),
                        (int) (progressBounds.getCenterY() + g2d.getFontMetrics()
                                .getMaxAscent() / 2));
                g2d.dispose();
                g.drawImage(nextImage, 0, 0, null);
            }
        };
        return c;
    }
}
