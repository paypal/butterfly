package com.paypal.butterfly.utilities.operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * This specialized buffered reader reads lines from {@link BufferedReader} objects
 * preserving end-of-line (EOL) characters, which could be line feed ('\n'),
 * carriage return ('\r'), or a carriage return followed immediately by a linefeed.
 *
 * @author facarvalho
 */
public class EolBufferedReader {

    private BufferedReader reader;
    private Object monitor = new Object();

    /**
     * This specialized buffered reader reads lines from {@link BufferedReader} objects
     * preserving end-of-line (EOL) characters, which could be line feed ('\n'),
     * carriage return ('\r'), or a carriage return followed immediately by a linefeed.
     *
     * @param bufferedReader the buffered reader object to be wrapped by this specialized
     *                       reader
     */
    public EolBufferedReader(BufferedReader bufferedReader) {
        setBufferedReader(bufferedReader);
    }

    private void setBufferedReader(BufferedReader bufferedReader) {
        if (bufferedReader == null) {
            throw new IllegalArgumentException("Buffered reader object cannot be null");
        }
        reader = bufferedReader;
    }

    /**
     * Reads a line of text preserving end-of-line (EOL) characters in the end of the line.
     * A line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'),
     * or a carriage return followed immediately by a linefeed.
     *
     * @return A String containing the contents of the line, including
     *         any EOL characters in the end of the line, or null if the end of the
     *         stream has been reached
     * @throws IOException if an I/O error occurs
     */
    public String readLineKeepEndEOL() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int read;

        synchronized (monitor) {
            while(true) {
                read = reader.read();
                if (read == -1) {
                    break;
                }
                stringBuilder.append((char) read);
                if (read == '\n') {
                    break;
                }
                if (read == '\r') {
                    reader.mark(1);
                    read = reader.read();
                    reader.reset();
                    if (read != '\n') {
                        break;
                    }
                }
            }
        }

        if (stringBuilder.length() == 0) {
            return null;
        }
        return stringBuilder.toString();
    }

    /**
     * Reads a line of text preserving end-of-line (EOL) characters in the beginning of the line.
     * A line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'),
     * or a carriage return followed immediately by a linefeed.
     *
     * @return A String containing the contents of the line, including
     *         any EOL characters in the end of the line, or null if the end of the
     *         stream has been reached
     * @throws IOException if an I/O error occurs
     */
    public String readLineKeepStartEOL() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        synchronized (monitor) {
            int read = reader.read();
            if (read == -1) {
                return null;
            }
            stringBuilder.append((char) read);
            if (read == '\r') {
                read = reader.read();
                if (read == -1) {
                    return stringBuilder.toString();
                }
                stringBuilder.append((char) read);
            }
            while(true) {
                reader.mark(1);
                read = reader.read();
                if (read == -1) {
                    break;
                }
                if (read == '\n' || read == '\r') {
                    reader.reset();
                    break;
                }
                stringBuilder.append((char) read);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * @see {@link Reader#close()}
     *
     * @throws IOException
     */
    public void close() throws IOException {
        reader.close();
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
     * @throws {@link IllegalArgumentException} if {@code line} is null
     */
    public static String removeEOL(String line) {
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
     * @throws {@link IllegalArgumentException} if {@code line} is null
     */
    public static boolean startsWithEOL(String line) {
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
     * @throws {@link IllegalArgumentException} if {@code line} is null
     */
    public static boolean endsWithEOL(String line) {
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
     * @throws {@link IllegalArgumentException} if {@code line} is null
     */
    public static String getStartEOL(String line) {
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
     * @throws {@link IllegalArgumentException} if {@code line} is null
     */
    public static String getEndEOL(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line argument cannot be null");
        }
        if (line.endsWith("\r\n")) return "\r\n";
        if (line.endsWith("\n")) return "\n";
        if (line.endsWith("\r")) return "\r";
        return null;
    }

}