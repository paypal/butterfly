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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TaskExecutorManager {

   private static TaskExecutorManager s_instance = new TaskExecutorManager();

   public static TaskExecutorManager getInstance() {
      return s_instance;
   }

   private ExecutorService  m_executor;

   private Map<String, WrapperExecutor> m_executors = new ConcurrentHashMap<String, WrapperExecutor>();

   private TaskExecutorManager() {
         m_executor = Executors.newFixedThreadPool(5);
     }

   public String addTask(ITask exec) {
      String id = exec.getId();
      WrapperExecutor wrapperTask = new WrapperExecutor(exec);
      m_executors.put(id, wrapperTask);
      m_executor.execute(wrapperTask);

      return id;
   }

   public Set<String> getAllTaskIds() {
      return m_executors.keySet();
   }

   public ITask getTask(String id) {
      WrapperExecutor wrapper = m_executors.get(id);

      if (wrapper != null) {
         return wrapper.getExecutor();
      } else {
         return null;
      }
   }

   public void stopAll() {
      Collection<WrapperExecutor> wrappers = m_executors.values();
      for (WrapperExecutor wrapper : wrappers) {
    	  wrapper.getExecutor().getManager().stop();
      }
   }

   protected static class WrapperExecutor implements Runnable {
      private ITask m_exec;

      public WrapperExecutor(ITask exec) {
         m_exec = exec;
      }

      public ITask getExecutor() {
         return m_exec;
      }

      @Override
      public void run() {
         m_exec.getManager().execute();
      }

   }
}
