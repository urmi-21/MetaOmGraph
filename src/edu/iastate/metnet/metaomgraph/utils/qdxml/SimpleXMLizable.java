package edu.iastate.metnet.metaomgraph.utils.qdxml;

public interface SimpleXMLizable<T> {
    SimpleXMLElement toXML();

    T fromXML(SimpleXMLElement paramSimpleXMLElement);
}
