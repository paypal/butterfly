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

    public PomChangePackaging() {
    }

    /**
     * Operation to change the packaging of a Maven artifact, by changing its POM file
     *
     * @param packagingType packaging type
     */
    public PomChangePackaging(String packagingType) {
        this.packagingType = packagingType;
    }

    public PomChangePackaging setPackagingType(String packagingType) {
        this.packagingType = packagingType;
        return this;
    }

    public String getPackagingType() {
        return packagingType;
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
