package at.metainfo.vaadin.view;

import java.util.function.Supplier;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;

public class EnhancedViewData {
	private IEnhancedView view;
	private String title;
	private Component titleIcon;
	private Object object;
	private Object[] data = new Object[4];

	public EnhancedViewData(IEnhancedView view) {
		this.view = view;
		initialize();
	}

	private void initialize() {
		title = view.title();
		titleIcon = icon(view.titleIcon());
		object = view.object();
		data[0] = (Supplier<Component>)() -> view.createContent();
		data[1] = (Supplier<Component>)() -> view.createToolbar();
		data[2] = (Supplier<Component>)() -> view.createHeader();
		data[3] = (Supplier<Component>)() -> view.createFooter();
	}

	private Component icon(Object titleIcon) {
		if(titleIcon instanceof Component) return (Component)titleIcon;
		if(titleIcon instanceof String) return new Image((String)titleIcon, "");
		return null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Component getTitleIcon() {
		return titleIcon;
	}

	public void setTitleIcon(Component titleIcon) {
		this.titleIcon = titleIcon;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public boolean close(ICloseable closeable) {
		return view.close(closeable);
	}

	public void resize() {
		view.resize();
	}

	public void refresh() {
		view.refresh();
	}

	public void reset() {
		initialize();
		view.reset();
	}

	private Component getComponent(int idx) {
		if(data[idx] instanceof Supplier) data[idx] = ((Supplier<?>)data[idx]).get();
		return (Component) data[idx];
	}

	public boolean contentCreated() {
		return data[0] instanceof Component;
	}

	public boolean toolbarCreated() {
		return data[1] instanceof Component;
	}

	public boolean headerCreated() {
		return data[2] instanceof Component;
	}

	public boolean footerCreated() {
		return data[3] instanceof Component;
	}

	public Component content() {
		return getComponent(0);
	}

	public Component toolbar() {
		return getComponent(1);
	}

	public Component header() {
		return getComponent(2);
	}

	public Component footer() {
		return getComponent(3);
	}

	public void setVisible(boolean visible) {
		for(var obj : data) {
			if(obj instanceof Component) ((Component)obj).setVisible(visible);
		}
	}
	
	public IEnhancedView getView() {
		return view;
	}
}
