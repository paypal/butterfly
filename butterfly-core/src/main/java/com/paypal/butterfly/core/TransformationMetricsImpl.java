package com.paypal.butterfly.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.api.TransformationMetrics;
import com.paypal.butterfly.api.TransformationStatistics;

/**
 * POJO containing statistics and meta-data about
 * the result of a transformation template execution
 *
 * @author facarvalho
 */
class TransformationMetricsImpl implements TransformationMetrics {

    private transient TransformationContextImpl transformationContext;
    private transient TransformationTemplate transformationTemplate;

    private String templateName;
    private String templateClassName;
    private String dateTime;
    private long timestamp;
    private String fromVersion;
    private String toVersion;
    private boolean hasManualInstructions;
    private boolean successful = false;
    private TransformationStatistics statistics;

    TransformationMetricsImpl(TransformationContextImpl transformationContext) {
        setTransformationContext(transformationContext);
        setTransformationTemplate();
        setMetaData();
    }

    private void setTransformationContext(TransformationContextImpl transformationContext) {
        if (transformationContext == null) {
            throw new IllegalArgumentException("transformation context object cannot be null");
        }
        this.transformationContext = transformationContext;
    }

    private void setTransformationTemplate() {
        this.transformationTemplate = transformationContext.getTransformationTemplate();
        if (transformationTemplate == null) {
            throw new IllegalArgumentException("transformation template object cannot be null");
        }
    }

    private void setMetaData() {
        Date runDateTime = new Date();

        timestamp = runDateTime.getTime();
        dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(runDateTime);
        templateName = transformationTemplate.getName();
        templateClassName = transformationTemplate.getClass().getName();

        if (transformationTemplate instanceof UpgradeStep) {
            fromVersion = ((UpgradeStep) transformationTemplate).getCurrentVersion();
            toVersion = ((UpgradeStep) transformationTemplate).getNextVersion();
        }

        hasManualInstructions = transformationContext.hasManualInstructions();
        successful = transformationContext.isSuccessfulTransformation();
        statistics = transformationContext.getStatistics();
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public String getTemplateClassName() {
        return templateClassName;
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
    public String getFromVersion() {
        return fromVersion;
    }

    @Override
    public String getToVersion() {
        return toVersion;
    }

    @Override
    public boolean hasManualInstructions() {
        return hasManualInstructions;
    }

    @Override
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public TransformationStatistics getStatistics() {
        return statistics;
    }

}
