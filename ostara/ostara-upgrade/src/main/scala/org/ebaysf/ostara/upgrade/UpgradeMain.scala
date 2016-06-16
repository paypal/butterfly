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
import java.io.IOException
import java.util.ArrayList
import java.util.Properties
import scala.collection.JavaConversions._
import scala.collection.mutable.MutableList
import org.apache.commons.cli.BasicParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.{SystemUtils, StringUtils}
import org.apache.log4j.PropertyConfigurator
import org.apache.maven.model.Dependency
import org.apache.maven.model.DependencyManagement
import org.apache.maven.model.DeploymentRepository
import org.apache.maven.model.Model
import org.apache.maven.model.Parent
import org.apache.maven.model.Plugin
import org.apache.maven.model.Repository
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.codehaus.plexus.util.xml.Xpp3Dom
import grizzled.slf4j.Logger
import grizzled.slf4j.Logging
import org.ebaysf.ostara.telemetry.mongodb._
import org.ebaysf.ostara.upgrade.paths.UpgradeStep
import org.ebaysf.ostara.upgrade.paths.UpgradeAddonRegistry
import org.ebaysf.ostara.upgrade.paths.PreprocessResult
import org.ebaysf.ostara.upgrade.paths.PlatformVersionManager
import org.ebaysf.ostara.upgrade.paths.DummyPlatformVersionManager
import org.ebaysf.ostara.upgrade.util._
import MigratorUtils._

class UpgradeMain extends Logging {
  val APP_TYPE_MESSAGING = "messaging"
  val APP_TYPE_BATCH = "batch"
  val APP_TYPE_SERVICE = "service"
    
  def MAVEN_REPO_OLD_THIRDPARTY:String = ""
  def MAVEN_REPO_OLD_RELEASES:String = ""
  def MAVEN_REPOS_OLD:List[String] = List()
  def MAVEN_REPO_NEW_THIRDPARTY:String = ""
  def MAVEN_REPO_NEW_RELEASES:String = ""
  def MAVEN_REPOS_NEW:List[String] = List()
  def MAVEN_REPO_NEW_CENTRAL_PROXY:String = "https://repo1.maven.org/maven2/"
  def OLD_THIRDPARTY_GROUPID_PREFIX:String = ""
  
  def platformGroupId:String = "org.ostara"
  def platformArtifactId:String = "ostara-supported-platform"
  def defaultPlatformVersion:String  = "1.0.0"
    
  var platformVersion:String = defaultPlatformVersion
  private var _platformAppTypeOverride:String = _

  def DEFAULT_LATEST_PLATFORM_VERSION = ""
  
  var taskid:String = _
  var disableBackup:Boolean = _
  var disableArtifactsScanning:Boolean = _
  
  var provider:Boolean = false
  
  /** */
  var forceLatestVersion:Boolean = _
  
  def platformAppTypeOverride = _platformAppTypeOverride
  
  def platformAppTypeOverride_=(value:String) {
    info(s"Overriding Platform App type to $value")
    _platformAppTypeOverride = value
  }
  
  var platformAppTypes:List[String] = List()
  var platformAppNames:List[String] = List()
  var oldPlatformVersions:List[String] = List()
    
  var R2U_VERSION = "0.5.0-SNAPSHOT"
  val CMDLINE = s"java -jar r2u-$R2U_VERSION.jar"
    
  val INPUT_OPTION = "i"
  val HELP_OPTION = "h"
  val PLATFORM_VERSION_OPTION = "R"
  val DELETE_EBA_OPTION = "E"
  val APP_TYPE_OVERRIDE_OPTION = "t"
  val SKIP_ADDONS_OPTION = "s"
  val TASKID_OPTION = "d"
  val FORCE_LATEST_VERSION_OPTION = "f"
  val DISABLE_BACKUP_OPTION = "b"
  val DISABLE_ADDONS_OPTION = "a"
    
  var parentPomFile:File = _

  def platformModel: Model = null
  
  var projectArtifacts = List[(File, Dependency)]()
  
  var line:CommandLine = null
  val options = new Options()
  
  var upgradePath:Array[UpgradeStep] = Array()
  
  def urb:UpgradeReportBuilder = new UpgradeReportBuilder()
  
