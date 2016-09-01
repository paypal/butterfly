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

    private static final String DESCRIPTION = "Add dependency %s:%s:%s to POM file %s";

    private String groupId;
    private String artifactId;
    private String version;
    private String scope;

    public PomAddDependency() {
    }

    /**
     * Operation to add a new dependency to a POM file.
     * This constructor assumes this is a managed dependency, since the version
     * is not set. However, if that is not really the case, during transformation
     * this operation will fail pre-validation.
     *
     * @param groupId new dependency group id
     * @param artifactId new dependency artifact id
     */
    public PomAddDependency(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    /**
     * Operation to add a new dependency to a POM file.
     *
     * @param groupId new dependency group id
     * @param artifactId new dependency artifact id
     * @param version new dependency artifact version
     */
    public PomAddDependency(String groupId, String artifactId, String version) {
        this(groupId, artifactId);
        setVersion(version);
    }

    /**
     * Operation to add a new dependency to a POM file.
     *
     * @param groupId new dependency group id
     * @param artifactId new dependency artifact id
     * @param version new dependency artifact version
     * @param scope new dependency artifact scope
     */
    public PomAddDependency(String groupId, String artifactId, String version, String scope) {
        this(groupId, artifactId, version);
        setScope(scope);
    }

    public PomAddDependency setGroupId(String groupId) {
        checkForBlankString("GroupId", groupId);
        this.groupId = groupId;
        return this;
    }

    public PomAddDependency setArtifactId(String artifactId) {
        checkForBlankString("ArtifactId",artifactId);
        this.artifactId = artifactId;
        return this;
    }

    public PomAddDependency setVersion(String version) {
        checkForEmptyString("Version", version);
        this.version = version;
        return this;
    }

    public PomAddDependency setScope(String scope) {
        checkForEmptyString("Scope", scope);
        this.scope = scope;
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

    public String getScope() {
        return scope;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, version, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        MavenXpp3Reader reader = new MavenXpp3Reader();
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(pomFile);

            Model model = reader.read(fileInputStream);
            Dependency dependency = new Dependency();
            dependency.setGroupId(groupId);
            dependency.setArtifactId(artifactId);
            if (version != null) {
                dependency.setVersion(version);
            }
            if (scope != null) {
                dependency.setScope(scope);
            }
            model.addDependency(dependency);

            fileOutputStream = new FileOutputStream(pomFile);
            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(fileOutputStream, model);
        }finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
            }finally {
                if(fileOutputStream != null) fileOutputStream.close();
            }
        }

        return String.format("Dependency %s:%s%s has been added to POM file %s", groupId, artifactId, (version == null ? "" : ":"+ version), getRelativePath());
    }

    @Override
    public PomAddDependency clone() throws CloneNotSupportedException {
        PomAddDependency clonedPomAddDependency = (PomAddDependency)super.clone();
        return clonedPomAddDependency;
    }

}
