package com.paypal.butterfly.cli;

import org.testng.annotations.Test;

import com.paypal.butterfly.api.TransformationRequest;
import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.api.Configuration;

import java.util.Properties;
import java.io.File;
import java.net.URISyntaxException;

import static org.testng.Assert.*;

/**
 * Test for ButterflyCliRunner
*/
public class ButterflyCliRunnerTest {
   
    // Test for parsing argument value for CLI option -p
    @Test
    public void testInlineProperties() throws URISyntaxException{
        String sampleAppPath = new File(this.getClass().getResource("/sample_app").toURI()).getAbsolutePath();
        ButterflyCliRun run = new ButterflyCliApp().run(sampleAppPath,"-p", "firstkey=firstvalue;secondkey=secondvalue");
        Properties properties = run.getTransformationResult().getTransformationRequest().getConfiguration().getProperties(); 
        assertEquals(run.getExitStatus(), 0);
        assertEquals(run.getInputArguments(), new String[]{sampleAppPath, "-p","firstkey=firstvalue;secondkey=secondvalue"});
        assertEquals(properties.get("firstkey"),"firstvalue");
        assertEquals(properties.get("secondkey"),"secondvalue");

    }

    // Test for parsing argument value for CLI option -p
    @Test
    public void testInlinePropertiesWithEscapedSemicolon() throws URISyntaxException{
        // Test with escaped ';' in the value
        String sampleAppPath = new File(this.getClass().getResource("/sample_app").toURI()).getAbsolutePath();
        ButterflyCliRun run = new ButterflyCliApp().run(sampleAppPath,"-p", "firstkey=first\\;value;secondkey=second\\;value");
        Properties properties = run.getTransformationResult().getTransformationRequest().getConfiguration().getProperties(); 
        assertEquals(run.getExitStatus(), 0);
        assertEquals(run.getInputArguments(), new String[]{sampleAppPath, "-p","firstkey=first\\;value;secondkey=second\\;value"});
        assertEquals(properties.get("firstkey"),"first;value");
        assertEquals(properties.get("secondkey"),"second;value");
    }

    // Test for parsing argument value for CLI option -p
    @Test
    public void testInlinePropertiesWithEscapedEqualSign() throws URISyntaxException{
        // Test with escaped '=' in the value
        String sampleAppPath = new File(this.getClass().getResource("/sample_app").toURI()).getAbsolutePath();
        ButterflyCliRun run = new ButterflyCliApp().run(sampleAppPath,"-p", "firstkey=first\\=value;secondkey=second\\=value");
        Properties properties = run.getTransformationResult().getTransformationRequest().getConfiguration().getProperties(); 
        assertEquals(run.getExitStatus(), 0);
        assertEquals(run.getInputArguments(), new String[]{sampleAppPath, "-p","firstkey=first\\=value;secondkey=second\\=value"});
        assertEquals(properties.get("firstkey"),"first=value");
        assertEquals(properties.get("secondkey"),"second=value");
    }
}
