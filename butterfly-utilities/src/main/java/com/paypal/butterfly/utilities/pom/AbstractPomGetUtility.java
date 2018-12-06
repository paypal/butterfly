package com.paypal.butterfly.utilities.pom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

/**
 * Abstract class to utility class to manipulate Maven artifacts either from a pom file or a model
 *
 * @author facarvalho
 */
public abstract class AbstractPomGetUtility<T extends AbstractPomGetUtility> extends TransformationUtility<T> {

    private String modelAttributeName;

    /**
     * Abstract class to utility class to manipulate Maven artifacts either from a pom file or a model
     */
    public AbstractPomGetUtility() {
    }

    /**
     * Abstract class to utility class to manipulate Maven artifacts either from a pom file or a model
     *
     * @param modelAttributeName the name of the context attribute containing the Maven {@link org.apache.maven.model.Model} object
     */
    public AbstractPomGetUtility(String modelAttributeName) {
        setModelAttributeName(modelAttributeName);
    }

    /**
     * Sets the name of the context attribute containing the Maven {@link org.apache.maven.model.Model} object
     *
     * @param modelAttributeName the name of the context attribute containing the Maven {@link org.apache.maven.model.Model} object
     * @return this transformation utility
     */
    public T setModelAttributeName(String modelAttributeName) {
        checkForBlankString("modelAttributeName", modelAttributeName);
        this.modelAttributeName = modelAttributeName;
        return (T) this;
    }

    /**
     * Returns the name of the context attribute containing the Maven {@link org.apache.maven.model.Model} object
     *
     * @return the name of the context attribute containing the Maven {@link org.apache.maven.model.Model} object
     */
    public String getModelAttributeName() {
        return modelAttributeName;
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        Model model;
        if (modelAttributeName == null) {
            if (!wasFileExplicitlySet()) {
                return TUExecutionResult.error(this, new TransformationUtilityException("Model transformation context attribute name nor pom file were set"));
            }
            File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);
            try (FileInputStream fileInputStream = new FileInputStream(pomFile)) {
                model = new MavenXpp3Reader().read(fileInputStream);
            } catch (IOException | XmlPullParserException e) {
                return TUExecutionResult.error(this, new TransformationUtilityException("The specified file could not be found or read and parsed as valid Maven pom file", e));
            }
        } else {
            if (!transformationContext.contains(modelAttributeName)) {
                return TUExecutionResult.error(this, new TransformationUtilityException("Transformation context attribute " + modelAttributeName + " does not exist"));
            }
            Object modelObj = transformationContext.get(modelAttributeName);
            if (modelObj == null) {
                return TUExecutionResult.error(this, new TransformationUtilityException("Transformation context attribute " + modelAttributeName + " is null"));
            }
            if (!(modelObj instanceof Model)) {
                return TUExecutionResult.error(this, new TransformationUtilityException("Transformation context attribute " + modelAttributeName + " is not a Maven model"));
            }
            model = (Model) modelObj;
        }

        return pomExecution(model);
    }

    protected abstract TUExecutionResult pomExecution(Model model);

}