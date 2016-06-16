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
package org.ostara.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.ostara.cmd.ICmdResult;
import org.ostara.cmd.ICommand;
import org.ostara.config.Config;
import org.ostara.service.TaskServiceInitializer;

public class UpgradeTaskTest {

   @Before
   public void setup() {
      TaskServiceInitializer.init();
   }

   @Test
   public void testUpgrade() {
      Config config = Config.getInstance();

      config.setGitAPIUrl("https://api.github.com/repos/");
      config.setOrganization("unknown");
      config.setPassword("doe");
      config.setUserName("john");

      String gitBranch = "master";
      String gitUrl = "https://github.com/susieliao/test.git";

      Map<String, Object> attributes = new HashMap<String, Object>();
      attributes.put("gitBranch", gitBranch);
      attributes.put("gitUrl", gitUrl);
      attributes.put("parentPom", "pom.xml");
      attributes.put("config.organization", config.getOrganization());
      attributes.put("config.username", config.getUserName());
      attributes.put("config.password", config.getPassword());
      attributes.put("upgradeVersion", "2.0.8-RELEASE");

      ITask upgradeTask = TaskFactory.createTask(attributes, TaskMetaRegistry.getInstance().getMeta("upgradeTask"));

      List<ICommand> allTasks = upgradeTask.getManager().getAllCmds();
      int i = 1;
      for (ICommand cmd : allTasks) {
         System.out.println((i++) + ". " + cmd.getMeta().getDescription());
      }

      upgradeTask.getManager().execute();

      Map<String, ICmdResult> results = upgradeTask.getManager().getResults();
      i = 1;
      for (ICommand cmd : allTasks) {
         ICmdResult result = results.get(cmd.getName());

         String status = "N/A";
         if (result != null) {
            if (result.getException() != null) {
               status = result.getException().toString();
            } else {
               status = "Completed";
            }
         }

         System.out.println((i++) + ". " + cmd.getMeta().getDescription() + ": " + status);
      }

      Assert.assertTrue(upgradeTask.getManager().isDone());
      Assert.assertTrue(upgradeTask.getManager().isSuccess());
   }

}
