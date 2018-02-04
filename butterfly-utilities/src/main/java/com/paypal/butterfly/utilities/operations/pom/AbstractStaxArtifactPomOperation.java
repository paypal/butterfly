package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;

import java.util.Objects;

/**
 * Abstract Artifact POM operation.
 *
 * @author facarvalho
 */
public abstract class AbstractStaxArtifactPomOperation<T extends AbstractStaxArtifactPomOperation> extends AbstractStaxPomOperation<T> {

    protected String groupId;
    protected String artifactId;

    public T setGroupId(String groupId) {
        checkForBlankString("GroupId", groupId);
        this.groupId = groupId;
        return (T) this;
    }

    public T setArtifactId(String artifactId) {
        checkForBlankString("ArtifactId",artifactId);
        this.artifactId = artifactId;
        return (T) this;
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
    public T setArtifact(String artifact) {
        checkForBlankString("artifact", artifact);
        String[] split = artifact.split(":");
        if (split.length != 2) {
            throw new TransformationDefinitionException("Invalid artifact " + artifact);
        }
        groupId = split[0];
        artifactId = split[1];
        return (T) this;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AbstractStaxArtifactPomOperation)) return false;

        AbstractStaxArtifactPomOperation tu = (AbstractStaxArtifactPomOperation) obj;
        if (!Objects.equals(tu.groupId, this.groupId)) return false;
        if (!Objects.equals(tu.artifactId, this.artifactId)) return false;

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode(super.hashCode(), groupId, artifactId);
    }

}
