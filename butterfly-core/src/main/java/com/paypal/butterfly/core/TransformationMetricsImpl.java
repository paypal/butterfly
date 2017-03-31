package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.metrics.AbortDetails;
import com.paypal.butterfly.extensions.api.metrics.TransformationMetrics;
import com.paypal.butterfly.extensions.api.metrics.TransformationStatistics;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.facade.ButterflyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * POJO containing statistics and meta-data about
 * the result of a transformation execution
 *
 * @author facarvalho
 */
public class TransformationMetricsImpl implements TransformationMetrics {

    private static final Logger logger = LoggerFactory.getLogger(TransformationMetricsImpl.class);

    private transient Transformation transformation;
    private transient TransformationContextImpl transformationContext;
    private transient TransformationTemplate transformationTemplate;

    private String butterflyVersion;
    private String templateName;
    private String dateTime;
    private long timestamp;
    private String userId;
    private String applicationType;
    private String applicationName;
    private String fromVersion;
    private String toVersion;
    private boolean requiresManualInstructions;
    private boolean successfulTransformation = false;
    private TransformationStatistics statistics;
    private String upgradeCorrelationId;
    private AbortDetails abortDetails;
    private String metricsId;
    private String originalApplicationLocation;
    private String transformedApplicationLocation;

    public TransformationMetricsImpl(Transformation transformation, TransformationContextImpl transformationContext) {
        setTransformation(transformation);
        setTransformationContext(transformationContext);
        setTransformationTemplate();
        setMetaData();
    }

    private void setTransformation(Transformation transformation) {
        if (transformation == null) {
            throw new IllegalArgumentException("transformation object cannot be null");
        }
        this.transformation = transformation;
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

        userId = System.getProperty("user.name");
        timestamp = runDateTime.getTime();
        dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(runDateTime);
        butterflyVersion = ButterflyProperties.getString("butterfly.version");
        templateName = transformationTemplate.getName();
        applicationType = transformationTemplate.getApplicationType();
        applicationName = transformationTemplate.getApplicationName();

        if (transformationTemplate instanceof UpgradeStep) {
            fromVersion = ((UpgradeStep) transformationTemplate).getCurrentVersion();
            toVersion = ((UpgradeStep) transformationTemplate).getNextVersion();
            upgradeCorrelationId = transformationContext.getUpgradeCorrelationId();
        }

        requiresManualInstructions = transformationContext.hasManualInstructions();
        successfulTransformation = transformationContext.isSuccessfulTransformation();
        statistics = transformationContext.getStatistics();
        abortDetails = transformationContext.getAbortDetails();
        originalApplicationLocation = transformation.getApplication().getFolder().getAbsolutePath();
        transformedApplicationLocation = transformation.getTransformedApplicationLocation().getAbsolutePath();

        metricsId =  UUID.randomUUID().toString();

        if (logger.isDebugEnabled()) {
            logger.debug("Metrics generated: {}", metricsId);
        }
    }

    @Override
    public String getButterflyVersion() {
        return butterflyVersion;
    }

    @Override
    public String getTemplateName() {
        return templateName;
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
    public String getUserId() {
        return userId;
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
    public String getFromVersion() {
        return fromVersion;
    }

    @Override
    public String getToVersion() {
        return toVersion;
    }

    @Override
    public boolean isRequiresManualInstructions() {
        return requiresManualInstructions;
    }

    @Override
    public boolean isSuccessfulTransformation() {
        return successfulTransformation;
    }

    @Override
    public TransformationStatistics getStatistics() {
        return statistics;
    }

    @Override
    public String getUpgradeCorrelationId() {
        return upgradeCorrelationId;
    }

    @Override
    public String getMetricsId() {
        return metricsId;
    }

    @Override
    public AbortDetails getAbortDetails() {
        return abortDetails;
    }

    @Override
    public String getOriginalApplicationLocation() {
        return originalApplicationLocation;
    }

    @Override
    public String getTransformedApplicationLocation() {
        return transformedApplicationLocation;
    }

}
