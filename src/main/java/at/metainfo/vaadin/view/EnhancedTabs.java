package at.metainfo.vaadin.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.shared.Registration;

import at.metainfo.vaadin.nls.NlsLabel;
import at.metainfo.vaadin.utilities.IGuiUtilities;

public class EnhancedTabs extends VerticalLayout implements IEnhancedViewContainer, IGuiUtilities, HasIconProvider {
	private static final long serialVersionUID = 2733605172349911727L;

	private Tabs tabs;
	private DropTarget<Tabs> tabsDropTarget;
	private Div contents;
	private HorizontalLayout toolbars;
	private Div headers;
	private Div footers;
	private HorizontalLayout toolbar1;
	private HorizontalLayout toolbar2;
	private HorizontalLayout topBar;
	
	private EnhancedViewData activeData;
	private Map<Tab, EnhancedViewData> tabData = new LinkedHashMap<>();
	private Map<Tab, Registration> tabToRegistration = new HashMap<>();
	
	private List<Consumer<SelectedChangeEvent>> tabSelectionListener = new ArrayList<>();
	private List<Consumer<Boolean>> tabsOpenCloseListener = new ArrayList<>();
	private boolean draggable = false;
	private boolean dropTarget = false;
	private Component minimize_handle = null;
	private Component maximize_handle = null;
	private Function<ViewIcon, Component> iconProvider;

	@SuppressWarnings("serial")
	public static class Tab extends com.vaadin.flow.component.tabs.Tab implements DragSource<Tab>, DropTarget<Tab> {

		private String labelOrId;

		public Tab(String labelOrId, Component... components) {
			super(components);
			this.labelOrId = labelOrId;
			if(components.length == 0) {
				setLabel(labelOrId);
			}
		}

		public String getLabelOrId() {
			return labelOrId;
		}

		public EnhancedViewData data() {
			return enhancedTabs(this).getTabData(this);
		}

		public void close() {
			enhancedTabs(this).closeTabDelayed(this);
		}

		protected EnhancedTabs removeFromEnhancedTabs() {
			EnhancedTabs enhancedTabs = enhancedTabs(this);
			enhancedTabs.removeTab(this);
			return enhancedTabs;
		}

		@Override
		public String toString() {
			return "TAB " + labelOrId;
		}
	}

	@SuppressWarnings("serial")
	public static class SelectedChangeEvent extends Tabs.SelectedChangeEvent {

		public SelectedChangeEvent(Tabs.SelectedChangeEvent event) {
			super(event.getSource(), event.getPreviousTab(), event.isFromClient());
		}

		@Override
		public Tab getPreviousTab() {
			Tab previousTab = (Tab)super.getPreviousTab();
			if(previousTab != null) {
				Tabs tabs = previousTab.findAncestor(getSource().getClass());
				if(tabs == null) previousTab = null;
			}
			return previousTab;
		}

		@Override
		public Tab getSelectedTab() {
			return (Tab)super.getSelectedTab();
		}
	}

	private static EnhancedTabs enhancedTabs(Tab tab) {
		Component par = tab.getParent().orElse(null);
		while (par != null && !(par instanceof EnhancedTabs)) {
				par = par.getParent().orElse(null);
		}
		return (EnhancedTabs)par;
	}

	protected void removeTab(Tab tab) {
		closeTabInternal(tab, tab.data());
	}

	public EnhancedTabs() {
		this(null);
	}

	public EnhancedTabs(Function<ViewIcon, Component> iconProvider) {
		this.iconProvider = iconProvider == null ? key -> key.getIconComponent() : iconProvider;
		setSizeFull();
		setPadding(false);
		initializeTabs();
		initialize();
	}

