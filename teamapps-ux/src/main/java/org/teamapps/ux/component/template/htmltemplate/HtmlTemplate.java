package org.teamapps.ux.component.template.htmltemplate;

import org.teamapps.dto.UiHtmlTemplate;
import org.teamapps.dto.UiTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HtmlTemplate implements Template {

	private static final Pattern PLACE_HOLDER_REGEX = Pattern.compile("\\{\\{(\\w+)\\}\\}");

	private final String html;
	private final List<String> dataKey;

	public HtmlTemplate(String htmlTemplateString) {
		this.html = htmlTemplateString;
		this.dataKey = PLACE_HOLDER_REGEX.matcher(html).results()
				.map(matchResult -> matchResult.group(1))
				.collect(Collectors.toList());
	}

	@Override
	public UiTemplate createUiTemplate() {
		return new UiHtmlTemplate(html);
	}

	@Override
	public List<String> getDataKeys() {
		return dataKey;
	}
	
}
