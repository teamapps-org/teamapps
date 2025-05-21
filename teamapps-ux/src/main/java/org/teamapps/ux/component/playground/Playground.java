package org.teamapps.ux.component.playground;

import org.teamapps.dto.*;
import org.teamapps.ux.component.AbstractComponent;

public class Playground extends AbstractComponent {

    protected String title;

    public Playground() {
        this(null);
    }

    public Playground(String title) {
        this.title = title;
    }

    @Override
    public UiComponent createUiComponent() {
        UiPlayground uiPlayground = new UiPlayground();
        return uiPlayground;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        queueCommandIfRendered(() -> new UiPlayground.SetTitleCommand(getId(), title));
    }

}
