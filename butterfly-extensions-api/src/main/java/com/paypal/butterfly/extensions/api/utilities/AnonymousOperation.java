package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.TransformationOperation;

/**
 * This is a basic abstract transformation operation that allows transformation templates
 * to define on-the-fly anonymous operations where only the execution method is left
 * to be implemented. Notice that the description can be set using a setter
 * inside of the execution implementation. If not set, a generic description will be set.
 *
 * @author facarvalho
 */
public abstract class AnonymousOperation extends TransformationOperation<AnonymousOperation> {

    private String description = null;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public AnonymousOperation setName(String name) {
        if(description == null) {
            description = "Anonymous transformation operation " + name;
        }
        return super.setName(name);
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
