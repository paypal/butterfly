package com.paypal.butterfly.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.paypal.butterfly.api.TransformationResult;

/**
 * This is just a POJO that represents an execution of
 * Butterfly. Its purpose is to facilitate printing a
 * JSON file representing the output, or result, of
 * Butterfly run via this CLI
 *
 * @author facarvalho
 */
class ButterflyCliRun {

    private String butterflyVersion;
    private String[] inputArguments;
    private int exitStatus;
    private File logFile;
    private String errorMessage;
    private String exceptionMessage;
    private TransformationResult transformationResult;
    private List<ExtensionMetaData> extensions = new ArrayList<>();

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

    void addExtensionMetaData(ExtensionMetaData extensionMetaData) {
        extensions.add(extensionMetaData);
    }

    int getExitStatus() {
        return exitStatus;
    }

    String getButterflyVersion() {
        return butterflyVersion;
    }

    String[] getInputArguments() {
        return Arrays.copyOf(inputArguments, inputArguments.length);
    }

    File getLogFile() {
        return logFile;
    }

    String getErrorMessage() {
        return errorMessage;
    }

    String getExceptionMessage() {
        return exceptionMessage;
    }

    TransformationResult getTransformationResult() {
        return transformationResult;
    }

    List<ExtensionMetaData> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

}
