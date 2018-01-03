package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

/**
 * Changes a dependency in a Maven POM file.
 * It allows changing anything but group id and artifact id.
 * It also allows removing specific configuration, letting them
 * to have default values, or be managed when applicable.
 * <br>
 * If the dependency to be changed doesn't actually exist, it will result
 * in error
 * <br>
 * Important: no check will be done here for possible reasons to break
 * the build, like the lack of version when the dependency is not managed
 *
 * @author facarvalho
 */
public class PomChangeDependency extends AbstractArtifactPomOperation<PomChangeDependency> implements ChangeOrRemoveElement<PomChangeDependency> {

    private static final String DESCRIPTION = "Change dependency %s:%s in POM file %s";

    // TODO enums could be used for scope and type
    // Changeable properties
    private String version;
    private String scope;
    private String type;
    private boolean optional;

    // Removable properties, letting them to have default values, or be managed when applicable.
    private boolean removeVersion = false;
    private boolean removeScope = false;
    private boolean removeType = false;
    private boolean removeOptional = false;

    // What to do if the dependency that is supposed to be changed is not present
    private IfNotPresent ifNotPresent = IfNotPresent.Fail;

    public PomChangeDependency() {
    }

    /**
     * Operation to change a dependency in a Maven POM file.
     * It allows changing anything but group id and artifact id.
     * It also allows removing specific configuration, letting them
     * to have default values, or be managed when applicable.
     * <br>
     * If the dependency to be changed doesn't actually exist, it will result
     * in error
     * <br>
     * Important: no check will be done here for possible reasons to break
     * the build, like the lack of version when the dependency is not managed
     *
     * @param groupId dependency group id
     * @param artifactId dependency artifact id
     */
    public PomChangeDependency(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    public PomChangeDependency setVersion(String version) {
        checkForBlankString("Version", version);
        this.version = version;
        return this;
    }

    public PomChangeDependency setScope(String scope) {
        checkForBlankString("Scope", scope);
        this.scope = scope;
        return this;
    }

    public PomChangeDependency setType(String type) {
        checkForBlankString("Type", type);
        this.type = type;
        return this;
    }

    public PomChangeDependency setOptional() {
        optional = true;
        return this;
    }

    public PomChangeDependency removeVersion() {
        removeVersion = true;
        return this;
    }

    public PomChangeDependency removeScope() {
        removeScope = true;
        return this;
    }

    public PomChangeDependency removeType() {
        removeType = true;
        return this;
    }

    public PomChangeDependency removeOptional() {
        removeOptional = true;
        return this;
    }

    public PomChangeDependency failIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    public PomChangeDependency warnIfNotPresent() {
        ifNotPresent = IfNotPresent.Warn;
        return this;
    }

    public PomChangeDependency noOpIfNotPresent() {
        ifNotPresent = IfNotPresent.NoOp;
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

    public boolean isOptional() {
        return optional;
    }

    public boolean isRemoveVersion() {
        return removeVersion;
    }

    public boolean isRemoveScope() {
        return removeScope;
    }

    public boolean isRemoveType() {
        return removeType;
    }

    public boolean isRemoveOptional() {
        return removeOptional;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        TOExecutionResult result;

        Dependency dependency = getDependency(model, groupId, artifactId);
        if (dependency != null) {
            model.removeDependency(dependency);

            if (removeVersion) dependency.setVersion(null); else if (version != null) dependency.setVersion(version);
            if (removeScope) dependency.setScope(null); else if (scope != null) dependency.setScope(scope);
            if (removeType) dependency.setType(null); else if (type != null) dependency.setType(type);
            if (removeOptional) dependency.setOptional(null); else dependency.setOptional(optional);

            model.addDependency(dependency);

            String details = String.format("Dependency %s:%s has been changed in %s", groupId, artifactId, getRelativePath());
            result = TOExecutionResult.success(this, details);
        } else {
            String message = String.format("Dependency %s:%s is not present in %s", groupId, artifactId, getRelativePath());

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
