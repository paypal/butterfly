package com.paypal.butterfly.utilities.operations.pom;

import org.apache.maven.model.Model;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;

/**
 * Removes a module entry from a POM file.
 * If the POM file does not have the specified module, the operation will return an error.
 * That behavior can be changed though, see {@link ChangeOrRemoveElement} for further details.
 *
 * @author facarvalho
 */
public class PomRemoveModule extends AbstractPomOperation<PomRemoveModule> implements ChangeOrRemoveElement<PomRemoveModule> {

    private static final String DESCRIPTION = "Remove module %s from POM file %s";

    private String moduleName;

    private IfNotPresent ifNotPresent = IfNotPresent.Fail;

    /**
     * Removes a module entry from a POM file.
     * If the POM file does not have the specified module, the operation will return an error.
     * That behavior can be changed though, see {@link ChangeOrRemoveElement} for further details.
     */
    public PomRemoveModule() {
    }

    /**
     * Removes a module entry from a POM file.
     * If the POM file does not have the specified module, the operation will return an error.
     * That behavior can be changed though, see {@link ChangeOrRemoveElement} for further details.
     *
     * @param moduleName module to be removed
     */
    public PomRemoveModule(String moduleName) {
        setModuleName(moduleName);
    }

    public PomRemoveModule setModuleName(String moduleName) {
        checkForBlankString("Module Name", moduleName);
        this.moduleName = moduleName;
        return this;
    }

    @Override
    public PomRemoveModule failIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomRemoveModule warnIfNotPresent() {
        ifNotPresent = IfNotPresent.Warn;
        return this;
    }

    @Override
    public PomRemoveModule noOpIfNotPresent() {
        ifNotPresent = IfNotPresent.NoOp;
        return this;
    }

    public String getModuleName() {
        return moduleName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, moduleName, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        TOExecutionResult result;

        if(!model.getModules().remove(moduleName)) {
            String details = String.format("Module %s has not been removed from POM file %s because it is not present", moduleName, relativePomFile);
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
        } else {
            String details = String.format("Module %s has been removed from POM file %s", moduleName, relativePomFile);
            result = TOExecutionResult.success(this, details);
        }

        return result;
    }

}

