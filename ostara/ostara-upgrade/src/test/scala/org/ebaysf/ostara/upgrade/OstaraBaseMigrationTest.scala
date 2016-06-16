package org.ebaysf.ostara.upgrade

import org.junit.Assert._
import org.junit.Before
import org.apache.log4j.PropertyConfigurator
import scala.collection.JavaConverters._
import org.ebaysf.ostara.upgrade.NiceDependency
import org.ebaysf.ostara.upgrade.NiceDependency._
import org.apache.maven.model.Plugin
import org.codehaus.plexus.util.xml.Xpp3Dom
import org.apache.commons.io.FileUtils
import java.io.File
import org.apache.commons.lang.SystemUtils
import grizzled.slf4j.Logging
import org.ebaysf.ostara.upgrade.PomReport
import org.ebaysf.ostara.upgrade.paths.UpgradeAddonRegistry
import org.ebaysf.ostara.upgrade.UpgradeReportBuilder
import org.ebaysf.ostara.upgrade.UpgradeMain

trait OstaraBaseMigrationTest extends Logging {
  val dummyReport = new PomReport()
  val urb = new UpgradeReportBuilder()
  val upgradeMain = new UpgradeMain()
  
  @Before def initialize() {
    PropertyConfigurator.configure(getClass().getResource("/config/log4jconfig.properties"))
    
    upgradeMain.disableArtifactsScanning = true // Disable by default because it takes a lot a time
  }
  
  /** Helper method to find a particular entry in a plugin configuration */
  def findPluginConfiguration(mwp:Plugin, configName:String): Xpp3Dom = {
    if(mwp!=null) {
      val conf = mwp.getConfiguration()
      
      if(conf != null) {
        for(c <- conf.asInstanceOf[Xpp3Dom].getChildren();
            if configName == c.getName()) 
        return c
      }
    }
    
    null
  } 
  
  def prepareProject(prjDir:String): java.io.File = {
    val prjSrc = new File(getClass().getResource(s"/$prjDir").toURI())
    val tmpDest = new File(SystemUtils.JAVA_IO_TMPDIR, s"r2u${System.currentTimeMillis()}.out")
    tmpDest.mkdirs()
    
    info(s"Copy project $prjSrc to temp location $tmpDest")
    FileUtils.copyDirectory(prjSrc, tmpDest)
    tmpDest
  }
  
  def getPlugins(wm: org.apache.maven.model.Model): List[org.apache.maven.model.Plugin] = {
    if(wm.getBuild() != null && wm.getBuild.getPlugins() != null) {
      wm.getBuild().getPlugins().asScala.toList
    } else {
      List();
    }
  }
  
  def getNiceDeps(wm: org.apache.maven.model.Model): List[NiceDependency] = {
    wm.getDependencies().asScala.toList.map(new NiceDependency(_))
  }
}
