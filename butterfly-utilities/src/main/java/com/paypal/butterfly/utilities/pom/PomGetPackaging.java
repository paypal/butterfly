package com.paypal.butterfly.utilities.pom;

import org.apache.maven.model.Model;

import com.paypal.butterfly.extensions.api.TUExecutionResult;

/**
 * Retrieve the packaging of specified Maven artifact.
 * There are two ways to specify the Maven artifact:
 * <ol>
 *     <li>As a file, specified via regular {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} methods</li>
 * <li>As a context attribute, containing the Maven {@link org.apache.maven.model.Model} object</li>
 * </ol>
 * If Maven artifact is set with both options, the Maven model will be used, and the file will be ignored.
 *
 * @author facarvalho 
 */
public class PomGetPackaging extends AbstractPomGetUtility<PomGetPackaging> {

    private static final String DESCRIPTION = "Retrieve the packaging of specified Maven POM module";

    /**
     * Retrieve the packaging of specified Maven artifact.
     * There are two ways to specify the Maven artifact:
     * <ol>
     *     <li>As a file, specified via regular {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} methods</li>
     * <li>As a context attribute, containing the Maven {@link org.apache.maven.model.Model} object</li>
     * </ol>
     * If Maven artifact is set with both options, the Maven model will be used, and the file will be ignored.
     */
    public PomGetPackaging() {
    }

    /**
     * Retrieve the packaging of specified Maven artifact.
     * There are two ways to specify the Maven artifact:
     * <ol>
     * <li>As a file, specified via regular {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} methods</li>
     * <li>As a context attribute, containing the Maven {@link org.apache.maven.model.Model} object</li>
     * </ol>
     * If Maven artifact is set with both options, the Maven model will be used, and the file will be ignored.
     *
     * @param modelAttributeName the name of the context attribute containing the Maven {@link org.apache.maven.model.Model} object
     */
    public PomGetPackaging(String modelAttributeName) {
        super(modelAttributeName);
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION);
    }

    @Override
    protected TUExecutionResult pomExecution(Model model) {
        String packaging = model.getPackaging();
        if (packaging == null) {
            packaging = "jar";
        }

        return TUExecutionResult.value(this, packaging);
    }
}