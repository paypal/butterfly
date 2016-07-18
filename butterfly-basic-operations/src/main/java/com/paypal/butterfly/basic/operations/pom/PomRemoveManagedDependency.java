package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.sun.tools.javac.file.RelativePath;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

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
        File p = new File(transformedAppFolder.toString());
        File pomFile = getAbsoluteFile(p, transformationContext);

        String resultMessage = null;

        MavenXpp3Reader reader = new MavenXpp3Reader();

        Model model = reader.read(new FileInputStream(pomFile));
        // TODO: remove found and if(!found) when file PomDependencyExists is created
        boolean found = false;
        if(groupId != null || artifactId != null) {
            List deps = model.getDependencyManagement().getDependencies();
            for (int i = 0; i < deps.size(); i++) {
                if(((Dependency)deps.get(i)).getArtifactId().equals(artifactId) && ((Dependency)deps.get(i)).getGroupId().equals(groupId)) {
                    model.getDependencyManagement().removeDependency((Dependency)deps.get(i));
                    resultMessage = String.format("Managed dependency %s:%s had been removed from POM file %s",
                            groupId,
                            artifactId,
                            getRelativePath());
                    found = true;
                    break;
                }
            }
            if(!found){
                resultMessage = String.format("*** SKIPPED *** Managed dependency %s:%s could not be found in POM file %s",
                        groupId,
                        artifactId,
                        getRelativePath());
            }
        }else {
            throw new IllegalStateException("The removal operation could not be completed");
        }

        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write(new FileOutputStream(pomFile), model);

        return resultMessage;

    }

}
