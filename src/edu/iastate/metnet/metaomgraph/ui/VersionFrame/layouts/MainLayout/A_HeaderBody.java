package edu.iastate.metnet.metaomgraph.ui.VersionFrame.layouts.MainLayout;

import edu.iastate.metnet.metaomgraph.ui.VersionFrame.components.BigHeader;
import edu.iastate.metnet.metaomgraph.ui.VersionFrame.components.FmtVersionLabel;
import edu.iastate.metnet.simpleui.AbstractComponent;
import edu.iastate.metnet.simpleui.AbstractLayout;
import edu.iastate.metnet.simpleui.ISimpleConstraint;
import edu.iastate.metnet.simpleui.StackConstraint;
import edu.iastate.metnet.simpleui.components.Label;

import javax.swing.*;
import java.awt.*;

public class A_HeaderBody extends AbstractLayout {

    public ISimpleConstraint constraint() {
        StackConstraint constraint = new StackConstraint(0, 2);
        constraint.applyGutters(new Dimension(15, 0));
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.asRows();

        return constraint;
    }

    @Override
    public AbstractComponent[] components() {
        Font defaultFont     = new JLabel().getFont();
        Font boldFontVariant = defaultFont.deriveFont(defaultFont.getStyle() | Font.BOLD);
        return new AbstractComponent[]{
                new BigHeader("UNSUPPORTED JAVA VERSION"),
                new FmtVersionLabel("You're using JDK %d. We only support JDK 11 and higher.", boldFontVariant),
                new Label("We can take you to the correct download page or you can choose to do " +
                          "so manually. Either way, please download the appropriate Java edition and " +
                          "re-open MetaOmGraph once you're finished.")
        };
    }
}
