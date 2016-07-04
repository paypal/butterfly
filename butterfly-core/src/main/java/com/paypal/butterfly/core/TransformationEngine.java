package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationOperation;
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

    public void perform(Transformation transformation) throws IOException, IllegalAccessException, InstantiationException {
        logger.debug("Transformation requested: " + transformation);

        File transformedAppFolder = prepareOutputFolder(transformation.getApplication());

        int total = transformation.getTemplate().getTransformationOperationsList().size();
        logger.info("Beginning transformation (" + total + " operations to be performed)");
        int n = 1;

        TransformationOperation transformationOperation;
        for(Object transformationOperationObj: transformation.getTemplate().getTransformationOperationsList()) {
            transformationOperation = (TransformationOperation) transformationOperationObj;
            String result = transformationOperation.perform(transformedAppFolder);
            logger.info("\t" + n + " - " + result);
            n++;
        }
        logger.info("Transformation has been completed");
    }

    private File prepareOutputFolder(Application application) throws IOException {
        logger.debug("Preparing output folder");
        logger.info("Original application folder: " + application.getFolder());

        File originalAppParent = application.getFolder().getParentFile();
        String transformedAppFolderName = application.getFolder().getName() + "-transformed-" + getCurrentTimeStamp();
        File transformedAppFolder = new File(originalAppParent.getAbsolutePath() + File.separator + transformedAppFolderName);
        logger.info("Transformed application folder: " + transformedAppFolder);

        transformedAppFolder.mkdir();
        FileUtils.copyDirectory(application.getFolder(), transformedAppFolder);
        logger.debug("Transformed application folder is prepared");

        return transformedAppFolder;
    }

    public static String getCurrentTimeStamp() {
        return simpleDateFormat.format(new Date());
    }

}
