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
import java.util.Map;

import org.ostara.cmd.annotation.InParameter;
import org.ostara.cmd.annotation.OutParameter;

public class ParameterIOCHandler implements ICmdHandler {

   @Override
   public void complete(ICommand command, ICmdResult result, ICmdCtx ctx) {
      Field[] fields = command.getClass().getDeclaredFields();
      for (Field field : fields) {
         field.setAccessible(true);
         OutParameter annotation = field.getAnnotation(OutParameter.class);

         if (annotation != null) {
            try {
               String key = command.getName() + "." + annotation.name();

               // handling binding mapping
               Map<String, String> mapping = ctx.getCmdParameterMapping();
               if (mapping != null) {
                  String mapKey = mapping.get(key);
                  if (mapKey != null) {
                     key = mapKey;
                  }
               }

               Object newValue = field.get(command);
               ctx.setAttribute(key, newValue);
            } catch (Exception e) {
               throw new RuntimeException("Failed to complete command(" + command.getName() + "), exception:"
                     + e.toString());
            }
         }
      }
   }

   private Object evaluateExpression(String expression, ICmdCtx ctx) {
      String value = expression;

      int start = value.indexOf('{');
      int end = value.indexOf('}', start + 1);

      while (start != -1 && end != -1) {
         String key = value.substring(start + 1, end);
         Object attribute = ctx.getAttribute(key);
         if (attribute != null) {
            value = value.substring(0, start) + attribute + value.substring(end + 1);
         } else {
            value = value.substring(0, start) + value.substring(end + 1);
         }

         start = value.indexOf('{');
         end = value.indexOf('}', start + 1);
      }

      return value;
   }

   @Override
   public boolean handleError(ICommand task, ICmdResult result, ICmdCtx ctx) {
      return false;
   }

   @Override
   public void init(ICommand command, ICmdCtx ctx) {
      // IBindingMapping mapping = command.getBindingMapping();
      Field[] fields = command.getClass().getDeclaredFields();
      for (Field field : fields) {
         InParameter annotation = field.getAnnotation(InParameter.class);

         if (annotation != null) {
            boolean requried = annotation.requried();
            String key = command.getName() + "." + annotation.name();

            // handling binding mapping
            Map<String, String> mapping = ctx.getCmdParameterMapping();
            String expression = null;
            if (mapping != null) {
               String mapKey = mapping.get(key);

               //has expression
               if (mapKey != null) {
                  int pos = mapKey.indexOf('{');
                  if (pos != -1) {
                     expression = mapKey;
                  } else {
                     key = mapKey;
                  }
               }
            }

            Object value = null;
            if (expression == null) {
               value = ctx.getAttribute(key);
            } else {
               value = evaluateExpression(expression, ctx);
            }

            if (requried && value == null) {
               throw new RuntimeException("Missing requried input parameter value(" + key + ") on command("
                     + command.getName() + ").");
            }

            if (value != null) {
               try {
                  field.setAccessible(true);
                  field.set(command, value);
               } catch (Exception e) {
                  throw new RuntimeException("Failed to set init value on field(" + field.getName() + "), exception:"
                        + e.toString());
               }
            }
         }
      }
   }
}
