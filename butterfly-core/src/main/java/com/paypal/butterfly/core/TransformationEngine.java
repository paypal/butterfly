package com.paypal.butterfly.core;

import com.paypal.butterfly.core.exception.InternalException;
import com.paypal.butterfly.extensions.api.*;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.extensions.api.utilities.ManualInstruction;
import com.paypal.butterfly.extensions.api.utilities.ManualInstructionRecord;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.TransformationResult;
import com.paypal.butterfly.facade.exception.TransformationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The transformation engine in charge of
 * applying transformations
 *
 * @author facarvalho
 */
@Component
public class TransformationEngine {

    private static final Logger logger = LoggerFactory.getLogger(TransformationEngine.class);

    // This is used to create a timestamp to be applied as suffix in the transformed application folder
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static final String ORDER_FORMAT = "%s.%d";

    private Collection<TransformationListener> transformationListeners;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void setupListeners() {
        Map<String, TransformationListener> beans = applicationContext.getBeansOfType(TransformationListener.class);
        transformationListeners = beans.values();
    }

    /**
     * Perform an application transformation based on the specified {@link Transformation}
     * object
     *
     * @param transformation the transformation object
     * @throws TransformationException if the transformation is aborted for any reason
     */
    public TransformationResult perform(Transformation transformation) throws TransformationException {
        if(logger.isDebugEnabled()) {
            logger.debug("Requested transformation: {}", transformation);
        }

        File transformedAppFolder = prepareOutputFolder(transformation);
        List<TransformationContextImpl> transformationContexts;

        if (transformation instanceof UpgradePathTransformation) {
            UpgradePath upgradePath = ((UpgradePathTransformation) transformation).getUpgradePath();
            transformationContexts = perform(upgradePath, transformedAppFolder);
        } else if (transformation instanceof TemplateTransformation) {
            TransformationTemplate template = ((TemplateTransformation) transformation).getTemplate();
            TransformationContextImpl transformationContext = perform(template, transformedAppFolder, null);
            transformationContexts = new ArrayList<>();
            transformationContexts.add(transformationContext);
        } else {
            throw new TransformationException("Transformation type not recognized");
        }

        triggerPostTransformationEvents(transformation, transformationContexts);

        TransformationResult transformationResult = new TransformationResultImpl(
                transformation.getConfiguration(),
                transformedAppFolder,
                transformation.getManualInstructionsFile());

        return transformationResult;
    }

    private void triggerPostTransformationEvents(Transformation transformation, List<TransformationContextImpl> transformationContexts) {
        for (TransformationListener listener : transformationListeners) {
            listener.postTransformation(transformation, transformationContexts);
        }
    }

    /*
     * Upgrade the application based on an upgrade path (from an original version to a target version)
     */
    private List<TransformationContextImpl> perform(UpgradePath upgradePath, File transformedAppFolder) throws TransformationException {
        logger.info("");
        logger.info("====================================================================================================================================");
        logger.info("\tUpgrade path from version {} to version {}", upgradePath.getOriginalVersion(), upgradePath.getUpgradeVersion());

        List<TransformationContextImpl> transformationContexts = new ArrayList<>();

        UpgradeStep upgradeStep;
        TransformationContextImpl previousContext = null;
        while (upgradePath.hasNext()) {
            upgradeStep = upgradePath.next();

            logger.info("");
            logger.info("====================================================================================================================================");
            logger.info("\tUpgrade step");
            logger.info("\t\t* from version: {}", upgradeStep.getCurrentVersion());
            logger.info("\t\t* to version: {}", upgradeStep.getNextVersion());

            // The context passed to this method call is not the same as the one returned,
            // although the variable holding them is the same
            previousContext = perform(upgradeStep, transformedAppFolder, previousContext);
            transformationContexts.add(previousContext);
        }

        return transformationContexts;
    }

    /*
     * Transform the application based on a single transformation template. Notice that this transformation
     * template can also be an upgrade step
     */
    private TransformationContextImpl perform(TransformationTemplate template, File transformedAppFolder, TransformationContextImpl previousTransformationContext) throws TransformationException {
        logger.info("====================================================================================================================================");
        logger.info("Beginning transformation");

        TransformationContextImpl transformationContext = perform(template.getUtilities(), transformedAppFolder, previousTransformationContext);
        transformationContext.setTransformationTemplate(template);

        logger.info("Transformation has been completed");

        return transformationContext;
    }

    /*
     * Performs a list of transformation utilities against an application.
     * Notice that any of those utilities can be operations
     */
    private TransformationContextImpl perform(List<TransformationUtility> utilities, File transformedAppFolder, TransformationContextImpl previousTransformationContext) throws TransformationException {
        int operationsExecutionOrder = 1;
        TransformationContextImpl transformationContext = TransformationContextImpl.getTransformationContext(previousTransformationContext);

        TransformationUtility utility;
        for(Object transformationUtilityObj: utilities) {
            utility = (TransformationUtility) transformationUtilityObj;
            perform(utility, transformedAppFolder, transformationContext, String.valueOf(operationsExecutionOrder));
            if (utility instanceof TransformationOperation || utility instanceof TransformationUtilityParent) {
                operationsExecutionOrder++;
            }
        }

        return transformationContext;
    }

