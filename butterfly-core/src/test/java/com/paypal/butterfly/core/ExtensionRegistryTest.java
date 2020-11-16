package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.springboot.ButterflySpringBootExtension;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * ExtensionRegistry Test
 *
 * Created by vkuncham on 11/1/2016.
 */
public class ExtensionRegistryTest {

    @Test
    public void testValidExtensionRegistry() {
        ExtensionRegistry extensionRegistry = new ExtensionRegistry();
        List<String> extensionNames = getExtensions(extensionRegistry);
        assertEquals(extensionNames.size(), 1);
        assertThat(extensionNames.get(0), is(ButterflySpringBootExtension.class.getName()));
    }

    @Test
    public void testSpringBoot() throws IOException {
        ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();

        URL url = findJar("spring-boot-uber-lib.jar");
        ClassLoader springBootLoader = SpringBootClassLoaderFactory.create(url);
        Thread.currentThread().setContextClassLoader(springBootLoader);

        try {
            ExtensionRegistry extensionRegistry = new ExtensionRegistry();
            List<String> extensionNames = getExtensions(extensionRegistry);

            List<String> expectedExtensionNames = Arrays.asList(ButterflySpringBootExtension.class.getName(),
                    "com.paypal.butterfly.test.springboot.UpgradeAppExtension",
                    "com.paypal.butterfly.test.springboot.UpgradeLibraryExtension");
            assertThat(extensionNames, is(expectedExtensionNames));

        } finally {
            Thread.currentThread().setContextClassLoader(originalContextLoader);
        }
    }

    private URL findJar(String jarName) throws IOException {
        Path root = Paths.get("..").toAbsolutePath().normalize();
        System.out.println("search root: " + root);
        try (Stream<Path> stream = Files.walk(root)) {
            Path jarPath = stream.filter(path -> path.endsWith(jarName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find " + jarName + " in " + root));
            return jarPath.toUri().toURL();
        }
    }

    private List<String> getExtensions(ExtensionRegistry extensionRegistry) {
        return extensionRegistry.getExtensions()
                .stream()
                .filter(extension -> isNotEmpty(extension.getDescription()))
                .filter(extension -> isNotEmpty(extension.getVersion()))
                .map(obj -> obj.getClass().getName())
                .collect(toList());
    }
}
