package com.paypal.butterfly.core;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.paypal.butterfly.api.TransformationRequest;
import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.api.AbortDetails;
import com.paypal.butterfly.api.TransformationMetrics;

/**
 * The transformation result implementation
 *
 * @author facarvalho
 */
class TransformationResultImpl implements TransformationResult {

    private transient static final Logger logger = LoggerFactory.getLogger(TransformationResultImpl.class);
    private transient static Gson gson;

    private String id;
    private TransformationRequest transformationRequest;
    private final String userId;
    private final long timestamp;
    private final String dateTime;
    private String applicationType;
    private String applicationName;
    private boolean successful = true;
    private File transformedApplicationDir;
    private int upgradeStepsCount;
    private boolean hasManualInstructions = false;
    private int manualInstructionsTotal;
    private File manualInstructionsDir;
    private File manualInstructionsFile;
    private List<TransformationMetrics> transformationMetrics;
    private transient Map<String, TransformationMetrics> transformationMetricsMap;
    private AbortDetails abortDetails;

    TransformationResultImpl(TransformationRequest transformationRequest, File transformedApplicationDir) {
        id = UUID.randomUUID().toString();
        if (logger.isDebugEnabled()) {
            logger.debug("Transformation result id: {}", id);
        }

        userId = System.getProperty("user.name");

        Date date = new Date();
        timestamp = date.getTime();
        dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

        setTransformationRequest(transformationRequest);
        setTransformedApplicationDir(transformedApplicationDir);
    }

    private void setTransformationRequest(TransformationRequest transformationRequest) {
        if (transformationRequest == null) {
            throw new IllegalArgumentException("Transformation request object cannot be null");
        }
        this.transformationRequest = transformationRequest;
    }

    private void setTransformedApplicationDir(File transformedApplicationDir) {
        if (transformedApplicationDir == null) {
            throw new IllegalArgumentException("Transformed application directory object cannot be null");
        }
        this.transformedApplicationDir = transformedApplicationDir;
    }

    // This can only be set after metrics is set
    private void setUpgradeStepsCount() {
        if (transformationRequest.isUpgradeStep()) {
            upgradeStepsCount = transformationMetrics.size();
        } else {
            upgradeStepsCount = 0;
        }
    }

    // TODO
    // Figure out an easy way for templates to set this
    TransformationResultImpl setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    // TODO
    // Figure out an easy way for templates to set this
    TransformationResultImpl setApplicationType(String applicationType) {
        this.applicationType = applicationType;
        return this;
    }

    /*
     * This sets the metrics list, set and also total of manual instructions, since that is calculated based on the metrics list
     */
    TransformationResultImpl setTransformationMetrics(List<TransformationMetrics> transformationMetrics) {
        if (transformationMetrics == null || transformationMetrics.isEmpty()) {
            throw new IllegalArgumentException("Transformed metrics list object cannot be null nor empty");
        }
        this.transformationMetrics = Collections.unmodifiableList(transformationMetrics);
        transformationMetricsMap = Collections.unmodifiableMap(transformationMetrics.stream().collect(Collectors.toMap(TransformationMetrics::getTemplateClassName, m -> m)));
        manualInstructionsTotal = transformationMetrics.stream().filter(m -> m.hasManualInstructions()).mapToInt(m -> m.getStatistics().getManualInstructionsCount()).sum();

        // This can only be set after metrics is set
        setUpgradeStepsCount();

        return this;
    }

    TransformationResultImpl setManualInstructionsDir(File manualInstructionsDir) {
        hasManualInstructions = true;
        this.manualInstructionsDir = manualInstructionsDir;
        return this;
    }

    TransformationResultImpl setManualInstructionsFile(File manualInstructionsFile) {
        this.manualInstructionsFile = manualInstructionsFile;
        return this;
    }

    TransformationResultImpl setAbortDetails(AbortDetails abortDetails) {
        this.abortDetails = abortDetails;
        successful = false;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TransformationRequest getTransformationRequest() {
        return transformationRequest;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getDateTime() {
        return dateTime;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getApplicationType() {
        return applicationType;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public File getTransformedApplicationDir() {
        return transformedApplicationDir;
    }

    @Override
    public int getUpgradeStepsCount() {
        return upgradeStepsCount;
    }

    @Override
    public boolean hasManualInstructions() {
        return hasManualInstructions;
    }

    @Override
    public int getManualInstructionsTotal() {
        return manualInstructionsTotal;
    }

    @Override
    public File getManualInstructionsDir() {
        return manualInstructionsDir;
    }

    @Override
    public File getManualInstructionsFile() {
        return manualInstructionsFile;
    }

    @Override
    public List<TransformationMetrics> getMetrics() {
        return transformationMetrics;
    }

    @Override
    public Map<String, TransformationMetrics> getMetricsMap() {
        return transformationMetricsMap;
    }

    @Override
    public AbortDetails getAbortDetails() {
        return abortDetails;
    }

    @Override
    public String toJson() {
        if (gson == null) {
            gson = new GsonBuilder().serializeNulls().setPrettyPrinting().registerTypeAdapter(File.class, new TypeAdapter<File>() {
                @Override
                public void write(JsonWriter jsonWriter, File file) throws IOException {
                    String fileAbsolutePath = (file == null ? null : file.getAbsolutePath());
                    jsonWriter.value(fileAbsolutePath);
                }
                @Override
                public File read(JsonReader jsonReader) {
                    throw new UnsupportedOperationException("There is no support for deserializing transformation result objects at the moment");
                }
            }).create();
        }
        return gson.toJson(this);
    }

}
