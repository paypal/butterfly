package com.paypal.butterfly.cli;

/**
 * This is just a POJO that represents a Template of a Butterfly extension. Its
 * purpose is to facilitate printing a JSON file representing the Template
 *
 * @author mmcrockett
 */
public class ButterflyCliTemplateMetaData {
    private String name;
    private int shortcut;
    private String typeInitial;
    private String description;

    public ButterflyCliTemplateMetaData(String name, int shortcut, String typeInitial, String description) {
        setName(name);
        setShortcut(shortcut);
        setTypeInitial(typeInitial);
        setDescription(description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShortcut() {
        return shortcut;
    }

    public void setShortcut(int shortcut) {
        this.shortcut = shortcut;
    }

    public String getTypeInitial() {
        return typeInitial;
    }

    public void setTypeInitial(String typeInitial) {
        this.typeInitial = typeInitial;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
