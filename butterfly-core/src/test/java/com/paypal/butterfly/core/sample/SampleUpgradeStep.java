package com.paypal.butterfly.core.sample;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;

/**
 * Created by vkuncham on 11/7/2016.
 */
public class SampleUpgradeStep extends UpgradeStep {

    @Override
    public String getNextVersion() {
        return null;
    }

    @Override
    public UpgradeStep getNextStep() {
        return null;
    }

    @Override
    public String getCurrentVersion() {
        return null;
    }

    @Override
    public Class<? extends Extension> getExtensionClass() {
        return ExtensionSample.class;
    }

    @Override
    public String getDescription() {
        return "Butterfly extension";
    }

    @Override
    public String getApplicationType() {
        return null;
    }

    @Override
    public String getApplicationName() {
        return null;
    }

}
