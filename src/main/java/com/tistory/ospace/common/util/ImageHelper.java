package com.tistory.ospace.common.util;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageHelper {
	private String        filepath;
	private BufferedImage image;
	
	public static ImageHelper of(String filepath) throws IOException {
		return new ImageHelper(ImageUtils.openImage(filepath), filepath);
	}
	
	private ImageHelper(BufferedImage image, String filepath) {
		this.image = image;
		this.filepath = filepath;
	}
	
	public int getHeight() {
		return image.getHeight();
	}
	
	public int getWidth() {
		return image.getWidth();
	}
	
	public String getFilepath() {
		return filepath;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void watermark(String filepath, float alpha) throws IOException {
		watermark(ImageIO.read(new File(filepath)), alpha);
	}
	
	public void watermark(ImageHelper image, float alpha) throws IOException {
		watermark(image.getImage(), alpha);
	}
	
	public void watermark(BufferedImage waterImage, float alpha) {
		ImageUtils.watermark(image, waterImage, alpha);
	}
	
	public ImageHelper resize(int width, int height, int mode) {
		return new ImageHelper(ImageUtils.resize(image, width, height, mode), filepath);
	}
	
	public void save(String filepath) throws IOException {
		ImageUtils.save(image, filepath);
	}
	
	public void save(String filepath, float quality) throws IOException {
		ImageUtils.save(image, filepath, quality);
	}
}