  def main(arg: Array[String]) {
  try {
    line = new BasicParser parse (buildCmdOptions, arg)
    
    if(line.hasOption(HELP_OPTION)) {
      new HelpFormatter().printHelp(CMDLINE, options)
    } else {
      if(line.hasOption(TASKID_OPTION)) {
        taskid = line.getOptionValue(TASKID_OPTION)
      }
    
		  // And then we just zap the configuration and overwrite it with our own. Ours is better, always.
		  val log4jConfig = IOUtils.toString(getClass().getResourceAsStream("/config/log4jconfig.properties"))
		  val modifiedLog4jConfig = org.apache.commons.lang.text.StrSubstitutor.replace(log4jConfig, Map("logSuffix" -> buildUniqueFileSuffix()))
		  
		  PropertyConfigurator.configure(new java.io.ByteArrayInputStream(modifiedLog4jConfig.getBytes()))
      
      val inputOptionValue = if(line.getOptionValue(INPUT_OPTION) == null) new File(".").getCanonicalPath() else line.getOptionValue(INPUT_OPTION)
    
      debug(s"Starting r2u version $R2U_VERSION...")
      
      val disabledAddonsCmdValue = line.getOptionValue(DISABLE_ADDONS_OPTION)
      if(disabledAddonsCmdValue != null) {
        UpgradeAddonRegistry.disable(disabledAddonsCmdValue.split(','))
      }
      
      platformVersion=MigratorUtils.getLatestArtifactVersion(MAVEN_REPO_NEW_RELEASES, platformGroupId, platformArtifactId)
      
      if(platformVersion == null) {
        platformVersion = defaultPlatformVersion
        warn(s"Could not detect the latest version of the platform. Falling back to $platformVersion")
      } else {
        info(s"Detected latest version of the platform: $platformVersion")
      }
      
      val platformVersionOptionVal = line.getOptionValue(PLATFORM_VERSION_OPTION)
      if (platformVersionOptionVal != null) {
        platformVersion = platformVersionOptionVal
        
        info(s"Overriding platform version with $platformVersion")
      }
      
      if(line.hasOption(APP_TYPE_OVERRIDE_OPTION)) {
        platformAppTypeOverride = line.getOptionValue(APP_TYPE_OVERRIDE_OPTION)
        warn(s"Platform app type is overridden to $platformAppTypeOverride")
      }
      
      if(line.hasOption(DISABLE_BACKUP_OPTION)) {
        disableBackup = true
      }
      
      forceLatestVersion = line.hasOption(FORCE_LATEST_VERSION_OPTION)
      
      var inputLocation = new File(inputOptionValue)
      
      // Make input location an absolute path
      if(!inputLocation.isAbsolute()) inputLocation = new File(".", inputOptionValue).getCanonicalFile()
      
      if(migrateAll(inputLocation)) {
        info("DONE")
        
        val upgradeReportFile = new File("platform-upgrade-report" + buildUniqueFileSuffix() + ".md")
        FileUtils.writeStringToFile(upgradeReportFile, urb.buildGitHubMarkdownReport(platformGroupEmail(), if(taskid != null) taskid else "N/A", inputOptionValue))
        logger.info(s"Wrote upgrade report file to " + upgradeReportFile.getName())
        
        logger.info("""NOTE: You might need to re-import the projects into your IDE. In Eclipse/RIDE use the "Import / Maven / Existing Maven Projects" wizard.""")
        
        saveToTelemetry();
      } else {
        logger.error("FAILED")
        System.exit(3)
      }
    }
  } catch {
    case ex:ParseException => {error(s"Invalid command line sytax: \n ${ex.getMessage}\n"); new HelpFormatter().printHelp(CMDLINE, options); System.exit(2);}
  }
  }
  
  def platformGroupEmail():String = ""
  
  def buildUniqueFileSuffix():String = (if(taskid != null) ("-" + taskid) else "")
  
  def saveToTelemetry() {
    if(taskid != null) {
      info(s"Saving telemetry in DB for task id $taskid")
      val data = new TelemetryData();
      
      data.setTaskId(taskid)
      data.setMigrationToolVersion(R2U_VERSION)
      
      data.setDetectedAppType(platformAppTypes)
      data.setOverrideAppType(platformAppTypeOverride)
      data.setAppName(platformAppNames)
      
      data.setNewPlatformVersion(platformVersion)
      data.setOldPlatformVersion(if(oldPlatformVersions.isEmpty) List(platformVersion) else oldPlatformVersions)

      val reportedArtifacts = new scala.collection.mutable.ArrayBuffer[String]()
      
      for(change <- urb.changes) {
        if(change._2.isInstanceOf[PomReport]) {
          val pomReport = change._2.asInstanceOf[PomReport]
          
          for((dep, (missingType, message, repos)) <- pomReport.missingArtifacts) {
            if(missingType != PomReport.NOT_MISSING) {
              val key = dep.getGroupId + ":" + dep.getArtifactId
              
              if("ShipmentTrackingService".equals(dep.getArtifactId)) {
                println("HERE")
              }
              
              if(!reportedArtifacts.contains(key)) { // Insert missing artifact only once
	              val ad = new ArtifactsData()
	              ad.setTaskId(taskid)
	              ad.setType(if(missingType == PomReport.MISSING_THIRDPARTY) "thirdparty" else "provider")
	              ad.setGroupId(dep.getGroupId)
	              ad.setArtifactId(dep.getArtifactId)
	              
	              reportedArtifacts += key
	              
	              if(repos != null) {
	                for(repo <- repos) {
	                  val props = 
		                  if(!StringUtils.isEmpty(dep.getDependency.getVersion)) {
		                    NexusUtils.extractBuildinfo(repo, dep.getDependency)
		                  } else {
		                    val depLatest = MigratorUtils.cloneDependency(dep.getDependency)
		                    depLatest.setVersion(MigratorUtils.getLatestArtifactVersion(repo=repo,depLatest.getGroupId(), depLatest.getArtifactId()))
		                    
		                    info(s"Artifact $dep has no version so taking the latest available in $repo: ${depLatest.getVersion}")
		                    
		                    NexusUtils.extractBuildinfo(repo, depLatest)
		                  }
	                  
	                  if(props != null) {
	                    val gitUrl = NexusUtils.getGitUrl(props)
	                    ad.setGitUrl(gitUrl)
	                    ad.setBranch(NexusUtils.getGitBranch(props))
	                    ad.setOwner(NexusUtils.getCommitter(props))
	                    
	                    ad.setGitCommitters(GitUtils.getListOfGitCommitters(gitUrl).mkString(", "))
	                  }
	                }
	              }
	              
	              info(s"Storing artifact data for $dep")
	              
	              try {
	            	  TelemetryDAO.getInstance().insertData(ad)
	              } catch {
	                case th:Throwable => warn("Could not save artifact data", th)
	              }
              }
            }
          }
        }
      }
      
      try {
      	TelemetryDAO.getInstance().insertData(data)
      } catch {
        case th:Throwable => warn("Could not save telemetry", th)
      }
      
      info("Save complete")
    } else {
      info("No telemetry saved as task ID is missing")
    }
  }
  
