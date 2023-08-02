package at.metainfo.vaadin.view;

import java.util.function.Function;
import java.util.function.Supplier;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;

import at.metainfo.vaadin.utilities.IGuiUtilities;

public interface HasIconProvider {

	public static enum ViewIcon {
		tabCloseIcon(() -> {
			//Icon icon = VaadinIcon.CLOSE.create();
			Icon icon = new Icon("lumo", "cross");
			icon.setColor(IGuiUtilities.DEFAULT_ICON_COLOR);
			icon.getStyle().set("cursor", "pointer");
			return icon;
		}),
		tabsMinimizeIcon(() -> {
			//return new Icons8("minus-square").size("24px").color("#990000");
			Icon icon = new Icon("vaadin", "minus-circle-o");
			icon.setSize(IGuiUtilities.DEFAULT_ICON_SIZE);
			icon.setColor(IGuiUtilities.DEFAULT_ICON_COLOR);
			icon.getStyle().set("cursor", "pointer");
			return icon;
		}),
		tabsMaximizeIcon(() -> {
			//return new Icons8("plus-square").size("24px").color("#990000");
			Icon icon = new Icon("vaadin", "plus-circle-o");
			icon.setSize(IGuiUtilities.DEFAULT_ICON_SIZE);
			icon.setColor(IGuiUtilities.DEFAULT_ICON_COLOR);
			icon.getStyle().set("cursor", "pointer");
			return icon;
		}),
		tabsCloseIcon(() -> {
			//return new Icons8("plus-square").size("24px").color("#990000");
			Icon icon = new Icon("vaadin", "close-circle-o");
			icon.setSize(IGuiUtilities.DEFAULT_ICON_SIZE);
			icon.setColor(IGuiUtilities.DEFAULT_ICON_COLOR);
			icon.getStyle().set("cursor", "pointer");
			return icon;
		}),
		;

		private final Supplier<Object> default_;

		private ViewIcon(Supplier<Object> default_) {
			this.default_ = default_;
		}

		public Component getIconComponent() {
			Object object = default_.get();
			if(object instanceof String) {
				return new Image((String)object, name());
			} else if(object instanceof IconFactory) {
				return ((IconFactory)object).create();
			} else if(object instanceof Component){
				return (Component)object;
			} else {
				return VaadinIcon.QUESTION.create();
			}
		}
	}

	default Function<ViewIcon, Component> iconProvider() {
		return null;
	}

	default Component getIcon(ViewIcon key) {
		if(iconProvider() == null) return key.getIconComponent();
		Component icon = iconProvider().apply(key);
		return icon == null ? key.getIconComponent() : icon;
	}
}
