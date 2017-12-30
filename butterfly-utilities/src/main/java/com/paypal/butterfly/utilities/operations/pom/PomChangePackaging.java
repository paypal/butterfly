package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import org.apache.maven.model.Model;

import java.util.Objects;

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PomChangePackaging)) return false;

        PomChangePackaging tu = (PomChangePackaging) obj;
        if (!Objects.equals(tu.packagingType, this.packagingType)) return false;

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode(super.hashCode(),
                packagingType);
    }

}