  def buildCmdOptions():Options = {
    options	.addOption(INPUT_OPTION, "input", true, s"The project's root directory which contains the top-level pom or full path to the pom file. Defaults to pom.xml in current directory.")
            .addOption(HELP_OPTION, "help", false, s"Displays this help.")
        .addOption(PLATFORM_VERSION_OPTION, "platformversion", true, s"The project's platform version. Defaults to $platformVersion")
        .addOption(APP_TYPE_OVERRIDE_OPTION, "appTypeOverride", true, s"Override the platform app type. It will be autodetected if not specified.")
        .addOption(SKIP_ADDONS_OPTION, "skipAddons", false, s"Do not run any addon operations.")
        .addOption(DELETE_EBA_OPTION, "deleteEbaProject", false, "Physically deletes the EBA project, not just the reference from the top-level POM")
        .addOption(TASKID_OPTION, "taskid", true, "[Web UI only] Upgrade task ID")
        .addOption(FORCE_LATEST_VERSION_OPTION, "forceLatestVersion", false, "Force updating versions of missing dependencies to the latest in platform repository")
        .addOption(DISABLE_BACKUP_OPTION, "disableBackup", false, "Do not take back up of the modified files")
        .addOption(DISABLE_ADDONS_OPTION, "disableAddons", true, s"Comma separated list of addons to be disabled. Available addons: ${UpgradeAddonRegistry.addons.map(_._1)}")
        
    options
  }
  
  /**
   * Migrates the project and its submodules.
   * 
   * @param parent the directory containing the top-level POM
   */
  def migrateAll(parent: File):Boolean= {
    platformAppTypes = List()
    platformAppNames = List()
    oldPlatformVersions = List()
    
    parentPomFile = if(parent.isFile()) parent else new File(parent, MigratorUtils.POM_XML);
    urb.pathToParentPom = parentPomFile.getParentFile()
    
    if(!parentPomFile.exists()) {
      error(s"Could not find parent ${MigratorUtils.POM_XML} in the specified directory: ${parent.getAbsolutePath}")
      
      return false
    }

    analyzeProject(parentPomFile)
    
    info("Upgrading top-level POM")
    
    detectPlatformApplicationData(parentPomFile)
    
    val parentPom = migrateProject(parentPomFile, doSavePom=false) // Save it only at the end
    
    if(parentPom != null) {
      processedModules.clear
      processProjectModules(parentPom, parent)
      MigratorUtils.savePom(parentPomFile, parentPom, !disableBackup)
    }
    
    for(crtPathSegment <- upgradePath) {
      crtPathSegment.postProcessAll(urb)
    }
    
    true
  }

  def analyzeProject(parentPomFile:File) {
    val parentPom = MigratorUtils.readPom(parentPomFile)
    
    if(parentPom != null) {
      for(m <- parentPom.getModules()) {
        analyzeModule(m, parentPomFile)
      }
    }
    
    for(p <- parentPom.getProfiles()) {
      for(m <- p.getModules()) {
        analyzeModule(m, parentPomFile)
      }
    }
    
    info(s"Detected ${projectArtifacts.size} project artifacts: $projectArtifacts")
    
    urb.projectArtifacts ++= projectArtifacts
  }
  
  def analyzeModule(m:String, parentPomFile:File) {
    try {
      val pomFile = MigratorUtils.getPomFromDirectory(m, parentPomFile)
	    val model = MigratorUtils.readPom(pomFile)
	    projectArtifacts ::= (pomFile, MigratorUtils.createDependency(model.getGroupId(), model.getArtifactId()))
    } catch {
      case ex:IOException => warn(ex, ex)
    }
  }
  
  def getPlatformAppTypes():List[String] = (if(platformAppTypeOverride != null) List(platformAppTypeOverride) else platformAppTypes).map(/* inefficient, but elegant */ _.toLowerCase)
    
  def detectPlatformApplicationData(parentPomFile:File, force:Boolean = false) = {}
  
  val processedModules = MutableList[String]()
  
  def processProjectModules(parentPom:Model, parent:File):List[String] = {
      // Collects all processed modules to prevent repeated processing of a module (a module can be in all profiles, not just one)
      
      for(m <- Array(parentPom.getModules()).flatten) {
        val nM = MigratorUtils.getNormalizedAbsoluteModuleName(parent, m)
        if(!processedModules.contains(nM)) {
          processModule(m, parent, parentPom)
          processedModules += nM
        }
      }
      
      for(p <- parentPom.getProfiles()) {
        for(m <- Array(p.getModules()).flatten) {
          val nM = MigratorUtils.getNormalizedAbsoluteModuleName(parent, m)
          if(!processedModules.contains(nM)) {
            processModule(m, parent, parentPom)
            processedModules += nM
          }
        }
      }
      
      return processedModules.toList
  }

  def processModule(m:String, parent:File, parentPom:Model) {
    val modulePomFile = MigratorUtils.getPomFromDirectory(m, parent)
    
    val thePom = MigratorUtils.readPom(modulePomFile)
    
    if(!thePom.getModules().isEmpty()) {
      info("Identified nested top-level pom: " + modulePomFile)
      detectPlatformApplicationData(modulePomFile)
      processProjectModules(thePom, modulePomFile)
    } else
    if(modulePomFile.exists()) {
      migrateProject(modulePomFile, parentPom)
    } else {
      warn(s"Skipping module $m because no POM was found: ${modulePomFile.getAbsolutePath()}")
    }
  }
  
