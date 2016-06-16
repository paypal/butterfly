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

package org.ebaysf.ostara.upgrade

import java.io.File
import java.io.FileReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap
import java.util.Properties
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.SystemUtils.JAVA_IO_TMPDIR
import org.apache.commons.lang.text.StrSubstitutor
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.Plugin
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.codehaus.plexus.util.xml.Xpp3Dom
import org.ebaysf.ostara.upgrade.util.POMModifierUtil.getLatestVersion
import grizzled.slf4j.Logging
import PomReport._
import java.io.InputStreamReader
import java.io.BufferedWriter
import java.io.FileWriter
import org.apache.maven.model.io.xpp3.MavenXpp3Writer
import scala.collection.JavaConversions._
import org.apache.maven.model.PluginManagement

object MigratorUtils extends Logging {
  val POM_XML = "pom.xml"
  
  def getPomFromDirectory(m:String, parent:File):File= {
    val moduleDir = new File(if(parent.isFile()) parent.getParentFile() else parent, m)
    
    if(moduleDir.isFile()) moduleDir else (new File(moduleDir, s"$POM_XML"))
  }
  
  def getNormalizedAbsoluteModuleName(parent:File, m:String):String = {
    parent.getCanonicalPath() + " # " + normalizeModuleName(m)
  }
  
  def normalizeModuleName(m:String):String= { 
    if(m.endsWith(POM_XML)) {
      m.dropRight(POM_XML.length).stripSuffix("/")
    } else m.stripSuffix("/").stripSuffix("\\")
  }
  
  def extractTextContentFromXml(xpath:String, xmlFile:File):String = {
    val xPath = javax.xml.xpath.XPathFactory.newInstance().newXPath();
    return xPath.evaluate(xpath, new org.xml.sax.InputSource(new FileReader(xmlFile)))
  }
  
  
  def readPom(file:File):Model={
    info(s"Reading $file")
    val reader = new FileReader(if(file.isDirectory()) new File(file, POM_XML) else file)
    val model = new MavenXpp3Reader().read(reader);
    reader.close
    
    return model
  }
  
  def readModelFromClasspath(sourcePomFile:String):Model ={
		var model:Model = null;
			try {
				val templateReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(sourcePomFile));
				val reader = new MavenXpp3Reader();
				 model = reader.read(templateReader);
				 templateReader.close();
			} catch {
			  case th:Throwable => error(th, th)
			}

