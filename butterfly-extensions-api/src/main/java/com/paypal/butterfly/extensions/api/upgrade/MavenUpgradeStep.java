package com.paypal.butterfly.extensions.api.upgrade;

import com.paypal.butterfly.extensions.api.Extension;

public class MavenUpgradeStep extends UpgradeStep {

    private String nextVersion;
    private MavenUpgradeStep nextStep;
    private String currentVersion;
    private Class<? extends Extension> extensionClass;
    private String description;

    public MavenUpgradeStep() {
        setUp();
    }

    private void setUp() {

    }

    @Override
    public final String getNextVersion() {
        return nextVersion;
    }

    @Override
    public final MavenUpgradeStep getNextStep() {
        return nextStep;
    }

    @Override
    public final String getCurrentVersion() {
        return currentVersion;
    }

    @Override
    public final Class<? extends Extension> getExtensionClass() {
        return extensionClass;
    }

    @Override
    public final String getDescription() {
        return description;
    }

}
