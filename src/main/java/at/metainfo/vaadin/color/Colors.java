package at.metainfo.vaadin.color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Colors {

	public static List<Color> hueColors(int degree, int steps) {
		List<Color> colors = new ArrayList<>();
		int step = 100 / steps;
		for(int h = 0; h < 360; h += degree) {
			for(int s = 0; s < 100; s += step) {
				Color color = Color.from(ColorSystem.HSV, h, s, 100);
				if(color.gray_scaled() < 0) colors.add(color);
			}
			for(int v = 100; v >= 0; v -= step) {
				Color color = Color.from(ColorSystem.HSV, h, 100, v);
				if(color.gray_scaled() < 0) colors.add(color);
			}
		}
		return colors;
	}

	public static List<Color> rgbColors(int step) {
		List<Color> colors = new ArrayList<>();
		for(int r = 0; r <= 256; r += step) {
			for(int g = 0; g <= 256; g += step) {
				for(int b = 0; b <= 256; b += step) {
					Color color = Color.from(ColorSystem.RGB, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
					colors.add(color);
				}
			}
		}
		return colors;
	}

	public static List<Color> grayColors(int steps) {
		List<Color> colors = new ArrayList<>();
		int step = 256 / steps;
		for(int g = 0; g <= 256; g += step) {
			var gc = Math.min(g, 255);
			Color color = Color.from(ColorSystem.RGB, gc, gc, gc);
			colors.add(color);
		}
		return colors;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(new File("d:\\Work\\css3colors.txt")))) {
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
}
