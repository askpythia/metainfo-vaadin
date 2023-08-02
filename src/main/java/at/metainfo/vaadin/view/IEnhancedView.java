package at.metainfo.vaadin.view;

import com.vaadin.flow.component.Component;

import at.metainfo.vaadin.utilities.IGuiUtilities;

public interface IEnhancedView extends IGuiUtilities {

	default String title() {
		return object() == null ? null : object().getClass().getSimpleName();
	}

	default Object titleIcon() {
		return null;
	}

	default Object object() {
		return this;
	}

	default boolean isCloseable() {
		return close(null);
	}

	default boolean close(ICloseable closeable) {
		if(closeable != null) {
			closeable.close();
		}
		return true;
	}

	default void resize() {
	}

	default void refresh() {
	}

	default void reset() {
	}

	default Component createContent() {
		return this instanceof Component ? (Component)this : null;
	}

	default Component createToolbar() {
		return null;
	}

	default Component createHeader() {
		return null;
	}

	default Component createFooter() {
		return null;
	}
}
