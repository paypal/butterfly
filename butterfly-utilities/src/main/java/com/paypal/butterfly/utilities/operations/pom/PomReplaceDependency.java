package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

/**
 * Replaces a dependency by another one in a POM file.
 *
 * @author facarvalho
 */
public class PomReplaceDependency extends AbstractArtifactPomOperation<PomReplaceDependency> implements ChangeOrRemoveElement<PomReplaceDependency> {

    // TODO
    // Add pre-validation to check, in case newVersion was not set, if dependency
    // is managed or not. If not, fail if no version has been set!

    private static final String DESCRIPTION = "Replace dependency %s:%s by %s:%s in POM file %s";

    private String newGroupId;
    private String newArtifactId;
    private String newVersion;
    private String newScope;

    private IfNotPresent ifNotPresent = IfNotPresent.Fail;

    public PomReplaceDependency() {
    }

    /**
     * Operation to replace a dependency by another one in a POM file
     *
     * @param groupId group id of the dependency to be replaced
     * @param artifactId artifact id of the dependency to be replaced
     * @param newGroupId group id of the new dependency
     * @param newArtifactId artifact id of the new dependency
     */
    public PomReplaceDependency(String groupId, String artifactId, String newGroupId, String newArtifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
        setNewGroupId(newGroupId);
        setNewArtifactId(newArtifactId);
    }

    /**
     * Operation to replace a dependency by another one in a POM file
     *
     * @param groupId group id of the dependency to be replaced
     * @param artifactId artifact id of the dependency to be replaced
     * @param newGroupId group id of the new dependency
     * @param newArtifactId artifact id of the new dependency
     * @param newVersion version of the new dependency
     */
    public PomReplaceDependency(String groupId, String artifactId, String newGroupId, String newArtifactId, String newVersion) {
        this(groupId, artifactId, newGroupId, newArtifactId);
        setNewVersion(newVersion);
    }

    /**
     * Operation to replace a dependency by another one in a POM file
     *
     * @param groupId group id of the dependency to be replaced
     * @param artifactId artifact id of the dependency to be replaced
     * @param newGroupId group id of the new dependency
     * @param newArtifactId artifact id of the new dependency
     * @param newVersion version of the new dependency
     * @param newScope version of the new dependency
     */
    public PomReplaceDependency(String groupId, String artifactId, String newGroupId, String newArtifactId, String newVersion, String newScope) {
        this(groupId, artifactId, newGroupId, newArtifactId, newVersion);
        setNewScope(newScope);
    }

    public PomReplaceDependency setNewGroupId(String newGroupId) {
        TransformationUtility.checkForBlankString("newGroupId", newGroupId);
        this.newGroupId = newGroupId;
        return this;
    }

    public PomReplaceDependency setNewArtifactId(String newArtifactId) {
        TransformationUtility.checkForBlankString("newArtifactId", newArtifactId);
        this.newArtifactId = newArtifactId;
        return this;
    }

    public PomReplaceDependency setNewVersion(String newVersion) {
        TransformationUtility.checkForEmptyString("newVersion", newVersion);
        this.newVersion = newVersion;
        return this;
    }

    public PomReplaceDependency setNewScope(String newScope) {
        TransformationUtility.checkForEmptyString("newScope", newScope);
        this.newScope = newScope;
        return this;
    }

    @Override
    public PomReplaceDependency failIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomReplaceDependency warnIfNotPresent() {
        ifNotPresent = IfNotPresent.Warn;
        return this;
    }

    @Override
    public PomReplaceDependency noOpIfNotPresent() {
        ifNotPresent = IfNotPresent.NoOp;
        return this;
    }

    public String getNewGroupId() {
        return newGroupId;
    }

    public String getNewArtifactId() {
        return newArtifactId;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public String getNewScope() {
        return newScope;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, newGroupId, newArtifactId, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        TOExecutionResult result = null;
        String details;

        Dependency dependency = getDependency(model, groupId, artifactId);
        if (dependency != null) {
            model.removeDependency(dependency);

            dependency = new Dependency();
            dependency.setGroupId(newGroupId);
            dependency.setArtifactId(newArtifactId);
            if (newVersion != null) {
                dependency.setVersion(newVersion);
            }
            if (newScope != null) {
                dependency.setScope(newScope);
            }
            model.addDependency(dependency);

            details = String.format("Dependency %s:%s has been replaced by %s:%s in POM file %s", groupId, artifactId, newGroupId, newArtifactId, relativePomFile);
            result = TOExecutionResult.success(this, details);
        } else {
            details = String.format("Dependency %s:%s has not been replaced by %s:%s in POM file %s because it is not present", groupId, artifactId, newGroupId, newArtifactId, relativePomFile);
            switch (ifNotPresent) {
                case Warn:
                    result = TOExecutionResult.warning(this, new TransformationOperationException(details));
                    break;
                case NoOp:
                    result = TOExecutionResult.noOp(this, details);
                    break;
                case Fail:
                    // Fail is the default
                default:
                    result = TOExecutionResult.error(this, new TransformationOperationException(details));
                    break;
            }
        }

        return result;
    }

}
