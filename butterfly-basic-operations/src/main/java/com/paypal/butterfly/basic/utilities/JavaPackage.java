package com.paypal.butterfly.basic.utilities;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;

import java.io.File;
import java.io.FileInputStream;

/**
 * Transformation utility to retrieve the package
 * of a given Java class
 *
 * @author facarvalho
 */
public class JavaPackage extends TransformationUtility<JavaPackage, String> {

    private static final String DESCRIPTION = "Retrieve the package of a Java class file %s";

    public JavaPackage() {
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        File javaClassFile = getAbsoluteFile(transformedAppFolder, transformationContext);

        FileInputStream fileInputStream = new FileInputStream(javaClassFile);
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(fileInputStream);
        } finally {
            fileInputStream.close();
        }
        PackageDeclaration packageDeclaration = compilationUnit.getPackage();

        return packageDeclaration.getPackageName();
    }

}
