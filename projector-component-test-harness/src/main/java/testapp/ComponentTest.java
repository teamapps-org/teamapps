

package testapp;

import org.teamapps.projector.component.Component;

public interface ComponentTest<T extends Component> {

	Component getParametersComponent();

	T getComponent();

	Component getWrappedComponent();

	default String getDocsHtmlResourceName() {
		return null;
	}

}
