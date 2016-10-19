package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;

/**
 * Operation to remove a plugin entry from a POM file
 *
 * @author facarvalho
 */
public class PomRemovePlugin extends AbstractArtifactPomOperation<PomRemovePlugin> implements ChangeOrRemoveElement<PomRemovePlugin> {

    private IfNotPresent ifNotPresent = IfNotPresent.Fail;

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
    public PomRemovePlugin failIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomRemovePlugin warnIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomRemovePlugin noOpIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        TOExecutionResult result = null;
        String details;

        Plugin plugin = getPlugin(model, groupId, artifactId);
        if (plugin != null) {
            model.getBuild().removePlugin(plugin);
            details = String.format("Plugin %s:%s has been removed from POM file %s", groupId, artifactId, relativePomFile);
            result = TOExecutionResult.success(this, details);
        } else {
            details = String.format("Plugin %s:%s has not been removed from POM file %s because it is not present", groupId, artifactId, relativePomFile);
            switch (ifNotPresent) {
                case Warn:
                    result = TOExecutionResult.warning(this, new TransformationOperationException(details));
                    break;
                case NoOp:
                    result = TOExecutionResult.noOp(this, details);
                    break;
                case Fail:
                    // Fail is the default
                default:
                    result = TOExecutionResult.error(this, new TransformationOperationException(details));
                    break;
            }
        }

        return result;
    }

    @Override
    public PomRemovePlugin clone() throws CloneNotSupportedException {
        PomRemovePlugin clone = (PomRemovePlugin) super.clone();
        return clone;
    }

}

