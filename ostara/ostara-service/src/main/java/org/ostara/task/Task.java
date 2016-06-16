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

import java.util.Map;

import org.ostara.cmd.CmdManager;
import org.ostara.cmd.CmdManagerFactory;
import org.ostara.cmd.util.TaskIdGenerator;

public class Task implements ITask {

   private CmdManager m_cmdManager;

   private ITaskMeta m_meta;

   private String m_id;

   protected Task(Map<String, Object> taskAttrs, ITaskMeta meta) {
      m_meta = meta;
      m_id = TaskIdGenerator.getIntance().generateTaskId();

      //add taskId
      taskAttrs.put("taskId", m_id);

      m_cmdManager = CmdManagerFactory.create(taskAttrs, meta.getCommands(), meta.getCmdParameterMapping(),
            meta.getCmdHandler());
   }

   @Override
   public String getId() {
      return m_id;
   }

   @Override
   public CmdManager getManager() {
      return m_cmdManager;
   }

   @Override
   public ITaskMeta getMeta() {
      return m_meta;
   }

}
