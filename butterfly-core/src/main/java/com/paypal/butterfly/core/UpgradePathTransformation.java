package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.facade.Configuration;

/**
 * Represents an specific transformation, made of an
 * {@link com.paypal.butterfly.extensions.api.upgrade.UpgradePath},
 * to be applied against a specific application
 *
 * @author facarvalho
 */
@SuppressWarnings("PMD.DefaultPackage")
public class UpgradePathTransformation extends Transformation {

    private static final String TO_STRING_SYNTAX = "{ \"application\" : %s, \"upgrade from version\" : %s, \"to version\" : %s }";

    // The upgrade path to be applied
    private UpgradePath upgradePath;

    public UpgradePathTransformation(Application application, UpgradePath upgradePath, Configuration configuration) {
        super(application, configuration);
        if (upgradePath == null) {
            throw new IllegalArgumentException("Upgrade path cannot be null");
        }
        this.upgradePath = upgradePath;
    }

    public UpgradePath getUpgradePath() {
        return upgradePath;
    }

    @Override
    String getExtensionName() {
        return getExtensionName(upgradePath.getExtension());
    }

    @Override
    String getExtensionVersion() {
        return getExtensionVersion(upgradePath.getExtension());
    }

    @Override
    String getTemplateName() {
        return upgradePath.getFirstStepTemplateName();
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_SYNTAX, getApplication(), upgradePath.getOriginalVersion(), upgradePath.getUpgradeVersion());
    }

}
