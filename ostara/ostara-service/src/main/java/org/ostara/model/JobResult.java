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
package org.ostara.model;

import java.util.List;
import java.util.Map;

public class JobResult {

   private String m_jobId;

   private String m_message;

   private List<String> m_allSteps;

   private Map<String, String> m_stepDescription;

   private Map<String, String> m_taskStatus;

   private Map<String, String> m_taskDuration;

   private Map<String, Object> m_taskResult;

   private boolean m_isCompleted;
   
   private boolean m_success;

   public List<String> getAllSteps() {
      return m_allSteps;
   }

   public String getJobId() {
      return m_jobId;
   }

   public String getMessage() {
      return m_message;
   }

   public Map<String, String> getStepDescription() {
      return m_stepDescription;
   }

   public Map<String, String> getTaskDuration() {
      return m_taskDuration;
   }

   public Map<String, String> getTaskStatus() {
      return m_taskStatus;
   }

   public boolean isCompleted() {
      return m_isCompleted;
   }

   public boolean isSuccess() {
      return m_success;
   }

   public void setCompleted(boolean isCompleted) {
      m_isCompleted = isCompleted;
   }

   public void setJobId(String jobId) {
      m_jobId = jobId;
   }

   public void setMessage(String message) {
      m_message = message;
   }

   public void setStepDescription(Map<String, String> stepDescription) {
      m_stepDescription = stepDescription;
   }

   public void setSteps(List<String> allTasks) {
      m_allSteps = allTasks;
   }

   public void setSuccess(boolean success) {
      m_success = success;
   }

   public void setTaskDuration(Map<String, String> taskDuration) {
      m_taskDuration = taskDuration;
   }

   public void setTaskStatus(Map<String, String> taskStatus) {
      m_taskStatus = taskStatus;
   }
   
   public void setTaskResult(Map<String, Object> taskResult) {
	   m_taskResult = taskResult;
   }

   public Map<String, Object> getTaskResult() {
	   return m_taskResult ;
   }

}
