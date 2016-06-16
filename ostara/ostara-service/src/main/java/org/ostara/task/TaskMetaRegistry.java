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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.ostara.cmd.ICmdHandler;
import org.ostara.cmd.ICommand;
import org.ostara.task.ITaskMeta.ICmdEntry;

public class TaskMetaRegistry {
   private static TaskMetaRegistry s_instance = new TaskMetaRegistry();

   public static TaskMetaRegistry getInstance() {
      return s_instance;
   }

   private Map<String, ITaskMeta> m_metas = new LinkedHashMap<>();

   private TaskMetaRegistry() {
   }

   @SuppressWarnings("unchecked")
   private ITaskMeta createMetaFromJson(String metaJson) {
      try {
         JSONObject json = new JSONObject(metaJson);
         String name = (String) json.get("name");
         TaskMeta meta = new TaskMeta(name);

         if (json.has("taskParameterMapping")) {
            Map<String, String> taskParameters = new HashMap<String, String>();
            JSONObject taskParamMappingJson = (JSONObject) json.get("taskParameterMapping");
            if (taskParamMappingJson != null) {
               Iterator<String> keys = taskParamMappingJson.keys();
               while (keys.hasNext()) {
                  String key = keys.next();
                  String value = (String) taskParamMappingJson.get(key);
                  taskParameters.put(key, value);
               }
            }
            meta.setTaskParameters(taskParameters);
         }

         List<ICmdEntry> commands = new ArrayList<>();
         JSONArray commandsJsonArray = (JSONArray) json.get("commands");
         for (int i = 0; i < commandsJsonArray.length(); i++) {
            JSONObject cmdJson = (JSONObject) commandsJsonArray.get(i);
            String cmdName = (String) cmdJson.getString("name");
            String clazzName = cmdJson.getString("commandClass");
            commands.add(new CmdEntry(cmdName, (Class<? extends ICommand>) Class.forName(clazzName)));
         }
         meta.setCommands(commands);

         if (json.has("cmdHandler")) {
            String handlerClazzName = json.getString("cmdHandler");
            if (handlerClazzName != null) {
               meta.setHandler((Class<? extends ICmdHandler>) Class.forName(handlerClazzName));
            }
         }

         if (json.has("cmdParameterMapping")) {
            Map<String, String> cmdParameterMapping = new HashMap<>();
            JSONObject cmdParameterMappingJson = (JSONObject) json.get("cmdParameterMapping");
            if (cmdParameterMappingJson != null) {
               Iterator<String> keys = cmdParameterMappingJson.keys();
               while (keys.hasNext()) {
                  String key = keys.next();
                  String value = cmdParameterMappingJson.getString(key);
                  cmdParameterMapping.put(key, value);
               }
            }
            meta.setCmdParameterMapping(cmdParameterMapping);
         }

         return meta;
      } catch (Exception e) {
         throw new RuntimeException("Failed to create meta, exception:" + e.toString());
      }
   }

   public Map<String, ITaskMeta> getAllMeta() {
      return Collections.unmodifiableMap(m_metas);
   }

   public void register(ITaskMeta meta) {
      register(meta, false);
   }

   private void register(ITaskMeta meta, boolean updatable) {
      if (!updatable && m_metas.containsKey(meta.getName())) {
         throw new RuntimeException("Meta(" + meta.getName() + ") has already been registered.");
      }

      m_metas.put(meta.getName(), meta);
   }

   public ITaskMeta register(String metaJson) {
      ITaskMeta meta = createMetaFromJson(metaJson);
      register(meta);
      return meta;
   }

   public ITaskMeta remove(String name) {
      return m_metas.remove(name);
   }

   public void update(ITaskMeta meta) {
      register(meta, true);
   }

   public ITaskMeta update(String metaJson) {
      ITaskMeta meta = createMetaFromJson(metaJson);
      register(meta, true);
      return meta;
   }

   public ITaskMeta getMeta(String taskName) {
      return m_metas.get(taskName);
   }
}
