package com.paypal.butterfly.cli;

import java.io.File;
import java.util.Arrays;

import com.paypal.butterfly.api.TransformationResult;

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
    private int exitStatus;
    private File logFile;
    private String errorMessage;
    private String exceptionMessage;
    private TransformationResult transformationResult;

    void setButterflyVersion(String butterflyVersion) {
        this.butterflyVersion = butterflyVersion;
    }

    void setInputArguments(String[] inputArguments) {
        this.inputArguments = Arrays.copyOf(inputArguments, inputArguments.length);
    }

    void setExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
    }

    void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    void setTransformationResult(TransformationResult transformationResult) {
        this.transformationResult = transformationResult;
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

    public File getLogFile() {
        return logFile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public TransformationResult getTransformationResult() {
        return transformationResult;
    }

}
