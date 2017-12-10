package com.paypal.butterfly.utilities.maven;

import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a tree of Maven artifacts, which are represented by {@link Model} objects.
 * The idea here is, given a list of Maven pom.xml {@link File} objects, create a tree
 * based on dependency among them, but specifying explicitly which Maven artifact should
 * be at the root of the tree. That means, if any artifact in the list is not a child,
 * directly or indirectly, of the root artifact, then it will end up no being in the tree.
 * <br>
 * As a result of building this tree, it is possible to know, out of the initial pom.xml files list,
 * which ones actually inherit, directly or not, from the root artifact. The result is retrieved
 * by calling {@link #getPomFilesInTree()}
 *
 * @author facarvalho
 */
class ModelTree {

    private ModelNode rootNode;
    private List<ModelNode> nodesInTree = new ArrayList<>();

// TODO we can comment this out in the future if we need this feature
//    private List<Model> modelsInTree = new ArrayList<>();
//    private List<Model> modelsNotInTree = new ArrayList<>();
    private List<File> pomFilesInTree = new ArrayList<>();

    /**
     * This is a tree of Maven artifacts, which are represented by {@link Model} objects.
     * The idea here is, given a list of Maven pom.xml {@link File} objects, create a tree
     * based on dependency among them, but specifying explicitly which Maven artifact should
     * be at the root of the tree. That means, if any artifact in the list is not a child,
     * directly or indirectly, of the root artifact, then it will end up no being in the tree.
     * <br>
     * As a result of building this tree, it is possible to know, out of the initial pom.xml files list,
     * which ones actually inherit, directly or not, from the root artifact. The result is retrieved
     * by calling {@link #getPomFilesInTree()}
     *
     * @param rootGroupId the group id of the artifact that should be at the root of the tree
     * @param rootArtifactId the artifact id of the artifact that should be at the root of the tree
     * @param rootVersion the version of the artifact that should be at the root of the tree
     * @param pomFiles a list of pom.xml files used to make the tree
     */
    public ModelTree(String rootGroupId, String rootArtifactId, String rootVersion, List<File> pomFiles) {
        Model rootModel = new Model();
        rootModel.setGroupId(rootGroupId);
        rootModel.setArtifactId(rootArtifactId);
        rootModel.setVersion(rootVersion);

        List<Model> models = new ArrayList<>();
        models.add(rootModel);

        for (File pomFile : pomFiles) {
            models.add(createModel(pomFile));
        }

// TODO we can comment this out in the future if we need this feature
//        modelsNotInTree = add(models);
        add(models);
    }

    private Model createModel(File pomFile) {
        FileInputStream fileInputStream = null;
        Exception ex = null;
        Model model = null;
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            fileInputStream = new FileInputStream(pomFile);
            model = reader.read(fileInputStream);
            if (model.getGroupId() == null) model.setGroupId(model.getParent().getGroupId());
            if (model.getVersion() == null) model.setVersion(model.getParent().getVersion());
            model.setPomFile(pomFile);
        } catch (Exception e) {
            ex = e;
            String exceptionMessage = String.format("Error when trying to create Maven pom file model, double check if this file has a valid Maven structure: %s", pomFile.getAbsolutePath());
            throw new TransformationUtilityException(exceptionMessage, ex);
        } finally {
            if (fileInputStream != null) try {
                fileInputStream.close();
            } catch (IOException e) {
                if (ex != null) ex.addSuppressed(e);
            }
        }

        return model;
    }

    private List<Model> add(List<Model> models) {
        int previousTreeSize = -1;
        while (models.size() > 0 && size() > previousTreeSize) {
            previousTreeSize = size();
            List<Model> leftOverModels = models;
            models = new ArrayList<>();
            for (Model model : leftOverModels) {
                if (!add(model)) {
                    models.add(model);
                }
            }
        }
        return models;
    }

    private boolean add(Model model) {
        ModelNode temp = null;
        if (rootNode == null) {
            rootNode = new ModelNode(model);
            nodesInTree.add(rootNode);
// TODO
// We can comment this out in the future if we want to allow
// not fixing the root artifact
//        } else if (rootNode.isChildOf(model)) {
//            temp = rootNode;
//            rootNode = new ModelNode(model);
//            rootNode.addChild(temp);
//            nodesInTree.add(rootNode);
        } else {
            for (ModelNode n : nodesInTree) {
                if(n.isParentOf(model)) {
                    temp = new ModelNode(model);
                    n.addChild(temp);
                    nodesInTree.add(temp);
                    break;
                }
            }
            if (temp == null) {
                return false;
            }
        }
// TODO we can comment this out in the future if we need this feature
//        modelsInTree.add(model);
        if (model.getPomFile() != null) {
            pomFilesInTree.add(model.getPomFile());
        }
        return true;
    }

    public int size() {
        return nodesInTree.size();
    }


    /**
     * List of pom.xml files that, directly or not, are children of the root artifact
     * set in the artifacts tree
     *
     * @return a list of pom.xml files that, directly or not, are children of the root artifact
     * set in the artifacts tree
     */
    public List<File> getPomFilesInTree() {
        return pomFilesInTree;
    }

    private static class ModelNode {

        private Model model;
        private List<ModelNode> children;

        private ModelNode(Model model) {
            this.model = model;
            children = new ArrayList<ModelNode>();
        }

        private void addChild(ModelNode child) {
            children.add(child);
        }

        @SuppressWarnings("PMD.SimplifyBooleanReturns")
        private boolean isParentOf(Model model) {
            if (model.getParent() == null) {
                return false;
            }

            if (!this.model.getGroupId().equals(model.getParent().getGroupId())) return false;
            if (!this.model.getArtifactId().equals(model.getParent().getArtifactId())) return false;
            if (!this.model.getVersion().equals(model.getParent().getVersion())) return false;
            return true;
        }

        @SuppressWarnings("PMD.SimplifyBooleanReturns")
        private boolean isChildOf(Model model) {
            if (this.model.getParent() == null) {
                return false;
            }

            if (!model.getGroupId().equals(this.model.getParent().getGroupId())) return false;
            if (!model.getArtifactId().equals(this.model.getParent().getArtifactId())) return false;
            if (!model.getVersion().equals(this.model.getParent().getVersion())) return false;
            return true;
        }

    }

}
