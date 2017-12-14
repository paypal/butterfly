package com.paypal.butterfly.utilities.file;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads a resource from the classpath, writes it to a temporary file,
 * and then returns a {@link File} reference to it, which is saved in the transformation
 * context. The file is written to a temporary folder to be defined by the OS.
 * If no resource file is found, an error is returned.
 *
 * @author facarvalho
 */
public class LoadFile extends TransformationUtility<LoadFile> {

    private static final String DESCRIPTION = "Load resource %s and writes it to a temporary file";

    private String resource;

    /**
     * Loads a resource from the classpath, writes it to a temporary file,
     * and then returns a {@link File} reference to it, which is saved in the transformation
     * context. The file is written to a temporary folder to be defined by the OS.
     * If no resource file is found, an error is returned.
     */
    public LoadFile() {
    }

    /**
     * Loads a resource from the classpath, writes it to a temporary file,
     * and then returns a {@link File} reference to it, which is saved in the transformation
     * context. The file is written to a temporary folder to be defined by the OS.
     * If no resource file is found, an error is returned.
     *
     * @param resource the name of the resource in the classpath
     */
    public LoadFile(String resource) {
        setResource(resource);
    }

    /**
     * Sets the name of the resource in the classpath. The syntax
     * here is the same as the one used in {@link ClassLoader#getResource(String)}
     *
     * @param resource the name of the resource in the classpath
     * @return this utility instance
     */
    public LoadFile setResource(String resource) {
        checkForBlankString("resource", resource);
        this.resource = resource;
        return this;
    }

    /**
     * Returns the name of the resource in the classpath
     *
     * @return the name of the resource in the classpath
     */
    public String getResource() {
        return resource;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, resource);
    }

    @SuppressFBWarnings("NP_ALWAYS_NULL_EXCEPTION")
    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        TUExecutionResult result = null;
        InputStream inputStream = null;
        IOException ioException = null;

        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(resource);
            if (inputStream == null) {
                String exceptionMessage = String.format("Resource %s could not be found in the classpath", resource);
                TransformationUtilityException e = new TransformationUtilityException(exceptionMessage);
                result = TUExecutionResult.error(this, e);
            } else {
                String fileNameSuffix = "_" + resource.replace('/', '_').replace('\\', '_');
                File fileFromInputStream = File.createTempFile("butterfly_", fileNameSuffix);
                FileUtils.copyInputStreamToFile(inputStream, fileFromInputStream);
                result = TUExecutionResult.value(this, fileFromInputStream);
            }
        } catch (IOException e) {
            ioException = e;
            result = TUExecutionResult.error(this, e);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                if (ioException != null) {
                    ioException.addSuppressed(e);
                } else {
                    result.addWarning(e);
                }
            }
        }

        return result;
    }

}
