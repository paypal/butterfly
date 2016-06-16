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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class ProcessCall implements Callable<ProcessCallOutput> {

   private final Process process;

   public ProcessCall(Process process) {
      this.process = process;
   }

   public ProcessCallOutput call() throws IOException, InterruptedException {
      ProcessCallOutput processOutput = new ProcessCallOutput();
      StringBuilder outputBuffer = new StringBuilder(512);
      StringBuilder errOutputBuffer = new StringBuilder(512);
      BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader errin = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      String line = null;

      try {
         while ((line = in.readLine()) != null) {
            outputBuffer.append(line + "\n");
         }
         while ((line = errin.readLine()) != null) {
            errOutputBuffer.append(line + "\n");
         }
         processOutput.exitValue = process.waitFor();
      } finally {
         process.getInputStream().close();
         process.getOutputStream().close();
         process.getErrorStream().close();
         in.close();
         errin.close();
      }
      processOutput.output = outputBuffer.toString();
      processOutput.errOutput = errOutputBuffer.toString();
      return processOutput;
   }
}