/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
package org.teamapps.ux.component.form.dynamic;

import org.junit.Assert;
import org.junit.Test;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.form.layoutpolicy.FormLayoutPolicy;
import org.teamapps.ux.component.form.layoutpolicy.FormSection;
import org.teamapps.ux.component.form.layoutpolicy.FormSectionFieldPlacement;

import java.util.List;

import static org.teamapps.ux.component.form.dynamic.DynamicFormLayout.LabelPosition.ABOVE;

public class DynamicFormTest {

	@Test
	public void defaultUsesHybridLayouts() {
		run(() -> {
			DynamicForm<Object> form = new DynamicForm<>();
			Assert.assertEquals(List.of(0, 576, 900, 1400, 1600),
					form.getLayouts().stream().map(DynamicFormLayout::getMinWidth).toList());
			Assert.assertEquals(List.of(1, 1, 2, 3, 4),
					form.getLayouts().stream().map(DynamicFormLayout::getFieldColumns).toList());
		});
	}

	@Test
	public void packsLogicalColumnSpansWithoutOverlap() {
		run(() -> {
			DynamicForm<Object> form = DynamicForm.<Object>builder()
				.layouts(List.of(new DynamicFormLayout(0, 3, ABOVE)))
				.build();
			DynamicFormSection section = form.addSection("contact", "Contact");
			section.addField("first", "First", new TextField());
			section.addField("address", "Address", new TextField()).columnSpan(2);
			section.addField("notes", "Notes", new TextField()).fullWidth();

			FormSection uiSection = form.getLayoutPolicies().get(0).getSections().get(0);
			Assert.assertEquals(3, uiSection.getColumns().size());
			Assert.assertEquals(4, uiSection.getRows().size());
			List<FormSectionFieldPlacement> placements = uiSection.getPlacements().stream()
				.map(FormSectionFieldPlacement.class::cast).toList();
			Assert.assertEquals(List.of(0, 1, 0, 1, 2, 3), placements.stream().map(FormSectionFieldPlacement::getRow).toList());
			Assert.assertEquals(List.of(0, 0, 1, 1, 0, 0), placements.stream().map(FormSectionFieldPlacement::getColumn).toList());
			Assert.assertEquals(List.of(1, 1, 2, 2, 3, 3), placements.stream().map(FormSectionFieldPlacement::getColSpan).toList());
		});
	}

	@Test
	public void removedFieldsLeaveTheActiveModelAndBecomeTombstones() {
		run(() -> {
			DynamicForm<Object> form = new DynamicForm<>();
			DynamicFormSection section = form.addSection("contact", "Contact");
			DynamicFormField first = section.addField("first", "First", new TextField());
			section.addField("last", "Last", new TextField());

			Assert.assertTrue(first.remove());
			Assert.assertEquals(List.of("last"), form.getFieldsMap().keySet().stream().toList());
			Assert.assertEquals(1, form.getRetainedFieldCount());
			Assert.assertFalse(first.getField().isVisible());
			Assert.assertFalse(first.getLabel().isVisible());
		});
	}

	@Test
	public void repeaterSwitchesFromCardsToOneWideTable() {
		run(() -> {
			DynamicForm<Object> form = DynamicForm.<Object>builder()
				.layouts(List.of(new DynamicFormLayout(0, 1, ABOVE), new DynamicFormLayout(1600, 4, ABOVE)))
				.build();
			DynamicFormRepeater repeater = form.addRepeater("addresses", "Addresses")
				.tableAt(DynamicFormLayouts.Breakpoint.WIDE)
				.actionColumn("Action", 154);
			repeater.addColumn("First");
			repeater.addColumn("Last");
			repeater.addRow("Record 1", row -> {
				row.addField("rows.1.first", new TextField());
				row.addField("rows.1.last", new TextField());
				row.action(new TextField());
			});
			repeater.addRow("Record 2", row -> {
				row.addField("rows.2.first", new TextField());
				row.addField("rows.2.last", new TextField());
				row.action(new TextField());
			});

			FormLayoutPolicy cards = policy(form, 0);
			FormLayoutPolicy table = policy(form, 1600);
			Assert.assertEquals(2, cards.getSections().size());
			Assert.assertEquals(1, table.getSections().size());
			Assert.assertEquals(3, table.getSections().get(0).getColumns().size());
			Assert.assertEquals(3, table.getSections().get(0).getRows().size());
		});
	}

