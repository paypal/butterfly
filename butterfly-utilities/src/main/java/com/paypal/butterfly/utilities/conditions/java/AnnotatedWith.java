package com.paypal.butterfly.utilities.conditions.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.List;

/**
 * Evaluates if the specified
 * Java class or interface is annotated with the
 * specified annotation.
 *
 * @author facarvalho
 */
public class AnnotatedWith extends AbstractTypeCheck<AnnotatedWith> {

    public AnnotatedWith(Class specifiedType) {
        super(specifiedType);
    }

    public AnnotatedWith(String specifiedTypeName) {
        super(specifiedTypeName);
    }

    @Override
    protected String getTypeName(CompilationUnit compilationUnit, int index) {
        List<AnnotationExpr> annotations = compilationUnit.getTypes().get(0).getAnnotations();
        return annotations.get(index).getNameAsString();
    }

    @Override
    protected int getNumberOfTypes(CompilationUnit compilationUnit) {
        List<AnnotationExpr> annotations = compilationUnit.getTypes().get(0).getAnnotations();
        return annotations.size();
    }

}