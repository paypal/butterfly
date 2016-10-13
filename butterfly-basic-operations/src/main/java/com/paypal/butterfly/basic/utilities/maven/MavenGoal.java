package com.paypal.butterfly.basic.utilities.maven;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

/**
 * Utility to run one or more Maven goals against a specific Maven POM file
 *
 * @author facarvalho
 */
public class MavenGoal extends TransformationUtility<MavenGoal> {

    private static final String DESCRIPTION = "Execute Maven goal %s against pom file %s";

    private String[] goals = {};

    private MavenInvocationOutputHandler[] outputHandlers = {};

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
        this.outputHandlers = Arrays.copyOf(outputHandlers, outputHandlers.length);
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

        try {
            MultipleOutputHandler multipleOutputHanlder = new MultipleOutputHandler();
            for (MavenInvocationOutputHandler outputHandler : outputHandlers) {
                multipleOutputHanlder.register(outputHandler);
            }

            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(pomFile);
            request.setGoals(Arrays.asList(goals));
            request.setOutputHandler(multipleOutputHanlder);

            Invoker invoker = new DefaultInvoker();
            InvocationResult invocationResult = invoker.execute(request);

            int exitCode = invocationResult.getExitCode();
            Map<String, Object> outputHandlersResult = multipleOutputHanlder.getResult();
            MavenGoalResult mavenGoalResult = new MavenGoalResult(exitCode, outputHandlersResult);

            if (exitCode == 0) {
                result = TUExecutionResult.value(this, mavenGoalResult);
            } else {
                Exception e = invocationResult.getExecutionException();
                result = TUExecutionResult.error(this, e);
            }
        } catch (Exception e) {
            result = TUExecutionResult.error(this, e);
        }

        return result;
    }

}

