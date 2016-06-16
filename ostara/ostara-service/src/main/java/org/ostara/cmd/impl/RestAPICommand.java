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
package org.ostara.cmd.impl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONObject;
import org.ostara.cmd.BaseCommand;
import org.ostara.cmd.CmdResult;
import org.ostara.cmd.ICmdResult;
import org.ostara.cmd.annotation.Command;
import org.ostara.cmd.annotation.InParameter;
import org.ostara.cmd.annotation.OutParameter;

/**
 * A generic command for RESTful operations.
 */
@Command(description = "restful api command")
public class RestAPICommand extends BaseCommand {
   @InParameter(name = "serviceUrl", requried = true)
   private String m_serviceUrl;

   @InParameter(name = "acceptContentType", requried = true)
   private String m_acceptType;

   @InParameter(name = "method")
   private String m_method;

   @InParameter(name = "postContent")
   private Object m_postContent;

   @InParameter(name = "responseType")
   private String m_responseType;

   @OutParameter(name = "response")
   private Object m_response;

   public RestAPICommand(String name) {
      super(name);
   }

   @Override
   public ICmdResult execute() {
      //default value
      CmdResult result = new CmdResult();
      if (m_method == null) {
         m_method = "get";
      }
      if (m_responseType == null) {
         m_responseType = "string";
      }

      try {
         if (!m_method.equalsIgnoreCase("get") && !m_method.equalsIgnoreCase("post")) {
            throw new RuntimeException("Unsupported method:" + m_method);
         }

         if (!m_responseType.equalsIgnoreCase("string") && !m_responseType.equalsIgnoreCase("json")) {
            throw new RuntimeException("Unsupported response type:" + m_responseType);
         }

         Client client = ClientBuilder.newClient();
         WebTarget target = client.target(m_serviceUrl);
         Response response = null;
         if (m_method.equalsIgnoreCase("get")) {
            response = target.request().accept(m_acceptType).get();
         } else {
            if (m_postContent != null) {
               response = target.request().accept(m_acceptType).post(Entity.entity(m_postContent, m_acceptType));
            } else {
               throw new RuntimeException("Post content can't be null!");
            }
         }

         if (response.getStatus() != 202 && response.getStatus() != 200) {
            throw new RuntimeException("Error response status:" + response.getStatus());
         }
         result.setMessage(String.valueOf(response.getStatus()));

         Object value = null;
         if (m_responseType.equalsIgnoreCase("string")) {
            value = response.readEntity(String.class);
         } else {
            value = response.readEntity(JSONObject.class);
         }

         m_response = value;
      } catch (Exception e) {
         result.setException(e);
      }

      return result;
   }
}
