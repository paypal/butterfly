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
package org.ostara.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.ostara.cmd.CmdManager;
import org.ostara.cmd.CmdRegistry;
import org.ostara.cmd.ICmdMeta;
import org.ostara.cmd.ICmdResult;
import org.ostara.cmd.ICommand;
import org.ostara.config.Config;
import org.ostara.model.JobResult;
import org.ostara.model.SubmitResult;
import org.ostara.service.util.ReportData;
import org.ostara.service.util.UpgradeStatusReportGen;
import org.ostara.task.ITask;
import org.ostara.task.ITaskMeta;
import org.ostara.task.TaskExecutorManager;
import org.ostara.task.TaskFactory;
import org.ostara.task.TaskMetaRegistry;

import com.google.gson.Gson;

public class TaskServiceImpl implements TaskService {
   @Context
   private HttpServletRequest servletRequest;

   @Override
   public String addTaskMeta(String jsonstr) {
      ITaskMeta taskMeta = TaskMetaRegistry.getInstance().register(jsonstr);
      return taskMeta.getName() + " is registered!";
   }

   @Override
   public String echo(String message) {
      return message;
   }

   private String getTaskStatus(ICmdResult taskResult, String id, boolean hasKey) {
      if (taskResult != null) {
         if (taskResult.getException() != null) {
            return "failed";
         } else {
            return "success";
         }
      } else if (hasKey) {
         return "in-progress";
      }

      return "planned";
   }

   @Override
   public Object query(String jobId) {

      JobResult jobResult = new JobResult();
      if (jobId != null) {
         jobResult.setJobId(jobId);

         ITask task = (ITask) TaskExecutorManager.getInstance().getTask(jobId);

         CmdManager manager = task.getManager();
         List<ICommand> tasks = manager.getAllCmds();
         Map<String, ICmdResult> taskResults = manager.getResults();

         List<String> allTasks = new ArrayList<String>(tasks.size());
         Map<String, String> taskStatus = new HashMap<String, String>(tasks.size());
         Map<String, String> descriptions = new HashMap<String, String>(tasks.size());
         Map<String, String> taskDuration = new HashMap<String, String>(tasks.size());
         Map<String, Object> taskResult = new HashMap<String, Object>(tasks.size());

         for (ICommand cmd : tasks) {
            String id = cmd.getName();
            allTasks.add(id);
            descriptions.put(id, cmd.getMeta().getDescription());

            ICmdResult cmdtaskResult = taskResults.get(id);
            String status = getTaskStatus(cmdtaskResult, id, taskResults.containsKey(id));

            if ("failed".equals(status)) {
               jobResult.setMessage(cmdtaskResult.getException().toString());
            }

            Object duration = null;
            if (cmdtaskResult != null) {
               duration = manager.getCmdCtx().getAttribute(cmd.getName() + ".duration");
            }
            taskDuration.put(id, String.valueOf(duration == null ? -1 : duration));

            taskStatus.put(id, status);
            if (cmdtaskResult != null) {
               taskResult.put(id,
                     cmdtaskResult.getException() != null ? cmdtaskResult.getException() : cmdtaskResult.getMessage());
            }

         }

         jobResult.setSteps(allTasks);
         jobResult.setTaskStatus(taskStatus);
         jobResult.setStepDescription(descriptions);
         jobResult.setTaskDuration(taskDuration);
         jobResult.setTaskResult(taskResult);

         jobResult.setCompleted(manager.isDone());

         //successfully completed
         String lastTaskId = tasks.get(tasks.size() - 1).getName();
         ICmdResult lastTaskResult = taskResults.get(lastTaskId);
         if (manager.isDone() && lastTaskResult != null && lastTaskResult.getException() == null) {
            jobResult.setSuccess(true);
         }

      } else {
         jobResult.setMessage("missing job id");
         jobResult.setCompleted(true);
      }

      return jobResult;
   }

   @Override
   public String queryAll() {
      return TaskExecutorManager.getInstance().getAllTaskIds().toString();
   }

   @Override
   public Object queryAllCmd() {
      Collection<ICmdMeta> commands = CmdRegistry.getInstance().getAllCommands();

      return commands;
   }

   @Override
   public Object queryTaskMeta() {
      Map<String, ITaskMeta> map = TaskMetaRegistry.getInstance().getAllMeta();
      return map.values();
   }

   @Override
@SuppressWarnings("unchecked")
   public Object submitTask(String taskName) {
      SubmitResult result = new SubmitResult();

      if (taskName == null) {
         result.setStatus(403);
         result.setMessage("TaskName is null!");
         return result;
      }

      ITaskMeta meta = TaskMetaRegistry.getInstance().getMeta(taskName);
      if (meta == null) {
         result.setStatus(403);
         result.setMessage("Can't find task meta:" + taskName);
         return result;
      }

      Map<String, Object> map = servletRequest.getParameterMap();
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
      newAttrs.put("config.username", Config.getInstance().getUserName());
      newAttrs.put("config.password", Config.getInstance().getPassword());
      newAttrs.put("config.organization", Config.getInstance().getOrganization());

      ITask task = TaskFactory.createTask(newAttrs, meta);
      String jobId = TaskExecutorManager.getInstance().addTask(task);

      if (jobId != null) {
         result.setStatus(200);
         result.setJobId(jobId);
         result.setMessage("Job submit successfully!");
      } else {
         result.setStatus(500);
         result.setMessage("Internal server error: can't create jobId!");
      }

      return result;
   }

   @Override
   public Object upgrade(String gitURL, String gitBranch, String parentPom, String upgradeVersion) {
      SubmitResult result = new SubmitResult();
      if (gitBranch == null) {
         gitBranch = "master";
      }
      if (parentPom == null) {
         parentPom = "pom.xml";
      }

      if (gitURL == null || upgradeVersion == null) {
         result.setStatus(403);
         result.setMessage("gitURL or upgradeVersion is missing");
         return result;
      }

      Map<String, Object> parameterMap = new HashMap<>();
      parameterMap.put("gitUrl", gitURL);
      parameterMap.put("gitBranch", gitBranch);
      parameterMap.put("parentPom", parentPom);
      parameterMap.put("upgradeVersion", upgradeVersion);
      parameterMap.put("config.username", Config.getInstance().getUserName());
      parameterMap.put("config.password", Config.getInstance().getPassword());
      parameterMap.put("config.organization", Config.getInstance().getOrganization());

      ITask task = TaskFactory.createTask(parameterMap, TaskMetaRegistry.getInstance().getMeta("upgradeTask"));
      String jobId = TaskExecutorManager.getInstance().addTask(task);

      if (jobId != null) {
         result.setStatus(200);
         result.setJobId(jobId);
         result.setMessage("Job submit successfully!");
      } else {
         result.setStatus(500);
         result.setMessage("Internal server error: can't create jobId!");
      }

      return result;
   }

	@Override
	public Object report() {
		ReportData reportData = UpgradeStatusReportGen.getReportData();

		Gson gson = new Gson();
		return gson.toJson(reportData);
	}
}