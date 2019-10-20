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
