package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;

/**
 * Operation to remove a dependency entry from a POM file
 *
 * @author facarvalho
 */
public class PomRemoveDependency extends AbstractPomOperation<PomRemoveDependency> {

    private static final String DESCRIPTION = "Remove dependency %s:%s from POM file %s";

    private String groupId;
    private String artifactId;

    public PomRemoveDependency() {
    }

    /**
     * Operation to remove a dependency entry from a POM file
     *
     * @param groupId dependency to be removed group id
     * @param artifactId dependency to be removed artifact id
     */
    public PomRemoveDependency(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    public PomRemoveDependency setGroupId(String groupId) {
        checkForBlankString("GroupId",groupId);
        this.groupId = groupId;
        return this;
    }

    public PomRemoveDependency setArtifactId(String artifactId) {
        checkForBlankString("ArtifactId", artifactId);
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
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) throws XmlPullParserException, IOException {
        TOExecutionResult result = null;
        String details;

        Dependency dependency = getDependency(model, groupId, artifactId);
        if (dependency != null) {
            model.removeDependency(dependency);
            details = String.format("Dependency %s:%s has been removed from POM file %s", groupId, artifactId, relativePomFile);
            result = TOExecutionResult.success(this, details);
        } else {
            details = String.format("Dependency %s:%s has NOT been removed from POM file %s because it is not present", groupId, artifactId, relativePomFile);
            result = TOExecutionResult.noOp(this, details);
        }

        return result;
    }

    @Override
    public PomRemoveDependency clone() throws CloneNotSupportedException {
        PomRemoveDependency pomRemoveDependency = (PomRemoveDependency)super.clone();
        return pomRemoveDependency;
    }

}
