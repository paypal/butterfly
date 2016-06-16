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
package org.ebaysf.ostara.upgrade.util.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class BaseXmlTransformer {
  protected final Logger LOG = Logger.getLogger(BaseXmlTransformer.class);
  
  public BaseXmlTransformer() {
    super();
  }

  protected Document loadAndBackupXml(File xmlFile, boolean backup) throws ParserConfigurationException, SAXException, IOException {
    if(!xmlFile.exists()) {
      return null;
    }
    if(backup == true) {
    	backUpWebXML(xmlFile);
    }
  
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(xmlFile);
    return doc;
  }

  /**
   * Save XML after changes
   * @param xmlFile
   * @param doc
   */
  protected void saveXML(File xmlFile, Document doc) {
    TransformerFactory transformerFactory = TransformerFactory
        .newInstance();
    Transformer transformer;
    try {
      LOG.debug("Saving " + xmlFile);
      transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(xmlFile);
      transformer.transform(source, result);
    } catch (TransformerConfigurationException e) {
      LOG.warn(e, e);
    } catch (TransformerException e) {
      LOG.warn(e, e);
    }
  }

  /**
   * Take backup of XML
   * @param xmlFile
   */
  protected void backUpWebXML(File xmlFile) {
    File file = new File(xmlFile.getAbsolutePath() + ".bak."
        + System.currentTimeMillis());
    try {
      LOG.debug("Backing up to " + file);
  
      FileUtils.copyFile(xmlFile, file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}