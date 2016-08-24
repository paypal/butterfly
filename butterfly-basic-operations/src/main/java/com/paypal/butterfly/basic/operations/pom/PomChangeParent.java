package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
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
public class PomChangeParent extends TransformationOperation<PomChangeParent> {

    private static final String DESCRIPTION = "Change parent artifact in POM file %s";

    private String groupId = null;
    private String artifactId = null;
    private String version = null;

    public PomChangeParent() {
    }

    /**
     * Operation for revving up the parent' version in a Maven POM file
     *
     * @param version parent artifact version to be set
     */
    public PomChangeParent(String version) {
        this(null, null, version);
    }

    /**
     * Operation for revving up the parent' version in a Maven POM file
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
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);

        String resultMessage = null;

        MavenXpp3Reader reader = new MavenXpp3Reader();

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try{
            fileInputStream = new FileInputStream(pomFile);


            Model model = reader.read(fileInputStream);
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
            fileOutputStream = new FileOutputStream(pomFile);
            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(fileOutputStream, model);

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
    public PomChangeParent clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}
