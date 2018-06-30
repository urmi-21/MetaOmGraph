package edu.iastate.metnet.metaomgraph;

public interface HashLoadable<E> {
    E getSaveData();

    void loadData(E paramE);

    String getNoun();
}
