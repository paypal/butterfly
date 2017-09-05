package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import org.apache.maven.model.Model;

/**
 * Changes the packaging of a Maven artifact, by changing its POM file.
 *
 * @author facarvalho
 */
public class PomChangePackaging extends AbstractPomOperation<PomChangePackaging> {

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
        setPackagingType(packagingType);
    }

    public PomChangePackaging setPackagingType(String packagingType) {
        checkForEmptyString("Packaging Type",packagingType);
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
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        model.setPackaging(packagingType);
        String details = String.format("Packaging for POM file %s has been changed to %s", relativePomFile, packagingType);

        return TOExecutionResult.success(this, details);
    }

    @Override
    public PomChangePackaging clone() throws CloneNotSupportedException {
        PomChangePackaging clone = (PomChangePackaging) super.clone();
        return clone;
    }

}