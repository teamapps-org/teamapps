

package testapp.test.formfield.combobox;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.projector.component.treecomponents.tree.model.ComboBoxModel;
import org.teamapps.projector.component.treecomponents.tree.simple.EagerNodesFilter;
import org.teamapps.projector.component.treecomponents.tree.simple.SimpleTreeNodeImpl;
import testapp.ComponentTestContext;
import testapp.util.DemoDataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestComboBoxModel implements ComboBoxModel<SimpleTreeNodeImpl<Void>> {

	private final ComponentTestContext context;
	private long latency = 0;
	private final List<SimpleTreeNodeImpl<Void>> nodes;

	public TestComboBoxModel(ComponentTestContext context) {
		this.context = context;
		nodes = createTreeData();
	}

	@Override
	public List<SimpleTreeNodeImpl<Void>> getRecords(String query) {
		sleep();
		List<SimpleTreeNodeImpl<Void>> result;
		if (StringUtils.isEmpty(query)) {
			result = this.nodes.stream()
					.filter(new EagerNodesFilter())
					.collect(Collectors.toList());
		} else {
			List<SimpleTreeNodeImpl<Void>> filteredTree = this.nodes.stream()
					.filter(node -> StringUtils.containsIgnoreCase(node.getCaption(), query))
					.flatMap(node -> ((List<SimpleTreeNodeImpl<Void>>) (List) node.getPath()).stream())
					.distinct()
					.collect(Collectors.toList());
			List<SimpleTreeNodeImpl<Void>> treeCopy = SimpleTreeNodeImpl.copyTree(filteredTree);
			treeCopy.forEach(node -> node.setExpanded(true));
			result = treeCopy;
		}
//		this.context.printLineToConsole(" --> Returning " + result.size() + " root nodes.");
		return result;
	}

	@Override
	public List<SimpleTreeNodeImpl<Void>> getChildRecords(SimpleTreeNodeImpl<Void> parent) {
		this.context.printLineToConsole("Child nodes requested for parent node \"" + parent + "\".");
		sleep();
		List<SimpleTreeNodeImpl<Void>> childRecords = nodes.stream()
				.filter(record -> record.getPath().contains(parent) && record != parent)
				.filter(new EagerNodesFilter(parent))
				.collect(Collectors.toList());
		this.context.printLineToConsole(" --> Returning " + childRecords.size() + " child nodes.");
		return childRecords;
	}

	private void sleep() {
		try {
			Thread.sleep(latency);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setLatency(long latency) {
		this.latency = latency;
	}

	private static List<SimpleTreeNodeImpl<Void>> createTreeData() {
		List<SimpleTreeNodeImpl<Void>> data = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			SimpleTreeNodeImpl<Void> rootRecord = new SimpleTreeNodeImpl<>(DemoDataGenerator.randomUserImageUrl(), "Root Node (non-selectable)" + (i + 1));
			rootRecord.setExpanded(i % 3 == 0);
			if (i % 3 == 1) {
				rootRecord.setLazyChildren(true);
				rootRecord.setDescription("Loads its children lazily");
			} else {
				rootRecord.setDescription("Loads its children eagerly");
			}
			rootRecord.setSelectable(false);
			data.add(rootRecord);
			if (i % 3 != 2) {
				for (int j = 0; j < 3; j++) {
					SimpleTreeNodeImpl<Void> level2Record = new SimpleTreeNodeImpl<>(DemoDataGenerator.randomUserImageUrl(), "Child Node " + (i + 1) + "-" + (j + 1));
					level2Record.setParent(rootRecord);
					if (j % 3 == 1) {
						level2Record.setLazyChildren(true);
						level2Record.setDescription("Loads its children lazily");
					} else {
						level2Record.setDescription("Loads its children eagerly");
					}
					data.add(level2Record);
					if (j % 3 != 2) {
						for (int k = 0; k < 3; k++) {
							SimpleTreeNodeImpl<Void> level3Record = new SimpleTreeNodeImpl<>(DemoDataGenerator.randomUserImageUrl(),
									"Grand-child Node " + (i + 1) + "-" + (j + 1) + "-" + (k + 1));
							level3Record.setParent(level2Record);
							data.add(level3Record);
						}
					}
				}
			}
		}
		return data;
	}

}