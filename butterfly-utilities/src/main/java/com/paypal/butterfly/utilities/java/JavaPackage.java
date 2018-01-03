package com.paypal.butterfly.utilities.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * Retrieves the package
 * of a given Java class.
 *
 * @author facarvalho
 */
public class JavaPackage extends TransformationUtility<JavaPackage> {

    private static final String DESCRIPTION = "Retrieve the package of a Java class file %s";

    // Even though it is redundant to have this default constructor here, since it is
    // the only one (the compiler would have added it implicitly), this is being explicitly
    // set here to emphasize that the public default constructor should always be
    // available by any transformation utility even when additional constructors are present.
    // The reason for that is the fact that one or more of its properties might be set
    // during transformation time, using the TransformationUtility set method
    @SuppressWarnings("PMD.UnnecessaryConstructor")
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

        // TODO
        // Add a validation here simply checking if the file name ends with .java

        try {
            fileInputStream = new FileInputStream(javaClassFile);
            CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);
            Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
            result = TUExecutionResult.value(this, packageDeclaration.get().getNameAsString());
        } catch (Exception  e) {
            TransformationUtilityException tue = new TransformationUtilityException("An error happened when trying to read and parse the specified Java file " + getRelativePath(transformedAppFolder, javaClassFile), e);
            result = TUExecutionResult.error(this, tue);
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
