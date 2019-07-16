package org.teamapps.ux.component.template.htmltemplate;

import org.teamapps.dto.UiHtmlTemplate;
import org.teamapps.dto.UiTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HtmlTemplate implements Template {

	private static Pattern placeHolderRegex = Pattern.compile("\\{\\{(\\w+)\\}\\}");

	private String html;

	public HtmlTemplate(String htmlTemplateString) {
		this.html = htmlTemplateString;
	}

	@Override
	public UiTemplate createUiTemplate() {
		return new UiHtmlTemplate(html);
	}

	@Override
	public List<String> getDataKeys() {
		return placeHolderRegex.matcher(html).results()
				.map(matchResult -> matchResult.group(1))
				.collect(Collectors.toList());
	}
	
}
