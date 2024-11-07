package org.teamapps;

import org.teamapps.common.format.Color;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.event.Event;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.application.ResponsiveApplication;
import org.teamapps.ux.application.layout.ExtendedLayout;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.format.SizeType;
import org.teamapps.ux.component.format.SizingPolicy;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.template.gridtemplate.GlyphIconElement;
import org.teamapps.ux.component.template.gridtemplate.GridTemplate;
import org.teamapps.ux.component.template.gridtemplate.TextElement;
import org.teamapps.ux.component.tree.Tree;
import org.teamapps.ux.model.TreeModel;
import org.teamapps.ux.model.TreeModelChangedEventData;

import java.util.List;
import java.util.Map;

public class GlyphIconDemoApp {

    static final int GLYPHICON_WIDTH_IN_PIXEL = 19;

    private final ResponsiveApplication responsiveApplication;
    final private Component rootComponent;

    private GlyphIconElement glyphIconElement1;
    private GlyphIconElement glyphIconElement2;


    public GlyphIconDemoApp() {
        this.responsiveApplication = ResponsiveApplication.createApplication();
        this.rootComponent = createRootComponent();
    }

    public Component getRootComponent() {
        return rootComponent;
    }

    private Component createRootComponent() {
        View view = View.createView( ExtendedLayout.LEFT, MaterialIcon.HELP, "GlyphIcon-Demo", createPanel() );
        view.setVisible( true );

        Perspective perspective = Perspective.createPerspective();
        perspective.addView( view );

        responsiveApplication.addPerspective( perspective );
        responsiveApplication.showPerspective( perspective );

        return responsiveApplication.getUi();
    }

    Component createPanel() {
        glyphIconElement1 = createIconElement( "iconClock", 0 );
        glyphIconElement2 = createIconElement( "iconArrowLeft", 1 );

        glyphIconElement1.setBackgroundColor( Color.ALICE_BLUE );
        glyphIconElement2.setBackgroundColor( Color.MATERIAL_LIME_50 );

        Tree<String> tree = new Tree<>( new Model() );
        tree.setVisible( true );
        tree.setEntryTemplate( createGridTemplate() );
        tree.setPropertyProvider( getPropertyProvider() );

        Panel panel = new Panel();
        panel.setContent( tree );

        return panel;
    }

    private GlyphIconElement createIconElement( String propertyName, int columnGridStartIndex ) {
        // ToDo The fontColor argument of the constructor has no effect!
        return new GlyphIconElement( propertyName, 0, columnGridStartIndex, GLYPHICON_WIDTH_IN_PIXEL, Color.RED );
    }

    private GridTemplate createGridTemplate() {
        GridTemplate template = new GridTemplate();
        template
            .addColumn( new SizingPolicy( SizeType.FIXED, 30.0f, 40 ) )
            .addColumn( new SizingPolicy( SizeType.FIXED, 30.0f, 40 ) )
            .addColumn( new SizingPolicy( SizeType.AUTO, 1.0f, 200 ) )
            .addRow( SizeType.AUTO, 1.0f, 40, 0, 0 )
            .addElement( glyphIconElement1 )
            .addElement( glyphIconElement2 )
            .addElement( new TextElement( "textField", 0, 2 ) );
        return template;
    }


    private PropertyProvider<String> getPropertyProvider() {
        return (protocolEntry, propertyNames) -> Map.of (
            "iconArrowRight", "glyphicon glyphicon-circle-arrow-right",
            "iconArrowLeft", "glyphicon glyphicon-circle-arrow-left",
            "iconClock", "glyphicon glyphicon-time",
            "textField", "   These GlyphIcons are not displayed correctly." );
    }



    private class Model implements TreeModel<String> {
        @Override
        public Event<Void> onAllNodesChanged() {
            return new Event<>();
        }

        @Override
        public Event<TreeModelChangedEventData<String>> onChanged() {
            return new Event<>();
        }

        @Override
        public List<String> getRecords() {
            return List.of( "Line 1", "Line 2", "Line 3" );
        }
    }
}
