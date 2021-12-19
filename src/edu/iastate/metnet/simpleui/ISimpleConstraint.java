package edu.iastate.metnet.simpleui;


import java.awt.*;

public interface ISimpleConstraint {
    void beforeInsert(Container container, Container item);

    void afterInsert(Container container, Container item);
}
