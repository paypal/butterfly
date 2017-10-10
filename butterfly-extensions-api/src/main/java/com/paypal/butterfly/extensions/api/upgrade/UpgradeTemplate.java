package com.paypal.butterfly.extensions.api.upgrade;

import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * A special type of transformation template that is intended to upgrade an application
 *
 * @author facarvalho
 */
abstract class UpgradeTemplate extends TransformationTemplate {

    /**
     * Returns the current version of the application, in other words, the
     * version the application would be upgraded from when this upgrade
     * template is executed
     *
     * @return the current version of the application
     */
    public abstract String getCurrentVersion();

}
