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
package org.ostara.cmd.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.ostara.cmd.BaseCmdLineCmd;
import org.ostara.cmd.ICmdResult;
import org.ostara.cmd.annotation.Command;
import org.ostara.cmd.annotation.InParameter;
import org.ostara.cmd.util.FileUtils;

/**
 * Pushes the local commits to the remote Git repository.
 */
@Command(description = "git push cmd")
public class GitPushCmd extends BaseCmdLineCmd {
   @InParameter(name = "gitDir", requried = true)
   String m_execDir;

   @InParameter(name = "gitBranch", requried = true)
   String m_branchName;

   @InParameter(name = "username")
   String m_userName;

   @InParameter(name = "password")
   String m_password;

   public GitPushCmd(String id) {
      super(id);
   }

   private String beautify(String value) {
      String str = (String) value;
      if (str != null) {
         int pos = str.indexOf('@');
         if (pos != -1) {
            int begin = str.lastIndexOf("//", pos);
            return str.substring(0, begin + 2) + str.substring(pos + 1);
         }
      }
      return str;
   }

   private void checkUpdateSecurity(String execDir) {
      // Put Update Git URL with user/pwd: .git/config
      // [remote "origin"]
      File config = new File(execDir, ".git/config");
      if (config.exists()) {
         try {
            String content = FileUtils.readStream(new FileInputStream(config));
            int pos = content.indexOf("\"origin\"]");
            if (pos != -1) {
               int pos2 = content.indexOf("url =", pos);
               if (pos2 != -1) {
                  StringBuilder sb = new StringBuilder(128);
                  boolean hasRevised = reviseUrl(sb, content, pos2);
                  if (hasRevised) {
                     FileUtils.writeFile(config.getPath(), sb.toString(), "utf-8");
                  }
               }
            }
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   @Override
   public ICmdResult execute() {
      // FIX: QA commit push 22 issue
      if (m_userName != null) {
         checkUpdateSecurity(m_execDir);
      }

      ICmdResult result = super.execute();
      result.setMessage(beautify(result.getMessage()));
      if (result.getException() != null) {
         //TODO:
         String message = result.getException().toString();
         if (message.contains("@")) {
            //need to beautify pwd
            result.setException(new RuntimeException((String) beautify(message)));
         }
      }

      return result;
   }

   @Override
   protected String getCmdDir() {
      return m_execDir;
   }

   @Override
   protected List<String> getCmdStrs() {
      List<String> cmd = new ArrayList<>();
      cmd.add("git");
      cmd.add("push");
      cmd.add("--porcelain");
      cmd.add("origin");
      cmd.add("refs/heads/" + m_branchName + ":refs/heads/" + m_branchName);
      return cmd;
   }

   private boolean reviseUrl(StringBuilder sb, String content, int start) {
      int end = content.indexOf("\n", start);
      if (end != -1) {
         int pos = content.indexOf("@", start);
         if (pos == -1 && pos < end) {
            sb.append(content.substring(0, start));
            String url = content.substring(start + 6, end);
            sb.append("url = https://");
            sb.append(m_userName);
            sb.append(":");
            sb.append(m_password).append('@');
            sb.append(url.substring(8));
            sb.append(content.substring(end));
            return true;
         }
      }

      return false;
   }

}
