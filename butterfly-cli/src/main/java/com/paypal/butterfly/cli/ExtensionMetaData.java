package com.paypal.butterfly.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * This is just a POJO that represents a Butterfly Extension.
 * Its purpose is to facilitate serializing a the extension for UI purposes.
 *
 * @author facarvalho, mmcrockett
 */
class ExtensionMetaData {

    private String name;
    private String description;
    private String version;
    private List<TemplateMetaData> templates = new ArrayList<>();

    private static transient AtomicInteger shortcut = new AtomicInteger(1);

    private ExtensionMetaData() {
    }

    static ExtensionMetaData newExtensionMetaData(Extension extension) throws IllegalAccessException, InstantiationException {
        ExtensionMetaData extensionMetaData = new ExtensionMetaData();
        extensionMetaData.name = extension.toString();
        extensionMetaData.description = extension.getDescription();
        extensionMetaData.version = extension.getVersion();

        for(Object templateObj : extension.getTemplateClasses().toArray()) {
            Class<? extends TransformationTemplate> template = (Class<? extends TransformationTemplate>) templateObj;
            extensionMetaData.addTemplate(template, shortcut.getAndIncrement());
        }

        return extensionMetaData;
    }

    private TemplateMetaData addTemplate(Class<? extends TransformationTemplate> transformationTemplateClass, int shortcut) throws InstantiationException, IllegalAccessException {
        TemplateMetaData templateMetaData = TemplateMetaData.newTemplateMetaData(this, transformationTemplateClass, shortcut);
        templates.add(templateMetaData);

        return templateMetaData;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    String getVersion() {
        return version;
    }

    /**
     * Returns an unmodifiable list of all transformation template metadata
     * registered to this extension metadata
     *
     * @return an unmodifiable list of all transformation template metadata
     */
    List<TemplateMetaData> getTemplates() {
        return Collections.unmodifiableList(templates);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ExtensionMetaData)) return false;

        ExtensionMetaData extensionMetaData = (ExtensionMetaData) obj;
        if (!Objects.equals(extensionMetaData.name, this.name)) return false;
        if (!Objects.equals(extensionMetaData.description, this.description)) return false;
        if (!Objects.equals(extensionMetaData.version, this.version)) return false;

        return Objects.equals(extensionMetaData.templates, this.templates);
    }

}