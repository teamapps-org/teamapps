

package testapp.test;

import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.blogview.*;
import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.toolbutton.ToolButton;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.util.DemoDataGenerator;
import testapp.util.Util;

import java.util.Arrays;

public class BlogViewTest extends AbstractComponentTest {
	public BlogViewTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
	}

	@Override
	protected Component createComponent() {
		BlogView blogView = new BlogView();
		CitationBlock citationBlock = new CitationBlock(DemoDataGenerator.randomUserImageUrl(), "This is a very wise thing to say.", "Recursius II");
		citationBlock.addToolButton(new ToolButton(MaterialIcon.REFRESH));
		blogView.addBlock(citationBlock);

		ComponentBlock componentBlock = new ComponentBlock(new DummyComponent(), 400);
		componentBlock.setAlignment(BlockAlignment.RIGHT);
		componentBlock.addToolButton(new ToolButton(MaterialIcon.REFRESH));
		blogView.addBlock(componentBlock);

		for (int i = 0; i < 10; i++) {
			BaseTemplateRecord<Void> topRecord = new BaseTemplateRecord<>(MaterialIcon.DO_NOT_DISTURB_ON, "Caption", "description");
			MessageBlock<BaseTemplateRecord> messageBlock = new MessageBlock<>();
			if (i % 3 != 2) {
				messageBlock.setTopTemplate(BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES);
				messageBlock.setTopRecord(topRecord);
			}
			messageBlock.setTopRecordAlignment(TopRecordElementAlignment.values()[i % TopRecordElementAlignment.values().length]);
			messageBlock.setHtml(Util.readResourceToString("org/teamapps/ux/testapp/demodata/richtexteditor-demo-text.html"));
			if (i%5 == 0) {
				messageBlock.setImageUrls(Arrays.asList("/resources/backgrounds/default-bl.jpg", "/resources/backgrounds/login3.jpg", "/resources/backgrounds/bg-gray-light-bl.jpg"));
			} else if (i%5 == 1) {
				messageBlock.setImageUrls(Arrays.asList("/resources/backgrounds/bg-gray-light-bl.jpg", "/resources/backgrounds/default-bl.jpg", "/resources/backgrounds/login3.jpg", "/resources/backgrounds/bg-gray-light-bl.jpg", "/resources/backgrounds/bg-gray-light-bl.jpg", "/resources/backgrounds/default-bl.jpg", "/resources/backgrounds/login3.jpg", "/resources/backgrounds/bg-gray-light-bl.jpg"));
			} else if (i%5 == 2) {
				messageBlock.setImageUrls(Arrays.asList("/resources/backgrounds/bg-gray-light-bl.jpg", "/resources/backgrounds/bg-gray-light-bl.jpg"));
			} else if (i%5 == 3) {
				messageBlock.setImageUrls(Arrays.asList("/resources/backgrounds/default-bl.jpg"));
			} else if (i%5 == 4) {
				messageBlock.setImageUrls(Arrays.asList("/resources/backgrounds/bg-gray-light-bl.jpg"));
			}
			messageBlock.setAlignment(i % 3 == 2 ? BlockAlignment.RIGHT : BlockAlignment.LEFT);
			messageBlock.setToolButtons(Arrays.asList(new ToolButton(MaterialIcon.HELP), new ToolButton(MaterialIcon.HELP)));
			blogView.addBlock(messageBlock);
		}

		return blogView;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return null;
	}
}
