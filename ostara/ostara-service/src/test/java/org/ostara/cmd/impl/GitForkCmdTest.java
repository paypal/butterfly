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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.ostara.cmd.CmdManager;
import org.ostara.cmd.CmdManagerFactory;
import org.ostara.config.Config;

public class GitForkCmdTest {

   @Test
   public void testFork() {
      GitForkCmd task = new GitForkCmd("fork1");

      Map<String, Object> attrs = new HashMap<>();

      String organization = "UpgradeTest";
      Config gitconfig = Config.getInstance();
      gitconfig.setGitAPIUrl("https://github.com/api/v3/repos/");
      gitconfig.setOrganization("UpgradeTest");

      attrs.put("fork1.gitUrl", "https://github.com/ostara/webres-sample");
      attrs.put("fork1.username", "platformupgradeservice");
      attrs.put("fork1.password", "dummy");
      attrs.put("fork1.organization", organization);

      CmdManager manager = CmdManagerFactory.create(attrs);

      manager.addCmd(task);
      manager.execute();

      Assert.assertTrue(manager.isDone());
      Assert.assertEquals(task, manager.getAllCmds().get(0));

      System.out.println("result:" + manager.getResults());
      Assert.assertTrue(manager.isSuccess());
   }

}
