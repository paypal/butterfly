package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Operation to remove a dependency entry from a POM file
 *
 * @author facarvalho
 */
public class PomRemoveDependency extends TransformationOperation<PomRemoveDependency> {

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
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public PomRemoveDependency setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public PomRemoveDependency setArtifactId(String artifactId) {
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
        String resultMessage = String.format("Managed dependency %s:%s could not be removed from POM file %s because it is not present", groupId, artifactId, getRelativePath());

        MavenXpp3Reader reader = new MavenXpp3Reader();

        Model model = reader.read(new FileInputStream(pomFile));

        if(model.getDependencies() != null) {
            for (Dependency d : model.getDependencies()) {
                if((d.getArtifactId().equals(artifactId)) && (d.getGroupId().equals(groupId))) {
                    model.removeDependency(d);
                    resultMessage = String.format("Managed dependency %s:%s has been removed from POM file %s", groupId, artifactId, getRelativePath());
                    MavenXpp3Writer writer = new MavenXpp3Writer();
                    writer.write(new FileOutputStream(pomFile), model);
                    break;
                }
            }
        }

        return resultMessage;
    }

}
