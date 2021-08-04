package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;

public class PomRemoveDependencyExclusion extends AbstractArtifactPomOperation<PomRemoveDependencyExclusion> implements ChangeOrRemoveElement<PomRemoveDependencyExclusion> {

    private static final String DESCRIPTION = "Remove the exclusion %s:%s if present in dependency %s:%s in POM file %s";

    private Exclusion exclusion;

    private ChangeOrRemoveElement.IfNotPresent ifNotPresent = ChangeOrRemoveElement.IfNotPresent.Fail;

    public PomRemoveDependencyExclusion() {
    }

    public PomRemoveDependencyExclusion(String groupId, String artifactId, String exclusionGroupId, String exclusionArtifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
        setExclusionGroupId(exclusionGroupId);
        setExclusionArtifactId(exclusionArtifactId);
    }

    public PomRemoveDependencyExclusion setExclusionGroupId(String exclusionGroupId) {
        TransformationUtility.checkForBlankString("exclusionGroupId", exclusionGroupId);
        this.exclusion.setGroupId(exclusionGroupId);
        return this;
    }

    public PomRemoveDependencyExclusion setExclusionArtifactId(String exclusionArtifactId) {
        TransformationUtility.checkForBlankString("exclusionArtifactId", exclusionArtifactId);
        this.exclusion.setArtifactId(exclusionArtifactId);
        return this;
    }

    @Override
    public PomRemoveDependencyExclusion failIfNotPresent() {
        ifNotPresent = ChangeOrRemoveElement.IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomRemoveDependencyExclusion warnIfNotPresent() {
        ifNotPresent = ChangeOrRemoveElement.IfNotPresent.Warn;
        return this;
    }

    @Override
    public PomRemoveDependencyExclusion noOpIfNotPresent() {
        ifNotPresent = ChangeOrRemoveElement.IfNotPresent.NoOp;
        return this;
    }

    public Exclusion getExclusion() {
        return this.exclusion;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, exclusion.getGroupId(), exclusion.getArtifactId(), groupId, artifactId, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        TOExecutionResult result = null;
        String details;

        Dependency dependency = getDependency(model, groupId, artifactId);
        if (dependency != null) {
            model.removeDependency(dependency);
            dependency.removeExclusion(exclusion);
            model.addDependency(dependency);

            details = String.format("Exclusion %s:%s has been removed from dependency %s:%s in POM file %s", exclusion.getGroupId(), exclusion.getArtifactId(), groupId, artifactId, relativePomFile);
            result = TOExecutionResult.success(this, details);
        } else {
            details = String.format("Exclusion %s:%s has not been removed as dependency %s:%s in POM file %s because dependency is not present", exclusion.getGroupId(), exclusion.getArtifactId(), groupId, artifactId, relativePomFile);
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
