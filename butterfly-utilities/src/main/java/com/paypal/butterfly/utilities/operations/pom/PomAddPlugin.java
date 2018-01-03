package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.AddElement;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;

import java.util.Objects;

/**
 * Adds a new plugin to a POM file.
 *
 * @author facarvalho
 */
public class PomAddPlugin extends AbstractArtifactPomOperation<PomAddPlugin> implements AddElement<PomAddPlugin> {

    // TODO
    // Add pre-validation to check, in case version was not set, if plugin
    // is managed or not. If not, fail!

    private static final String DESCRIPTION = "Add plugin %s:%s to POM file %s";

    private String version;

    private IfPresent ifPresent = IfPresent.Fail;

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

    public PomAddPlugin setVersion(String version) {
        checkForEmptyString("Version", version);
        this.version = version;
        return this;
    }

    @Override
    public PomAddPlugin failIfPresent() {
        ifPresent = AddElement.IfPresent.Fail;
        return this;
    }

    @Override
    public PomAddPlugin warnNotAddIfPresent() {
        ifPresent = AddElement.IfPresent.WarnNotAdd;
        return this;
    }

    @Override
    public PomAddPlugin warnButAddIfPresent() {
        ifPresent = AddElement.IfPresent.WarnButAdd;
        return this;
    }

    @Override
    public PomAddPlugin noOpIfPresent() {
        ifPresent = AddElement.IfPresent.NoOp;
        return this;
    }

    @Override
    public PomAddPlugin overwriteIfPresent() {
        ifPresent = AddElement.IfPresent.Overwrite;
        return this;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        Plugin plugin;
        Exception warning = null;

        plugin = getPlugin(model);
        if (plugin != null) {
            String message = String.format("Plugin %s:%s is already present in %s", groupId, artifactId, getRelativePath());

            switch (ifPresent) {
                case WarnNotAdd:
                    return TOExecutionResult.warning(this, new TransformationOperationException(message));
                case WarnButAdd:
                    warning = new TransformationOperationException(message);
                    break;
                case NoOp:
                    return TOExecutionResult.noOp(this, message);
                case Overwrite:
                    // Nothing to be done here
                    break;
                case Fail:
                    // Fail is the default
                default:
                    return TOExecutionResult.error(this, new TransformationOperationException(message));
            }
        }

        plugin = new Plugin();
        plugin.setGroupId(groupId);
        plugin.setArtifactId(artifactId);
        if (version != null) {
            plugin.setVersion(version);
        }
        if (model.getBuild() == null) {
            model.setBuild(new Build());
        }
        model.getBuild().addPlugin(plugin);
        String details = String.format("Plugin %s:%s%s has been added to POM file %s", groupId, artifactId, (version == null ? "" : ":" + version), relativePomFile);
        TOExecutionResult result = TOExecutionResult.success(this, details);

        if (warning != null) {
            result.addWarning(warning);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PomAddPlugin)) return false;

        PomAddPlugin tu = (PomAddPlugin) obj;
        if (!Objects.equals(tu.ifPresent, this.ifPresent)) return false;
        if (!Objects.equals(tu.version, this.version)) return false;

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode(super.hashCode(),
                ifPresent,
                version);
    }

}

