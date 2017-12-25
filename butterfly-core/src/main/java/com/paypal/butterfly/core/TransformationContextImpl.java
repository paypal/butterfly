package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.PerformResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.metrics.AbortDetails;
import com.paypal.butterfly.extensions.api.metrics.TransformationStatistics;
import com.paypal.butterfly.extensions.api.utilities.ManualInstructionRecord;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Transformation context implementation
 *
 * @see {@link TransformationContext}
 *
 * @author facarvalho
 */
@SuppressWarnings("PMD.DefaultPackage")
@SuppressFBWarnings("URF_UNREAD_FIELD")
class TransformationContextImpl implements TransformationContext {

    private TransformationTemplate transformationTemplate;
    private Map<String, Object> attributes = new HashMap<>();
    private Map<String, PerformResult> results = new HashMap<>();
    private List<ManualInstructionRecord> manualInstructionRecords = new ArrayList<>();
    private boolean successfulTransformation = true;
    private boolean collectStats = false;
    private TransformationStatisticsImpl statistics;
    private String upgradeCorrelationId;
    private AbortDetails abortDetails;

    void setCollectStats(boolean collectStats) {
        this.collectStats = collectStats;
        if (collectStats) {
            statistics = new TransformationStatisticsImpl();
        }
    }

    void setTransformationTemplate(TransformationTemplate transformationTemplate) {
        if(transformationTemplate == null) {
            throw new IllegalArgumentException("Transformation template object cannot be null");
        }
        this.transformationTemplate = transformationTemplate;
        if (upgradeCorrelationId == null) {
            upgradeCorrelationId = String.format("%s_%s", transformationTemplate.getExtensionClass().getSimpleName(), UUID.randomUUID().toString());
        }
    }

    @Override
    public Object get(String name) {
        if(StringUtils.isBlank(name)) {
            // TODO
            // Replace this by a better exception type.
            // TransformationContextException could be a good one, however, it is a checked exception, we definitely need a runtime exception here.
            throw new IllegalArgumentException("Transformation context attribute key cannot be null");
        }
        return attributes.get(name);
    }

    @Override
    public PerformResult getResult(String utilityName) {
        if(StringUtils.isBlank(utilityName)) {
            throw new IllegalArgumentException("Result key cannot be null");
        }
        return results.get(utilityName);
    }

    /**
     * Puts a new transformation context attribute, using its name as key.
     * If another attribute had already been associated with same key,
     * it is replaced by the new one.
     *
     * @param name the transformation context attribute name
     * @param attributeObject the attribute object
     */
    void put(String name, Object attributeObject) {
// TODO
//        if(StringUtils.isBlank(name) || attributeObject == null || (attributeObject instanceof String && StringUtils.isBlank((String) attributeObject))) {
//            // TODO
//            // Replace this by a better exception type.
//             // TransformationContextException could be a good one, however, it is a checked exception, we definitely need a runtime exception here.
//            throw new IllegalArgumentException("Transformation context attribute name and object cannot be null nor blank");
//        }
        attributes.put(name, attributeObject);
    }

    /**
     * Puts a new transformation utility result, using its name as key.
     * If another result had already been associated with same key,
     * it is replaced by the new one.
     *
     * @param name the transformation utility name
     * @param resultObject the result object
     */
    void putResult(String name, PerformResult resultObject) {
        if(StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Result name cannot be null nor blank");
        }
        if(resultObject == null) {
            throw new IllegalArgumentException("Result object is null for key " + name);
        }
        results.put(name, resultObject);

        if (collectStats) {
            statistics.registerResult(resultObject);
        }
    }

    /**
     * Adds a new manual instruction object to the list. In the end of the transformation
     *
     * @param manualInstructionRecord the manual instruction record to be added to the list
     */
    void registerManualInstruction(ManualInstructionRecord manualInstructionRecord) {
        if(manualInstructionRecord == null) {
            throw new IllegalArgumentException("Manual instruction record object cannot be null");
        }
        manualInstructionRecords.add(manualInstructionRecord);

        if (collectStats) {
            statistics.addManualInstruction();
        }
    }

    /**
     * Returns the {@link TransformationTemplate} object
     * whose execution originated this context object
     *
     * @return the {@link TransformationTemplate} object
     * whose execution originated this context object
     */
    TransformationTemplate getTransformationTemplate() {
        return transformationTemplate;
    }

    /**
     * Returns true if there are manual instructions
     * registered to this transformation context
     *
     * @return true if there are manual instructions
     * registered to this transformation context
     */
    boolean hasManualInstructions() {
        return manualInstructionRecords.size() > 0;
    }

    List<ManualInstructionRecord> getManualInstructionRecords() {
        return manualInstructionRecords;
    }

    boolean isSuccessfulTransformation() {
        return successfulTransformation;
    }

    TransformationStatistics getStatistics() {
        return statistics;
    }

    /**
     * This is a factory method for transformation context objects.
     * A transformation context instance can be created from scratch,
     * or it can be created based on a previous context
     *
     * @param previousTransformationContext the previous context object, which can lend some
     *                                      characteristics for the new one
     * @return the new transformation context object
     */
    static TransformationContextImpl getTransformationContext(TransformationContextImpl previousTransformationContext) {
        TransformationContextImpl context = new TransformationContextImpl();
        if (previousTransformationContext != null) {
            context.upgradeCorrelationId = previousTransformationContext.upgradeCorrelationId;
        }
        context.setCollectStats(true);
        return context;
    }

    String getUpgradeCorrelationId() {
        return upgradeCorrelationId;
    }

    void transformationAborted(Exception ex, String abortMessage, String utilityName) {
        successfulTransformation = false;
        abortDetails = new AbortDetails(ex, abortMessage, utilityName);
    }

    AbortDetails getAbortDetails() {
        return abortDetails;
    }

}
