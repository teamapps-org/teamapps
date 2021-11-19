
# TeamApps

TeamApps is a Java web application framework


## Getting started

### Setting up the dependencies

For the TeamApps framework use - please replace `x`, `y` and `z` with the latest version numbers: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.teamapps/teamapps/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.teamapps/teamapps)


```xml
<dependency>
    <groupId>org.teamapps</groupId>
    <artifactId>teamapps-ux</artifactId>
    <version>x.z.y</version>
</dependency>
```

To start a ready to run server with TeamApps included use:


```xml
<dependency>
    <groupId>org.teamapps</groupId>
    <artifactId>teamapps-server-jetty-embedded</artifactId>
    <version>x.z.y</version>
</dependency>
```

## Quick start

### Hello World
         
This will start a server on port 8080, so you should see the result under http://localhost:8080 

```java
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.server.jetty.embedded.TeamAppsJettyEmbeddedServer;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.rootpanel.RootPanel;

public class HelloWorld {

	public static void main(String[] args) throws Exception {
		new TeamAppsJettyEmbeddedServer(sessionContext -> {
			RootPanel rootPanel = sessionContext.addRootPanel();
			Button<?> button = Button.create(MaterialIcon.INFO, "Click me!");
			button.onClicked.addListener(() -> {
				sessionContext.showNotification(MaterialIcon.CHAT, "Hello World!", "Congrats for your first TeamApps program!");
			});
			rootPanel.setContent(button);
		}, 8080).start();
	}

}
```

### Application Layout

In this example we create a responsive application with a single perspective, a few empty panels and a toolbar.
Add the `teamapps-server-jetty-embedded` dependency to run this example.

```java
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.server.jetty.embedded.TeamAppsJettyEmbeddedServer;
import org.teamapps.ux.application.ResponsiveApplication;
import org.teamapps.ux.application.layout.StandardLayout;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.webcontroller.WebController;

public class TeamAppsDemo {

    public static void main(String[] args) throws Exception {
        WebController controller = sessionContext -> {
            RootPanel rootPanel = new RootPanel();
            sessionContext.addRootPanel(null, rootPanel);

            //create a responsive application that will run on desktops as well as on smart phones
            ResponsiveApplication application = ResponsiveApplication.createApplication();

            //create perspective with default layout
            Perspective perspective = Perspective.createPerspective();
            application.addPerspective(perspective);

            //create an empty left panel
            perspective.addView(View.createView(StandardLayout.LEFT, MaterialIcon.MESSAGE, "Left panel", null));

            //create a tabbed center panel
            perspective.addView(View.createView(StandardLayout.CENTER, MaterialIcon.SEARCH, "Center panel", null));
            perspective.addView(View.createView(StandardLayout.CENTER, MaterialIcon.PEOPLE, "Center panel 2", null));

            //create a right panel
            perspective.addView(View.createView(StandardLayout.RIGHT, MaterialIcon.FOLDER, "Left panel", null));

            //create a right bottom panel
            perspective.addView(View.createView(StandardLayout.RIGHT_BOTTOM, MaterialIcon.VIEW_CAROUSEL, "Left bottom panel", null));

            //create toolbar buttons
            ToolbarButtonGroup buttonGroup = new ToolbarButtonGroup();
            buttonGroup.addButton(ToolbarButton.create(MaterialIcon.SAVE, "Save", "Save changes")).onClick.addListener(toolbarButtonClickEvent -> {
                sessionContext.showNotification(MaterialIcon.MESSAGE, "Save was clicked!");
            });
            buttonGroup.addButton(ToolbarButton.create(MaterialIcon.DELETE, "Delete", "Delete some items"));

            //display these buttons only when this perspective is visible
            perspective.addWorkspaceButtonGroup(buttonGroup);

            application.showPerspective(perspective);
            rootPanel.setContent(application.getUi());

            // set Background Image
            String defaultBackground = "/resources/backgrounds/default-bl.jpg";
            sessionContext.registerBackgroundImage("default", defaultBackground, defaultBackground);
            sessionContext.setBackgroundImage("default", 0);
        };

        new TeamAppsJettyEmbeddedServer(controller, 8080).start();
    }
}
```
The result should look something like this:
![ScreenShot](https://raw.githubusercontent.com/teamapps-org/teamapps-screenshots/master/screenshots/teamapps-example1.png)

## License

The TeamApps Framework is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

## Supported By

<a href="https://www.ej-technologies.com/products/jprofiler/overview.html">JProfiler</a>

<a href="https://www.ej-technologies.com/products/jprofiler/overview.html"><img src="https://www.ej-technologies.com/images/product_banners/jprofiler_large.png"></a>

<a href="https://www.yourkit.com/java/profiler/">YourKit</a>
  
<a href="https://www.yourkit.com/java/profiler/"><img src="https://www.yourkit.com/images/yklogo.png"></a> 
