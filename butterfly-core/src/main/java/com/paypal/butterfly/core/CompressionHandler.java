package com.paypal.butterfly.core;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
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
public class CompressionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CompressionHandler.class);

    public void compress(Transformation transformation) {
        File inputFile = transformation.getTransformedApplicationLocation().getAbsoluteFile();
        File compressedFile = new File(transformation.getTransformedApplicationLocation().getAbsolutePath() + ".zip");

        logger.info("Compressing transformed application");

        try {
            ZipFile zipFile = new ZipFile(compressedFile);
            ZipParameters parameters = new ZipParameters();

            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            zipFile.addFolder(inputFile, parameters);
            FileUtils.deleteDirectory(transformation.getTransformedApplicationLocation());

            logger.info("Transformed application has been compressed to {}", compressedFile.getAbsoluteFile());
        } catch (Exception e) {
            logger.error("Error when compressing transformed application", e);
        }
    }

}
