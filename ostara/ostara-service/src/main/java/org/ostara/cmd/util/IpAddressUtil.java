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
package org.ostara.cmd.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IpAddressUtil {
   public static String getCurrentHostIpAddress() {
      Enumeration<NetworkInterface> n;
      try {
         n = NetworkInterface.getNetworkInterfaces();
         for (; n.hasMoreElements();) {
            NetworkInterface e = n.nextElement();
            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();) {
               InetAddress addr = a.nextElement();

               String ipAddress = addr.getHostAddress();
               if (!ipAddress.equals("127.0.0.1") && ipAddress.indexOf(":") == -1) {
                  return ipAddress;
               }
            }
         }
         throw new RuntimeException("Can't get the current host ip address.");
      } catch (SocketException e1) {
         throw new RuntimeException("Can't get the current host ip address:" + e1);
      }
   }
}
