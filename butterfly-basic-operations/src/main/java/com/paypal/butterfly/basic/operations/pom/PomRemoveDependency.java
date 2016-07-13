package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Operation to remove a dependency entry from a POM file
 *
 * @author facarvalho
 */
public class PomRemoveDependency extends TransformationOperation<PomRemoveDependency> {

    private static final String DESCRIPTION = "Remove dependency %s:%s from POM file %s";

    private String groupId;
    private String artifactId;

    public PomRemoveDependency() {
    }

    /**
     * Operation to remove a dependency entry from a POM file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     */
    private PomRemoveDependency(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to remove a dependency entry from a POM file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     * @param groupId dependency to be removed group id
     * @param artifactId dependency to be removed artifact id
     */
    public PomRemoveDependency(String relativePath, String groupId, String artifactId) {
        this(relativePath);
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public PomRemoveDependency setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public PomRemoveDependency setArtifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        // TODO

        return null;
    }

}