	@SuppressWarnings("serial")
	private void initializeTabs() {
		tabs = new Tabs() {
			@Override
			public void setSelectedTab(com.vaadin.flow.component.tabs.Tab selectedTab) {
				try {
					super.setSelectedTab(selectedTab);
				} catch(IllegalArgumentException ex) {
					// Could be temporary on a dialog ...
				}
			}
			@Override
			public void remove(Component... components) {
				try {
					super.remove(components);
				} catch(IllegalArgumentException ex) {
					// Could be temporary on a dialog ...
				}
			}
		};
		tabs.setWidthFull();
		tabs.getStyle().set("overflow", "auto");
		tabs.addSelectedChangeListener(event -> selectedChanged(new SelectedChangeEvent(event)));
		toolbars = toolbar();
		addToolbarStyle(toolbars);
		toolbar1 = toolbar();
		addToolbarStyle(toolbar1);
		toolbar2 = toolbar();
		addToolbarStyle(toolbar2);

		DomEventListener listener = event -> switchMinimized();
		minimize_handle = getIcon(ViewIcon.tabsMinimizeIcon);
		minimize_handle.getElement().addEventListener("click", listener); 
		maximize_handle = getIcon(ViewIcon.tabsMaximizeIcon);
		maximize_handle.getElement().addEventListener("click", listener);

		topBar = new HorizontalLayout(tabs, toolbar1, toolbars, toolbar2);
		topBar.setMargin(false);
		topBar.setPadding(false);
		topBar.setWidthFull();
		contents = new Div();
		contents.setSizeFull();
		initializeDropTarget();
		add(topBar, contents);
	}

	private void selectedChanged(SelectedChangeEvent event) {
		Tab selectedTab = event.getSelectedTab();

		if(activeData != null) {
			activeData.setVisible(false);
		}

		EnhancedViewData selectedData = getTabData(selectedTab);
		if(selectedData != null) {
			addData(selectedData);
			selectedData.setVisible(true);
			activeData = selectedData;
			tabSelectionListener.forEach(listener -> listener.accept(event));
		}
	}

	private void addToolbarStyle(Component component) {
		//component.getElement().getStyle().set("margin-left", "0");
		component.getElement().getStyle().set("box-shadow", "inset 0 -1px 0 0 var(--lumo-contrast-10pct)");
	}

	private void initializeDropTarget() {
		tabsDropTarget = DropTarget.configure(tabs);
		tabsDropTarget.setDropEffect(DropEffect.MOVE);
		tabsDropTarget.addDropListener(event -> {
			if(event.getDragSourceComponent().isPresent()) {
				var data = event.getDragSourceComponent().get();
				if(data instanceof Tab) {
					moveTab((Tab)data, null);
				}
			}
		});
	}

	protected void initialize() {
		// Override to add initializations if needed
	}

	public boolean isDraggable() {
		return draggable;
	}

	public void setDraggable(boolean draggable) {
		if(this.draggable != draggable) {
			this.draggable = draggable;
			getAllTabs().forEach(t -> t.setDraggable(draggable));
		}
	}

	public boolean isDropTarget() {
		return dropTarget;
	}

	public void setGlobalTarget(boolean globalTarget) {
		if(this.dropTarget != globalTarget) {
			this.dropTarget = globalTarget;
			tabsDropTarget.setActive(globalTarget);
			getAllTabs().forEach(t -> t.setActive(globalTarget));
		}
	}

	public boolean isMinimizeable() {
		return toolbar2.getChildren().anyMatch(c -> c == minimize_handle || c == maximize_handle);
	}

	public void setMinimizeable(boolean minimizeable) {
		if(minimizeable && !isMinimizeable()) {
			toolbar2.add(minimize_handle);
		} else {
			toolbar2.remove(minimize_handle);
			toolbar2.remove(maximize_handle);
		}
	}

	public void switchMinimizeable() {
		setMinimizeable(!isMinimizeable());
	}

	public boolean isMinimized() {
		return isMinimizeable() && toolbar2.getChildren().anyMatch(c -> c == maximize_handle);
	}

	public void setMinimized(boolean minimize) {
		if(isMinimizeable()) {
			if(minimize) {
				toolbar2.remove(minimize_handle);
				toolbar2.add(maximize_handle);
			} else {
				toolbar2.remove(maximize_handle);
				toolbar2.add(minimize_handle);
			}
			contents.setVisible(!minimize);
			if(headers != null) headers.setVisible(!minimize);
			if(footers != null) footers.setVisible(!minimize);
			tabsOpenCloseListener.forEach(listener -> listener.accept(!minimize));
		}
	}

	public void switchMinimized() {
		setMinimized(!isMinimized());
	}

	public Set<Tab> getAllTabs() {
		return tabData.keySet();
	}

	public Tab getActiveTab() {
		return (Tab)tabs.getSelectedTab();
	}

