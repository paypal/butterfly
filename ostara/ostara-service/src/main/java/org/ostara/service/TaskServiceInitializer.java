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
package org.ostara.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.ostara.cmd.CmdRegistry;
import org.ostara.cmd.ICommand;
import org.ostara.cmd.impl.CmdLineCmd;
import org.ostara.cmd.impl.GitAddCmd;
import org.ostara.cmd.impl.GitBranchCmd;
import org.ostara.cmd.impl.GitCheckoutCmd;
import org.ostara.cmd.impl.GitCloneCmd;
import org.ostara.cmd.impl.GitCommitCmd;
import org.ostara.cmd.impl.GitForkCmd;
import org.ostara.cmd.impl.GitPullRequestCmd;
import org.ostara.cmd.impl.GitReleaseVersionCmd;
import org.ostara.cmd.impl.RestAPICommand;
import org.ostara.cmd.impl.VersionUpgradeCmd;
import org.ostara.cmd.util.FileUtils;
import org.ostara.config.Config;
import org.ostara.task.TaskMetaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskServiceInitializer {
   private static Logger logger = LoggerFactory.getLogger(GitPullRequestCmd.class);
   private static boolean s_inited;

   public static void init() {
      if (!s_inited) {
         InputStream in = null;
         try {
            //load config file
        	String ostaraConfigOverride = System.getProperty("ostaraConfig");
        	 
        	if(ostaraConfigOverride != null) {
        		logger.info("Loading config from file " + ostaraConfigOverride);
        		in = new FileInputStream(ostaraConfigOverride);
        	} else {
        		in = TaskServiceInitializer.class.getResourceAsStream("ostara.properties");
        		logger.info("Loading config from the classpath");
        	}
            
            init(in);
         } catch (Exception ex) {
            throw new RuntimeException("Failed to init TaskService, exception:" + ex.toString());
         } finally {
            if (in != null) {
               try {
                  in.close();
               } catch (IOException e) {
                  //ignore it
               }
            }
         }
         s_inited = true;
      }
   }

	public static void init(InputStream in) throws IOException {
		Config.getInstance().load(in);
	
		//load meta
		TaskMetaRegistry.getInstance()
		      .register(
		            FileUtils.readStream(TaskServiceInitializer.class.getResource("upgradetask.json").openStream(),
		                  true));
	
		//load command
		register(CmdLineCmd.class);
		register(GitAddCmd.class);
		register(GitBranchCmd.class);
		register(GitCheckoutCmd.class);
		register(GitCloneCmd.class);
		register(GitCommitCmd.class);
		register(GitForkCmd.class);
		register(GitPullRequestCmd.class);
		register(GitReleaseVersionCmd.class);
		register(RestAPICommand.class);
		register(VersionUpgradeCmd.class);
	}

   private static void register(Class<? extends ICommand> clazz) {
      CmdRegistry.getInstance().register(clazz);
   }
}
