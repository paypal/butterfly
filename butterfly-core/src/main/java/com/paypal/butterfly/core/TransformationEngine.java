package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.core.exception.InternalException;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
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

        File transformedAppFolder = prepareOutputFolder(transformation.getApplication());

        int total = transformation.getTemplate().getTransformationOperationsList().size();
        logger.info("Beginning transformation (" + total + " operations to be performed)");
        int n = 1;

        TransformationOperation transformationOperation;
        for(Object transformationOperationObj: transformation.getTemplate().getTransformationOperationsList()) {
            transformationOperation = (TransformationOperation) transformationOperationObj;
            String result = null;
            try {
                result = transformationOperation.perform(transformedAppFolder);
            } catch (TransformationOperationException e) {
                if(transformationOperation.abortTransformationOnFailure()) {
                    logger.error("*** Transformation will be aborted due to failed operation ***");
                    logger.error("*** Operation: \t" + transformationOperation.getDescription());
                    logger.error("*** Cause: \t" + e.getCause());
                    throw new TransformationException("Operation " + transformationOperation.getName() + " failed when performing transformation", e);
                } else {
                    // TODO
                    // State/save warning and go on with transformation
                }
            }
            logger.info("\t" + n + " - " + result);
            n++;
        }
        logger.info("Transformation has been completed");
    }

    private File prepareOutputFolder(Application application) {
        logger.debug("Preparing output folder");
        logger.info("Original application folder: " + application.getFolder());

        File originalAppParent = application.getFolder().getParentFile();
        String transformedAppFolderName = application.getFolder().getName() + "-transformed-" + getCurrentTimeStamp();
        File transformedAppFolder = new File(originalAppParent.getAbsolutePath() + File.separator + transformedAppFolderName);
        logger.info("Transformed application folder: " + transformedAppFolder);

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
