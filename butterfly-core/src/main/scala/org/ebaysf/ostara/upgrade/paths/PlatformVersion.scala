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

import org.ebaysf.ostara.upgrade.PomReport
import org.apache.maven.model.Model
import org.apache.maven.model.Parent

case class PlatformVersion(version:String)

trait PlatformVersionManager {
  var versions:Seq[PlatformVersion] = Seq()
  
  def parseVersion(ver:String):PlatformVersion
  def processPlatformVersion(raptorPlatformVersion: String, parentVersion:PlatformVersion): String
  def upgradePaths(raptorSourceVersion:PlatformVersion, raptorTargetVersion:PlatformVersion, parent:Parent, crtReport:PomReport):Array[UpgradeStep]
}

class DummyPlatformVersionManager extends PlatformVersionManager {
  val dummyPlatformVersion = PlatformVersion("0.1")

  def parseVersion(ver:String):PlatformVersion = dummyPlatformVersion
  def processPlatformVersion(raptorPlatformVersion: String, parentVersion:PlatformVersion): String = "0.1"
  def upgradePaths(raptorSourceVersion:PlatformVersion, raptorTargetVersion:PlatformVersion, parent:Parent, crtReport:PomReport):Array[UpgradeStep] = Array()

}