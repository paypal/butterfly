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
 * Creates a new local Git branch from an existing one.
 */
@Command(description = "git create branch command")
public class GitBranchCmd extends BaseCmdLineCmd {
	
	/** The directory inside the local Git repository */
   @InParameter(name = "gitDir", requried = true)
   String m_execDir;

   /** The existing branch */
   @InParameter(name = "gitBranch", requried = true)
   String m_branch;

   public GitBranchCmd(String name) {
      super(name);
   }

   @Override
   protected String getCmdDir() {
      return m_execDir;
   }

   @Override
   protected List<String> getCmdStrs() {
      List<String> cmd = new ArrayList<>();
      cmd.add("git");
      cmd.add("checkout");
      cmd.add("-b");
      cmd.add(m_branch);

      return cmd;
   }

   @Override
   public String toString() {
      return "GitBranchTask [m_execDir=" + m_execDir + ", m_branch=" + m_branch + "]";
   }

}
