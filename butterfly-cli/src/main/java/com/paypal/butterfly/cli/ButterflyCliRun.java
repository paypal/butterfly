package com.paypal.butterfly.cli;

import java.io.File;
import java.util.Arrays;

/**
 * This is just a POJO that represents an execution of
 * Butterfly. Its purpose is to facilitate printing a
 * JSON file representing the output, or result, of
 * Butterfly run via this CLI
 *
 * @author facarvalho
 */
public class ButterflyCliRun {

    private String butterflyVersion;

    private String[] inputArguments;

    private String application;

    private String transformationTemplate;

    private int exitStatus;

    private String transformedApplication;

    private String logFile;

    private String manualInstructionsFile;

    // TODO
    // Metrics are not first class citizen yet
    // They work as an opt-in feature
    private String metricsFile;

    private String errorMessage;

    private String exceptionMessage;

    public void setButterflyVersion(String butterflyVersion) {
        this.butterflyVersion = butterflyVersion;
    }

    public void setInputArguments(String[] inputArguments) {
        this.inputArguments = Arrays.copyOf(inputArguments, inputArguments.length);
    }

    public void setApplication(File application) {
        this.application = application.getAbsolutePath();
    }

    public void setTransformationTemplate(String transformationTemplate) {
        this.transformationTemplate = transformationTemplate;
    }

    public void setExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
    }

    public void setTransformedApplication(File transformedApplication) {
        this.transformedApplication = transformedApplication.getAbsolutePath();
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile.getAbsolutePath();
    }

    public void setManualInstructionsFile(File manualInstructionsFile) {
        this.manualInstructionsFile = manualInstructionsFile.getAbsolutePath();
    }

    public void setMetricsFile(File metricsFile) {
        this.metricsFile = metricsFile.getAbsolutePath();
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public int getExitStatus() {
        return exitStatus;
    }

    public String getButterflyVersion() {
        return butterflyVersion;
    }

    public String[] getInputArguments() {
        return Arrays.copyOf(inputArguments, inputArguments.length);
    }

    public String getApplication() {
        return application;
    }

    public String getTransformationTemplate() {
        return transformationTemplate;
    }

    public String getTransformedApplication() {
        return transformedApplication;
    }

    public String getLogFile() {
        return logFile;
    }

    public String getManualInstructionsFile() {
        return manualInstructionsFile;
    }

    public String getMetricsFile() {
        return metricsFile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
