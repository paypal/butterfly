package com.paypal.butterfly.core;

import com.paypal.butterfly.core.exception.InternalException;
import com.paypal.butterfly.extensions.api.*;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
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

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    // TODO do a little refactoring in all these perform methods. There is too much duplicated code here

    public void perform(Transformation transformation) throws TransformationException {
        if(logger.isDebugEnabled()) {
            logger.debug("Requested transformation: " + transformation);
        }

        File transformedAppFolder = prepareOutputFolder(transformation);

        TransformationTemplate template = transformation.getTemplate();
        logger.info("Beginning transformation (" + template.getOperationsCount() + " operations to be performed)");
        AtomicInteger operationsExecutionOrder = new AtomicInteger(1);

        TransformationContextImpl transformationContext = new TransformationContextImpl();

        TransformationOperation operation;
        MultipleOperations multipleOperations;
        TransformationUtility utility;
        PerformResult result;
        for(Object transformationUtilityObj: template.getTransformationUtilitiesList()) {
            utility = (TransformationUtility) transformationUtilityObj;
            if(transformationUtilityObj instanceof TransformationOperation) {
                operation = (TransformationOperation) transformationUtilityObj;
                result = performOperation(operation, transformedAppFolder, transformationContext, operationsExecutionOrder, null);
            } else if(transformationUtilityObj instanceof MultipleOperations) {
                multipleOperations = (MultipleOperations) transformationUtilityObj;
                result = performMultipleOperations(multipleOperations, transformedAppFolder, transformationContext, operationsExecutionOrder);
                // FIXME saving multiple operation results need to be properly taken care of
            } else {
                result = performUtility(utility, transformedAppFolder, transformationContext);
            }
            if (utility.isSaveResult()) {
                transformationContext.putResult(((TransformationUtility) transformationUtilityObj).getName(), result);
            }
        }
        logger.info("Transformation has been completed");
    }

    private PerformResult performOperation(TransformationOperation operation, File transformedAppFolder, TransformationContext transformationContext, AtomicInteger operationsExecutionOrder, Integer outterOrder) throws TransformationException {
        PerformResult result = null;
        String order;
        if(outterOrder != null) {
            order = String.format("%d.%d", outterOrder, operationsExecutionOrder.get());
        } else {
            order = String.valueOf(operationsExecutionOrder.get());
        }
        try {
            result = operation.perform(transformedAppFolder, transformationContext);

            switch (result.getType()) {
                case SKIPPED_CONDITION:
                case SKIPPED_DEPENDENCY:
                    logger.info("\t{}\t - {}", order, result.getDetails());
                    break;
                case EXECUTION_RESULT:
                    TOExecutionResult executionResult = (TOExecutionResult) result.getExecutionResult();
                    switch (executionResult.getType()) {
                        case SUCCESS:
                            logger.info("\t{}\t - {}", order, executionResult.getDetails());
                            break;
                        case NO_OP:
                            logger.warn("\t{}\t - {}", order, executionResult.getDetails());
                            break;
                        case WARNING:
                            logger.warn("\t{}\t - Operation '{}' has successfully been executed, but it has warnings, see debug logs for further details", order, operation.getName());
                            if (logger.isDebugEnabled()) {
                                if (result.getWarnings().size() == 0) {
                                    logger.warn("\t\t\t * Warning message: {}", result.getDetails());
                                } else {
                                    logger.warn("\t\t\t * Execution details: {}", executionResult.getDetails());
                                    logger.warn("\t\t\t * Warnings (see debug logs for further details):");
                                    for (Exception warning : executionResult.getWarnings()) {
                                        String message = String.format("\t\t\t\t - %s: %s", warning.getClass().getName(), warning.getMessage());
                                        logger.warn(message, warning);
                                    }
                                }
                            }
                            break;
                        case ERROR:
                            if(logger.isDebugEnabled()) {
                                logger.error("Transformation operation " + operation.getName() + " has failed due to the exception below", executionResult.getException());
                            }
                            logger.error("\t{}\t - Operation '{}' has failed. See debug logs for further details.", order, operation.getName());
                            break;
                        default:
                            logger.error("\t{}\t - Operation '{}' has resulted in an unexpected execution result type {}", order, operation.getName(), executionResult.getType().name());
                            break;
                    }
                    break;
                case ERROR:
                    if(logger.isDebugEnabled()) {
                        logger.error("Transformation operation " + operation.getName() + " has failed due to the exception below", result.getException());
                    }
                    logger.error("\t{}\t - Operation '{}' has failed . See debug logs for further details.", order, operation.getName());
                    break;
                default:
                    logger.error("\t{}\t - Operation '{}' has resulted in an unexpected perform result type {}", order, operation.getName(), ((TUExecutionResult) result.getExecutionResult()).getType().name());
                    break;
            }
        } catch (TransformationOperationException e) {
            result = PerformResult.error(operation, e);
            if (operation.abortOnFailure()) {
                logger.error("*** Transformation will be aborted due to failed operation ***");
                logger.error("*** Operation: {} - {}", operation.getName(), operation.getDescription());
                logger.error("*** Cause: " + e.getCause());

                throw new TransformationException("Operation " + operation.getName() + " failed when performing transformation", e);
            } else {
                if(logger.isDebugEnabled()) {
                    logger.debug("Transformation operation " + operation.getName() + " has failed due to the exception below", e);
                }
                logger.error("\t{}\t - Operation '{}' has failed. See debug logs for further details.", order, operation.getName());
            }
        } finally {
            operationsExecutionOrder.incrementAndGet();
        }
        return result;
    }

    // TODO how to deal with results here???
    // First of all, Multiple operations must be converted to TO, instead of TU
    private PerformResult performMultipleOperations(MultipleOperations multipleOperations, File transformedAppFolder, TransformationContext transformationContext, AtomicInteger outterOpExecOrder) throws TransformationException {
        List<TransformationOperation> operations;
        PerformResult result;
        try {
            result = multipleOperations.perform(transformedAppFolder, transformationContext);

            // TODO what if the result type is not EXECUTION_RESULT?
            operations = (List<TransformationOperation>) ((TUExecutionResult) result.getExecutionResult()).getValue();
        } catch (TransformationUtilityException e) {

            // TODO what about abortOnFailure for MultipleOperations?

            logger.error("*** Transformation will be aborted due to failed utility ***");
            logger.error("*** Utility: {} - {}", multipleOperations.getName(), multipleOperations.getDescription());
            logger.error("*** Cause: " + e.getCause());

            throw new TransformationException("Utility " + multipleOperations.getName() + " failed when being executed", e);
        }

        logger.info("\t{}\t - Executing {} over {} files", outterOpExecOrder.intValue(), multipleOperations.getTemplateOperation().getName(), operations.size());

        AtomicInteger innerOpExecOrder = new AtomicInteger(1);
        for(TransformationOperation operation : operations) {
            performOperation(operation, transformedAppFolder, transformationContext, innerOpExecOrder, outterOpExecOrder.intValue());
            // FIXME the transformation context is not having a chance to save the result of every one of these. Only the template operation is having its result saved
        }

        outterOpExecOrder.incrementAndGet();

        return result;
    }

    private PerformResult performUtility(TransformationUtility utility, File transformedAppFolder, TransformationContextImpl transformationContext) throws TransformationException {
        PerformResult result = null;
        try {
            result = utility.perform(transformedAppFolder, transformationContext);

            switch (result.getType()) {
                case SKIPPED_CONDITION:
                case SKIPPED_DEPENDENCY:
                    if (logger.isDebugEnabled()) {
                        logger.info("\t-\t - {}", result.getDetails());
                    }
                    break;
                case EXECUTION_RESULT:
                    if (utility.isSaveResult()) {
                        String key = (utility.getContextAttributeName() != null ? utility.getContextAttributeName() : utility.getName());
                        transformationContext.put(key, ((TUExecutionResult) result.getExecutionResult()).getValue());
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
                            logger.warn("\t\t - Utility '{}' has successfully been executed, but it has warnings, see debug logs for further details", utility.getName());
                            if (logger.isDebugEnabled()) {
                                if (result.getWarnings().size() == 0) {
                                    logger.warn("\t\t\t * Warning message: {}", result.getDetails());
                                } else {
                                    logger.warn("\t\t\t * Execution details: {}", result.getDetails());
                                    logger.warn("\t\t\t * Warnings (see debug logs for further details):");
                                    for (Exception warning : result.getWarnings()) {
                                        String message = String.format("\t\t\t\t - %s: %s", warning.getClass().getName(), warning.getMessage());
                                        logger.warn(message, warning);
                                    }
                                }
                            }
                            break;
                        case ERROR:
                            if(logger.isDebugEnabled()) {
                                logger.debug("Transformation utility " + utility.getName() + " has failed due to the exception below", result.getException());
                            }
                            logger.error("\t \t - Utility '{}' has failed. See debug logs for further details.", utility.getName());
                            break;
                    }
                    break;
                case ERROR:
                    if(logger.isDebugEnabled()) {
                        logger.error("Transformation utility " + utility.getName() + " has failed due to the exception below", result.getException());
                    }
                    logger.error("\t-\t - Utility '{}' has failed . See debug logs for further details.", utility.getName());
                    break;
                default:
                    logger.error("\t-\t - Utility '{}' has resulted in an unexpected perform result type {}", utility.getName(), ((TUExecutionResult) result.getExecutionResult()).getType().name());
                    break;
            }
        } catch (TransformationUtilityException e) {
            result = PerformResult.error(utility, e);
            if(utility.abortOnFailure()) {
                logger.error("*** Transformation will be aborted due to failed utility ***");
                logger.error("*** Utility: {} - {}", utility.getName(), utility.getDescription());
                logger.error("*** Cause: " + e.getCause());

                throw new TransformationException("Utility " + utility.getName() + " failed when being executed", e);
            } else {
                if(logger.isDebugEnabled()) {
                    logger.debug("Transformation utility " + utility.getName() + " has failed due to the exception below", e);
                }
                logger.error("\t \t - Utility '{}' has failed. See debug logs for further details.", utility.getName());
            }
        }
        return result;
    }

    private File prepareOutputFolder(Transformation transformation) {
        logger.debug("Preparing output folder");

        Application application =  transformation.getApplication();
        Configuration configuration =  transformation.getConfiguration();

        logger.info("Original application folder: " + application.getFolder());

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

        logger.info("Transformed application folder: " + transformedAppFolder);

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
