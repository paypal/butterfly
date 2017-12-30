package com.paypal.butterfly.utilities.maven;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Runs one or more Maven goals against a specific Maven POM file.
 *
 * @author facarvalho
 */
public class MavenGoal extends TransformationUtility<MavenGoal> {

    private static final String DESCRIPTION = "Execute Maven goal %s against pom file %s";

    private String[] goals = {};

    private Properties properties = null;

    private MavenInvocationOutputHandler[] outputHandlers = {};

    private MultipleOutputHandler multipleOutputHandler = new MultipleOutputHandler();
    
    private String mavenFailureBehavior = null;
    
    private InvocationRequest request = new DefaultInvocationRequest();

    private Invoker invoker = new DefaultInvoker();

    private boolean warnOnError = false;

    /**
     * Utility to run one or more Maven goals against a specific Maven POM file
     */
    public MavenGoal() {
    }

    /**
     * Utility to run one or more Maven goals against a specific Maven POM file
     *
     * @param goals Maven goals to be executed
     */
    public MavenGoal(String... goals) {
        setGoals(goals);
    }

    /**
     * Utility to run one or more Maven goals against a specific Maven POM file
     *
     * @param goals Maven goals to be executed
     * @param outputHandlers output handlers to be executed against the Maven goals execution result
     */
    public MavenGoal(String[] goals, MavenInvocationOutputHandler[] outputHandlers) {
        setGoals(goals);
        setOutputHandlers(outputHandlers);
    }

    /**
     * Set the Maven goals to be executed
     *
     * @param goals Maven goals to be executed
     * @return this utility instance
     */
    public MavenGoal setGoals(String... goals) {
        checkForNull("Maven goals", goals);
        this.goals = Arrays.copyOf(goals, goals.length);
        return this;
    }

    /**
     * Set the output handlers to be executed against the Maven goals execution result
     *
     * @param outputHandlers output handlers to be executed against the Maven goals execution result
     * @return this utility instance
     */
    public MavenGoal setOutputHandlers(MavenInvocationOutputHandler... outputHandlers) {
        checkForNull("Output handlers", outputHandlers);
        this.outputHandlers = outputHandlers;
        return this;
    }

    /**
     * Set the Maven properties for the goal
     *
     * @param properties equivalent to '-D' options
     * @return this utility instance
     */
    public MavenGoal setProperties(Properties properties) {
        checkForNull("Maven properties", properties);
        this.properties = properties;
        return this;
    }

    /**
     * Set the maven failure behavior to only fail at the end.
     *
     * @return this utility instance
     */
    public MavenGoal setFailAtEnd() {
        mavenFailureBehavior = InvocationRequest.REACTOR_FAIL_AT_END;
        return this;
    }

    /**
     * If this is set to true, then in case the maven goal command
     * does not succeed, then a warn result type will be returned,
     * instead of error. The default value is error.
     *
     * @param warnOnError whether, in case the maven goal command
     * does not succeed, a warn result type should be returned,
     * instead of error
     * @return this utility instance
     */
    public MavenGoal setWarnOnError(boolean warnOnError) {
        this.warnOnError = warnOnError;
        return this;
    }

    /**
     * Return the Maven goals to be executed
     *
     * @return the Maven goals to be executed
     */
    public String[] getGoals() {
        return Arrays.copyOf(goals, goals.length);
    }

    /**
     * Return the Maven properties for the goal
     *
     * @return properties for the Maven invocation
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Return the output handlers to be executed against the Maven goals execution result
     *
     * @return the output handlers to be executed against the Maven goals execution result
    */
    public MavenInvocationOutputHandler[] getOutputHandlers() {
        return Arrays.copyOf(outputHandlers, outputHandlers.length);
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, Arrays.toString(goals), getRelativePath());
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        TUExecutionResult result = null;
        InvocationResult invocationResult = null;

        try {
            for (MavenInvocationOutputHandler outputHandler : outputHandlers) {
                multipleOutputHandler.register(outputHandler);
            }

            request.setPomFile(pomFile);
            request.setGoals(Arrays.asList(goals));
            request.setOutputHandler(multipleOutputHandler);

            if (mavenFailureBehavior != null) {
                request.setFailureBehavior(mavenFailureBehavior);
            }

            if (null != properties && !properties.isEmpty()) {
                request.setProperties(properties);
            }

            invocationResult = invoker.execute(request);

            int exitCode = invocationResult.getExitCode();
            Map<Class<? extends MavenInvocationOutputHandler>, Object> outputHandlersResult = multipleOutputHandler.getResult();

            if (exitCode == 0) {
                result = TUExecutionResult.value(this, outputHandlersResult);
            } else {
                Exception e = invocationResult.getExecutionException();
                if (e == null) {
                    e = new TransformationUtilityException(String.format("Maven goals %s execution failed with exit code %d", Arrays.toString(goals), exitCode));
                }
                if (warnOnError) {
                    result = TUExecutionResult.warning(this, e, outputHandlersResult);
                } else {
                    result = TUExecutionResult.error(this, e, outputHandlersResult);
                }
            }
        } catch (Exception e) {
            if (invocationResult != null) {
                Exception invocationException = invocationResult.getExecutionException();
                if (invocationException != null) {
                    result = TUExecutionResult.error(this, invocationException);
                }
            }

            if (result == null) {
                result = TUExecutionResult.error(this, e);
            }
        }

        return result;
    }

    @Override
    public MavenGoal clone() {
        MavenGoal clone = super.clone();
        clone.multipleOutputHandler = new MultipleOutputHandler();
        clone.request = new DefaultInvocationRequest();
        clone.outputHandlers = new MavenInvocationOutputHandler[outputHandlers.length];
        int i = 0;
        for (MavenInvocationOutputHandler outputHandler : outputHandlers) {
            clone.outputHandlers[i] = (MavenInvocationOutputHandler) outputHandler.copy();
            i++;
        }
        return clone;
    }

}