package mccanny.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Picture{
	
	private static Picture picture;
	
	public static Picture pic(){
		if(picture == null)
			picture = new Picture();
		return picture;
	}
	
	public static final int           DEFAULT_ICON_WIDTH  = 35;
	public static final int           DEFAULT_ICON_HEIGHT = 35;
	public static final String        SPRITE_SHEET_PATH   = "assets/spriteSheet/McCannyManagementSpriteSheet.png";
	private             BufferedImage sheet;
	public final        BufferedImage MENU;
	public final        BufferedImage INFO;
	public final        BufferedImage WARNING;
	public final        BufferedImage NEW;
	public final        BufferedImage FILTER;
	
	private Picture(){
		try{
			this.sheet = ImageIO.read(new File(SPRITE_SHEET_PATH));
		}catch(IOException e){
			e.printStackTrace();
		}
		MENU = createImage(0, 0);
		INFO = createImage(0, 1);
		WARNING = createImage(0, 2);
		NEW = createImage(0, 3);
		FILTER = createImage(0, 4);
	}
	
	/**
	 * start at 0
	 */
	private BufferedImage createImage(int row, int col){
		return sheet.getSubimage(col * DEFAULT_ICON_WIDTH, row * DEFAULT_ICON_HEIGHT, DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT);
	}
}
