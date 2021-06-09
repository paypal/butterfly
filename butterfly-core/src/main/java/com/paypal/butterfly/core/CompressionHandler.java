package com.paypal.butterfly.core;

import com.paypal.butterfly.api.TransformationResult;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * This bean takes care of compressing the output folder,
 * which results in a zip file
 *
 * @author facarvalho, matcurtis
 */
@Component
class CompressionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CompressionHandler.class);

    void compress(TransformationResult transformationResult) {
        File inputFile = transformationResult.getTransformedApplicationDir().getAbsoluteFile();
        File compressedFile = new File(transformationResult.getTransformedApplicationDir().getAbsolutePath() + ".zip");

        logger.info("Compressing transformed application");

        try {
            ZipFile zipFile = new ZipFile(compressedFile);
            ZipParameters parameters = new ZipParameters();

            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            parameters.setCompressionLevel(CompressionLevel.NORMAL);

            zipFile.addFolder(inputFile, parameters);
            FileUtils.deleteDirectory(transformationResult.getTransformedApplicationDir());

            logger.info("Transformed application has been compressed to {}", compressedFile.getAbsoluteFile());
        } catch (Exception e) {
            logger.error("An exception happened when compressing transformed application", e);
        }
    }

}
