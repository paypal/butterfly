package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;

import java.util.List;
import java.util.Objects;

/**
 * Abstract Artifact POM operation.
 *
 * @author facarvalho
 */
public abstract class AbstractArtifactPomOperation<T extends AbstractArtifactPomOperation> extends AbstractPomOperation<T> {

    protected String groupId;
    protected String artifactId;

    public T setGroupId(String groupId) {
        checkForBlankString("GroupId", groupId);
        this.groupId = groupId;
        return (T) this;
    }

    public T setArtifactId(String artifactId) {
        checkForBlankString("ArtifactId",artifactId);
        this.artifactId = artifactId;
        return (T) this;
    }

    /**
     * Set dependency group id and artifact id based on a String
     * whose format is "groupId:artifactId". It throws a
     * {@link TransformationDefinitionException} if the specified
     * artifact String is invalid
     *
     * @param artifact the artifact formatted String
     * @return this operation instance
     */
    public T setArtifact(String artifact) {
        checkForBlankString("artifact", artifact);
        String[] split = artifact.split(":");
        if (split.length != 2) {
            throw new TransformationDefinitionException("Invalid artifact " + artifact);
        }
        groupId = split[0];
        artifactId = split[1];
        return (T) this;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    protected Dependency getDependency(Model model) {
        return getDependencyInList(model.getDependencies(), groupId, artifactId);
    }

    protected Dependency getDependency(Model model, String groupId, String artifactId) {
        return getDependencyInList(model.getDependencies(), groupId, artifactId);
    }

    protected Dependency getManagedDependency(Model model) {
        return getManagedDependency(model, groupId, artifactId);
    }

    protected Dependency getManagedDependency(Model model, String groupId, String artifactId) {
        if (model.getDependencyManagement() == null) {
            return null;
        }
        return getDependencyInList(model.getDependencyManagement().getDependencies(), groupId, artifactId);
    }

    private Dependency getDependencyInList(List<Dependency> dependencyList, String groupId, String artifactId) {
        if (dependencyList == null || dependencyList.size() == 0) {
            return null;
        }

        Dependency dependency = null;
        for (Dependency d : dependencyList) {
            if(d.getArtifactId().equals(artifactId) && d.getGroupId().equals(groupId)) {
                dependency = d;
                break;
            }
        }

        return dependency;
    }

    protected Plugin getPlugin(Model model) {
        if (model.getBuild() == null) {
            return null;
        }
        return getPluginInList(model.getBuild().getPlugins(), groupId, artifactId);
    }

    protected Plugin getPlugin(Model model, String groupId, String artifactId) {
        if (model.getBuild() == null) {
            return null;
        }
        if (model.getBuild().getPlugins() == null) {
            return null;
        }
        return getPluginInList(model.getBuild().getPlugins(), groupId, artifactId);
    }

    protected Plugin getManagedPlugin(Model model) {
        return getManagedPlugin(model, groupId, artifactId);
    }

    protected Plugin getManagedPlugin(Model model, String groupId, String artifactId) {
        if (model.getBuild() == null) {
            return null;
        }
        if (model.getBuild().getPluginManagement().getPlugins() == null) {
            return null;
        }
        return getPluginInList(model.getBuild().getPluginManagement().getPlugins(), groupId, artifactId);
    }

    private Plugin getPluginInList(List<Plugin> pluginList, String groupId, String artifactId) {
        if (pluginList == null || pluginList.size() == 0) {
            return null;
        }

        Plugin plugin = null;
        for (Plugin d : pluginList) {
            if(d.getArtifactId().equals(artifactId) && d.getGroupId().equals(groupId)) {
                plugin = d;
                break;
            }
        }

        return plugin;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AbstractArtifactPomOperation)) return false;

        AbstractArtifactPomOperation tu = (AbstractArtifactPomOperation) obj;
        if (!Objects.equals(tu.groupId, this.groupId)) return false;
        if (!Objects.equals(tu.artifactId, this.artifactId)) return false;

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode(super.hashCode(),
                groupId,
                artifactId);
    }

}
