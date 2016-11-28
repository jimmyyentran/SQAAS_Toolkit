package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.RallyObject;
import com.sqasquared.toolkit.connection.TaskRallyObject;

import java.util.HashMap;

/**
 * Created by jimmytran on 11/1/16.
 */
interface TreeAlgorithmInterface {
    RallyObject constructTree(HashMap<String, TaskRallyObject> map);
}
