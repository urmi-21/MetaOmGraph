package edu.iastate.metnet.simpleui;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class StackConstraint extends GridBagConstraints implements ISimpleConstraint {

    protected Dimension gutters;
    protected Dimension container;

    protected int deltaX = 0;
    protected int deltaY = 0;

    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }

    public void asRows() {
        this.deltaX = 0;
        this.deltaY = 1;
    }

    public void asColumns() {
        this.deltaX = 1;
        this.deltaY = 0;
    }

    public StackConstraint(int itemMarginX, int itemMarginY) {
        super(0, 0,
              1, 1,
              1f, 1f,
              GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
              new Insets(itemMarginY, itemMarginX, itemMarginY, itemMarginX),
              itemMarginX, itemMarginY);

        this.asRows();

        this.gutters = new Dimension(0, 0);

        // NOTE(Johnny): Leave this null by default, we don't wanna use zero-coords.
        this.container = null;
    }

    /**
     * Applies a gutter effect onto the layout.
     *
     * <p>
     * |---------|
     * |    acx |
     * |    xaax|
     * |    xa  |
     * ^
     * \----------- gutter
     *
     * @param dim
     */
    public void applyGutters(Dimension dim) {
        this.gutters = dim;
    }

    /**
     * Applies a container effect onto the layout.
     * <p>
     * |------------|
     * |    acx     |
     * |    xaax    |
     * |    xa      |
     * |---|    |---|
     * ^        ^
     * container
     *
     * @param dim
     */
    public void applyContainer(Dimension dim) {
        this.container = dim;
    }

    Container getRootFrame(Container container) {
        Container parent = container.getParent();
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }

        return parent;
    }


    @Override
    public void beforeInsert(final Container container, final Container preInsertItem) {
        Container root  = getRootFrame(container);
        int       width = root.getWidth(), height = root.getHeight();

        // Gutters/Margins
        this.insets.left   = Math.min(Math.max(this.gutters.width, this.insets.left), width);
        this.insets.right  = Math.min(Math.max(this.gutters.width, this.insets.right), width);
        this.insets.top    = Math.min(Math.max(this.gutters.height, this.insets.top), height);
        this.insets.bottom = Math.min(Math.max(this.gutters.height, this.insets.bottom), height);

        // Containers/Padding
        if (this.container != null) {
            this.insets.left   = Math.max(this.insets.left, width - this.container.width);
            this.insets.right  = Math.max(this.insets.right, width - this.container.width);
            this.insets.top    = Math.max(this.insets.top, height - this.container.height);
            this.insets.bottom = Math.max(this.insets.bottom, height - this.container.height);
        }
    }

    @Override
    public void afterInsert(final Container container, @NotNull final Container postInsertedItem) {
        this.gridy += this.deltaY;
        this.gridx += this.deltaX;
    }
}
