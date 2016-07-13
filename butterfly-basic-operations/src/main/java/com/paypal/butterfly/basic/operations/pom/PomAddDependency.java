package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Operation to add a new dependency to a POM file
 *
 * @author facarvalho
 */
public class PomAddDependency extends TransformationOperation<PomAddDependency> {

    // TODO
    // Add pre-validation to check, in case version was not set, if dependency
    // is managed or not. If not, fail!

    // TODO
    // What happens if dependency already exists? Fail? Warning? Replace it (if different version)?

    private static final String DESCRIPTION = "Add dependency %s:%s:$s to POM file %s";

    private String groupId;
    private String artifactId;
    private String version;

    public PomAddDependency() {
    }

    /**
     * Operation to add a new dependency to a POM file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     */
    private PomAddDependency(String relativePath) {
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
    public PomAddDependency(String relativePath, String groupId, String artifactId) {
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
     * @param version new dependency artifact version
     */
    public PomAddDependency(String relativePath, String groupId, String artifactId, String version) {
        this(relativePath, groupId, artifactId);
        this.version = version;
    }

    public PomAddDependency setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public PomAddDependency setArtifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public PomAddDependency setVersion(String version) {
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
        return String.format(DESCRIPTION, groupId, artifactId, version, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        MavenXpp3Reader reader = new MavenXpp3Reader();

        Model model = reader.read(new FileInputStream(pomFile));
        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        if(version != null) {
            dependency.setVersion(version);
        }
        model.addDependency(dependency);

        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write(new FileOutputStream(pomFile), model);

        return String.format("Added dependency %s:%s:%s to POM file %s", groupId, artifactId, (version == null ? "" : version), getRelativePath());
    }

}
