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
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

public class TaskFactory {
   private TaskFactory() {
   }

   @SuppressWarnings("unchecked")
   public static ITask createTask(HttpServletRequest request, ITaskMeta meta) {
      Map<String, Object> map = request.getParameterMap();

      Map<String, Object> newAttrs = new HashMap<String, Object>(map.size());
      for (Entry<String, Object> entry : map.entrySet()) {
         Object value = entry.getValue();
         if (value instanceof String[]) {
            if (((String[]) value).length > 0) {
               value = ((String[]) value)[0];
            } else {
               value = null;
            }
         }
         newAttrs.put(entry.getKey(), value);
      }
      return createTask(newAttrs, meta);
   }

   public static ITask createTask(Map<String, Object> attributes, ITaskMeta meta) {
      Map<String, String> mapping = meta.getTaskParameterMapping();
      Map<String, Object> taskAttrs = attributes;

      //do parameter conversion
      if (mapping != null) {
         taskAttrs = new HashMap<>();
         for (Entry<String, String> entry : mapping.entrySet()) {
            taskAttrs.put(entry.getKey(), attributes.get(entry.getValue()));
         }
      }

      return new Task(taskAttrs == null ? new HashMap<String, Object>() : taskAttrs, meta);
   }

}
