package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;

/**
 * Operation to add a new dependency to a POM file
 *
 * @author facarvalho
 */
public class PomAddDependency extends AbstractArtifactPomOperation<PomAddDependency> {

    // TODO
    // Add pre-validation to check, in case version was not set, if dependency
    // is managed or not. If not, fail!

    // TODO
    // What happens if dependency already exists? Fail? Warning? Replace it (if different version)?

    private static final String DESCRIPTION = "Add dependency %s:%s:%s to POM file %s";

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
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) throws IOException, XmlPullParserException {

        // FIXME what if the dependency already exists?

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
        String details = String.format("Dependency %s:%s%s has been added to POM file %s", groupId, artifactId, (version == null ? "" : ":"+ version), relativePomFile);

        return TOExecutionResult.success(this, details);
    }

    @Override
    public PomAddDependency clone() throws CloneNotSupportedException {
        PomAddDependency clonedPomAddDependency = (PomAddDependency)super.clone();
        return clonedPomAddDependency;
    }

}
