package com.paypal.butterfly.utilities.maven;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

/**
 * Reads lines from the Maven execution console output and return data after evaluating it.
 *
 * @author mcrockett, facarvalho
 */
public interface MavenInvocationOutputHandler<T, R> extends InvocationOutputHandler {

    /**
     * Returns the desired result from parsing the console output
     *
     * @return the desired result from parsing the console output
     */
    R getResult();

    /**
     * Returns a copy of this object, but with its internal state reset,
     * so it can be run in a brand new Maven invocation
     *
     * @return a copy of this object, but with its internal state reset
     */
    T copy();

}