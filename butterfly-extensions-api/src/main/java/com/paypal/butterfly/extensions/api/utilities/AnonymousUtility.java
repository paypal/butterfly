package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.TransformationUtility;

/**
 * This is a basic abstract transformation utility that allows transformation templates
 * to define on-the-fly anonymous utilities where only the execution method is left
 * to be implemented. Notice that the description can be set using a setter
 * inside of the execution implementation. If not set, a generic description will be set.
 *
 * @author facarvalho
 */
public abstract class AnonymousUtility<RT> extends TransformationUtility<AnonymousUtility, RT> {

    private String description = null;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public AnonymousUtility setName(String name) {
        if(description == null) {
            description = "Anonymous transformation utility " + name;
        }
        return super.setName(name);
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
