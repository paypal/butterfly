package com.paypal.butterfly.utilities.conditions.pom;

import com.paypal.butterfly.extensions.api.SingleCondition;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * Checks if a parent POM exists in a Maven POM file.
 * Returns an error if the file to be evaluated is not a well-formed XML file.
 *
 * @author apandilwar
 */
public class PomHasParent extends SingleCondition<PomHasParent> {

    private static final String DESCRIPTION = "Check if a parent POM exists in a Maven POM file";

    /**
     * Default Constructor
     */
    public PomHasParent() {
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        boolean hasParent = false;
        File file = null;
        FileInputStream stream = null;
        TransformationUtilityException exception = null;

        try {
            file = getAbsoluteFile(transformedAppFolder, transformationContext);
            stream = new FileInputStream(file);
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(stream);
            hasParent = Optional.ofNullable(model.getParent()).isPresent();
        } catch (XmlPullParserException | IOException e) {
            String pomFileRelativePath = getRelativePath(transformedAppFolder, file);
            String exceptionMessage = String.format("Exception occurred while checking if parent POM exists in %s", pomFileRelativePath);
            exception = new TransformationUtilityException(exceptionMessage, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    if (exception == null) {
                        String pomFileRelativePath = getRelativePath(transformedAppFolder, file);
                        String exceptionMessage = String.format("Exception occurred while closing POM file %s", pomFileRelativePath);
                        exception = new TransformationUtilityException(exceptionMessage, e);
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }

        if (exception != null) {
            return TUExecutionResult.error(this, exception);
        }

        return TUExecutionResult.value(this, hasParent);
    }

}
