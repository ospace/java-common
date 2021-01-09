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
		Graphics2D g = image.createGraphics();
		
		AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g.setComposite(comp);
		
		int x = image.getWidth() - waterImage.getWidth();
		int y = image.getHeight() - waterImage.getHeight();
		g.drawImage(waterImage, x, y, null);
		
		g.dispose();
	}
	
	//mode: 0 - not fit, 1 - fit width, 2 - fit height
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
		ImageIO.write(image, getImageExtention(filepath), new File(filepath));
	}
	
	public static void save(BufferedImage image, String filepath, float quality) throws IOException {
		String ext = getImageExtention(filepath);
		
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
	
	public static int findJpegBegin(byte[] data, int offset) {
		for(int i=offset, n=data.length-1; i<n; ++i) {
			if(isJpegBoi(data[i], data[i+1])) {
				return i;
			}
		}
		return -1;
	}
	
	/* example
	 * byte[] buf = …;
	 * int boi = MjpgUtils.findJpegBegin(buf, 0);
	 * int eoi = MjpgUtils.findJpegEnd(buf, boi+2);
	 * 
	 * byte[] jpeg = Arrays.copyOfRange(buf, boi, eoi); 
	 */
	public static int findJpegEnd(byte[] data, int offset) {
		for(int i=offset, n=data.length-1; i<n; ++i) {
			if(isJpegEoi(data[i], data[i+1])) {
				return i + 2;
			}
		}
		return -1;
	}

	private static final byte[]  JPEG_BOI = { (byte)0xff, (byte)0xd8 };
	public static boolean isJpegBoi(byte l, byte r) {
		return JPEG_BOI[0] == l && JPEG_BOI[1] == r;
	}
	
	private static final byte[]  JPEG_EOI = { (byte)0xff, (byte)0xd9 };
	public static boolean isJpegEoi(byte l, byte r) {
		return JPEG_EOI[0] == l && JPEG_EOI[1] == r;
	}
	
	private static String getImageExtention(String filepath) {
		return StringUtils.isEmpty(FileUtils.getExtension(filepath), "jpg");
	}
}
