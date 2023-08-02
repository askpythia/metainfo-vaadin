package at.metainfo.vaadin.color;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import java.util.Comparator;

public class Color {
	
	private static double Ɛ = 0.0001;

	public static Color from(ColorSystem system, Number... values) {
		return new Color(system, values);
	}

	public static Color fromHexCode(String hexCode) {
		return new Color(hexCode);
	}

	private static boolean equals(double d1, double d2) {
		return abs(d1 - d2) < Ɛ;
	}

	private String name;
	private double red;
	private double green;
	private double blue;
	private double hue;
	private double saturation_v;
	private double value;
	private double saturation_l;
	private double luminance;
	private double transparency;
	private Color(ColorSystem system, Number... values) {
		int usedValues = initialize(system, values);
		this.transparency = usedValues < values.length ? number2double(values[usedValues], 0) : 0.0;
	}

	private Color(String hexcode) {
		if(hexcode.startsWith("#")) hexcode = hexcode.substring(1);
		if(hexcode.endsWith(";")) hexcode = hexcode.substring(0, hexcode.length()-1);
		int color = (int)Long.parseLong(hexcode, 16);
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color >> 0) & 0xFF;
		initialize(ColorSystem.RGB, r, g, b);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double transparency() {
		return transparency;
	}

	public double red() {
		return red;
	}

	public double green() {
		return green;
	}

	public double blue() {
		return blue;
	}

	public int red_scaled() {
		return (int)(red * ColorSystem.RGB.scale(0));
	}

	public int green_scaled() {
		return (int)(green * ColorSystem.RGB.scale(1));
	}

	public int blue_scaled() {
		return (int)(blue * ColorSystem.RGB.scale(2));
	}

	public int gray_scaled() {
		if(red_scaled() == green_scaled() && green_scaled() == blue_scaled()) {
			return red_scaled();
		}
		return -1;
	}

	public double hue() {
		return hue;
	}

	public double saturation_v() {
		return saturation_v;
	}

	public double value() {
		return value;
	}

	public double luminance() {
		return luminance;
	}

	public double saturation_l() {
		return saturation_l;
	}

	public String hexCode(boolean withTransparency) {
		StringBuilder out = new StringBuilder();
		out.append("#");
		out.append(toHex(red * ColorSystem.RGB.scale(0), 2));
		out.append(toHex(green * ColorSystem.RGB.scale(1), 2));
		out.append(toHex(blue * ColorSystem.RGB.scale(2), 2));
		if(withTransparency) out.append(toHex(transparency * ColorSystem.RGB.scale(3), 2));
		return out.toString();
	}

	public String info() {
		StringBuilder out = new StringBuilder();
		out.append("RGB red=");
		out.append(red);
		out.append(" green=");
		out.append(green);
		out.append(" blue=");
		out.append(blue);
		out.append(", HSV hue=");
		out.append(hue);
		out.append(" saturation=");
		out.append(saturation_v);
		out.append(" value=");
		out.append(value);
		out.append(", HSL hue=");
		out.append(hue);
		out.append(" saturation=");
		out.append(saturation_l);
		out.append(" luminance=");
		out.append(luminance);
		out.append(", transparency=");
		out.append(transparency);
		return out.toString();
	}

	@Override
	public String toString() {
		return name == null ? hexCode(false) : name;
	}

	private double number2double(Number number, int scale) {
		if(scale <= 1) scale = 1;
		return number.doubleValue() / scale;
	}

	private int initialize(ColorSystem system, Number... values) {
		switch(system) {
			case RGB:
				if(values.length < 3) throw new IllegalArgumentException("RGB needs at least 3 vaules: red, green and blue!");
				this.red = number2double(values[0], system.scale(0));
				this.green = number2double(values[1], system.scale(1));
				this.blue = number2double(values[2], system.scale(2));
				init_rgb();
				return 3;
			case HSV:
				if(values.length < 3) throw new IllegalArgumentException("RSV needs at least 3 vaules: hue, saturation and value!");
				this.hue = number2double(values[0], system.scale(0));
				this.saturation_v = number2double(values[1], system.scale(1));
				this.value = number2double(values[2], system.scale(2));
				init_hsv_hsl(true);
				return 3;
			case HSL:
				if(values.length < 3) throw new IllegalArgumentException("HLS needs at least 3 vaules: hue, luminance and saturation!");
				this.hue = number2double(values[0], system.scale(0));
				this.luminance = number2double(values[1], system.scale(1));
				this.saturation_l = number2double(values[2], system.scale(2));
				init_hsv_hsl(false);
				return 3;
		}
		return 0;
	}

