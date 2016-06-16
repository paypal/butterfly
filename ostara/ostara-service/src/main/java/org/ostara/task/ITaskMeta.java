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

import java.util.List;
import java.util.Map;

import org.ostara.cmd.ICmdHandler;
import org.ostara.cmd.ICommand;

public interface ITaskMeta {
   public String getName();

   // name ==> target attr name
   public Map<String, String> getTaskParameterMapping();

   public List<ICmdEntry> getCommands();

   // parameterName == > mapping
   public Map<String, String> getCmdParameterMapping();

   // command handler in manager
   public Class<? extends ICmdHandler> getCmdHandler();

   public interface ICmdEntry {
      public String getName();

      public Class<? extends ICommand> getCommandClass();
   }
}
