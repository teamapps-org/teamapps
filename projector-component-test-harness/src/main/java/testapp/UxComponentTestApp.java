package testapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.application.ResponsiveApplication;
import org.teamapps.projector.application.layout.ExtendedLayout;
import org.teamapps.projector.application.perspective.Perspective;
import org.teamapps.projector.application.view.View;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.field.DisplayField;
import org.teamapps.projector.component.core.field.MultiLineTextField;
import org.teamapps.projector.component.core.toolbutton.ToolButton;
import org.teamapps.projector.component.field.FieldEditingMode;
import org.teamapps.projector.component.progress.MultiProgressDisplay;
import org.teamapps.projector.component.treecomponents.tree.Tree;
import org.teamapps.projector.component.treecomponents.tree.model.ListTreeModel;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.composite.CompositeIcon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.test.*;
import testapp.test.charting.ForceLayoutGraphTest;
import testapp.test.charting.PieChartTest;
import testapp.test.charting.TreeGraphTest;
import testapp.test.formfield.*;
import testapp.test.formfield.combobox.ComboBoxTest;
import testapp.test.formfield.combobox.TagComboBoxTest;
import testapp.test.formfield.datetime.InstantDateTimeFieldTest;
import testapp.test.formfield.datetime.LocalDateFieldTest;
import testapp.test.formfield.datetime.LocalDateTimeFieldTest;
import testapp.test.formfield.datetime.LocalTimeFieldTest;
import testapp.test.table.TableTest;
import testapp.test.tree.SimpleTreeTest;
import testapp.test.tree.TreeTest;
import testapp.util.Util;

import java.util.ArrayList;
import java.util.List;

public class UxComponentTestApp {

	private static final Logger LOGGER = LoggerFactory.getLogger(UxComponentTestApp.class);

	private final SessionContext sessionContext;

	private final Component rootComponent;

	private View parametersView;
	private View componentView;
	private MultiLineTextField consoleField;
	private DisplayField docsField;

	private final ComponentTestContext testContext = new ComponentTestContext() {
		@Override
		public SessionContext getSessionContext() {
			return sessionContext;
		}

		@Override
		public void printLineToConsole(String s) {
			consoleField.append(s + "\n", true);
		}

	};
	private MultiProgressDisplay multiProgressDisplay;
	private Tree<ComponentTestTreeNode> tree;

	public UxComponentTestApp(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
		this.rootComponent = createRootComponent();
	}

	private Component createRootComponent() {
		ResponsiveApplication responsiveApplication = ResponsiveApplication.createApplication();
		multiProgressDisplay = responsiveApplication.getMultiProgressDisplay();
		Perspective perspective = Perspective.createPerspective();
		responsiveApplication.addPerspective(perspective);

		parametersView = View.createView(ExtendedLayout.CENTER, MaterialIcon.HELP, "Parameters", null);
		perspective.addView(parametersView);

		componentView = View.createView(ExtendedLayout.RIGHT, MaterialIcon.HELP, "Component", null);
		perspective.addView(componentView);

		docsField = new DisplayField(false, true);
		docsField.setEditingMode(FieldEditingMode.READONLY);

		View documentationView = View.createView(ExtendedLayout.CENTER, MaterialIcon.HELP, "Dokumentation", docsField);
		perspective.addView(documentationView);
		documentationView.getPanel().setPadding(5);

		consoleField = new MultiLineTextField();
		consoleField.setEditingMode(FieldEditingMode.READONLY);
		View consoleView = View.createView(ExtendedLayout.RIGHT_BOTTOM, MaterialIcon.HELP, "Log", consoleField);
		perspective.addView(consoleView);
		ToolButton clearConsoleToolButton = new ToolButton(MaterialIcon.HELP);
		clearConsoleToolButton.onClick.addListener(() -> this.consoleField.setValue(""));
		consoleView.getPanel().addToolButton(clearConsoleToolButton);

		ListTreeModel<ComponentTestTreeNode> treeModel = new ListTreeModel<>();
		Tree<?> tree = new Tree<>(treeModel);
		tree.setEntryTemplate(BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES);
		tree.setToggleExpansionOnClick(true);
		tree.setEnforceSingleExpandedPath(true);
		tree.setExpandersVisible(true);
		tree.setIndentation(40);
		tree.onNodeSelected.addListener(node -> {
			if (node instanceof ComponentTestTreeNode testTreeNode) {
				setActiveComponentTest(testTreeNode.getComponentTest());
			}
		});
		fillTree(treeModel);
		View treeView = View.createView(ExtendedLayout.LEFT, CompositeIcon.of(MaterialIcon.COMPUTER, MaterialIcon.ALARM_ON), "Components", this.tree);
//		ToolButton lightThemeButton = new ToolButton(MaterialIcon.WB_SUNNY, "Light theme");
//		lightThemeButton.onClick.addListener(() -> {
//			sessionContext.setTheme(StylingTheme.DEFAULT);
//			sessionContext.setBackgroundImage("default", 500);
//			sessionContext.setConfiguration(configuration);
//		});
//		treeView.getPanel().addToolButton(lightThemeButton);
//		ToolButton darkThemeButton = new ToolButton(MaterialIcon.BRIGHTNESS_3, "Dark theme");
//		darkThemeButton.onClick.addListener(() -> {
//			SessionConfiguration configuration = sessionContext.getConfiguration();
//			configuration.setTheme(StylingTheme.DARK);
//			sessionContext.setBackgroundImage("dark", 500);
//			sessionContext.setConfiguration(configuration);
//		});
//		treeView.getPanel().addToolButton(darkThemeButton);
		perspective.addView(treeView);

		responsiveApplication.showPerspective(perspective);

		return responsiveApplication.getUi();
	}

