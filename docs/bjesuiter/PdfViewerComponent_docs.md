# PdfViewerComponent Implementation

## Parts 

DTO: `/teamapps-ui-api/src/main/dto/UiPdfViewer.dto`

Java Impl: `teamapps-ux/src/main/java/org/teamapps/ux/component/pdfviewer`

Typescript Impl: `teamapps-client/ts/modules`

Testfile (for anybody working in this repo): 
`teamapps-server-jetty-embedded/src/test/java/org/teamapps/server/jetty/embedded/TeamAppsJettyEmbeddedServerTest.java`

## Testfile Content for testing PdfViewer

```java
/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import org.teamapps.ux.component.pdfviewer.PdfViewer;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.table.AbstractTableModel;
import org.teamapps.ux.component.table.Table;
import org.teamapps.ux.resource.ClassPathResource;
import org.teamapps.webcontroller.WebController;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class TeamAppsJettyEmbeddedServerTest {

	public static void main(String[] args) throws Exception {
		WebController controller = sessionContext -> {
			RootPanel rootPanel = new RootPanel();
			sessionContext.addRootPanel(null, rootPanel);

			String testPdfLink = sessionContext.createResourceLink(new ClassPathResource("test.pdf", "application/pdf" ));
			PdfViewer component = new PdfViewer(testPdfLink);
			rootPanel.setContent(component);
		};

		TeamAppsJettyEmbeddedServer.builder(controller)
				.setPort(8082)
				.build()
				.start();
	}


}

```

## Run the Test environment 

Preparation: 
- mvn clean install (aka TeamApps - clean + install)
  - installs/compiles everything in project 

1. Run the Testfile (`teamapps-server-jetty-embedded/src/test/java/org/teamapps/server/jetty/embedded/TeamAppsJettyEmbeddedServerTest.java`) via IntelliJ (Green Arrow in UI)
2. Goto teamapps-client in a shell
3. Run yarn install 
4. Run `./start-dev-server.sh 8082` (because the Jetty starts at that port)
5. Goto http://localhost:9000 to see/debug the component

## Compile the DTO again 

- run clean + install for the Teamapps UX Subpackage

## Todos

- In PdfViewer.java: update all setters to update ts client