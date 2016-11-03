package com.paypal.butterfly.utilities.conditions.pom;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.UtilityCondition;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Condition to check if a particular Maven dependency exists
 *
 * @author facarvalho
 */
public class PomDependencyExists extends UtilityCondition<PomDependencyExists> {

    private static final String DESCRIPTION = "Check if dependency '%s:%s:%s' exists in POM file %s";

    private String groupId;
    private String artifactId;
    private String version = null;

    public PomDependencyExists() {
    }

    /**
     * Condition to check if a particular Maven dependency exists or not
     *
     * @param groupId managed dependency group id
     * @param artifactId managed dependency artifact id
     */
    public PomDependencyExists(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    /**
     * Condition to check if a particular Maven dependency exists or not
     *
     * @param groupId managed dependency group id
     * @param artifactId managed dependency artifact id
     * @param version managed dependency version
     */
    public PomDependencyExists(String groupId, String artifactId, String version) {
        this(groupId, artifactId);
        setVersion(version);
    }

    public PomDependencyExists setGroupId(String groupId) {
        checkForBlankString("GroupId", groupId);
        this.groupId = groupId;
        return this;
    }

    public PomDependencyExists setArtifactId(String artifactId) {
        checkForBlankString("ArtifactId", artifactId);
        this.artifactId = artifactId;
        return this;
    }

    public PomDependencyExists setVersion(String version) {
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
        return String.format(DESCRIPTION, groupId, artifactId, (version == null ? "" : version), getRelativePath());
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        MavenXpp3Reader reader = new MavenXpp3Reader();
        FileInputStream fileInputStream = null;
        TUExecutionResult result = null;
        boolean exists = false;

        try {
            fileInputStream = new FileInputStream(pomFile);
            Model model = reader.read(fileInputStream);
            for (Dependency d : model.getDependencies()) {
                if (d.getGroupId().equals(groupId) && d.getArtifactId().equals(artifactId) && (version == null || version.equals(d.getVersion()))) {
                        exists = true;
                        break;
                }
            }
            result = TUExecutionResult.value(this, exists);
        } catch (XmlPullParserException|IOException e) {
            String pomFileRelative = getRelativePath(transformedAppFolder, pomFile);
            String dependency = String.format("%s:%s%s", groupId, artifactId, (version == null ? "" : ":" + version));
            String details = String.format("Error happened when checking if POM dependency %s exists in %s", dependency, pomFileRelative);
            result = TUExecutionResult.error(this, e, details);
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            }
        }

        return result;
    }

}
