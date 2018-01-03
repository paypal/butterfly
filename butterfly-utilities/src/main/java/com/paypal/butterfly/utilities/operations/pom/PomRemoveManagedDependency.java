package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

/**
 * Removes a managed dependency entry from a POM file.
 *
 * @author facarvalho
 */
public class PomRemoveManagedDependency extends AbstractArtifactPomOperation<PomRemoveManagedDependency> implements ChangeOrRemoveElement<PomRemoveManagedDependency> {

    private static final String DESCRIPTION = "Remove managed dependency entry %s:%s from POM file %s";

    private IfNotPresent ifNotPresent = IfNotPresent.Fail;

    public PomRemoveManagedDependency() {
    }

    /**
     * Operation to remove a managed dependency entry from a POM file
     *
     * @param groupId managed dependency to be removed group id
     * @param artifactId managed dependency to be removed artifact id
     */
    public PomRemoveManagedDependency(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    @Override
    public PomRemoveManagedDependency failIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomRemoveManagedDependency warnIfNotPresent() {
        ifNotPresent = IfNotPresent.Warn;
        return this;
    }

    @Override
    public PomRemoveManagedDependency noOpIfNotPresent() {
        ifNotPresent = IfNotPresent.NoOp;
        return this;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        TOExecutionResult result = null;
        String details;

        Dependency dependency = getManagedDependency(model, groupId, artifactId);
        if (dependency != null) {
            model.getDependencyManagement().removeDependency(dependency);
            details = String.format("Managed dependency %s:%s has been removed from POM file %s", groupId, artifactId, relativePomFile);
            result = TOExecutionResult.success(this, details);
        } else {
            details = String.format("Managed dependency %s:%s has NOT been removed from POM file %s because it is not present", groupId, artifactId, relativePomFile);
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