    /*
     * Perform a list of utilities in an application
     */
    private void perform(TransformationUtilityParent utilityParent, PerformResult result, File transformedAppFolder, TransformationContextImpl transformationContext, String order) throws TransformationException {
        TUExecutionResult.Type executionResultType = (TUExecutionResult.Type) result.getExecutionResult().getType();
        if(!executionResultType.equals(TUExecutionResult.Type.VALUE)) {
            processUtilityExecutionResult((TransformationUtility) utilityParent, result, transformationContext);
            return;
        }

        // TODO print number of \t based on depth of parents
        logger.info("\t{}\t - Executing utilities parent {}", order, utilityParent.getName());

        String childOrder;
        int i = 1;
        for(TransformationUtility utility : utilityParent.getChildren()) {
            childOrder = String.format(ORDER_FORMAT, order, i);
            perform(utility, transformedAppFolder, transformationContext, childOrder);
            if (utility instanceof TransformationOperation || utility instanceof TransformationUtilityParent) {
                i++;
            }
        }
    }

    /*
     * Perform an transformation utility against an application. Notice that this utility can also be
     * actually a transformation operation
     */
    private void perform(TransformationUtility utility, File transformedAppFolder, TransformationContextImpl transformationContext, String order) throws TransformationException {
        boolean isTO = utility instanceof TransformationOperation;
        PerformResult result = null;
        try {
            result = utility.perform(transformedAppFolder, transformationContext);

            switch (result.getType()) {
                case SKIPPED_CONDITION:
                    // Same as SKIPPED_DEPENDENCY
                case SKIPPED_DEPENDENCY:
                    if (isTO || logger.isDebugEnabled()) {
                        // TODO print number of \t based on depth of parents
                        logger.debug("\t{}\t - {}", order, result.getDetails());
                    }
                    break;
                case EXECUTION_RESULT:
                    if (isTO) {
                        processOperationExecutionResult(utility, result, order);
                    } else {
                        TUExecutionResult executionResult = (TUExecutionResult) result.getExecutionResult();
                        Object executionValue = executionResult.getValue();

                        processUtilityExecutionResult(utility, result, transformationContext);
                        if (utility instanceof TransformationUtilityLoop) {

                            /* Executing loops of utilities */
                            boolean iterate = executionValue instanceof Boolean && ((Boolean) executionValue).booleanValue();
                            if (iterate) {
                                TransformationUtilityLoop utilityLoop = (TransformationUtilityLoop) utility;
                                String newOrder = String.format("%s.%s", order, utilityLoop.getNextIteration());

                                logger.info("...........................");
                                logger.info("\t{}\t - Iteration {} loop {}", newOrder, utilityLoop.getNextIteration(), utilityLoop.getName());

                                perform(utilityLoop.run(), transformedAppFolder, transformationContext, newOrder + ".1");
                                perform(utilityLoop.iterate(), transformedAppFolder, transformationContext, order);
                            }
                        } else if(utility instanceof TransformationUtilityParent) {

                            /* Executing utilities parents */
                            perform((TransformationUtilityParent) utility, result, transformedAppFolder, transformationContext, order);
                        } else if(utility instanceof ManualInstruction) {

                            /* Adding manual instruction */
                            transformationContext.registerManualInstruction((ManualInstructionRecord) executionValue);
                        }
                    }
                    break;
                case ERROR:
                    processError(utility, result.getException(), order);
                    break;
                default:
                    logger.error("\t{}\t - '{}' has resulted in an unexpected perform result type {}", order, utility.getName(), result.getType().name());
                    break;
            }
        } catch (TransformationUtilityException e) {
            result = PerformResult.error(utility, e);
            processError(utility, e, order);
        } finally {
            if (utility.isSaveResult()) {
                // Saving the whole perform result, which is different from the value that resulted from the utility execution,
                // saved in processUtilityExecutionResult

                transformationContext.putResult(utility.getName(), result);
            }
        }
    }

    private void processError(TransformationUtility utility, Exception e, String order) throws TransformationException {
        if (utility.abortOnFailure()) {
            logger.error("*** Transformation will be aborted due to failure in {} ***", utility.getName());
            String abortionMessage = utility.getAbortionMessage();
            if (abortionMessage != null) {
                logger.error("*** {} ***", abortionMessage);
            }
            logger.error("*** Description: {}", utility.getDescription());
            logger.error("*** Cause: {}", e.getMessage());
            logger.error("*** Exception stack trace:", e);

            String exceptionMessage = (abortionMessage != null ? abortionMessage : utility.getName() + " failed when performing transformation");
            throw new TransformationException(exceptionMessage, e);
        } else {
            logger.error("\t{}\t -  '{}' has failed. See debug logs for further details. Utility name: {}", order, utility.getDescription(), utility.getName());
            if(logger.isDebugEnabled()) {
                logger.error(utility.getName() + " has failed due to the exception below", e);
            }
        }
    }