  def migrateProject(pomFile: File, parentPom:Model=null, doSavePom:Boolean=true, crtUpgradePaths:Array[UpgradeStep] = upgradePath): Model = {
    info(s"Processing POM: ${pomFile.getAbsolutePath}")
    

    import java.nio.file._
    
    val crtReport = new PomReport()
    urb.changes += (MigratorUtils.getRelativePathToParent(pomFile, parentPomFile) -> crtReport)
    
    try {
      for(crtPath <- crtUpgradePaths
        if(crtPath.preprocessProject(pomFile, parentPom, line, crtReport) == PreprocessResult.RemoveProject)) {
          info(s"Stopping preprocessing since project was removed")
          return null
        }
      
      val model = migratePom (pomFile, parentPom, crtReport , crtUpgradePaths)
      
      if(pomFile.exists) {       
        for(crtPath <- crtUpgradePaths) {
          crtPath.postprocessProject(pomFile, model)
        }
        
        if(doSavePom) MigratorUtils.savePom(pomFile, model, !disableBackup)
        
        if(model.getPackaging() == "pom") {
          info("No addons run on a POM aggregator project")
         } else {
          info("Running addons")
          if (line != null && line.hasOption(SKIP_ADDONS_OPTION)) {
            info("Skipping addons per user's request")
          } else {
            // Checking for encoding overrides. Note that these are _not_ inherited from parent (http://docs.codehaus.org/display/MAVENUSER/MavenPropertiesGuide).
            val sourceEncoding = model.getProperties.getProperty("project.build.sourceEncoding", System.getProperty(SystemUtils.FILE_ENCODING))
            info(s"Running addons with $sourceEncoding encoding")
          
            for (u <- upgradePath) {
              try {
                u.runAddons(pomFile.getParentFile, sourceEncoding, urb)
              } catch {
                case th: Throwable => warn(s"Fatal error while running an addon. Consider running the tool again with the $SKIP_ADDONS_OPTION option.", th)
              }
            }
          }
        }
        
        model
      } else null /* The POM file was deleted. Forget about it. */
    } catch {
      case ex:Throwable => warn(s"Skipping file and project due to error: $ex", ex); return null;
    }
  }
  
  def getPlatformVersionManager():PlatformVersionManager = new DummyPlatformVersionManager()
  
  def beforeMigratePom(parentPom:Model, model:Model) = {}
  
  def migratePom(pomFile: File, parentPom: Model, crtReport:PomReport, crtUpgradePaths:Array[UpgradeStep]): Model = {
    val model = MigratorUtils.readPom(pomFile)
    if (model == null) return null
    
    val parent = model.getParent
    
    if(checkPlatformAncestor(parent, false)) {
      info("A platform parent was detected, adjusting upgrade paths accordingly")
      
      val platformSourceVersion = getPlatformVersionManager.parseVersion(parent.getVersion())
      platformVersion = getPlatformVersionManager.processPlatformVersion(platformVersion, platformSourceVersion);
      val platformTargetVersion = getPlatformVersionManager.parseVersion(platformVersion)
      
      upgradePath = getPlatformVersionManager.upgradePaths(platformSourceVersion, platformTargetVersion, parent, crtReport)
      
      info(s"Detected platform version ${parent.getVersion()} and applying these upgrade paths: ${upgradePath.map(_.getClass().getSimpleName()).mkString(", ")}")
    }
    
    beforeMigratePom(parentPom, model)
    
    for(crtPath <- crtUpgradePaths) crtPath.adjustParentDependencies(parentPom = parentPom, model = model, pomFile)
    	
    upgradeRepos(model)
    
    var bHasParent = false
    
    if(model.getParent == null) {
      bHasParent = true
      
      val parent = new Parent()
      setPlatformParent(parent)
      model.setParent(parent)
      
      info(s"Project has no parent. Setting its parent to $parent")
    } else {
      for(crtPath <- crtUpgradePaths) {
	      bHasParent = crtPath.checkPlatformAncestor(model.getParent())
	      
	      if(!bHasParent && parentPom != null) {
	        bHasParent = crtPath.checkPlatformAncestor(parentPom.getParent())
	      }
      }
    }
    
    val parentPomProperties = if(parentPom != null) MigratorUtils.extractModelProperties(parentPom) else null

    val projectGroupId = if(model.getGroupId()!=null)model.getGroupId() else if(parentPom != null) parentPom.getGroupId() else null
    
    // Remove versions from managed plugins
    if(model.getBuild() != null && model.getBuild().getPluginManagement() != null) {
       MigratorUtils.adjustPluginVersions(model.getBuild().getPluginManagement().getPlugins(), platformModel.getBuild().getPluginManagement().getPlugins(), crtReport, true)
    }
    
    val dependencyManagement = processDependencyManagement(model, crtReport, false, parentPomProperties, projectGroupId, model.getPackaging(), crtUpgradePaths) // always process since we may add dependency management
    if (!dependencyManagement.getDependencies().isEmpty()) {
      model.setDependencyManagement(dependencyManagement)
    }

    if (model.getBuild() != null) {
      MigratorUtils.adjustPluginVersions(model.getBuild().getPlugins(), platformModel.getBuild().getPluginManagement().getPlugins(), crtReport)
    }
    
    val dependencies = processDependencies(model.getDependencies.toList, crtReport, bHasParent, projectGroupId=projectGroupId, projectType=model.getPackaging()) // always process since we may add dependency
    if(!dependencies.isEmpty()) {
      if(!disableArtifactsScanning) checkDependenciesAvailability(dependencies, forceLatestVersion, crtReport, projectArtifacts, false, List(MigratorUtils.extractModelProperties(model), parentPomProperties), projectGroupId, provider, platformModel.getDependencyManagement().getDependencies())
      model.setDependencies(dependencies)
    }
    
    if(model.getBuild != null && model.getBuild.getPlugins != null) {
      model.getBuild.setPlugins(processPlugins(model.getBuild.getPlugins.toList))
    }
    
    processProject(model, pomFile, parentPom, crtReport, crtUpgradePaths)
    
    return model
  }
  
