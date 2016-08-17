package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Operation to remove a managed dependency entry from a POM file
 *
 * @author facarvalho
 */
public class PomRemoveManagedDependency extends TransformationOperation<PomRemoveManagedDependency> {

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
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public PomRemoveManagedDependency setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public PomRemoveManagedDependency setArtifactId(String artifactId) {
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
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        String resultMessage = null;
        MavenXpp3Reader reader = new MavenXpp3Reader();

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(pomFile);


            Model model = reader.read(fileInputStream);
            boolean found = false;
            DependencyManagement dependencyManagement = model.getDependencyManagement();

            if(dependencyManagement != null) {
                for (Dependency dependency : dependencyManagement.getDependencies()) {
                    if(dependency.getArtifactId().equals(artifactId) && dependency.getGroupId().equals(groupId)) {
                        dependencyManagement.removeDependency(dependency);
                        resultMessage = String.format("Managed dependency %s:%s has been removed from POM file %s", groupId, artifactId, getRelativePath());
                        MavenXpp3Writer writer = new MavenXpp3Writer();
                        fileOutputStream = new FileOutputStream(pomFile);
                        writer.write(fileOutputStream, model);

                        found = true;
                        break;
                    }
                }
            }
            if(!found){
                resultMessage = String.format("Managed dependency %s:%s could not be found in POM file %s", groupId, artifactId, getRelativePath());
            }

        }finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
            }finally {
                if(fileOutputStream != null) fileOutputStream.close();
            }
        }

        return resultMessage;
    }

    @Override
    public PomRemoveManagedDependency clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}
