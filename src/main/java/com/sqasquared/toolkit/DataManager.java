package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.DataObject;

/**
 * Created by JTran on 11/30/2016.
 */
public abstract class DataManager<T extends DataObject> extends
        ObjectManager<T> {
    protected DataObject topNode = null;
    protected TreeAlgorithmInterface alg = null;
    protected Loader<T> loader = null;

    public DataManager() {
        super();
    }

    public DataObject getTopNode() {
        return topNode;
    }

    public void setAlgorithm(TreeAlgorithmInterface alg) {
        this.alg = alg;
    }

    protected void run() {
        topNode = this.alg.constructTree(objectContainer);
    }

    public void setLoader(Loader<T> loader) {
        this.loader = loader;
    }
}
