package com.paypal.butterfly.utilities.operations.pom;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

/**
 * Copies dependencies from a POM file to another.
 * It also allows replacements plus filtering which dependencies to copy.
 *
 * @author facarvalho
 */
public class PomCopyDependencies extends AbstractPomCopyDependencies<PomCopyDependencies> {

    private static final String DESCRIPTION = "Copy Maven dependencies from POM file %s to %s";

    public PomCopyDependencies() {
        super(DESCRIPTION);
    }

    @Override
    List<Dependency> getMavenDependencies(Model mavenModel) {
        return mavenModel.getDependencies();
    }

    @Override
    void addMavenDependencies(Model mavenModelTo, List<Dependency> dependencies) {
        dependencies.forEach(d -> mavenModelTo.addDependency(d));
    }

}
