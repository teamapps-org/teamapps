package org.teamapps.ux.component.template.htmltemplate;

import org.teamapps.dto.UiHtmlTemplate;
import org.teamapps.dto.UiMustacheTemplate;
import org.teamapps.dto.UiTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MustacheTemplate implements Template {

	private static final Pattern PLACE_HOLDER_REGEX = Pattern.compile("\\{\\{#?(\\w+)\\}\\}");

	private final String templateString;
	private final List<String> dataKeys;

	public MustacheTemplate(String templateString) {
		this.templateString = templateString;
		this.dataKeys = PLACE_HOLDER_REGEX.matcher(this.templateString).results()
				.map(matchResult -> matchResult.group(1))
				.collect(Collectors.toList());
	}

	@Override
	public UiTemplate createUiTemplate() {
		return new UiMustacheTemplate(templateString);
	}

	@Override
	public List<String> getDataKeys() {
		return dataKeys;
	}
	
}
