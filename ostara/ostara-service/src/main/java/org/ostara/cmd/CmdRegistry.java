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
package org.ostara.cmd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ostara.cmd.ICmdMeta.IParameter;
import org.ostara.cmd.annotation.Command;
import org.ostara.cmd.annotation.InParameter;
import org.ostara.cmd.annotation.OutParameter;

public class CmdRegistry {
   private static CmdRegistry s_instance = new CmdRegistry();

   public static CmdRegistry getInstance() {
      return s_instance;
   }

   private Map<Class<? extends ICommand>, ICmdMeta> m_clazzMap = new LinkedHashMap<Class<? extends ICommand>, ICmdMeta>();

   private CmdRegistry() {
   }

   public Collection<ICmdMeta> getAllCommands() {
      return m_clazzMap.values();
   }

   public ICmdMeta getCmdMeta(Class<? extends ICommand> clazz) {
      ICmdMeta meta = m_clazzMap.get(clazz);
      if (meta == null) {
         register(clazz);
      }

      return m_clazzMap.get(clazz);
   }

   public void register(Class<? extends ICommand> clazz) {
      // register command
      Command cmdAnno = clazz.getAnnotation(Command.class);
      String description = cmdAnno.description();

      List<IParameter> input = new ArrayList<>();
      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
         InParameter annotation = field.getAnnotation(InParameter.class);

         if (annotation != null) {
            boolean requried = annotation.requried();
            String key = annotation.name();
            Parameter parameter = new Parameter(key, requried);
            input.add(parameter);
         }
      }

      List<IParameter> output = new ArrayList<>();
      for (Field field : fields) {
         OutParameter annotation = field.getAnnotation(OutParameter.class);

         if (annotation != null) {
            String key = annotation.name();
            // ignore required for output field
            Parameter parameter = new Parameter(key, false);
            output.add(parameter);
         }
      }

      CmdMeta meta = new CmdMeta(description, input, output, clazz);
      m_clazzMap.put(clazz, meta);
   }
}
