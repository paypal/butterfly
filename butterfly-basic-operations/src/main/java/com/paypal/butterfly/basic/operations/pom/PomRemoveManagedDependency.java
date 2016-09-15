package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;

/**
 * Operation to remove a managed dependency entry from a POM file
 *
 * @author facarvalho
 */
public class PomRemoveManagedDependency extends AbstractPomOperation<PomRemoveManagedDependency> {

    private static final String DESCRIPTION = "Remove managed dependency entry %s:%s from POM file %s";

    private String groupId;
    private String artifactId;

    public PomRemoveManagedDependency() {
    }

    /**
     * Operation to remove a managed dependency entry from a POM file
     *
     * @param groupId managed dependency to be removed group id
     * @param artifactId managed dependency to be removed artifact id
     */
    public PomRemoveManagedDependency(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    public PomRemoveManagedDependency setGroupId(String groupId) {
        checkForBlankString("GroupId", groupId);
        this.groupId = groupId;
        return this;
    }

    public PomRemoveManagedDependency setArtifactId(String artifactId) {
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
        boolean found = false;
        DependencyManagement dependencyManagement = model.getDependencyManagement();

        TOExecutionResult result = null;
        String details = null;

        if(dependencyManagement != null) {
            for (Dependency dependency : dependencyManagement.getDependencies()) {
                if(dependency.getArtifactId().equals(artifactId) && dependency.getGroupId().equals(groupId)) {
                    dependencyManagement.removeDependency(dependency);
                    details = String.format("Managed dependency %s:%s has been removed from POM file %s", groupId, artifactId, relativePomFile);
                    result = TOExecutionResult.success(this, details);
                    found = true;
                    break;
                }
            }
        }
        if(!found) {
            details = String.format("Managed dependency %s:%s could not be found in POM file %s", groupId, artifactId, relativePomFile);
            result = TOExecutionResult.noOp(this, details);
        }

        return result;
    }

    @Override
    public PomRemoveManagedDependency clone() throws CloneNotSupportedException {
        PomRemoveManagedDependency pomRemoveManagedDependency = (PomRemoveManagedDependency) super.clone();
        return pomRemoveManagedDependency;
    }

}
