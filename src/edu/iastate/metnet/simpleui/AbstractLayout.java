package edu.iastate.metnet.simpleui;

import javax.swing.*;
import java.awt.*;

/**
 * AbstractLayout
 * <p>
 * Represents a possible layout for our JFrame; this layer is responsible for laying out our information.
 */
public abstract class AbstractLayout extends AbstractComponent {

    protected int index = 0;

    /**
     * Called on inserting a single component into a container.
     *
     * @param parent    what to fill
     * @param component what to insert
     */
    public void fillEach(Container parent, AbstractComponent component, ISimpleConstraint constraint) {
        Container item = component.create();

        constraint.beforeInsert(parent, item);
        parent.add(item, constraint, this.index++);
        constraint.afterInsert(parent, item);

        if (component instanceof AbstractLayout) {
            ((AbstractLayout) component).fill(item);
        }
    }

    protected abstract AbstractComponent[] components();

    protected abstract ISimpleConstraint constraint();

    /**
     * @param components set of components to insert
     * @param constraint what constraints to abide by
     */
    protected void fill(Container container, AbstractComponent[] components, ISimpleConstraint constraint) {
        container.setLayout(new GridBagLayout());

        for (AbstractComponent component : components) {
            this.fillEach(container, component, constraint);
        }
    }

    public void fill(Container container) {
        this.fill(container, this.components(), this.constraint());
    }

    @Override
    public Container create() {
        JPanel panel = new JPanel();

        return panel;
    }
}
