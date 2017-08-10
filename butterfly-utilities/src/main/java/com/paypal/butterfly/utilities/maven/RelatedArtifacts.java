package com.paypal.butterfly.utilities.maven;

import com.paypal.butterfly.extensions.api.ExecutionResult;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;

import java.io.File;
import java.util.List;

/**
 * This transformation utility, given a list of pom.xml {@link File} objects and
 * a parent artifact, results in a sub-list of those pom.xml files containing only the ones
 * that are, directly or indirectly, a child of the specified parent artifact.
 *
 * @author facarvalho
 */
public class RelatedArtifacts extends TransformationUtility<RelatedArtifacts> {

    private static final String DESCRIPTION = "Identifies all pom files whose parent is %s:%s:%s, directly or indirectly";

    private String parentGroupId;
    private String parentArtifactId;
    private String parentVersion;
    private String pomFilesAttribute;

    public RelatedArtifacts() {
    }

    /**
     * This transformation utility, given a list of pom.xml {@link File} objects and
     * a parent artifact, results in a sub-list of those pom.xml files containing only the ones
     * that are, directly or indirectly, a child of the specified parent artifact.
     *
     * @param parentGroupId parent group id
     * @param parentArtifactId parent artifact id
     * @param parentVersion parent version
     * @param pomFilesAttribute the name of the transformation context attribute that contains
     *                          a list of pom.xml files to be analyzed
     */
    public RelatedArtifacts(String parentGroupId, String parentArtifactId, String parentVersion, String pomFilesAttribute) {
        setParentGroupId(parentGroupId);
        setParentArtifactId(parentArtifactId);
        setParentVersion(parentVersion);
        setPomFilesAttribute(pomFilesAttribute);
    }

    public RelatedArtifacts setParentGroupId(String parentGroupId) {
        checkForBlankString("parentGroupId", parentGroupId);
        this.parentGroupId = parentGroupId;
        return this;
    }

    public RelatedArtifacts setParentArtifactId(String parentArtifactId) {
        checkForBlankString("parentArtifactId", parentArtifactId);
        this.parentArtifactId = parentArtifactId;
        return this;
    }

    public RelatedArtifacts setParentVersion(String parentVersion) {
        checkForBlankString("parentVersion", parentVersion);
        this.parentVersion = parentVersion;
        return this;
    }

    public RelatedArtifacts setPomFilesAttribute(String pomFilesAttribute) {
        checkForBlankString("pomFilesAttribute", pomFilesAttribute);
        this.pomFilesAttribute = pomFilesAttribute;
        return this;
    }

    public String getParentGroupId() {
        return parentGroupId;
    }

    public String getParentArtifactId() {
        return parentArtifactId;
    }

    public String getParentVersion() {
        return parentVersion;
    }

    public String getPomFilesAttribute() {
        return pomFilesAttribute;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, parentGroupId, parentArtifactId, parentVersion);
    }

    @Override
    protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        List<File> pomFiles = (List<File>) transformationContext.get(pomFilesAttribute);
        ModelTree modelTree = new ModelTree(parentGroupId, parentArtifactId, parentVersion, pomFiles);
        List<File> pomFilesInTree = modelTree.getPomFilesInTree();

        return TUExecutionResult.value(this, pomFilesInTree);
    }

}
