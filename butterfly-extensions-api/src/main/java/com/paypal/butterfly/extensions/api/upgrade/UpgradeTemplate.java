package com.paypal.butterfly.extensions.api.upgrade;

import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * A special type of transformation template that is intended to upgrade an application
 *
 * @author facarvalho
 */
public abstract class UpgradeTemplate<UT> extends TransformationTemplate<UT> {

    public abstract String getCurrentVersion();

}
