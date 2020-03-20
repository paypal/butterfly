package com.paypal.butterfly.cli;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;

/**
 * Helper class used by integration tests to generate
 * temporary files usually used as baseline for comparisons.
 * These files are created based on a template file and values
 * to be interpolated over the template.
 *
 * @author facarvalho
 */
abstract class SimpleInterpolator {

    /**
     * Given a template file, and a map, returns a new temporary file. Its content is generated out of the template
     * and interpolated values based on the map object, whose keys are characters used as placeholders (having % as predecessor) in the template file.
     *
     * @param templateFile the template file, containing content to be preserved, plus interpolation placeholders, marked as % followed by one character
     * @param values a map whose keys are interpolation placeholder, and values are the values to be placed in the generated file
     * @return the generated file
     * @throws IOException if anything goes wrong when reading template file, or creating and writing the interpolated file
     */
    static File generate(File templateFile, Map<Character, String> values) throws IOException {
        File baseline = Files.createTempFile("butterfly-interpolated-file-", null, new FileAttribute[]{}).toFile();

        try (Reader templateReader = new BufferedReader(new FileReader(templateFile));
             Writer baselineWriter = new BufferedWriter(new FileWriter(baseline))
        ) {
            int b;
            boolean token = false;
            while ((b = templateReader.read()) != -1) {
                char c = (char) b;
                if (!token && c != '%') {
                    baselineWriter.write(c);
                } else if (!token && c == '%') {
                    token = true;
                } else if (token && values.containsKey(c)) {
                    baselineWriter.write(values.get(c));
                    token = false;
                }
            }
        }

        return baseline;
    }

}
