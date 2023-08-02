package at.metainfo.vaadin.icons;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Span;

@NpmPackage(value="line-awesome", version="1.3.0")
@CssImport("line-awesome/dist/line-awesome/css/line-awesome.min.css")
public class Icons8 extends Span {
	private static final long serialVersionUID = -7686064203876105071L;

	private static final String FONT_SIZE = "font-size";
	private static final String COLOR = "color";

	public Icons8(String... names) {
		getElement().getClassList().add("las");
		for(var name : names) {
			getElement().getClassList().add("la-" + name);
		}
	}

	public Icons8 size(String size) {
		if(size == null) {
			getElement().getStyle().remove(FONT_SIZE);
		} else {
			getElement().getStyle().set(FONT_SIZE, size);
			getElement().getStyle().set(FONT_SIZE, size);
		}
		return this;
	}

	public Icons8 color(String color) {
		if(color == null) {
			getElement().getStyle().remove(COLOR);
		} else {
			getElement().getStyle().set(COLOR, color);
		}
		return this;
	}
}
