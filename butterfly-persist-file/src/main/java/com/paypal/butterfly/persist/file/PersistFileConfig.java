package com.paypal.butterfly.persist.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.paypal.butterfly.api.TransformationListener;
import com.paypal.butterfly.api.TransformationRequest;
import com.paypal.butterfly.api.TransformationResult;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configuration
@SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
public class PersistFileConfig {

    // TODO
    // A few improvements to do here in the future:
    //      - Make the file name configurable
    //      - Make the file location configurable
    //      - Make the JSON content "pretty or compact" configurable
    //      - Add support to other formats, like YAML

    private static final Logger logger = LoggerFactory.getLogger(PersistFileConfig.class);

    private static final int DEFAULT_NAME_APP_NAME_FORMAT = 10;
    private static final int DEFAULT_NAME_TRANSFORMATION_FOLDER_FORMAT = 20;
    private static final int DEFAULT_NAME_FORMAT = DEFAULT_NAME_TRANSFORMATION_FOLDER_FORMAT;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TransformationListener persistFile() {
        return new TransformationListener() {

            @Override
            public void postTransformation(TransformationRequest transformationRequest, TransformationResult transformationResult) {
                persist(transformationResult);
            }

            @Override
            public void postTransformationAbort(TransformationRequest transformationRequest, TransformationResult transformationResult) {
                persist(transformationResult);
            }

            private void persist(TransformationResult transformationResult) {
                String fileName = getFileName(transformationResult);
                File file = new File("results", fileName);

                try {
                    String transformationResultString = transformationResult.toJson();
                    FileUtils.writeStringToFile(file, transformationResultString, Charset.defaultCharset());
                    logger.info("Transformation result persisted in file {}", file.getAbsolutePath());
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.error("It was not possible to write transformation result to file", e);
                    } else {
                        logger.error("It was not possible to write transformation result to file. Run Butterfly in debug mode for further information.");
                    }
                }
            }

            private String getFileName(TransformationResult transformationResult) {
                String fileName;

                switch (DEFAULT_NAME_FORMAT) {
                    case DEFAULT_NAME_TRANSFORMATION_FOLDER_FORMAT:
                        String transformedAppPath = transformationResult.getTransformedApplicationDir().getAbsolutePath();
                        String name = new File(transformedAppPath).getName();
                        fileName = String.format("%s.json", name);
                        break;
                    case DEFAULT_NAME_APP_NAME_FORMAT:
                    default:
                        fileName = String.format("%s_%s.json", transformationResult.getApplicationName(), transformationResult.getTimestamp());
                        break;
                }

                return fileName;
            }
        };
    }

}