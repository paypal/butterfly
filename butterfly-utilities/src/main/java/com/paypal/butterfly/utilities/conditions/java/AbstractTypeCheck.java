package com.paypal.butterfly.utilities.conditions.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import org.apache.commons.lang3.StringUtils;

/**
 * Evaluates the specified
 * compilation unit based on an abstract check
 * against a specified type.
 *
 * @author facarvalho
 */
public abstract class AbstractTypeCheck<T extends AbstractTypeCheck> extends JavaCondition<T> {

    private String specifiedTypeSimpleName;
    private String specifiedTypePackageName;
    private String specifiedTypeName;

    public AbstractTypeCheck(Class specifiedType) {
        if (specifiedType == null) {
            throw new TransformationDefinitionException("Specified type cannot be null");
        }
        this.specifiedTypeName = specifiedType.getName();
        this.specifiedTypeSimpleName = specifiedType.getSimpleName();
        this.specifiedTypePackageName = specifiedType.getPackage().getName();
    }

    public AbstractTypeCheck(String specifiedTypeName) {
        if (specifiedTypeName == null || specifiedTypeName.trim().length() == 0) {
            throw new TransformationDefinitionException("Specified type cannot be empty");
        }
        this.specifiedTypeName = specifiedTypeName;
        int i = specifiedTypeName.lastIndexOf(".");
        if (i == -1) {
            this.specifiedTypeSimpleName = specifiedTypeName;
            this.specifiedTypePackageName = "";
        } else {
            this.specifiedTypeSimpleName = specifiedTypeName.substring(i + 1);
            this.specifiedTypePackageName = specifiedTypeName.substring(0, i);
        }
    }

    @Override
    public boolean eval(CompilationUnit compilationUnit) {
        String currentTypeName;
        boolean containsPackageName;

        for (int i = 0; i < getNumberOfTypes(compilationUnit); i++) {
            currentTypeName = getTypeName(compilationUnit, i);

            // Check if currentTypeName is fully qualified (contains package name)
            // If it is, then this check is just a matter of a simple comparison with
            // the specified currentTypeName
            containsPackageName = currentTypeName.contains(".");
            if (containsPackageName) {
                if (currentTypeName.equals(specifiedTypeName)) {
                    return true;
                } else {
                    continue;
                }
            }

            // Check currentTypeName class simple name first.
            // If it doesn't match, then just move on to the next item
            if (!currentTypeName.equals(specifiedTypeSimpleName)) {
                continue;
            }

            // Check if currentTypeName is imported to the compilation unit
            // If it is, then that means the annotation matches
            if(isImported(compilationUnit, currentTypeName)) {
                return true;
            }

        }
        return false;
    }

    protected abstract String getTypeName(CompilationUnit compilationUnit, int index);

    protected abstract int getNumberOfTypes(CompilationUnit compilationUnit);

    /*
     * Check if {@code typeSimpleName} is imported (explicitly or not)
     * to the Java class represented by {@code compilationUnit}.
     * It returns true if one of this conditions is met:
     *      1- {@code typeSimpleName} is explicitly imported with a import declaration
     *      2- {@code typeSimpleName} is implicitly imported for being part of java.lang
     *      3- {@code typeSimpleName} is at the same package where {@code compilationUnit} Java class is.
     */
    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    private boolean isImported(CompilationUnit compilationUnit, String typeSimpleName) {
        if (StringUtils.isBlank(typeSimpleName) || typeSimpleName.contains(".")) {
            throw new IllegalArgumentException("Invalid type simple name");
        }

        // If the specified type is part of the JDK,
        // then it won't need need an explicit import statement
        if (specifiedTypePackageName.startsWith("java.lang")) {
            return true;
        }

        // Check if the compilation unit has an explicit import declaration whose
        // type name matches the specified type simple name
        String importClass;
        for (ImportDeclaration importDeclaration : compilationUnit.getImports()) {
            importClass = importDeclaration.getName().getIdentifier();
            if (importClass.equals(typeSimpleName)) {
                return true;
            }
        }

        // Check if the annotation is declared
        // at the same package where the class is
        if (compilationUnit.getPackageDeclaration().get().getNameAsString().equals(specifiedTypePackageName)) {
            return true;
        }

        return false;
    }

}