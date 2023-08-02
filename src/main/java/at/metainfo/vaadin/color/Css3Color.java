package at.metainfo.vaadin.color;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Css3Color {

	AliceBlue("#F0F8FF"),
	AntiqueWhite("#FAEBD7"),
	Aqua("#00FFFF"),
	Aquamarine("#7FFFD4"),
	Azure("#F0FFFF"),
	Beige("#F5F5DC"),
	Bisque("#FFE4C4"),
	Black("#000000"),
	BlanchedAlmond("#FFEBCD"),
	Blue("#0000FF"),
	BlueViolet("#8A2BE2"),
	Brown("#A52A2A"),
	BurlyWood("#DEB887"),
	CadetBlue("#5F9EA0"),
	Chartreuse("#7FFF00"),
	Chocolate("#D2691E"),
	Coral("#FF7F50"),
	CornflowerBlue("#6495ED"),
	Cornsilk("#FFF8DC"),
	Crimson("#DC143C"),
	Cyan("#00FFFF"),
	DarkBlue("#00008B"),
	DarkCyan("#008B8B"),
	DarkGoldenrod("#B8860B"),
	DarkGray("#A9A9A9"),
	DarkGreen("#006400"),
	DarkGrey("#A9A9A9"),
	DarkKhaki("#BDB76B"),
	DarkMagenta("#8B008B"),
	DarkOliveGreen("#556B2F"),
	DarkOrange("#FF8C00"),
	DarkOrchid("#9932CC"),
	DarkRed("#8B0000"),
	DarkSalmon("#E9967A"),
	DarkSeaGreen("#8FBC8F"),
	DarkSlateBlue("#483D8B"),
	DarkSlateGray("#2F4F4F"),
	DarkSlateGrey("#2F4F4F"),
	DarkTurquoise("#00CED1"),
	DarkViolet("#9400D3"),
	DeepPink("#FF1493"),
	DeepSkyBlue("#00BFFF"),
	DimGray("#696969"),
	DodgerBlue("#1E90FF"),
	FireBrick("#B22222"),
	FloralWhite("#FFFAF0"),
	ForestGreen("#228B22"),
	Fuchsia("#FF00FF"),
	Gainsboro("#DCDCDC"),
	GhostWhite("#F8F8FF"),
	Gold("#FFD700"),
	Goldenrod("#DAA520"),
	Gray("#808080"),
	Green("#008000"),
	GreenYellow("#ADFF2F"),
	Grey("#808080"),
	Honeydew("#F0FFF0"),
	HotPink("#FF69B4"),
	IndianRed("#CD5C5C"),
	Indigo("#4B0082"),
	Ivory("#FFFFF0"),
	Khaki("#F0E68C"),
	Lavender("#E6E6FA"),
	LavenderBlush("#FFF0F5"),
	LawnGreen("#7CFC00"),
	LemonChiffon("#FFFACD"),
	LightBlue("#ADD8E6"),
	LightCoral("#F08080"),
	LightCyan("#E0FFFF"),
	LightGoldenrodYellow("#FAFAD2"),
	LightGray("#D3D3D3"),
	LightGreen("#90EE90"),
	LightGrey("#D3D3D3"),
	LightPink("#FFB6C1"),
	LightSalmon("#FFA07A"),
	LightSeaGreen("#20B2AA"),
	LightSkyBlue("#87CEFA"),
	LightSlateGray("#778899"),
	LightSlateGrey("#778899"),
	LightSteelBlue("#B0C4DE"),
	LightYellow("#FFFFE0"),
	Lime("#00FF00"),
	LimeGreen("#32CD32"),
	Linen("#FAF0E6"),
	Magenta("#FF00FF"),
	Maroon("#800000"),
	MediumAquamarine("#66CDAA"),
	MediumBlue("#0000CD"),
	MediumOrchid("#BA55D3"),
	MediumPurple("#9370DB"),
	MediumSeaGreen("#3CB371"),
	MediumSlateBlue("#7B68EE"),
	MediumSpringGreen("#00FA9A"),
	MediumTurquoise("#48D1CC"),
	MediumVioletRed("#C71585"),
	MidnightBlue("#191970"),
	MintCream("#F5FFFA"),
	MistyRose("#FFE4E1"),
	Moccasin("#FFE4B5"),
	NavajoWhite("#FFDEAD"),
	Navy("#000080"),
	OldLace("#FDF5E6"),
	Olive("#808000"),
	OliveDrab("#6B8E23"),
	Orange("#FFA500"),
	OrangeRed("#FF4500"),
	Orchid("#DA70D6"),
	PaleGoldenrod("#EEE8AA"),
	PaleGreen("#98FB98"),
	PaleTurquoise("#AFEEEE"),
	PaleVioletRed("#DB7093"),
	PapayaWhip("#FFEFD5"),
	PeachPuff("#FFDAB9"),
	Peru("#CD853F"),
	Pink("#FFC0CB"),
	Plum("#DDA0DD"),
	PowderBlue("#B0E0E6"),
	Purple("#800080"),
	Rebeccapurple("#663399"),
	Red("#FF0000"),
	RosyBrown("#BC8F8F"),
	RoyalBlue("#4169E1"),
	SaddleBrown("#8B4513"),
	Salmon("#FA8072"),
	SandyBrown("#F4A460"),
	SeaGreen("#2E8B57"),
	Seashell("#FFF5EE"),
	Sienna("#A0522D"),
	Silver("#C0C0C0"),
	SkyBlue("#87CEEB"),
	SlateBlue("#6A5ACD"),
	SlateGray("#708090"),
	SlateGrey("#708090"),
	Snow("#FFFAFA"),
	SpringGreen("#00FF7F"),
	SteelBlue("#4682B4"),
	Tan("#D2B48C"),
	Teal("#008080"),
	Thistle("#D8BFD8"),
	Tomato("#FF6347"),
	Turquoise("#40E0D0"),
	Violet("#EE82EE"),
	Wheat("#F5DEB3"),
	White("#FFFFFF"),
	WhiteSmoke("#F5F5F5"),
	Yellow("#FFFF00"),
	YellowGreen("#9ACD32"),
	;

	public static List<Color> colors() {
		return Arrays.stream(values()).map(c -> c.color()).collect(Collectors.toList());
	}

	public static List<Color> colors(Comparator<Color> comparator) {
		return Arrays.stream(values()).map(c -> c.color()).sorted(comparator).collect(Collectors.toList());
	}

	private final Color color;

	private Css3Color(String hexcode) {
		color = Color.fromHexCode(hexcode);
		color.setName(name());
	}

	public Color color() {
		return color;
	}

	public String hexCode() {
		return color.hexCode(false);
	}
	
	@Override
	public String toString() {
		return color.toString();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// Color Table from "https://www.cssportal.com/css3-color-names" copied to "Css3Color.txt" to get correct Literals for this enumeration
		InputStream in = Css3Color.class.getResourceAsStream(Css3Color.class.getSimpleName() + ".txt");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
			Map<String, String> codes = new LinkedHashMap<>();
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.isBlank()) {
					var parts = line.split("\t");
					codes.put(parts[0], parts[1]);
				}
			}
			for (var e : codes.entrySet()) {
				System.out.println("\t" + e.getKey() + "(\"" + e.getValue() + "\"),");
			}
		}
	}

	public Css3Color matchingTextColor() {
		// @see https://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color
		double l = 0.2126 * normalize(color.red()) + 0.7152 * normalize(color.green()) + 0.0722 * normalize(color.blue());
		Css3Color textColor = l > 0.179 ? Black : White;
		return textColor;
	}

	private double normalize(double c) {
		return c <= 0.04045 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
	}
}
