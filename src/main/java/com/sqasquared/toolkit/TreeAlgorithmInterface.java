package com.sqasquared.toolkit;

import com.sqasquared.toolkit.rally.RallyObject;
import com.sqasquared.toolkit.rally.TaskRallyObject;

import java.util.HashMap;

/**
 * Created by jimmytran on 11/1/16.
 */
public interface TreeAlgorithmInterface {
    RallyObject constructTree(HashMap<String, TaskRallyObject> map);
}
