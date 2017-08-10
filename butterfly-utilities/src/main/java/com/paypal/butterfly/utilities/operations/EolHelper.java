package com.paypal.butterfly.utilities.operations;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.*;

/**
 * This helper class has utility methods to deal with text files EOL characters.
 *
 * @author facarvalho
 */
public abstract class EolHelper {

    /**
     * Finds out what EOL character(s) are used by the specified text file.
     * If the specified file has no EOL characters null will be returned, and if more than
     * one type of EOL character(s) are used, the very first EOL occurrence will be returned.
     *
     * @param textFile file to be analyzed based on its EOL character(s)
     * @return  the very first occurrence of EOL used in the specified text file, or null,
     *          if none is found
     * @throws IOException if any IO exception happens when opening and reading the text file
     */
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    public static String findEol(File textFile) throws IOException {
        if (textFile == null) {
            throw new IllegalArgumentException("Text file object cannot be null");
        }
        if (!textFile.isFile()) {
            throw new IllegalArgumentException("Text file is not a file");
        }
        EolBufferedReader eolBufferedReader = null;
        try {
            eolBufferedReader = new EolBufferedReader(new BufferedReader(new FileReader(textFile)));
            String line = eolBufferedReader.readLineKeepEol();
            return getEndEol(line);
        } finally {
            if (eolBufferedReader != null) eolBufferedReader.close();
        }
    }

    /**
     * Finds out what EOL character(s) are used by the specified text file.
     * If the specified file has no EOL characters the default OS EOL character(s) will be returned, and if more than
     * one type of EOL character(s) are used, the very first EOL occurrence will be returned.
     *
     * @param textFile file to be analyzed based on its EOL character(s)
     * @return  the very first occurrence of EOL used in the specified text file, or the default OS EOL character(s),
     *          if none is found
     * @throws IOException if any IO exception happens when opening and reading the text file
     */
    public static String findEolDefaultToOs(File textFile) throws IOException {
        String eol = findEol(textFile);
        return (eol == null ? System.lineSeparator() : eol);
    }

    /**
     * Return a new String like the provided one but without any end-of-line (EOL) character.
     * EOL characters can be a line feed ('\n'), a carriage return ('\r'),
     * or a carriage return followed immediately by a linefeed. If the provided
     * String has none it is returned as is.
     *
     * @param line the String whose EOL characters should be removed from
     * @return a new String like the provided one but without any EOL character
     *
     * @throws IllegalArgumentException if {@code line} is null
     */
    public static String removeEol(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line argument cannot be null");
        }
        return line.replaceAll("(\\n)|(\\r)|(\\r\\n)", "");
    }

    /**
     * Returns true only if {@code line} starts with any end-of-line (EOL) character.
     * EOL characters can be a line feed ('\n'), a carriage return ('\r'),
     * or a carriage return followed immediately by a linefeed. If the provided
     * String has none it is returned as is.
     *
     * @param line the text line to be evaluated
     * @return true only if {@code line} starts with any end-of-line (EOL) character
     * @throws IllegalArgumentException if {@code line} is null
     */
    public static boolean startsWithEol(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line argument cannot be null");
        }
        return line.startsWith("\n") || line.startsWith("\r");
    }

    /**
     * Returns true only if {@code line} ends with any end-of-line (EOL) character.
     * EOL characters can be a line feed ('\n'), a carriage return ('\r'),
     * or a carriage return followed immediately by a linefeed. If the provided
     * String has none it is returned as is.
     *
     * @param line the text line to be evaluated
     * @return true only if {@code line} ends with any end-of-line (EOL) character
     * @throws IllegalArgumentException if {@code line} is null
     */
    public static boolean endsWithEol(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line argument cannot be null");
        }
        return line.endsWith("\n") || line.endsWith("\r");
    }

    /**
     * Returns end-of-line (EOL) character(s) present in the beginning of this line of text.
     * If there is none, null is returned.
     * EOL characters can be a line feed ('\n'), a carriage return ('\r'),
     * or a carriage return followed immediately by a linefeed. If the provided
     * String has none it is returned as is.
     *
     * @param line the text line to be evaluated
     * @return end-of-line (EOL) character(s) present in the beginning of this line of text
     * @throws IllegalArgumentException if {@code line} is null
     */
    public static String getStartEol(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line argument cannot be null");
        }
        if (line.startsWith("\n")) return "\n";
        if (line.startsWith("\r\n")) return "\r\n";
        if (line.startsWith("\r")) return "\r";
        return null;
    }

    /**
     * Returns end-of-line (EOL) character(s) present in the end of this line of text.
     * If there is none, null is returned.
     * EOL characters can be a line feed ('\n'), a carriage return ('\r'),
     * or a carriage return followed immediately by a linefeed. If the provided
     * String has none it is returned as is.
     *
     * @param line the text line to be evaluated
     * @return end-of-line (EOL) character(s) present in the end of this line of text
     * @throws IllegalArgumentException if {@code line} is null
     */
    public static String getEndEol(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line argument cannot be null");
        }
        if (line.endsWith("\r\n")) return "\r\n";
        if (line.endsWith("\n")) return "\n";
        if (line.endsWith("\r")) return "\r";
        return null;
    }

}
