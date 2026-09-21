package org.teamapps;

import org.teamapps.server.jetty.embedded.TeamAppsJettyEmbeddedServer;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.webcontroller.WebController;

public class Main {
    public static void main( String[] args ) {
        WebController controller1 = sessionContext -> {
            GlyphIconDemoApp demoApp = new GlyphIconDemoApp();

            RootPanel rootPanel = new RootPanel();
            rootPanel.setContent( demoApp.getRootComponent() );

            sessionContext.addRootPanel(null, rootPanel );
        };

        try {
            TeamAppsJettyEmbeddedServer.builder( controller1 )
                .setPort( 8080 )
                .build()
                .start();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
}