package com.paypal.butterfly.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Factory creating ClassLoaders that can read spring-boot uber jars
 */
public class SpringBootClassLoaderFactory {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootClassLoaderFactory.class);

    public static ClassLoader create(URL url) {
        try {
            Archive archive = newArchive(url);
            NoOpJarLauncher noOpJarLauncher = new NoOpJarLauncher(archive);
            noOpJarLauncher.launch(new String[0]);
            return noOpJarLauncher.classLoader;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create ClassLoader for spring-boot uber jar. url=" + url, e);
        }
    }

    public static boolean isSpringBootUberJar(URL url) {
        try {
            Archive archive = newArchive(url);
            Manifest manifest = archive.getManifest();
            if (manifest == null) {
                return false;
            }

            Attributes mainAttributes = manifest.getMainAttributes();
            return mainAttributes.containsKey(new Attributes.Name("Spring-Boot-Lib"));
        } catch (IOException e) {
            logger.warn("Cannot detect spring-boot uber jar. url={}", url);
            return false;
        }
    }

    private static Archive newArchive(URL url) throws IOException {
        String file = url.getFile();
        return new JarFileArchive(new File(file));
    }

    private static class NoOpJarLauncher extends JarLauncher {

        private ClassLoader classLoader;

        public NoOpJarLauncher(Archive archive) {
            super(archive);
        }

        @Override
        public void launch(String[] args) throws Exception {
            super.launch(args);
        }

        @Override
        public void launch(String[] args, String launchClass, ClassLoader classLoader) throws Exception {
            this.classLoader = classLoader;
        }

        @Override
        protected String getMainClass() throws Exception {
            return "";
        }
    }
}

