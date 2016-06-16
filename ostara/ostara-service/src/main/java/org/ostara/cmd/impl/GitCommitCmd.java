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
import org.ostara.cmd.annotation.Command;
import org.ostara.cmd.annotation.InParameter;

/**
 * Commits the Git index to the local Git clone.
 */
@Command(description = "git commit command")
public class GitCommitCmd extends BaseCmdLineCmd {
   private static final String COMMIT_MESSAGE = "anonymous commit";

   @InParameter(name = "gitDir", requried = true)
   String m_execDir;

   @InParameter(name = "commitMessage")
   String m_commitMessage;

   public GitCommitCmd(String id) {
      super(id);
   }

   @Override
   protected String getCmdDir() {
      return m_execDir;
   }

   @Override
   protected List<String> getCmdStrs() {
      List<String> cmd = new ArrayList<>();
      cmd.add("git");
      cmd.add("commit");
      cmd.add("-m");
      cmd.add(m_commitMessage == null ? COMMIT_MESSAGE : m_commitMessage);
      cmd.add("-a");
      return cmd;
   }

   @Override
   public String toString() {
      return "GitCommitTask [m_execDir=" + m_execDir + ", m_commitMessage=" + m_commitMessage + "]";
   }
}
