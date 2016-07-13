package com.paypal.butterfly.basic.operations.file;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;
import java.net.URL;

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
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     */
    private ApplyZip(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to apply the contents of a zip file
     *
     * @see {@link #setRelativePath(String)}
     * @param relativePath
     *
     * @param zipFileUrl URL to locate the zip file to be applied
     */
    public ApplyZip(String relativePath, URL zipFileUrl) {
        this(relativePath);
        this.zipFileUrl = zipFileUrl;
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
        // TODO

        return null;
    }

}