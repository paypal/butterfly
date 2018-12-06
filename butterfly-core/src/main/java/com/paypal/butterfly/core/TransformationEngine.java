package com.paypal.butterfly.core;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.paypal.butterfly.extensions.api.utilities.Abort;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.paypal.butterfly.extensions.api.*;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.extensions.api.utilities.ManualInstruction;
import com.paypal.butterfly.extensions.api.utilities.ManualInstructionRecord;
import com.paypal.butterfly.api.*;
import com.paypal.butterfly.api.exception.TransformationException;
import com.paypal.butterfly.api.TransformationMetrics;

/**
 * The transformation engine in charge of
 * applying transformations
 *
 * @author facarvalho
 */
@Component
class TransformationEngine {

    private static final Logger logger = LoggerFactory.getLogger(TransformationEngine.class);

    // This is used to create a timestamp to be applied as suffix in the transformed application folder
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private static final String ORDER_FORMAT = "%s.%d";

    private Collection<TransformationListener> transformationListeners;

    @Autowired
    private ApplicationContext applicationContext;

    private ManualInstructionsHandler manualInstructionsHandler;

    private TransformationValidator validator;

    @PostConstruct
    void setupListeners() {
        Map<String, TransformationListener> beans = applicationContext.getBeansOfType(TransformationListener.class);
        transformationListeners = beans.values();
        if (transformationListeners == null) {
            transformationListeners = Collections.emptyList();
        }

        validator = applicationContext.getBean(TransformationValidator.class);
        manualInstructionsHandler = applicationContext.getBean(ManualInstructionsHandler.class);
    }

    /**
     * Perform an application transformation based on the specified {@link TransformationRequest}
     * object
     *
     * @param transformationRequest the transformationRequest object
     * @return the result after performing this transformationRequest
     */
    TransformationResult perform(TransformationRequest transformationRequest) {

        // Throws an ApplicationValidationException if validation fails
        validator.preTransformation(transformationRequest);

        MDC.put("uid",transformationRequest.getId());

        if(logger.isDebugEnabled()) {
            logger.debug("Requested transformationRequest: {}", transformationRequest);
        }
        logger.info("Extension name:\t\t\t\t\t{}", transformationRequest.getExtensionName());
        logger.info("Extension version:\t\t\t\t{}", transformationRequest.getExtensionVersion());
        logger.info("Transformation template:\t\t\t{}", transformationRequest.getTemplateClassName());

        File transformedAppFolder = prepareOutputFolder(transformationRequest);
        List<TransformationContextImpl> transformationContexts = new ArrayList<>();

        TransformationResult transformationResult = performTransformation(transformedAppFolder, transformationRequest, transformationContexts);

        if (transformationResult.isSuccessful()) {
            manualInstructionsHandler.processManualInstructions(transformationResult, transformationContexts);
            transformationListeners.forEach(l -> l.postTransformation(transformationRequest, transformationResult));
        } else {
            transformationListeners.forEach(l -> l.postTransformationAbort(transformationRequest, transformationResult));
        }
        MDC.remove("uid");

        return transformationResult;
    }

    /*
     * Returns a list of transformation context objects to be passed to transformation listeners as part of a notification event.
     * Listeners are not suppose to modify these transformation context objects, and that is why the need of this auxiliary method.
     */
    private List<TransformationContextImpl> getTransformationContextsReadonlyList(List<TransformationContextImpl> transformationContexts) {
        // FIXME
        // It would be better to create a new list with read-only DTOs from TransformationContextImpl objects,
        // to then make it unmodifiable, and send to listeners. Otherwise listeners my change the context
        // objects, which they are not supposed to to. Right now this implementation is not preventing listeners to
        // modify transformation context objects.
        return Collections.unmodifiableList(transformationContexts);
    }