  def checkPlatformAncestor(parent: Parent, adjust:Boolean = true): Boolean = false
  
  def processPlugins(plugins:List[Plugin]):java.util.List[Plugin]= {
    var out = new MutableList[Plugin]()
    out ++= plugins
    
    for(path <- upgradePath) {
      for(p <- out) {
        path.getMappedPlugins.get(new NiceDependency(p.getGroupId(), p.getArtifactId())) match {
              case Some(mappedPlugin) => {info(s"Mapping $p to $mappedPlugin"); MigratorUtils.setGAV(p, mappedPlugin.getGroupId, mappedPlugin.getArtifactId);}
              case None => {debug(s"Carrying over unmodified $p");}
        }
      }
      
      out = out.filterNot(p => {
         val removed = path.getRemovedPlugins.contains(MigratorUtils.createNiceDependency(p.getGroupId(), p.getArtifactId()))
         if(removed)info(s"Removing $p")
         removed
      })
    }
    
    return new ArrayList[Plugin](out) // Keep it a Java mutable list
  }

  def processProject(model:Model, pomFile:File, parentPom:Model, crtReport:PomReport, crtUpgradePaths:Array[UpgradeStep]) {
    if(getPlatformAppTypes.size == 1 && getPlatformAppTypes()(0) == APP_TYPE_BATCH)processBatchProject(model, pomFile, crtUpgradePaths, crtReport)
    else model.getPackaging() match {
      case "war" => processWebOrServiceProject(model, pomFile, crtUpgradePaths, crtReport)
      case "bundle" => processLibraryProject(model)
      case _ => processRegularProject(model)
    }
    
    removeDistributionManagementAndRepositories(model)
  }
  
  def processWebOrServiceProject(model:Model, pomFile:File, crtUpgradePaths:Array[UpgradeStep] = upgradePath, crtReport:PomReport) {
    info("Procesing web or service project: " + model.getName)

    val introducedDependencies:java.util.List[Dependency] = new java.util.ArrayList[Dependency]()

    for(path <- crtUpgradePaths) {
      introducedDependencies.addAll(path.processWebOrServiceProject(model, pomFile, crtReport, urb))
    }
    
    // Ensure artifact uniqueness TODO There might be a nicer way
    for(dep <- introducedDependencies) {
      if(!MigratorUtils.findDependency(model.getDependencies(), dep)) {
        model.setDependencies(model.getDependencies() ++ List(dep))
      }
    }
  }
  
  /**
   * This is a currently working mechanism to detect if a project is a SOA interface or implementation project because {@link #removeEclipseMetadata()} 
   * doesn't delete .project files for any SOA related project. Should that behavior change, this will have to be rethought.
   */
  def isSOAProducerProject(model:Model, pomFile:File):Boolean=false
  
  def createWebResources(prjDir:File, config:Xpp3Dom):Xpp3Dom = {
    val V4_CONTENT_SOURCE = "content/v4contentsource"
    val v4ContentSourceFile = new File(prjDir, s"$V4_CONTENT_SOURCE")
      
    if(v4ContentSourceFile.exists() && !v4ContentSourceFile.list().isEmpty) {
      val wres = new Xpp3Dom("webResources")
      val res2 = new Xpp3Dom("resource")
      wres.addChild(res2)
      res2.addChild(MigratorUtils.createXpp3Dom("directory", V4_CONTENT_SOURCE))
      res2.addChild(MigratorUtils.createXpp3Dom("targetPath", "WEB-INF/classes/v4contentsource"))
      
      return wres
    } else {
      return null;
    }
  }
  
  def processRegularProject(model:Model) {
    info("Processing regular project: " + model.getName())
  }
  
  def processLibraryProject(model:Model) {
    info("Procesing library project: " + model.getName())
    
    model setPackaging "jar"
  }
  
  def processBatchProject(model:Model, pomFile:File, crtUpgradePaths:Array[UpgradeStep] = upgradePath, crtReport:PomReport) {
    info("Procesing batch project: " + model.getName)
    
    for(crtPath <- crtUpgradePaths) {
      crtPath.processBatchProject(model, pomFile, parentPomFile, crtReport, urb)
    }
  }
  
  def afterProcessDependencyManagement(dependencyManagement:DependencyManagement) = {}
  
  def processDependencyManagement(model:Model, crtReport:PomReport, bHasParent:Boolean, parentProperties:java.util.Properties, projectGroupId:String, projectType:String, crtUpgradePaths:Array[UpgradeStep] = upgradePath): DependencyManagement = {
    val dependencyManagement = new DependencyManagement();
    if (model.getDependencyManagement() != null) {

      import scala.collection.JavaConversions
      
      dependencyManagement.setDependencies(processDependencies(model.getDependencyManagement.getDependencies().toList, crtReport, bHasParent, dependencyManagement=true, projectGroupId=projectGroupId, projectType=projectType, crtUpgradePaths=crtUpgradePaths));
      if(!disableArtifactsScanning) checkDependenciesAvailability(dependencyManagement.getDependencies(), forceLatestVersion, crtReport, projectArtifacts, true, List(MigratorUtils.extractModelProperties(model), parentProperties), projectGroupId, provider, platformModel.getDependencyManagement().getDependencies())
    }
    
    afterProcessDependencyManagement(dependencyManagement)
    
    return dependencyManagement
  }
  
