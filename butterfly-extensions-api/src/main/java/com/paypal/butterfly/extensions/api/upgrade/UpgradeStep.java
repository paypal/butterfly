package com.paypal.butterfly.extensions.api.upgrade;

import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * A special type of upgrade template that takes an application
 * from one minor version to the next subsequent available version
 *
 * @author facarvalho
 */
public abstract class UpgradeStep<US> extends UpgradeTemplate<US> {

    public abstract String getNextVersion();

}