	private void init_rgb() {
		var max = max(red, max(green, blue));
		var min = min(red, min(green, blue));
	
		// HUE
		if(equals(min, max)) {
			hue = 0.0;
		} else {
			if(equals(red, max)) {
				hue = 60.0 * (0.0 + (green - blue) / (max - min));
			} else if(equals(green, max)) {
				hue = 60.0 * (2.0 + (blue - red) / (max - min));
			} else if(equals(blue, max)) {
				hue = 60.0 * (4.0 + (red - green) / (max - min));
			}
			if(hue < 0) hue = hue + 360.0;
		}
		
		//HSV Saturation
		if(equals(max, 0.0)) {
			saturation_v = 0.0;
		} else {
			saturation_v = (max - min) / max;
		}
		
		//HSV Value
		value = max;
		
		//HLS Luminance
		luminance = (max + min) / 2.0;
		
		//HLS Saturation
		if(equals(max, 0.0) || equals(min, 1.0)) {
			saturation_l = 0.0;
		} else {
			saturation_l = (max - min) / (1 - abs(max + min - 1));
		}
	}

	private void init_hsv_hsl(boolean hsv) {
		var c = hsv ? value * saturation_v : ((1.0 - abs(luminance * 2.0 - 1.0)) * saturation_l);
		var x = c * (1 - abs((hue / 60.0) % 2 - 1));
		var m = hsv ? value - c : (luminance - c / 2.0);
		if(hsv) {
			this.luminance = value - value * saturation_v / 2.0;
			this.saturation_l = equals(luminance, 0) || equals(luminance, 1) ? 0.0 : ((value - luminance)/min(luminance, 1.0 - luminance));
		} else {
			this.value = luminance + saturation_l * min(luminance, 1.0 - luminance);
			this.saturation_v = equals(value, 0.0) ? 0.0 : 2.0 * (1.0 - (luminance / value));
		}
		if(hue < 60.0) {
			this.red = c;
			this.green = x;
			this.blue = 0.0;
		} else if(hue < 120.0) {
			this.red = x;
			this.green = c;
			this.blue = 0.0;
		} else if(hue < 180.0) {
			this.red = 0.0;
			this.green = c;
			this.blue = x;
		} else if(hue < 240.0) {
			this.red = 0.0;
			this.green = x;
			this.blue = c;
		} else if(hue < 300.0) {
			this.red = x;
			this.green = 0.0;
			this.blue = c;
		} else if(hue < 360.0) {
			this.red = c;
			this.green = 0.0;
			this.blue = x;
		}
		this.red += m;
		this.green += m;
		this.blue += m;
	}

	private String toHex(double d, int digits) {
		var result = Long.toString(round(d), 16);
		while(result.length() < digits) result = "0" + result;
		return result;
	}

	public static Comparator<Color> stepSorting(int steps) {
		return new Comparator<Color>() {
			@Override
			public int compare(Color c1, Color c2) {
				int g1 = c1.gray_scaled(); if(g1 >= 0) g1 = -256 + g1;
				int g2 = c2.gray_scaled(); if(g2 >= 0) g2 = -256 + g2;
				if(g1 == g2) {
					int h1 = (int)(c1.hue() / 360.0 * steps);
					int h2 = (int)(c2.hue() / 360.0 * steps);
					if(h1 == h2) {
						int l1 = (int)(Math.sqrt(0.241 * c1.red() + 0.691 * c1.green() + 0.068 * c1.blue()) * steps);
						int l2 = (int)(Math.sqrt(0.241 * c2.red() + 0.691 * c2.green() + 0.068 * c2.blue()) * steps);
						if(l1 == l2) {
							int v1 = (int)(c1.value() * steps);
							var v2 = (int)(c2.value() * steps);
							return v1 - v2;
						} else {
							return l1 - l2;
						}
					} else {
						return h1 - h2;
					}
				} else {
					return g1 - g2;
				}
			}
		};
	}

	public static Comparator<Color> hueSorting(int degrees, int steps) {
		return new Comparator<Color>() {
			@Override
			public int compare(Color c1, Color c2) {
				int g1 = c1.gray_scaled(); if(g1 >= 0) g1 = -256 + g1;
				int g2 = c2.gray_scaled(); if(g2 >= 0) g2 = -256 + g2;
				if(g1 == g2) {
					int h1 = (int)(c1.hue() / 360.0 * degrees);
					int h2 = (int)(c2.hue() / 360.0 * degrees);
					if(h1 == h2) {
						int l1 = (int)(Math.sqrt(0.241 * c1.red() + 0.691 * c1.green() + 0.068 * c1.blue()) * steps);
						int l2 = (int)(Math.sqrt(0.241 * c2.red() + 0.691 * c2.green() + 0.068 * c2.blue()) * steps);
						if(l1 == l2) {
							int v1 = (int)(c1.value() * steps);
							var v2 = (int)(c2.value() * steps);
							return v1 - v2;
						} else {
							return l1 - l2;
						}
					} else {
						return h1 - h2;
					}
				} else {
					return g1 - g2;
				}
			}
		};
	}
}