    private void processOperationExecutionResult(TransformationUtility utility, PerformResult result, String order) throws TransformationException {
        TOExecutionResult executionResult = (TOExecutionResult) result.getExecutionResult();
        switch (executionResult.getType()) {
            case SUCCESS:
                // TODO print number of \t based on depth of parents
                logger.info("\t{}\t - {}", order, executionResult.getDetails());
                break;
            case NO_OP:
                // TODO print number of \t based on depth of parents
                logger.debug("\t{}\t - {}", order, executionResult.getDetails());
                break;
            case WARNING:
                processExecutionResultWarningType(utility, executionResult, order);
                break;
            case ERROR:
                processError(utility, executionResult.getException(), order);
                break;
            default:
                processExecutionResultUnknownType(utility, executionResult, order);
                break;
        }
    }

    private void processUtilityExecutionResult(TransformationUtility utility, PerformResult result, TransformationContextImpl transformationContext) throws TransformationException {
        TUExecutionResult executionResult = (TUExecutionResult) result.getExecutionResult();
        if (utility.isSaveResult()) {
            // Saving the value that resulted from the utility execution, which is different from the whole perform result
            // object saved in the main perform method

            String key = (utility.getContextAttributeName() != null ? utility.getContextAttributeName() : utility.getName());
            transformationContext.put(key, executionResult.getValue());
        }
        switch (executionResult.getType()) {
            case NULL:
                if (utility.isSaveResult() && logger.isDebugEnabled()) {
                    logger.warn("\t-\t - {}. {} has returned NULL", utility, utility.getName());
                }
                break;
            case VALUE:
                logger.debug("\t-\t - [{}][Result: {}][Utility: {}]", StringUtils.abbreviate(utility.toString(), 240),  StringUtils.abbreviate(executionResult.getValue().toString(), 120), utility.getName());
                break;
            case WARNING:
                processExecutionResultWarningType(utility, executionResult, "-");
                break;
            case ERROR:
                processError(utility, executionResult.getException(), "-");
                break;
            default:
                processExecutionResultUnknownType(utility, executionResult, "-");
                break;
        }
    }

    private void processExecutionResultWarningType(TransformationUtility utility, ExecutionResult executionResult, String order) {
        logger.warn("\t{}\t -  '{}' has successfully been executed, but it has warnings, see debug logs for further details. Utility name: {}", order, utility.getDescription(), utility.getName());
        if (logger.isDebugEnabled()) {
            if (executionResult.getWarnings().size() == 0) {
                logger.warn("\t\t\t * Warning message: {}", executionResult.getDetails());
            } else {
                logger.warn("\t\t\t * Execution details: {}", executionResult.getDetails());
                logger.warn("\t\t\t * Warnings:");
                for (Object warning : executionResult.getWarnings()) {
                    String message = String.format("\t\t\t\t - %s: %s", warning.getClass().getName(), ((Exception) warning).getMessage());
                    logger.warn(message, warning);
                }
            }
        }
    }

    private void processExecutionResultUnknownType(TransformationUtility utility, ExecutionResult executionResult, String order) {
        logger.error("\t{}\t - '{}' has resulted in an unexpected execution result type {}", order, utility.getName(), executionResult.getType());
    }

    private File prepareOutputFolder(Transformation transformation) {
        logger.debug("Preparing output folder");

        Application application =  transformation.getApplication();
        Configuration configuration =  transformation.getConfiguration();

        logger.info("Original application folder:\t\t" + application.getFolder());

        File originalAppParent = application.getFolder().getParentFile();
        String transformedAppFolderName = application.getFolder().getName() + "-transformed-" + simpleDateFormat.format(new Date());

        File transformedAppFolder;

        if(configuration.getOutputFolder() != null) {
            if(!configuration.getOutputFolder().exists()) {
                throw new IllegalArgumentException("Invalid output folder (" + configuration.getOutputFolder() + ")");
            }
            transformedAppFolder = new File(configuration.getOutputFolder().getAbsolutePath() + File.separator + transformedAppFolderName);
        } else {
            transformedAppFolder = new File(originalAppParent.getAbsolutePath() + File.separator + transformedAppFolderName);
        }

        logger.info("Transformed application folder:\t" + transformedAppFolder);

        transformation.setTransformedApplicationLocation(transformedAppFolder);

        boolean bDirCreated = transformedAppFolder.mkdir();
        if(bDirCreated){
            try {
                FileUtils.copyDirectory(application.getFolder(), transformedAppFolder);
            } catch (IOException e) {
                String exceptionMessage = String.format(
                        "An error occurred when preparing the transformed application folder (%s). Check also if the original application folder (%s) is valid",
                        transformedAppFolder, application.getFolder());
                logger.error(exceptionMessage, e);
                throw new InternalException(exceptionMessage, e);
            }
            logger.debug("Transformed application folder is prepared");
        }else{
            String exceptionMessage = String.format("Transformed application folder (%s) could not be created", transformedAppFolder);
            InternalException ie  = new InternalException(exceptionMessage);
            logger.error(exceptionMessage, ie);
            throw ie;
        }
        return transformedAppFolder;
    }

}
