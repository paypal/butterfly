package com.paypal.butterfly.basic.utilities.maven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MultipleInvocationOutputHandler contains a list of
 * MavenInvocationOutputHandlers.</br>
 * It then handles each line of maven console output and calls each handler
 * in the list and returns a Map that contains the class name as the key and
 * the result from each of the handlers as the value.
 * 
 * @author mcrockett, facarvalho
 */
class MultipleOutputHandler implements MavenInvocationOutputHandler<Map<Class<? extends MavenInvocationOutputHandler>, Object>> {
    
    private final List<MavenInvocationOutputHandler<Object>> handlers = new ArrayList<>();
    private boolean executionStarted = false;

    /**
     * Calls each of the registered handlers for each line of input.
     * 
     * @param line
     *            - a line of the console output.
     */
    @Override
    public void consumeLine(String line) {
        executionStarted = true;
        for (MavenInvocationOutputHandler<?> handler : handlers) {
            handler.consumeLine(line);
        }
    }

    /**
     * Returns the results of calling each handler for each line.
     * 
     * @return results - a map with class as key and results as the
     *         corresponding value.
     * @throws IllegalStateException - if we haven't consumed any lines yet.
     */
    @Override
    public Map<Class<? extends MavenInvocationOutputHandler>, Object> getResult() {
        if (false == executionStarted) {
            throw new IllegalStateException("Execution has not started. No results to return.");
        }

        Map<Class<? extends MavenInvocationOutputHandler>, Object> results = new HashMap<Class<? extends MavenInvocationOutputHandler>, Object>();

        for (MavenInvocationOutputHandler<?> handler : handlers) {
            results.put(handler.getClass(), handler.getResult());
        }

        return results;
    }

    /**
     * Registers the handler by adding it to the list of handlers.
     * 
     * @param handler - an output handler to add. Ignores null handlers.
     * @throws IllegalStateException - if execution has started, a
     * handler cannot be added.
     */
    void register(MavenInvocationOutputHandler<Object> handler) throws IllegalStateException {
        if (true == executionStarted) {
            throw new IllegalStateException("Execution has started. Not allowed to register new handlers.");
        } else if (null != handler) {
            handlers.add(handler);
        }
    }
}
