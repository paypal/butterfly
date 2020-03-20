package com.paypal.butterfly.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paypal.butterfly.api.TransformationResult;

@Component
class ManualInstructionsHandler {

    private static final Logger logger = LoggerFactory.getLogger(ManualInstructionsHandler.class);

    private static final String MANUAL_INSTRUCTIONS_DIR = "BUTTERFLY_MANUAL_INSTRUCTIONS";
    static final String MANUAL_INSTRUCTIONS_MAIN_FILE = "BUTTERFLY_MANUAL_INSTRUCTIONS.md";

    @Autowired
    private ManualInstructionsWriter manualInstructionsWriter;

    void processManualInstructions(TransformationResult transformationResult, List<TransformationContextImpl> transformationContexts) {
        if (!hasManualInstructions(transformationContexts)) {
            logger.debug("There are no post-transformation manual instructions to be processed");
            return;
        }

        try {
            setupManualInstructions(transformationResult);
            manualInstructionsWriter.writeManualInstructions(transformationResult, transformationContexts);
        } catch (Exception e) {
            throw new InternalException("An error happened when writing post-transformation manual instruction documents", e);
        }
    }

    private boolean hasManualInstructions(List<TransformationContextImpl> transformationContexts) {
        if (transformationContexts.size() == 0) {
            logger.debug("There are no transformation contexts to be processed");
            return false;
        }
        for (TransformationContextImpl transformationContext : transformationContexts) {
            if (transformationContext.hasManualInstructions()) {
                return true;
            }
        }
        return false;
    }

    private void setupManualInstructions(TransformationResult transformationResult) throws IOException {

        // Creating empty manual instructions directory
        File transformedAppFolder = transformationResult.getTransformedApplicationDir();
        File manualInstructionsDir = new File(transformedAppFolder, MANUAL_INSTRUCTIONS_DIR);
        if (!manualInstructionsDir.mkdir()) {
            throw new IOException("Manual instructions directory " + manualInstructionsDir + " could not be created");
        }

        // Creating empty manual instructions baseline file
        File manualInstructionsFile = new File(transformedAppFolder, MANUAL_INSTRUCTIONS_MAIN_FILE);
        manualInstructionsFile.createNewFile();

        // Setting manual instructions file and dir into transformation result object
        ((TransformationResultImpl) transformationResult).setManualInstructionsFile(manualInstructionsFile);
        ((TransformationResultImpl) transformationResult).setManualInstructionsDir(manualInstructionsDir);

        logger.debug("Baseline manual instruction file has been created");
    }

}
