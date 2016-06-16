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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.ostara.cmd.BaseCommand;
import org.ostara.cmd.CmdResult;
import org.ostara.cmd.ICmdResult;
import org.ostara.cmd.annotation.Command;
import org.ostara.cmd.annotation.InParameter;
import org.ostara.cmd.annotation.OutParameter;
import org.ostara.config.Config;

/**
 * Performs a remote fork of the repository in GitHub.
 */
@Command(description = "Git fork command")
public class GitForkCmd extends BaseCommand {
   @InParameter(name = "gitUrl", requried = true)
   String m_gitUrl;

   @InParameter(name = "organization", requried = true)
   String m_organization;

   @InParameter(name = "username", requried = true)
   String m_userName;

   @InParameter(name = "password", requried = true)
   String m_password;

   @OutParameter(name = "forkedGitUrl")
   String m_forkedGitUrl;

   public GitForkCmd(String name) {
      super(name);
   }

   @Override
   public ICmdResult execute() {
      CmdResult result = new CmdResult();
      String sourceOrganization = getSourceOrganization(m_gitUrl);
      String sourceRepo = getSourceRepo(m_gitUrl);

      String forkUrl = Config.getInstance().getGitAPIUrl() + sourceOrganization + "/" + sourceRepo + "/forks";

      try {
         ResteasyClient client = new ResteasyClientBuilder().build();
         client.register(new BasicAuthentication(m_userName, m_password));
         client.register(new AgentHeadersRequestFilter(m_userName));

         ResteasyWebTarget target = client.target(forkUrl);

         Organization o = new Organization(m_organization);
         Response response = target.request().post(Entity.entity(o, "application/json"));
         if ((response.getStatus() == 201) || (response.getStatus() == 202)) {
            String resStr = response.readEntity(String.class);
            JSONObject jsonObject = new JSONObject(resStr);
            //System.out.println("Get response:" + jsonObject);
            //System.out.println(jsonObject.get("html_url"));
            try {
               m_forkedGitUrl = jsonObject.get("html_url").toString();
            } catch (JSONException e) {
               e.printStackTrace();
               result.setException(e);
            }

            result.setMessage("OK");
         } else {
            System.err.println("Unexpected response status:" + response.getStatus());
            throw new RuntimeException("Unexpected response status:" + response.getStatus() + "  "
                  + response.readEntity(String.class));

         }

         // step 2: wait due to async fork restful api
         if (result.getException() == null) {
            waitForkComplete(client, forkUrl);
         }

      } catch (Exception ex) {
         result.setException(ex);
      }

      return result;
   }

   private String getSourceOrganization(String gitUrl) {
      int last = gitUrl.length();
      if (gitUrl.endsWith("/")) {
         last = last - 1;
      }

      int end = gitUrl.lastIndexOf('/', last - 1);
      int start = gitUrl.lastIndexOf('/', end - 1);

      return gitUrl.substring(start + 1, end);
   }

   private String getSourceRepo(String gitUrl) {
      int last = gitUrl.length();
      if (gitUrl.endsWith("/")) {
         last = last - 1;
      }

      int end = gitUrl.lastIndexOf('/', last - 1);
      String repo = gitUrl.substring(end + 1, last);

      if (repo.endsWith(".git")) {
         return repo.substring(0, repo.length() - 4);
      } else {
         return repo;
      }
   }

   private void waitForkComplete(ResteasyClient client, String forkUrl) {

      //waiting count
      int count = 10;
      while (count > 0) {

         ResteasyWebTarget target = client.target(forkUrl);

         Response response1 = target.request().accept("application/json").get();
         try {
            JSONObject jsonObject = new JSONObject("{\"response\":" + response1.readEntity(String.class) + "}");
            //System.out.println("Get response:" + jsonObject);
            JSONArray array = (JSONArray) jsonObject.get("response");
            int len = array.length();
            for (int i = 0; i < len; i++) {
               JSONObject obj = (JSONObject) array.get(i);
               String url = null;
               url = (String) obj.get("html_url");

               //only check organization since the repo could be renamed.
               if (url != null && url.contains(m_organization)) {
                  return;
               }
            }
         } catch (Exception ex) {
            System.err.println("json error");
         }
         try {
            Thread.sleep(1000l);
         } catch (InterruptedException e) {
            break;
         }

         count--;
      }

   }

   public class Organization {
      private String m_organization;

      public Organization() {
      }

      public Organization(String s) {
         m_organization = s;
      }

      public String getOrganization() {
         return m_organization;
      }

      public void setOrganization(String organization) {
         m_organization = organization;
      }

   }

}
