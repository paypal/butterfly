package com.paypal.butterfly.cli;

import java.util.*;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * This is just a POJO that represents a Butterfly Extension.
 * Its purpose is to facilitate serializing a the extension for UI purposes.
 *
 * @author facarvalho, mmcrockett
 */
public class ExtensionMetaData {

    private String name;
    private String description;
    private String version;
    private List<TemplateMetaData> templates = new ArrayList<>();
    private NavigableSet<String> supportedUpgradeVersions = new TreeSet<>();

    private ExtensionMetaData() {
    }

    static ExtensionMetaData newExtensionMetaData(Extension extension) throws IllegalAccessException, InstantiationException {
        ExtensionMetaData extensionMetaData = new ExtensionMetaData();
        extensionMetaData.name = extension.toString();
        extensionMetaData.description = extension.getDescription();
        extensionMetaData.version = extension.getVersion();

        int shortcut = 1;
        for(Object templateObj : extension.getTemplateClasses().toArray()) {
            Class<? extends TransformationTemplate> template = (Class<? extends TransformationTemplate>) templateObj;
            TemplateMetaData templateMetaData = extensionMetaData.addTemplate(template, shortcut);
            if (templateMetaData.getUpgradeToVersion() != null) {
                extensionMetaData.supportedUpgradeVersions.add(templateMetaData.getUpgradeToVersion());
            }
            shortcut++;
        }
        if (extensionMetaData.supportedUpgradeVersions.size() == 0) {
            extensionMetaData.supportedUpgradeVersions = null;
        } else {
            extensionMetaData.supportedUpgradeVersions = Collections.unmodifiableNavigableSet(extensionMetaData.supportedUpgradeVersions);
        }

        return extensionMetaData;
    }

    private TemplateMetaData addTemplate(Class<? extends TransformationTemplate> transformationTemplateClass, int shortcut) throws InstantiationException, IllegalAccessException {
        TemplateMetaData templateMetaData = TemplateMetaData.newTemplateMetaData(this, transformationTemplateClass, shortcut);
        templates.add(templateMetaData);

        return templateMetaData;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    /**
     * Returns an unmodifiable list of all transformation template metadata
     * registered to this extension metadata
     *
     * @return an unmodifiable list of all transformation template metadata
     */
    public List<TemplateMetaData> getTemplates() {
        return Collections.unmodifiableList(templates);
    }

    public NavigableSet<String> getSupportedUpgradeVersions() {
        return supportedUpgradeVersions;
    }
}