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

package com.paypal.butterfly.core.upgrade

import java.io.File
import java.util.ArrayList

import com.paypal.butterfly.core.upgrade.paths.{UpgradeAddonRegistry, UpgradeStep}

import scala.collection.JavaConversions._
import scala.collection.mutable.MutableList
import org.apache.commons.cli.BasicParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.log4j.PropertyConfigurator
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.Plugin
import grizzled.slf4j.Logging
import com.paypal.butterfly.core.upgrade.paths.PlatformVersionManager
import com.paypal.butterfly.core.upgrade.paths.DummyPlatformVersionManager

object UpgradeMain extends Logging {


  def defaultPlatformVersion:String  = "1.0.0"

  var platformVersion:String = defaultPlatformVersion
  private var _platformAppTypeOverride:String = _

  def DEFAULT_LATEST_PLATFORM_VERSION = ""
  
  var taskid:String = _
  var disableBackup:Boolean = _
  var disableArtifactsScanning:Boolean = _
  
  var provider:Boolean = false

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

  def platformModel: Model = null
  
  var projectArtifacts = List[(File, Dependency)]()
  
  var line:CommandLine = null
  val options = new Options()
  
  var upgradePath:Array[UpgradeStep] = Array()
  
  def urb:UpgradeReportBuilder = new UpgradeReportBuilder()

  def main(arg: Array[String]) {
    try {
      // And then we just zap the configuration and overwrite it with our own. Ours is better, always.
      val log4jConfig = IOUtils.toString(getClass().getResourceAsStream("/config/log4jconfig.properties"))
      val modifiedLog4jConfig = org.apache.commons.lang.text.StrSubstitutor.replace(log4jConfig, Map("logSuffix" -> buildUniqueFileSuffix()))

      PropertyConfigurator.configure(new java.io.ByteArrayInputStream(modifiedLog4jConfig.getBytes()))

      line = new BasicParser parse (buildCmdOptions, arg)

      if(line.hasOption(HELP_OPTION)) {
        new HelpFormatter().printHelp(CMDLINE, options)
      } else {
        if (!line.hasOption(INPUT_OPTION)) {
          info(s"Option -i is required. Please specify what project you want to upgrade. To see full option info use -h")
        } else {
          if (line.hasOption(TASKID_OPTION)) {
            taskid = line.getOptionValue(TASKID_OPTION)
          }


          val inputOptionValue = if (line.getOptionValue(INPUT_OPTION) == null) new File(".").getCanonicalPath() else line.getOptionValue(INPUT_OPTION)

          debug(s"Starting r2u version $R2U_VERSION...")

          val disabledAddonsCmdValue = line.getOptionValue(DISABLE_ADDONS_OPTION)
          if (disabledAddonsCmdValue != null) {
            UpgradeAddonRegistry.disable(disabledAddonsCmdValue.split(','))
          }

          if (line.hasOption(DISABLE_BACKUP_OPTION)) {
            disableBackup = true
          }

          forceLatestVersion = line.hasOption(FORCE_LATEST_VERSION_OPTION)

          var inputLocation = new File(inputOptionValue)

          // Make input location an absolute path
          if (!inputLocation.isAbsolute()) inputLocation = new File(".", inputOptionValue).getCanonicalFile()

          if (migrateAll(inputLocation)) {
            info("DONE")

            val upgradeReportFile = new File("platform-upgrade-report" + buildUniqueFileSuffix() + ".md")
            FileUtils.writeStringToFile(upgradeReportFile, urb.buildGitHubMarkdownReport(platformGroupEmail(), if (taskid != null) taskid else "N/A", inputOptionValue))
            logger.info(s"Wrote upgrade report file to " + upgradeReportFile.getName())

            logger.info("""NOTE: You might need to re-import the projects into your IDE.""")
          } else {
            logger.error("FAILED")
            System.exit(3)
          }
        }
      }
    } catch {
      case ex:ParseException => {error(s"Invalid command line sytax: \n ${ex.getMessage}\n"); new HelpFormatter().printHelp(CMDLINE, options); System.exit(2);}
    }
  }

  def platformGroupEmail():String = ""
  
  def buildUniqueFileSuffix():String = (if(taskid != null) ("-" + taskid) else "")

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


    /*
     *This is where the upgrades will start
     *Loop through each UpgradeStep
     */
    if (upgradePath.size == 0){
      info("No upgrades specified")
    }
    for(crtPathSegment <- upgradePath) {
      crtPathSegment.postProcessAll(urb)
    }

    true
  }


  def getPlatformVersionManager():PlatformVersionManager = new DummyPlatformVersionManager()
  

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

  def toJavaList(tmpList:MutableList[NiceDependency]):java.util.ArrayList[Dependency]= {
    import scala.collection.JavaConversions._
    
    val javaList = new java.util.ArrayList[Dependency]
    javaList.addAll(new JavaUtils().filterRetList(tmpList).map(_.getDependency))
    return javaList
  }

}
