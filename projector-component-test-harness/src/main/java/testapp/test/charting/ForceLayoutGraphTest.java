

package testapp.test.charting;

import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.chart.forcelayout.ExpandState;
import org.teamapps.projector.component.chart.forcelayout.ForceLayoutGraph;
import org.teamapps.projector.component.chart.forcelayout.ForceLayoutLink;
import org.teamapps.projector.component.chart.forcelayout.ForceLayoutNode;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.util.DemoDataGenerator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ForceLayoutGraphTest extends AbstractComponentTest<ForceLayoutGraph<BaseTemplateRecord<Void>>> {

	public ForceLayoutGraphTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
	}

	@Override
	public ForceLayoutGraph<BaseTemplateRecord<Void>> createComponent() {
		int numberOfNodes = 100;

		List<ForceLayoutNode<BaseTemplateRecord<Void>>> nodes = createNodes(numberOfNodes, "Node-");
		List<ForceLayoutLink<BaseTemplateRecord<Void>>> links = createLinks(nodes, null);
		ForceLayoutGraph<BaseTemplateRecord<Void>> graph = new ForceLayoutGraph<>(nodes, links);
		graph.setAnimationDuration(numberOfNodes * 10);

		graph.onNodeExpandedOrCollapsed.addListener(event -> {
			List<ForceLayoutNode<BaseTemplateRecord<Void>>> childNodes = createNodes(10, event.getNode().getRecord().getCaption() + "-");
			List<ForceLayoutLink<BaseTemplateRecord<Void>>> childLinks = createLinks(childNodes, event.getNode());
			if (event.isExpanded()) {
				graph.addNodesAndLinks(childNodes, childLinks);
			} else {
				List<ForceLayoutNode<BaseTemplateRecord<Void>>> nodesToRemove = graph.getNodes().stream()
						.filter(n -> n.getRecord().getCaption().startsWith(event.getNode().getRecord().getCaption() + "-"))
						.collect(Collectors.toList());
				graph.removeNodesAndLinks(nodesToRemove);
			}
		});
		return graph;
	}

	private List<ForceLayoutNode<BaseTemplateRecord<Void>>> createNodes(int numberOfNodes, String captionPrefix) {
		return IntStream.range(0, numberOfNodes)
					.mapToObj(i -> {
						ForceLayoutNode<BaseTemplateRecord<Void>> node = new ForceLayoutNode<>(new BaseTemplateRecord<Void>(MaterialIcon.PLAY_CIRCLE_OUTLINE, captionPrefix + i, "This is the description"), 100, 30);
						node.setBackgroundColor(RgbaColor.MATERIAL_GREEN_100);
						node.setExpandState(ExpandState.values()[i % ExpandState.values().length]);
						node.setTemplate(BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES);
						return node;
					})
					.collect(Collectors.toList());
	}

	private List<ForceLayoutLink<BaseTemplateRecord<Void>>> createLinks(List<ForceLayoutNode<BaseTemplateRecord<Void>>> nodes, ForceLayoutNode<BaseTemplateRecord<Void>> rootParent) {
		return IntStream.range(0, nodes.size())
				.mapToObj(i -> {
					ForceLayoutNode<BaseTemplateRecord<Void>> parentNode = nodes.get((int) Math.floor(Math.sqrt(i)));
					ForceLayoutNode<BaseTemplateRecord<Void>> childNode = nodes.get(i);
					ForceLayoutLink<BaseTemplateRecord<Void>> link = new ForceLayoutLink(childNode, parentNode == childNode && rootParent != null ? rootParent : parentNode);
					link.setLineColor(DemoDataGenerator.randomColor());
					link.setLineWidth((float) ThreadLocalRandom.current().nextDouble(.5, 5));
					link.setLineDashArray(i % 5 == 0 ? "8 2 4" : null);
					return link;
				})
				.collect(Collectors.toList());
	}

	@Override
	public String getDocsHtmlResourceName() {
		return null;
	}
}
