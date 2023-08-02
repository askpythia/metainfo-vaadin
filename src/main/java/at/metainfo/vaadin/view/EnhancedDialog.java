package at.metainfo.vaadin.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;

import at.metainfo.vaadin.nls.NlsLabel;
import at.metainfo.vaadin.view.EnhancedTabs.Tab;

/**
 * @author askpythia
 * 
 * Extends the Vaadin 23 Dialog to display IEnhancedViews
 * Views originated on EnhancedTabs can be moved between Tabs and Dialog by double-click on the tab / header
 */
@CssImport(value = "./styles/enhanced-dialog-styles.css", themeFor = "vaadin-dialog-overlay")
public class EnhancedDialog extends Dialog implements IEnhancedViewContainer, HasIconProvider {
	private static final long serialVersionUID = 1634155364581269950L;

	private EnhancedViewData data;

	private Tab tab;
	private EnhancedTabs tabbedView;

	/**
	 * Creates a dialog using the IEnhancedView
	 * The Dialog header will be used for the title header: The icon, title (nls enabled), toolbar on the right (if defined) and close action (if view is closeable)
	 * If the view defines a header it will be in the second line of the dialog header (below the title header)
	 * If the view defines a footer it will be added to the dialog footer
	 * The  content of the view will be added to the dialog content
	 *    
	 * @param view
	 */
	public EnhancedDialog(IEnhancedView view) {
		initialize();
		addView(view);
	}

	/**
	 * Switches the content of the Dialog to an other EnhancedView
	 * The previously displayed view will be removed from the dialog (forced, calling the close method without a close handler)
	 */
	@Override
	public void addView(IEnhancedView view) {
		if(data != null) {
			if(data.getView() == view) {
				return;
			} else {
				data.getView().close(null);
				removeView();
			}
		}
		data = new EnhancedViewData(view);
		addViewInternal();
	}

	/**
	 * Build an EnhancedDialog from given Tab of an EnhancedTabs
	 * The Tab will be removed from the EnhancedTabs and is moved to a new EnhancedDialog
	 * The EnhancedDialog remembers the EnhancedTabs and Tab to optionally move it back on request
	 * EnhancedViewData encapsulates the IEnhancedView to provide lazy initialization, caching and visibility-control of all Components
	 * 
	 * @param tab
	 */
	protected EnhancedDialog(Tab tab) {
		initialize();
		this.tab = tab;
		this.data = tab.data();
		this.tabbedView = tab.removeFromEnhancedTabs();
		data.setVisible(true);
		addViewInternal();
		setSizeFull();
	}

	/**
	 * The default for an EnhancedDialog is draggable, resizeable and closeable using Escape
	 * These options can be changed before opening the dialog
	 * To display the Dialog Title nicely we use an additional theme "meta", defined in enhanced-dialog-styles.css (see @CssImport)
	 */
	private void initialize() {
		setModal(false);
		addThemeName("meta");
		setDraggable(true);
		setResizable(true);
		setCloseOnOutsideClick(false);
		addResizeListener(event -> data.resize());
	}

	/**
	 * Add the IEnhancedView to the different parts of the dialog.
	 * The title header gets the class draggable so the dialog can be dragged using the title header
	 */
	private void addViewInternal() {
		add(data.content());
		Component titleIcon = data.getTitleIcon();
		if(titleIcon == null) titleIcon = VaadinIcon.MODAL.create();
		HorizontalLayout title = hl(titleIcon, new NlsLabel(data.getTitle()));
		title.setAlignItems(Alignment.CENTER);
		title.addClassNames("draggable");
		Component toolbar = data.toolbar();
		if(toolbar != null) {
			toolbar.getElement().getStyle().set("margin-left", "auto");
			title.add(toolbar);
		}
		if(data.getView().isCloseable()) {
			// If view is closeable don't react on forced close by vaadin dialog
			setCloseOnEsc(false);
			setCloseOnOutsideClick(false);
			// Add close shortcut using close-handler
			Shortcuts.addShortcutListener(this, () -> {
				data.close(() -> close());
			}, Key.ESCAPE).listenOn(this);
			// Add close icon using close-handler
			Component close = getIcon(ViewIcon.tabCloseIcon);
			Element closeElement = close.getElement();
			closeElement.addEventListener("click", click -> {
				data.close(() -> close());
			});
			if(toolbar == null) {
				closeElement.getStyle().set("margin-left", "auto");
			}
			title.add(close);
		} else {
			// If view is not closeable the dialog can be (forced) closed at least by escape
			setCloseOnEsc(true);
		}
		// Build title and add all view components to the dialog regions
		VerticalLayout header = vl(title);
		Component viewHeader = data.header();
		if(viewHeader != null) header.add(header);
		getHeader().add(header);
		Component footer = data.footer();
		if(footer != null) getFooter().add(footer);
		// When view was moved from an EnhancedTabs to the dialog add a double click listener on the title to move it back
		if(tabbedView != null) {
			title.getElement().addEventListener("dblclick", event -> moveBackToTabs());
		}
		// Stop the double click when over the toolbar
		if(toolbar != null) {
			toolbar.getElement().addEventListener("dblclick", event -> {}).addEventData("event.stopPropagation()");
		}
	}

	/**
	 * Removes all EnhancedView components from the dialog
	 */
	private void removeView() {
		removeAll();
		getHeader().removeAll();
		getFooter().removeAll();
	}

	/**
	 * Moves the EnhancedView back to the origin EnhancedTabs and closes the dialog
	 */
	private void moveBackToTabs() {
		if(tabbedView != null) {
			removeView();
			tabbedView.addForeignTab(tab, data);
			close();
		}
	}
}
