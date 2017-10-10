package com.paypal.butterfly.extensions.api.upgrade;

/**
 * A special type of upgrade template that takes an application
 * from one minor version to the next subsequent available version.
 *
 * @author facarvalho
 */
public abstract class UpgradeStep extends UpgradeTemplate {

    /**
     * Returns the version the application would be upgraded to
     *
     * @return the version the application would be upgraded to
     */
    public abstract String getNextVersion();

    /**
     * Returns the next {@link UpgradeStep}.
     * Returns null if there is no {@link UpgradeStep}
     * associated with the next version
     *
     * @return the next {@link UpgradeStep}
     */
    public abstract UpgradeStep getNextStep();

}