  def afterProcessDependencies(deps:MutableList[NiceDependency]) = {}
  
  def processDependencies(deps: List[Dependency], crtReport:PomReport, bHasParent: Boolean = false, crtUpgradePaths:Array[UpgradeStep] = upgradePath, dependencyManagement:Boolean=false, projectGroupId:String=null, projectType:String=null): java.util.List[Dependency] = {
    val tmpList = new scala.collection.mutable.MutableList[NiceDependency]()
    var inputDeps = deps.map(MigratorUtils.createNiceDependency(_))
    
    if(crtUpgradePaths.isEmpty) {
      tmpList ++= inputDeps
    } else
	    for(crtPath <- crtUpgradePaths) {
	      tmpList.clear
	      
		    for (d <- inputDeps) {
		      if(MigratorUtils.findDependency(projectArtifacts.map(_._2), d.getDependency, false, projectGroupId)) {
		        debug(s"Skipping dependency as it belongs to the project: d")
		        tmpList += MigratorUtils.createNiceDependency(d.getDependency)
		      } else {
		        val transformedDependency = crtPath.transformDependency(d.getDependency, bHasParent, crtReport)
		        
		        if(transformedDependency == null) {
		        	tmpList ++= checkUpgradedDependencies(d.getDependency, crtReport, crtUpgradePaths)
		        } else {
		          tmpList ++= transformedDependency
		        }
		      }
		    }
	    
	    inputDeps = tmpList.toList
    }
    
    if(!dependencyManagement) {
      for(p <- crtUpgradePaths) {
        val addedValues = p.getAddedArtifacts.filter(_._1 == projectType).map(_._2)
        tmpList ++= addedValues
        crtReport.addedArtifacts ++= addedValues
      }
    }
    
    afterProcessDependencies(tmpList)
    
    import scala.collection.JavaConversions._
    
    return toJavaList(tmpList)
  }
    
  def toJavaList(tmpList:MutableList[NiceDependency]):java.util.ArrayList[Dependency]= {
    import scala.collection.JavaConversions._
    
    val javaList = new java.util.ArrayList[Dependency]
    javaList.addAll(new JavaUtils().filterRetList(tmpList).map(_.getDependency))
    return javaList
  }
  
  /**
   * @return one of the below:
   * null if no upgrade info was provided
   * empty list if the dependency is to be removed
   * one entry if the dependency was mapped
   * more entries if the dependency was unmerged
   */
  def checkUpgradedDependencies(d:Dependency, crtReport:PomReport, customUpgradePath:Array[UpgradeStep] = upgradePath) : List[NiceDependency] = {
    var deps = List(MigratorUtils.createNiceDependency(d))
    
    for(p <- customUpgradePath) {
      deps = upgradeDependency(deps, crtReport, p)
    }
    
    return deps
  }
  
  def upgradeDependency(deps:List[NiceDependency], crtReport:PomReport, path:UpgradeStep):List[NiceDependency] = {
    var outDeps = MutableList[NiceDependency]()

    // The Dependency class isn't collections friendly (no override on equals(), hashmap()) so we're using implicit conversions
import NiceDependency.ImplicitConversions._

    for (d <- deps) {
      if ((d.getType() == null || d.getType() == "jar") && d.getClassifier() == null) {
        if (path.getRemovedArtifacts.contains(MigratorUtils.createNiceDependency(d))) {
          info(s"Removing obsolete $d")
          crtReport.removedArtifacts ::= MigratorUtils.createNiceDependency(d)
        } else {
          path.getMappedArtifacts.get(d) match {
            case Some(mappedDep) => { outDeps += mappedDep; info(s"Mapping dependency $d to $mappedDep"); crtReport.mappedArtifacts += (d -> mappedDep) }
            case None => {
              path.getUnmergedArtifacts.get(d) match {
                case Some(unmergedDeps) => { outDeps ++= unmergedDeps; info(s"Expanding unmerged $d into $unmergedDeps"); crtReport.unmergedArtifacts += (d -> unmergedDeps) }
                case None => {
                  if(path.getMergedArtifacts.isEmpty) {
                    info(s"Carrying over untouched $d")
                    outDeps += d
                  } else 
	                  for (dl <- path.getMergedArtifacts) {
	                    dl._1.find(_ == MigratorUtils.createNiceDependency(d)) match {
	                      case Some(mergedDep) => { outDeps += dl._2; info(s"Contracting merged $d into ${dl._2}"); crtReport.mergedArtifacts += (List(d) -> dl._2) }
	                      case None => {
	                        outDeps += processBundleDependency(d)
	                      }
	                    }
	                  }
                }
              }
            }
          }
        }
      } else {
        outDeps += processBundleDependency(d)
      }
    }
    
    return outDeps.toList
  }
  
  def processBundleDependency(d:Dependency):Dependency= {
    if("bundle" == d.getType()) {
      d.setType("jar")
      info(s"Removing obsolete bundle type from artifact $d")
    } else {
    	info(s"Carrying over untouched $d")
    }
    
    return d
  }
  
