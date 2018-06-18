package paypal.butterfly.sample;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;

import java.io.File;

/**
 * Butterfly sample extension
 *
 * @author facarvalho
 */
public class SampleButterflyExtension extends Extension {

    public SampleButterflyExtension() {
        add(JavaEEToSpringBoot.class);
    }

    @Override
    public String getDescription() {
        return "Sample extension";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public Class<? extends TransformationTemplate> automaticResolution(File file) throws TemplateResolutionException {
        return JavaEEToSpringBoot.class;
    }

}
