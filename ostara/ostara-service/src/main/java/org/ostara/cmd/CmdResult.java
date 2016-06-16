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


public class CmdResult implements ICmdResult {
   private Exception m_exception;

   private String m_message;

   public Exception getException() {
      return m_exception;
   }

   public String getMessage() {
      return m_message;
   }

   @Override
   public boolean isSuccess() {
      return m_exception == null;
   }

   public void setException(Exception e) {
      m_exception = e;
   }

   public void setMessage(String result) {
      m_message = result;
   }

   @Override
   public String toString() {
      return "CmdResult [m_exception=" + m_exception + ", m_message=" + m_message + "]";
   }
}
