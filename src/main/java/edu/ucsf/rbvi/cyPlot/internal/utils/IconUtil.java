package edu.ucsf.rbvi.cyPlot.internal.utils;



import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;



public final class IconUtil {

	
	
	private static Font appFont;

	static {
		try {
			appFont = Font.createFont(Font.TRUETYPE_FONT, IconUtil.class.getResourceAsStream("/fonts/cyplot.ttf"));
		} catch (FontFormatException e) {
			throw new RuntimeException();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	public static Font getAppFont(float size) {
		return appFont.deriveFont(size);
	}
	
	private IconUtil() {
		// restrict instantiation
	}

	
	
}