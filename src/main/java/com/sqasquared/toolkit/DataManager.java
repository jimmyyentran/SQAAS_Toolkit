package com.sqasquared.toolkit;

import com.sqasquared.toolkit.ObjectManager;
import com.sqasquared.toolkit.connection.DataObject;

/**
 * Created by JTran on 11/30/2016.
 */
public abstract class DataManager<T extends DataObject> extends ObjectManager<T>{
    protected DataObject topNode;
    protected TreeAlgorithmInterface alg = null;
    protected Loader loader = new Loader();

    public DataManager(){
        super();
        topNode = null;
    }

    public DataObject getTopNode() {
        return topNode;
    }

    public void setAlgorithm(TreeAlgorithmInterface alg){
        this.alg = alg;
    }

    protected void run() {
        topNode = this.alg.constructTree(objectContainer);
    }

}
