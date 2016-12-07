package com.paypal.butterfly.utilities.conditions.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.conditions.AbstractCriteriaCondition;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Condition to check if a particular Maven dependency exists in one
 * or more Maven pom files. Multiple files can be  specified (via {@link #setFiles(String...)}).
 * While one file can be specified by regular {@link #relative(String)} and
 * {@link #absolute(String)} methods.
 * </br>
 * When evaluating multiple files, the criteria is configurable, and
 * can be al of them have the dependency, or at least one of them has it.
 * </br>
 * These three behaviors are set by the following methods:
 * <ol>
 *     <li>{@link #singleFile()}</li>
 *     <li>{@link #multipleFilesAtLeastOne()}</li>
 *     <li>{@link #multipleFilesAll()}</li>
 * </ol>
 * <strong>Notes:</strong>
 * <ol>
 *     <li>{@link #singleFile()} is the default behavior if none is specified</li>
 *     <li>When a multiple files method is set, the file specified by {@link #relative(String)}
 *     or {@link #absolute(String)} is ignored</li>
 * </ol>
 *
 * @author facarvalho
 */
public class PomDependencyExists extends AbstractCriteriaCondition<PomDependencyExists> {

    private static final String DESCRIPTION = "Check if dependency '%s:%s:%s' exists in one or more POM files";

    private String groupId;
    private String artifactId;
    private String version = null;

    public PomDependencyExists() {
    }

    /**
     * Condition to check if a particular Maven dependency exists in one
     * or more Maven pom files. Multiple files can be  specified (via {@link #setFiles(String...)}).
     * While one file can be specified by regular {@link #relative(String)} and
     * {@link #absolute(String)} methods.
     * </br>
     * When evaluating multiple files, the criteria is configurable, and
     * can be al of them have the dependency, or at least one of them has it.
     * </br>
     * These three behaviors are set by the following methods:
     * <ol>
     *     <li>{@link #singleFile()}</li>
     *     <li>{@link #multipleFilesAtLeastOne()}</li>
     *     <li>{@link #multipleFilesAll()}</li>
     * </ol>
     * <strong>Notes:</strong>
     * <ol>
     *     <li>{@link #singleFile()} is the default behavior if none is specified</li>
     *     <li>When a multiple files method is set, the file specified by {@link #relative(String)}
     *     or {@link #absolute(String)} is ignored</li>
     * </ol>
     *
     * @param groupId managed dependency group id
     * @param artifactId managed dependency artifact id
     */
    public PomDependencyExists(String groupId, String artifactId) {
        setGroupId(groupId);
        setArtifactId(artifactId);
    }

    /**
     * Condition to check if a particular Maven dependency exists in one
     * or more Maven pom files. Multiple files can be  specified (via {@link #setFiles(String...)}).
     * While one file can be specified by regular {@link #relative(String)} and
     * {@link #absolute(String)} methods.
     * </br>
     * When evaluating multiple files, the criteria is configurable, and
     * can be al of them have the dependency, or at least one of them has it.
     * </br>
     * These three behaviors are set by the following methods:
     * <ol>
     *     <li>{@link #singleFile()}</li>
     *     <li>{@link #multipleFilesAtLeastOne()}</li>
     *     <li>{@link #multipleFilesAll()}</li>
     * </ol>
     * <strong>Notes:</strong>
     * <ol>
     *     <li>{@link #singleFile()} is the default behavior if none is specified</li>
     *     <li>When a multiple files method is set, the file specified by {@link #relative(String)}
     *     or {@link #absolute(String)} is ignored</li>
     * </ol>
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
        return String.format(DESCRIPTION, groupId, artifactId, (version == null ? "" : version));
    }

    @Override
    protected boolean eval(File transformedAppFolder, TransformationContext transformationContext, File file) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        FileInputStream fileInputStream = null;
        boolean exists = false;
        TransformationUtilityException ex = null;

        try {
            fileInputStream = new FileInputStream(file);
            Model model = reader.read(fileInputStream);
            for (Dependency d : model.getDependencies()) {
                if (d.getGroupId().equals(groupId) && d.getArtifactId().equals(artifactId) && (version == null || version.equals(d.getVersion()))) {
                    exists = true;
                    break;
                }
            }
        } catch (XmlPullParserException|IOException e) {
            String pomFileRelative = getRelativePath(transformedAppFolder, file);
            String dependency = String.format("%s:%s%s", groupId, artifactId, (version == null ? "" : ":" + version));
            String details = String.format("Exception happened when checking if POM dependency %s exists in %s", dependency, pomFileRelative);
            ex = new TransformationUtilityException(details, e);
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    if (ex == null) {
                        String pomFileRelative = getRelativePath(transformedAppFolder, file);
                        ex = new TransformationUtilityException("Exception happened when closing pom file " + pomFileRelative, e);
                    } else {
                        ex.addSuppressed(e);
                    }
                }
            }
        }

        if (ex != null) {
            throw ex;
        }

        return exists;
    }

}
