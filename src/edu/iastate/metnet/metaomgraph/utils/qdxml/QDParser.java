package edu.iastate.metnet.metaomgraph.utils.qdxml;

import java.util.Stack;

public class QDParser {
    private static final int TEXT = 1;
    private static final int ENTITY = 2;
    private static final int OPEN_TAG = 3;
    private static final int CLOSE_TAG = 4;

    public QDParser() {
    }

    private static int popMode(Stack st) {
        if (!st.empty()) {
            return ((Integer) st.pop()).intValue();
        }
        return 15;
    }

    private static final int START_TAG = 5;
    private static final int ATTRIBUTE_LVALUE = 6;
    private static final int ATTRIBUTE_EQUAL = 9;
    private static final int ATTRIBUTE_RVALUE = 10;
    private static final int QUOTE = 7;
    private static final int IN_TAG = 8;

    public static void parse(DocHandler doc, java.io.Reader r) throws Exception {
        Stack st = new Stack();
        int depth = 0;
        int mode = 15;
        int c = 0;
        int quotec = 34;
        depth = 0;
        StringBuffer sb = new StringBuffer();
        StringBuffer etag = new StringBuffer();
        String tagName = null;
        String lvalue = null;
        String rvalue = null;
        java.util.Hashtable attrs = null;
        st = new Stack();
        doc.startDocument();
        int line = 1;
        int col = 0;
        boolean eol = false;
        while ((c = r.read()) != -1) {


            if ((c == 10) && (eol)) {
                eol = false;
            } else {
                if (eol) {
                    eol = false;
                } else if (c == 10) {
                    line++;
                    col = 0;
                } else if (c == 13) {
                    eol = true;
                    c = 10;
                    line++;
                    col = 0;
                } else {
                    col++;
                }

                if (mode == 11) {
                    doc.endDocument();
                    return;
                }

                if (mode == 1) {
                    if (c == 60) {
                        st.push(new Integer(mode));
                        mode = 5;
                        if (sb.length() > 0) {
                            doc.text(sb.toString());
                            sb.setLength(0);
                        }
                    } else if (c == 38) {
                        st.push(new Integer(mode));
                        mode = 2;
                        etag.setLength(0);
                    } else {
                        sb.append((char) c);
                    }
                } else if (mode == 4) {
                    if (c == 62) {
                        mode = popMode(st);
                        tagName = sb.toString();
                        sb.setLength(0);
                        depth--;
                        if (depth == 0)
                            mode = 11;
                        doc.endElement(tagName);
                    } else {
                        sb.append((char) c);
                    }

                } else if (mode == 16) {
                    if ((c == 62) && (sb.toString().endsWith("]]"))) {
                        sb.setLength(sb.length() - 2);
                        doc.text(sb.toString());
                        sb.setLength(0);
                        mode = popMode(st);
                    } else {
                        sb.append((char) c);
                    }

                } else if (mode == 13) {
                    if ((c == 62) && (sb.toString().endsWith("--"))) {
                        sb.setLength(0);
                        mode = popMode(st);
                    } else {
                        sb.append((char) c);
                    }
                } else if (mode == 15) {
                    if (c == 60) {
                        mode = 1;
                        st.push(new Integer(mode));
                        mode = 5;
                    }


                } else if (mode == 14) {
                    if (c == 62) {
                        mode = popMode(st);
                        if (mode == 1) {
                            mode = 15;
                        }

                    }

                } else if (mode == 5) {
                    mode = popMode(st);
                    if (c == 47) {
                        st.push(new Integer(mode));
                        mode = 4;
                    } else if (c == 63) {
                        mode = 14;
                    } else {
                        st.push(new Integer(mode));
                        mode = 3;
                        tagName = null;
                        attrs = new java.util.Hashtable();
                        sb.append((char) c);
                    }

                } else if (mode == 2) {
                    if (c == 59) {
                        mode = popMode(st);
                        String cent = etag.toString();
                        etag.setLength(0);
                        if (cent.equals("lt")) {
                            sb.append('<');
                        } else if (cent.equals("gt")) {
                            sb.append('>');
                        } else if (cent.equals("amp")) {
                            sb.append('&');
                        } else if (cent.equals("quot")) {
                            sb.append('"');
                        } else if (cent.equals("apos")) {
                            sb.append('\'');


                        } else if (cent.startsWith("#")) {
                            sb.append((char) Integer.parseInt(cent.substring(1)));
                        } else
                            exc("Unknown entity: &" + cent + ";", line, col);
                    } else {
                        etag.append((char) c);

                    }


                } else if (mode == 12) {
                    if (tagName == null)
                        tagName = sb.toString();
                    if (c != 62)
                        exc("Expected > for tag: <" + tagName + "/>", line, col);
                    doc.startElement(tagName, attrs);
                    doc.endElement(tagName);
                    if (depth == 0) {
                        doc.endDocument();
                        return;
                    }
                    sb.setLength(0);
                    attrs = new java.util.Hashtable();
                    tagName = null;
                    mode = popMode(st);


                } else if (mode == 3) {
                    if (c == 62) {
                        if (tagName == null)
                            tagName = sb.toString();
                        sb.setLength(0);
                        depth++;
                        doc.startElement(tagName, attrs);
                        tagName = null;
                        attrs = new java.util.Hashtable();
                        mode = popMode(st);
                    } else if (c == 47) {
                        mode = 12;
                    } else if ((c == 45) && (sb.toString().equals("!-"))) {
                        mode = 13;
                    } else if ((c == 91) && (sb.toString().equals("![CDATA"))) {
                        mode = 16;
                        sb.setLength(0);
                    } else if ((c == 69) && (sb.toString().equals("!DOCTYP"))) {
                        sb.setLength(0);
                        mode = 14;
                    } else if (Character.isWhitespace((char) c)) {
                        tagName = sb.toString();
                        sb.setLength(0);
                        mode = 8;
                    } else {
                        sb.append((char) c);
                    }


                } else if (mode == 7) {
                    if (c == quotec) {
                        rvalue = sb.toString();
                        sb.setLength(0);
                        attrs.put(lvalue, rvalue);
                        mode = 8;

                    } else if (" \r\n\t".indexOf(c) >= 0) {
                        sb.append(' ');
                    } else if (c == 38) {
                        st.push(new Integer(mode));
                        mode = 2;
                        etag.setLength(0);
                    } else {
                        sb.append((char) c);
                    }
                } else if (mode == 10) {
                    if ((c == 34) || (c == 39)) {
                        quotec = c;
                        mode = 7;
                    } else if (!Character.isWhitespace((char) c)) {

                        exc("Error in attribute processing", line, col);
                    }
                } else if (mode == 6) {
                    if (Character.isWhitespace((char) c)) {
                        lvalue = sb.toString();
                        sb.setLength(0);
                        mode = 9;
                    } else if (c == 61) {
                        lvalue = sb.toString();
                        sb.setLength(0);
                        mode = 10;
                    } else {
                        sb.append((char) c);
                    }
                } else if (mode == 9) {
                    if (c == 61) {
                        mode = 10;
                    } else if (!Character.isWhitespace((char) c)) {

                        exc("Error in attribute processing.", line, col);
                    }
                } else if (mode == 8) {
                    if (c == 62) {
                        mode = popMode(st);
                        doc.startElement(tagName, attrs);
                        depth++;
                        tagName = null;
                        attrs = new java.util.Hashtable();
                    } else if (c == 47) {
                        mode = 12;
                    } else if (!Character.isWhitespace((char) c)) {

                        mode = 6;
                        sb.append((char) c);
                    }
                }
            }
        }
        if (mode == 11) {
            doc.endDocument();
        } else
            exc("missing end tag", line, col);
    }

    private static final int SINGLE_TAG = 12;

    private static void exc(String s, int line, int col) throws Exception {
        throw new Exception(s + " near line " + line + ", column " + col);
    }

    private static final int COMMENT = 13;
    private static final int DONE = 11;
    private static final int DOCTYPE = 14;
    private static final int PRE = 15;
    private static final int CDATA = 16;
}