	private void setActiveComponentTest(ComponentTest componentTest) {
		if (componentView.getComponent() != componentTest.getWrappedComponent()) {
			this.parametersView.setComponent(componentTest.getParametersComponent());
			this.componentView.setComponent(componentTest.getWrappedComponent());
			String docsHtmlResourceName = componentTest.getDocsHtmlResourceName();
			this.docsField.setValue(docsHtmlResourceName != null ? Util.readResourceToString(docsHtmlResourceName) : null);
			this.consoleField.setValue("");
			tree.getModel().getRecords().stream()
					.filter(node -> node.getComponentTest() == componentTest)
					.findFirst().ifPresent(node -> tree.setSelectedNode(node));
			componentView.focus();
		}
	}

	private void fillTree(ListTreeModel<ComponentTestTreeNode> treeModel) {
		List<ComponentTestTreeNode> testNodes = new ArrayList<>();

		testNodes.add(createTestTreeNode("Panel", new PanelTest(testContext), MaterialIcon.HELP, "Panel", "Common Component container"));
		testNodes.add(createTestTreeNode("Window", new WindowTest(testContext), MaterialIcon.HELP, "Window", "Like Panel, but floating"));
		testNodes.add(createTestTreeNode("Toolbar", new ToolbarTest(testContext), MaterialIcon.HELP, "Toolbar", "Providing a list of buttons"));
		testNodes.add(createTestTreeNode("SplitPane", new SplitPaneTest(testContext), MaterialIcon.HELP, "SplitPane", "Splits content into two resizable sections"));
		testNodes.add(createTestTreeNode("TabPanel", new TabPanelTest(testContext), MaterialIcon.HELP, "TabPanel", "Manages multiple components using tabs"));
		testNodes.add(createTestTreeNode("Tree", new TreeTest(testContext), MaterialIcon.HELP, "Tree", "Baum-Komponente"));
		testNodes.add(createTestTreeNode("Tree", new SimpleTreeTest(testContext), MaterialIcon.HELP, "Tree", "Baum-Komponente"));
		testNodes.add(createTestTreeNode("Table", new TableTest(testContext), MaterialIcon.HELP, "Table", "Grid- or list-like component"));
		testNodes.add(createTestTreeNode("Calendar", new CalendarTest(testContext), MaterialIcon.HELP, "Calendar", "Outlook Calendar-like component"));
		testNodes.add(createTestTreeNode("ItemView", new ItemViewTest(testContext), MaterialIcon.HELP, "ItemView", "Grouped button grid component"));
		testNodes.add(createTestTreeNode("InfiniteItemView2", new InfiniteItemView2Test(testContext), MaterialIcon.HELP, "InfiniteItemView2", "Infinite scrolling button grid component"));
		testNodes.add(createTestTreeNode("MapView2", new MapViewTest(testContext), MaterialIcon.HELP, "MapView2", "Interactive map component"));
		testNodes.add(createTestTreeNode("IFrame", new IFrameTest(testContext), MaterialIcon.HELP, "IFrame", "Displays a website"));
		testNodes.add(createTestTreeNode("PageView", new BlogViewTest(testContext), MaterialIcon.HELP, "PageView", "Blog-like article display"));
		testNodes.add(createTestTreeNode("ImageCropper", new ImageCropperTest(testContext), CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "ImageCropper", "Select an area of an image"));
		testNodes.add(createTestTreeNode("MaterialIcons", new MaterialIconTest(testContext), MaterialIcon.IMAGE, "Material Icons", "All material icons with available default styles"));
		testNodes.add(createTestTreeNode("ProgressDisplay", new ProgressDisplayTest(testContext), MaterialIcon.IMAGE, "Progress Display", "Displays the progress of a task"));
		testNodes.add(createTestTreeNode("NotificationBar", new NotificationBarTest(testContext), MaterialIcon.NOTIFICATIONS, "Notification Bar", "Displays notifications"));
		testNodes.add(createTestTreeNode("ToolButton", new ToolButtonTest(testContext), MaterialIcon.NOTIFICATIONS, "Tool Button", "Tool Button"));
		testNodes.add(createTestTreeNode("AbsoluteLayout", new AbsoluteLayoutTest(testContext), CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "AbsoluteLayout", "Position components using absolute positions"));

		testNodes.add(createTestTreeNode("TextField", new TextFieldTest(testContext), MaterialIcon.HELP, "TextField", "Text input field"));
		testNodes.add(createTestTreeNode("NumberField", new NumberFieldTest(testContext), MaterialIcon.HELP, "NumberField", "Input field for numbers"));
		testNodes.add(createTestTreeNode("MultiLineTextField", new MultiLineTextFieldTest(testContext), MaterialIcon.HELP, "MultiLineTextField", "Multi-line text input field"));
		testNodes.add(createTestTreeNode("PasswordField", new PasswordFieldTest(testContext), MaterialIcon.HELP, "PasswordField", "Password input field"));
		testNodes.add(createTestTreeNode("DisplayField", new DisplayFieldTest(testContext), MaterialIcon.HELP, "DisplayField", "Read-only component for displaying formatted text."));
		testNodes.add(createTestTreeNode("CurrencyField", new CurrencyFieldTest(testContext), MaterialIcon.HELP, "CurrencyField", "Input field for money values (currency and amount)"));
		testNodes.add(createTestTreeNode("FileField", new FileFieldTest(testContext), MaterialIcon.HELP, "FileField", "Field for uploading one or multiple files."));
		testNodes.add(createTestTreeNode("SimpleFileField", new SimpleFileFieldTest(testContext), MaterialIcon.HELP, "SimpleFileField", "Field for uploading one or multiple files."));
		testNodes.add(createTestTreeNode("PictureChooser", new PictureChooserTest(testContext), MaterialIcon.HELP, "PictureChooser", "Field for uploading and cropping images"));
		testNodes.add(createTestTreeNode("CheckBox", new CheckBoxTest(testContext), MaterialIcon.HELP, "CheckBox", "On-off/true-false toggle"));
		testNodes.add(createTestTreeNode("TemplateField", new TemplateFieldTest(testContext), MaterialIcon.HELP, "TemplateField", "TemplateField"));
		testNodes.add(createTestTreeNode("ComboBox", new ComboBoxTest(testContext), MaterialIcon.HELP, "ComboBox", "ComboBox"));
		testNodes.add(createTestTreeNode("TagComboBox", new TagComboBoxTest(testContext), MaterialIcon.HELP, "TagComboBox", "Multi-selection ComboBox"));
		testNodes.add(createTestTreeNode("Button", new ButtonTest(testContext), MaterialIcon.HELP, "Button", "Button"));
		testNodes.add(createTestTreeNode("LinkButton", new LinkButtonTest(testContext), MaterialIcon.HELP, "LinkButton", "Button that looks like a link"));
		testNodes.add(createTestTreeNode("LocalDateField", new LocalDateFieldTest(testContext), MaterialIcon.HELP, "LocalDateField", "For displaying an local's date values (zoned)"));
		testNodes.add(createTestTreeNode("LocalTimeField", new LocalTimeFieldTest(testContext), MaterialIcon.HELP, "LocalTimeField", "For choosing local time values"));
		testNodes.add(createTestTreeNode("LocalDateTimeField", new LocalDateTimeFieldTest(testContext), MaterialIcon.HELP, "LocalDateTimeField", "For displaying an instants (zoned)"));
		testNodes.add(createTestTreeNode("InstantDateTimeField", new InstantDateTimeFieldTest(testContext), MaterialIcon.HELP, "InstantDateTimeField", "For displaying an instants (zoned)"));
		testNodes.add(createTestTreeNode("ComponentField", new ComponentFieldTest(testContext), MaterialIcon.HELP, "ComponentField", "Field containing a normal component"));

		testNodes.add(createTestTreeNode("MobileLayout", new MobileLayoutTest(testContext), MaterialIcon.HELP, "MobileLayout", "Card layout optimized for small devices"));
		testNodes.add(createTestTreeNode("NavigationBar", new NavigationBarTest(testContext), MaterialIcon.HELP, "NavigationBar", "Contains navigation buttons for mobile applications"));
		testNodes.add(createTestTreeNode("ToolAccordion", new ToolAccordionTest(testContext), MaterialIcon.HELP, "ToolAccordion", "Providing an ItemView-like list of buttons"));

		testNodes.add(createTestTreeNode("DocumentViewer", new DocumentViewerTest(testContext), MaterialIcon.HELP, "DocumentViewer", "Zoomable readonly documents view"));
		testNodes.add(createTestTreeNode("VideoPlayer", new VideoPlayerTest(testContext), MaterialIcon.HELP, "VideoPlayer", "video/audio player component"));
		testNodes.add(createTestTreeNode("ShakaPlayer", new ShakaPlayerTest(testContext), MaterialIcon.HELP, "ShakaPlayer", "VOD player"));
		testNodes.add(createTestTreeNode("MediaSoupV3WebRtcClient", new MediaSoupV3WebRtcClientTest(testContext), CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "MediaSoupV3WebRtcClient", "For MediaSoup V3"));
		testNodes.add(createTestTreeNode("AudioLevelIndicator", new AudioLevelIndicatorTest(testContext), CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "AudioLevelIndicator", "Audio level indicator"));

		testNodes.add(createTestTreeNode("TimeGraph", new TimeGraphTest(testContext), MaterialIcon.MULTILINE_CHART, "TimeGraph", "Displays aggregated data over time"));
		testNodes.add(createTestTreeNode("PieChart", new PieChartTest(testContext), MaterialIcon.PIE_CHART, "Pie Chart", "Circular display of data proportions"));
		testNodes.add(createTestTreeNode("TreeChart", new TreeGraphTest(testContext), MaterialIcon.PIE_CHART, "Tree Chart", "Hierarchical chart"));
		testNodes.add(createTestTreeNode("ForceLayoutGraph", new ForceLayoutGraphTest(testContext), MaterialIcon.GRADE, "Force Layout Graph", "Force Layout"));

		testNodes.add(createTestTreeNode("ChatView", new ChatTest(testContext), MaterialIcon.HELP, "ChatView", "A chat client component"));
		testNodes.add(createTestTreeNode("SideDrawer", new SideDrawerTest(testContext), MaterialIcon.HELP, "Side Drawer", "Container floating over another component"));
		testNodes.add(createTestTreeNode("Notifications", new NotificationTest(testContext), MaterialIcon.HELP, "Notifications", "Notifications"));
		testNodes.add(createTestTreeNode("HTMLView", new HtmlViewTest(testContext), MaterialIcon.HELP, "HTML View", "HTML view"));
		testNodes.add(createTestTreeNode("WakeLock", new WakeLockTest(testContext), MaterialIcon.HELP, "WakeLock", "Make the browser stay awake!"));
		testNodes.add(createTestTreeNode("SessionContext", new SessionContextTest(testContext), MaterialIcon.HELP, "SessionContext", "SessionContext stuff!"));

		treeModel.setRecords(testNodes);
	}

	private ComponentTestTreeNode createTestTreeNode(String basePathName, ComponentTest<?> componentTest, Icon icon, String title, String description) {
		return new ComponentTestTreeNode(icon, title, description, basePathName, componentTest);
	}

	public Component getRootComponent() {
		return rootComponent;
	}

}
