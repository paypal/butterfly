/*******************************************************************************
 * Copyright (c) 2014 eBay Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.ostara.cmd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FileUtils {

   public static String getTempIODir() {
      return getTempIODir(false);
   }

   public static String getTempIODir(boolean createFolder) {
      String folderPath = System.getProperty("java.io.tmpdir") + "/" + System.currentTimeMillis();
      if (createFolder) {
         new File(folderPath).mkdirs();
      }
      return folderPath;
   }

   public static void copy(InputStream is, OutputStream os) throws IOException {
      byte[] content = new byte[4096];

      try {
         while (true) {
            int size = is.read(content);

            if (size == -1) {
               break;
            } else {
               os.write(content, 0, size);
            }
         }
      } finally {
         try{is.close();} catch(Throwable e){};
         try{os.close();} catch(Throwable e){};
      }
   }

   public static void writeFile(String fileName, String text, String characterEncoding)
         throws UnsupportedEncodingException, FileNotFoundException, IOException {
      FileOutputStream fos;
      byte[] buf = text.getBytes(characterEncoding);
      int numWritten;
      fos = new FileOutputStream(fileName);
      numWritten = 0;
      fos.write(buf, numWritten, buf.length);
      fos.close();
   }

   public static String readFile(String fileName, String characterEncoding) throws UnsupportedEncodingException,
         FileNotFoundException, IOException {
      FileInputStream fis;
      InputStreamReader isr;
      StringBuilder sb = new StringBuilder(75000);
      char[] buf = new char[4096];
      int numRead;
      fis = new FileInputStream(fileName);
      isr = new InputStreamReader(fis, characterEncoding);
      do {
         numRead = isr.read(buf, 0, buf.length);
         if (numRead > 0) {
            sb.append(buf, 0, numRead);
         }
      } while (numRead >= 0);
      isr.close();
      fis.close();
      return sb.toString();
   }

   /** This does a binary file copy
    */
   public static void copyFile(final String sourceFile, final String destinationFile) throws IOException {
      final FileInputStream fis = new FileInputStream(sourceFile);
      final FileOutputStream fos = new FileOutputStream(destinationFile);
      try {
         final byte[] buffer = new byte[4096];
         int numBytesRead;
         do {
            numBytesRead = fis.read(buffer);
            if (numBytesRead > 0) {
               fos.write(buffer, 0, numBytesRead);
            }
         } while (numBytesRead >= 0);
      } finally {
         try {
            fos.flush();
         } finally {
            try {
               fos.close();
            } finally {
               fis.close();
            }
         }
      }
   }

   /** This is a convenience method thread reads an input stream into a
    * List<String>
    *
    * @param inputStream
    * @return
    * @throws IOException
    */
   public static List<String> readLines(final InputStream inputStream) throws IOException {
      final String text = readStream(inputStream);
      StringTokenizer tokenizer = new StringTokenizer(text, "\n\r");

      List<String> list = new ArrayList<String>();

      while (tokenizer.hasMoreElements()) {
         final String line = tokenizer.nextToken();
         list.add(line);
      }
      return list;
   }
   
   public static String readStream(final InputStream inputStream, boolean autoClose) throws IOException {
      try {
         return readStream(inputStream);
      } finally {
         if(autoClose) {
            try {inputStream.close();} catch(Throwable e){};
         }
      }
   }

   /** This is a convienence method to read text from an input stream into a
    * string.  It will use the default encoding of the OS.
    * It call the underlying readString(InputStreamReader)
    *
    * @param inputStream - InputStream
    * @return String - the text that was read in
    * @throws IOException
    */
   public static String readStream(final InputStream inputStream) throws IOException {
      final InputStreamReader isr = new InputStreamReader(inputStream);
      try {
         return readStream(isr);
      } finally {
         isr.close();
      }
   }

   /** This is a convienence method to read text from a stream into a string.
    * The transfer buffer is 4k and the initial string buffer is 75k.  If
    * this causes a problem, write your own routine.
    *
    * @param isr - InputStreamReader
    * @return String - the text that was read in
    * @throws IOException
    */
   public static String readStream(final InputStreamReader isr) throws IOException {
      StringBuilder sb = new StringBuilder(75000);
      char[] buf = new char[4096];
      int numRead;
      do {
         numRead = isr.read(buf, 0, buf.length);
         if (numRead > 0) {
            sb.append(buf, 0, numRead);
         }
      } while (numRead >= 0);
      final String result = sb.toString();
      return result;
   }

   /**
    * Answers the String value of the specified resourceName.  The resourceName
    * is looked for in the calling methods Class space.
    * @param resourceName
    * @return String value of the resource
    * @throws IOException
    */
   // public static String getResourceAsString(String resourceName)
   //       throws IOException
   // {
   //       return getResourceAsString(CallerIntrospector.getCaller(), resourceName) ;
   // }

   /**
    * Answers the String value of the specified resourceName.  The resourceName
    * is looked for in the calling methods Class space.  If an IOException
    * occurs, null is returned.
    * @param resourceName
    * @return String value of the resource
    */
   // public static String getResourceString(String resourceName) {
   //       return getResourceString(CallerIntrospector.getCaller(), resourceName) ;
   // }

   // public static String getResourceString(Class<?> clz, String resourceName) {
   //       try {
   //          return getResourceAsString(clz, resourceName) ;
   //       }
   //       catch(IOException e) {
   //          return null ;
   //       }
   // }

   // public static String getResourceAsString(Class<?> clz, String resourceName)
   //       throws IOException
   // {
   //       final InputStream is = clz.getResourceAsStream(resourceName) ;
   //       final byte bytes[] = new byte[4096] ;
   //       final RopeBuffer buffer = new RopeBuffer() ;
   //       while(true) {
   //          int bytesRead = is.read(bytes) ;
   //          String thisChunk = new String(bytes, 0, bytesRead) ;
   //          buffer.append(thisChunk) ;
   //          if (bytesRead < 4096) {
   //             break ;
   //          }
   //       }
   //       return buffer.toString() ;
   // }

   /** This is a convienence method to read text from a stream into a string.
    * The transfer buffer is 4k and the initial string buffer is 75k.  If
    * this causes a problem, write your own routine.
    *
    * @param isr - InputStreamReader
    * @return String - the text that was read in
    * @throws IOException
    */
   public static boolean isDirectoryWithFiles(String directory) {
      if (directory == null || directory.trim().length() == 0)
         return false;

      File dir = new File(directory);
      if (dir.isDirectory()) {
         String[] files = dir.list();
         if (files != null && files.length > 0)
            return true;
      }
      return false;
   }

   //
   // Helper Class(es)
   //
   private final static class CallerIntrospector extends SecurityManager {
      static CallerIntrospector instance = new CallerIntrospector();

      static Class<?> getCaller() {
         return instance.getClassContext()[2];
      }
   }
}
