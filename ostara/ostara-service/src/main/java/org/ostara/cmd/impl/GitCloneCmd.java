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

import java.util.ArrayList;
import java.util.List;

import org.ostara.cmd.BaseCmdLineCmd;
import org.ostara.cmd.ICmdResult;
import org.ostara.cmd.annotation.Command;
import org.ostara.cmd.annotation.InParameter;
import org.ostara.cmd.annotation.OutParameter;
import org.ostara.cmd.util.FileUtils;
import org.ostara.config.Config;

/**
 * Clones a remote Git repository into a local repository.
 */
@Command(description = "git clone command line task.")
public class GitCloneCmd extends BaseCmdLineCmd {
   @InParameter(name = "gitUrl", requried = true)
   String m_gitUrl;

   @InParameter(name = "cloneDir")
   String m_execDir;

   @InParameter(name = "repo")
   String m_repo;

   @InParameter(name = "branch")
   String m_branch;

   @OutParameter(name = "outputDir")
   String m_outputDir;

   public GitCloneCmd(String name) {
      super(name);
   }

   @Override
   public ICmdResult execute() {
      ICmdResult result = super.execute();

      //set output dir if success
      if (result.isSuccess()) {
         m_outputDir = m_execDir + '/' + m_repo;
      }

      return result;
   }

   @Override
   protected String getCmdDir() {
      if (m_execDir == null) {
         //use temp dir
         m_execDir = FileUtils.getTempIODir(true);
      }
      return m_execDir;
   }

   @Override
   protected List<String> getCmdStrs() {
	   Config config = Config.getInstance();
	   
      // fix empty git repo issue
      if (m_gitUrl.endsWith("/")) {
         m_gitUrl = m_gitUrl.substring(0, m_gitUrl.length() - 2) + ".git";
      } else if (!m_gitUrl.endsWith(".git")) {
         m_gitUrl = m_gitUrl + ".git";
      }
      
      if(m_gitUrl.startsWith("https://")) {
    	  m_gitUrl = "https://" + config.getUserName() + ":" + config.getPassword() + "@" + m_gitUrl.substring(8);
      }

      List<String> cmd = new ArrayList<>();
      cmd.add("git");
      cmd.add("clone");
      cmd.add("-v");
      cmd.add("--progress");

      // check out target branch if user specified
      if (m_branch != null && !m_branch.isEmpty()) {
         cmd.add("-b");
         cmd.add(m_branch);
      }

      cmd.add(m_gitUrl);

      //if it is null, use default name
      if (m_repo == null) {
         m_repo = getRepo(m_gitUrl);
      }
      cmd.add(m_repo);

      return cmd;
   }

   private String getRepo(String gitUrl) {
      int pos = gitUrl.lastIndexOf('/');
      return gitUrl.substring(pos + 1, gitUrl.length() - 4);
   }

   @Override
   public String toString() {
      return "GitCloneTask [m_gitUrl=" + m_gitUrl + ", m_execDir=" + m_execDir + ", m_repo=" + m_repo + ", m_branch="
            + m_branch + "]";
   }

}
