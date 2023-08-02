package at.metainfo.vaadin.icons;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Span;

@NpmPackage(value="eva-icons", version="1.1.3")
@CssImport("eva-icons/style/eva-icons.css")
public class EvaIcon extends Span {
	private static final long serialVersionUID = -7686064203876105072L;

	private static final String FONT_SIZE = "font-size";
	private static final String COLOR = "color";

	private static final String DEFAULT_SIZE = null;
	private static final String DEFAULT_COLOR = null;

	public EvaIcon(String name) {
		this(name, DEFAULT_SIZE, DEFAULT_COLOR);		
	}

	public EvaIcon(String name, String size, String color) {
		getElement().getClassList().add("eva");
		getElement().getClassList().add("eva-" + name);
		size(size);
		color(color);
	}
	// @UseExplicitStyle
	public EvaIcon size(String size) {
		if(size == null) {
			getElement().getStyle().remove(FONT_SIZE);
		} else {
			getElement().getStyle().set(FONT_SIZE, size);
			getElement().getStyle().set(FONT_SIZE, size);
		}
		return this;
	}
	// @UseExplicitStyle
	public EvaIcon color(String color) {
		if(color == null) {
			getElement().getStyle().remove(COLOR);
		} else {
			getElement().getStyle().set(COLOR, color);
		}
		return this;
	}
}
