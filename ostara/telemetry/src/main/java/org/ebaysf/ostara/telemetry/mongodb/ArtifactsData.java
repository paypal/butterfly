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
package org.ebaysf.ostara.telemetry.mongodb;

import org.bson.types.ObjectId;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "MissingArtifacts", noClassnameStored = true)
public class ArtifactsData {
	
	@Id
	private ObjectId id;
	
	private String artifactId;
	
	private String groupId;
	
	private String version;
	
	private String type;
	
	private String owner;
	
	private String gitUrl;
	
	private String branch;
	
	private String gitCommitters;
	
	private String consumerGitCommitters;
	
	private String taskId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getGitUrl() {
		return gitUrl;
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getGitCommitters() {
		return gitCommitters;
	}

	public void setGitCommitters(String gitCommitters) {
		this.gitCommitters = gitCommitters;
	}

	public String getConsumerGitCommitters() {
		return consumerGitCommitters;
	}

	public void setConsumerGitCommitters(String consumerGitCommitters) {
		this.consumerGitCommitters = consumerGitCommitters;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Override
  public String toString() {
	  return "ArtifactsData [id=" + id + ", artifactId=" + artifactId
	      + ", groupId=" + groupId + ", version=" + version + ", type=" + type
	      + ", owner=" + owner + ", gitUrl=" + gitUrl + ", branch=" + branch
	      + ", gitCommitters=" + gitCommitters + ", consumerGitCommitters="
	      + consumerGitCommitters + ", taskId=" + taskId + "]";
  }

	
}
