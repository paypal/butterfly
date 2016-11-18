package com.paypal.butterfly.utilities.conditions.pom;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.UtilityCondition;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
public class PomDependencyExists extends UtilityCondition<PomDependencyExists> {

    private static final String DESCRIPTION = "Check if dependency '%s:%s:%s' exists in POM file %s";

    private enum Mode {
        SINGLE, MULTI_ONE, MULTI_ALL
    }

    private String groupId;
    private String artifactId;
    private String version = null;

    private Mode mode = Mode.SINGLE;

    // Array of transformation context attributes that hold list of Files
    // which the condition should perform against.
    // If more than one attribute is specified, all list of files will be
    // combined into a single one.
    private String[] filesAttributes;

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

    /**
     * Sets one or more transformation context attributes that hold list of Files
     * which the condition should perform against.
     * If more than one attribute is specified, all list of files will be
     * combined into a single one
     *
     * @param filesAttributes one or more transformation context attributes that hold list
     *                   of Files which the condition should perform
     *                   against
     * @return this transformation utility object
     */
    public PomDependencyExists setFiles(String... filesAttributes) {
        this.filesAttributes = filesAttributes;
        return this;
    }

    /**
     * Only the file specified by {@link #relative(String)} or
     * {@link #absolute(String)} is evaluated
     */
    public void singleFile() {
        mode = Mode.SINGLE;
    }

    /**
     * All files specified by {@link #setFiles(String...)} are
     * evaluated, and true is returned if at least one of
     * them has the dependency.
     * </br>
     * The file specified by {@link #relative(String)} or
     * {@link #absolute(String)} is ignored
     */
    public PomDependencyExists multipleFilesAtLeastOne() {
        mode = Mode.MULTI_ONE;
        return this;
    }

    /**
     * All files specified by {@link #setFiles(String...)} are
     * evaluated, and true is returned only if all of them
     * have the dependency.
     * </br>
     * The file specified by {@link #relative(String)} or
     * {@link #absolute(String)} is ignored
     */
    public PomDependencyExists multipleFilesAll() {
        mode = Mode.MULTI_ALL;
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

    public String[] getFilesAttributes() {
        return Arrays.copyOf(filesAttributes, filesAttributes.length);
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, groupId, artifactId, (version == null ? "" : version), getRelativePath());
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        TUExecutionResult result = null;

        switch (mode) {
            case MULTI_ONE:
                // Same method call as MULTI_ALL
            case MULTI_ALL:
                result = multipleFileExecution(transformedAppFolder, transformationContext);
                break;
            case SINGLE:
                // SINGLE is the default mode
            default:
                result = singleFileExecution(transformedAppFolder, transformationContext);
        }

        return result;
    }

    private TUExecutionResult singleFileExecution(File transformedAppFolder, TransformationContext transformationContext) {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);

        try {
            boolean exists = dependencyExists(pomFile, transformedAppFolder);
            return TUExecutionResult.value(this, exists);
        } catch (TransformationUtilityException e) {
            return TUExecutionResult.error(this, e);
        }
    }

    private TUExecutionResult multipleFileExecution(File transformedAppFolder, TransformationContext transformationContext) {
        Collection<File> files;
        Set<File> allPomFiles = new HashSet<>();

        for(String attribute: filesAttributes) {
            files = (Collection<File>) transformationContext.get(attribute);
            if (files != null) {
                allPomFiles.addAll(files);
            }
        }

        if (allPomFiles.size() == 0) {
            TransformationUtilityException e = new TransformationUtilityException("No pom files have been specified");
            return TUExecutionResult.error(this, e);
        }

        boolean exists = false;
        try {
            for (File pomFile : allPomFiles) {
                exists = dependencyExists(pomFile, transformedAppFolder);
                if (!exists && mode.equals(Mode.MULTI_ALL) || exists && mode.equals(Mode.MULTI_ONE)) {
                    break;
                }
            }
        } catch (TransformationUtilityException e) {
            return TUExecutionResult.error(this, e);
        }

        return TUExecutionResult.value(this, exists);
    }

    private boolean dependencyExists(File pomFile, File transformedAppFolder) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        FileInputStream fileInputStream = null;
        boolean exists = false;
        TransformationUtilityException ex = null;

        try {
            fileInputStream = new FileInputStream(pomFile);
            Model model = reader.read(fileInputStream);
            for (Dependency d : model.getDependencies()) {
                if (d.getGroupId().equals(groupId) && d.getArtifactId().equals(artifactId) && (version == null || version.equals(d.getVersion()))) {
                    exists = true;
                    break;
                }
            }
        } catch (XmlPullParserException|IOException e) {
            String pomFileRelative = getRelativePath(transformedAppFolder, pomFile);
            String dependency = String.format("%s:%s%s", groupId, artifactId, (version == null ? "" : ":" + version));
            String details = String.format("Exception happened when checking if POM dependency %s exists in %s", dependency, pomFileRelative);
            ex = new TransformationUtilityException(details, e);
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    if (ex == null) {
                        String pomFileRelative = getRelativePath(transformedAppFolder, pomFile);
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
