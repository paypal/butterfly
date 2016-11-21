package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.TransformationTemplate;

import java.net.URL;

/**
 * Instances of this class represents a manual instruction record
 *
 * @author facarvalho
 */
public class ManualInstructionRecord {

    private TransformationTemplate transformationTemplate;
    private String description;
    private URL resource;

    ManualInstructionRecord(TransformationTemplate transformationTemplate, String description, URL resource) {
        setTransformationTemplate(transformationTemplate);
        setDescription(description);
        setResource(resource);
    }

    private void setTransformationTemplate(TransformationTemplate transformationTemplate) {
        this.transformationTemplate = transformationTemplate;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setResource(URL resource) {
        this.resource = resource;
    }

    public TransformationTemplate getTransformationTemplate() {
        return transformationTemplate;
    }

    public String getDescription() {
        return description;
    }

    public URL getResource() {
        return resource;
    }

}
