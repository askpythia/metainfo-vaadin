package at.metainfo.vaadin.utilities;

import java.util.function.Consumer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;

@SuppressWarnings("serial")
public class ToggleButton extends Button implements LocaleChangeObserver {

	private String label;
	private Component component;
	private boolean value;

	public ToggleButton() {
		this("", true, null);
	}

	public ToggleButton(String label) {
		this(label, true, null);
	}

	public ToggleButton(String label, boolean value) {
		this(label, value, null);
	}

	public ToggleButton(Component component, boolean value, Consumer<Boolean> setValueListener) {
		super(component);
		this.component = component;
		this.value = value;
		updateTheme();
		addClickListener(e-> {
			setValue(!getValue());
			if(setValueListener != null) setValueListener.accept(getValue());
		});
	}

	public ToggleButton(String label, boolean value, Consumer<Boolean> setValueListener) {
		this.label = label;
		this.value = value;
		updateLabel();
		updateTheme();
		addClickListener(e-> {
			setValue(!getValue());
			if(setValueListener != null) setValueListener.accept(getValue());
		});
	}

	private void updateLabel() {
		if(label == null) return;
		String translation = getTranslation(label);
		translation = translation.startsWith("!") ? label : translation;
		setText(translation);
	}

	private void updateTheme() {
		getThemeNames().clear();
		if(value) {
			addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
			if(component instanceof Icon icon) {
				addThemeVariants(ButtonVariant.LUMO_ICON);
				icon.setColor("#FFFFFF");
			}
		} else {
			addThemeVariants(/*ButtonVariant.LUMO_TERTIARY,*/ ButtonVariant.LUMO_SMALL);
			if(component instanceof Icon icon) {
				addThemeVariants(ButtonVariant.LUMO_ICON);
				icon.setColor("#990000");
			}
		}
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
		updateTheme();
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		updateLabel();
	}
}
