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

package org.ebaysf.ostara.upgrade.paths

import org.ebaysf.ostara.upgrade.NiceDependency
import java.io.File
import org.ebaysf.ostara.upgrade.PomReport
import org.apache.maven.model.Plugin
import org.apache.maven.model.Model
import org.apache.commons.cli.CommandLine
import org.apache.maven.model.Dependency
import org.apache.maven.model.Parent
import org.ebaysf.ostara.upgrade.UpgradeReportBuilder

object PreprocessResult extends Enumeration {
  type Type = Value
  val DoNothing, RemoveProject = Value
}

abstract class UpgradeStep {
  val PROJECT_TYPE_WAR = "war"
  val PROJECT_TYPE_ALL = "special all type"
  
  def getSourceVersion: PlatformVersion = null
  def getTargetVersion: PlatformVersion = null

  def getMergedArtifacts: Map[List[NiceDependency], NiceDependency] = Map()
  def getUnmergedArtifacts: Map[NiceDependency, List[NiceDependency]] = Map()
  def getMappedArtifacts: Map[NiceDependency, NiceDependency] = Map()
  def getRemovedArtifacts: List[NiceDependency] = List()
  def getAddedArtifacts: List[(String, NiceDependency)] = List()
  
  def getMappedPlugins: Map[NiceDependency, NiceDependency] = Map()
  def getRemovedPlugins: List[NiceDependency] = List()
  
  def runAddons(projectRootDir:File, projectEncoding:String, urb:UpgradeReportBuilder) {}

  def preprocessProject(pomFile:File, parentPom:Model, line:CommandLine, crtReport:PomReport):PreprocessResult.Type = { return PreprocessResult.DoNothing }
  def postprocessProject(pomFile:File, model:Model) {}
  
  def postProcessAll(urb:UpgradeReportBuilder): Unit = {}
  
  def adjustParentDependencies(parentPom: Model, model: Model, pomFile: File) {}
  
  def checkPlatformAncestor(parent: Parent, adjust:Boolean = true): Boolean = false
  
  /**
   * @return null if no transformation was performed on the dependency
   */
  def transformDependency(d:Dependency, bHasParent:Boolean, crtReport:PomReport):List[NiceDependency] = null
  
  def processWebOrServiceProject(model:Model, pomFile:File, crtReport:PomReport, urb:UpgradeReportBuilder):java.util.List[Dependency] = new java.util.ArrayList[Dependency]()
  
  def processBatchProject(model:Model, pomFile:File, parentPomFile:File, crtReport:PomReport, urb:UpgradeReportBuilder) {}
}
