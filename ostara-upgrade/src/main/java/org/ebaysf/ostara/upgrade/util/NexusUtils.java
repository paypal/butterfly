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
package org.ebaysf.ostara.upgrade.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.ebaysf.ostara.upgrade.MigratorUtils;

public class NexusUtils {
	protected static final Logger LOG = Logger.getLogger(NexusUtils.class);
	
	public static String getUrl(String content, int pos) {
		int start = content.lastIndexOf("\"", pos);
		return content.substring(start + 1, pos + 4);
	}

	public static Model readPom(String pomUrl) {
		try {
			InputStream is = new URL(pomUrl).openStream();
			Model model = new MavenXpp3Reader().read(is);
			is.close();

			return model;
		} catch (Throwable th) {
			th.printStackTrace();

			return null;
		}
	}

	public static Properties extractBuildinfo(String repo, Dependency dep) {
		return extractBuildinfo(repo + MigratorUtils.buildArtifactUrl(dep, true, null));
	}
	
	public static Properties extractBuildinfo(String url) {
		try {
			String content = IOUtils.toString(new URL(url));
			int jarPos = content.indexOf(".pom\"");
			if (jarPos != -1) {
				String jarSuffix = ".jar\"";
				jarPos = 0;

				do {
					jarPos = content.indexOf(jarSuffix, jarPos);
					//TODO Any qualifier will mess up the scanning
					final String SOURCES_JAR_FLAG = "-sources";
					final String TESTS_JAR_FLAG = "-tests";

					if (jarPos != -1 && content.substring(jarPos - SOURCES_JAR_FLAG.length(), jarPos).equals(SOURCES_JAR_FLAG)) {
						jarPos = content.indexOf(jarSuffix, jarPos + jarSuffix.length() - 1);
						continue;
					}
					
					if (jarPos != -1 && content.substring(jarPos - TESTS_JAR_FLAG.length(), jarPos).equals(TESTS_JAR_FLAG)) {
						jarPos = content.indexOf(jarSuffix, jarPos + jarSuffix.length() - 1);
						continue;
					}
					

					final String JAVADOC_JAR_FLAG = "-javadoc";

					if (jarPos != -1 && jarPos > JAVADOC_JAR_FLAG.length()
					    && content.substring(jarPos - JAVADOC_JAR_FLAG.length(), jarPos).equals(JAVADOC_JAR_FLAG)) {
						jarPos = content.indexOf(jarSuffix, jarPos + jarSuffix.length() - 1);
						continue;
					}

					if (jarPos != -1) {
						String jarUrl = getUrl(content, jarPos);

						System.out.println("Identified JAR URL: " + jarUrl);

						try (JarInputStream jis = new JarInputStream(new URL(jarUrl).openStream())) {
							JarEntry je = null;

							while ((je = jis.getNextJarEntry()) != null) {
								if (je.getName().equals("buildinfo.properties")) {
									Properties props = new Properties();
									props.load(new ByteArrayInputStream(IOUtils.toByteArray(jis)));

									return props;
								}
							}
						} catch (IOException e) {
							System.err.println("Failed to read JAR file contents");
							e.printStackTrace();
						}
					}

					break;
				} while (jarPos != -1);
			}
		} catch (IOException e) {
			LOG.warn(e.getMessage());
		}
		
		return null;
	}
	
	public static String getGitUrl(Properties props) {
		return props.getProperty("git.url");
	}
	
	public static String getGitBranch(Properties props) {
		return props.getProperty("git.branch");
	}
	
	public static String getCommitter(Properties props) {
		return props.getProperty("git.commit.user.email");
	}
}
