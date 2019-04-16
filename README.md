
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

In this example we create a responsive application with a single perspective, a few empty panels and a toolbar.
Please use the `teamapps-server-jetty-embedded` dependency to run this example.

```java
public static void main(String[] args) throws Exception {
    WebController controller = SimpleWebController.createDefaultController(context -> {
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
            CurrentSessionContext.get().showNotification(MaterialIcon.MESSAGE, "Save was clicked!");
        });
        buttonGroup.addButton(ToolbarButton.create(MaterialIcon.DELETE, "Delete", "Delete some items"));

        //display these buttons only when this perspective is visible
        perspective.addWorkspaceButtonGroup(buttonGroup);


        application.showPerspective(perspective);
        return application.createUi();
    }, Color.MATERIAL_GREY_300, true);

    new TeamAppsJettyEmbeddedServer(controller, Files.createTempDir()).start();
}
```
The result should look something like this:
![ScreenShot](https://raw.githubusercontent.com/teamapps-org/teamapps-screenshots/master/screenshots/teamapps-example1.png)

## License

The TeamApps Framework is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
