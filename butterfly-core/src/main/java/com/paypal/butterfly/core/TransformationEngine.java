package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.core.exception.InternalException;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.exception.TransformationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The transformation engine in charge of
 * applying transformations
 *
 * @author facarvalho
 */
@Component
public class TransformationEngine {

    private static final Logger logger = LoggerFactory.getLogger(TransformationEngine.class);

    private static final String TIMESTAMP_SUFFIX_FORMAT = "yyyyMMddHHmmssSSS";

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIMESTAMP_SUFFIX_FORMAT);

    public void perform(Transformation transformation) throws TransformationException {
        logger.debug("Transformation requested: " + transformation);

        File transformedAppFolder = prepareOutputFolder(transformation);

        TransformationTemplate template = transformation.getTemplate();

        int total = template.getTransformationUtilitiesList().size();
        logger.info("Beginning transformation (" + template.getOperationsCount() + " operations to be performed)");
        int operationsExecutionOrder = 1;

        TransformationContext transformationContext = new TransformationContextImpl();

        TransformationUtility utility;
        TransformationOperation operation;
        for(Object transformationUtilityObj: template.getTransformationUtilitiesList()) {
            if(transformationUtilityObj instanceof TransformationOperation) {
                operation = (TransformationOperation) transformationUtilityObj;
                String result = null;
                try {
                    result = operation.perform(transformedAppFolder, transformationContext);
                    String key = (operation.getContextAttributeName() != null ? operation.getContextAttributeName() : operation.getName());
                    transformationContext.put(key, result);
                } catch (TransformationOperationException e) {
                    if (operation.abortTransformationOnFailure()) {
                        logger.error("*** Transformation will be aborted due to failed operation ***");
                        logger.error("*** Operation: \t" + operation.getDescription());
                        logger.error("*** Cause: \t" + e.getCause());
                        throw new TransformationException("Operation " + operation.getName() + " failed when performing transformation", e);
                    } else {
                        // TODO
                        // State/save/log the exception, and go on with transformation
                    }
                }
                logger.info("\t" + operationsExecutionOrder + "\t - " + result);
                operationsExecutionOrder++;
            } else {
                utility = (TransformationUtility) transformationUtilityObj;
                try {
                    Object result = utility.perform(transformedAppFolder, transformationContext);
                    String key = (utility.getContextAttributeName() != null ? utility.getContextAttributeName() : utility.getName());
                    transformationContext.put(key, result);
                    logger.debug("\t-\t - {} ({})", utility, utility.getName());
                } catch (TransformationUtilityException e) {
                    logger.error("*** Transformation will be aborted due to failed utility ***");
                    logger.error("*** Utility: \t" + utility.getDescription());
                    logger.error("*** Cause: \t" + e.getCause());
                    throw new TransformationException("Utility " + utility.getName() + " failed when being executed", e);
                }
            }
        }
        logger.info("Transformation has been completed");
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

        transformedAppFolder.mkdir();
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

        return transformedAppFolder;
    }

    public static String getCurrentTimeStamp() {
        return simpleDateFormat.format(new Date());
    }

}
