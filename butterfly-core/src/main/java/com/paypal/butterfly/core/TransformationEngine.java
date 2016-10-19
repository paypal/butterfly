package com.paypal.butterfly.core;

import com.paypal.butterfly.core.exception.InternalException;
import com.paypal.butterfly.extensions.api.*;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.extensions.api.utilities.MultipleOperations;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.exception.TransformationException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    /**
     * Perform an application transformation based on the specified {@link Transformation}
     * object
     *
     * @param transformation the transformation object
     * @throws TransformationException if the transformation is aborted for any reason
     */
    public void perform(Transformation transformation) throws TransformationException {
        if(logger.isDebugEnabled()) {
            logger.debug("Requested transformation: {}", transformation);
        }
        File transformedAppFolder = prepareOutputFolder(transformation);

        if (transformation instanceof UpgradePathTransformation) {
            UpgradePath upgradePath = ((UpgradePathTransformation) transformation).getUpgradePath();
            perform(upgradePath, transformedAppFolder);
        } else if (transformation instanceof TemplateTransformation) {
            TransformationTemplate template = ((TemplateTransformation) transformation).getTemplate();
            perform(template, transformedAppFolder);
        } else {
            throw new TransformationException("Transformation type not recognized");
        }
    }

    /*
     * Upgrade the application based on an upgrade path (from an original version to a target version)
     */
    private void perform(UpgradePath upgradePath, File transformedAppFolder) throws TransformationException {
        logger.info("====================================================================================================================================");
        logger.info("\tUpgrade path from version {} to version {}", upgradePath.getOriginalVersion(), upgradePath.getUpgradeVersion());

        UpgradeStep upgradeStep;
        while (upgradePath.hasNext()) {
            upgradeStep = upgradePath.next();

            logger.info("====================================================================================================================================");
            logger.info("\tUpgrade step");
            logger.info("\t\t* from version: {}", upgradeStep.getCurrentVersion());
            logger.info("\t\t* to version: {}", upgradeStep.getNextVersion());

            perform(upgradeStep, transformedAppFolder);
        }
    }

    /*
     * Transform the application based on a single transformation template. Notice that this transformation
     * template can also be an upgrade step
     */
    private void perform(TransformationTemplate template, File transformedAppFolder) throws TransformationException {
        logger.info("====================================================================================================================================");
        logger.info("Beginning transformation (template: {}, operations to be performed: {})", template.getClass().getName(), template.getOperationsCount());

        AtomicInteger operationsExecutionOrder = new AtomicInteger(1);

        TransformationContextImpl transformationContext = new TransformationContextImpl();

        MultipleOperations multipleOperations;
        TransformationUtility utility;
        PerformResult result;
        for(Object transformationUtilityObj: template.getUtilities()) {
            utility = (TransformationUtility) transformationUtilityObj;
            result = perform(utility, transformedAppFolder, transformationContext, operationsExecutionOrder, null);
            if(utility instanceof MultipleOperations) {
                multipleOperations = (MultipleOperations) utility;
                performMultiOperations(result, multipleOperations, transformedAppFolder, transformationContext, operationsExecutionOrder);
            }
            if (utility.isSaveResult()) {
                // Saving the whole perform result, which is different from the value that resulted from the utility execution,
                // saved in processUtilityExecutionResult

                transformationContext.putResult(((TransformationUtility) transformationUtilityObj).getName(), result);
            }
        }

        logger.info("Transformation has been completed");
    }

    private void performMultiOperations(PerformResult result, MultipleOperations multipleOperations, File transformedAppFolder, TransformationContextImpl transformationContext, AtomicInteger operationsExecutionOrder) throws TransformationException {
        PerformResult.Type performResultType = result.getType();
        if (!performResultType.equals(PerformResult.Type.EXECUTION_RESULT)) {
            // TODO
            return;
        }
        TUExecutionResult.Type executionResultType = (TUExecutionResult.Type) result.getExecutionResult().getType();
        if(!executionResultType.equals(TUExecutionResult.Type.VALUE)) {
            // TODO
            return;
        }

        List<TransformationOperation> operations = (List<TransformationOperation>) ((TUExecutionResult) result.getExecutionResult()).getValue();
        perform(operations, multipleOperations, transformedAppFolder, transformationContext, operationsExecutionOrder);
        // FIXME saving multiple operation results need to be properly taken care of
        // FIXME what if any of them fail?
    }

    /*
     * Perform multiple operations in an application
     */
    // TODO how to deal with results here???
    // First of all, Multiple operations must be converted to TO, instead of TU
    private void perform(List<TransformationOperation> operations, MultipleOperations multipleOperations, File transformedAppFolder, TransformationContextImpl transformationContext, AtomicInteger outterOpExecOrder) throws TransformationException {
        logger.info("\t{}\t - Executing {} over {} files", outterOpExecOrder.intValue(), multipleOperations.getTemplateOperation().getName(), operations.size());

        AtomicInteger innerOpExecOrder = new AtomicInteger(1);
        for(TransformationOperation operation : operations) {
            perform(operation, transformedAppFolder, transformationContext, innerOpExecOrder, outterOpExecOrder.intValue());
            // FIXME the transformation context is not having a chance to save the result of every one of these. Only the template operation is having its result saved
        }

        outterOpExecOrder.incrementAndGet();

        // TODO what should we do with individual multiple operations results?
//        return result;
    }

    /*
     * Perform an transformation utility against an application. Notice that this utility can also be
     * actually a transformation operation
     */
    private PerformResult perform(TransformationUtility utility, File transformedAppFolder, TransformationContextImpl transformationContext, AtomicInteger operationsExecutionOrder, Integer outterOrder) throws TransformationException {
        boolean isTO = utility instanceof TransformationOperation;
        String order = "-";
        if (isTO) {
            if(outterOrder != null) {
                order = String.format("%d.%d", outterOrder, operationsExecutionOrder.get());
            } else {
                order = String.valueOf(operationsExecutionOrder.get());
            }
        }
        PerformResult result = null;
        try {
            result = utility.perform(transformedAppFolder, transformationContext);

            switch (result.getType()) {
                case SKIPPED_CONDITION:
                    // Same as SKIPPED_DEPENDENCY
                case SKIPPED_DEPENDENCY:
                    if (isTO || logger.isDebugEnabled()) {
                        logger.info("\t{}\t - {}", order, result.getDetails());
                    }
                    break;
                case EXECUTION_RESULT:
                    if (isTO) {
                        processOperationExecutionResult(utility, result, order);
                    } else {
                        processUtilityExecutionResult(utility, result, transformationContext);
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
            if (isTO) operationsExecutionOrder.incrementAndGet();
        }
        return result;
    }

    private void processError(TransformationUtility utility, Exception e, String order) throws TransformationException {
        if (utility.abortOnFailure()) {
            logger.error("*** Transformation will be aborted due to failure in {}  ***", utility.getName());
            logger.error("*** Description: {}", utility.getDescription());
            logger.error("*** Cause: {}", e.getMessage());

            throw new TransformationException(utility.getName() + " failed when performing transformation", e);
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
                logger.info("\t{}\t - {}", order, executionResult.getDetails());
                break;
            case NO_OP:
                logger.info("\t{}\t - {}", order, executionResult.getDetails());
                break;
            case WARNING:
                processExecutionResultWarningType(utility, result, executionResult, order);
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
        switch (((TUExecutionResult) result.getExecutionResult()).getType()) {
            case NULL:
                if (utility.isSaveResult() && logger.isDebugEnabled()) {
                    logger.warn("\t-\t - {} ({}) has returned NULL", utility, utility.getName());
                }
                break;
            case VALUE:
                logger.debug("\t-\t - {} ({})", utility, utility.getName());
                break;
            case WARNING:
                processExecutionResultWarningType(utility, result, executionResult, "-");
                break;
            case ERROR:
                processError(utility, executionResult.getException(), "-");
                break;
            default:
                processExecutionResultUnknownType(utility, executionResult, "-");
                break;
        }
    }

    private void processExecutionResultWarningType(TransformationUtility utility, PerformResult result, ExecutionResult executionResult, String order) {
        logger.warn("\t{}\t -  '{}' has successfully been executed, but it has warnings, see debug logs for further details. Utility name: {}", order, utility.getDescription(), utility.getName());
        if (logger.isDebugEnabled()) {
            if (result.getWarnings().size() == 0) {
                logger.warn("\t\t\t * Warning message: {}", result.getDetails());
            } else {
                logger.warn("\t\t\t * Execution details: {}", executionResult.getDetails());
                logger.warn("\t\t\t * Warnings (see debug logs for further details):");
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
        String transformedAppFolderName = application.getFolder().getName() + "-transformed-" + getCurrentTimeStamp();

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

    @SuppressFBWarnings("STCAL_INVOKE_ON_STATIC_DATE_FORMAT_INSTANCE")
    public static String getCurrentTimeStamp() {
        return simpleDateFormat.format(new Date());
    }

}
