package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;

import java.util.Objects;

/**
 * Removes a plugin entry from a POM file.
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
        ifNotPresent = IfNotPresent.Warn;
        return this;
    }

    @Override
    public PomRemovePlugin noOpIfNotPresent() {
        ifNotPresent = IfNotPresent.NoOp;
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PomRemovePlugin)) return false;

        PomRemovePlugin tu = (PomRemovePlugin) obj;
        if (!Objects.equals(tu.ifNotPresent, this.ifNotPresent)) return false;

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode(super.hashCode(),
                ifNotPresent
        );
    }

}

