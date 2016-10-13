package com.paypal.butterfly.basic.utilities.maven;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Just a simple POJO to contain {@link MavenGoal} result
 *
 * @author facarvalho
 */
public class MavenGoalResult {

    // This is the Maven execution exit code
    private int exitCode;

    // This map keeps the result of each output handler executed
    // The key is the handler class name
    private Map<String, Object> outputHandlersResult = new HashMap<>();

    MavenGoalResult(int exitCode, Map<String, Object> outputHandlersResult) {
        this.exitCode = exitCode;
        if (outputHandlersResult != null) {
            this.outputHandlersResult = outputHandlersResult;
        }
    }

    public int getExitCode() {
        return exitCode;
    }

    public Map<String, Object> getResult() {
        return Collections.unmodifiableMap(outputHandlersResult);
    }

}
