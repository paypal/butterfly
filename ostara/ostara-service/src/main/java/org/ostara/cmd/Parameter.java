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

import org.ostara.cmd.ICmdMeta.IParameter;

public class Parameter implements IParameter {

   private String m_name;

   private boolean m_required;

   public Parameter(String m_name, boolean m_required) {
      this.m_name = m_name;
      this.m_required = m_required;
   }

   @Override
   public String getName() {
      return m_name;
   }

   @Override
   public boolean isRequired() {
      return m_required;
   }

   @Override
   public String toString() {
      return "Parameter [m_name=" + m_name + ", m_required=" + m_required + "]";
   }

}
