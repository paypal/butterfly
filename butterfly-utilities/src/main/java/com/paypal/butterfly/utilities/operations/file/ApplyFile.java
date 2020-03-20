package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Applies a file, whose location is set as a URL,
 * into the transformed application.
 * The file is placed at the folder specified using
 * {@code relative} or {@code absolute} methods.
 *
 * @author facarvalho
 */
public class ApplyFile extends TransformationOperation<ApplyFile> {

    private static final String DESCRIPTION = "Download and place file %s at %s";

    private URL fileUrl;

    /**
     * Applies a file, whose location is set as a URL,
     * into the transformed application.
     * The file is placed at the folder specified using
     * {@code relative} or {@code absolute} methods.
     */
    public ApplyFile() {
    }

    /**
     * Applies a file, whose location is set as a URL,
     * into the transformed application.
     * The file is placed at the folder specified using
     * {@code relative} or {@code absolute} methods.
     *
     * @param fileUrl URL string to locate the file to be applied
     */
    public ApplyFile(String fileUrl) {
        try {
            checkForBlankString("File URL", fileUrl);
            setFileUrl(new URL(fileUrl));
        } catch (MalformedURLException e) {
            throw new TransformationDefinitionException("Malformed file URL", e);
        }
    }

    /**
     /**
     * Applies a file, whose location is set as a URL,
     * into the transformed application.
     * The file is placed at the folder specified using
     * {@code relative} or {@code absolute} methods.
     *
     * @param fileUrl URL to locate the file to be applied
     */
    public ApplyFile(URL fileUrl) {
        setFileUrl(fileUrl);
    }

    /**
     * Set the URL to locate the file to be applied
     *
     * @param fileUrl URL to locate the file to be applied
     * @return this transformation utility
     */
    public ApplyFile setFileUrl(URL fileUrl) {
        checkForNull("File URL", fileUrl);
        this.fileUrl = fileUrl;
        return this;
    }

    /**
     * Returns the URL to locate the file to be applied
     *
     * @return the URL to locate the file to be applied
     */
    public URL getFileUrl() {
        return fileUrl;
    }

    @Override
    public String getDescription() {
        String fileLocation = getRelativePath();
        if (StringUtils.isBlank(fileLocation)) {
            fileLocation = "the root folder";
        }
        return String.format(DESCRIPTION, fileUrl.getFile(), fileLocation);
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        // Folder where the file is supposed to be placed
        File folder = getAbsoluteFile(transformedAppFolder, transformationContext);
        FileOutputStream fileOutputStream = null;
        TOExecutionResult result = null;
        File fileDescriptor = null;
        ReadableByteChannel readableByteChannel = null;
        try {
            readableByteChannel = Channels.newChannel(fileUrl.openStream());

            int p = fileUrl.getPath().lastIndexOf("/") + 1;
            String fileName = fileUrl.getPath().substring(p);

            fileDescriptor = new File(folder, fileName);
            fileOutputStream = new FileOutputStream(fileDescriptor);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            String fileLocation = getRelativePath(transformedAppFolder, fileDescriptor.getParentFile());
            if (StringUtils.isBlank(fileLocation)) {
                fileLocation = "the root folder";
            }

            String details = String.format("File '%s' has been downloaded at %s", fileUrl.getFile(), fileLocation);
            result = TOExecutionResult.success(this, details);
        } catch (IOException e) {
            result = TOExecutionResult.error(this, e);
        } finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            }
            if (readableByteChannel != null) {
                try {
                    readableByteChannel.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            }
        }
        return result;
    }

}