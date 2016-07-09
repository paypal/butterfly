package com.paypal.butterfly.basic.conditions.pom;

import com.paypal.butterfly.extensions.api.TransformationOperationCondition;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

/**
 * Transformation operation condition to check if
 * a particular Maven dependency exists or not
 *
 * @author facarvalho
 */
public class PomDependencyExists extends TransformationOperationCondition {

    private static final Logger logger = LoggerFactory.getLogger(PomDependencyExists.class);

    private static final String DESCRIPTION = "Check if dependency '%s:%s:%s' exists in POM file %s";

    private String groupId;
    private String artifactId;
    private String version = null;

    /**
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     */
    private PomDependencyExists(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to add a new dependency to a POM file.
     * This constructor assumes this is a managed dependency, since the version
     * is not set. However, if that is not really the case, during transformation
     * this operation will fail pre-validation.
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     * @param groupId new dependency group id
     * @param artifactId new dependency artifact id
     */
    public PomDependencyExists(String relativePath, String groupId, String artifactId) {
        this(relativePath);
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    /**
     * Operation to add a new dependency to a POM file.
     * This constructor assumes this is a managed dependency, since the version
     * is not set. However, if that is not really the case, during transformation
     * this operation will fail pre-validation.
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     * @param groupId new dependency group id
     * @param artifactId new dependency artifact id
     */
    public PomDependencyExists(String relativePath, String groupId, String artifactId, String version) {
        this(relativePath, groupId, artifactId);
        this.version = version;
    }

    @Override
    protected boolean evaluate(File transformedAppFolder) {
        File pomFile = getAbsoluteFile(transformedAppFolder);
        MavenXpp3Reader reader = new MavenXpp3Reader();

        try {
            Model model = reader.read(new FileInputStream(pomFile));
            for(Dependency d : model.getDependencies()) {
                if(d.getGroupId().equals(groupId) && d.getArtifactId().equals(artifactId)) {
                    return (version == null || version.equals(d.getVersion()));
                }
            }
        } catch (Exception e) {
            logger.error("Error happened during transformation operation condition evaluation");
            return false;
        }

        return false;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, (version == null ? "" : version), getRelativePath());
    }

}
