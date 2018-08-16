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
public class TemplateMetaData {

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

    public ExtensionMetaData getExtensionMetaData() {
        return extensionMetaData;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public TemplateType getTemplateType() {
        return templateType;
    }

    public String getDescription() {
        return description;
    }

    public String getUpgradeFromVersion() {
        return upgradeFromVersion;
    }

    public String getUpgradeToVersion() {
        return upgradeToVersion;
    }

    public int getShortcut() {
        return shortcut;
    }

}