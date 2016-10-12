package com.paypal.butterfly.basic.utilities.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Transformation utility to retrieve the package
 * of a given Java class
 *
 * @author facarvalho
 */
public class JavaPackage extends TransformationUtility<JavaPackage> {

    private static final String DESCRIPTION = "Retrieve the package of a Java class file %s";

    public JavaPackage() {
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath());
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File javaClassFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        FileInputStream fileInputStream = null;
        TUExecutionResult result = null;

        try {
            fileInputStream = new FileInputStream(javaClassFile);
            CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);
            PackageDeclaration packageDeclaration = compilationUnit.getPackage();
            result = TUExecutionResult.value(this, packageDeclaration.getPackageName());
        } catch (FileNotFoundException|ParseException  e) {
            result = TUExecutionResult.error(this, e);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                result.addWarning(e);
            }
        }

        return result;
    }

}
