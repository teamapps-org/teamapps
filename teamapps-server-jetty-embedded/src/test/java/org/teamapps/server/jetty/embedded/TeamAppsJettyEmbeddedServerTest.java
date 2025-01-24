/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.server.jetty.embedded;

import org.teamapps.common.format.RgbaColor;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.table.AbstractTableModel;
import org.teamapps.ux.component.table.Table;
import org.teamapps.webcontroller.WebController;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class TeamAppsJettyEmbeddedServerTest {

	public static final List<RgbaColor> FOREGROUND_COLORS = Arrays.asList(
			RgbaColor.MATERIAL_GREEN_500,
			RgbaColor.MATERIAL_RED_700,
			RgbaColor.MATERIAL_BLUE_600,
			RgbaColor.MATERIAL_YELLOW_600,
			RgbaColor.MATERIAL_PURPLE_500,
			RgbaColor.MATERIAL_BROWN_500,
			RgbaColor.MATERIAL_PINK_500,
			RgbaColor.MATERIAL_DEEP_ORANGE_500
	);

	public static <T> T randomOf(Collection<T> collection) {
		List<T> list = new ArrayList<>(collection);
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}

	public static RgbaColor randomColor() {
		return randomOf(FOREGROUND_COLORS);
	}

	public static void main(String[] args) throws Exception {
		WebController controller = sessionContext -> {
			RootPanel rootPanel = new RootPanel();
			sessionContext.addRootPanel(null, rootPanel);

			Table<String> table = new Table<>();
			table.addColumn("a", "a", new TextField()).setValueExtractor(Objects::toString);
			table.addColumn("b", "b", new TextField()).setValueExtractor(Objects::toString);
			table.addColumn("c", "c", new TextField()).setValueExtractor(Objects::toString);
			table.addColumn("d", "d", new TextField()).setValueExtractor(Objects::toString);
			table.addColumn("e", "e", new TextField()).setValueExtractor(Objects::toString);
			table.addColumn("f", "f", new TextField()).setValueExtractor(Objects::toString);
			table.addColumn("g", "g", new TextField()).setValueExtractor(Objects::toString);
			table.addColumn("h", "h", new TextField()).setValueExtractor(Objects::toString);

			table.setRowCssStyleProvider(s -> Map.of("background-color", randomColor().toHtmlColorString()));

			table.setModel(new AbstractTableModel<String>() {

				private final List<String> data = IntStream.range(0, 10000).mapToObj(value -> "" + value).toList();

				@Override
				public int getCount() {
					return data.size();
				}

				@Override
				public List<String> getRecords(int startIndex, int length) {
					return data.stream().skip(startIndex).limit(length).toList();
				}
			});
			table.setCellMarked("12", "x", true);

			rootPanel.setContent(table);
		};

		TeamAppsJettyEmbeddedServer.builder(controller)
				.setPort(8082)
				.build()
				.start();
	}


}
