package com.test;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.utilities.operations.file.ApplyFile;

import java.net.URL;

/**
 * @author facarvalho
 */
public class BlankTemplate extends TransformationTemplate {

    public BlankTemplate() {
        setBlank(true);

        URL fileUrl = this.getClass().getResource("/butterfly.properties");
        add(new ApplyFile().setFileUrl(fileUrl).relative(""));
    }

    @Override
    public Class<? extends Extension> getExtensionClass() {
        return SampleExtension.class;
    }

    @Override
    public String getDescription() {
        return "BlankTemplate for tests purposes";
    }

}