	@Test
	public void tableAtAddsAnExactEffectiveBreakpoint() {
		run(() -> {
			DynamicForm<Object> form = DynamicForm.<Object>builder()
					.layouts(List.of(new DynamicFormLayout(0, 1, ABOVE), new DynamicFormLayout(1600, 4, ABOVE)))
					.build();
			DynamicFormRepeater repeater = form.addRepeater("addresses", "Addresses").tableAt(1200);
			repeater.addColumn("First");
			repeater.addRow("Record 1", row -> row.addField("rows.1.first", new TextField()));

			Assert.assertEquals(List.of(0, 1200, 1600),
					form.getLayoutPolicies().stream().map(FormLayoutPolicy::getMinWidth).toList());
			Assert.assertEquals(1, policy(form, 1200).getSections().size());
			Assert.assertEquals("addresses", policy(form, 1200).getSections().get(0).getId());
		});
	}

	@Test
	public void readOnlyAlsoAppliesToFieldsAddedLater() {
		run(() -> {
			DynamicForm<Object> form = new DynamicForm<>();
			form.setReadOnly(true);
			TextField field = new TextField();
			form.addSection("person", "Person").addField("first", "First", field);

			Assert.assertEquals(FieldEditingMode.READONLY, field.getEditingMode());
			form.setReadOnly(false);
			Assert.assertEquals(FieldEditingMode.EDITABLE, field.getEditingMode());
		});
	}

	@Test
	public void rejectsMissingZeroLayoutAndDuplicateActiveProperties() {
		run(() -> {
			assertIllegalArgument(() -> DynamicForm.<Object>builder()
					.layouts(List.of(new DynamicFormLayout(576, 1, ABOVE))).build());

			DynamicForm<Object> form = new DynamicForm<>();
			assertIllegalArgument(() -> form.setLayouts(List.of(new DynamicFormLayout(576, 1, ABOVE))));
			Assert.assertEquals(0, form.getLayouts().get(0).getMinWidth());
			DynamicFormSection section = form.addSection("person", "Person");
			TextField field = new TextField();
			section.addField("first", "First", field);
			assertIllegalArgument(() -> section.addField("first", "Duplicate", field));
			Assert.assertEquals(1, section.getElements().size());
			assertIllegalArgument(() -> form.removeLayout(0));
		});
	}

	@Test
	public void beanBindingUsesOnlyActiveFields() {
		run(() -> {
			DynamicForm<Person> form = new DynamicForm<>();
			DynamicFormSection section = form.addSection("person", "Person");
			DynamicFormField retired = section.addField("firstName", "First", new TextField());
			TextField last = new TextField();
			section.addField("lastName", "Last", last);
			Person person = new Person();
			person.setFirstName("Ada");
			person.setLastName("Lovelace");

			retired.remove();
			form.applyRecordValuesToFields(person);
			Assert.assertEquals("Lovelace", last.getValue());
			last.setValue("Byron");
			form.applyFieldValuesToRecord(person);
			Assert.assertEquals("Ada", person.getFirstName());
			Assert.assertEquals("Byron", person.getLastName());
		});
	}

	private static void run(Runnable runnable) {
		UxTestUtil.doWithMockedSessionContext(runnable).join();
	}

	private static FormLayoutPolicy policy(DynamicForm<?> form, int minWidth) {
		return form.getLayoutPolicies().stream().filter(candidate -> candidate.getMinWidth() == minWidth).findFirst().orElseThrow();
	}

	private static void assertIllegalArgument(Runnable runnable) {
		try {
			runnable.run();
			Assert.fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			// expected
		}
	}

	public static class Person {
		private String firstName;
		private String lastName;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	}
}
