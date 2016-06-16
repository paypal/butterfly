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

import java.io.File
import grizzled.slf4j.Logging
import org.ebaysf.ostara.upgrade.UpgradeReportBuilder

/**
 * @author renyedi@ebay.com
 */
trait UpgradeAddon {
  val id:String;
  val description:String;
  
  val sourceVersion: PlatformVersion
  val targetVersion: PlatformVersion
  
  def run(projectRootDir:File, projectEncoding:String, urb:UpgradeReportBuilder);
}

object UpgradeAddonRegistry extends Logging {
  var addons:Map[String/*id*/,UpgradeAddon] = Map()
  var disabledAddons:Set[String] = Set()
  
  def init(addons:UpgradeAddon*) {
    for(addon <- addons) registerAddon(addon)
  }
  
  def registerAddon(addon:UpgradeAddon) {
    addons += addon.id -> addon
  }
  
  def disable(disabled:Array[String]) {
    disabledAddons ++= disabled
    
    if(disabled.isEmpty) {
      info("No disabled addons")
    } else {
    	info(s"Disabled addons: ${disabled.mkString}")
    }
  }
    
  def runAddon(id:String, projectRootDir:File, projectEncoding:String, urb:UpgradeReportBuilder) {
    if(disabledAddons.contains(id)) {
      warn(s"Skipping addon $id as it was disabled")
    } else 
    if(!addons.contains(id)) {
      warn(s"Unregistered addon $id")
    } else {
    	val addon = addons(id)
    	
  	  info(s"Running addon $id")
  	  addon.run(projectRootDir, projectEncoding, urb)
    }
  }
}