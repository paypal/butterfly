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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ostara.cmd.BaseCmdLineCmd;
import org.ostara.cmd.CmdResult;
import org.ostara.cmd.ICmdResult;
import org.ostara.cmd.annotation.Command;
import org.ostara.cmd.annotation.InParameter;
import org.ostara.cmd.annotation.OutParameter;
import org.ostara.cmd.util.FileUtils;

/**
 * TODO
 */
@Command(description = "Git release version command")
public class GitReleaseVersionCmd extends BaseCmdLineCmd {
   @InParameter(name = "gitDir", requried = true)
   String m_execDir;

   //identify whether you need to cut a new branch
   @InParameter(name = "releaseBranch")
   String m_branch;

   //the version to be updated
   @InParameter(name = "releaseVersion", requried = true)
   String m_version;

   //TODO: consider group id later
   @InParameter(name = "updatedVersions")
   @OutParameter(name = "updatedVersions")
   Map<String, String> m_updatedVersions;

   public GitReleaseVersionCmd(String name) {
      super(name);
   }

   @Override
   public ICmdResult execute() {
      //checkout branch
      if (m_branch != null) {
         //try to checkout existing branch, if it failed, try to create one
         List<String> cmd = new ArrayList<>();
         cmd.add("git");
         cmd.add("checkout");
         cmd.add(m_branch);

         CmdResult result = new CmdResult();
         executeCommand(result, cmd, m_execDir, isWait());

         if (result.getException() != null) {
            cmd = new ArrayList<>();
            cmd.add("git");
            cmd.add("checkout");
            cmd.add("-b");
            cmd.add(m_branch);

            result = new CmdResult();
            executeCommand(result, cmd, m_execDir, isWait());

            if (result.getException() != null) {
               return result;
            }
         }
      }

      //TODO: support auto incremental version

      CmdResult result = new CmdResult();
      //update version
      try {
         String filePath = new File(m_execDir, "pom.xml").getCanonicalPath();
         String content = FileUtils.readFile(filePath, "utf-8");

         //get submodules of pom
         List<String> modules = getMoudles(content);

         //get parent of the pom
         String parentPomPath = getParentRelativePath(content);

         if (parentPomPath != null) {
            updateParentProject(modules, parentPomPath);
         }

         //update module pom
         for (String moduleName : modules) {
            //skip the processed parent
            if (moduleName.equals(parentPomPath)) {
               continue;
            }

            updateModuleProject(modules, moduleName);
         }

         //update current pom
         String newContent = updatePom(modules, content);

         if (!newContent.equals(content)) {
            FileUtils.writeFile(filePath, newContent, "utf-8");
         }

         //set output artifact Id, temp to use module name
         if (m_updatedVersions == null) {
            m_updatedVersions = new HashMap<String, String>();
         }

         if (!modules.isEmpty()) {
            for (String moduleName : modules) {
               m_updatedVersions.put(moduleName, m_version);
            }
         }

         String artifactId = getArtifactId(newContent);
         if (artifactId != null) {
            m_updatedVersions.put(artifactId, m_version);
         }

         result.setMessage("OK");
      } catch (Exception e) {
         result.setException(e);
         return result;
      }

      return result;
   }

   private String getArtifactId(String newContent) {
      int pos = newContent.indexOf("</parent>");

      int start = -1;
      if (pos != -1) {
         start = newContent.indexOf("<artifactId>", pos + 1);
      } else {
         start = newContent.indexOf("<artifactId>");
      }

      if (start != -1) {
         int end = newContent.indexOf("</artifactId>", start + 1);
         if (end != -1) {
            return newContent.substring(start + "<artifactId>".length(), end);
         }
      }

      return null;
   }

   private List<String> getMoudles(String content) {
      List<String> modules = new ArrayList<String>();

      int start = content.indexOf("<modules>");
      if (start != -1) {
         int end = content.indexOf("</modules>", start);
         if (end != -1) {
            int pos = content.indexOf("<module>", start);
            while (pos != -1) {
               int pos2 = content.indexOf("</module>", pos + 1);
               if (pos2 != -1 && pos2 < end) {
                  String moduleName = content.substring(pos + "<module>".length(), pos2).trim();
                  modules.add(moduleName);
               }

               pos = content.indexOf("<module>", pos + 1);
            }
         }

      }

      return modules;
   }

   private String getParentArtifactId(String newContent) {
      int pos = newContent.indexOf("<parent>");

      if (pos != -1) {
         int start = newContent.indexOf("<artifactId>", pos + 1);
         int end = newContent.indexOf("</artifactId>", start + 1);
         return newContent.substring(start + "<artifactId>".length(), end);
      }

      return null;
   }

