/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.agent;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.teamapps.ux.session.ClientUserAgent;

public class UserAgentParser {

    private UserAgentAnalyzer agentAnalyzer;

    public UserAgentParser() {
        new Thread(() -> {
            agentAnalyzer = UserAgentAnalyzer
                    .newBuilder()
                    .hideMatcherLoadStats()
                    .withCache(5_000)
                    .build();
        }).start();
    }

    public ClientUserAgent parseUserAgent(String userAgent) {
        try {
            if (agentAnalyzer == null) {
                return null;
            }
            UserAgent agent = agentAnalyzer.parse(userAgent);
            ClientUserAgent agentInfo = new ClientUserAgent();
            agentInfo.setDeviceClass(agent.getValue("DeviceClass"));
            agentInfo.setDeviceName(agent.getValue("DeviceName"));
            agentInfo.setDeviceBrand(agent.getValue("DeviceBrand"));
            agentInfo.setDeviceCpu(agent.getValue("DeviceCpu"));
            agentInfo.setOperatingSystemClass(agent.getValue("OperatingSystemClass"));
            agentInfo.setOperatingSystemName(agent.getValue("OperatingSystemName"));
            agentInfo.setOperatingSystemNameVersion(agent.getValue("OperatingSystemNameVersion"));
            agentInfo.setLayoutEngineNameVersion(agent.getValue("LayoutEngineNameVersion"));
            agentInfo.setAgentClass(agent.getValue("AgentClass"));
            agentInfo.setAgentName(agent.getValue("AgentName"));
            agentInfo.setAgentVersion(agent.getValue("AgentVersion"));
            agentInfo.setAgentNameVersion(agent.getValue("AgentNameVersion"));
            return agentInfo;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


}
