package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Operation to apply the contents of a zip file
 *
 * @author facarvalho
 */
public class ApplyZip extends TransformationOperation<ApplyZip> {

    private static final Logger logger = LoggerFactory.getLogger(ApplyZip.class);

    private static final String DESCRIPTION = "Download, decompress and place contents of zip %s file at %s";

    private URL zipFileUrl;

    public ApplyZip() {
    }

    /**
     * Operation to apply the contents of a zip file
     *
     * @param zipFileUrl URL string to locate the zip file to be applied
     */
    public ApplyZip(String zipFileUrl) {
        try {
            checkForBlankString("Zip File URL", zipFileUrl);
            setZipFileUrl(new URL(zipFileUrl));
        } catch (MalformedURLException e) {
            throw new TransformationDefinitionException("Malformed Zip file URL", e);
        }
    }

    /**
     * Operation to apply the contents of a zip file
     *
     * @param zipFileUrl URL to locate the zip file to be applied
     */
    public ApplyZip(URL zipFileUrl) {
        setZipFileUrl(zipFileUrl);
    }

    public ApplyZip setZipFileUrl(URL zipFileUrl) {
        checkForNull("Zip File URL", zipFileUrl);
        this.zipFileUrl = zipFileUrl;
        return this;
    }

    public URL getZipFileUrl() {
        return zipFileUrl;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, zipFileUrl.getFile(), getRelativePath());
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="NP_ALWAYS_NULL_EXCEPTION")
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        // Folder where the zip file is supposed to be extracted
        File folder = getAbsoluteFile(transformedAppFolder, transformationContext);
        FileOutputStream fileOutputStream = null;
        TOExecutionResult result = null;

        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(zipFileUrl.openStream());

            int p = zipFileUrl.getPath().lastIndexOf("/") + 1;
            String fileName = zipFileUrl.getPath().substring(p);

            File zipFileDescriptor = new File(folder, fileName);
            fileOutputStream = new FileOutputStream(zipFileDescriptor);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            ZipFile zipFile = new ZipFile(zipFileDescriptor);
            zipFile.extractAll(zipFileDescriptor.getParent());
            FileUtils.deleteQuietly(zipFileDescriptor);

            String details = String.format("Zip file '%s' has been downloaded and decompressed into %s", zipFileUrl, getRelativePath(transformedAppFolder, zipFileDescriptor.getParentFile()));
            result = TOExecutionResult.success(this, details);
        } catch (ZipException|IOException e) {
            result = TOExecutionResult.error(this, e);
        } finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            }
        }

        return result;
    }

    @Override
    public ApplyZip clone() throws CloneNotSupportedException {
        ApplyZip clonedApplyZip = null;
        try {
            clonedApplyZip = (ApplyZip) super.clone();
            clonedApplyZip.zipFileUrl = new URL(this.zipFileUrl.toString());
        } catch (MalformedURLException e) {
            logger.error(String.format("Error when cloning ApplyZip %s", getName()), e);
        }
        return clonedApplyZip;
    }

}