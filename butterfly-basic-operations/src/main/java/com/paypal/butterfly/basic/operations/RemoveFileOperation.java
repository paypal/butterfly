package com.paypal.butterfly.basic.operations;

import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class RemoveFileOperation extends TransformationOperation<RemoveFileOperation> {

    @Override
    public String getDescription() {
        return "Remove file " + getRelativePath();
    }

    @Override
    protected String execution(File transformedAppFolder) {
        File fileToBeRemoved = new File(transformedAppFolder, getRelativePath());
        FileUtils.deleteQuietly(fileToBeRemoved);

        return "File " + getRelativePath() + " has been removed";
    }

}