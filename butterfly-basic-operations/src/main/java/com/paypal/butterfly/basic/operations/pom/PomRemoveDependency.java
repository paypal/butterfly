package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Operation to remove a dependency entry from a POM file
 *
 * @author facarvalho
 */
public class PomRemoveDependency extends TransformationOperation<PomRemoveDependency> {

    private static final String DESCRIPTION = "Remove dependency %s:%s from POM file %s.";

    private String groupId;
    private String artifactId;

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

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder) throws Exception {
        // TODO

        return null;
    }

}
