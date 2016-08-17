package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Operation to remove a plugin entry from a POM file
 *
 * @author facarvalho
 */
public class PomRemovePlugin extends TransformationOperation<PomRemovePlugin> {

    private static final String DESCRIPTION = "Remove plugin %s:%s from POM file %s";

    private String groupId;
    private String artifactId;

    public PomRemovePlugin() {
    }

    /**
     * Operation to remove a plugin entry from a POM file
     *
     * @param groupId plugin to be removed group id
     * @param artifactId plugin to be removed artifact id
     */
    public PomRemovePlugin(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public PomRemovePlugin setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public PomRemovePlugin setArtifactId(String artifactId) {
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
        MavenXpp3Reader reader = new MavenXpp3Reader();

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(pomFile);


            Model model = reader.read(fileInputStream);

            Plugin plugin = new Plugin();
            plugin.setGroupId(groupId);
            plugin.setArtifactId(artifactId);
            model.getBuild().removePlugin(plugin);

            MavenXpp3Writer writer = new MavenXpp3Writer();
            fileOutputStream = new FileOutputStream(pomFile);
            writer.write(fileOutputStream, model);

        }finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
            }finally {
                if(fileOutputStream != null) fileOutputStream.close();
            }
        }

        return String.format("Plugin %s:%s has been removed from POM file %s", groupId, artifactId, getRelativePath());
    }

    @Override
    public PomRemovePlugin clone() {
        // TODO
        throw new RuntimeException("Clone operation not supported yet");
    }

}