    private TransformationResult performTransformation(File transformedAppFolder, TransformationRequest transformationRequest, List<TransformationContextImpl> transformationContexts) {
        if (transformationRequest == null) {
            throw new InternalException("Transformation request cannot be null");
        }
        if (!(transformationRequest instanceof UpgradePathTransformationRequest || transformationRequest instanceof TemplateTransformationRequest)) {
            throw new InternalException("Transformation request type not recognized: " + transformationRequest.getClass().getName());
        }

        TransformationResultImpl transformationResult = new TransformationResultImpl(transformationRequest, transformedAppFolder);

        try {
            if (transformationRequest instanceof UpgradePathTransformationRequest) {
                UpgradePath upgradePath = ((UpgradePathTransformationRequest) transformationRequest).getUpgradePath();
                performUpgrade(upgradePath, transformedAppFolder, transformationContexts, transformationRequest);
            } else if (transformationRequest instanceof TemplateTransformationRequest) {
                TransformationTemplate template = ((TemplateTransformationRequest) transformationRequest).getTemplate();
                TransformationContextImpl transformationContext = performTemplate(template, transformedAppFolder, null, transformationRequest);
                transformationContexts.add(transformationContext);
            }
        } catch (InternalTransformationException e) {
            TransformationContextImpl abortedTransformationContext = e.getTransformationContext();
            if (abortedTransformationContext != null) {
                transformationContexts.add(abortedTransformationContext);
            }

            transformationResult.setAbortDetails(abortedTransformationContext.getAbortDetails());
        }

        // If the transformation was an upgrade, the list will have one metrics object per upgrade step.
        // If it is not, then the list will have just one item.
        List<TransformationMetrics> metricsList = transformationContexts.stream().map(c -> new TransformationMetricsImpl(c)).collect(Collectors.toList());
        transformationResult.setTransformationMetrics(metricsList);

        return transformationResult;
    }

