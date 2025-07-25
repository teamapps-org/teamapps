

package testapp.test.tree;

import org.teamapps.projector.component.treecomponents.tree.simple.SimpleTreeModel;
import org.teamapps.projector.component.treecomponents.tree.simple.SimpleTreeNodeImpl;
import testapp.ComponentTestContext;
import testapp.util.DemoDataGenerator;

import java.util.ArrayList;
import java.util.List;

public class TestTreeModel extends SimpleTreeModel<Void> {

	private final ComponentTestContext context;
	private long latency = 0;

	public TestTreeModel(ComponentTestContext context) {
		this.context = context;
		List<SimpleTreeNodeImpl<Void>> data = createTreeData();
		this.setNodes(data);
	}

	@Override
	public List<SimpleTreeNodeImpl<Void>> getRecords() {
		sleep();
		List<SimpleTreeNodeImpl<Void>> result = super.getRecords();
		this.context.printLineToConsole(" --> Returning " + result.size() + " root nodes.");
		return result;
	}

	@Override
	public List<SimpleTreeNodeImpl<Void>> getChildRecords(SimpleTreeNodeImpl<Void> parent) {
		this.context.printLineToConsole("Child nodes requested for parent node \"" + parent + "\".");
		sleep();
		List<SimpleTreeNodeImpl<Void>> childRecords = super.getChildRecords(parent);
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
			SimpleTreeNodeImpl<Void> rootRecord = new SimpleTreeNodeImpl<>(DemoDataGenerator.randomUserImageUrl(), "Root Node " + (i + 1));
			rootRecord.setTitle(rootRecord.getCaption());
			rootRecord.setAriaLabel(rootRecord.getCaption());
			rootRecord.setExpanded(i % 3 == 0);
			if (i % 3 == 1) {
				rootRecord.setLazyChildren(true);
				rootRecord.setDescription("Loads its children lazily");
			} else {
				rootRecord.setDescription("Loads its children eagerly");
			}
			data.add(rootRecord);
			if (i % 3 != 2) {
				for (int j = 0; j < 3; j++) {
					SimpleTreeNodeImpl<Void> level2Record = new SimpleTreeNodeImpl<>(DemoDataGenerator.randomUserImageUrl(), "Child Node " + (i + 1) + "-" + (j + 1));
					level2Record.setTitle(level2Record.getCaption());
					level2Record.setAriaLabel(level2Record.getCaption());
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
							SimpleTreeNodeImpl<Void> level3Record = new SimpleTreeNodeImpl<>(DemoDataGenerator.randomUserImageUrl(), "Grand-child Node " + (i + 1) + "-" + (j + 1) + "-" + (k + 1));
							level3Record.setTitle(level3Record.getCaption());
							level3Record.setAriaLabel(level3Record.getCaption());
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