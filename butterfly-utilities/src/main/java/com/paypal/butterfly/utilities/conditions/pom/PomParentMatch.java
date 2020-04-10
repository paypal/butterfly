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

/**
 * Checks if a Maven POM file has a parent artifact and it matches the specified groupId, artifactId and version.
 * Returns an error if the file to be evaluated is not a well-formed XML file.
 *
 * @author apandilwar
 */
public class PomParentMatch extends SingleCondition<PomParentMatch> {

    private static final String DESCRIPTION = "Check if a Maven POM file has a parent artifact '%s:%s%s'";

    private String groupId;
    private String artifactId;
    private String version = null;

    public PomParentMatch() {
    }

    /**
     * Condition to check if a Maven POM file has a parent artifact and it matches the specified groupId, artifactId and version.
     * Returns an error if the file to be evaluated is not a well formed XML file.
     *
     * @param groupId    managed dependency group id
     * @param artifactId managed dependency artifact id
     */
    public PomParentMatch(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    /**
     * if a Maven POM file has a parent artifact and it matches the specified groupId, artifactId and version.
     * Returns an error if the file to be evaluated is not a well formed XML file.
     *
     * @param groupId    managed dependency group id
     * @param artifactId managed dependency artifact id
     * @param version    managed dependency version
     */
    public PomParentMatch(String groupId, String artifactId, String version) {
        this(groupId, artifactId);
        setVersion(version);
    }

    public PomParentMatch setGroupId(String groupId) {
        checkForBlankString("GroupId", groupId);
        this.groupId = groupId;
        return this;
    }

    public PomParentMatch setArtifactId(String artifactId) {
        checkForBlankString("ArtifactId", artifactId);
        this.artifactId = artifactId;
        return this;
    }

    public PomParentMatch setVersion(String version) {
        checkForEmptyString("Version", version);
        this.version = version;
        return this;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, (version == null ? "" : ":" + version));
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        boolean exists = false;
        File file = null;
        FileInputStream stream = null;
        TransformationUtilityException exception = null;

        try {
            file = getAbsoluteFile(transformedAppFolder, transformationContext);
            stream = new FileInputStream(file);
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(stream);
            Parent parent = model.getParent();
            if (parent != null && parent.getGroupId().equals(groupId) && parent.getArtifactId().equals(artifactId) && (version == null || version.equals(parent.getVersion()))) {
                exists = true;
            }
        } catch (XmlPullParserException | IOException e) {
            String pomFileRelativePath = getRelativePath(transformedAppFolder, file);
            String artifact = String.format("%s:%s%s", groupId, artifactId, (version == null ? "" : ":" + version));
            String exceptionMessage = String.format("Exception occurred while checking if Maven POM file %s has a parent artifact '%s'", pomFileRelativePath, artifact);
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

        return TUExecutionResult.value(this, exists);
    }

}
