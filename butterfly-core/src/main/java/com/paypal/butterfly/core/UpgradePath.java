package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.exception.ButterflyRuntimeException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.g00fy2.versioncompare.Version;

/**
 * Upgrade paths enable upgrading an application from one version to a target version,
 * which means executing a collection of sequential upgrade steps. This collection
 * might be made of only one upgrade step as well.
 *
 * @author facarvalho
 */
final class UpgradePath {

    private static final Logger logger = LoggerFactory.getLogger(UpgradeStep.class);

    private static final String DESCRIPTION_FORMAT = "Upgrade path from version %s to version %s";

    // The first upgrade step to be executed in this upgrade path
    // It must never be null, and it might be the only one,
    // if the version it upgrades to is the same as upgradeVersion
    private UpgradeStep firstStep;

    // The target, or final, version this upgrade path will upgrade the application to
    private String upgradeVersion;

    // The next upgrade step to be executed. At first, this is same as firstStep.
    // As the transformation happens, step by step, this reference is updated to
    // the next upgrade step to be executed.
    // It might be null, if there is no more upgrade step to be executed next
    private UpgradeStep nextStep;

    // The description of the upgrade path, pretty much a message stating the versions
    // "from" and "to"
    private String description;

    UpgradePath(Class<? extends UpgradeStep> firstStepClass) {
        this(firstStepClass, null);
    }

    @SuppressWarnings("PMD.AvoidReassigningParameters")
    UpgradePath(Class<? extends UpgradeStep> firstStepClass, String upgradeVersion) {
        if (firstStepClass == null) {
            throw new IllegalArgumentException("First step class cannot be null");
        }
        try {
            this.firstStep = firstStepClass.newInstance();
        } catch (InstantiationException e) {
            String exceptionMessage = "Upgrade step class " + firstStepClass + " could not be instantiated. Run Butterfly in debug mode, double check if its extension has been properly registered, and also double check if it complies with Butterfly extensions API";
            logger.error(exceptionMessage, e);
            throw new ButterflyRuntimeException(exceptionMessage, e);
        } catch (IllegalAccessException e) {
            String exceptionMessage = "Upgrade step class " + firstStepClass + " could not be accessed";
            logger.error(exceptionMessage, e);
            throw new ButterflyRuntimeException(exceptionMessage, e);
        }
        if (upgradeVersion == null || upgradeVersion.isEmpty()) {
            upgradeVersion = getLastVersion();
        } else if (firstStep.getCurrentVersion().equals(upgradeVersion)) {
            throw new IllegalArgumentException("The requested upgrade version (" + upgradeVersion + ") is the same as the version the application is currently at");
        } else if (isOlderTargetVersion(upgradeVersion)) {
            throw new IllegalArgumentException("The requested upgrade version (" + upgradeVersion + ") is older than the version the application is currently at (" + firstStep.getCurrentVersion() + ")");
        } else if (!upgradeVersionValidation(upgradeVersion)) {
            throw new IllegalArgumentException("The requested upgrade version (" + upgradeVersion + ") is inexistent");
        }
        this.nextStep = firstStep;
        this.upgradeVersion = upgradeVersion;
        description = String.format(DESCRIPTION_FORMAT, firstStep.getCurrentVersion(), upgradeVersion);
    }

    /*
     * Returns the latest version possible based on the chain
     * of {@link UpgradeStep} objects, and the one on its end
     */
    private String getLastVersion() {
        UpgradeStep us = firstStep;
        while (us.getNextStep() != null) {
            us = us.getNextStep();
        }
        return us.getNextVersion();
    }

    /*
     * Returns true only {@code targetVersion} is valid, in other words,
     * if it is the "upgrade to" version of one of the {@link UpgradeStep}
     * objects in the chain
     */
    private boolean upgradeVersionValidation(String targetVersion) {
        UpgradeStep us = firstStep;
        do {
            if (us.getNextVersion().equals(targetVersion)) {
                return true;
            }
            us = us.getNextStep();
        } while (us != null);

        return false;
    }

    /*
     * Returns true if {@code targetVersion} is older than {@code currentVersion}
     * of {@code firstStep}
    */
    private boolean isOlderTargetVersion(String targetVersion){
        return new Version(targetVersion).isLowerThan(firstStep.getCurrentVersion());
    }

    /**
     * Returns the original version to upgrade the application from
     * in this upgrade path
     *
     * @return the original version to upgrade the application from
     * in this upgrade path
     */
    String getOriginalVersion() {
        return firstStep.getCurrentVersion();
    }

    /**
     * Returns the target version to upgrade the application to
     * in this upgrade path
     *
     * @return the target version to upgrade the application to
     * in this upgrade path
     */
    String getUpgradeVersion() {
        return upgradeVersion;
    }

    /**
     * Returns the {@link Extension} class associated
     * with this upgrade path via its first upgrade step
     *
     * @return the {@link Extension} class associated
     * with this upgrade path
     */
    Class<? extends Extension> getExtension() {
        return firstStep.getExtensionClass();
    }

    String getDescription() {
        return description;
    }

    /**
     * Returns true only if the upgrade path would not return
     * null if {@link #next()} was called
     *
     * @return if there is a next upgrade step to be executed
     */
    boolean hasNext() {
        return nextStep != null;
    }

    /**
     * Returns the next {@link UpgradeStep} to be executed and
     * move its internal cursor to the following {@link UpgradeStep}
     * object to be executed, if existent
     *
     * @return the next {@link UpgradeStep} to be executed
     */
    UpgradeStep next() {
        if (!hasNext()) {
            return null;
        }
        UpgradeStep stepToReturn = nextStep;
        if (stepToReturn.getNextVersion().equals(upgradeVersion)) {
            nextStep = null;
        } else {
            nextStep = nextStep.getNextStep();
        }
        return stepToReturn;
    }

    /**
     * Return the name of the upgrade template to be performed
     * as the first step in this upgrade path
     *
     * @return the name of the upgrade template to be performed
     * as the first step in this upgrade path
     */
    String getFirstStepTemplateName() {
        return firstStep.getName();
    }

    /**
     * Return the class name of the upgrade template to be performed
     * as the first step in this upgrade path
     *
     * @return the class name of the upgrade template to be performed
     * as the first step in this upgrade path
     */
    String getFirstStepTemplateClassName() {
        return firstStep.getClass().getName();
    }

}
