package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Changes a Plugin in a Maven POM file.
 * It allows changing version, extensions, executions and dependencies but not group id and artifact id.
 * It also allows removing specific configuration, letting them
 * to have default values, or be managed by applicable.
 * <br>
 * If the plugin to be changed doesn't actually exist, it will result
 * in error. The default behavior can be changed. See {@link ChangeOrRemoveElement} for further details.
 * <br>
 * Important: no check will be done here for possible reasons to break
 * the build, like the lack of version when the plugin is not managed
 *
 * @author praveesingh
 */
public class PomChangePlugin extends AbstractArtifactPomOperation<PomChangePlugin> implements ChangeOrRemoveElement<PomChangePlugin> {

    private static final String DESCRIPTION = "Change Plugin %s:%s in POM file %s";

    private String version;
    private String extensions;
    private List<PluginExecution> executions;
    private List<Dependency> pluginDependencies;

    // Removable properties, letting them to have default values, or be managed when applicable.
    private boolean removeVersion = false;
    private boolean removeExtensions = false;
    private boolean removeExecutions = false;
    private boolean removePluginDependencies = false;

    // What to do if the dependency that is supposed to be changed is not present
    private IfNotPresent ifNotPresent = IfNotPresent.Fail;

    public PomChangePlugin() {
    }

    /**
     * Operation to change a plugin in a Maven POM file.
     * It allows changing version, extensions, executions and dependencies but not group id and artifact id.
     * It also allows removing specific configuration, letting them
     * to have default values, or be managed by applicable.
     * <br>
     * If the plugin to be changed doesn't actually exist, it will result
     * in error
     * <br>
     * Important: no check will be done here for possible reasons to break
     * the build, like the lack of version when the plugin is not managed
     *
     * @param groupId    plugin group id
     * @param artifactId plugin artifact id
     */
    public PomChangePlugin(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    /**
     * Operation to set the version that will add/update the existing plugin version.
     * @param   version Version to be set in the transformed plugin
     * @return          this PomChangePlugin object
     */
    public PomChangePlugin setVersion(String version) {
        checkForBlankString("Version", version);
        this.version = version;
        return this;
    }

    /**
     * Operation to set the extensions that will add/update the existing plugin extensions.
     * @param   extensions  Extension that needs to be set in the transformed plugin
     * @return              this PomChangePlugin object
     */
    public PomChangePlugin setExtensions(String extensions) {
        checkForBlankString("Extensions", extensions);
        this.extensions = extensions;
        return this;
    }

    /**
     * Operation to set the Executions that will add/update the existing plugin executions.
     * @param   executions  List of {@link PluginExecution} that needs to be set in the transformed plugin
     * @return              this PomChangePlugin object
     */
    public PomChangePlugin setExecutions(List<PluginExecution> executions) {
        checkForNull("Executions", executions);
        Map<String, PluginExecution> executionMap = new LinkedHashMap<>();
        for (PluginExecution exec : executions) {
            if (executionMap.containsKey(exec.getId())) {
                throw new IllegalArgumentException("You cannot have two plugin executions with the same " +
                        "(or missing) <id/> elements.\nOffending execution\n\nId: '" + exec.getId()
                        + "'\nPlugin: '" + this.groupId + ":" + this.artifactId + "'\n\n");
            }
            executionMap.put(exec.getId(), exec);
        }
        this.executions = executions;
        return this;
    }

    /**
     * Operation to set the Dependencies that will add/update the existing plugin dependencies.
     * @param   pluginDependencies  List of {@link Dependency} that needs to be set in the transformed plugin
     * @return                      this PomChangePlugin object
     */
    public PomChangePlugin setPluginDependencies(List<Dependency> pluginDependencies) {
        checkForNull("Plugin Dependencies", pluginDependencies);
        this.pluginDependencies = pluginDependencies;
        return this;
    }

    /**
     * Operation to remove version of the plugin.
     * @return  this PomChangePlugin object
     */
    public PomChangePlugin removeVersion() {
        removeVersion = true;
        return this;
    }

    /**
     * Operation to remove extensions of the plugin.
     * @return  this PomChangePlugin object
     */
    public PomChangePlugin removeExtensions() {
        removeExtensions = true;
        return this;
    }

    /**
     * Operation to remove executions of the plugin.
     * @return  this PomChangePlugin object
     */
    public PomChangePlugin removeExecutions() {
        removeExecutions = true;
        return this;
    }

    /**
     * Operation to remove dependencies of the plugin.
     * @return  this PomChangePlugin object
     */
    public PomChangePlugin removeDependencies() {
        removePluginDependencies = true;
        return this;
    }

    @Override
    public PomChangePlugin failIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomChangePlugin warnIfNotPresent() {
        ifNotPresent = IfNotPresent.Warn;
        return this;
    }

    @Override
    public PomChangePlugin noOpIfNotPresent() {
        ifNotPresent = IfNotPresent.NoOp;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public String getExtensions() {
        return extensions;
    }

    public List<PluginExecution> getExecutions() {
        return executions;
    }

    public List<Dependency> getPluginDependencies() {
        return pluginDependencies;
    }

    public boolean isRemoveVersion() {
        return removeVersion;
    }

    public boolean isRemoveExtensions() {
        return removeExtensions;
    }

    public boolean isRemoveExecutions() {
        return removeExecutions;
    }

    public boolean isRemovePluginDependencies() {
        return removePluginDependencies;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        TOExecutionResult result;

        Plugin plugin = getPlugin(model, groupId, artifactId);
        if (plugin != null) {
            model.getBuild().removePlugin(plugin);

            if (removeVersion) plugin.setVersion(null); else if (version != null) plugin.setVersion(version);
            if (removeExtensions) plugin.setExtensions(null); else if (extensions != null) plugin.setExtensions(extensions);
            if (removeExecutions) plugin.setExecutions(null); else if (executions != null) plugin.setExecutions(executions);
            if (removePluginDependencies) plugin.setDependencies(null); else if (pluginDependencies != null) plugin.setDependencies(pluginDependencies);

            model.getBuild().addPlugin(plugin);

            String details = String.format("Plugin %s:%s has been changed in %s", groupId, artifactId, getRelativePath());
            result = TOExecutionResult.success(this, details);
        } else {
            String message = String.format("Plugin %s:%s is not present in %s", groupId, artifactId, getRelativePath());

            switch (ifNotPresent) {
                case Warn:
                    result = TOExecutionResult.warning(this, new TransformationOperationException(message));
                    break;
                case NoOp:
                    result = TOExecutionResult.noOp(this, message);
                    break;
                case Fail:
                    // Fail is the default
                default:
                    result = TOExecutionResult.error(this, new TransformationOperationException(message));
                    break;
            }
        }

        return result;
    }

}
