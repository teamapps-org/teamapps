/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
package org.teamapps.ux.session;

public class ClientUserAgent {

	private String deviceClass;
	private String deviceName;
	private String deviceBrand;
	private String deviceCpu;
	private String operatingSystemClass;
	private String operatingSystemName;
	private String operatingSystemNameVersion;
	private String layoutEngineNameVersion;
	private String agentClass;
	private String agentName;
	private String agentVersion;
	private String agentNameVersion;

	public ClientUserAgent() {
	}

	public String getDeviceClass() {
		return deviceClass;
	}

	public void setDeviceClass(String deviceClass) {
		this.deviceClass = deviceClass;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceBrand() {
		return deviceBrand;
	}

	public void setDeviceBrand(String deviceBrand) {
		this.deviceBrand = deviceBrand;
	}

	public String getDeviceCpu() {
		return deviceCpu;
	}

	public void setDeviceCpu(String deviceCpu) {
		this.deviceCpu = deviceCpu;
	}

	public String getOperatingSystemClass() {
		return operatingSystemClass;
	}

	public void setOperatingSystemClass(String operatingSystemClass) {
		this.operatingSystemClass = operatingSystemClass;
	}

	public String getOperatingSystemName() {
		return operatingSystemName;
	}

	public void setOperatingSystemName(String operatingSystemName) {
		this.operatingSystemName = operatingSystemName;
	}

	public String getOperatingSystemNameVersion() {
		return operatingSystemNameVersion;
	}

	public void setOperatingSystemNameVersion(String operatingSystemNameVersion) {
		this.operatingSystemNameVersion = operatingSystemNameVersion;
	}

	public String getLayoutEngineNameVersion() {
		return layoutEngineNameVersion;
	}

	public void setLayoutEngineNameVersion(String layoutEngineNameVersion) {
		this.layoutEngineNameVersion = layoutEngineNameVersion;
	}

	public String getAgentClass() {
		return agentClass;
	}

	public void setAgentClass(String agentClass) {
		this.agentClass = agentClass;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentVersion() {
		return agentVersion;
	}

	public void setAgentVersion(String agentVersion) {
		this.agentVersion = agentVersion;
	}

	public String getAgentNameVersion() {
		return agentNameVersion;
	}

	public void setAgentNameVersion(String agentNameVersion) {
		this.agentNameVersion = agentNameVersion;
	}

	@Override
	public String toString() {
		return "ClientUserAgent{" +
				"deviceClass='" + deviceClass + '\'' +
				", deviceName='" + deviceName + '\'' +
				", deviceBrand='" + deviceBrand + '\'' +
				", deviceCpu='" + deviceCpu + '\'' +
				", operatingSystemClass='" + operatingSystemClass + '\'' +
				", operatingSystemName='" + operatingSystemName + '\'' +
				", operatingSystemNameVersion='" + operatingSystemNameVersion + '\'' +
				", layoutEngineNameVersion='" + layoutEngineNameVersion + '\'' +
				", agentClass='" + agentClass + '\'' +
				", agentName='" + agentName + '\'' +
				", agentVersion='" + agentVersion + '\'' +
				", agentNameVersion='" + agentNameVersion + '\'' +
				'}';
	}
}
