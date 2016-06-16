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

import java.util.HashMap;
import java.util.Map;

public class CmdCtx implements ICmdCtx {

   private Map<String, Object> m_parentAttrs;

   private Map<String, Object> m_attrs = new HashMap<String, Object>();

   private Map<String, String> m_mappings;

   public CmdCtx(Map<String, Object> parentAttrs) {
      this(parentAttrs, null);
   }

   public CmdCtx(Map<String, Object> attrs, Map<String, String> mappings) {
      m_parentAttrs = attrs;
      m_mappings = mappings;
   }

   @Override
   public Object getAttribute(String key) {
      Object value = m_attrs.get(key);

      if (value == null && m_parentAttrs != null) {
         return m_parentAttrs.get(key);
      } else {
         return value;
      }
   }

   @Override
   public Map<String, String> getCmdParameterMapping() {
      return m_mappings;
   }

   @Override
   public void setAttribute(String key, Object value) {
      m_attrs.put(key, value);
   }

   @Override
   public String toString() {
      return "CmdCtx [m_attrs=" + m_attrs + "]";
   }

}
