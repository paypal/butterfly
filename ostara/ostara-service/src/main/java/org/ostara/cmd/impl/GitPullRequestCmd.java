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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a pull request in the remote GitHub installation.
 */
@Command(description = "Git pull request command")
public class GitPullRequestCmd extends BaseCommand {
   @InParameter(name = "sourceGitUrl", requried = true)
   private String m_sourceGitUrl;
   
   @InParameter(name = "gitDestOrganization", requried = true)
   private String m_gitDestOrganization;
   
   @InParameter(name = "gitDestBranch", requried = true)
   private String m_gitDestBranch;
   
   @InParameter(name = "gitSourceBranch", requried = true)
   private String m_gitSourceBranch;
   
   @InParameter(name = "username", requried = true)
   private String m_userName;
   
   @InParameter(name = "password", requried = true)
   private String m_password;
   
   @OutParameter(name = "gitPullURL")
   private String m_gitPullURL;
   
   private String m_gitSourceOrganization;

   private String m_gitSourceRepo;


   public GitPullRequestCmd(String name) {
      super(name);
   }

   private static Logger logger = LoggerFactory.getLogger(GitPullRequestCmd.class);

   @Override
   public ICmdResult execute() {
      CmdResult result = new CmdResult();
      try {
         m_gitSourceOrganization = getSourceOrganization(m_sourceGitUrl);
         m_gitSourceRepo = getSourceRepo(m_sourceGitUrl);
         
         String title = "Platform upgrade created by http://go/r2u on behalf of " + m_userName;
         String body = "This pull request was created by the Platform Upgrade as a Service tool. The upgrade request was submitted by user "
               + m_userName
               + ".\n"
               + "If you believe this pull request was created by accident, please contact "
               + m_userName + "@ebay.com.";
         String head = m_gitDestOrganization + ":" + m_gitDestBranch;
         String base = m_gitSourceBranch;
         String pullUrl = Config.getInstance().getGitAPIUrl() + m_gitSourceOrganization + "/" + m_gitSourceRepo
               + "/pulls";
         
         ResteasyClient client = new ResteasyClientBuilder().build();
         client.register(new BasicAuthentication(m_userName, m_password));
         client.register(new AgentHeadersRequestFilter(m_userName));

         ResteasyWebTarget target = client.target(pullUrl);
         PullModel model = new PullModel(title, body, head, base);

         Response response = target.request().post(Entity.entity(model, "application/json"));

         if (response.getStatus() == 201) {
            try {
               JSONObject output = new JSONObject(response.readEntity(String.class));

               m_gitPullURL = output.get("html_url").toString();
               result.setMessage("OK");
            } catch (Exception e) {
               logger.warn("Error fetching output", e);
               m_gitPullURL = "N/A";
            }
         } else {
            System.err.println("git pull internal error:" + response.getStatus());
            result.setException(new Exception("git pull internal error" + response.toString()));
         }
      } catch (Exception e) {
         result.setException(e);
         logger.error(e.getMessage(), e);
         e.printStackTrace();
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

   static class PullModel {
      private String title;

      private String body;

      private String head;

      private String base;

      public PullModel() {
      }

      public PullModel(String title, String body, String head, String base) {
         this.title = title;
         this.body = body;
         this.head = head;
         this.base = base;
      }

      public String getTitle() {
         return title;
      }

      public void setTitle(String title) {
         this.title = title;
      }

      public String getBody() {
         return body;
      }

      public void setBody(String body) {
         this.body = body;
      }

      public String getHead() {
         return head;
      }

      public void setHead(String head) {
         this.head = head;
      }

      public String getBase() {
         return base;
      }

      public void setBase(String base) {
         this.base = base;
      }

   }

}
