package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

/**
 * This transformation utility is deprecated.
 * Please use {@link PomAddParent} or {@link PomChangeParentVersion} instead.
 *
 * @author facarvalho
 */
@Deprecated
public class PomChangeParent extends AbstractArtifactPomOperation<PomChangeParent> implements ChangeOrRemoveElement<PomChangeParent> {

    private static final String DESCRIPTION = "Change parent artifact in POM file %s";

    private String version = null;

    private IfNotPresent ifNotPresent = IfNotPresent.Fail;

    public PomChangeParent() {
    }

    /**
     * Operation to change the parent, or its version, in a Maven POM file
     *
     * @param version parent artifact version to be set
     */
    public PomChangeParent(String version) {
        this(null, null, version);
    }

    /**
     * Operation to change the parent, or its version, in a Maven POM file
     *
     * @param groupId parent artifact group id to be set
     * @param artifactId parent artifact id to be set
     * @param version parent artifact version to be set
     */
    public PomChangeParent(String groupId, String artifactId, String version) {
        setGroupId(groupId);
        setArtifactId(artifactId);
        setVersion(version);
    }

    // Overriding because in this case this property is actually optional
    @Override
    public PomChangeParent setGroupId(String groupId) {
        checkForEmptyString("GroupId", groupId);
        this.groupId = groupId;
        return this;
    }

    // Overriding because in this case this property is actually optional
    @Override
    public PomChangeParent setArtifactId(String artifactId) {
        checkForEmptyString("ArtifactId", artifactId);
        this.artifactId = artifactId;
        return this;
    }

    public PomChangeParent setVersion(String version) {
        checkForBlankString("Version", version);
        this.version = version;
        return this;
    }

    @Override
    public PomChangeParent failIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomChangeParent warnIfNotPresent() {
        ifNotPresent = IfNotPresent.Warn;
        return this;
    }

    @Override
    public PomChangeParent noOpIfNotPresent() {
        ifNotPresent = IfNotPresent.NoOp;
        return this;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        String details;
        Parent parent = model.getParent();

        if (parent == null) {
            String message = String.format("Pom file %s does not have a parent", getRelativePath());

            switch (ifNotPresent) {
                case Warn:
                    return TOExecutionResult.warning(this, new TransformationOperationException(message));
                case NoOp:
                    return TOExecutionResult.noOp(this, message);
                case Fail:
                    // Fail is the default
                default:
                    return TOExecutionResult.error(this, new TransformationOperationException(message));
            }
        }

        if(groupId != null && artifactId != null && version != null) {
            parent.setGroupId(groupId);
            parent.setArtifactId(artifactId);
            parent.setVersion(version);
            String newParent = parent.toString();
            details = String.format("Parent for POM file (%s) has been set to %s", relativePomFile, newParent);
        } else if (groupId == null && artifactId == null && version != null) {
            String oldVersion = parent.getVersion();
            parent.setVersion(version);
            details = String.format("Parent's version for POM file (%s) has been changed from %s to %s", relativePomFile, oldVersion, version);
        } else {
            // FIXME this should be in a pre-validation
            return TOExecutionResult.error(this, new TransformationOperationException("Invalid POM parent transformation operation"));
        }

        return TOExecutionResult.success(this, details);
    }

}
