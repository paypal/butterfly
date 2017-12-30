package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.util.Objects;

/**
 * Removes a dependency entry from a POM file.
 *
 * @author facarvalho
 */
public class PomRemoveDependency extends AbstractArtifactPomOperation<PomRemoveDependency> implements ChangeOrRemoveElement<PomRemoveDependency> {

    private IfNotPresent ifNotPresent = IfNotPresent.Fail;

    private static final String DESCRIPTION = "Remove dependency %s:%s from POM file %s";

    public PomRemoveDependency() {
    }

    /**
     * Operation to remove a dependency entry from a POM file
     *
     * @param groupId dependency to be removed group id
     * @param artifactId dependency to be removed artifact id
     */
    public PomRemoveDependency(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    @Override
    public PomRemoveDependency failIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomRemoveDependency warnIfNotPresent() {
        ifNotPresent = IfNotPresent.Warn;
        return this;
    }

    @Override
    public PomRemoveDependency noOpIfNotPresent() {
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

        Dependency dependency = getDependency(model, groupId, artifactId);
        if (dependency != null) {
            model.removeDependency(dependency);
            details = String.format("Dependency %s:%s has been removed from POM file %s", groupId, artifactId, relativePomFile);
            result = TOExecutionResult.success(this, details);
        } else {
            details = String.format("Dependency %s:%s has NOT been removed from POM file %s because it is not present", groupId, artifactId, relativePomFile);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PomRemoveDependency)) return false;

        PomRemoveDependency tu = (PomRemoveDependency) obj;
        if (!Objects.equals(tu.ifNotPresent, this.ifNotPresent)) return false;

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode(super.hashCode(),
                ifNotPresent
        );
    }

}
