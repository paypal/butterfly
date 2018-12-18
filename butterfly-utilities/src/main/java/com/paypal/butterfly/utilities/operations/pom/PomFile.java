package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.AddElementTO;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Creates, or overwrites, a pom.xml file given a Model object.
 * The Maven Model can be set as a transformation context attribute or as an object, available during definition time.
 * The path to the directory of the pom file is set using {@link #relative(String)}, {@link #absolute(String)} or {@link #absolute(String, String)}.
 * If a pom file is already present, by default an error should be returned. However, that behavior can be configured,
 * according to {@link com.paypal.butterfly.extensions.api.operations.AddElementTO}.
 *
 * @author facarvalho
 */
public class PomFile extends AddElementTO<PomFile> {

    private static final String DESCRIPTION = "Writes a pom.xml file at %s given a Model object%s";

    // Name of the transformation context attribute holding the Model object
    private String attribute;

    // The model object used to write the pom file
    private Model model;

    /**
     * Creates, or overwrites, a pom.xml file given a Model object.
     * The Maven Model can be set as a transformation context attribute or as an object, available during definition time.
     * If a pom file is already present, by default an error should be returned. However, that behavior can be configured,
     * according to {@link com.paypal.butterfly.extensions.api.operations.AddElementTO}.
     */
    public PomFile() {
    }

    /**
     * Creates, or overwrites, a pom.xml file given a Model object.
     * The Maven Model can be set as a transformation context attribute or as an object, available during definition time.
     * If a pom file is already present, by default an error should be returned. However, that behavior can be configured,
     * according to {@link com.paypal.butterfly.extensions.api.operations.AddElementTO}.
     *
     * @param attribute the name of the transformation context attribute holding the {@link Model} object
     */
    public PomFile(String attribute) {
        setAttribute(attribute);
    }

    /**
     * Creates, or overwrites, a pom.xml file given a Model object.
     * The Maven Model can be set as a transformation context attribute or as an object, available during definition time.
     * If a pom file is already present, by default an error should be returned. However, that behavior can be configured,
     * according to {@link com.paypal.butterfly.extensions.api.operations.AddElementTO}.
     *
     * @param model the {@link Model} object used to write the pom.xml file
     */
    public PomFile(Model model) {
        setModel(model);
    }

    /**
     * Sets the name of the transformation context attribute holding the {@link Model} object
     *
     * @param attribute the name of the transformation context attribute holding the {@link Model} object
     * @return this transformation operation
     */
    public PomFile setAttribute(String attribute) {
        checkForBlankString("attribute", attribute);
        this.attribute = attribute;
        model = null;
        return this;
    }

    /**
     * Sets the {@link Model} object used to write the pom.xml file
     *
     * @param model the {@link Model} object used to write the pom.xml file
     * @return this transformation operation
     */
    public PomFile setModel(Model model) {
        checkForNull("model", model);
        this.model = model;
        attribute = null;
        return this;
    }

    /**
     * Returns the name of the transformation context attribute holding the Model object
     *
     * @return the name of the transformation context attribute holding the Model object
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Returns the {@link Model} object used to write the pom.xml file
     *
     * @return the {@link Model} object used to write the pom.xml file
     */
    public Model getModel() {
        return model;
    }

    @Override
    public String getDescription() {
        String modelObject = (attribute == null ? "" : " kept at transformation context attribute " + attribute);
        return String.format(DESCRIPTION, getRelativePath(), modelObject);
    }

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        if (attribute != null) {
            if (!transformationContext.contains(attribute)) {
                return TOExecutionResult.error(this, new TransformationOperationException("Transformation context attribute " + attribute + " does not exist"));
            }
            Object modelObj = transformationContext.get(attribute);
            if (modelObj == null) {
                return TOExecutionResult.error(this, new TransformationOperationException("Transformation context attribute " + attribute + " is null"));
            }
            if (!(modelObj instanceof Model)) {
                return TOExecutionResult.error(this, new TransformationOperationException("Transformation context attribute " + attribute + " is not a Model object, but " + modelObj.getClass().getName()));
            }
            model = (Model) modelObj;
        } else if (model == null) {
            return TOExecutionResult.error(this, new TransformationOperationException("Neither transformation context attribute nor Model object were specified"));
        }

        File pomFile = new File(getAbsoluteFile(transformedAppFolder, transformationContext), "pom.xml");
        TOExecutionResult errorResult;

        if (pomFile.exists()) {
            switch (ifPresent) {
                case Fail:
                    return TOExecutionResult.error(this, new TransformationOperationException("There is already a pom file at " + getRelativePath()));
                case WarnNotAdd:
                    return TOExecutionResult.warning(this, new TransformationOperationException("There is already a pom file at " + getRelativePath()));
                case NoOp:
                    return TOExecutionResult.noOp(this, "There is already a pom file at " + getRelativePath());
                case WarnButAdd:
                    errorResult = writePomFile(pomFile);
                    if (errorResult != null) return errorResult;
                    return TOExecutionResult.warning(this, new TransformationOperationException("There is already a pom file at " + getRelativePath()), "Pom file overwritten at " + getRelativePath());
                case Overwrite:
                    errorResult = writePomFile(pomFile);
                    if (errorResult != null) return errorResult;
                    return TOExecutionResult.success(this, "Pom file overwritten at " + getRelativePath());
            }
        } else {
            try {
                pomFile.createNewFile();
            } catch (IOException e) {
                return TOExecutionResult.error(this, new TransformationOperationException("There was an error when creating pom.xml file at " + getRelativePath() + ", double check if that directory exists"));
            }
        }

        errorResult = writePomFile(pomFile);
        if (errorResult != null) return errorResult;

        return TOExecutionResult.success(this, "Pom file written at " + getRelativePath());
    }

    /*
     * Writes pom file and return null if successful, or an error result if not
     */
    private TOExecutionResult writePomFile(File pomFile) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(pomFile)) {
            new MavenXpp3Writer().write(fileOutputStream, model);
        } catch (IOException e) {
            return TOExecutionResult.error(this, new TransformationOperationException("There was an error when writing to pom file " + getRelativePath()));
        }
        return null;
    }

}
