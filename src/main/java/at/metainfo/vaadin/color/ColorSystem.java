package at.metainfo.vaadin.color;

public enum ColorSystem {

	RGB(255,255,255,255),
	HSL(-360,100,100),
	HSV(-360,100,100);

	private final int[] scale;

	private ColorSystem() {
		this(0, 0, 0);
	}

	private ColorSystem(int... scale) {
		this.scale = scale; 
	}

	public int scale(int index) {
		return index < scale.length ? scale[index] : 0;
	}
}
