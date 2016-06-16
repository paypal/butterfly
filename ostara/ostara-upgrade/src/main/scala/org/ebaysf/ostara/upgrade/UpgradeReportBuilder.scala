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

import grizzled.slf4j.Logging
import org.apache.commons.io.FilenameUtils
import java.io.File
import org.apache.maven.model.Dependency

class BaseReport extends Logging {
  var automated:Boolean = _;
  
  var manualChanges = Map[String, String]()
  var warnings = Map[String, String]()

  def addManualChange(description:String, action:String) {
    warn(s"Description: $description '\n'Action: $action")
    manualChanges += (description -> action)
  }
}

object PomReport {
  val NOT_MISSING = 1
  val MISSING_PROVIDER = 2
  val MISSING_THIRDPARTY = 3
}

class PomReport extends BaseReport {
  var missingArtifacts = Map[NiceDependency, (Int, String, List[String] /*repos*/)]()
  var mergedArtifacts = Map[List[NiceDependency], NiceDependency]()
  var unmergedArtifacts = Map[NiceDependency, List[NiceDependency]]()
  var mappedArtifacts = Map[NiceDependency, NiceDependency]()
  var addedArtifacts = List[NiceDependency]()
  var removedArtifacts = List[NiceDependency]()
  var mappedPlugins = Map[NiceDependency, NiceDependency]()
  var removedPlugins = List[NiceDependency]()
  
  def addMissingArtifact(dep:Dependency, depType:Int, description:String, repo:List[String]) {
    addMissingArtifact(new NiceDependency(dep), depType, description, repo)
  }
  
  def addMissingArtifact(dep:NiceDependency, depType:Int = PomReport.NOT_MISSING, description:String, repo:List[String]=null) {
    warn(s"Dependency analysis of $dep: \n$description")
    missingArtifacts += (dep -> (depType, description, repo))
  }
}

class JavaFileReport extends BaseReport {
  var changes = 0
}

class WebXmlReport extends BaseReport {
  var changes = 0
}

/**
 * File specific changes
 */
class FileReport extends BaseReport {
  var message:String = _;

  def this(msg:String, atmtd:Boolean) {
    this()
    this.message = msg
    automated = atmtd
  }
}

/**
 * @author renyedi
 */
class UpgradeReportBuilder extends Logging {
  var projectArtifacts = Set[(File, Dependency)]()
  var pathToParentPom:File = _
  var changes = Map[String/*relative path*/, BaseReport]()
  
  var manualChecksAndChanges = List[String]()
  
  def appMetadataFileName:String = ""
  def platformName:String = "Ostara dummy platform"
  def disclaimer(teamDL:String):String = "This is totally safe to use, probably"
  def additionalManualChecksAndChanges:String = "N/A"
    
