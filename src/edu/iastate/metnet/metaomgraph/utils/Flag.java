package edu.iastate.metnet.metaomgraph.utils;

public class Flag {
    private boolean raised;

    public Flag() {
        this(false);
    }

    public Flag(boolean raised) {
        this.raised = raised;
    }

    public void setRaised(boolean raised) {
        this.raised = raised;
    }

    public boolean isRaised() {
        return raised;
    }
}
