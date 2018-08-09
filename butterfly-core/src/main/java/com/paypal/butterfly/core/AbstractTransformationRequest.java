package com.paypal.butterfly.core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.api.Application;
import com.paypal.butterfly.api.Configuration;
import com.paypal.butterfly.api.TransformationRequest;

/**
 * Represents an specific transformation to be applied
 * against a specific application
 *
 * @author facarvalho
 */
@SuppressWarnings("PMD.DefaultPackage")
abstract class AbstractTransformationRequest implements TransformationRequest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTransformationRequest.class);
    private static final String BUTTERFLY_VERSION = ButterflyProperties.getString("butterfly.version");

    // This has to be added as an instance variable, as opposed to relying on the constant directly only,
    // because otherwise it won't be persisted (for example, as JSON in CouchDB or a file)
    private final String butterflyVersion = BUTTERFLY_VERSION;

    private final long timestamp;
    private final String dateTime;

    // Application to be transformed
    private Application application;

    // Butterfly configuration object specific to this transformation
    private Configuration configuration;

    // The location where the baseline application code is
    // The baseline application is a copy of the original application, used only for
    // blank transformations
    // Notice this instance variable is used internally only, hidden from the API consumer
    private transient File baselineApplicationDir = null;

    // Set by subclass
    protected String extensionName;
    protected String extensionVersion;
    protected String templateName;
    protected String templateClassName;
    protected boolean upgradeStep;

    // Whether the transformation template set in this transformation object
    // is a "blank transformation" or not
    private boolean blank;

    AbstractTransformationRequest(Application application, Configuration configuration, boolean blank) {
        Date date = new Date();
        timestamp = date.getTime();
        dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

        this.application = application;
        this.configuration = configuration;
        setBlank(blank);
    }

    private void setBlank(boolean blank) {
        this.blank = blank;
        if (blank) {
            if (configuration.isModifyOriginalFolder()) {
                baselineApplicationDir = Files.createTempDir();
                logger.debug("Baseline directory pointing to temporary directory: {}", baselineApplicationDir.getAbsolutePath());
            } else {
                baselineApplicationDir = application.getFolder();
                logger.debug("Baseline directory pointing to original application folder: {}", baselineApplicationDir.getAbsolutePath());
            }
        }
    }

    @Override
    public String getButterflyVersion() {
        return butterflyVersion;
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
    public Application getApplication() {
        return application;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public String getExtensionName() {
        return extensionName;
    }

    @Override
    public String getExtensionVersion() {
        return extensionVersion;
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
    public boolean isUpgradeStep() {
        return upgradeStep;
    }

    @Override
    public boolean isBlank() {
        return blank;
    }

    File getBaselineApplicationDir() {
        return baselineApplicationDir;
    }

    protected String getExtensionName(Class<? extends Extension> extension) {
        return extension.getName();
    }

    protected String getExtensionVersion(Class<? extends Extension> extension) {
        String version = "UNKNOWN";
        try {
            version = extension.newInstance().getVersion();
        } catch (Exception e) {
            logger.warn("An exception happened when retrieving extension version", e);
        }
        return version;
    }

}