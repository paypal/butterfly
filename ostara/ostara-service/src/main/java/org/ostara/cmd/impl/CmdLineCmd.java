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

import java.util.Arrays;
import java.util.List;

import org.ostara.cmd.BaseCmdLineCmd;
import org.ostara.cmd.annotation.Command;
import org.ostara.cmd.annotation.InParameter;

/**
 * Executes an arbitrary operating system command line command.
 */
@Command(description = "command line task")
public class CmdLineCmd extends BaseCmdLineCmd {

	/** The execution directory for the command */
   @InParameter(name = "cmdDir")
   private String m_cmdDir;

   /** The command line to execute */
   @InParameter(name = "cmdStr", requried = true)
   private String m_commandStr;

   public CmdLineCmd(String name) {
      super(name);
   }

   @Override
   protected String getCmdDir() {
      //default user home
      if (m_cmdDir == null) {
         m_cmdDir = System.getProperty("user.home");
      }
      return m_cmdDir;
   }

   @Override
   protected List<String> getCmdStrs() {
      return Arrays.asList(m_commandStr.split(" "));
   }

}
