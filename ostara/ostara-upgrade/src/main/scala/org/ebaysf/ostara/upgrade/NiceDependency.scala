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

import org.apache.maven.model.Dependency

object NiceDependency {
  object ImplicitConversions {
    implicit def dependencyToNiceDependency(d:Dependency):NiceDependency = new NiceDependency(d)
    implicit def niceDependencyToDependency(d:NiceDependency):Dependency = d.getDependency
    implicit def niceDepsListToDepsList(nds:List[NiceDependency]):List[Dependency] = nds.map(_.getDependency)
  }
  
    
  def typesEquivalent(t1:String, t2:String):Boolean = {
    val actualT1 = if(t1 == null) "jar" else t1
    val actualT2 = if(t2 == null) "jar" else t2

    return actualT1 == actualT2
  }

  def scopesEquivalent(s1:String, s2:String):Boolean = {
    val actualT1 = if(s1 == null) "compile" else s1
    val actualT2 = if(s2 == null) "compile" else s2

    return actualT1 == actualT2
  }
  
   def classifiersEquivalent(s1:String, s2:String):Boolean = {
    val actualT1 = if(s1 == null) "compile" else s1
    val actualT2 = if(s2 == null) "compile" else s2

    return actualT1 == actualT2
  }

}

/**
 * Wrapper around the Maven Dependency class which make it play nicely with collections
 */
class NiceDependency(d:Dependency) {
  def getGroupId:String = d.getGroupId
  def getArtifactId:String = d.getArtifactId
  def getDependency:Dependency = d
  
  def this(gid:String, aid:String) {
    this(MigratorUtils.createDependency(gid, aid, null))
  }
  
  override def equals(obj:Any):Boolean = {
    obj match {
      case d2:NiceDependency => d.getArtifactId == d2.getArtifactId && d.getGroupId == d2.getGroupId
      case _ => false
    }
  }
  override def hashCode:Int = (if(getGroupId != null) getGroupId.hashCode() else 0)+ getArtifactId.hashCode()
  
  override def toString:String = s"${getGroupId}:${getArtifactId}${if(d.getVersion != null)":"+d.getVersion else ""}"
}