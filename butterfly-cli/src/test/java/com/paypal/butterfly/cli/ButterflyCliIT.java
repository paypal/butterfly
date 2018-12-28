package com.paypal.butterfly.cli;

import org.testng.annotations.Test;

import java.io.*;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

public class ButterflyCliIT {

    @Test
    public void test() throws IOException, URISyntaxException {
        // First run to flush out logback log statements
        assertEquals(new ButterflyCliApp().run().getExitStatus(), 0);

        PrintStream systemOut = System.out;

        File helpOut = File.createTempFile("butterfly-cli-help-output", null);
        PrintStream helpStream = new PrintStream(helpOut);
        System.setOut(helpStream);

        assertEquals(new ButterflyCliApp().run().getExitStatus(), 0);
        assertEquals(new ButterflyCliApp().run("-h").getExitStatus(), 0);
        assertEquals(new ButterflyCliApp().run("-?").getExitStatus(), 0);

        helpStream.close();

        File helpBaselineOut = new File(this.getClass().getResource("/helpOut.txt").toURI());

// TODO
// This would fail because of the version print in the console.
// The solution is to do a static mock of ButterflyProperties.getString("butterfly.version")
//        assertTrue(FileUtils.contentEquals(helpBaselineOut, helpOut), printInvalidOutput(helpOut));

        File listOut = File.createTempFile("butterfly-cli-list-output", null);
        PrintStream listStream = new PrintStream(listOut);
        System.setOut(listStream);

        ButterflyCliRun run = new ButterflyCliApp().run("-l");
        assertEquals(run.getExitStatus(), 0);

        listStream.close();
        System.setOut(systemOut);

        File listBaselineOut = new File(this.getClass().getResource("/extensionsListOut.txt").toURI());
// TODO
// This would fail because of the version print in the console.
// The solution is to do a static mock of ButterflyProperties.getString("butterfly.version")
//        assertTrue(FileUtils.contentEquals(listBaselineOut, listOut), printInvalidOutput(listOut));
    }

    private String printInvalidOutput(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("Output file " + file.getAbsolutePath() + " is invalid:\n\n[");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        }
        int i = stringBuilder.length();
        stringBuilder.replace(i -1, i, "]");

        return stringBuilder.toString();
    }

}