    /*
     * Upgrade the application based on an upgrade path (from an original version to a target version)
     */
    private void performUpgrade(UpgradePath upgradePath, File transformedAppFolder, List<TransformationContextImpl> transformationContexts, TransformationRequest transformationRequest) throws InternalTransformationException {
        logger.info("");
        logger.info("====================================================================================================================================");
        logger.info("\tUpgrade path from version {} to version {}", upgradePath.getOriginalVersion(), upgradePath.getUpgradeVersion());

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
            previousContext = performTemplate(upgradeStep, transformedAppFolder, previousContext, transformationRequest);
            transformationContexts.add(previousContext);
        }
    }

    /*
     * Transform the application based on a single transformation template.
     * *** Notice that this transformation template can also be an upgrade step ***
     */
    private TransformationContextImpl performTemplate(TransformationTemplate template, File transformedAppFolder, TransformationContextImpl previousTransformationContext, TransformationRequest transformationRequest) throws InternalTransformationException {
        logger.info("====================================================================================================================================");
        logger.info("Beginning transformation");

        TransformationContextImpl transformationContext = performUtilities(template, template.getUtilities(), transformedAppFolder, previousTransformationContext, transformationRequest);

        logger.info("");
        logger.info("Transformation has been completed");

        return transformationContext;
    }

    /*
     * Performs a list of transformation utilities against an application.
     * Notice that any of those utilities can be operations
     */
    private TransformationContextImpl performUtilities(TransformationTemplate template, List<TransformationUtility> utilities, File transformedAppFolder, TransformationContextImpl previousTransformationContext, TransformationRequest transformationRequest) throws InternalTransformationException {
        int operationsExecutionOrder = 1;
        TransformationContextImpl transformationContext = TransformationContextImpl.getTransformationContext(previousTransformationContext);
        transformationContext.setTransformationTemplate(template);
        if (template.isBlank()) {
            File baseline = ((AbstractTransformationRequest) transformationRequest).getBaselineApplicationDir();
            if (baseline == null || !baseline.exists() || !baseline.isDirectory()) {
                // TODO save exception and abortion description into the transformationContext
                throw new InternalTransformationException("Baseline application location is invalid: " + baseline, transformationContext);
            }
            transformationContext.put(template.BASELINE, baseline);
        }

        try {
            TransformationUtility utility;
            for(Object transformationUtilityObj: utilities) {
                utility = (TransformationUtility) transformationUtilityObj;
                performUtility(utility, transformedAppFolder, transformationContext, String.valueOf(operationsExecutionOrder));
                if (utility instanceof TransformationOperation || utility instanceof TransformationUtilityParent) {
                    operationsExecutionOrder++;
                }
            }
        } catch (TransformationException e) {
            // TODO save exception and abortion description into the transformationContext
            throw new InternalTransformationException(e, transformationContext);
        }

        return transformationContext;
    }

    /*
     * Perform a condition against multiple files
     */
    private PerformResult performConditions(MultipleConditions utility, Set<File> files, File transformedAppFolder, TransformationContextImpl transformationContext) throws TransformationException {

        UtilityCondition condition;
        boolean allMode = utility.getMode().equals(MultipleConditions.Mode.ALL);
        boolean result = false;

        for (File file : files) {
            condition = utility.newConditionInstance(file);

            PerformResult innerPerformResult = condition.perform(transformedAppFolder, transformationContext);
            processUtilityExecutionResult(condition, innerPerformResult, transformationContext);

            if(innerPerformResult.getType().equals(PerformResult.Type.EXECUTION_RESULT) &&
                    (innerPerformResult.getExecutionResult().getType().equals(TUExecutionResult.Type.VALUE)
                    || innerPerformResult.getExecutionResult().getType().equals(TUExecutionResult.Type.WARNING))) {
                result = (boolean) ((TUExecutionResult) innerPerformResult.getExecutionResult()).getValue();
                if (!result && allMode || result && !allMode) {
                    break;
                }
            } else {
                Exception innerException;
                if (innerPerformResult.getType().equals(PerformResult.Type.ERROR)) {
                    innerException = innerPerformResult.getException();
                } else {
                    innerException = innerPerformResult.getExecutionResult().getException();
                }
                String exceptionMessage = String.format("Multiple utility condition %s execution failed when evaluating condition %s against file %s", utility.getName(), condition.getName(), file.getAbsolutePath());
                TransformationUtilityException outerException = new TransformationUtilityException(exceptionMessage, innerException);
                TUExecutionResult multipleExecutionResult = TUExecutionResult.error(utility, outerException);
                return PerformResult.executionResult(utility, multipleExecutionResult);
            }
        }

        TUExecutionResult multipleExecutionResult = TUExecutionResult.value(utility, result);
        return PerformResult.executionResult(utility, multipleExecutionResult);
    }

    /*
     * Perform a filter in a list of files based on a condition
     */
    private PerformResult performFiles(FilterFiles utility, Set<File> files, File transformedAppFolder, TransformationContextImpl transformationContext) throws TransformationException {

        SingleCondition condition;
        boolean conditionResult;
        List<File> subList = new ArrayList<>();

        int warnings = 0;

        for (File file : files) {
            condition = utility.newConditionInstance(file);

            PerformResult innerPerformResult = condition.perform(transformedAppFolder, transformationContext);

            processUtilityExecutionResult(condition, innerPerformResult, transformationContext);

            if(innerPerformResult.getType().equals(PerformResult.Type.EXECUTION_RESULT) &&
                    (innerPerformResult.getExecutionResult().getType().equals(TUExecutionResult.Type.VALUE)
                            || innerPerformResult.getExecutionResult().getType().equals(TUExecutionResult.Type.WARNING))) {
                conditionResult = (boolean) ((TUExecutionResult) innerPerformResult.getExecutionResult()).getValue();
                if (conditionResult) {
                    subList.add(file);
                }
                if (innerPerformResult.getExecutionResult().getType().equals(TUExecutionResult.Type.WARNING)) {
                    warnings++;
                }
            } else {
                Exception innerException;
                if (innerPerformResult.getType().equals(PerformResult.Type.ERROR)) {
                    innerException = innerPerformResult.getException();
                } else {
                    innerException = innerPerformResult.getExecutionResult().getException();
                }
                String exceptionMessage = String.format("FilterFiles %s failed when evaluating condition %s against file %s", utility.getName(), condition.getName(), file.getAbsolutePath());
                TransformationUtilityException outerException = new TransformationUtilityException(exceptionMessage, innerException);
                TUExecutionResult multipleExecutionResult = TUExecutionResult.error(utility, outerException);
                return PerformResult.executionResult(utility, multipleExecutionResult);
            }
        }

        TUExecutionResult filterFilesExecutionResult;
        if (warnings == 0) {
            filterFilesExecutionResult = TUExecutionResult.value(utility, subList);
        } else {
            filterFilesExecutionResult = TUExecutionResult.warning(utility, warnings + " warnings were generated when filtering files", subList);
        }
        return PerformResult.executionResult(utility, filterFilesExecutionResult);
    }

    /*
     * Perform a list of utilities, under a parent, in an application
     */
    private void performParent(TransformationUtilityParent utilityParent, PerformResult result, File transformedAppFolder, TransformationContextImpl transformationContext, String order) throws TransformationException {
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
            performUtility(utility, transformedAppFolder, transformationContext, childOrder);
            if (utility instanceof TransformationOperation || utility instanceof TransformationUtilityParent) {
                i++;
            }
        }
    }

    /*
     * Perform an transformation utility against an application. Notice that this utility can also be
     * actually a transformation operation
     */
    private void performUtility(TransformationUtility utility, File transformedAppFolder, TransformationContextImpl transformationContext, String order) throws TransformationException {
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
                        processOperationExecutionResult(utility, result, order, transformationContext);
                    } else {
                        TUExecutionResult executionResult = (TUExecutionResult) result.getExecutionResult();
                        Object executionValue = executionResult.getValue();

                        if(executionResult.getType().equals(TUExecutionResult.Type.ERROR)) {
                            processUtilityExecutionResult(utility, result, transformationContext);
                            break;
                        }

                        if(utility instanceof MultipleConditions) {

                            /* Executing a condition against multiple files */
                            Set<File> files = (Set<File>) executionValue;
                            result = performConditions((MultipleConditions) utility, files, transformedAppFolder, transformationContext);
                        } else if(utility instanceof FilterFiles) {

                            /* Execute a filter in a list of files based on a condition */
                            Set<File> files = (Set<File>) executionValue;
                            result = performFiles((FilterFiles) utility, files, transformedAppFolder, transformationContext);
                        }

                        processUtilityExecutionResult(utility, result, transformationContext);

                        if (utility instanceof TransformationUtilityLoop) {

                            /* Executing loops of utilities */
                            boolean iterate = executionValue instanceof Boolean && ((Boolean) executionValue).booleanValue();
                            if (iterate) {
                                TransformationUtilityLoop utilityLoop = (TransformationUtilityLoop) utility;
                                String newOrder = String.format("%s.%s", order, utilityLoop.getNextIteration());

                                logger.info("...........................");
                                logger.info("\t{}\t - Iteration {} loop {}", newOrder, utilityLoop.getNextIteration(), utilityLoop.getName());

                                performUtility(utilityLoop.run(), transformedAppFolder, transformationContext, newOrder + ".1");
                                performUtility(utilityLoop.iterate(), transformedAppFolder, transformationContext, order);
                            }
                        } else if(utility instanceof TransformationUtilityParent) {

                            /* Executing utilities parents */
                            performParent((TransformationUtilityParent) utility, result, transformedAppFolder, transformationContext, order);
                        } else if(utility instanceof ManualInstruction) {

                            /* Adding manual instruction */
                            transformationContext.registerManualInstruction((ManualInstructionRecord) executionValue);
                        }
                    }
                    break;
                case ERROR:
                    processError(utility, result.getException(), order, transformationContext);
                    break;
                default:
                    logger.error("\t{}\t - '{}' has resulted in an unexpected perform result type {}", order, utility.getName(), result.getType().name());
                    break;
            }
        } catch (TransformationUtilityException e) {
            result = PerformResult.error(utility, e);
            processError(utility, e, order, transformationContext);
        } finally {
            if (utility.isSaveResult()) {
                // Saving the whole perform result, which is different from the value that resulted from the utility execution,
                // saved in processUtilityExecutionResult

                transformationContext.putResult(utility.getName(), result);
            }
        }
    }

    private void processError(TransformationUtility utility, Exception e, String order, TransformationContextImpl transformationContext) throws TransformationException {
        if (utility.isAbortOnFailure()) {
            logger.error("*** Transformation will be aborted due to {}{} ***", (utility instanceof Abort? "" : "failure in "), utility.getName());
            String abortionMessage = utility.getAbortionMessage();
            if (abortionMessage != null) {
                logger.error("*** {} ***", abortionMessage);
            }
            if (!(utility instanceof Abort)) {
                logger.error("*** Description: {}", utility.getDescription());
                logger.error("*** Cause: {}", e.getMessage());
            }
            logger.error("*** Exception stack trace:", e);

            String exceptionMessage = (abortionMessage != null ? abortionMessage : utility.getName() + " failed when performing transformation");
            transformationContext.transformationAborted(e, exceptionMessage, utility.getName(), utility.getClass().getName());

            throw new TransformationException(exceptionMessage, e);
        } else {
            logger.error("\t{}\t -  '{}' has failed. See debug logs for further details. Utility name: {}", order, utility.getDescription(), utility.getName());
            if(logger.isDebugEnabled()) {
                logger.error(utility.getName() + " has failed due to the exception below", e);
            }
        }
    }

    private void processOperationExecutionResult(TransformationUtility utility, PerformResult result, String order, TransformationContextImpl transformationContext) throws TransformationException {
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
                processError(utility, executionResult.getException(), order, transformationContext);
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
                    logger.debug("\t-\t - {}. {} has returned NULL", utility, utility.getName());
                }
                break;
            case VALUE:
                logger.debug("\t-\t - [{}][Result: {}][Utility: {}]", utility.toString(),  executionResult.getValue().toString(), utility.getName());
                break;
            case WARNING:
                processExecutionResultWarningType(utility, executionResult, "-");
                break;
            case ERROR:
                processError(utility, executionResult.getException(), "-", transformationContext);
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

    private File prepareOutputFolder(TransformationRequest transformationRequest) {
        logger.debug("Preparing output folder");

        Application application = transformationRequest.getApplication();
        Configuration configuration = transformationRequest.getConfiguration();
        File transformedAppFolder;

        logger.info("Original application folder:\t\t" + application.getFolder());

        if (configuration.isModifyOriginalFolder()) {
            transformedAppFolder = application.getFolder();
        } else {
            File originalAppParent = application.getFolder().getParentFile();
            if (originalAppParent == null) {
                originalAppParent = new File(System.getProperty("user.dir"));
            }

            String transformedAppFolderName = application.getFolder().getName() + "-transformed-" + simpleDateFormat.format(new Date()) + "-" + transformationRequest.getId();

            if(configuration.getOutputFolder() != null) {
                if(!configuration.getOutputFolder().exists()) {
                    throw new IllegalArgumentException("Invalid output folder (" + configuration.getOutputFolder() + ")");
                }
                transformedAppFolder = new File(configuration.getOutputFolder(), transformedAppFolderName);
            } else {
                transformedAppFolder = new File(originalAppParent, transformedAppFolderName);
            }
        }

        logger.info("Transformed application folder:\t" + transformedAppFolder);

        File baselineAppFolder = ((AbstractTransformationRequest) transformationRequest).getBaselineApplicationDir();

        if (transformationRequest.isBlank()) {
            logger.info("Baseline application folder:\t\t{}", baselineAppFolder);
        }

        if (configuration.isModifyOriginalFolder()) {
            if (transformationRequest.isBlank()) {
                try {
                    FileUtils.copyDirectory(application.getFolder(), baselineAppFolder);
                } catch (IOException e) {
                    String exceptionMessage = String.format(
                            "An exception occurred when preparing the baseline application folder (%s). Check also if the original application folder (%s) is valid",
                            baselineAppFolder, application.getFolder());
                    throw new InternalException(exceptionMessage, e);
                }
                logger.debug("Baseline application folder is prepared");
            }
        } else {
            if (transformedAppFolder.exists()) {
                String exceptionMessage = String.format("Transformed application folder (%s) could not be created because it already exists", transformedAppFolder);
                throw new InternalException(exceptionMessage);
            }
            boolean bDirCreated = transformedAppFolder.mkdir();
            if(bDirCreated){
                if (!transformationRequest.isBlank()) {
                    try {
                        FileUtils.copyDirectory(application.getFolder(), transformedAppFolder);
                    } catch (IOException e) {
                        String exceptionMessage = String.format(
                                "An exception occurred when preparing the transformed application folder (%s). Check also if the original application folder (%s) is valid",
                                transformedAppFolder, application.getFolder());
                        throw new InternalException(exceptionMessage, e);
                    }
                    logger.debug("Transformed application folder is prepared");
                }
            } else {
                String exceptionMessage = String.format("Transformed application folder (%s) could not be created", transformedAppFolder);
                throw new InternalException(exceptionMessage);
            }
        }

        return transformedAppFolder;
    }

}
