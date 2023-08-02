package at.metainfo.vaadin.view;

public abstract class CloseableView implements IEnhancedView {

	private boolean isCloseable;
	
	public CloseableView(boolean isCloseable) {
		this.isCloseable = isCloseable;
	}
	
	@Override
	public boolean close(ICloseable closeable) {
		IEnhancedView.super.close(closeable);
		return isCloseable;
	}
}