  /**
   * Syntax reference https://help.github.com/articles/github-flavored-markdown
   */
  def buildGitHubMarkdownReport(teamDL:String, taskId:String, relativePath:String):String = {
    // Construct the summary data
    var manualChanges = 0
    var pomFileChanges = 0
    var webXmlFileChanges = 0
    var totalWebXmlChanges = 0
    var appMetadataFileChanges = 0
    var javaFileChanges = 0
    
    for(change <- changes) {
      manualChanges += change._2.manualChanges.size
      
      if(change._1.endsWith("web.xml")) {webXmlFileChanges+=1; totalWebXmlChanges += change._2.asInstanceOf[WebXmlReport].changes}
      else if(change._1.endsWith(appMetadataFileName)) appMetadataFileChanges+=1
      else if(change._1.endsWith(".java")) javaFileChanges+=1
      else pomFileChanges+=1
      
    }
    
    
    var report = s"""
# Project upgrade report to ${platformName}
## Project details
Name | Description
---- | -----------
Path to project POM |	${relativePath}
Upgrade job ID | ${taskId}
Full upgrade log | [link](platform-upgrade-debug-${taskId}.log)
Upgrade warnings only log | [link](platform-upgrade-warn-${taskId}.log)

### Artifacts
This upgrade request processed only the ${projectArtifacts.size} Maven project artifacts that were referenced directly or indirectly by the project's parent POM:

| No. | POM file | Artifact ID |
| ---:| -------- | ----------- |
"""

	  var pomCount = 0

		for(crtModule <- projectArtifacts) {
		  pomCount += 1
		  val crtFile = pathToParentPom.toURI().relativize(crtModule._1.toURI())
		  report += s"| $pomCount | [$crtFile]($crtFile) | ${crtModule._2.getArtifactId()} |\n"
		}

report += s"""

## Disclaimer
${disclaimer(teamDL)}

## Summary
Operation | Details
---- | -----------
[Manual changes](#manualChanges) | ${manualChanges} required by user
[Additional manual checks and changes](#manualChecksAndChanges) | Potentially several, depending on the project
[Automated changes](#automatedChanges) were applied to | ${pomFileChanges} POM file(s), ${totalWebXmlChanges} changes in ${webXmlFileChanges} web.xml file(s), ${appMetadataFileChanges} ${appMetadataFileName} file(s), ${javaFileChanges} Java file(s)

<a name="manualChanges"/>
## Pending manual operations
"""
    if(hasNoManualChanges && hasNoMissingDependencies) {
      report += "No manual operations seem to be required. Please note that this is no guarantee that your project will build and run without errors.\n"
    } else {
      if(!hasNoManualChanges) {
        report +=
s"""
### Code changes

The following code changes could not be performed by the ${platformName} upgrade tool. You will need to do them manually by following the recommended approach.

"""
          for(change <- changes) {
            if(!change._2.manualChanges.isEmpty) {
              val crtFile = FilenameUtils.separatorsToUnix(change._1)
              
                report += 
s"""
### File [$crtFile]($crtFile)

Problem | Action
------- | ------
"""
            for(manualChange <- change._2.manualChanges) {
              report += manualChange._1 + " | " + manualChange._2 + "\n"
            }
          }
        }
      }
      
      if(!hasNoMissingDependencies) {
        report +=
s"""
### Missing dependencies

<p>NOTE: This is an experimental feature and might not be fully accurate
<p>The following dependencies could not be found in the ${platformName} Maven repository (ebaycentral) and have been adjusted or need additional changes.

"""
          for(change <- changes) {
            if(change._2.isInstanceOf[PomReport] && !change._2.asInstanceOf[PomReport].missingArtifacts.isEmpty) {
              val crtFile = FilenameUtils.separatorsToUnix(change._1)
              
                report += 
s"""
### File [$crtFile]($crtFile)

Artifact | Description
-------- | -----------
"""
            for(missingArtifact <- change._2.asInstanceOf[PomReport].missingArtifacts) {
              report += missingArtifact._1 + " | " + missingArtifact._2._2 + "\n"
            }
          }
        }
      }
    }
    
    report +=
s"""
<a name="manualChecksAndChanges"/>
## Additional manual checks and changes
"""

  for(manualCheckAndChange <- manualChecksAndChanges) {
    report += manualCheckAndChange
  }

  for(change <- changes;
      if !change._2.automated;
      if change._2.isInstanceOf[FileReport]) {
        val crtFile = FilenameUtils.separatorsToUnix(change._1)

        report +=
s"""
### File [$crtFile]($crtFile)
"""

      val fileReport: FileReport = change._2.asInstanceOf[FileReport]
      report += fileReport.message
    }

    report +=
s"""
<a name="automatedChanges"/>
## Automated upgrade operations
This section lists all the changes that were automatically applied to the original project.
Please consult the changeset for the definitive list of changes made by the upgrade service as some of those might not be described below.

    """
   for(change <- changes;
     if change._2.automated) {
     val crtFile = FilenameUtils.separatorsToUnix(change._1)

     report += 
s"""
### File [$crtFile]($crtFile)
"""

     
     if(change._2.isInstanceOf[PomReport]) {
       val pomChange = change._2.asInstanceOf[PomReport]
      if(!pomChange.mergedArtifacts.isEmpty) {
        report +=
s"""
#### Merged artifacts
The following groups of artifacts were consolidated into a single one.

Artifact group | Consolidated artifact
-------------- | ---------------------
"""
        for(crtGroup <- pomChange.mergedArtifacts) {
          report += crtGroup._1.mkString("\n") + " | " + crtGroup._2.toString + "\n"
        }
      }
      
      if(!pomChange.unmergedArtifacts.isEmpty) {
        report +=
s"""
#### Unmerged artifacts
The following groups of artifacts were expanded into individual ones.

Artifact | Expanded artifacts
-------- | ------------------
"""
        for(crtGroup <- pomChange.unmergedArtifacts) {
          report += "| " + crtGroup._1.toString + " | " + crtGroup._2.mkString(", ") + " |\n"
        }
      }
      
      if(!pomChange.mappedArtifacts.isEmpty) {
        report +=
s"""
#### Mapped artifacts
The following groups of artifacts were mapped to new ones.

Original artifact | Mapped artifact
----------------- | ---------------
"""
        for(crtGroup <- pomChange.mappedArtifacts) {
          report += crtGroup._1.toString + " | " + crtGroup._2 + "\n"
        }
      }
            
      if(!pomChange.addedArtifacts.isEmpty) {
        report +=
s"""
#### Added artifacts
The following artifacts were added to the POM:

"""
        for(crtArtifact <- pomChange.addedArtifacts) {
          report += "1. " + crtArtifact.toString + "\n"
        }
      }
      
      if(!pomChange.removedArtifacts.isEmpty) {
        report +=
s"""
#### Removed artifacts
The following artifacts were removed from the POM:

"""
        for(crtArtifact <- pomChange.removedArtifacts) {
          report += "1. " + crtArtifact.toString + "\n"
        }
      }
      
      if(!pomChange.mappedPlugins.isEmpty) {
        report +=
s"""
#### Mapped plugins
The following plugins were mapped to new ones.

Original plugin | Mapped plugin
--------------- | -------------
"""
        for(crtGroup <- pomChange.mappedPlugins) {
          report += crtGroup._1.toString + " | " + crtGroup._2 + "\n"
        }
      }
      
      if(!pomChange.removedPlugins.isEmpty) {
        report +=
s"""
#### Removed plugins
The following plugins were removed from the POM:

"""
        for(crtPlugin <- pomChange.removedPlugins) {
          report += "1. " + crtPlugin.toString + "\n"
        }
      }
    } else 
      if(change._2.isInstanceOf[WebXmlReport]) {
        report += "Various web.xml changes.\n"
      } else
      if(change._2.isInstanceOf[JavaFileReport]) {
        report += "Java code changes.\n"
      } else
      if(change._2.isInstanceOf[FileReport]) {
       val fileReport: FileReport = change._2.asInstanceOf[FileReport]
       report += fileReport.message
      } else {
        val additionalReport = handleAdditionalReportTypes(change._2)
        
        if(additionalReport != null) {
          report += additionalReport;
        } else {
	        warn("Unknown report type. Ignoring")
	        report += "N/A \n"
        }
      }
    }
    
    return report
  }
  
  def handleAdditionalReportTypes(report: BaseReport):String = null
  
  def hasNoManualChanges : Boolean = {
    for(change <- changes) {
      if(!change._2.manualChanges.isEmpty) {
        return false
      }
    }
    
    true
  }
  
  def hasNoMissingDependencies : Boolean = {
    for(change <- changes) {
      if(change._2.isInstanceOf[PomReport] && !change._2.asInstanceOf[PomReport].missingArtifacts.isEmpty) {
        return false
      }
    }
    
    true
  }
}
