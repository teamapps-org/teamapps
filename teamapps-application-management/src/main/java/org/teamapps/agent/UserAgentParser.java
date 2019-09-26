package org.teamapps.agent;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.teamapps.ux.session.ClientUserAgent;

public class UserAgentParser {

    private UserAgentAnalyzer agentAnalyzer;

    public UserAgentParser() {
        agentAnalyzer = UserAgentAnalyzer
                .newBuilder()
                .hideMatcherLoadStats()
                .withCache(5_000)
                .build();
    }

    public ClientUserAgent parseUserAgent(String userAgent) {
        try {
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


    public static void main(String[] args) {
        UserAgentAnalyzer uaa = UserAgentAnalyzer
                .newBuilder()
                .hideMatcherLoadStats()
                .withCache(25_000)
                .build();

        String userAgentString = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36";
        UserAgent agent = uaa.parse(userAgentString);
        for (String fieldName: agent.getAvailableFieldNamesSorted()) {
            System.out.println(fieldName + " = " + agent.getValue(fieldName));
        }
    }
}
