package com.test;


import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.utilities.Abort;
import com.paypal.butterfly.utilities.conditions.FileExists;
import com.paypal.butterfly.utilities.file.LoadFile;
import com.paypal.butterfly.utilities.operations.file.ApplyFile;
import com.paypal.butterfly.utilities.operations.file.DeleteFile;
import com.paypal.butterfly.utilities.operations.file.MoveFile;
import com.paypal.butterfly.utilities.operations.pom.*;

import java.net.URL;

/**
 * Sample transformation template
 *
 * @author facarvalho
 */
public class SampleTransformationTemplate extends TransformationTemplate {

    public SampleTransformationTemplate() {

        // Checking first it the app has a root pom.xml file,
        // and aborting if not
        final String pomFileExists = add(new FileExists().relative("pom.xml"));
        add(new Abort("This application does not have a root pom.xml file").executeUnless(pomFileExists));

        // Misc changes in pom.xml
        add(new PomChangePackaging("jar").relative("pom.xml"));
        add(new PomAddParent("org.springframework.boot", "spring-boot-starter-parent", "1.5.6.RELEASE").relative("pom.xml"));
        add(new PomAddPlugin("org.springframework.boot", "spring-boot-maven-plugin").relative("pom.xml"));
        add(new PomRemoveProperty("spring.version").relative("pom.xml"));

        // Removing unnecessary dependencies in pom.xml
        add(new PomRemoveDependency("org.springframework", "spring-context").relative("pom.xml"));
        add(new PomRemoveDependency("org.springframework", "spring-web").relative("pom.xml"));
        add(new PomRemoveDependency("org.jboss.resteasy", "resteasy-servlet-initializer").relative("pom.xml"));
        add(new PomRemoveDependency("org.jboss.resteasy", "resteasy-jackson2-provider").relative("pom.xml"));
        add(new PomRemoveDependency("org.jboss.resteasy", "resteasy-spring").relative("pom.xml"));

        // Adding Spring Boot starter dependencies in pom.xml
        add(new PomAddDependency("org.springframework.boot", "spring-boot-starter-web").relative("pom.xml"));
        add(new PomAddDependency("com.paypal.springboot", "resteasy-spring-boot-starter", "2.3.3-RELEASE", "runtime").relative("pom.xml"));

        // Removing webapp folder
        add(new DeleteFile().relative("/src/main/webapp"));

        // Adding Spring Boot entry point class
        URL javaFileUrl = this.getClass().getResource("/Application.java");
        final String applicationFile = add(new ApplyFile(javaFileUrl).relative("/src/main/java/com/sample/app"));
    }

    @Override
    public Class<? extends Extension> getExtensionClass() {
        return SampleExtension.class;
    }

    @Override
    public String getDescription() {
        return "Sample transformation template";
    }

}
