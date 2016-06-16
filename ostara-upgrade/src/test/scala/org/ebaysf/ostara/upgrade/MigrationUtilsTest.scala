package org.ebaysf.ostara.upgrade

import java.io.File
import java.util.ArrayList
import java.util.Properties
import org.apache.maven.model.Dependency
import org.apache.maven.model.Exclusion
import org.apache.maven.model.Model
import org.ebaysf.ostara.upgrade.MigratorUtils.{createDependency => d}
import org.junit.Assert._
import org.junit.Test
import org.codehaus.plexus.util.xml.Xpp3Dom
import org.apache.maven.model.Plugin
import org.ebaysf.ostara.upgrade.MigratorUtils.{createDependency => d}
import scala.collection.JavaConversions.seqAsJavaList

class MigrationUtilsTest extends OstaraBaseMigrationTest {
  @Test def verifyGetRelativePath() {
    var pomFile=new File("c:/temp/basedir/dir1/pom.xml");
    var parentPomFile = new File("c:/temp/basedir/pom.xml");

    assertEquals("dir1/pom.xml", MigratorUtils.getRelativePathToParent(pomFile, parentPomFile));
    
    pomFile=new File("c:/temp/basedir/pom.xml");
    parentPomFile = new File("c:/temp/basedir/pom.xml");
    
    assertEquals("pom.xml", MigratorUtils.getRelativePathToParent(pomFile, parentPomFile));
  }

  @Test def verifyArtifactUnavailabilityOverall() {
    val dep = MigratorUtils.createDependency("a", "b", null)
    val result = upgradeMain.checkArtifactAvailability(dep, false, new PomReport())
    
    assertNull(result._1)
    assertNull(result._2)
  }
  
  @Test def verifyArtifactUnavailabilityInNew() {
    val dep = MigratorUtils.createDependency("springframework", "spring", null)
    val result = upgradeMain.checkArtifactAvailability(dep, false, new PomReport())
    
    assertNotNull(result._1)
    assertNull(result._2)
  }

  @Test def verifyBuildArtifactUrlWithPropertyVersion() {
    val props = new Properties()
    val ver = "3.1.4-RELEASE"
    val verP = "versionProperty"
    props.put(verP, ver)
    val dep = MigratorUtils.createDependency("org.springframework", "spring-web", "${" + verP + "}")
    val result = MigratorUtils.buildArtifactUrl(dep, true, List(props))
    
    assertTrue(result.contains(ver))
  }
  
  @Test def verifyBuildArtifactUrlWithPropertiesInVersion() {
    val props = new Properties()
    val ver = "3.1.4-RELEASE"
    val verPrefix = "vPrefix"
    props.put(verPrefix, "3.1")
    val verMinor = "vMinor"
    props.put(verMinor, "4")
    val verSuffix = "vSuffix"
    props.put(verSuffix, "RELEASE")
    val dep = MigratorUtils.createDependency("org.springframework", "spring-web", "${" + verPrefix + "}.${" + verMinor + "}-${" + verSuffix + "}")
    val result = MigratorUtils.buildArtifactUrl(dep, true, List(props))
    
    assertTrue(result, result.contains(ver))
  }
  
  @Test def verifyArtifactExists() {
    val dep = MigratorUtils.createDependency("springframework", "spring", null)
    assertTrue(MigratorUtils.artifactExists(upgradeMain.MAVEN_REPO_NEW_CENTRAL_PROXY + MigratorUtils.buildArtifactUrl(dep)))
  }
  
   @Test def verifyArtifactExistsMavenCentral() {
    val dep = MigratorUtils.createDependency("com.google.code.gson", "gson", "2.2.2")
    val url = upgradeMain.MAVEN_REPO_NEW_CENTRAL_PROXY + MigratorUtils.buildArtifactUrl(dep, true)
     
    println(url)
    assertTrue(MigratorUtils.artifactExists(url))
  }

   @Test def verifyArtifactNoVersionExistsMavenCentral() {
    val dep = MigratorUtils.createDependency("com.google.code.gson", "gson")
    val url = upgradeMain.MAVEN_REPO_NEW_CENTRAL_PROXY + MigratorUtils.buildArtifactUrl(dep, true)
     
    println(url)
    assertTrue(MigratorUtils.artifactExists(url))
  }

  @Test def verifyFindProjectDependency() {
    val lst = new java.util.ArrayList[Dependency]()
    lst.add(d(null, "ViewItemSvcErrorContent"))
    lst.add(d(null, "ViewItemServiceTest"))
    lst.add(d(null, "viewitemserviceapp.eba"))
    lst.add(d(null, "viewitemserviceapp"))
    lst.add(d(null, "ViewItemSvcClient"))
    lst.add(d(null, "ViewItemService"))
    lst.add(d(null, "ViewItemApplication"))
    lst.add(d(null, "ViewItemDomain"))
    
    val dep = d("dummy", "ViewItemDomain")
    assertTrue(MigratorUtils.findDependency(lst, dep, false, "dummy"))
  }
  
  @Test def verityEvaluateVersion() {
    val m = new Model()
    val theVersion = "1.2.3"
    m.setVersion(theVersion)
    m.getProperties().put("myversion", "${project.version}")
    val d = MigratorUtils.createDependency("a", "b", "${myversion}")
    
    MigratorUtils.evaluateVersion(d, List(MigratorUtils.extractModelProperties(m)))
    
    assertEquals(theVersion, d.getVersion)
  }
  
  @Test def verifyCloneDependency() {
    val gid = "g1"
    val aid = "a1"
    val v = "v1"
    val scope = "test"
    val depType = "war"
    val classifier = "jdk13"
    val systemPath = "mypath"
    
    val origDep = MigratorUtils.createDependency(gid, aid, v, scope, depType)
    val ex1 = new Exclusion()
    val exList = new ArrayList[Exclusion]()
    exList.add(ex1)
    origDep.setExclusions(exList)
    
    origDep.setClassifier(classifier)
    origDep.setOptional(true)
    origDep.setSystemPath(systemPath)
    
    val clonedDep = MigratorUtils.cloneDependency(origDep)
    
    assertEquals(gid, clonedDep.getGroupId())
    assertEquals(aid, clonedDep.getArtifactId())
    assertEquals(v, clonedDep.getVersion())
    assertEquals(scope, clonedDep.getScope())
    assertEquals(depType, clonedDep.getType())
    
    assertNotNull(clonedDep.getExclusions())
    assertEquals(1, clonedDep.getExclusions().size())
    assertEquals(ex1, clonedDep.getExclusions().get(0))
    
    assertEquals(classifier, clonedDep.getClassifier)
    assertTrue(clonedDep.isOptional)
    assertEquals(systemPath, clonedDep.getSystemPath)
  }
}