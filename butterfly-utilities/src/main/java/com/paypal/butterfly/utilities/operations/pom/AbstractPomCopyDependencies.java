package com.paypal.butterfly.utilities.operations.pom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.operations.AbstractToOperation;

/**
 * Copies dependencies from a POM file to another.
 * It also allows replacements plus filtering which dependencies to copy.
 *
 * @author facarvalho
 */
abstract class AbstractPomCopyDependencies<T extends AbstractPomCopyDependencies> extends AbstractToOperation<T> {

    private static final Pattern dependencyStringEvaluator = Pattern.compile("([a-zA-Z0-9-_\\.]*\\:[a-zA-Z0-9-_\\.]*)");

    private Set<String> filter = new HashSet<>();
    private Set<String> filterAttributes = new HashSet<>();
    private Map<String, String> replacements = new HashMap<>();

    protected AbstractPomCopyDependencies(String description) {
        super(description);
    }

    /**
     * Registers a Maven dependency (in format groupId:artifactId) to be filtered out
     * when copying dependencies from original to POM file
     *
     * @param dependency a Maven dependency (in format groupId:artifactId) to be filtered out
     * @return this transformation operation instance
     */
    public T filter(String dependency) {
        checkForBlankString("Dependency", dependency);
        validateDependencyString(dependency);
        filter.add(dependency);
        return (T) this;
    }

    /**
     * Registers a Maven dependency (in format groupId:artifactId) to be filtered out
     * when copying dependencies from original to POM file. The dependency is stored
     * in a transformation context attribute name and is resolved during transformation time.
     * If that transformation context attribute does not exist, has null value, or has an invalid
     * value, it will be silently ignored.
     *
     * @param attributeName the name of the transformation context attribute name
     *                      that holds the Maven dependency (in format groupId:artifactId) to be filtered out
     * @return this transformation operation instance
     */
    public T filterAttribute(String attributeName) {
        checkForBlankString("Attribute name", attributeName);
        filterAttributes.add(attributeName);
        return (T) this;
    }

    /**
     * Registers a Maven dependency (in format groupId:artifactId) to be replaced
     * when copying dependencies from original to POM file
     *
     * @param dependency a Maven dependency (in format groupId:artifactId) to be replaced
     * @param replacementDependency the replacement Maven dependency (in format groupId:artifactId)
     * @return this transformation operation instance
     */
    public T replace(String dependency, String replacementDependency) {
        checkForBlankString("Dependency", dependency);
        checkForBlankString("Dependency replacement", replacementDependency);
        validateDependencyString(dependency);
        validateDependencyString(replacementDependency);
        replacements.put(dependency, replacementDependency);
        return (T) this;
    }

    private void validateDependencyString(String dependency) {
        if (!isValidDependencyString(dependency)) {
            throw new TransformationDefinitionException("Maven dependency String representation '" + dependency + "' does not conform with format groupId:artifactId");
        }
    }

    private boolean isValidDependencyString(String dependency) {
        return dependencyStringEvaluator.matcher(dependency).matches();
    }

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        TOExecutionResult result = null;
        FileOutputStream fileOutputStream = null;

        try {
            File fileFrom = getAbsoluteFile(transformedAppFolder, transformationContext);
            Model modelFrom = getModel(fileFrom);
            List<Dependency> dependencies = getMavenDependencies(modelFrom);

            if (dependencies.size() == 0) {
                result = TOExecutionResult.noOp(this, "POM file " + getRelativePath() + " does not have any dependencies");
            } else {
                File fileTo = getFileTo(transformedAppFolder, transformationContext);
                Model modelTo = getModel(fileTo);
                Set<Dependency> modelToDependencies = new TreeSet<>(Comparator.comparing(this::getDependencyString));

                modelToDependencies.addAll(getMavenDependencies(modelTo));

                addDependenciesByAttribute(transformationContext);

                List<Dependency> modifiedDependenciesList = dependencies.stream()
                        .filter(d -> !filter.contains(getDependencyString(d)))
                        .filter(d -> !modelToDependencies.contains(d))
                        .map(d -> replacements.containsKey(getDependencyString(d)) ? getDependencyModel(replacements.get(getDependencyString(d))) : d)
                        .collect(Collectors.toList());

                if (modifiedDependenciesList.size() == 0) {
                    result = TOExecutionResult.noOp(this, "All dependencies from POM file " + getRelativePath() + " were filtered out");
                } else {
                    addMavenDependencies(modelTo, modifiedDependenciesList);

                    fileOutputStream = new FileOutputStream(fileTo);
                    MavenXpp3Writer writer = new MavenXpp3Writer();
                    writer.write(fileOutputStream, modelTo);

                    result = TOExecutionResult.success(this, modifiedDependenciesList.size() + " dependencies were added to " + getRelativePath(transformedAppFolder, fileTo));
                }
            }
        } catch (IOException | XmlPullParserException e) {
            result = TOExecutionResult.error(this, new TransformationOperationException("POM file could not be modified", e));
        } finally {
            if(fileOutputStream != null) try {
                fileOutputStream.close();
            } catch (IOException e) {
                result.addWarning(e);
            }
        }

        return result;
    }

    /**
     * Return a list of dependencies, managed or not, depending on the implementation,
     * to be copied form the given Maven model.
     *
     * @param mavenModel Maven model used to get dependencies, managed or not, to be copied
     * @return a list of dependencies, managed or not, to be copied form the given Maven model
     */
    abstract List<Dependency> getMavenDependencies(Model mavenModel);

    /**
     * Add the given dependencies to the given Maven model. Whether the dependencies
     * are going to be added as regular dependencies, or as managed, is up to the implementation.
     *
     * @param mavenModelTo Maven model copied dependencies, managed or not, should be added to
     * @param dependencies the Maven dependencies, managed or not, to be added
     */
    abstract void addMavenDependencies(Model mavenModelTo, List<Dependency> dependencies);

    private void addDependenciesByAttribute(TransformationContext transformationContext) {
        filterAttributes.forEach(a -> {
            if (transformationContext.contains(a)) {
                Object v = transformationContext.get(a);
                if (v instanceof String && isValidDependencyString((String) v)) {
                    filter.add((String) v);
                }
            }
        });
    }

    /*
     * Returns a String representation of this Maven dependency made of
     * group id and artifact id separated by colon
     */
    private String getDependencyString(Dependency dependency) {
        return dependency.getGroupId() + ":" + dependency.getArtifactId();
    }

    /*
     * Returns a Dependency object constructed from its String representation,
     * which is made of group id and artifact id separated by colon
     */
    private Dependency getDependencyModel(String dependencyString) {
        int c = dependencyString.indexOf(':');
        Dependency d = new Dependency();
        d.setGroupId(dependencyString.substring(0, c));
        d.setArtifactId(dependencyString.substring(c + 1, dependencyString.length()));
        return d;
    }

    private Model getModel(File file) throws IOException, XmlPullParserException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            return reader.read(fileInputStream);
        }
    }

}
