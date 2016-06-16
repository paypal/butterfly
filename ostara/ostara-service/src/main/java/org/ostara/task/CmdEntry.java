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

import org.ostara.cmd.ICommand;
import org.ostara.task.ITaskMeta.ICmdEntry;

public class CmdEntry implements ICmdEntry {

   private String m_name;

   private Class<? extends ICommand> m_class;

   public CmdEntry(String name, Class<? extends ICommand> clazz) {
      m_name = name;
      m_class = clazz;
   }

   @Override
   public String getName() {
      return m_name;
   }

   @Override
   public Class<? extends ICommand> getCommandClass() {
      return m_class;
   }

}
