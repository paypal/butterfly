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

    // Maximum number of lines to be printed in the results in case of failed comparison
    private static final int MAX_LINES = 10;

    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    static {
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
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

        printResult(missing, "Missing in transformed application", failureMessage);
        printResult(unexpected, "Unexpectedly found in transformed application", failureMessage);
        printResult(different, "Different file content", failureMessage);

        throw new AssertionError(failureMessage.toString());
    }

    private static void printResult(TreeSet<String> entries, String header, StringBuilder failureMessage) {
        if(!entries.isEmpty()) {
            failureMessage.append("\n" + header + " (" + entries.size() + "):\n");
            Iterator<String> i = entries.iterator();
            int c = MAX_LINES;
            for (; i.hasNext() && c > 0; c--) failureMessage.append("\t" + i.next() + "\n");
            int more = (entries.size() - MAX_LINES);
            if (more > 0) failureMessage.append("\t(More " + more + ")\n");
        }
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
            fileRelativePath = getRelativePath(expectedDir, expectedFile);
            if (expectedFile.isDirectory()) {
                expectedDirectories.add(fileRelativePath);
            } else {
                expectedNonDirectories.add(fileRelativePath);
            }
        }

        File[] actualFiles = actualDir.listFiles();

        for (File actualFile : actualFiles) {
            fileRelativePath = getRelativePath(actualDir, actualFile);
            if (actualFile.isDirectory()) {
                if (!expectedDirectories.contains(fileRelativePath)) {
                    unexpected.add(getRelativePath(baselineApplicationDir, expectedDir) + fileRelativePath + " <dir>");
                } else {
                    expectedDirectories.remove(fileRelativePath);
                }
            } else {
                if (!expectedNonDirectories.contains(fileRelativePath)) {
                    unexpected.add(getRelativePath(baselineApplicationDir, expectedDir) + fileRelativePath);
                } else {
                    expectedNonDirectories.remove(fileRelativePath);
                }
            }
        }

        if(!expectedDirectories.isEmpty()) {
            Iterator<String> i = expectedDirectories.iterator();
            while (i.hasNext()) missing.add(getRelativePath(baselineApplicationDir, expectedDir) + i.next() + " <dir>");
        }
        if(!expectedNonDirectories.isEmpty()) {
            Iterator<String> i = expectedNonDirectories.iterator();
            while (i.hasNext()) missing.add(getRelativePath(baselineApplicationDir, expectedDir) + i.next());
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
    private static String getRelativePath(File potentialParent, File potentialChild) {
        String parentPath = potentialParent.getAbsolutePath();
        String childPath = potentialChild.getAbsolutePath();

        if (childPath.equals(parentPath)) {
            return "";
        }
        if (childPath.equals(parentPath) || !childPath.startsWith(parentPath + File.separatorChar)) {
            throw new IllegalArgumentException("File " + potentialChild + " is not a direct nor indirect child of " + potentialParent);
        }

        return childPath.substring(parentPath.length(), childPath.length());
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
                    different.add(getRelativePath(baselineApplication, expectedFile));
                }
            }
        }
    }

    /*
     * Returns true only if both XML files have same content
     */
    private static boolean xmlEqual(File expectedFile, File actualFile) throws IOException {

        boolean file1parsed = false;
        boolean file2parsed = false;

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
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
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("XML parser could not be configured", e);
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
