package com.paypal.butterfly.utilities.operations.pom;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.AddElement;

/**
 * Adds a new managed dependency to a POM file.
 * If the POM file already has the specified managed dependency, the operation will return an error.
 * That behavior can be changed though, see {@link AddElement} for further details.
 *
 * @author facarvalho
 */
public class PomAddManagedDependency extends AbstractArtifactPomOperation<PomAddManagedDependency> implements AddElement<PomAddManagedDependency> {

    private static final String DESCRIPTION = "Add managed dependency %s:%s:%s to POM file %s";

    private String version;
    private String scope;
    private String type;

    private IfPresent ifPresent = IfPresent.Fail;

    /**
     * Operation to add a new managed dependency to a POM file.
     */
    public PomAddManagedDependency() {
    }

    /**
     * Operation to add a new managed dependency to a POM file.
     *
     * @param groupId new managed dependency group id
     * @param artifactId new managed dependency artifact id
     * @param version new managed dependency artifact version
     */
    public PomAddManagedDependency(String groupId, String artifactId, String version) {
        setGroupId(groupId);
        setArtifactId(artifactId);
        setVersion(version);
    }

    /**
     * Operation to add a new managed dependency to a POM file.
     *
     * @param groupId new managed dependency group id
     * @param artifactId new managed dependency artifact id
     * @param version new managed dependency artifact version
     * @param scope new managed dependency artifact scope
     */
    public PomAddManagedDependency(String groupId, String artifactId, String version, String scope) {
        this(groupId, artifactId, version);
        setScope(scope);
    }

    public PomAddManagedDependency setVersion(String version) {
        checkForEmptyString("Version", version);
        this.version = version;
        return this;
    }

    public PomAddManagedDependency setScope(String scope) {
        checkForEmptyString("Scope", scope);
        this.scope = scope;
        return this;
    }

    public PomAddManagedDependency setType(String type) {
        checkForEmptyString("Type", type);
        this.type = type;
        return this;
    }

    @Override
    public PomAddManagedDependency failIfPresent() {
        ifPresent = IfPresent.Fail;
        return this;
    }

    @Override
    public PomAddManagedDependency warnNotAddIfPresent() {
        ifPresent = IfPresent.WarnNotAdd;
        return this;
    }

    @Override
    public PomAddManagedDependency warnButAddIfPresent() {
        ifPresent = IfPresent.WarnButAdd;
        return this;
    }

    @Override
    public PomAddManagedDependency noOpIfPresent() {
        ifPresent = IfPresent.NoOp;
        return this;
    }

    @Override
    public PomAddManagedDependency overwriteIfPresent() {
        ifPresent = IfPresent.Overwrite;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public String getScope() {
        return scope;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, version, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        Dependency dependency;
        Exception warning = null;

        dependency = getManagedDependency(model);
        if (dependency != null) {
            String message = String.format("Managed dependency %s:%s is already present in %s", groupId, artifactId, getRelativePath());

            switch (ifPresent) {
                case WarnNotAdd:
                    return TOExecutionResult.warning(this, message);
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

        dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        if (scope != null) {
            dependency.setScope(scope);
        }
        if (type != null) {
            dependency.setType(type);
        }

        if (model.getDependencyManagement() == null) {
            model.setDependencyManagement(new DependencyManagement());
        }
        model.getDependencyManagement().addDependency(dependency);

        String details = String.format("Managed dependency %s:%s%s has been added to POM file %s", groupId, artifactId, (version == null ? "" : ":"+ version), relativePomFile);
        TOExecutionResult result = TOExecutionResult.success(this, details);

        if (warning != null) {
            result.addWarning(warning);
        }

        return result;
    }

}
