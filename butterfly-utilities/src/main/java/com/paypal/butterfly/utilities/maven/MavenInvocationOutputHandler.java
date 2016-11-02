package com.paypal.butterfly.utilities.maven;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

/**
 * MavenInvocationOutputHandlers read lines from the maven console output and
 * return data based on this output.
 *
 * @author mcrockett, facarvalho
 */
public interface MavenInvocationOutputHandler<T, RT> extends InvocationOutputHandler {

    /**
     * Returns the desired result from parsing the console output
     *
     * @return the desired result from parsing the console output
     */
    RT getResult();

    /**
     * Returns a copy of this object, but with its internal state reset,
     * so it can be run in a brand new Maven invocation
     *
     * @return a copy of this object, but with its internal state reset
     */
    T copy();

}