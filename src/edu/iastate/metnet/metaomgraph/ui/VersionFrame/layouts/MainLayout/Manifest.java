package edu.iastate.metnet.metaomgraph.ui.VersionFrame.layouts.MainLayout;

import edu.iastate.metnet.simpleui.*;

import java.awt.*;

public class Manifest extends AbstractLayout {

    public ISimpleConstraint constraint() {
        StackConstraint constraint = new StackConstraint(0, 0);
        constraint.applyGutters(new Dimension(15, 30));
        constraint.fill   = GridBagConstraints.BOTH;
        constraint.asRows();

        return constraint;
    }

    @Override
    public AbstractComponent[] components() {
        return new AbstractComponent[]{
                new A_HeaderBody(),
                new B_Buttons()
        };
    }
}
