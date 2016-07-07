package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Operation for revving up the parent' version in a Maven POM file
 *
 * @author facarvalho
 */
public class ChangeParent extends TransformationOperation<ChangeParent> {

    private static final String DESCRIPTION = "Change parent artifact in POM file %s.";

    private String groupId = null;
    private String artifactId = null;
    private String version = null;

    /**
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     */
    private ChangeParent(String relativePath) {
        super(relativePath);
    }

    public ChangeParent(String relativePath, String version) {
        this(relativePath);
        this.version = version;
    }

    public ChangeParent(String relativePath, String groupId, String artifactId, String version) {
        this(relativePath);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder) throws Exception {
        File pomFile = getAbsoluteFile(transformedAppFolder);

        String resultMessage = null;

        MavenXpp3Reader reader = new MavenXpp3Reader();

        Model model = reader.read(new FileInputStream(pomFile));
        Parent parent = model.getParent();
        if(groupId != null && artifactId != null && version != null) {
            parent.setGroupId(groupId);
            parent.setArtifactId(artifactId);
            parent.setVersion(version);
            String newParent = parent.toString();
            resultMessage = String.format("Parent for POM file (%s) has been set to %s", getRelativePath(), newParent);
        } else if (groupId == null && artifactId == null && version != null) {
            String oldVersion = parent.getVersion();
            parent.setVersion(version);
            resultMessage = String.format("Parent's version for POM file (%s) has been changed from %s to %s", getRelativePath(), oldVersion, version);
        } else {
            throw new IllegalStateException("Invalid POM parent transformation operation");
        }

        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write(new FileOutputStream(pomFile), model);

        return resultMessage;
    }

}
