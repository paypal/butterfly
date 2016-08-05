package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Operation to remove a property entry from a properties file
 *
 * @author facarvalho
 */
public class PomRemoveProperty extends TransformationOperation<PomRemoveProperty> {

    private static final String DESCRIPTION = "Remove property %s from POM file %s";

    private String propertyName;

    public PomRemoveProperty() {
    }

    /**
     * Operation to remove a property entry from a properties file
     *
     * @param propertyName property to be removed
     */
    public PomRemoveProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public PomRemoveProperty setPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, propertyName, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);

        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileInputStream(pomFile));
        model.getProperties().remove(propertyName);
        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write(new FileOutputStream(pomFile), model);

        String resultMessage = String.format("Property %s has been removed from POM file %s", propertyName, getRelativePath());

        return resultMessage;
    }

}