   private String getParentRelativePath(String content) {
      int start = content.indexOf("<parent>");
      if (start != 0) {
         int end = content.indexOf("</parent>", start + 1);
         if (end != -1) {
            int pos = content.indexOf("</relativePath>", start + 1);
            if (pos != -1 && pos < end) {
               int pos2 = content.indexOf("<relativePath>", start + 1);
               if (pos2 != -1) {
                  String relativePath = content.substring(pos2 + "<relativePath>".length(), pos).trim();

                  //support ending pom.xml
                  if (relativePath.endsWith("/pom.xml")) {
                     relativePath = relativePath.substring(0, relativePath.length() - "/pom.xml".length());
                  }

                  return relativePath;
               }
            }
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return "GitBranchTask [m_execDir=" + m_execDir + ", m_branch=" + m_branch + "]";
   }

   private void updateModuleProject(List<String> modules, String moduleName) throws Exception {
      String fileName = new File(m_execDir, moduleName + "/pom.xml").getCanonicalPath();
      String content = FileUtils.readFile(fileName, "utf-8");

      String newContent = updatePom(modules, content);

      if (!newContent.equals(content)) {
         FileUtils.writeFile(fileName, newContent, "utf-8");
      }
   }

   private void updateParentProject(List<String> modules, String parentPomPath) throws Exception {
      String fileName = new File(m_execDir, parentPomPath + "/pom.xml").getCanonicalPath();
      String content = FileUtils.readFile(fileName, "utf-8");
      String newContent = updatePom(modules, content);

      if (!newContent.equals(content)) {
         FileUtils.writeFile(fileName, newContent, "utf-8");
      }
   }

   private String updatePom(List<String> modules, String content) throws Exception {
      String newContent = content;

      //update the parent as always by convention
      String parentArtifactId = getParentArtifactId(content);
      String relativePath = getParentRelativePath(content);
      if (parentArtifactId != null) {
         if (modules.contains(parentArtifactId) || relativePath != null && !relativePath.isEmpty()) {
            newContent = updateValue(newContent, "<parent>", "</parent>", "<version>", "</version>", m_version);
         } else if (m_updatedVersions != null) {
            String version = m_updatedVersions.get(parentArtifactId);
            if (version != null) {
               newContent = updateValue(newContent, "<parent>", "</parent>", "<version>", "</version>", version);
            }
         }
      }

      //update first artifact version
      if (parentArtifactId != null) {
         newContent = updateValue(newContent, "</parent>", "<dependencies>", "<version>", "</version>", m_version);
      } else {
         //no parent
         newContent = updateValue(newContent, "<project", "<dependencies>", "<version>", "</version>", m_version);
      }

      //update module reference in current pom
      for (String moduleName : modules) {
         newContent = updateValue(newContent, "<artifactId>" + moduleName, "</dependency>", "<version>", "</version>",
               m_version);
      }

      //search for updated artifact id and version
      if (m_updatedVersions != null) {
         for (Entry<String, String> entry : m_updatedVersions.entrySet()) {
            String id = entry.getKey();
            String version = entry.getValue();

            newContent = updateValue(newContent, "<artifactId>" + id + "</artifactId>", "</dependency>", "<version>",
                  "</version>", version);
         }
      }

      return newContent;
   }

   private String updateValue(String content, String start, String end, String matchStart, String matchEnd,
         String replacedValue) {
      return updateValue(content, start, end, matchStart, matchEnd, replacedValue, 0);
   }

   private String updateValue(String content, String start, String end, String matchStart, String matchEnd,
         String replacedValue, int startPos) {
      String newContent = content;
      int pos = newContent.indexOf(start, startPos);

      if (pos != -1) {
         int pos2 = newContent.indexOf(end, pos);
         if (pos2 != -1) {
            int m1 = newContent.indexOf(matchStart, pos);

            if (m1 != -1 && m1 < pos2) {
               //skip the comments part
               int commentStart = newContent.lastIndexOf("<!--", m1);
               if (commentStart != -1) {
                  int commentEnd = newContent.lastIndexOf("-->", m1);
                  if (commentEnd == -1 || commentEnd < commentStart) {
                     //skip one comment
                     m1 = newContent.indexOf(matchStart, m1 + 1);
                  }
               }
            }

            if (m1 != -1 && m1 < pos2) {
               int m2 = newContent.indexOf(matchEnd, m1);
               if (m2 != -1 && m2 < pos2) {
                  //found the match, then replace the value
                  newContent = newContent.substring(0, m1 + matchStart.length()) + replacedValue
                        + newContent.substring(m2);

                  //check next available
                  newContent = updateValue(newContent, start, end, matchStart, matchEnd, replacedValue, m1 + 1);
               }
            }
         }
      }

      return newContent;
   }

   @Override
   protected String getCmdDir() {
      return m_execDir;
   }

   @Override
   protected List<String> getCmdStrs() {
      return null;
   }

}
