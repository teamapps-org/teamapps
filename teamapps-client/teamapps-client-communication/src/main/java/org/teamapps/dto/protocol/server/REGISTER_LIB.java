package org.teamapps.dto.protocol.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("REGISTER_LIB")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class REGISTER_LIB extends AbstractReliableServerMessage {

	@JsonProperty("lid")
	protected final String lid;
	@JsonProperty("jsUrl")
	protected final String jsUrl;
	@JsonProperty("cssUrl")
	protected final String cssUrl;

	@JsonCreator
	public REGISTER_LIB(
			@JsonProperty("lid") String lid,
			@JsonProperty("jsUrl") String jsUrl,
			@JsonProperty("cssUrl") String cssUrl) {
		this.lid = lid;
		this.jsUrl = jsUrl;
		this.cssUrl = cssUrl;
	}

	public String getLid() {
		return lid;
	}

	public String getJsUrl() {
		return jsUrl;
	}

	public String getCssUrl() {
		return cssUrl;
	}
}