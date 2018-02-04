package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.AddElement;
import com.paypal.butterfly.utilities.operations.pom.stax.EndElementEventCondition;
import com.paypal.butterfly.utilities.operations.pom.stax.StartElementEventCondition;
import org.apache.maven.model.Parent;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;

/**
 * Add a parent artifact in a Maven POM file.
 * By default, if parent is already present, it is overwritten.
 * This behavior though can be changed.
 *
 * @author facarvalho
 */
public class PomAddParent extends AbstractStaxArtifactPomOperation<PomAddParent> implements AddElement<PomAddParent> {

    private static final String DESCRIPTION = "Add parent artifact %s:%s in POM file %s";

    private String version = null;

    private IfPresent ifPresent = IfPresent.Overwrite;

    /**
     * Add a parent artifact in a Maven POM file.
     * By default, if parent is already present, it is overwritten.
     * This behavior though can be changed.
     */
    public PomAddParent() {
    }

    /**
     * Operation to add a parent artifact in a Maven POM file
     *
     * @param groupId parent artifact group id to be set
     * @param artifactId parent artifact id to be set
     * @param version parent artifact version to be set
     */
    public PomAddParent(String groupId, String artifactId, String version) {
        setGroupId(groupId);
        setArtifactId(artifactId);
        setVersion(version);
    }

    public PomAddParent setVersion(String version) {
        checkForBlankString("Version", version);
        this.version = version;
        return this;
    }

    @Override
    public PomAddParent failIfPresent() {
        ifPresent = IfPresent.Fail;
        return this;
    }

    @Override
    public PomAddParent warnNotAddIfPresent() {
        ifPresent = IfPresent.WarnNotAdd;
        return this;
    }

    @Override
    public PomAddParent warnButAddIfPresent() {
        ifPresent = IfPresent.WarnButAdd;
        return this;
    }

    @Override
    public PomAddParent noOpIfPresent() {
        ifPresent = IfPresent.NoOp;
        return this;
    }

    @Override
    public PomAddParent overwriteIfPresent() {
        ifPresent = IfPresent.Overwrite;
        return this;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getGroupId(), getArtifactId(), getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(File transformedAppFolder, TransformationContext transformationContext) throws XmlPullParserException, XMLStreamException, IOException {

        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        Parent existingParent = getModel(pomFile).getParent();
        String details;

        String relativePomFile = getRelativePath(transformedAppFolder, pomFile);

        if (existingParent != null) {
            String message = String.format("Pom file %s already has a parent", relativePomFile);

            switch (ifPresent) {
                case WarnNotAdd:
                    return TOExecutionResult.warning(this, message);
                case NoOp:
                    return TOExecutionResult.noOp(this, message);
                case Fail:
                    // Fail is the default
                    return TOExecutionResult.error(this, new TransformationOperationException(message));
            }
        }

        // FIXME this should be in a pre-validation
        if(groupId == null && artifactId == null && version == null) {
            throw new IllegalStateException("Invalid POM parent transformation operation");
        }

        Parent newParent = new Parent();
        newParent.setGroupId(groupId);
        newParent.setArtifactId(artifactId);
        newParent.setVersion(version);

        XMLEventReader reader = getReader(transformedAppFolder, transformationContext);
        XMLEventWriter writer = getWriter(transformedAppFolder, transformationContext);
        XMLEvent indentation = getIndentation(transformedAppFolder, transformationContext);

        TOExecutionResult result = null;

        if (existingParent != null) {
            copyUntil(reader, writer, new StartElementEventCondition("parent"), true);
            skipUntil(reader, new EndElementEventCondition("parent"));
            writer.add(LINE_FEED);

            writeNewParent(writer, indentation);

            writer.add(indentation);
            writer.add(eventFactory.createEndElement("", "","parent"));

            details = String.format("Parent for POM file %s has been overwritten to %s", relativePomFile, newParent);
            if (ifPresent.equals(IfPresent.Overwrite)) {
                result = TOExecutionResult.success(this, details);
            } else if (ifPresent.equals(IfPresent.WarnButAdd)) {
                result = TOExecutionResult.warning(this, details);
            }
        } else {
            copyUntil(reader, writer, new StartElementEventCondition("project"), true);

            writer.add(LINE_FEED);
            writer.add(LINE_FEED);
            writer.add(indentation);
            writer.add(eventFactory.createStartElement("", "", "parent"));
            writer.add(LINE_FEED);

            writeNewParent(writer, indentation);

            writer.add(indentation);
            writer.add(eventFactory.createEndElement("", "","parent"));
            writer.add(LINE_FEED);

            details = String.format("Parent for POM file %s has been set to %s", relativePomFile, newParent);
            result = TOExecutionResult.success(this, details);
        }

        writer.add(reader);

        return result;
    }

    private void writeNewParent(XMLEventWriter writer, XMLEvent indentation) throws XMLStreamException {
        writeMultiple(writer, indentation, 2);
        writer.add(eventFactory.createStartElement("", "", "groupId"));
        writer.add(eventFactory.createCharacters(groupId));
        writer.add(eventFactory.createEndElement("", "", "groupId"));
        writer.add(LINE_FEED);

        writeMultiple(writer, indentation, 2);
        writer.add(eventFactory.createStartElement("", "", "artifactId"));
        writer.add(eventFactory.createCharacters(artifactId));
        writer.add(eventFactory.createEndElement("", "", "artifactId"));
        writer.add(LINE_FEED);

        writeMultiple(writer, indentation, 2);
        writer.add(eventFactory.createStartElement("", "", "version"));
        writer.add(eventFactory.createCharacters(version));
        writer.add(eventFactory.createEndElement("", "", "version"));
        writer.add(LINE_FEED);
    }

}
