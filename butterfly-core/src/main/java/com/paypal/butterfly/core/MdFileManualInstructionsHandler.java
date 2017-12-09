package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.extensions.api.utilities.ManualInstructionRecord;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This class processes all {@link com.paypal.butterfly.extensions.api.utilities.ManualInstruction}
 * objects present in a {@link TransformationContextImpl} object and persist them in a set of MD files,
 * to be placed in the transformed application folder
 *
 * @author facarvalho
 */
@Component
public class MdFileManualInstructionsHandler implements TransformationListener {

    private static final String MANUAL_INSTRUCTIONS_MAIN_FILE = "MANUAL_INSTRUCTIONS_%s.md";
    private static final String MANUAL_INSTRUCTIONS_BASELINE_FILE = "MANUAL_INSTRUCTIONS_BASELINE.md";
    private static final String MANUAL_INSTRUCTIONS_DIR = "BUTTERFLY_MANUAL_INSTRUCTIONS";
    private static final String SECTION_TITLE_FORMAT = "%n### Manual instructions upgrading to version %s%n%n";
    private static final String DESCRIPTION_LINE_FORMAT = "1. [%s](%s/%s)%n";

    private static final Logger logger = LoggerFactory.getLogger(MdFileManualInstructionsHandler.class);

    @Override
    public void postTransformation(Transformation transformation, List<TransformationContextImpl> transformationContexts) {
        if (!hasManualInstructions(transformationContexts)) {
            return;
        }

        try {
            File mainManualInstructionsFile = createMainManualInstructionsFile(transformation);

            // Here the idea is to differentiate between a transformation made of one single transformation template,
            // and one made of upgrade steps, because we want to print a section title per upgrade
            // steps in the main instructions file, in case of upgrade paths. By the way,
            // this information could be held at the Transformation object actually (potential future improvement)

            if (transformationContexts.size() == 1) {
                createManualInstrutionDocument(transformation, transformationContexts.get(0));
            } else {
                for (TransformationContextImpl transformationContext : transformationContexts) {
                    if (transformationContext.hasManualInstructions()) {
                        addSectionTitle(mainManualInstructionsFile, transformationContext.getTransformationTemplate());
                        File manualInstrutionDocumentFolder = createManualInstrutionDocumentFolder(transformation, transformationContext);
                        createManualInstrutionDocument(transformation, transformationContext, manualInstrutionDocumentFolder);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Exception happened when generating manual instructions", e);
        }
    }

    @Override
    public void postTransformationAbort(Transformation transformation, List<TransformationContextImpl> transformationContexts) {
        // Nothing to be done here
    }

    private File createManualInstrutionDocumentFolder(Transformation transformation, TransformationContextImpl transformationContext) throws IOException {
        UpgradeStep upgradeStep = (UpgradeStep) transformationContext.getTransformationTemplate();
        File manualInstrutionDocumentFolder = new File(transformation.getManualInstructionsDir(), upgradeStep.getClass().getSimpleName());
        if (!manualInstrutionDocumentFolder.mkdir()) {
            throw new IOException("Manual instructions directory " + manualInstrutionDocumentFolder + " could not be created");
        }
        return manualInstrutionDocumentFolder;
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

    private File createMainManualInstructionsFile(Transformation transformation) throws IOException {
        try {
            final String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

            // Creating manual instructions directory
            File transformedAppFolder = transformation.getTransformedApplicationLocation();
            String manualInstructiondDirName = String.format("%s_%s", MANUAL_INSTRUCTIONS_DIR, timestamp);
            File manualInstructionsDir = new File(transformedAppFolder, manualInstructiondDirName);
            if (!manualInstructionsDir.mkdir()) {
                throw new IOException("Manual instructions directory " + manualInstructionsDir + " could not be created");
            }

            // Creating manual instructions baseline file
            String manualInstructiondFileName = String.format(MANUAL_INSTRUCTIONS_MAIN_FILE, timestamp);
            File manualInstructionsFile = new File(transformedAppFolder, manualInstructiondFileName);
            URL baselineFileURL = getClass().getClassLoader().getResource(MANUAL_INSTRUCTIONS_BASELINE_FILE);
            FileUtils.copyURLToFile(baselineFileURL, manualInstructionsFile);

            transformation.setManualInstructionsDir(manualInstructionsDir);
            transformation.setManualInstructionsFile(manualInstructionsFile);

            logger.debug("Baseline manual instruction file has been created");

            return manualInstructionsFile;
        } catch (IOException e) {
            throw new IOException("Exception happened when creating baseline manual instruction file", e);
        }
    }

    private void addSectionTitle(File mainManualInstructionsFile, TransformationTemplate transformationTemplate) throws IOException {
        UpgradeStep upgradeStep = (UpgradeStep) transformationTemplate;
        String sectionTile = String.format(SECTION_TITLE_FORMAT, upgradeStep.getNextVersion());
        org.codehaus.plexus.util.FileUtils.fileAppend(mainManualInstructionsFile.getAbsolutePath(), sectionTile);
    }

    private void createManualInstrutionDocument(Transformation transformation, TransformationContextImpl transformationContext) throws IOException {
        createManualInstrutionDocument(transformation, transformationContext, null);
    }

    private void createManualInstrutionDocument(Transformation transformation, TransformationContextImpl transformationContext, File manualInstrutionDocumentFolder) throws IOException {
        File manualInstructionsDir = transformation.getManualInstructionsDir();

        String instructionDescription;
        File instructionFile;
        URL instructionResource;

        for (ManualInstructionRecord manualInstructionRecord : transformationContext.getManualInstructionRecords()) {
            instructionDescription = manualInstructionRecord.getDescription();
            instructionResource = manualInstructionRecord.getResource();

            if (manualInstrutionDocumentFolder == null) {
                instructionFile = new File(manualInstructionsDir, getResourceFileName(instructionResource));
            } else {
                instructionFile = new File(manualInstrutionDocumentFolder, getResourceFileName(instructionResource));
            }
            FileUtils.copyURLToFile(instructionResource, instructionFile);
            addInstructionDescription(transformation.getManualInstructionsFile(), instructionDescription, manualInstructionsDir, instructionFile);

            logger.debug("Manual instruction document generated: {}", instructionFile.getAbsolutePath());
        }
    }

    private String getResourceFileName(URL instructionResource) {
        String resourceName = instructionResource.getFile();
        int i = resourceName.lastIndexOf("/");
        return resourceName.substring(i + 1);
    }

    private void addInstructionDescription(File manualInstructionsFile, String description, File manualInstructionsDir, File instructionFile) throws IOException {
        String filePath = "";
        if (!instructionFile.getParentFile().equals(manualInstructionsDir)) {
            filePath = instructionFile.getParentFile().getName() + "/";
        }
        filePath = filePath.concat(instructionFile.getName());

        String descriptionLine = String.format(DESCRIPTION_LINE_FORMAT, description, manualInstructionsDir.getName(), filePath);
        org.codehaus.plexus.util.FileUtils.fileAppend(manualInstructionsFile.getAbsolutePath(), descriptionLine);
    }

}
