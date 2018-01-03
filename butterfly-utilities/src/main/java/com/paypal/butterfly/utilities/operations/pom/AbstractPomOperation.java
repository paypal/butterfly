package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Abstract POM operation.
 *
 * @author facarvalho
 */
abstract class AbstractPomOperation<T extends AbstractPomOperation> extends TransformationOperation<T> {

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File pomFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        MavenXpp3Reader reader = new MavenXpp3Reader();
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        TOExecutionResult result = null;

        try {
            fileInputStream = new FileInputStream(pomFile);
            Model model = reader.read(fileInputStream);

            String relativePomFile = getRelativePath(transformedAppFolder, pomFile);
            result = pomExecution(relativePomFile, model);

            if (result.getType().equals(TOExecutionResult.Type.SUCCESS) || result.getType().equals(TOExecutionResult.Type.WARNING)) {
                fileOutputStream = new FileOutputStream(pomFile);
                MavenXpp3Writer writer = new MavenXpp3Writer();
                writer.write(fileOutputStream, model);
            }
        } catch (XmlPullParserException|IOException e) {
            result = TOExecutionResult.error(this, e);
        } finally {
            try {
                if (fileInputStream != null) try {
                    fileInputStream.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            } finally {
                if(fileOutputStream != null) try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    result.addWarning(e);
                }
            }
        }

        return result;
    }

    protected abstract TOExecutionResult pomExecution(String relativePomFile, Model model);

}
