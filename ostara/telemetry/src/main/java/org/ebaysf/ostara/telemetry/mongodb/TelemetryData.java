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

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "TelemetryData", noClassnameStored = true)
public class TelemetryData implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	// Auto set
	@Id
	private ObjectId id;

	// Set in migration tool
	private List<String> appName;

	private List<String> oldPlatformVersion;

	private String newPlatformVersion;

	private String debugLogURL;

	private String warnLogURL;
	
	private String finalReportURL;
	
	private String progressReportURL;

	private List<String> detectedAppType;

	private String migrationToolVersion;

	// Set in web tool
	private String userName;

	private String additionalEmails;

	private Date startTime;

	private String overrideAppType;

	private String taskId;

	private String sourceGitURL;

	private String sourceGitBranch;

	private String sourceGitRelativePath;

	private String destinationGitURL;

	private String destinationGitBranch;
	
	private String gitPullRequestURL;

	private Boolean isGitForkSuccessful;

	private Boolean isGitCloneSuccessful;

	private Boolean isMigrationSuccessful;

	private Boolean isGitPushSuccesful;
	
	private Boolean isGitBranchSuccessful;
	
	private Boolean isGitPullSuccessful;
	
	private Boolean success;

	private long gitForkDuration;

	private long gitCloneDuration;

	private long migrationDuration;

	private long gitPushDuration;
	
	private long gitBranchDuration;
	
	private long gitPullDuration;

	private long totalDuration;
	
	private String startDate;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<String> getAppName() {
		return appName;
	}

	public void setAppName(List<String> appName) {
		this.appName = appName;
	}

	public List<String> getOldPlatformVersion() {
		return oldPlatformVersion;
	}

	public void setOldPlatformVersion(List<String> oldPlatformVersion) {
		this.oldPlatformVersion = oldPlatformVersion;
	}

	public String getNewPlatformVersion() {
		return newPlatformVersion;
	}

	public void setNewPlatformVersion(String newPlatformVersion) {
		this.newPlatformVersion = newPlatformVersion;
	}

	public List<String> getDetectedAppType() {
		return detectedAppType;
	}

	public void setDetectedAppType(List<String> detectedAppType) {
		this.detectedAppType = detectedAppType;
	}

	public String getMigrationToolVersion() {
		return migrationToolVersion;
	}

	public void setMigrationToolVersion(String migrationToolVersion) {
		this.migrationToolVersion = migrationToolVersion;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAdditionalEmails() {
		return additionalEmails;
	}

	public void setAdditionalEmails(String additionalEmails) {
		this.additionalEmails = additionalEmails;
	}

	public String getSourceGitURL() {
		return sourceGitURL;
	}

	public void setSourceGitURL(String sourceGitURL) {
		this.sourceGitURL = sourceGitURL;
	}

	public String getDestinationGitURL() {
		return destinationGitURL;
	}

	public void setDestinationGitURL(String destinationGitURL) {
		this.destinationGitURL = destinationGitURL;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getOverrideAppType() {
		return overrideAppType;
	}

	public void setOverrideAppType(String overrideAppType) {
		this.overrideAppType = overrideAppType;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getSourceGitBranch() {
		return sourceGitBranch;
	}

	public void setSourceGitBranch(String sourceGitBranch) {
		this.sourceGitBranch = sourceGitBranch;
	}

	public String getSourceGitRelativePath() {
		return sourceGitRelativePath;
	}

	public void setSourceGitRelativePath(String sourceGitRelativePath) {
		this.sourceGitRelativePath = sourceGitRelativePath;
	}

	public String getDestinationGitBranch() {
		return destinationGitBranch;
	}

	public void setDestinationGitBranch(String destinationGitBranch) {
		this.destinationGitBranch = destinationGitBranch;
	}

	public Boolean isGitForkSuccessful() {
		return isGitForkSuccessful;
	}

	public void setGitForkSuccessful(Boolean isGitForkSuccessful) {
		this.isGitForkSuccessful = isGitForkSuccessful;
	}

	public Boolean isGitCloneSuccessful() {
		return isGitCloneSuccessful;
	}

	public void setGitCloneSuccessful(Boolean isGitCloneSuccessful) {
		this.isGitCloneSuccessful = isGitCloneSuccessful;
	}

	public Boolean isMigrationSuccessful() {
		return isMigrationSuccessful;
	}

	public void setMigrationSuccessful(Boolean isMigrationSuccessful) {
		this.isMigrationSuccessful = isMigrationSuccessful;
	}

	public Boolean isGitPushSuccesful() {
		return isGitPushSuccesful;
	}

	public void setGitPushSuccesful(Boolean isGitPushSuccesful) {
		this.isGitPushSuccesful = isGitPushSuccesful;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getGitForkDuration() {
		return gitForkDuration;
	}

	public void setGitForkDuration(long gitForkDuration) {
		this.gitForkDuration = gitForkDuration;
	}

	public long getGitCloneDuration() {
		return gitCloneDuration;
	}

	public void setGitCloneDuration(long gitCloneDuration) {
		this.gitCloneDuration = gitCloneDuration;
	}

	public long getMigrationDuration() {
		return migrationDuration;
	}

	public void setMigrationDuration(long migrationDuration) {
		this.migrationDuration = migrationDuration;
	}

	public long getGitPushDuration() {
		return gitPushDuration;
	}

	public void setGitPushDuration(long gitPushDuration) {
		this.gitPushDuration = gitPushDuration;
	}

	public long getTotalDuration() {
		return totalDuration;
	}

	public void setTotalDuration(long totalDuration) {
		this.totalDuration = totalDuration;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public Boolean isGitBranchSuccessful() {
		return isGitBranchSuccessful;
	}

	public void setGitBranchSuccessful(Boolean isGitBranchSuccessful) {
		this.isGitBranchSuccessful = isGitBranchSuccessful;
	}

	public Boolean isGitPullSuccessful() {
		return isGitPullSuccessful;
	}

	public void setGitPullSuccessful(Boolean isGitPullSuccessful) {
		this.isGitPullSuccessful = isGitPullSuccessful;
	}

	public long getGitBranchDuration() {
		return gitBranchDuration;
	}

	public void setGitBranchDuration(long gitBranchDuration) {
		this.gitBranchDuration = gitBranchDuration;
	}

	public long getGitPullDuration() {
		return gitPullDuration;
	}

	public void setGitPullDuration(long gitPullDuration) {
		this.gitPullDuration = gitPullDuration;
	}

	public Boolean isSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getDebugLogURL() {
		return debugLogURL;
	}

	public void setDebugLogURL(String debugLogURL) {
		this.debugLogURL = debugLogURL;
	}

	public String getWarnLogURL() {
		return warnLogURL;
	}

	public void setWarnLogURL(String warnLogURL) {
		this.warnLogURL = warnLogURL;
	}

	public String getFinalReportURL() {
		return finalReportURL;
	}

	public void setFinalReportURL(String finalReportURL) {
		this.finalReportURL = finalReportURL;
	}

	public String getProgressReportURL() {
		return progressReportURL;
	}

	public void setProgressReportURL(String progressReportURL) {
		this.progressReportURL = progressReportURL;
	}

	public String getGitPullRequestURL() {
		return gitPullRequestURL;
	}

	public void setGitPullRequestURL(String gitPullRequestURL) {
		this.gitPullRequestURL = gitPullRequestURL;
	}

	@Override
  public String toString() {
	  return "TelemetryData [id=" + id + ", appName=" + appName
	      + ", oldPlatformVersion=" + oldPlatformVersion + ", newPlatformVersion="
	      + newPlatformVersion + ", debugLogURL=" + debugLogURL + ", warnLogURL="
	      + warnLogURL + ", finalReportURL=" + finalReportURL
	      + ", progressReportURL=" + progressReportURL + ", detectedAppType="
	      + detectedAppType + ", migrationToolVersion=" + migrationToolVersion
	      + ", userName=" + userName + ", additionalEmails=" + additionalEmails
	      + ", startTime=" + startTime + ", overrideAppType=" + overrideAppType
	      + ", taskId=" + taskId + ", sourceGitURL=" + sourceGitURL
	      + ", sourceGitBranch=" + sourceGitBranch + ", sourceGitRelativePath="
	      + sourceGitRelativePath + ", destinationGitURL=" + destinationGitURL
	      + ", destinationGitBranch=" + destinationGitBranch
	      + ", gitPullRequestURL=" + gitPullRequestURL + ", isGitForkSuccessful="
	      + isGitForkSuccessful + ", isGitCloneSuccessful="
	      + isGitCloneSuccessful + ", isMigrationSuccessful="
	      + isMigrationSuccessful + ", isGitPushSuccesful=" + isGitPushSuccesful
	      + ", isGitBranchSuccessful=" + isGitBranchSuccessful
	      + ", isGitPullSuccessful=" + isGitPullSuccessful + ", success="
	      + success + ", gitForkDuration=" + gitForkDuration
	      + ", gitCloneDuration=" + gitCloneDuration + ", migrationDuration="
	      + migrationDuration + ", gitPushDuration=" + gitPushDuration
	      + ", gitBranchDuration=" + gitBranchDuration + ", gitPullDuration="
	      + gitPullDuration + ", totalDuration=" + totalDuration + ", startDate="
	      + startDate + "]";
  }
}
