package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.AddElement;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

/**
 * Adds a new dependency to a POM file.
 *
 * @author facarvalho
 */
public class PomAddDependency extends AbstractArtifactPomOperation<PomAddDependency> implements AddElement<PomAddDependency> {

    // TODO
    // Add pre-validation to check, in case version was not set, if dependency
    // is managed or not. If not, fail!

    private static final String DESCRIPTION = "Add dependency %s:%s:%s to POM file %s";

    private String version;
    private String scope;

    private IfPresent ifPresent = IfPresent.Fail;

    public PomAddDependency() {
    }

    /**
     * Operation to add a new dependency to a POM file.
     * This constructor assumes this is a managed dependency, since the version
     * is not set. However, if that is not really the case, during transformation
     * this operation will fail pre-validation.
     *
     * @param groupId new dependency group id
     * @param artifactId new dependency artifact id
     */
    public PomAddDependency(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    /**
     * Operation to add a new dependency to a POM file.
     *
     * @param groupId new dependency group id
     * @param artifactId new dependency artifact id
     * @param version new dependency artifact version
     */
    public PomAddDependency(String groupId, String artifactId, String version) {
        this(groupId, artifactId);
        setVersion(version);
    }

    /**
     * Operation to add a new dependency to a POM file.
     *
     * @param groupId new dependency group id
     * @param artifactId new dependency artifact id
     * @param version new dependency artifact version
     * @param scope new dependency artifact scope
     */
    public PomAddDependency(String groupId, String artifactId, String version, String scope) {
        this(groupId, artifactId, version);
        setScope(scope);
    }

    public PomAddDependency setVersion(String version) {
        checkForEmptyString("Version", version);
        this.version = version;
        return this;
    }

    public PomAddDependency setScope(String scope) {
        checkForEmptyString("Scope", scope);
        this.scope = scope;
        return this;
    }

    @Override
    public PomAddDependency failIfPresent() {
        ifPresent = IfPresent.Fail;
        return this;
    }

    @Override
    public PomAddDependency warnNotAddIfPresent() {
        ifPresent = IfPresent.WarnNotAdd;
        return this;
    }

    @Override
    public PomAddDependency warnButAddIfPresent() {
        ifPresent = IfPresent.WarnButAdd;
        return this;
    }

    @Override
    public PomAddDependency noOpIfPresent() {
        ifPresent = IfPresent.NoOp;
        return this;
    }

    @Override
    public PomAddDependency overwriteIfPresent() {
        ifPresent = IfPresent.Overwrite;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, version, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        Dependency dependency;
        Exception warning = null;

        dependency = getDependency(model);
        if (dependency != null) {
            String message = String.format("Dependency %s:%s is already present in %s", groupId, artifactId, getRelativePath());

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
        if (version != null) {
            dependency.setVersion(version);
        }
        if (scope != null) {
            dependency.setScope(scope);
        }
        model.addDependency(dependency);
        String details = String.format("Dependency %s:%s%s has been added to POM file %s", groupId, artifactId, (version == null ? "" : ":"+ version), relativePomFile);
        TOExecutionResult result = TOExecutionResult.success(this, details);

        if (warning != null) {
            result.addWarning(warning);
        }

        return result;
    }

}
