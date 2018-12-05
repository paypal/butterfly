package com.paypal.butterfly.utilities.pom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.maven.model.Model;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Retrieve the packaging of specified Maven artifact.
 * There are two ways to specify the Maven artifact:
 * <ol>
 *     <li>As a file, specified via regular {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} methods</li>
 * <li>As a context attribute, containing the Maven {@link org.apache.maven.model.Model} object</li>
 * </ol>
 * If Maven artifact is set with both options, the Maven model will be used, and the file will be ignored.
 *
 * @author facarvalho 
 */
public class PomGetPackaging extends TransformationUtility<PomGetPackaging> {

    private String modelAttributeName;

    private static final String DESCRIPTION = "Retrieve the packaging of specified Maven POM module";

    /**
     * Retrieve the packaging of specified Maven artifact.
     * There are two ways to specify the Maven artifact:
     * <ol>
     *     <li>As a file, specified via regular {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} methods</li>
     * <li>As a context attribute, containing the Maven {@link org.apache.maven.model.Model} object</li>
     * </ol>
     * If Maven artifact is set with both options, the Maven model will be used, and the file will be ignored.
     */
    public PomGetPackaging() {
    }

    /**
     * Retrieve the packaging of specified Maven artifact.
     * There are two ways to specify the Maven artifact:
     * <ol>
     * <li>As a file, specified via regular {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)} methods</li>
     * <li>As a context attribute, containing the Maven {@link org.apache.maven.model.Model} object</li>
     * </ol>
     * If Maven artifact is set with both options, the Maven model will be used, and the file will be ignored.
     *
     * @param modelAttributeName the name of the context attribute containing the Maven {@link org.apache.maven.model.Model} object
     */
    public PomGetPackaging(String modelAttributeName) {
        setModelAttributeName(modelAttributeName);
    }

    /**
     * Sets the name of the context attribute containing the Maven {@link org.apache.maven.model.Model} object
     *
     * @param modelAttributeName the name of the context attribute containing the Maven {@link org.apache.maven.model.Model} object
     * @return this transformation utility
     */
    private PomGetPackaging setModelAttributeName(String modelAttributeName) {
        checkForBlankString("modelAttributeName", modelAttributeName);
        this.modelAttributeName = modelAttributeName;
        return this;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION);
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

        String packaging = model.getPackaging();
        if (packaging == null) {
            packaging = "jar";
        }

        return TUExecutionResult.value(this, packaging);
    }

}