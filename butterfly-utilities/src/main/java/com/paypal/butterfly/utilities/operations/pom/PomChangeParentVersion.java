package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import com.paypal.butterfly.utilities.operations.pom.stax.StartElementEventCondition;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;

/**
 * Changes the parent's version in a Maven POM file.
 * If the artifact doesn't have a parent, this operation will result in error
 *
 * @author facarvalho
 */
public class PomChangeParentVersion extends AbstractStaxPomOperation<PomChangeParentVersion> implements ChangeOrRemoveElement<PomChangeParentVersion> {

    private static final String DESCRIPTION = "Change artifact's parent version in POM file %s";

    private String version = null;

    private IfNotPresent ifNotPresent = ChangeOrRemoveElement.IfNotPresent.Fail;

    public PomChangeParentVersion() {
    }

    /**
     * Operation to change the parent, or its version, in a Maven POM file
     *
     * @param version parent artifact version to be set
     */
    public PomChangeParentVersion(String version) {
        setVersion(version);
    }

    public PomChangeParentVersion setVersion(String version) {
        checkForBlankString("Version", version);
        this.version = version;
        return this;
    }

    @Override
    public PomChangeParentVersion failIfNotPresent() {
        ifNotPresent = ChangeOrRemoveElement.IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomChangeParentVersion warnIfNotPresent() {
        ifNotPresent = ChangeOrRemoveElement.IfNotPresent.Warn;
        return this;
    }

    @Override
    public PomChangeParentVersion noOpIfNotPresent() {
        ifNotPresent = ChangeOrRemoveElement.IfNotPresent.NoOp;
        return this;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(File transformedAppFolder, TransformationContext transformationContext) throws XmlPullParserException, XMLStreamException, IOException {
        File fileToBeModified = getAbsoluteFile(transformedAppFolder, transformationContext);

        TOExecutionResult result = null;

        boolean documentHasParent = getModel(fileToBeModified).getParent() != null;

        if (documentHasParent) {
            XMLEventReader reader = getReader(transformedAppFolder, transformationContext);
            XMLEventWriter writer = getWriter(transformedAppFolder, transformationContext);

            copyUntil(reader, writer, new StartElementEventCondition("parent"), true);
            XMLEvent oldVersionElement = copyUntil(reader, writer, new StartElementEventCondition("version"), true);

            // Discharging the current version
            reader.next();

            // Writing the new version
            writer.add(eventFactory.createCharacters(version));

            String relativePomFile = getRelativePath(transformedAppFolder, fileToBeModified);
            String oldVersion = oldVersionElement.asStartElement().getName().getLocalPart();
            String details = String.format("Parent's version for POM file (%s) has been changed from %s to %s", relativePomFile, oldVersion, version);
            result = TOExecutionResult.success(this, details);

            // Writing to the end
            writer.add(reader);
        } else {
            String details = String.format("Pom file %s does not have a parent", getRelativePath(transformedAppFolder, fileToBeModified));

            switch (ifNotPresent) {
                case NoOp:
                    result = TOExecutionResult.noOp(this, details);
                    break;
                case Warn:
                    TransformationOperationException w = new TransformationOperationException(details);
                    result = TOExecutionResult.warning(this, w);
                    break;
                case Fail:
                default:
                    TransformationOperationException e = new TransformationOperationException(details);
                    result = TOExecutionResult.error(this, e);
                    break;
            }
        }

        return result;
    }

}
