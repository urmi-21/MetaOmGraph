package edu.iastate.metnet.metaomgraph.metabolomics;

import ca.ansir.swing.tristate.TriStateTreeNode;

public class CustomTriStateTreeNode extends TriStateTreeNode {
    public CustomTriStateTreeNode(Object userObj) {
        super(userObj.toString());
        userObject = userObj;
    }

    public void setUserObject(Object userObject) {
    }


    public Object getUserObject() {
        return userObject;
    }
}
