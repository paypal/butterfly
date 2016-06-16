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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CmdManager {
   private ICmdCtx m_cmdCtx;

   private List<ICommand> m_commands = new ArrayList<ICommand>();

   private Map<String, ICmdResult> m_result = new LinkedHashMap<String, ICmdResult>();

   private List<ICmdHandler> m_handlers = new ArrayList<ICmdHandler>();

   private ParameterIOCHandler m_iocHandler;

   private CmdManagerState m_state = CmdManagerState.Created;

   CmdManager() {
   }

   public void addCmd(ICommand cmd) {
      //inject ctx
      if (cmd instanceof BaseCommand) {
         ((BaseCommand) cmd).setCtx(m_cmdCtx);
      }
      m_commands.add(cmd);
   }

   public void addCmdHandler(ICmdHandler handler) {
      if (handler != null) {
         m_handlers.add(handler);
      }
   }

   private void complete(ICommand task, ICmdResult result) {
      for (ICmdHandler handler : m_handlers) {
         handler.complete(task, result, m_cmdCtx);
      }

      m_iocHandler.complete(task, result, m_cmdCtx);
   }

   public void execute() {
      long begin = System.currentTimeMillis();
      for (ICommand cmd : m_commands) {
         if (m_state == CmdManagerState.Stopped) {
            break;
         }

         ICmdResult result = null;
         long duration = 0;
         try {
            m_result.put(cmd.getName(), null);

            long start = System.currentTimeMillis();

            // do init
            init(cmd);

            // indicate on-going
            result = cmd.execute();
            m_result.put(cmd.getName(), result);

            complete(cmd, result);
            duration = System.currentTimeMillis() - start;
            m_cmdCtx.setAttribute(cmd.getName() + ".duration", duration);
         } catch (Exception e) {
            //catch all exception
            if (result == null) {
               result = new CmdResult();
            }
            result.setException(e);
            m_result.put(cmd.getName(), result);
         }

         if (result.isSuccess()) {
            System.out.println("Completed '" + cmd.getName() + "' in " + duration + "ms.");
            System.out.println("ctx:" + m_cmdCtx);
         } else {
            System.out.println("Failed '" + cmd.getName() + "' ,result: " + result + ".");
            if (!handleError(cmd, result)) {
               m_state = CmdManagerState.Failed;
               break;
            } else {
               // reset error
               result.setException(null);
            }
         }
      }

      if (m_state != CmdManagerState.Failed) {
         m_state = CmdManagerState.Completed;
         System.out.println("Completed all tasks in " + (System.currentTimeMillis() - begin) + "ms.");
      }
   }

   public List<ICommand> getAllCmds() {
      return Collections.unmodifiableList(m_commands);
   }

   public ICmdCtx getCmdCtx() {
      return m_cmdCtx;
   }

   public Map<String, ICmdResult> getResults() {
      return Collections.unmodifiableMap(m_result);
   }

   public CmdManagerState getStatus() {
      return m_state;
   }

   private boolean handleError(ICommand task, ICmdResult result) {
      for (ICmdHandler handler : m_handlers) {
         if (handler.handleError(task, result, m_cmdCtx)) {
            return true;
         }
      }

      return false;
   }

   private void init(ICommand task) {
      for (ICmdHandler handler : m_handlers) {
         handler.init(task, m_cmdCtx);
      }

      m_iocHandler.init(task, m_cmdCtx);
   }

   public boolean isDone() {
      return m_state == CmdManagerState.Completed || m_state == CmdManagerState.Failed
            || m_state == CmdManagerState.Stopped;
   }

   public boolean isSuccess() {
      return m_state == CmdManagerState.Completed;
   }

   void setCmdCtx(ICmdCtx cmdCtx) {
      m_cmdCtx = cmdCtx;
   }

   void setIocHandler(ParameterIOCHandler iocHandler) {
      m_iocHandler = iocHandler;
   }

   public void stop() {
      m_state = CmdManagerState.Stopped;
   }

   public enum CmdManagerState {
      Created, Completed, Failed, Stopped
   }

}