  def doOptionalOperation(m:Model, f: (Model) => Unit, option:String, operationDescription:String) {
    if(line == null || line.hasOption(option)) {
      info(s"Performing: $operationDescription")
      f(m)
      info(s"Completed: $operationDescription")
    } else {
      info(s"Skipping: $operationDescription")
    }
  }
  
  def upgradeRepos(model: Model): Unit = {
    val repositoryList = model.getRepositories
    val tmpRepoList = new ArrayList[Repository]()
    if (repositoryList != null) {									/* TODO This is ugly. Rewrite it in a nicer way. */
      for (r <- repositoryList 
          if (r.getUrl != null) 
            && (r.getUrl.stripSuffix("/").endsWith("/content/repositories/releases")
            || r.getUrl.stripSuffix("/").endsWith("/content/repositories/snapshots")
            || r.getUrl.stripSuffix("/").endsWith("/content/repositories/v3artifacts")
            || r.getUrl.stripSuffix("/").endsWith("/content/repositories/thirdparty")
            || r.getUrl.stripSuffix("/").endsWith("/content/repositories/domaindata"))) {
        tmpRepoList.add(r)
      }
    }
    for (r <- tmpRepoList) {
      info(s"Removing legacy Maven repo ${r.getUrl()}")
      repositoryList.remove(r)
    }
  }
  
  def setPlatformParent(parent:Parent) {
    if(platformVersion != parent.getVersion) {
      if(parent.getVersion != null) {
	    oldPlatformVersions :+= parent.getVersion
	    oldPlatformVersions = oldPlatformVersions.distinct
      }
    }
    
    parent.setGroupId(platformGroupId)
    parent.setArtifactId(platformArtifactId)
    parent.setVersion(platformVersion)
  }

  private def removeDistributionManagementAndRepositories(model: Model): Unit = {
    info(s"Deleting the distribution management section from ${MigratorUtils.POM_XML} as it's provided by the platform")
    model.setDistributionManagement(null)
  }
    
  def checkDependenciesAvailability(deps:java.util.List[Dependency], updateToLatest:Boolean = false, report:PomReport, projectArtifacts:java.util.List[(File, Dependency)], dependencyManagement:Boolean, properties:List[java.util.Properties], projectGroupId:String=null, provider:Boolean, platformManagedDeps:java.util.List[Dependency]) {
    var depsToRemove = Set[Dependency]()
    
    import scala.collection.JavaConversions._
    import PomReport._
    
    for(dep <- asScalaBuffer(deps)) {
      debug(s"Looking for artifact $dep")

      // Check if it belongs to the project
      if(findDependency(projectArtifacts.map(_._2), dep, false, projectGroupId)) {
        debug(s"Skipping dependency as it belongs to the project: $dep")
      } else
        if(findDependency(platformManagedDeps, dep, false)) { // Check if it's managed
          if(!StringUtils.isEmpty(dep.getVersion())) {
            if(dependencyManagement) {
              if(!provider) {
              report.addMissingArtifact(dep, NOT_MISSING, s"Removed version override for artifact managed by $platformArtifactId", null)
              depsToRemove += dep
              report.addMissingArtifact(dep, NOT_MISSING, s"Removed artifact from dependency management section because its version is managed by $platformArtifactId", null)
              }
            } else {
              if(!provider) {
            	  report.addMissingArtifact(dep, NOT_MISSING, s"Removed version override for artifact managed by $platformArtifactId", null)
            	  dep.setVersion(null)
              }
            }
          }
        } else {
          checkArtifactAvailability(dep, updateToLatest, report, properties)
        }
    }
    
    if(dependencyManagement) for(dep <- depsToRemove) deps.remove(dep)
  }
  
