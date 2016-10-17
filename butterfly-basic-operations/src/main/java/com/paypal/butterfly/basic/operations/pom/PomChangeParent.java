package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;

/**
 * Operation to change the parent, or its version, in a Maven POM file
 *
 * @author facarvalho
 */
public class PomChangeParent extends AbstractPomOperation<PomChangeParent> {

    private static final String DESCRIPTION = "Change parent artifact in POM file %s";

    private String groupId = null;
    private String artifactId = null;
    private String version = null;

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

    public PomChangeParent setGroupId(String groupId) {
        checkForEmptyString("GroupId", groupId);
        this.groupId = groupId;
        return this;
    }

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

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) throws XmlPullParserException, IOException {
        String details;
        Parent parent = model.getParent();

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
            throw new IllegalStateException("Invalid POM parent transformation operation");
        }

        return TOExecutionResult.success(this, details);
    }

    @Override
    public PomChangeParent clone() throws CloneNotSupportedException {
        PomChangeParent pomChangeParent = (PomChangeParent)super.clone();
        return pomChangeParent;
    }

}
