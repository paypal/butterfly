package com.paypal.butterfly.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.extensions.api.utilities.ManualInstructionRecord;

/**
 * This class processes all {@link com.paypal.butterfly.extensions.api.utilities.ManualInstruction}
 * objects present in a {@link TransformationContextImpl} object and persist them in a set of GitHub MD files,
 * to be placed in the transformed application folder
 *
 * @author facarvalho
 */
@Component
class GitHubMdFileManualInstructionsWriter implements ManualInstructionsWriter {

    private static final String MANUAL_INSTRUCTIONS_BASELINE_FILE = "MANUAL_INSTRUCTIONS_GITHUB_MD_BASELINE.md";
    private static final String SECTION_TITLE_FORMAT = "%n### Manual instructions upgrading to version %s%n%n";
    private static final String DESCRIPTION_LINE_FORMAT = "1. [%s](%s/%s)%n";

    private static final Logger logger = LoggerFactory.getLogger(GitHubMdFileManualInstructionsWriter.class);

    @Override
    public void writeManualInstructions(TransformationResult transformationResult, List<TransformationContextImpl> transformationContexts) throws InternalTransformationException {
        if (!transformationResult.hasManualInstructions()) {
            throw new IllegalStateException("Transformation result does not have post-transformation manual instructions");
        }

        try {
            File mainManualInstructionsFile = transformationResult.getManualInstructionsFile();

            writeMainManualInstructionsFile(mainManualInstructionsFile);

            // Here the idea is to differentiate between a transformation made of one single transformation template,
            // and one made of upgrade steps, because we want to print a section title per upgrade
            // steps in the main instructions file, in case of upgrade paths. By the way,

            if (transformationResult.getTransformationRequest().isUpgradeStep()) {
                for (TransformationContextImpl transformationContext : transformationContexts) {
                    if (transformationContext.hasManualInstructions()) {
                        addSectionTitle(mainManualInstructionsFile, transformationContext.getTransformationTemplate());
                        File manualInstructionDocumentFolder = createManualInstructionDocumentFolder(transformationResult, transformationContext);
                        createManualInstructionDocument(transformationResult, transformationContext, manualInstructionDocumentFolder);
                    }
                }
            } else {
                createManualInstructionDocument(transformationResult, transformationContexts.get(0));
            }
        } catch (IOException e) {
            throw new InternalTransformationException("Exception happened when generating manual instructions", e);
        }
    }

    private void writeMainManualInstructionsFile(File mainManualInstructionsFile) throws IOException {
        URL baselineFileURL = getClass().getClassLoader().getResource(MANUAL_INSTRUCTIONS_BASELINE_FILE);
        if (baselineFileURL == null) {
            throw new IllegalStateException("File " + MANUAL_INSTRUCTIONS_BASELINE_FILE + " could not be found");
        }
        FileUtils.copyURLToFile(baselineFileURL, mainManualInstructionsFile);
    }

    private File createManualInstructionDocumentFolder(TransformationResult transformationResult, TransformationContextImpl transformationContext) throws IOException {
        UpgradeStep upgradeStep = (UpgradeStep) transformationContext.getTransformationTemplate();
        File manualInstructionDocumentFolder = new File(transformationResult.getManualInstructionsDir(), upgradeStep.getClass().getSimpleName());
        if (!manualInstructionDocumentFolder.mkdir()) {
            throw new IOException("Manual instructions directory " + manualInstructionDocumentFolder + " could not be created");
        }
        return manualInstructionDocumentFolder;
    }

    private void addSectionTitle(File mainManualInstructionsFile, TransformationTemplate transformationTemplate) throws IOException {
        UpgradeStep upgradeStep = (UpgradeStep) transformationTemplate;
        String sectionTile = String.format(SECTION_TITLE_FORMAT, upgradeStep.getNextVersion());
        org.codehaus.plexus.util.FileUtils.fileAppend(mainManualInstructionsFile.getAbsolutePath(), sectionTile);
    }

    private void createManualInstructionDocument(TransformationResult transformationResult, TransformationContextImpl transformationContext) throws IOException {
        createManualInstructionDocument(transformationResult, transformationContext, null);
    }

    private void createManualInstructionDocument(TransformationResult transformationResult, TransformationContextImpl transformationContext, File manualInstrutionDocumentFolder) throws IOException {
        File manualInstructionsDir = transformationResult.getManualInstructionsDir();

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
            addInstructionDescription(transformationResult.getManualInstructionsFile(), instructionDescription, manualInstructionsDir, instructionFile);

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
