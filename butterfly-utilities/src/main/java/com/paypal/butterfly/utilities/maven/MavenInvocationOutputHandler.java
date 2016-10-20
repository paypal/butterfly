package com.paypal.butterfly.utilities.maven;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

/**
 * MavenInvocationOutputHandlers read lines from the maven console output and
 * return data based on this output.
 *
 * @author mcrockett, facarvalho
 */
public interface MavenInvocationOutputHandler<RT> extends InvocationOutputHandler {

    /**
     * Returns the desired result from parsing the console output.
     */
    RT getResult();

}