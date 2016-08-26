package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationContext;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Transformation context implementation
 *
 * @see {@link TransformationContext}
 *
 * @author facarvalho
 */
public class TransformationContextImpl implements TransformationContext {

    private Map<String, Object> attributes = new HashMap<String, Object>();

    @Override
    public Object get(String key) {
        if(StringUtils.isBlank(key)) {
            // TODO replace this by a better exception type.
            // TransformationContextException could be a good one, however, according to the link below,
            // it is a checked exception, but we definitely need a runtime exception here.
            // https://engineering.paypalcorp.com/confluence/display/RaptorServices/Butterfly#Butterfly-Butterflyexceptions
            throw new IllegalArgumentException("Transformation context attribute key cannot be null");
        }
        return attributes.get(key);
    }

    @Override
    public void put(String key, Object attribute) {
        if(StringUtils.isBlank(key) || attribute == null || (attribute instanceof String && StringUtils.isBlank((String) attribute))) {
            // TODO replace this by a better exception type.
            // TransformationContextException could be a good one, however, according to the link below,
            // it is a checked exception, but we definitely need a runtime exception here.
            // https://engineering.paypalcorp.com/confluence/display/RaptorServices/Butterfly#Butterfly-Butterflyexceptions
            throw new IllegalArgumentException("Transformation context attribute key and value cannot be null nor blank");
        }
        attributes.put(key, attribute);
    }

}