			return model;
	}
  
  // TODO Can this be merged into findDependency?
  def getDependency(dependencies:java.util.List[Dependency], artifactId:String):Dependency ={
		
		for( d <- dependencies ){
			if( d.getArtifactId() == artifactId ){
				return d;
			}
		}
		
		return null;
		
	}
  
  def findDependency(lst:java.util.List[Dependency], dep:Dependency, remove:Boolean = false, defaultGroupId:String=null):Boolean = {
    for(i <- 0 to lst.size-1) {
      val crtDep = lst.get(i)
      if(crtDep.getArtifactId() == dep.getArtifactId() && (if(crtDep.getGroupId==null) defaultGroupId == dep.getGroupId else if(dep.getGroupId==null) crtDep.getGroupId == defaultGroupId else crtDep.getGroupId() == dep.getGroupId())) {
        if(remove) {
          lst.remove(i)
        }
        return true;
      }
    }
    
    return false;
  }

  def removeChild(node:Xpp3Dom, childName:String) {
    val childIdx = getChildIndex(node, childName)
    
    if(childIdx != -1) {
      node.removeChild(childIdx)
    }
  }
  
  def getChildIndex(node:Xpp3Dom, childName:String):Int = {
    for(i <- 0 to node.getChildCount() - 1) {
      if(node.getChildren()(i).getName() == childName) {
        return i
      }
    }
    
    return -1
  }
  
  def findMBPInstruction(mbp:Plugin, instruction:String, caseSensitive:Boolean=true): Xpp3Dom = {
    if(mbp!=null) {
      val conf = mbp.getConfiguration()
      
      if(conf != null) {
        for(c <- conf.asInstanceOf[Xpp3Dom].getChildren();
            if "instructions" == c.getName();
            i <- c.getChildren();
            if(caseSensitive && instruction == i.getName() || !caseSensitive && instruction.equalsIgnoreCase(i.getName())) ) 
        return i
      }
    }
    
    null
  }
  
  def createMavenPlugin(groupId:String, artifactId:String, version:String=null):Plugin = {
    val p = new Plugin()
    p setGroupId groupId
    p setArtifactId artifactId
    p setVersion version
    p
  }
  
  def findPlugin(plugins:Seq[Plugin], groupId:String, artifactId:String):Plugin = {
    for(p <- plugins) {
      if (p.getGroupId == groupId && p.getArtifactId == artifactId) return p 
    }
    
    return null
  }
  
      
  def adjustPluginVersions(plugins:java.util.List[Plugin], managedPlugins:java.util.List[Plugin], report:PomReport, pluginManagement:Boolean = false) {
     var pluginsToRemove = Set[Plugin]()
     
     for(plugin <- asScalaBuffer(plugins)) {
       debug(s"Checking plugin $plugin")
       
       import scala.collection.JavaConversions._
       
       // Check if it's managed
       val managedPlugin = findPlugin(managedPlugins, plugin.getGroupId(), plugin.getArtifactId())
       
       if(managedPlugin != null) {
         val dep = createNiceDependency(plugin.getGroupId(), plugin.getArtifactId())
         
         report.addMissingArtifact(dep, NOT_MISSING, s"Removed version override for plugin managed by RaptorPlatform", null)
         plugin.setVersion(null)
       }
     }
     
     if(pluginManagement) for(plugin <- pluginsToRemove) plugins.remove(plugin)
   }
  
  
  def savePom(pomFile: File, model: Model, backup:Boolean=true): Unit = {
    if(backup) {
      val backupPomFile = new File(pomFile.getAbsolutePath() + ".bak." + System.currentTimeMillis())
      
      debug(s"Backing up POM to ${backupPomFile}")
      FileUtils.copyFile(pomFile, backupPomFile)
    }
    
    debug(s"Writing upgraded POM to ${pomFile}")
    
		val out = new BufferedWriter(new FileWriter(pomFile));
		val writer = new MavenXpp3Writer();
		writer.write(out, model);
		out.close();
  }

  def getRelativePathToParent(pomFile:File, parentPomFile:File):String= {
    if(parentPomFile == null) null
    else getRelativePath(pomFile, parentPomFile.getParentFile())
	}
  
  def getRelativePath(pomFile:File, parentPomFile:File):String = parentPomFile.toURI().relativize(pomFile.toURI()).toString();
 /*
    def checkDependenciesAvailability(deps:java.util.List[Dependency], updateToLatest:Boolean = false, report:PomReport, projectArtifacts:java.util.List[(File, Dependency)], dependencyManagement:Boolean, properties:List[java.util.Properties], projectGroupId:String=null) {
    var depsToRemove = Set[Dependency]()
    
    import scala.collection.JavaConversions._
    
    for(dep <- asScalaBuffer(deps)) {
      debug(s"Looking for artifact $dep")

      // Check if it belongs to the project
      if(findDependency(projectArtifacts.map(_._2), dep, false, projectGroupId)) {
        debug(s"Skipping dependency as it belongs to the project: $dep")
      } else
        if(findDependency(UpgradeMain.platformModel.getDependencyManagement().getDependencies(), dep, false)) { // Check if it's managed
          if(!StringUtils.isEmpty(dep.getVersion())) {
            if(dependencyManagement) {
              if(!UpgradeMain.provider) {
              depsToRemove += dep
              report.addMissingArtifact(dep, NOT_MISSING, s"Removed artifact from dependency management section because its version is managed by RaptorPlatform", null)
              }
            } else {
              if(!UpgradeMain.provider) {
                report.addMissingArtifact(dep, NOT_MISSING, s"Removed version override for artifact managed by RaptorPlatform", null)
                dep.setVersion(null)
              }
            }
          }
        } else {
          UpgradeMain.checkArtifactAvailability(dep, updateToLatest, report, properties)
        }
    }
    
    if(dependencyManagement) for(dep <- depsToRemove) deps.remove(dep)
  }*/
  
  def getLatestArtifactVersion(repo:String, gid:String, aid:String):String= {

    import org.ebaysf.ostara.upgrade.util.POMModifierUtil._

import org.apache.commons.lang.SystemUtils._

    try {
      return getLatestVersion(JAVA_IO_TMPDIR, repo, gid, aid, null)
    } catch {
      case th:Throwable => warn(th.getMessage, th); return null
    }
  }
  
  def getResponseCode(urlString:String):Integer = {
    val u = new URL(urlString); 
    val huc =  u.openConnection().asInstanceOf[HttpURLConnection]; 
    huc.setRequestMethod("GET");
    huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
    huc.connect(); 
    return huc.getResponseCode();
}
  
  // TODO this needs to be refined and errors different from 404 should at least be included in the report
  def artifactExists(aUrl:String):Boolean = {
    try {
    getResponseCode(aUrl) == 200
    } catch {
      case th:Throwable => warn(th, th); return false;
    }
  }
  
  def buildArtifactUrl(dep:Dependency, includeVersion:Boolean=false, props:List[java.util.Properties] = List()):String = {
    var versionString:String = ""
    
    if(includeVersion && !StringUtils.isEmpty(dep.getVersion)) {
	  versionString = "/" + evaluateVersion(dep, props)
  	}
    
    dep.getGroupId().replace('.', '/') + '/' + dep.getArtifactId() + versionString
  }
  
  /** Enrich the properties by including the builtin Maven ones when possible: http://docs.codehaus.org/display/MAVENUSER/MavenPropertiesGuide */
  def extractModelProperties(model:Model):Properties ={
    val outProps = new Properties()
    def v = model.getVersion()
    
    if(!StringUtils.isEmpty(v)) {
      outProps.put("version", v)
      outProps.put("project.version", v)
      outProps.put("pom.version", v)
      
      for(entry <- scala.collection.JavaConversions.propertiesAsScalaMap(model.getProperties)) {
        outProps.put(entry._1, evaluateVersion(entry._2, List(outProps, model.getProperties)))
      }
    } else {
      outProps.putAll(model.getProperties())
    }
    
    
    return outProps
  }
  
  def evaluateVersion(dep:Dependency, props:List[Properties] = List()):String={
    dep.setVersion(evaluateVersion(dep.getVersion, props))
    return dep.getVersion
  }
  
  def evaluateVersion(version:String, props:List[Properties]):String={
    if(version != null && version.contains('$') && props != null) {
		for(prop <- props) {
		  if(prop != null) {
		    val map = new HashMap[String, String]()
		      
		    for (name <- prop.stringPropertyNames.toArray(Array[String]())) {
		     	map.put(name, prop.getProperty(name));
		    }
		        
		  	return StrSubstitutor.replace(version, map)
		  }
		}
    }
	
    return version
  }
  
  def cloneDependency(dep:Dependency, props:List[java.util.Properties] = List()):Dependency={
    // TODO Kind of error prone, maybe the Maven code has this logic already?
    val clonedDep = createDependency(dep.getGroupId(), dep.getArtifactId(), evaluateVersion(dep, props), dep.getScope(), dep.getType())
    clonedDep.setExclusions(dep.getExclusions())
    clonedDep.setClassifier(dep.getClassifier())
    clonedDep.setOptional(dep.getOptional) // Work with the string value as the boolean signatures are just a wrapper
    clonedDep.setSystemPath(dep.getSystemPath)
    
    return clonedDep
  }
  
  def createXpp3Dom(name:String, value:String):Xpp3Dom = {
    val node = new Xpp3Dom(name)
    node.setValue(value)
    return node
  }

  def createNiceDependency(groupId:String, artifactId:String, version:String = null, scope:String = null, atype:String = null):NiceDependency = new NiceDependency(createDependency(groupId, artifactId, version, scope, atype))
  def createNiceDependency(d:Dependency):NiceDependency = new NiceDependency(d)
  
  def createDependency(groupId:String, artifactId:String, version:String = null, scope:String = null, atype:String = null): Dependency = {
    var dep:Dependency = new Dependency();
    setGAV(dep, groupId, artifactId, version)
    dep.setScope(scope)
    dep.setType(atype)
    dep
  }
  
  def setGAV(artifact: {def setGroupId(value: String); def setArtifactId(value: String); def setVersion(v: String)}, 
                    groupId:String, artifactId:String, version:String = null): Unit = {
    artifact.setGroupId(if(groupId != null) groupId.trim else null)
    artifact.setArtifactId(artifactId.trim)
    artifact.setVersion(if(version != null) version.trim else null)
  }
}
