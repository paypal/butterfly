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
    public String readLineKeepEol() throws IOException {
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
     * Sometimes it might be preferable to read a text file line by line, but keeping the EOL character(s)
     * in the beginning of the next line, instead of the in the end of the previous one.
     * This method reads a line of text preserving end-of-line (EOL) characters in the beginning of the line.
     * A line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'),
     * or a carriage return followed immediately by a linefeed.
     *
     * @return A String containing the contents of the line, including
     *         any EOL characters in the beginning of the line (unless the returned line is the first),
     *         or null if the end of the stream has been reached
     * @throws IOException if an I/O error occurs
     */
    public String readLineKeepStartEol() throws IOException {
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
     * See {@link Reader#close()}.
     *
     * @throws IOException  If an I/O error occurs
     */
    public void close() throws IOException {
        reader.close();
    }

}