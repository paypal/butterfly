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

import java.util.List;
import java.util.Map;

import org.ostara.task.ITaskMeta.ICmdEntry;

public class CmdManagerFactory {
   private CmdManagerFactory() {
   }

   public static CmdManager create() {
      return create(null);
   }

   public static CmdManager create(Map<String, Object> attrs) {
      CmdManager manager = new CmdManager();
      manager.setCmdCtx(new CmdCtx(attrs));
      manager.setIocHandler(new ParameterIOCHandler());
      return manager;
   }

   public static CmdManager create(Map<String, Object> attrs, List<ICmdEntry> commands,
         Map<String, String> parameterMapping, Class<? extends ICmdHandler> handlerClazz) {
      CmdManager manager = new CmdManager();
      try {
         manager.setCmdCtx(new CmdCtx(attrs, parameterMapping));
         manager.setIocHandler(new ParameterIOCHandler());
         if (handlerClazz != null) {
            manager.addCmdHandler(handlerClazz.newInstance());
         }

         // load commands
         for (ICmdEntry entry : commands) {
            String name = entry.getName();
            Class<? extends ICommand> clazz = entry.getCommandClass();
            ICmdMeta meta = CmdRegistry.getInstance().getCmdMeta(clazz);
            if (meta == null) {
               throw new RuntimeException("Can't find the command:" + name);
            }

            manager.addCmd(meta.getCommandClass().getConstructor(String.class).newInstance(name));
         }
      } catch (Exception e) {
         throw new RuntimeException("Failed to create commandManager:" + e.toString());
      }

      return manager;
   }
}
