package com.paypal.butterfly.utilities.conditions.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.Optional;

/**
 * Evaluates if the specified
 * compilation unit directly extends the
 * specified class.
 *
 * @author facarvalho
 */
public class Extends extends AbstractTypeCheck<Extends> {

    public Extends(Class specifiedType) {
        super(specifiedType);
    }

    public Extends(String specifiedTypeName) {
        super(specifiedTypeName);
    }

    @Override
    protected String getTypeName(CompilationUnit compilationUnit, int index) {
        ClassOrInterfaceDeclaration type = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        NodeList<ClassOrInterfaceType> extendedTypes = type.getExtendedTypes();
        ClassOrInterfaceType extendedType = extendedTypes.get(index);
        String typeSimpleName = extendedType.getName().getIdentifier();
        Optional<ClassOrInterfaceType> scope = extendedType.getScope();
        String typeName;
        if (scope.isPresent()) {
            String typePackageName = scope.get().toString();
            typeName = String.format("%s.%s", typePackageName, typeSimpleName);
        } else {
            typeName = typeSimpleName;
        }
        return typeName;
    }

    @Override
    protected int getNumberOfTypes(CompilationUnit compilationUnit) {
        TypeDeclaration<?> typeDeclaration = compilationUnit.getType(0);
        if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration type = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
            NodeList<ClassOrInterfaceType> extendedTypes = type.getExtendedTypes();
            return extendedTypes.size();
        }

        // If typeDeclaration is not ClassOrInterfaceDeclaration, then it is
        // EnumDeclaration or AnnotationDeclaration, and none of them have
        // a getExtendedTypes operation

        return 0;
    }

}