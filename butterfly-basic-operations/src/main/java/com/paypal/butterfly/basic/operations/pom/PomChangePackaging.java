package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Operation to change the packaging of a Maven artifact, by changing its POM file
 *
 * @author facarvalho
 */
public class PomChangePackaging extends TransformationOperation<PomChangePackaging> {

    private static final String DESCRIPTION = "Change packaging to %s in POM file %s";

    private String packagingType;

    /**
     * Operation to change the packaging of a Maven artifact, by changing its POM file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     */
    private PomChangePackaging(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to change the packaging of a Maven artifact, by changing its POM file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     * @param packagingType packaging type
     */
    public PomChangePackaging(String relativePath, String packagingType) {
        this(relativePath);
        this.packagingType = packagingType;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, packagingType, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        // TODO

        return null;
    }

}
