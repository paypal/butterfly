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
package org.ebaysf.ostara.upgrade;

import java.util.ArrayList;
import java.util.List;

public class JavaUtils {
  /**
   * Filter unique artifacts following this uniqueness criteria: gid, aid and scope (this is additional to NiceDependency.equals())
   * 
   * @param tmpList
   * @return
   */
  public List<NiceDependency> filterRetList(List<NiceDependency> tmpList) {
    // TODO Implement in Scala
    ArrayList<NiceDependency> retList = new ArrayList<NiceDependency>();
    
    for(NiceDependency crtDep : tmpList) {
      int idx = retList.indexOf(crtDep);
      
      if(idx != -1) {
        NiceDependency p = retList.get(idx);
        if(!NiceDependency.scopesEquivalent(p.getDependency().getScope(), crtDep.getDependency().getScope()) 
            || !NiceDependency.typesEquivalent(p.getDependency().getType(), crtDep.getDependency().getType()) 
            || !NiceDependency.classifiersEquivalent(p.getDependency().getClassifier(), crtDep.getDependency().getClassifier())) {
          retList.add(crtDep);
        }
      } else {
        retList.add(crtDep);
      }
    }
    
    return retList;
  }
}
