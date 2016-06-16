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
package org.ostara.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;


public class Config {
	private static Config m_instance = new Config();
	private String userName;
	private String password;
	private String organization;
	private String gitAPIUrl;
	private String migrationJarName;
	private String ostaraCmdMavenRepoUrl;
	
	public static Config getInstance() {
		return m_instance;
	}
	
	
	private Config() {	
	}
	
	
	public void load(InputStream in)
	{
		
		try {
			Properties prop = new Properties();
			if (in != null) {
				prop.load(in);
				userName=prop.getProperty("userName");
				password=prop.getProperty("password");
				organization=prop.getProperty("organization");
				gitAPIUrl=prop.getProperty("gitAPIUrl");
				ostaraCmdMavenRepoUrl=prop.getProperty("ostaraCmdMavenRepoUrl");
			} else {
				System.err.println("no config file found");
			}

		} catch (IOException e) {
			
			e.printStackTrace();
		}
	
//		System.out.println(config);
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getOrganization() {
		return organization;
	}


	public void setOrganization(String organization) {
		this.organization = organization;
	}


	public String getGitAPIUrl() {
		return gitAPIUrl;
	}
	
	public String getGitAPIUserUrl() {
		return getGitAPIUrl() + "/user";
	}


	public void setGitAPIUrl(String gitAPIUrl) {
		this.gitAPIUrl = gitAPIUrl;
	}


	public void setMigrationJarName(String migrationJarName) {
		this.migrationJarName = migrationJarName;
	}
	
	public String getMigrationJarName() {
		return this.migrationJarName;
	}

	public String getMigrationWorkdir() {
		return SystemUtils.JAVA_IO_TMPDIR + "/migration";
	}

	public String getOstaraCmdMavenRepoUrl() {
		return ostaraCmdMavenRepoUrl;
	}

}
