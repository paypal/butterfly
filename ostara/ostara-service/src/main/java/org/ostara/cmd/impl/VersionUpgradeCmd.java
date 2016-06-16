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

import org.ostara.cmd.BaseCommand;
import org.ostara.cmd.CmdResult;
import org.ostara.cmd.ICmdResult;
import org.ostara.cmd.annotation.Command;
import org.ostara.cmd.annotation.InParameter;
import org.ostara.cmd.util.FileUtils;

/**
 * Sample command showcasing a minimal transformation on a Maven POM file.
 */
@Command(description = "Platform version Upgrade Command")
public class VersionUpgradeCmd extends BaseCommand {
   @InParameter(name = "execDir", requried = true)
   String m_execDir;

   @InParameter(name = "upgradeVersion", requried = true)
   private String m_newVersion;

   @InParameter(name = "parentPomPath", requried = true)
   private String m_parentPomPath;

   public VersionUpgradeCmd(String name) {
      super(name);
   }

   @Override
   public ICmdResult execute() {
	   // TODO Pass UTF-8 encoding to ostara-upgrade
	   // cmd.add("-Dfile.encoding=UTF-8"); // Make sure we don't mess up special characters in files
	   
      CmdResult result = new CmdResult();
      try {
         String content = FileUtils.readFile(m_execDir + "/" + m_parentPomPath, "utf-8");

         int pos = content.indexOf("<artifactId>PlatformParent</artifactId>");
         if (pos == -1) {
            pos = content.indexOf("<artifactId>Platform</artifactId>");
            if (pos == -1) {
               throw new RuntimeException("Can't find the platform parent from pom:" + m_parentPomPath);
            }
         }

         int start = content.indexOf("<version>", pos + 1);
         int end = content.indexOf("</version>", start + 1);

         String newContent = content.substring(0, start + "<version>".length()) + m_newVersion + content.substring(end);

         FileUtils.writeFile(m_execDir + "/" + m_parentPomPath, newContent, "utf-8");
      } catch (Exception e) {
         result.setException(e);
      }

      return result;
   }

   @Override
   public String toString() {
      return "VersionUpdateTask [upgradedVersion=" + m_newVersion + ", parentPomPath=" + m_parentPomPath + "]";
   }
}
