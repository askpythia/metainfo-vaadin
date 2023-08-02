package at.metainfo.vaadin.nls;

import java.util.function.Supplier;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;

public class NlsLabel extends Span implements LocaleChangeObserver {
	private static final long serialVersionUID = -5673524262701135261L;

	private Supplier<String> textSupplier;
	private Object[] params;

	public NlsLabel(String text, Object... params) {
		this(() -> text, params);
		getStyle().set("white-space", "nowrap");
	}

	public NlsLabel(Supplier<String> textSupplier, Object... params) {
		this.textSupplier = textSupplier;
		this.params = params;
		localeChange(null);
	}

	@Override
	public String getText() {
		String text = textSupplier.get();
		String translation = getTranslation(text, params);
		return text.equals(translation) || translation.startsWith("!") ? text : translation;
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		super.setText(getText());
	}
}
