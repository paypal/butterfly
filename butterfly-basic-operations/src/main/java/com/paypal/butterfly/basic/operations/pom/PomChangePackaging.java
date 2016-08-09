package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Operation to change the packaging of a Maven artifact, by changing its POM file
 *
 * @author facarvalho
 */
public class PomChangePackaging extends TransformationOperation<PomChangePackaging> {

    private static final String DESCRIPTION = "Change packaging to %s in POM file %s";

    private String packagingType;

    public PomChangePackaging() {
    }

    /**
     * Operation to change the packaging of a Maven artifact, by changing its POM file
     *
     * @param packagingType packaging type
     */
    public PomChangePackaging(String packagingType) {
        this.packagingType = packagingType;
    }

    public PomChangePackaging setPackagingType(String packagingType) {
        this.packagingType = packagingType;
        return this;
    }

    public String getPackagingType() {
        return packagingType;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, packagingType, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {

        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);

        MavenXpp3Reader reader = new MavenXpp3Reader();

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(pomFile);


            Model model = reader.read(fileInputStream);

            model.setPackaging(packagingType);

            MavenXpp3Writer writer = new MavenXpp3Writer();

            fileOutputStream = new FileOutputStream(pomFile);
            writer.write(fileOutputStream, model);
        }finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
            }finally {
                if(fileOutputStream != null) fileOutputStream.close();
            }
        }

        return String.format("Packaging for POM file %s has been changed to %s",getRelativePath(), packagingType);
    }


}

