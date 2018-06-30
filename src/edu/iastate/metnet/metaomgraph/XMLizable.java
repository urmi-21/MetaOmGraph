package edu.iastate.metnet.metaomgraph;

import org.jdom.Element;

public interface XMLizable {
    Element toXML();

    void fromXML(Element paramElement);
}
