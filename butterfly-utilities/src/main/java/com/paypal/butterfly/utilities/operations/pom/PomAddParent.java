package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.AddElement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

/**
 * Add a parent artifact in a Maven POM file.
 * By default, if parent is already present, it is overwritten.
 * This behavior though can be changed.
 *
 * @author facarvalho
 */
public class PomAddParent extends AbstractArtifactPomOperation<PomAddParent> implements AddElement<PomAddParent> {

    private static final String DESCRIPTION = "Add parent artifact %s:%s in POM file %s";

    private String version = null;

    private IfPresent ifPresent = IfPresent.Overwrite;

    /**
     * Add a parent artifact in a Maven POM file.
     * By default, if parent is already present, it is overwritten.
     * This behavior though can be changed.
     */
    public PomAddParent() {
    }

    /**
     * Operation to add a parent artifact in a Maven POM file
     *
     * @param groupId parent artifact group id to be set
     * @param artifactId parent artifact id to be set
     * @param version parent artifact version to be set
     */
    public PomAddParent(String groupId, String artifactId, String version) {
        setGroupId(groupId);
        setArtifactId(artifactId);
        setVersion(version);
    }

    public PomAddParent setVersion(String version) {
        checkForBlankString("Version", version);
        this.version = version;
        return this;
    }

    @Override
    public PomAddParent failIfPresent() {
        ifPresent = IfPresent.Fail;
        return this;
    }

    @Override
    public PomAddParent warnNotAddIfPresent() {
        ifPresent = IfPresent.WarnNotAdd;
        return this;
    }

    @Override
    public PomAddParent warnButAddIfPresent() {
        ifPresent = IfPresent.WarnButAdd;
        return this;
    }

    @Override
    public PomAddParent noOpIfPresent() {
        ifPresent = IfPresent.NoOp;
        return this;
    }

    @Override
    public PomAddParent overwriteIfPresent() {
        ifPresent = IfPresent.Overwrite;
        return this;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getGroupId(), getArtifactId(), getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        String details;
        Parent existingParent = model.getParent();

        if (existingParent != null) {
            String message = String.format("Pom file %s already has a parent", getRelativePath());

            switch (ifPresent) {
                case WarnNotAdd:
                    return TOExecutionResult.warning(this, new TransformationOperationException(message));
                case NoOp:
                    return TOExecutionResult.noOp(this, message);
                case Fail:
                    // Fail is the default
                default:
                    return TOExecutionResult.error(this, new TransformationOperationException(message));
            }
        }

        Parent newParent;
        if(groupId != null && artifactId != null && version != null) {
            newParent = new Parent();
            newParent.setGroupId(groupId);
            newParent.setArtifactId(artifactId);
            newParent.setVersion(version);
            model.setParent(newParent);
        } else {
            // FIXME this should be in a pre-validation
            throw new IllegalStateException("Invalid POM parent transformation operation");
        }

        if (ifPresent.equals(IfPresent.Overwrite)) {
            details = String.format("Parent for POM file (%s) has been set to %s", relativePomFile, newParent);
            return TOExecutionResult.success(this, details);
        } else {
            details = String.format("Parent for POM file (%s) has been overwritten to %s", relativePomFile, newParent);
            return TOExecutionResult.warning(this, details);
        }
    }

}
