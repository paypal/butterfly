package com.paypal.butterfly.extensions.springboot;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.extensions.api.utilities.Abort;
import com.paypal.butterfly.utilities.conditions.FileExists;
import com.paypal.butterfly.utilities.operations.pom.PomChangeParentVersion;

public class SpringBootUpgrade_1_5_6_to_1_5_7 extends UpgradeStep {

    public SpringBootUpgrade_1_5_6_to_1_5_7() {
        final String pomFileExists = add(new FileExists().relative("pom.xml"));
        add(new Abort("This application does not have a root pom.xml file").executeUnless(pomFileExists));
        add(new PomChangeParentVersion("1.5.7.RELEASE").relative("pom.xml"));
    }

    @Override
    public Class<? extends Extension> getExtensionClass() {
        return ButterflySpringBootExtension.class;
    }

    @Override
    public String getDescription() {
        return "Upgrade Spring Boot application from version 1.5.6 to version 1.5.7";
    }

    @Override
    public String getCurrentVersion() {
        return "1.5.6";
    }

    @Override
    public String getNextVersion() {
        return "1.5.7";
    }

    @Override
    public UpgradeStep getNextStep() {
        return null;
    }

}