	/**
	 * Select given Tab (if object is Tab) or Tab with given Id (when object is String) or Tab associated to given Object
	 * @param object
	 */
	public Tab selectTab(Object object) {
		Tab tab = getTab(object);
		if (tab != null) tabs.setSelectedTab(tab);
		return tab;
	}

	/**
	 * Select given Tab (if object is Tab) or Tab with given Id (when object is String) or Tab associated to given Object
	 * @param object
	 */
	public void closeTab(Object object) {
		Tab tab = getTab(object);
		if (tab != null) closeTabDelayed(tab);
	}

	public Registration addSelectedChangeListener(Consumer<SelectedChangeEvent> listener) {
		tabSelectionListener.add(listener);
		return Registration.once(() -> tabSelectionListener.remove(listener));
	}

	public Registration addTabsOpenCloseListener(Consumer<Boolean> listener) {
		tabsOpenCloseListener.add(listener);
		return Registration.once(() -> tabsOpenCloseListener.remove(listener));
	}

	public Tab addViewTab(IEnhancedView enhancedView, Component... tabComponents) {
		EnhancedViewData data = new EnhancedViewData(enhancedView);
		Tab tab = createTab(data.getTitle(), enhancedView.isCloseable(), tabComponents);
		Component titleIcon = data.getTitleIcon();
		if(titleIcon != null) {
			titleIcon.getElement().getStyle().set("margin-right", "0.2rem");
			tab.getElement().insertChild(0, titleIcon.getElement());
		}
		tabData.put(tab, data);
		tabs.add(tab);
		return tab;
	}

	public Tab addTab(String caption, Object object, Function<ICloseable, Boolean> onClose, Component content, Component... tabComponents) {
		if(content instanceof IEnhancedView) {
			return addViewTab((IEnhancedView)content, tabComponents);
		} else {
			return addTab(caption, object, onClose, () -> content, tabComponents);
		}
	}

	public Tab addTab(String caption, Object object, Function<ICloseable, Boolean> onClose, Supplier<Component> contentSupplier, Component... tabComponents) {
		return addViewTab(new IEnhancedView() {
			@Override
			public String title() {
				return caption;
			}
			@Override
			public Object object() {
				return object;
			}
			@Override
			public boolean close(ICloseable closeable) {
				return onClose.apply(closeable);
			}
			@Override
			public Component createContent() {
				return contentSupplier.get();
			}
		}, tabComponents);
	}

	public void reorderTabs(List<Tab> newOrder, List<Tab> fixOrder) {
		List<Tab> oldOrder = new ArrayList<>(tabData.keySet());
		newOrder.removeAll(fixOrder);
		fixOrder.addAll(newOrder);
		if(!oldOrder.equals(fixOrder)) {
			Tab current = (Tab)tabs.getSelectedTab();
			// Remove all tabs which should be ordered from tabs
			for(Tab t : fixOrder) tabs.remove(t);
			// Prepare new TabData map
			Map<Tab, EnhancedViewData> newTabData = new LinkedHashMap<>();
			// Add all tabs in correct order
			for(Tab t : fixOrder) {
				tabs.add(t);
				newTabData.put(t, getTabData(t));
			}
			tabData = newTabData;
			tabs.setSelectedTab(current);
		}
	}

	public void resize() {
		tabData.values().forEach(vd -> vd.resize());
	}

	public void reset() {
		tabData.values().forEach(vd -> {
			removeData(vd);
			vd.reset();
			if(vd == activeData) addData(vd);
		});
	}

	private Tab createTab(String caption, boolean closeable, Component... tabComponents) {
		boolean hasComponents = tabComponents != null && tabComponents.length > 0;
		final Tab tab = hasComponents
			? new Tab(caption, tabComponents)
			: new Tab(caption, new NlsLabel(caption));
		if(closeable) {
			Component close = getIcon(ViewIcon.tabCloseIcon);
			close.getElement().addEventListener("click", click -> {
				tab.close();
			});
			tab.add(close);
		}
		tab.setEffectAllowed(EffectAllowed.MOVE);
		tab.setDraggable(isDraggable());
		tab.addDragStartListener(event -> {
			EnhancedTabs enhancedTabs = enhancedTabs(tab);
			if(enhancedTabs != null) enhancedTabs.dragStart(tab);
		});
		tab.addDragEndListener(event -> {
			EnhancedTabs enhancedTabs = enhancedTabs(tab);
			if(enhancedTabs != null) enhancedTabs.dragEnd(tab);
		});
		tab.getElement().addEventListener("dblclick", event -> moveToDialog(tab));
		tabToRegistration.put(tab, addDropTarget(tab));
		return tab;
	}

