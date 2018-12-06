package com.paypal.butterfly.utilities.pom;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Download a Maven artifact, model it and place it in transformation context.
 * The Maven artifact is specified by its group id, artifact id and version.
 *
 * @author vkuncham, radkrish
 */
public class PomModel extends TransformationUtility<PomModel> {

    private String groupId;
    private String artifactId;
    private String version;
    private String repoURI = DEFAULT_REPO_URI;

    private static final String DEFAULT_REPO_URI = "https://repo1.maven.org/maven2";

    private static final String DESCRIPTION = "Retrieve the parent pom and load it in to Model Object";

    /**
     * Download a Maven artifact, model it and place it in transformation context.
     * The Maven artifact is specified by its group id, artifact id and version.
     */
    public PomModel() {
    }

    /**
     * Download a Maven artifact, model it and place it in transformation context.
     * The Maven artifact is specified by its group id, artifact id and version.
     *
     * @param groupId    Maven artifact group id
     * @param artifactId Maven artifact id
     * @param version    Maven artifact version
     */
    public PomModel(String groupId, String artifactId, String version) {
        setGroupId(groupId);
        setArtifactId(artifactId);
        setVersion(version);
    }

    /**
     * Download a Maven artifact, model it and place it in transformation context.
     * The Maven artifact is specified by its group id, artifact id and version.
     *
     * @param groupId    Maven artifact group id
     * @param artifactId Maven artifact id
     * @param version    Maven artifact version
     * @param repoURI    Maven repository URI
     */
    public PomModel(String groupId, String artifactId, String version, String repoURI) {
        setGroupId(groupId);
        setArtifactId(artifactId);
        setVersion(version);
        setRepoURI(repoURI);
    }

    /**
     * Sets the groupId, artifactId & version from artifactInfo.
     *
     * @param artifactInfo
     * @return this transformation utility
     */
    public PomModel setArtifact(String artifactInfo) {
        checkForBlankString("artifactInfo", artifactInfo);
        String[] artifactInfoArray = artifactInfo.split(":");
        if (artifactInfoArray.length != 3) {
            throw new TransformationDefinitionException("Artifact info should be specified as [groupId]:[artifactId]:[version]");
        }
        setGroupId(artifactInfoArray[0]);
        setArtifactId(artifactInfoArray[1]);
        setVersion(artifactInfoArray[2]);
        return this;
    }

    /**
     * Sets Maven artifact group id
     *
     * @param groupId Maven artifact group id
     * @return this transformation utility
     */
    public PomModel setGroupId(String groupId) {
        checkForBlankString("groupId", groupId);
        this.groupId = groupId;
        return this;
    }

    /**
     * @return Maven artifact group id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the artifactId
     *
     * @param artifactId
     * @return this transformation utility
     */
    public PomModel setArtifactId(String artifactId) {
        checkForBlankString("artifactId", artifactId);
        this.artifactId = artifactId;
        return this;
    }

    /**
     * @return artifactId
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Sets the version
     *
     * @param version
     * @return this transformation utility
     */
    public PomModel setVersion(String version) {
        checkForBlankString("version", version);
        this.version = version;
        return this;
    }

    /**
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the repoURI
     *
     * @param repoURI
     * @return this transformation utility
     */
    public PomModel setRepoURI(String repoURI) {
        checkForBlankString("repoURI", repoURI);
        repoURI = repoURI.replaceAll("/$", "");
        boolean valid = isValidURI(repoURI);
        if (!valid) {
            throw new TransformationDefinitionException("repoURI is not a valid URI");
        }
        this.repoURI = repoURI;
        return this;
    }

    /**
     * @return repoURI
     */
    public String getRepoURI() {
        return repoURI;
    }

    private boolean isValidURI(String uri) {
        final URL url;
        try {
            url = new URL(uri);
        } catch (Exception e1) {
            return false;
        }
        return "https".equals(url.getProtocol()) || "http".equals(url.getProtocol());
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION);
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        Model model = null;
        try {
            model = fetchModelFromRemote();
            if (model == null)
                return TUExecutionResult.error(this, new TransformationUtilityException("Returned maven model is null"));
        } catch (IOException | XmlPullParserException e) {
            return TUExecutionResult.error(this, new TransformationUtilityException("The specified file could not be found or read and parsed as valid Maven pom file", e));
        }
        return TUExecutionResult.value(this, model);
    }

    /**
     * method to do remote fetch from maven repo URI
     *
     * @return Model
     */
    private Model fetchModelFromRemote() throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;

        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(getRepoURI() + "/" + getGroupId() + "/" + getArtifactId() + "/" + getVersion() + "/" + getArtifactId() + "-" + getVersion() + ".pom").openStream())) {
            model = reader.read(inputStream);
        }
        return model;
    }
}

