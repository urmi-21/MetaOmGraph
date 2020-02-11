package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class UpdatingSortableFilterableTable extends SortableFilterableTable {
    private boolean animating;
    private BufferedImage[] animation;
    private int frame;
    private int width;
    private int height;
    private Long min;
    private Long max;
    private Long progress;
    private javax.swing.Timer t;
    private String text;
    private BufferedImage thisFrame;
    private JProgressBar myProgressBar;

    public static void main(String[] args) {
        String[] headers = {"Download", "ID", "Assays", "Name", "Updated"};
        Object[][] tableData = {
                {Double.valueOf(Math.random()), "One", Integer.valueOf(1), "Line 1", Double.valueOf(Math.random())},
                {Double.valueOf(Math.random()), "Two", Integer.valueOf(2), "Line 2", Double.valueOf(Math.random())},
                {Double.valueOf(Math.random()), "Three", Integer.valueOf(3), "Line 3", Double.valueOf(Math.random())},
                {Double.valueOf(Math.random()), "Four", Integer.valueOf(4), "Line 4", Double.valueOf(Math.random())}};
        NoneditableTableModel model = new NoneditableTableModel(tableData,
                headers);


        try {
            final UpdatingSortableFilterableTable table = new UpdatingSortableFilterableTable(
                    model, "Testing...", new Long(0L), new Long(100L));
            JFrame f = new JFrame("Table test");
            javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
            final JButton toggleButton = new JButton("Empty");
            javax.swing.AbstractAction toggleAction = new javax.swing.AbstractAction("Start") {
                @Override
				public void actionPerformed(ActionEvent e) {
                    if ("Start".equals(toggleButton.getText())) {
                        table.start();
                        toggleButton.setText("Stop");
                    } else {
                        table.stop();
                        toggleButton.setText("Start");
                    }

                }
            };
            toggleButton.setAction(toggleAction);
            f.getContentPane().add(new javax.swing.JScrollPane(table), "Center");
            f.getContentPane().add(toggleButton, "South");

            final ClearableTextField filterField = new ClearableTextField();
            filterField.addActionListener(new java.awt.event.ActionListener() {
                @Override
				public void actionPerformed(ActionEvent arg0) {
                    table.getFilterModel().applyFilter(filterField.getText());
                }

            });
            f.getContentPane().add(filterField, "North");
            f.setSize(800, 600);
            f.setDefaultCloseOperation(3);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public UpdatingSortableFilterableTable(javax.swing.table.TableModel model)
            throws IOException {
        this(model, null, null, null);
    }

    public UpdatingSortableFilterableTable(javax.swing.table.TableModel model, String text, Long min, Long max) throws IOException {
        super(model);
        int rows = 4;
        int cols = 8;
        this.min = min;
        this.max = max;
        progress = min;
        this.text = text;
        if ((this.min != null) && (this.max != null)) {
            myProgressBar = new JProgressBar(min.intValue(), max.intValue());
            myProgressBar.setStringPainted(true);
            myProgressBar.setPreferredSize(new java.awt.Dimension(400,
                    myProgressBar.getPreferredSize().height));
        }


        BufferedImage source = javax.imageio.ImageIO.read(getClass().getResourceAsStream(
                "/resource/tango/32x32/animations/process-working.png"));
        frame = 0;
        animation = new BufferedImage[rows * cols];
        int frameIndex = 0;
        width = (source.getWidth() / cols);
        height = (source.getHeight() / rows);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                animation[(frameIndex++)] = source.getSubimage(col * width, row *
                        height, width, height);
            }
        }
        thisFrame = animation[0];
        t = new javax.swing.Timer(25, new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                if (!animating) {
                    t.stop();
                    return;
                }
                frame = ((frame + 1) % animation.length);
                if (frame == 0) {
                    frame = 1;
                }
                thisFrame = animation[frame];
                repaint();
            }
        });
        animating = false;
    }

    public Long getMax() {
        return max;
    }

    public Long getMin() {
        return min;
    }

    public Long getProgress() {
        return progress;
    }

    public void increaseProgress(Long amount) {
        if (progress != null) {
            progress = Long.valueOf(progress.longValue() + amount.longValue());
        } else {
            progress = amount;
        }
        if (myProgressBar != null) {
            myProgressBar.setValue(progress.intValue());
        }
    }

    @Override
	protected void paintComponent(Graphics g) {
        if (!animating) {
            super.paintComponent(g);
            return;
        }
        BufferedImage nextImage = new BufferedImage(getWidth(), getHeight(),
                1);
        Graphics2D g2d = (Graphics2D) nextImage.getGraphics();

        Rectangle rect = getVisibleRect();
        if (((getParent() instanceof javax.swing.JViewport)) &&
                ((getParent().getParent() instanceof javax.swing.JScrollPane))) {
            rect =
                    ((javax.swing.JScrollPane) getParent().getParent()).getViewportBorderBounds();
        }

        g2d.setBackground(getBackground());
        g2d.clearRect(0, 0, (int) rect.getWidth(), (int) rect.getHeight());
        if ((min == null) || (max == null) || (progress == null)) {
            g2d.drawImage(thisFrame, null, ((int) rect.getWidth() - width) / 2,
                    ((int) rect.getHeight() - height) / 2);
            if (text != null) {
                int strWidth = javax.swing.SwingUtilities.computeStringWidth(
                        g2d.getFontMetrics(), text);
                g2d.setColor(Color.BLACK);
                g2d.drawString(text, ((int) rect.getWidth() - strWidth) / 2,
                        (int) (rect.getHeight() / 2.0D + height / 2 + g2d
                                .getFontMetrics().getMaxAscent()));
            }
            g2d.dispose();
            g.drawImage(nextImage, 0, 0, null);
        } else if (myProgressBar == null) {
            float barHeight = 50.0F;
            float barWidth = (float) (rect.getWidth() * 0.8D);
            float barX = (float) ((rect.getWidth() - barWidth) / 2.0D);
            float barY = (float) (rect.getCenterY() - barHeight / 2.0F);
            float arcSize = 5.0F;
            int strokeSize = 2;
            RoundRectangle2D progressBounds = new java.awt.geom.RoundRectangle2D.Float(barX,
                    barY, barWidth, barHeight, arcSize, arcSize);
            RoundRectangle2D innerBounds = new java.awt.geom.RoundRectangle2D.Float(barX +
                    strokeSize, barY + strokeSize, barWidth - 2 * strokeSize,
                    barHeight - 2 * strokeSize, arcSize - strokeSize - 1.0F,
                    arcSize - strokeSize - 1.0F);

            g2d.setColor(edu.iastate.metnet.metaomgraph.utils.Utils.darker(g2d.getBackground()));
            g2d.setStroke(new java.awt.BasicStroke(strokeSize));
            g2d.draw(progressBounds);
            g2d.setColor(edu.iastate.metnet.metaomgraph.utils.Utils.darker(g2d.getColor()));
            g2d.draw(innerBounds);
            g2d.setColor(Color.BLACK);

            int strWidth = javax.swing.SwingUtilities.computeStringWidth(
                    g2d.getFontMetrics(), text);
            g2d.drawString(text,
                    (int) (progressBounds.getCenterX() - strWidth / 2),
                    (int) (progressBounds.getY() + progressBounds.getHeight() +
                            15.0D + g2d.getFontMetrics().getMaxAscent() / 2));

            double percentComplete = (progress.longValue() - min.longValue()) / max.longValue();
            String percentText = (int) (percentComplete * 100.0D) + "% complete";
            strWidth = javax.swing.SwingUtilities.computeStringWidth(g2d.getFontMetrics(),
                    percentText);
            g2d.drawString(percentText,
                    (int) (progressBounds.getCenterX() - strWidth / 2),
                    (int) (progressBounds.getCenterY() + g2d.getFontMetrics()
                            .getMaxAscent() / 2));

            g2d.setClip(0, 0, (int) (rect.getWidth() * percentComplete),
                    (int) rect.getHeight());
            g2d.setColor(Color.BLUE);
            g2d.fill(innerBounds);
            g2d.setColor(Color.WHITE);
            g2d.drawString(percentText,
                    (int) (progressBounds.getCenterX() - strWidth / 2),
                    (int) (progressBounds.getCenterY() + g2d.getFontMetrics()
                            .getMaxAscent() / 2));
            g2d.dispose();
            g.drawImage(nextImage, 0, 0, null);
        } else {
            myProgressBar.setSize(new java.awt.Dimension((int) (rect.getWidth() * 0.9D),
                    myProgressBar.getPreferredSize().height));
            BufferedImage myImage = new BufferedImage(myProgressBar.getWidth(),
                    myProgressBar.getHeight(), 2);
            myProgressBar.paint(myImage.getGraphics());
            g2d.drawImage(myImage,
                    (int) (rect.getCenterX() - myImage.getWidth() / 2),
                    (int) (rect.getCenterY() - myImage.getHeight() / 2), null);
            g2d.setColor(Color.BLACK);

            int strWidth = javax.swing.SwingUtilities.computeStringWidth(
                    g2d.getFontMetrics(), text);
            g2d.drawString(
                    text,
                    (int) (rect.getCenterX() - strWidth / 2),
                    (int) (rect.getCenterY() + myProgressBar.getHeight() + 5.0D + g2d
                            .getFontMetrics().getMaxAscent() / 2));
            g2d.dispose();
            g.drawImage(nextImage, 0, 0, null);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMax(Long max) {
        this.max = max;
        if (myProgressBar != null) {
            myProgressBar.setMaximum(max.intValue());
        }
    }

    public void setMin(Long min) {
        this.min = min;
        if (myProgressBar != null) {
            myProgressBar.setMinimum(min.intValue());
        }
    }

    public void setProgress(Long progress) {
        this.progress = progress;
        if (myProgressBar != null) {
            myProgressBar.setValue(progress.intValue());
        }
    }

    public void start() {
        animating = true;
        t.start();
        if (getParent() != null) {
            setPreferredSize(getParent().getSize());
        } else {
            setPreferredSize(getVisibleRect().getSize());
        }
        revalidate();
        if (isVisible()) {
            paintImmediately(getBounds());
        }
    }

    public void stop() {
        animating = false;
        t.stop();
        setPreferredSize(null);
        repaint();
        revalidate();
    }

    @Override
	public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        System.out.println("Visible!");
    }
}