	private void moveToDialog(Tab tab) {
		EnhancedDialog dialog = new EnhancedDialog(tab);
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);
		dialog.open();
	}

	private void moveTab(Tab tab, Tab destination) {
		if(tab == destination) return;
		EnhancedTabs sourceView = enhancedTabs(tab);
		if(sourceView != null) {
			if(!isMoveAllowed(sourceView, tab)) return;
			if(sourceView != this) {
				var data = tab.data();
				tabData.put(tab, data);
				sourceView.closeTabInternal(tab, data);
				addData(data);
				tabs.add(tab);
				tabToRegistration.put(tab, addDropTarget(tab));
			}
			if(destination != null || sourceView != this) {
				//List<String> idList = new ArrayList<>();
				List<Tab> tabNew = new ArrayList<>();
				List<Tab> tabList = tabs.getChildren().filter(c -> c instanceof Tab).map(c -> (Tab)c).collect(Collectors.toList());
				boolean moveRight = destination != null && tabList.indexOf(destination) > tabList.indexOf(tab);
				for(var t : tabList) {
					if(moveRight) {
						if(t != tab) {
							//idList.add(t.getLabelOrId());
							tabNew.add(t);
						}
						if(t == destination) {
							//idList.add(tab.getLabelOrId());
							tabNew.add(tab);
						}
					} else {
						if(t == destination) {
							//idList.add(tab.getLabelOrId());
							tabNew.add(tab);
						}
						if(t != tab) {
							//idList.add(t.getLabelOrId());
							tabNew.add(t);
						}
					}
				}
				if(destination == null) {
					//idList.add(tab.getLabelOrId());
					tabNew.add(tab);
				}
				reorderTabs(tabNew, new ArrayList<>());
			}
			tabs.setSelectedTab(tab);
		};
	}

	protected void addForeignTab(Tab tab, EnhancedViewData data) {
		tabData.put(tab, data);
		addData(data);
		Component titleIcon = data.getTitleIcon();
		if(titleIcon != null) {
			titleIcon.getElement().getStyle().set("margin-right", "0.2rem");
			tab.getElement().insertChild(0, titleIcon.getElement());
		}
		tabs.add(tab);
		tabToRegistration.put(tab, addDropTarget(tab));
		tabs.setSelectedTab(tab);
	}

	private boolean isMoveAllowed(EnhancedTabs sourceView, Tab tab) {
		if(isChildOf(this, tab.data().content())) {
			Notification.show("Can't drag tab to own content!", 2000, Position.MIDDLE);
			return false;
		}
		return sourceView == this || isDropTarget();
	}

	private boolean isChildOf(Component child, Component parent) {
		if(child == parent) return true;
		Optional<Component> optionalParent = child.getParent();
		return optionalParent.isPresent() ? isChildOf(optionalParent.get(), parent) : false;
	}

	private void closeTabDelayed(final Tab tab) {
		var data = tabData.get(tab);
		if(data == null) {
			closeTabInternal(tab, null);
		} else {
			data.close(() -> closeTabInternal(tab, data));
		}
	}

	private void closeTabInternal(final Tab tab, EnhancedViewData data) {
		boolean current = data == activeData;
		Tab next = null;
		if(current) {
			int idx = tabs.indexOf(tab);
			int count = tabs.getComponentCount();
			if(count == 1) {
				// no next
			} else if(idx == count-1) {
				next = (Tab)tabs.getComponentAt(idx-1);
			} else {
				next = (Tab)tabs.getComponentAt(idx+1);
			}
		} else {
			next = tabData.entrySet().stream().filter(e -> e.getValue() == activeData).findFirst().get().getKey();
		}
		// Remove
		tabData.remove(tab);
		removeData(data);
		var dropTarget = tabToRegistration.remove(tab);
		if(dropTarget != null) dropTarget.remove();
		tabs.remove(tab);
		// Select next one
		if(next != null) {
			if(activeData != null) activeData.setVisible(false);
			tabs.setSelectedTab(next);
			activeData = tabData.get(next);
			activeData.setVisible(true);
		}
		if(getAllTabs().isEmpty()) setGlobalTarget(true);
	}

	private void dragStart(Tab draggedTab) {
		if(!isDropTarget()) {
			for(var tab : getAllTabs()) {
				tab.setActive(true);
			}
			if(tabsDropTarget != null) tabsDropTarget.setActive(true);
		}
		draggedTab.setActive(false);
	}

	private void dragEnd(Tab draggedTab) {
		if(!isDropTarget()) {
			for(var tab : getAllTabs()) {
				tab.setActive(false);
			}
			if(tabsDropTarget != null) tabsDropTarget.setActive(false);
		}
		draggedTab.setActive(isDropTarget());
	}

	private Registration addDropTarget(final Tab tab) {
		tab.setDropEffect(DropEffect.MOVE);
		return tab.addDropListener(event -> {
			if(event.getDragSourceComponent().isPresent()) {
				var data = event.getDragSourceComponent().get();
				if(data instanceof Tab) {
					moveTab((Tab)data, tab);
				}
			}
		});
	}

	private Map<String, Tab> getTabIdMap() {
		LinkedHashMap<String, Tab> map = new LinkedHashMap<>();
		for(Tab tab : tabData.keySet()) map.put(tab.getLabelOrId(), tab);
		return map;
	}

	private void addData(EnhancedViewData data) {
		if(data != null) {
			addComponent(contents, () -> data.content());
			addComponent(toolbars, () -> data.toolbar());
			addComponent(headers, () -> data.header());
			addComponent(footers, () -> data.footer());
		}
	}

	private void removeData(EnhancedViewData data) {
		if(data != null) {
			if(data.contentCreated()) removeComponent(contents, () -> data.content());
			if(data.toolbarCreated()) removeComponent(toolbars, () -> data.toolbar());
			if(data.headerCreated()) removeComponent(headers, () -> data.header());
			if(data.footerCreated()) removeComponent(footers, () -> data.footer());
		}
	}

	private void addComponent(HasComponents parent, Supplier<Component> componentSupplier) {
		if(parent != null) {
			Component component = componentSupplier.get();
			if(component != null && component.getParent().filter(parent::equals).isEmpty()) {
				parent.add(component);
			}
		}
	}

	private void removeComponent(HasComponents parent, Supplier<Component> componentSupplier) {
		if(parent != null) {
			Component component = componentSupplier.get();
			if(component != null) {
				parent.remove(component);
			}
		}
	}

	protected Map<Tab, EnhancedViewData> getTabData() {
		return tabData;
	}

	protected EnhancedViewData getTabData(Tab tab) {
		return tabData.get(tab);
	}

	/**
	 * Get given Tab (if object is Tab) or Tab with given Id (when object is String) or Tab associated to given Object
	 * @param caption
	 * @return
	 */
	protected Tab getTab(Object object) {
		if(object instanceof Tab) return (Tab)object;
		if(object instanceof String) {
			var labelOrId = (String)object;
			for(Tab tab : tabData.keySet()) {
				if(labelOrId.equals(tab.getLabelOrId())) return tab;
			}
		} else {
			for(Entry<Tab, EnhancedViewData> entry: tabData.entrySet()) {
				if(object == entry.getValue().getObject()) return entry.getKey();
			}
			if(object instanceof Class) {
				Class<?> clazz = (Class<?>)object;
				for(Entry<Tab, EnhancedViewData> entry: tabData.entrySet()) {
					if(clazz.isInstance(entry.getValue().getView())) return entry.getKey();
				}
			}
		}
		return null;
	}

	public Tabs getInternalTabs() {
		return tabs;
	}

	public HorizontalLayout getInternalTopBar() {
		return topBar;
	}

	public void addToLeftToolbar(Component... components) {
		for(Component component : components) toolbar1.add(component);
	}

	public void addToToolbar(Component... components) {
		for(Component component : components) toolbar2.add(component);
	}

	public void removeFromLeftToolbar(Component... components) {
		for(Component component : components) toolbar1.remove(component);
	}

	public void removeFromToolbar(Component... components) {
		for(Component component : components) toolbar2.remove(component);
	}

	@Override
	public Function<ViewIcon, Component> iconProvider() {
		return iconProvider;
	}

	@Override
	public void addView(IEnhancedView view) {
		addViewTab(view);
	}
}
