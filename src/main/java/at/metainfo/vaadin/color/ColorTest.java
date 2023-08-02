package at.metainfo.vaadin.color;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

public class ColorTest {

	private static final int STEP = 32;

	public static void main(String[] args) {
		new ColorTest().run();
	}

	private void run() {
		URL url = getClass().getResource(getClass().getSimpleName() + ".html");
		String file = url.getFile().replace("/target/classes/", "/src/main/java/");
		//String file = url.getFile().replace("/bin/", "/src/");
		try(var out = new FileWriter(new File(file), Charset.forName("UTF-8"))) {
			run(out);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void run(Writer out) throws IOException {
//		out.append(header());

		List<Color> rgbColors = Colors.rgbColors(STEP);
		out.append(colors("Generated RGB", rgbColors));

		rgbColors.sort(Color.hueSorting(30, 8));
		out.append(colors("Sorted RGB", rgbColors));

		List<Color> hueColors = Colors.grayColors(8);
		hueColors.addAll(Colors.hueColors(15, 5));
		out.append(colors("Generated HUE", hueColors));
		
		hueColors.sort(Color.hueSorting(30, 8));
		out.append(colors("Sorted HUE", hueColors));

		List<Color> css3Colors = Css3Color.colors();
		out.append(colors("Alphabetical CSS3", css3Colors));
		
		css3Colors.sort(Color.hueSorting(30, 8));
		out.append(colors("Sorted CSS3", css3Colors));

//		out.append(footer());
	}

//	private String header() {
//			return """
//	<html>
//		<head>
//			<style>
//				.root {
//					display: flex;
//				}
//				.root div {
//					height: 100px;
//					width: 100%;
//				}
//			</style>
//		</head>
//		<body>
//	""";
//		}

	private String colors(String name, Collection<Color> colors) {
		StringBuilder out = new StringBuilder();
		out.append("		<div>");
		out.append("		");
		out.append(name);
		out.append("		</div>");
		out.append("		<div class=\"root\">");
		for(var color : colors) {
			out.append(color(color));
		}
		out.append("		</div>");
		return out.toString();
	}

	private String color(Color color) {
		return "			<div style=\"background-color:" + color.hexCode(false) + ";\" title=\"" + title(color) + "\"></div>\n";
	}

	private String title(Color color) {
		return color.getName() == null ? color.hexCode(false) : (color.getName() + " (" + color.hexCode(false) + ")");
	}

//	private String footer() {
//		return """
//	</body>
//</html>
//""";
//	}
}
