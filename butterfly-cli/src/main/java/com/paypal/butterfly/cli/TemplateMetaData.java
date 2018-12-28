package com.paypal.butterfly.cli;

import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;

/**
 * This is just a POJO that represents a Template of a Butterfly extension.
 * Its purpose is to facilitate serializing
 * a transformation template for UI purposes.
 *
 * @author facarvalho, mmcrockett
 */
class TemplateMetaData {

    private transient ExtensionMetaData extensionMetaData;
    private String name;
    private String className;
    private TemplateType templateType;
    private String description;
    private String upgradeFromVersion;
    private String upgradeToVersion;
    private int shortcut;

    private TemplateMetaData() {
    }

    static TemplateMetaData newTemplateMetaData(ExtensionMetaData extensionMetaData, Class<? extends TransformationTemplate> transformationTemplateClass, int shortcut) throws IllegalAccessException, InstantiationException {
        TemplateMetaData templateMetaData = new TemplateMetaData();

        templateMetaData.extensionMetaData = extensionMetaData;
        templateMetaData.className = transformationTemplateClass.getName();
        templateMetaData.shortcut = shortcut;
        templateMetaData.templateType = TemplateType.getFromClass(transformationTemplateClass);

        TransformationTemplate transformationTemplate = transformationTemplateClass.newInstance();
        templateMetaData.name = transformationTemplate.getName();
        templateMetaData.description = transformationTemplate.getDescription();

        if (transformationTemplate instanceof UpgradeStep) {
            UpgradeStep upgradeStep = (UpgradeStep) transformationTemplate;
            templateMetaData.upgradeFromVersion = upgradeStep.getCurrentVersion();
            templateMetaData.upgradeToVersion = upgradeStep.getNextVersion();
        }

        return templateMetaData;
    }

    ExtensionMetaData getExtensionMetaData() {
        return extensionMetaData;
    }

    String getName() {
        return name;
    }

    String getClassName() {
        return className;
    }

    TemplateType getTemplateType() {
        return templateType;
    }

    String getDescription() {
        return description;
    }

    String getUpgradeFromVersion() {
        return upgradeFromVersion;
    }

    String getUpgradeToVersion() {
        return upgradeToVersion;
    }

    int getShortcut() {
        return shortcut;
    }

}