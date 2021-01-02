package com.tistory.ospace.common.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtils {
	public static BufferedImage openImage(String filepath) throws IOException {
		return ImageIO.read(new File(filepath));
	}
	
	public static void watermark(BufferedImage image, BufferedImage waterImage, float alpha) {
		AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		
		int x = image.getWidth() - waterImage.getWidth();
		int y = image.getHeight() - waterImage.getHeight();
		Graphics2D g = image.createGraphics();
		g.setComposite(comp);
		g.drawImage(waterImage, x, y, null);
		g.dispose();
	}
	
	public static BufferedImage resize(BufferedImage image, int width, int height, int mode) {
		int x = 0, y = 0;
		int resizeWidth = width, resizeHeight = height;

		switch(mode) {
		case 1:
			if(image.getWidth() > width) {
				resizeHeight = height * image.getHeight()/image.getWidth();
				y = resizeHeight < height ? (height-resizeHeight)>>1 : 0; 
			}
			break;
		case 2:
			if(image.getHeight() > height) {
				resizeWidth = width * image.getWidth()/image.getHeight();
				x = resizeWidth < width ? (width-resizeWidth)>>1 : 0;
			}
			break;
		}

		int type = 0 == image.getType() ? BufferedImage.TYPE_INT_ARGB : image.getType();
		BufferedImage resizeImg = new BufferedImage(width, height, type);
		Graphics2D g = resizeImg.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(image, x, y, resizeWidth, resizeHeight, null);
		g.dispose();
		
		return resizeImg;
	}
	
	public static void save(BufferedImage image, String filepath) throws IOException {
		String ext = getExtention(filepath);
		ImageIO.write(image, ext, new File(filepath));
	}
	
	public static void save(BufferedImage image, String filepath, float quality) throws IOException {
		String ext = getExtention(filepath);
		
		ImageWriter writer = ImageIO.getImageWritersByFormatName(ext).next();
		ImageWriteParam iwparam = writer.getDefaultWriteParam();
		iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwparam.setCompressionQuality(quality);

		FileOutputStream  fos = new FileOutputStream(new File(filepath));
		ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
		writer.setOutput(ios);
		try  {
			writer.write(image);
		} finally {
			writer.dispose();
			ios.flush();
			ios.close();
		}
	}
	
	public static String getExtention(String filepath) {
		int pos = filepath.lastIndexOf('.');
		return 0 < pos ? filepath.substring(pos+1) : "jpg";
	}
}
