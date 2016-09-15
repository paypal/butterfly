package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;

/**
 * Operation to add a new plugin to a POM file
 *
 * @author facarvalho
 */
public class PomAddPlugin extends AbstractPomOperation<PomAddPlugin> {

    // TODO
    // Add pre-validation to check, in case version was not set, if plugin
    // is managed or not. If not, fail!

    // TODO
    // What happens if plugin already exists? Fail? Warning? Replace it (if different version)?

    private static final String DESCRIPTION = "Add plugin %s:%s:%s to POM file %s";

    private String groupId;
    private String artifactId;
    private String version;

    public PomAddPlugin() {
    }

    /**
     * Operation to add a new plugin to a POM file.
     * This constructor assumes this is a managed plugin, since the version
     * is not set. However, if that is not really the case, during transformation
     * this operation will fail pre-validation.
     *
     * @param groupId new plugin group id
     * @param artifactId new plugin artifact id
     */
    public PomAddPlugin(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    /**
     * Operation to add a new plugin to a POM file.
     *
     * @param groupId new plugin group id
     * @param artifactId new plugin artifact id
     * @param version new plugin artifact version
     */
    public PomAddPlugin(String groupId, String artifactId, String version) {
        this(groupId, artifactId);
        setVersion(version);
    }

    public PomAddPlugin setGroupId(String groupId) {
        checkForBlankString("GroupId", groupId);
        this.groupId = groupId;
        return this;
    }

    public PomAddPlugin setArtifactId(String artifactId) {
        checkForBlankString("ArtifactId", artifactId);
        this.artifactId = artifactId;
        return this;
    }

    public PomAddPlugin setVersion(String version) {
        checkForEmptyString("Version", version);
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
        return String.format(DESCRIPTION, groupId, artifactId, version, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) throws XmlPullParserException, IOException {
        Plugin plugin = new Plugin();
        plugin.setGroupId(groupId);
        plugin.setArtifactId(artifactId);
        if (version != null) {
            plugin.setVersion(version);
        }
        model.getBuild().addPlugin(plugin);
        String details = String.format("Plugin %s:%s%s has been added to POM file %s", groupId, artifactId, (version == null ? "" : ":" + version), relativePomFile);

        return TOExecutionResult.success(this, details);
    }

    @Override
    public PomAddPlugin clone() throws CloneNotSupportedException{
        PomAddPlugin clonedPomAddPlugin = (PomAddPlugin) super.clone();
        return clonedPomAddPlugin;
    }


}

