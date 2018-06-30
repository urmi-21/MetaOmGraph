package edu.iastate.metnet.metaomgraph.utils.qdxml;

import java.util.Hashtable;

public interface DocHandler {
    void startElement(String paramString, Hashtable<String, String> paramHashtable)
            throws Exception;

    void endElement(String paramString)
            throws Exception;

    void startDocument()
            throws Exception;

    void endDocument()
            throws Exception;

    void text(String paramString)
            throws Exception;
}
