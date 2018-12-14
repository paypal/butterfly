package com.paypal.butterfly.utilities.pom;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

/**
 * Retrieve the Maven coordinates (group id, artifact id and version) of the parent of specified Maven artifact.
 * The result is a single String where groupId, artifactId and version are separated by colon.
 * If given Maven artifact does not have a parent, it results in {@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#NULL}
 * There are two ways to specify the Maven artifact:
 * <ol>
 *     <li>As a context attribute, containing the Maven {@link Model} object</li>
 *     <li>As a file, specified via regular {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} methods</li>
 * </ol>
 * If Maven artifact is set with both options, the Maven model will be used, and the file will be ignored.
 *
 * @author facarvalho
 */
public class PomGetParent extends AbstractPomGetUtility<PomGetParent> {

    private static final String DESCRIPTION = "Retrieve the parent Maven coordinates of specified Maven POM module";

    /**
     * Retrieve the Maven coordinates (group id, artifact id and version) of the parent of specified Maven artifact.
     * The result is a single String where groupId, artifactId and version are separated by colon.
     * If given Maven artifact does not have a parent, it results in {@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#NULL}
     * There are two ways to specify the Maven artifact:
     * <ol>
     *     <li>As a file, specified via regular {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} methods</li>
     * <li>As a context attribute, containing the Maven {@link Model} object</li>
     * </ol>
     * If Maven artifact is set with both options, the Maven model will be used, and the file will be ignored.
     */
    public PomGetParent() {
    }

    /**
     * Retrieve the Maven coordinates (group id, artifact id and version) of the parent of specified Maven artifact.
     * The result is a single String where groupId, artifactId and version are separated by colon.
     * If given Maven artifact does not have a parent, it results in {@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#NULL}
     * There are two ways to specify the Maven artifact:
     * <ol>
     *     <li>As a file, specified via regular {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} methods</li>
     * <li>As a context attribute, containing the Maven {@link Model} object</li>
     * </ol>
     * If Maven artifact is set with both options, the Maven model will be used, and the file will be ignored.
     *
     * @param modelAttributeName the name of the context attribute containing the Maven {@link Model} object
     */
    public PomGetParent(String modelAttributeName) {
        super(modelAttributeName);
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION);
    }

    @Override
    protected TUExecutionResult pomExecution(Model model) {
        Parent parent = model.getParent();
        if (parent == null) {
            return TUExecutionResult.nullResult(this, "Specified Maven artifact does not have a parent");
        }

        String parentCoordinates = String.format("%s:%s:%s", parent.getGroupId(), parent.getArtifactId(), parent.getVersion());

        return TUExecutionResult.value(this, parentCoordinates);
    }


}