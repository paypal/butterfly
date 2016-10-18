package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.util.List;

/**
 * Operation to remove a plugin entry from a POM file
 *
 * @author facarvalho
 */
public class PomRemovePlugin extends AbstractArtifactPomOperation<PomRemovePlugin> {

    private static final String DESCRIPTION = "Remove plugin %s:%s from POM file %s";

    public PomRemovePlugin() {
    }

    /**
     * Operation to remove a plugin entry from a POM file
     *
     * @param groupId plugin to be removed group id
     * @param artifactId plugin to be removed artifact id
     */
    public PomRemovePlugin(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) throws XmlPullParserException, IOException {
        boolean found = false;
        List<Plugin> plugins = model.getBuild().getPlugins();
        TOExecutionResult result = null;
        String details = null;

        if(plugins != null) {
            for (Plugin plugin : plugins) {
                if(plugin.getArtifactId().equals(artifactId) && plugin.getGroupId().equals(groupId)) {
                    model.getBuild().removePlugin(plugin);
                    details = String.format("Plugin %s:%s has been removed from POM file %s", groupId, artifactId, relativePomFile);
                    result = TOExecutionResult.success(this, details);
                    found = true;
                    break;
                }
            }
        }
        if(!found) {
            details = String.format("Plugin %s:%s could not be found in POM file %s", groupId, artifactId, relativePomFile);
            result = TOExecutionResult.noOp(this, details);
        }

        return result;
    }

    @Override
    public PomRemovePlugin clone() throws CloneNotSupportedException {
        PomRemovePlugin pomRemovePlugin = (PomRemovePlugin)super.clone();
        return pomRemovePlugin;
    }

}

