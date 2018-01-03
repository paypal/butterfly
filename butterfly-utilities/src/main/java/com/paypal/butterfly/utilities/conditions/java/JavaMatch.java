package com.paypal.butterfly.utilities.conditions.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.paypal.butterfly.extensions.api.SingleCondition;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Parses and evaluates the specified Java class file
 * based on a set of {@link JavaCondition}. It returns true only
 * if they all are true. If the specified Java class file contains
 * more than one type, only the outer one will be considered
 * during evaluation. If it has none, the evaluation will result
 * in false and a warning be returned.
 *
 * @author facarvalho
 */
public class JavaMatch extends SingleCondition<JavaMatch> {

    private static final String DESCRIPTION = "Check if Java class in '%s' matches all specified criteria";

    private Set<JavaCondition> conditions = new HashSet<>();

    /**
     * This utility parses and evaluates the specified Java class file
     * based on a set of {@link JavaCondition}. It returns true only
     * if they all are true. If the specified Java class file contains
     * more than one type, only the outer one will be considered
     * during evaluation. If it has none, the evaluation will result
     * in false and a warning be returned.
     */
    public JavaMatch() {}

    /**
     * This utility parses and evaluates the specified Java class file
     * based on a set of {@link JavaCondition}. It returns true only
     * if they all are true. If the specified Java class file contains
     * more than one type, only the outer one will be considered
     * during evaluation. If it has none, the evaluation will result
     * in false and a warning be returned.
     *
     * @param condition one condition to be evaluated. More can be added with
     *                  {@link #addCondition(JavaCondition)}
     */
    public JavaMatch(JavaCondition condition) {
        addCondition(condition);
    }

    /**
     * Returns the set of Java conditions to be used to
     * evaluate the specified class. The returned object
     * is not a copy and it is modifiable.
     *
     * @return the set of Java conditions to be used to evaluate the specified class
     */
    public Set<JavaCondition> getConditions() {
        return conditions;
    }

    /**
     * Sets the set of Java conditions to be used to
     * evaluate the specified class. Any conditions
     * added previously will be discarded.
     *
     * @param conditions the set of Java conditions to be used to evaluate the specified class
     * @return this transformation utility condition instance
     */
    public JavaMatch setConditions(Set<JavaCondition> conditions) {
        checkForNull("conditions", conditions);
        this.conditions = conditions;
        return this;
    }

    /**
     * Add a new Java condition to be evaluated against the Java class.
     *
     * @param condition a Java condition to be used to evaluate the specified class
     * @return this transformation utility condition instance
     */
    public JavaMatch addCondition(JavaCondition condition) {
        checkForNull("condition", condition);
        conditions.add(condition);
        return this;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File javaClassFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        FileInputStream fileInputStream = null;
        TUExecutionResult result = null;

        try {
            fileInputStream = new FileInputStream(javaClassFile);
            CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);

            // TODO
            // This should be done as part of validation, as soon as
            // TU validation is implemented
            if(compilationUnit.getTypes().size() == 0) {
                return TUExecutionResult.warning(this, new TransformationUtilityException("This Java class file has no declared types: " + javaClassFile.getAbsolutePath()), false);
            }

            boolean match = evaluate(compilationUnit);
            result = TUExecutionResult.value(this, match);
        } catch (Exception e) {
            result = TUExecutionResult.error(this, e);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                if (result == null) {
                    result = TUExecutionResult.error(this, e);
                } else {
                    result.addWarning(e);
                }
            }
        }

        return result;
    }

    /*
     * Evaluates all conditions against the specified Java class
     * and returns true only if all of them are true
     */
    private boolean evaluate(CompilationUnit compilationUnit) {
        for (JavaCondition condition : conditions) {
            if (!condition.evaluate(compilationUnit)) return false;
        }
        return true;
    }

}
