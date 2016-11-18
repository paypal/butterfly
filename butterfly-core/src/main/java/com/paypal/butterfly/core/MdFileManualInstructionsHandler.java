package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.utilities.ManualInstruction;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * This class processes all {@link com.paypal.butterfly.extensions.api.utilities.ManualInstruction}
 * objects present in a {@link TransformationContextImpl] object and persist them in a set of MD files,
 * to be placed in the transformed application folder
 *
 * @author facarvalho
 */
@Component
public class MdFileManualInstructionsHandler implements TransformationListener {

    private static final String MANUAL_INSTRUCTIONS_BASELINE_FILE = "MANUAL_INSTRUCTIONS_BASELINE.md";
    private static final String MANUAL_INSTRUCTIONS_FILE_NAME = "POST_TRANSFORMATION_MANUAL_INSTRUCTIONS.md";
    private static final String MANUAL_INSTRUCTIONS_DIR = "butterfly_manual_instructions";
    private static final String DESCRIPTION_LINE_FORMAT = "1. [%s](%s/%s)" + System.lineSeparator();
    private static final int BUFFER_SIZE = 1024;

    private static final Logger logger = LoggerFactory.getLogger(MdFileManualInstructionsHandler.class);

    @Override
    public void postTransformation(Transformation transformation, TransformationContextImpl transformationContext) {
        if (!transformationContext.hasManualInstructions()) {
            logger.debug("This transformation has no manual instructions");
            return;
        }

        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            File transformedAppFolder = transformation.getTransformedApplicationLocation();

            // Creating manual instructions directory
            File manualInstructionsDir = new File(transformedAppFolder, MANUAL_INSTRUCTIONS_DIR);
            if (!manualInstructionsDir.mkdir()) {
                logger.error("Manual instructions directory could not be created");
                return;
            }

            // Preparing manual instructions file to be written
            File manualInstructionsFile = new File(transformedAppFolder, MANUAL_INSTRUCTIONS_FILE_NAME);
            outputStream = new FileOutputStream(manualInstructionsFile);

            // Writing baseline content
            inputStream = getClass().getClassLoader().getResourceAsStream(MANUAL_INSTRUCTIONS_BASELINE_FILE);
            appendData(outputStream, inputStream);
            inputStream.close();

            String instructionDescription;
            File instructionFile;
            URL instructionResource;
            for (ManualInstruction manualInstruction : transformationContext.getManualInstructions()) {
                instructionDescription = manualInstruction.getDescription();
                instructionResource = manualInstruction.getResource();

                instructionFile = new File(manualInstructionsDir, getResourceFileName(instructionResource));
                FileUtils.copyURLToFile(instructionResource, instructionFile);

                addInstructionDescription(outputStream, instructionDescription, manualInstructionsDir, instructionFile);

                logger.debug("Manual instruction document {} generated", instructionFile.getName());
            }

            transformation.setManualInstructionsFile(manualInstructionsFile);

            logger.debug("Manual instructions documents have been generated");
        } catch (IOException e) {
            logger.error("Exception happened when generating manual instructions", e);
        } finally {
            if (outputStream != null) try {
                outputStream.close();
            } catch (IOException e) {
                logger.error("Exception happened when closing " + MANUAL_INSTRUCTIONS_FILE_NAME + " file", e);
            }
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                logger.error("Exception happened when closing " + MANUAL_INSTRUCTIONS_BASELINE_FILE + " file", e);
            }
        }
    }

    private String getResourceFileName(URL instructionResource) {
        String resourceName = instructionResource.getFile();
        int i = resourceName.lastIndexOf("/");
        return resourceName.substring(i + 1);
    }

    private void addInstructionDescription(OutputStream outputStream, String description, File manualInstructionsDir, File instructionFile) throws IOException {
        String descriptionLine = String.format(DESCRIPTION_LINE_FORMAT, description, manualInstructionsDir.getName(), instructionFile.getName());
        outputStream.write(descriptionLine.getBytes(Charset.defaultCharset()));
    }

    private void appendData(OutputStream outputStream, InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = null;

        try {
            bufferedInputStream = new BufferedInputStream(inputStream);

            byte[] buffer = new byte[BUFFER_SIZE];
            int available;

            while ((available = bufferedInputStream.available()) > 0) {
                if (available > BUFFER_SIZE) {
                    available = BUFFER_SIZE;
                }
                bufferedInputStream.read(buffer, 0, available);
                outputStream.write(buffer, 0, available);
            }
        } finally {
            if(inputStream != null) inputStream.close();
            if(bufferedInputStream != null) bufferedInputStream.close();
        }
    }

}
