package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.api.Application;
import com.paypal.butterfly.api.Configuration;

/**
 * Represents an specific transformation, made of an
 * {@link com.paypal.butterfly.extensions.api.upgrade.UpgradePath},
 * to be applied against a specific application
 *
 * @author facarvalho
 */
@SuppressWarnings("PMD.DefaultPackage")
class UpgradePathTransformationRequest extends AbstractTransformationRequest {

    private transient static final String TO_STRING_SYNTAX = "{ \"application\" : %s, \"upgrade from version\" : %s, \"to version\" : %s }";

    // The upgrade path to be applied
    private transient UpgradePath upgradePath;

    UpgradePathTransformationRequest(Application application, UpgradePath upgradePath, Configuration configuration) {
        super(application, configuration, false);
        if (upgradePath == null) {
            throw new IllegalArgumentException("Upgrade path cannot be null");
        }
        this.upgradePath = upgradePath;

        extensionName = getExtensionName(upgradePath.getExtension());
        extensionVersion = getExtensionVersion(upgradePath.getExtension());
        templateName = upgradePath.getFirstStepTemplateName();
        templateClassName = upgradePath.getFirstStepTemplateClassName();
        upgradeStep = true;
    }

    UpgradePath getUpgradePath() {
        return upgradePath;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_SYNTAX, getApplication(), upgradePath.getOriginalVersion(), upgradePath.getUpgradeVersion());
    }

}
