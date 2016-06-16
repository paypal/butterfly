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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.ostara.cmd.util.FileUtils;
import org.ostara.service.TaskServiceInitializer;

public class CmdLineTaskTest {
   @Before
   public void setup() {
      TaskServiceInitializer.init();
      try {
         TaskMetaRegistry.getInstance().register(
               FileUtils.readStream(CmdLineTaskTest.class.getResourceAsStream("cmdline.json"), true));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @Test
   public void testJava() {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("cmdStr", "java -version");
      ITask task = TaskFactory.createTask(map, TaskMetaRegistry.getInstance().getMeta("cmdline"));

      task.getManager().execute();

      System.out.println(task.getManager().getResults());
      Assert.assertTrue(task.getManager().isDone());
      Assert.assertTrue(task.getManager().isSuccess());
   }
}
