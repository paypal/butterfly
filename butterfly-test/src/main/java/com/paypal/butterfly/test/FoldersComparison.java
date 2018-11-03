package com.paypal.butterfly.test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;

import com.google.common.io.Files;

/**
 * Utility class to compare two folders and their contents
 *
 * @author facarvalho
 */
class FoldersComparison {

    private static DocumentBuilder builder;
    private static ParserConfigurationException xmlParserConfigurationException;

    static {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            xmlParserConfigurationException = e;
        }
    }

    /**
     * Compares actual folder against baseline folder,
     * producing an error if they are not equal
     *
     * @param expected folder used as baseline for comparison
     * @param actual folder to be evaluated during comparison
     * @param xmlSemanticComparison if true XML files are compared semantically
     *                              (ignoring formatting, comments and indentation),
     *                              as opposed to byte by byte
     */
    static void assertEquals(File expected, File actual, boolean xmlSemanticComparison) {
        TreeSet<String> missing = new TreeSet<>();
        TreeSet<String> unexpected = new TreeSet<>();
        TreeSet<String> different = new TreeSet<>();
        FoldersComparison.assertEqualFolderStructure(expected, expected, actual, missing, unexpected);
        try {
            FoldersComparison.assertEqualFolderContent(expected, expected, actual, xmlSemanticComparison, different);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        if (!missing.isEmpty() || !unexpected.isEmpty() || !different.isEmpty()) {
            fail(missing, unexpected, different);
        }
    }

    private static void fail(TreeSet<String> missing, TreeSet<String> unexpected, TreeSet<String> different) {
        StringBuilder failureMessage = new StringBuilder("Baseline and transformed applications don't match, as detailed below:\n");

        if(!missing.isEmpty()) {
            failureMessage.append("\nMissing in transformed application:\n");
            Iterator<String> i = missing.iterator();
            while (i.hasNext()) failureMessage.append("\t" + i.next() + "\n");
        }
        if(!unexpected.isEmpty()) {
            failureMessage.append("\nUnexpectedly found in transformed application:\n");
            Iterator<String> i = unexpected.iterator();
            while (i.hasNext()) failureMessage.append("\t" + i.next() + "\n");
        }
        if(!different.isEmpty()) {
            failureMessage.append("\nDifferent file content:\n");
            Iterator<String> i = different.iterator();
            while (i.hasNext()) failureMessage.append("\t" + i.next() + "\n");
        }

        throw new AssertionError(failureMessage.toString());
    }

    /*
     * Check if the expected and actual directories have the same number of files and folders, and with same names.
     * This check is done recursively and do not evaluate file contents, only name and relative paths.
     *
     * @param baselineApplicationDir the same as the very first expectedDir, which is carried over through the recursive calls
     * to allow retrieving the relative path to all files and folders (just for error messages purposes)
     * @param expectedDir expected directory for comparison purposes
     * @param actualDir actual directory, whose structure is expected to be equals to the expected one
     * @param missing alphabetically ordered set of missing files and folders
     */
    private static void assertEqualFolderStructure(File baselineApplicationDir, File expectedDir, File actualDir, TreeSet<String> missing, TreeSet<String> unexpected) {

        // All direct files and folders inside `expected`
        File[] expectedFiles = expectedDir.listFiles();

        // All direct folders inside `expected`
        Set<String> expectedDirectories = new HashSet<>();

        // All direct files inside `expected`
        Set<String> expectedNonDirectories = new HashSet<>();

        String fileRelativePath;
        for (File expectedFile : expectedFiles) {
            fileRelativePath = getRelativePath(expectedFile, expectedDir);
            if (expectedFile.isDirectory()) {
                expectedDirectories.add(fileRelativePath);
            } else {
                expectedNonDirectories.add(fileRelativePath);
            }
        }

        File[] actualFiles = actualDir.listFiles();

        for (File actualFile : actualFiles) {
            fileRelativePath = getRelativePath(actualFile, actualDir);
            if (actualFile.isDirectory()) {
                if (!expectedDirectories.contains(fileRelativePath)) {
                    unexpected.add(getRelativePath(expectedDir, baselineApplicationDir) + fileRelativePath + " <dir>");
                } else {
                    expectedDirectories.remove(fileRelativePath);
                }
            } else {
                if (!expectedNonDirectories.contains(fileRelativePath)) {
                    unexpected.add(getRelativePath(expectedDir, baselineApplicationDir) + fileRelativePath);
                } else {
                    expectedNonDirectories.remove(fileRelativePath);
                }
            }
        }

        if(!expectedDirectories.isEmpty()) {
            Iterator<String> i = expectedDirectories.iterator();
            while (i.hasNext()) missing.add(getRelativePath(expectedDir, baselineApplicationDir) + i.next() + " <dir>");
        }
        if(!expectedNonDirectories.isEmpty()) {
            Iterator<String> i = expectedNonDirectories.iterator();
            while (i.hasNext()) missing.add(getRelativePath(expectedDir, baselineApplicationDir) + i.next());
        }

        // If code reach this point, it means expected and actual have same structure,
        // then now it is time to compare the structure of their respective folders recursively
        Arrays.stream(expectedDir.listFiles(pathName -> pathName.isDirectory()))
                .filter(expDir -> new File(actualDir, expDir.getName()).exists())
                .forEach(expDir -> assertEqualFolderStructure(baselineApplicationDir, expDir, new File(actualDir, expDir.getName()), missing, unexpected));
    }

    /*
     * Given a file and a supposed parent, return the relative path from
     * the parent file to the child file
     */
    private static String getRelativePath(File file, File parent) {
        String filePath = file.getAbsolutePath();
        String parentPath = parent.getAbsolutePath();

        if (filePath.equals(parentPath)) {
            return "";
        }
        if (filePath.equals(parentPath) || !filePath.startsWith(parentPath + File.separatorChar)) {
            throw new IllegalArgumentException("File " + file + " is not a direct nor indirect child of " + parent);
        }

        return filePath.substring(parentPath.length(), filePath.length());
    }

    /*
     * Compare every file under the expected and actual folders and sub-folders making sure their content is the same.
     * This method assumes the expected and actual folders are structurally identical, meaning, they have same number
     * of files and folders and they are named the same
     */
    private static void assertEqualFolderContent(File baselineApplication, File expected, File actual, boolean xmlSemanticComparison, TreeSet<String> different) throws IOException {
        for (File expectedFile : expected.listFiles()) {
            File actualFile = new File(actual, expectedFile.getName());
            if (expectedFile.isDirectory() && actualFile.exists() && actualFile.isDirectory()) {
                assertEqualFolderContent(baselineApplication, expectedFile, actualFile, xmlSemanticComparison, different);
            } else if (expectedFile.isFile() && actualFile.exists() && actualFile.isFile()) {
                boolean equal;
                if (xmlSemanticComparison && expectedFile.getName().endsWith(".xml")) {
                    equal = xmlEqual(expectedFile, actualFile);
                } else {
                    equal = Files.equal(expectedFile, actualFile);
                }
                if(!equal) {
                    different.add(getRelativePath(expectedFile, baselineApplication));
                }
            }
        }
    }

    /*
     * Returns true only if both XML files have same content
     */
    private static boolean xmlEqual(File expectedFile, File actualFile) throws IOException {
        if (xmlParserConfigurationException != null) {
            throw new IllegalStateException("XML parser could not be configured", xmlParserConfigurationException);
        }

        boolean file1parsed = false;
        boolean file2parsed = false;

        try {
            Document file1Xml = builder.parse(expectedFile);
            file1parsed = true;
            Document file2Xml = builder.parse(actualFile);
            file2parsed = true;

            file1Xml.normalizeDocument();
            file2Xml.normalizeDocument();

            XMLUnit.setIgnoreAttributeOrder(true);
            XMLUnit.setIgnoreComments(true);
            XMLUnit.setIgnoreWhitespace(true);

            return XMLUnit.compareXML(file1Xml, file2Xml).similar();
        } catch (Exception e) {
            if (file1parsed ^ file2parsed) {
                // This means only one file couldn't be parsed, which means they are not equal
                return false;
            }
            // This means both files couldn't be parsed, so this comparison is being delegated to
            // a regular file comparison
            return Files.equal(expectedFile, actualFile);
        }
    }

}
