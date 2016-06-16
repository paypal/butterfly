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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class POMModifierUtil {


	public static String getLatestVersion(String tempDir, String url, String groupId, String artifactId, String version) throws Exception {
		return getLatestVersion(tempDir, url, null, groupId, artifactId, version);
	}

	public static String getLatestVersion(String tempDir, String url, String snapshotUrl, String groupId, String artifactId, String version) throws Exception {

		String latestVersion = null;

		String metaUrl = null;
		if (version == null) {
			metaUrl = url + "/" + groupId.replace(".", "/") + "/" + artifactId + "/maven-metadata.xml";
		} else {
			if (version.contains("SNAPSHOT") && snapshotUrl != null && !snapshotUrl.trim().isEmpty()) {
				metaUrl = snapshotUrl + "/" + groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/maven-metadata.xml";
			} else {
				metaUrl = url + "/" + groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/maven-metadata.xml";
			}
		}

		String tempPath = tempDir + "/" + groupId + "." + artifactId + "-" + new Date().getTime() + "-maven-metadata.xml";
		downloadFile(metaUrl, tempPath);

		// get the factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document dom = builder.parse(new File(tempPath));
		XPath xpath = XPathFactory.newInstance().newXPath();
		// XPath Query for showing all nodes value
		String xPathString = null;
		if (version == null) {
			xPathString = "//versioning/versions/version";
		} else {
			xPathString = "//versioning/snapshotVersions/snapshotVersion/value";
		}

		XPathExpression expr = xpath.compile(xPathString);

		NodeList list = (NodeList) expr.evaluate(dom, XPathConstants.NODESET);

		if (list != null) {
			String latest = ((Element) list.item(0)).getTextContent();
			for (int i = 1; i < list.getLength(); i++) {
				String temp = ((Element) list.item(i)).getTextContent();
				if (compareToVersion(temp, latest) > 0) {
					latest = temp;
				}
			}

			latestVersion = latest;

		}

		return latestVersion;
	}

	/**
	 * Download the url page content and save it to local file
	 * 
	 * @param urlString
	 * @param filename
	 * @throws Exception
	 */
	public static void downloadFile(String urlString, String filename) throws Exception {
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko/20100101 Firefox/11.0");
		InputStream is = con.getInputStream();

		byte[] bs = new byte[1024];
		int len;
		OutputStream os = new FileOutputStream(filename);
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		os.close();
		is.close();
	}

	public static void copyFile(String source, String dest) throws IOException {

		int byteread = 0;
		InputStream in = null;
		OutputStream out = null;

		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];

			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @ver1 = ver2: return 0
	 * @ver1 > ver2: return 1
	 * @ver1 < ver2: return -1
	 * 
	 * @param ver1
	 * @param ver2
	 * @return
	 */
	private static int compareToVersion(String ver1, String ver2) {
		if (ver1 == null && ver2 == null) {
			return 0;
		} else if (ver1 == null && ver2 != null) {
			return -1;
		} else if (ver1 != null && ver2 == null) {
			return 1;
		} else if (ver2.equalsIgnoreCase(ver1)) {
			return 0;
		} else {
			if (ver1.startsWith(ver2)) {
				return 1;
			} else if (ver2.startsWith(ver1)) {
				return -1;
			} else {
				String[] ver1Array = ver1.split("\\.");
				String[] ver2Array = ver2.split("\\.");

				for (int i = 0; ver1Array != null && ver2Array != null && i < ver1Array.length && i < ver2Array.length; i++) {

					if (ver1Array != null && ver2Array[i] != null && ver1Array[i].equals(ver2Array[i])) {
						continue;
					}

					int int1 = 0;
					int int2 = 0;
					boolean isNumber = true;
					try {
						int1 = Integer.parseInt(ver1Array[i]);
					} catch (NumberFormatException igore) {
						isNumber = false;
					}

					try {
						int2 = Integer.parseInt(ver2Array[i]);
					} catch (NumberFormatException igore) {
						isNumber = false;
					}

					if (isNumber) {
						if (int1 != int2) {
							return int1 - int2;
						}
					} else {
						return ver1Array[i].compareTo(ver2Array[i]);
					}
				}
			}
		}
		return 1;
	}
}
