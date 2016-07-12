package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Operation to remove a managed dependency entry from a POM file
 *
 * @author facarvalho
 */
public class PomRemoveManagedDependency extends TransformationOperation<PomRemoveManagedDependency> {

    private static final String DESCRIPTION = "Remove managed dependency entry %s:%s from POM file %s";

    private String groupId;
    private String artifactId;

    /**
     * Operation to remove a managed dependency entry from a POM file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     */
    private PomRemoveManagedDependency(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to remove a managed dependency entry from a POM file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     * @param groupId managed dependency to be removed group id
     * @param artifactId managed dependency to be removed artifact id
     */
    public PomRemoveManagedDependency(String relativePath, String groupId, String artifactId) {
        this(relativePath);
        this.groupId = groupId;
        this.artifactId = artifactId;
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
