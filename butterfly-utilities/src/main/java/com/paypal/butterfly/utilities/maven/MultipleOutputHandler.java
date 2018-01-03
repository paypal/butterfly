package com.paypal.butterfly.utilities.maven;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains a list of
 * MavenInvocationOutputHandlers.<br>
 * It then handles each line of maven console output and calls each handler
 * in the list and returns a Map that contains the class name as the key and
 * the result from each of the handlers as the value.
 * 
 * @author mcrockett, facarvalho
 */
@SuppressWarnings("PMD.DefaultPackage")
class MultipleOutputHandler implements MavenInvocationOutputHandler<MultipleOutputHandler, Map<Class<? extends MavenInvocationOutputHandler>, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(MultipleOutputHandler.class);

    /* Contains all output handlers */
    private final Set<MavenInvocationOutputHandler> handlers = new HashSet<>();

    /* Contains output handlers that have thrown an exception and the exception thrown */
    private final Map<MavenInvocationOutputHandler, Exception> failedHandlers = new HashMap<>();
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
        for (MavenInvocationOutputHandler handler : handlers) {
            if (!failedHandlers.containsKey(handler)) {
                try {
                    handler.consumeLine(line);
                } catch (Exception e) {
                    if(logger.isDebugEnabled()) {
                        logger.error(handler.getClass().getName() + " has failed due to an exception ", e);
                    }
                    failedHandlers.put(handler, e);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Maven execution: {}", line);
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
        if (!executionStarted) {
            throw new IllegalStateException("Execution has not started. No results to return.");
        }

        Map<Class<? extends MavenInvocationOutputHandler>, Object> results = new HashMap<Class<? extends MavenInvocationOutputHandler>, Object>();

        for (MavenInvocationOutputHandler handler : handlers) {
            if (!failedHandlers.containsKey(handler)) {
                results.put(handler.getClass(), handler.getResult());
            } else {
                results.put(handler.getClass(), failedHandlers.get(handler));
            }
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
    void register(MavenInvocationOutputHandler handler) throws IllegalStateException {
        if (executionStarted) {
            throw new IllegalStateException("Execution has started. Not allowed to register new handlers.");
        } else if (null != handler) {
            handlers.add(handler);
        }
    }

    @Override
    public MultipleOutputHandler copy() {
        MultipleOutputHandler copy = new MultipleOutputHandler();
        for (MavenInvocationOutputHandler handler : handlers) {
            copy.handlers.add((MavenInvocationOutputHandler) handler.copy());
        }

        return copy;
    }

}
