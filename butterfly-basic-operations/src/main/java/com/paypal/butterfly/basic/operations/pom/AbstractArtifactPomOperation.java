package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.util.List;

/**
 * Abstract Artifact POM operation
 *
 * @author facarvalho
 */
public abstract class AbstractArtifactPomOperation<TO extends AbstractArtifactPomOperation> extends AbstractPomOperation<TO> {

    protected String groupId;
    protected String artifactId;

    public TO setGroupId(String groupId) {
        checkForBlankString("GroupId", groupId);
        this.groupId = groupId;
        return (TO) this;
    }

    public TO setArtifactId(String artifactId) {
        checkForBlankString("ArtifactId",artifactId);
        this.artifactId = artifactId;
        return (TO) this;
    }

    /**
     * Set dependency group id and artifact id based on a String
     * whose format is "groupId:artifactId". It throws a
     * {@link TransformationDefinitionException} if the specified
     * artifact String is invalid
     *
     * @param artifact the artifact formatted String
     * @return this operation instance
     */
    public TO setArtifact(String artifact) {
        checkForBlankString("artifact", artifact);
        String[] split = artifact.split(":");
        if (split.length != 2) {
            throw new TransformationDefinitionException("Invalid artifact " + artifact);
        }
        groupId = split[0];
        artifactId = split[1];
        return (TO) this;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    protected Dependency getDependency(Model model, String groupId, String artifactId) {
        return getDependencyInList(model.getDependencies(), groupId, artifactId);
    }

    protected Dependency getManagedDependency(Model model, String groupId, String artifactId) {
        if (model.getDependencyManagement() == null) {
            return null;
        }
        return getDependencyInList(model.getDependencyManagement().getDependencies(), groupId, artifactId);
    }

    private Dependency getDependencyInList(List<Dependency> dependencyList, String groupId, String artifactId) {
        if (dependencyList == null || dependencyList.size() == 0) {
            return null;
        }

        Dependency dependency = null;
        for (Dependency d : dependencyList) {
            if(d.getArtifactId().equals(artifactId) && d.getGroupId().equals(groupId)) {
                dependency = d;
                break;
            }
        }

        return dependency;
    }

}
