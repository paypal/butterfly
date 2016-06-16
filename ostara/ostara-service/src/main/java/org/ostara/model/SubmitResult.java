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

public class SubmitResult {
   private int m_status;

   private String m_jobId;
   
   private String m_message;

   public String getJobId() {
      return m_jobId;
   }

   public String getMessage() {
      return m_message;
   }

   public int getStatus() {
      return m_status;
   }

   public void setJobId(String jobId) {
      m_jobId = jobId;
   }

   public void setMessage(String message) {
      m_message = message;
   }

   public void setStatus(int status) {
      m_status = status;
   }
}
