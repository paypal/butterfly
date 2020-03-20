package com.paypal.butterfly.utilities.operations.pom;

import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;

/**
 * Copies managed dependencies from a POM file to another.
 * It also allows replacements plus filtering which managed dependencies to copy.
 *
 * @author facarvalho
 */
public class PomCopyManagedDependencies extends AbstractPomCopyDependencies<PomCopyManagedDependencies> {

    private static final String DESCRIPTION = "Copy Maven managed dependencies from POM file %s to %s";

    public PomCopyManagedDependencies() {
        super(DESCRIPTION);
    }

    @Override
    List<Dependency> getMavenDependencies(Model mavenModel) {
        DependencyManagement dependencyManagement = mavenModel.getDependencyManagement();
        if (dependencyManagement == null) {
            return Collections.emptyList();
        }
        return dependencyManagement.getDependencies();
    }

    @Override
    void addMavenDependencies(Model mavenModelTo, List<Dependency> dependencies) {
        if (mavenModelTo.getDependencyManagement() == null) {
            mavenModelTo.setDependencyManagement(new DependencyManagement());
        }
        dependencies.forEach(d -> mavenModelTo.getDependencyManagement().addDependency(d));
    }

}
