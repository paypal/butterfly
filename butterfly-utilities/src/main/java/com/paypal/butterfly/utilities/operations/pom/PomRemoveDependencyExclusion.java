package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Removes an exclusion from a dependency in a POM file.
 * If the POM file does not have the specified dependency, the operation will return an error.
 * That behavior can be changed though, see {@link ChangeOrRemoveElement} for further details.
 * @author asingh21
 */
public class PomRemoveDependencyExclusion extends AbstractArtifactPomOperation<PomRemoveDependencyExclusion> implements ChangeOrRemoveElement<PomRemoveDependencyExclusion> {

    private static final String DESCRIPTION = "Remove the exclusion %s:%s if present in dependency %s:%s in POM file %s";

    private Exclusion exclusion;

    private ChangeOrRemoveElement.IfNotPresent ifNotPresent = ChangeOrRemoveElement.IfNotPresent.Fail;

    public PomRemoveDependencyExclusion() {
    }

    /**
     * Operation to remove an exclusion from a dependency in a POM file.
     *
     * @param groupId group id of the dependency to be replaced
     * @param artifactId artifact id of the dependency to be replaced
     * @param exclusionGroupId group id of the exclusion to remove
     * @param exclusionArtifactId artifact id of the exclusion to remove
     */
    public PomRemoveDependencyExclusion(String groupId, String artifactId, String exclusionGroupId, String exclusionArtifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
        exclusion = new Exclusion();
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
        return String.format(DESCRIPTION, exclusion.getGroupId(), exclusion.getArtifactId(), groupId,
                artifactId, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        TOExecutionResult result = null;
        String details;

        Dependency dependency = getDependency(model, groupId, artifactId);
        if(dependency == null) {
            dependency = getDependencyFromDependencyManagement(model);
        }
        if (dependency != null) {

            List<Exclusion> exclusions = dependency.getExclusions();

            if(exclusions != null && exclusions.size() > 0) {
                removeExclusionFromDependency(dependency, exclusions);
                details = String.format("Exclusion %s:%s has been removed from dependency %s:%s in POM file %s",
                        exclusion.getGroupId(), exclusion.getArtifactId(), groupId, artifactId, relativePomFile);
                result = TOExecutionResult.success(this, details);
            } else {
                details = String.format("Exclusion %s:%s not present in dependency %s:%s in POM file %s",
                        exclusion.getGroupId(), exclusion.getArtifactId(), groupId, artifactId, relativePomFile);
                result = getTOExecutionResult(details);
            }
        } else {
            details = String.format("Exclusion %s:%s has not been removed as dependency %s:%s in POM file %s because dependency is not present",
                    exclusion.getGroupId(), exclusion.getArtifactId(), groupId, artifactId, relativePomFile);
            result = getTOExecutionResult(details);
        }
        return result;
    }

    private TOExecutionResult getTOExecutionResult(String details) {
        TOExecutionResult result;
        switch (ifNotPresent) {
            case Warn:
                result = TOExecutionResult.warning(this,
                        new TransformationOperationException(details));
                break;
            case NoOp:
                result = TOExecutionResult.noOp(this, details);
                break;
            case Fail:
                // Fail is the default
            default:
                result = TOExecutionResult.error(this,
                        new TransformationOperationException(details));
                break;
        }
        return result;
    }

    private Dependency getDependencyFromDependencyManagement(Model model) {
        final Predicate<Dependency> matchesGroupId = de -> de.getGroupId().equals(groupId);
        final Predicate<Dependency> matchesArtifactId = de -> de.getArtifactId().equals(artifactId);
        Predicate<Dependency> matchesRequiredDependency = matchesGroupId.and(matchesArtifactId);
        final List<Dependency> managedDependencies = model.getDependencyManagement()
                .getDependencies()
                .stream()
                .filter(matchesRequiredDependency)
                .collect(Collectors.toList());
        if(managedDependencies.size() > 0) {
            return managedDependencies.get(0);
        }
        return null;
    }

    private void removeExclusionFromDependency(Dependency dependency, List<Exclusion> exclusions) {
        final Predicate<Exclusion> matchesGroupId = ex -> ex.getGroupId().equals(exclusion.getGroupId());
        final Predicate<Exclusion> matchesArtifactId = ex -> ex.getArtifactId().equals(exclusion.getArtifactId());
        Predicate<Exclusion> isExclusionToRemove = matchesGroupId.and(matchesArtifactId);
        dependency.setExclusions(exclusions.stream()
                .filter(isExclusionToRemove.negate()).collect(Collectors.toList()));
    }


}
