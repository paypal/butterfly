package com.paypal.butterfly.extensions.api.utilities;

import java.net.URL;

/**
 * Instances of this class represents a manual instruction record
 *
 * @author facarvalho
 */
public class ManualInstructionRecord {

    private String description;
    private URL resource;

    ManualInstructionRecord(String description, URL resource) {
        setDescription(description);
        setResource(resource);
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setResource(URL resource) {
        this.resource = resource;
    }

    public String getDescription() {
        return description;
    }

    public URL getResource() {
        return resource;
    }

}
