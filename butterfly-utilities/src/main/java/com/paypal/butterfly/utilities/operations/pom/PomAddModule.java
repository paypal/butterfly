package com.paypal.butterfly.utilities.operations.pom;

import java.util.Objects;

import org.apache.maven.model.Model;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.AddElement;

/**
 * Adds a new module to a POM file.
 * If the POM file already has a module with same name, the operation will return an error.
 * That behavior can be changed though, see {@link AddElement} for further details.
 *
 * @author facarvalho
 */
public class PomAddModule extends AbstractPomOperation<PomAddModule> implements AddElement<PomAddModule> {

    // TODO
    // Add pre-validation to check, in case version was not set, if plugin
    // is managed or not. If not, fail!

    private static final String DESCRIPTION = "Add module %s to POM file %s";

    private String moduleName;

    private IfPresent ifPresent = IfPresent.Fail;

    /**
     * Adds a new module to a POM file.
     * If the POM file already has a module with same name, the operation will return an error.
     * That behavior can be changed though, see {@link AddElement} for further details.
     */
    public PomAddModule() {
    }

    /**
     * Adds a new module to a POM file.
     * If the POM file already has a module with same name, the operation will return an error.
     * That behavior can be changed though, see {@link AddElement} for further details.
     *
     * @param moduleName the name of the module to be set
     */
    public PomAddModule(String moduleName) {
        setModuleName(moduleName);
    }

    @Override
    public PomAddModule failIfPresent() {
        ifPresent = IfPresent.Fail;
        return this;
    }

    @Override
    public PomAddModule warnNotAddIfPresent() {
        ifPresent = IfPresent.WarnNotAdd;
        return this;
    }

    @Override
    public PomAddModule warnButAddIfPresent() {
        ifPresent = IfPresent.WarnButAdd;
        return this;
    }

    @Override
    public PomAddModule noOpIfPresent() {
        ifPresent = IfPresent.NoOp;
        return this;
    }

    @Override
    public PomAddModule overwriteIfPresent() {
        ifPresent = IfPresent.Overwrite;
        return this;
    }

    /**
     * Sets the name of the module to be set by this operation
     *
     * @param moduleName the name of the module to be set
     * @return this transformation operation
     */
    public PomAddModule setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    /**
     * Return the name of the module to be set by this operation
     *
     * @return the name of the module to be set
     */
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, moduleName, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        Exception warning = null;

        if (model.getModules().contains(moduleName)) {
            String notAddMessage = "Module " + moduleName + " was not added to POM file " + getRelativePath() + " because it is already present";
            String overwriteMessage = "Module " + moduleName + " was already present in POM file " + getRelativePath();
            switch (ifPresent) {
                case WarnNotAdd:
                    return TOExecutionResult.warning(this, new TransformationOperationException(notAddMessage));
                case WarnButAdd:
                    return TOExecutionResult.warning(this, new TransformationOperationException(overwriteMessage));
                case NoOp:
                    return TOExecutionResult.noOp(this, notAddMessage);
                case Overwrite:
                    return TOExecutionResult.success(this, overwriteMessage);
                case Fail:
                    // Fail is the default
                default:
                    return TOExecutionResult.error(this, new TransformationOperationException(notAddMessage));
            }
        }

        model.addModule(moduleName);
        String details = String.format("Module %s has been added to POM file %s", moduleName, relativePomFile);
        TOExecutionResult result = TOExecutionResult.success(this, details);

        if (warning != null) {
            result.addWarning(warning);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PomAddModule)) return false;

        PomAddModule tu = (PomAddModule) obj;
        if (!Objects.equals(tu.ifPresent, this.ifPresent)) return false;
        if (!Objects.equals(tu.moduleName, this.moduleName)) return false;

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode(super.hashCode(), ifPresent, moduleName);
    }

}