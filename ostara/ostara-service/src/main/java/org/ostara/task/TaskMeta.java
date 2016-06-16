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

public class TaskMeta implements ITaskMeta {

   private Class<? extends ICmdHandler> m_handler;

   private Map<String, String> m_taskParameters;

   private Map<String, String> m_cmdParameterMapping;

   private List<ICmdEntry> m_commands;

   private String m_name;

   public TaskMeta(String name) {
      if (name == null) {
         throw new IllegalArgumentException("Name can't be null.");
      }
      m_name = name;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TaskMeta other = (TaskMeta) obj;
      if (m_name == null) {
         if (other.m_name != null)
            return false;
      } else if (!m_name.equals(other.m_name))
         return false;
      return true;
   }

   @Override
   public Class<? extends ICmdHandler> getCmdHandler() {
      return m_handler;
   }

   @Override
   public Map<String, String> getCmdParameterMapping() {
      return m_cmdParameterMapping;
   }

   @Override
   public List<ICmdEntry> getCommands() {
      return m_commands;
   }

   @Override
   public String getName() {
      return m_name;
   }

   @Override
   public Map<String, String> getTaskParameterMapping() {
      return m_taskParameters;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
      return result;
   }

   public void setCmdParameterMapping(Map<String, String> cmdParameterMapping) {
      m_cmdParameterMapping = cmdParameterMapping;
   }

   public void setCommands(List<ICmdEntry> commands) {
      m_commands = commands;
   }

   public void setHandler(Class<? extends ICmdHandler> handler) {
      m_handler = handler;
   }

   public void setTaskParameters(Map<String, String> taskParameters) {
      m_taskParameters = taskParameters;
   }
}
