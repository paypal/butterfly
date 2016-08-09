package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;

import java.io.*;
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
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        // Folder where the zip file is supposed to be extracted
        File folder = getAbsoluteFile(transformedAppFolder, transformationContext);

        ReadableByteChannel readableByteChannel = Channels.newChannel(zipFileUrl.openStream());

        int p = zipFileUrl.getPath().lastIndexOf("/") + 1;
        String fileName = zipFileUrl.getPath().substring(p);

        File zipFileDescriptor = new File(folder, fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(zipFileDescriptor);
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

        ZipFile zipFile = new ZipFile(zipFileDescriptor);
        zipFile.extractAll(zipFileDescriptor.getParent());
        FileUtils.deleteQuietly(zipFileDescriptor);

        return String.format("Zip file %s has been downloaded and decompressed into %s", zipFileUrl, getRelativePath(transformedAppFolder, zipFileDescriptor.getParentFile()));
    }

}