  /**
   * @param dep The dependency to check. Its groupId might be updated in case it's a platform customized thirparty artifact.
   */
  def checkArtifactAvailability(dep:Dependency, updateToLatest:Boolean, crtReport:PomReport, properties:List[java.util.Properties]=List()):(List[String], List[String]) = {
    import PomReport._
    
    info(s"Analyzing artifact $dep")
      val oldRepos = MAVEN_REPOS_OLD.filter(repo => artifactExists(repo + buildArtifactUrl(dep, props=properties)))
      
      if(!oldRepos.isEmpty) {
        debug(s"Found artifact in old repos: $oldRepos")
        debug("Checking in new repos")

        if(oldRepos.contains(MAVEN_REPO_OLD_THIRDPARTY)) {
          debug(s"Found artifact in $MAVEN_REPO_OLD_THIRDPARTY.")
        
          if(artifactExists(MAVEN_REPO_NEW_THIRDPARTY + buildArtifactUrl(dep, props=properties))) {
            info(s"Found artifact in Maven Central")
            return (List(MAVEN_REPO_NEW_CENTRAL_PROXY), null)
          } else {
	          val origGID = dep.getGroupId() // Store this and restore if needed as opposed to cloning dep
	          dep.setGroupId(OLD_THIRDPARTY_GROUPID_PREFIX + dep.getGroupId)
	          
	          if(artifactExists(MAVEN_REPO_NEW_THIRDPARTY + buildArtifactUrl(dep, props=properties))) {
	            if(StringUtils.isEmpty(dep.getVersion())) {
	              crtReport.addMissingArtifact(dep, MISSING_THIRDPARTY ,s"Artifact containing platform customizations is replacing the one with groupId ${origGID}. Could not detect a version.", List(MAVEN_REPO_OLD_THIRDPARTY))
	              return (oldRepos, null)
	            } else {
	              if(artifactExists(MAVEN_REPO_NEW_THIRDPARTY + buildArtifactUrl(dep, true, props=properties))) {
	                info(s"Mapped third party dependency to $dep");
	                return (oldRepos, List(MAVEN_REPO_NEW_THIRDPARTY))
	              } else {
	                val latestVersion = getLatestArtifactVersion(MAVEN_REPO_NEW_THIRDPARTY, dep.getGroupId(), dep.getArtifactId())
	                
	                if(updateToLatest) {
	                  if(latestVersion != null) {
	                    dep.setVersion(latestVersion)
	                    crtReport.addMissingArtifact(cloneDependency(dep, properties), NOT_MISSING, s"No exact version match found. Newest version of artifact containing platform customizations is replacing the one with groupId $origGID", List(MAVEN_REPO_OLD_THIRDPARTY))
	                    return (oldRepos, List(MAVEN_REPO_NEW_THIRDPARTY))
	                  } else {
	                    warn(s"Could not retrieve latest version of $dep")
	                    
	                    dep.setGroupId(origGID) // Restore the original value
	                    return (oldRepos, null)
	                  }
	                } else {
	                  dep.setGroupId(origGID) // Restore the original value
	                  
	                  if(artifactExists(MAVEN_REPO_NEW_CENTRAL_PROXY + buildArtifactUrl(dep, true, props=properties))) {
	                    info(s"Found artifact in Maven Central")
	                    return (oldRepos, List(MAVEN_REPO_NEW_CENTRAL_PROXY))
	                  } else {
	                    crtReport.addMissingArtifact(cloneDependency(dep, properties), MISSING_THIRDPARTY, s"Third party dependency could not be found in Maven Central. In case you're using a custom Maven repository, the platform team does not recommend it.", List(MAVEN_REPO_OLD_THIRDPARTY))
	                    
	                    return (oldRepos, null)
	                  }
	                }
	              }
	            }
	          } else {
	            dep.setGroupId(origGID) // Restore the original value
	            
	            if(artifactExists(MAVEN_REPO_NEW_CENTRAL_PROXY + buildArtifactUrl(dep, true, props=properties))) {
	              info(s"Found artifact in Maven Central")
	              return (oldRepos, List(MAVEN_REPO_NEW_CENTRAL_PROXY))
	            } else {
	              crtReport.addMissingArtifact(cloneDependency(dep, properties), MISSING_THIRDPARTY, s"Third party dependency could not be found in Maven Central. In case you're using a custom Maven repository, the platform team does not recommend it.", null)
	              return (oldRepos, null)
	            }
	          }
          }
        } else {
          val containingNewRepos = MAVEN_REPOS_NEW.filter(repo => artifactExists(repo + buildArtifactUrl(dep, props=properties)))
              
          if(containingNewRepos.isEmpty) {
            crtReport.addMissingArtifact(cloneDependency(dep, properties), MISSING_PROVIDER, s"Could not find artifact in $MAVEN_REPOS_NEW", oldRepos)
            
            return (oldRepos, null)
          } else {
            val missingRelease = oldRepos.contains(MAVEN_REPO_OLD_RELEASES) && !containingNewRepos.contains(MAVEN_REPO_NEW_RELEASES)
            
            for(newRepo <- containingNewRepos) {
              if(artifactExists(newRepo + buildArtifactUrl(dep, includeVersion=true, props=properties))) {
                info(s"Found exact match in $newRepo")
                return (oldRepos, List(newRepo))
              } else {
                val latestVersion = getLatestArtifactVersion(newRepo, dep.getGroupId(), dep.getArtifactId())
                
                if(updateToLatest) {
                  if(latestVersion != null) {
                    if(missingRelease) {
                      crtReport.addMissingArtifact(cloneDependency(dep, properties), MISSING_PROVIDER, s"No release version of provider found in platform repository.", oldRepos)
                    } else {
                      crtReport.addMissingArtifact(dep, NOT_MISSING, s"No exact version match found. Picking newest version of artifact per user's request.", null)
                    }
                    
                    dep.setVersion(latestVersion)
                    
                    return (oldRepos, List(newRepo))
                  } else {
                    warn(s"Could not retrieve latest version of $dep")
                    
                    return (oldRepos, null)
                  }
                } else {
                  if(missingRelease) {
                      crtReport.addMissingArtifact(cloneDependency(dep, properties), MISSING_PROVIDER, s"No release version of provider found in platform repository. Temporarily you may use the latest snapshot version ($latestVersion) but ask its owner to release to platform repository.", oldRepos)
                    } else {
                    	crtReport.addMissingArtifact(dep, NOT_MISSING, s"No exact version match found in platform repository." + (if(latestVersion!=null)s" Consider using the latest available version $latestVersion." else ""), null)
                    }
                  return (oldRepos, null)
                }
              }
            }
            
            crtReport.addMissingArtifact(dep, MISSING_PROVIDER, s"Could not locate the dependency in the new Maven repositories. Please ask its owner to release a version to platform repository.", oldRepos)
            return (oldRepos, null)
          }
        }
      } else 
      if(artifactExists(MAVEN_REPO_NEW_CENTRAL_PROXY + buildArtifactUrl(dep, true, props=properties))) {
        info(s"Found artifact in Maven Central")
        return (List(MAVEN_REPO_NEW_CENTRAL_PROXY), null)
      } else {
          crtReport.addMissingArtifact(dep, MISSING_THIRDPARTY, s"Couldn't find artifact in the old Maven repositories.", null)
          // TODO then what?
        return (null, null)
      }
  }
}
