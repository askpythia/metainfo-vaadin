package at.metainfo.vaadin.utilities;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.SynchronizedRequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")
public class NoticeClosed extends Div {

    public static class BeaconEvent extends ComponentEvent<UI> {
        public BeaconEvent(UI source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    public static class BeaconHandler extends SynchronizedRequestHandler {
        private final UI ui;
        private final String beaconPath = "/beacon/" + UUID.randomUUID().toString();

        public BeaconHandler(UI ui) {
            this.ui = ui;
        }

        @Override
        protected boolean canHandleRequest(VaadinRequest request) {
            return beaconPath.equals(request.getPathInfo());
        }

        @Override
        public boolean synchronizedHandleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) throws IOException {
            ComponentUtil.fireEvent(ui, new BeaconEvent(ui, true));
            return true;
        }

        public static Registration addBeaconListener(UI ui, ComponentEventListener<BeaconEvent> listener) {
            ensureInstalledForUi(ui);
            return ComponentUtil.addListener(ui, BeaconEvent.class, listener);
        }

        private static void ensureInstalledForUi(UI ui) {
            if (ComponentUtil.getData(ui, BeaconHandler.class) != null) {
                // Already installed, nothing to do
                return;
            }

            BeaconHandler beaconHandler = new BeaconHandler(ui);

            // ./beacon/<random uuid>
            String relativeBeaconPath = "." + beaconHandler.beaconPath;

            ui
                .getElement()
                .executeJs(
                    "window.addEventListener('unload', function() {navigator.sendBeacon && navigator.sendBeacon($0)})",
                    relativeBeaconPath
                );

            VaadinSession session = ui.getSession();
            session.addRequestHandler(beaconHandler);
            ui.addDetachListener(detachEvent -> session.removeRequestHandler(beaconHandler));

            ComponentUtil.setData(ui, BeaconHandler.class, beaconHandler);
        }
    }

	private Consumer<UI> onClose;

    public NoticeClosed(Consumer<UI> onClose) {
    	this.onClose = onClose;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();

        Registration beaconRegistration = BeaconHandler.addBeaconListener(
            ui,
            beaconEvent -> {
            	if(onClose != null) onClose.accept(beaconEvent.getSource());
            }
        );

        addDetachListener(
            detachEvent -> {
                detachEvent.unregisterListener();
                beaconRegistration.remove();
            }
        );
    }
}