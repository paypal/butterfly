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
 * TODO 
 */
@Command(description = "Git checkout command")
public class GitCheckoutCmd extends BaseCmdLineCmd {
   @InParameter(name = "gitDir", requried = true)
   String m_execDir;

   @InParameter(name = "gitBranch")
   String m_branch;

   @InParameter(name = "gitCommitId")
   String m_commitId;

   public GitCheckoutCmd(String name) {
      super(name);
   }

   @Override
   protected List<String> getCmdStrs() {
      List<String> cmd = new ArrayList<>();
      cmd.add("git");
      cmd.add("checkout");

      if (m_commitId != null) {
         cmd.add(m_commitId);
      } else if (m_branch != null) {
         cmd.add(m_branch);
      } else {
         throw new RuntimeException("Checkout Branch or CommitId is missing.");
      }

      return cmd;
   }

   @Override
   protected String getCmdDir() {
      return m_execDir;
   }

   @Override
   public String toString() {
      return "GitCheckoutTask [m_execDir=" + m_execDir + ", m_branch=" + m_branch + ", m_commitId=" + m_commitId + "]";
   }

}
