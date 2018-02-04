package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.utilities.operations.pom.stax.AbstractStaxOperation;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Abstract POM operation.
 *
 * @author facarvalho
 */
abstract class AbstractStaxPomOperation<T extends AbstractStaxPomOperation> extends AbstractStaxOperation<T> {

    /*
     * Read the Maven pom file and returns an in-memory model of it
     */
    protected Model getModel(File pomFile) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        FileInputStream fileInputStream = new FileInputStream(pomFile);
        Model model = reader.read(fileInputStream);
        fileInputStream.close();

        return model;
    }

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        return super.execution(transformedAppFolder, transformationContext);
    }

    @Override
    protected TOExecutionResult xmlExecution(File transformedAppFolder, TransformationContext transformationContext) throws XmlPullParserException, XMLStreamException, IOException {
        return pomExecution(transformedAppFolder, transformationContext);
    }

    /*
     * This abstract method is supposed to be developed by the subclasses executing POM file operations
     */
    protected abstract TOExecutionResult pomExecution(File transformedAppFolder, TransformationContext transformationContext) throws XmlPullParserException, XMLStreamException, IOException;

}
