package com.paypal.butterfly.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * This is just a POJO that represents an Extension of
 * Butterfly. Its purpose is to facilitate printing a
 * JSON file representing the Extension
 *
 * @author mmcrockett
 */
public class ButterflyCliExtensionMetaData {
    private String name;

    private String description;

    private String version;

    private List<ButterflyCliTemplateMetaData> templates = new ArrayList<ButterflyCliTemplateMetaData>();

    public ButterflyCliExtensionMetaData(String name, String description, String version) {
        setName(name);
        setDescription(description);
        setVersion(version);
    }

    public ButterflyCliExtensionMetaData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ButterflyCliTemplateMetaData> getTemplates() {
        return templates;
    }

    public void setTemplates(List<ButterflyCliTemplateMetaData> templates) {
        this.templates = templates;
    }

    public void addTemplate(String name, int shortcut, String typeInitial, String description) {
        ButterflyCliTemplateMetaData templateData = new ButterflyCliTemplateMetaData(name, shortcut, typeInitial, description);
        templates.add(templateData);
    }
}
