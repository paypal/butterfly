package com.paypal.butterfly.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Factory creating ClassLoaders that can read spring-boot uber jars
 */
public class SpringBootClassLoaderFactory {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootClassLoaderFactory.class);

    public static ClassLoader create(URL url) {
        try {
            NoOpJarLauncher noOpJarLauncher = new NoOpJarLauncher(url);
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

        private final URL url;
        private ClassLoader classLoader;

        public NoOpJarLauncher(URL url) throws IOException {
            super(newArchive(url));
            this.url = url;
        }

        @Override
        public void launch(String[] args) throws Exception {
            super.launch(args);
        }

        @Override
        public void launch(String[] args, String launchClass, ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        protected String getMainClass() {
            return "";
        }

        @Override
        protected ClassLoader createClassLoader(URL[] urls) throws Exception {
            List<URL> urlList = new ArrayList<>(Arrays.asList(urls));
            urlList.add(url);
            return super.createClassLoader(urlList.toArray(new URL[0]));
        }
    }
}

