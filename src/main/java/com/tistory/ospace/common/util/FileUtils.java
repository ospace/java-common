package com.tistory.ospace.common.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

public class FileUtils {
	public static final int BUFFER_SIZE = 4*1024;

	/**
	 * 이미지 리사이징
	 * @param originalFile
	 * @param thumbnailFile
	 * @param thumbWidth
	 * @param thumbHeight
	 * @param quality
	 * @throws Exception
	 */
	public static void makeThumbNail(String originalFile, String thumbnailFile, int thumbWidth, int thumbHeight) throws Exception{
		
		Image image = javax.imageio.ImageIO.read(new File(originalFile));

		double thumbRatio = (double)thumbWidth / (double)thumbHeight;
		int imageWidth    = image.getWidth(null);
		int imageHeight   = image.getHeight(null);
		
		double imageRatio = (double)imageWidth / (double)imageHeight;
		if (thumbRatio < imageRatio) {
			thumbHeight = (int)(thumbWidth / imageRatio);
		} else {
			thumbWidth = (int)(thumbHeight * imageRatio);
		}
		   
		if(imageWidth < thumbWidth && imageHeight < thumbHeight) {
			thumbWidth = imageWidth;
			thumbHeight = imageHeight;
		} else if(imageWidth < thumbWidth){
			thumbWidth = imageWidth;
		}else if(imageHeight < thumbHeight){
			thumbHeight = imageHeight;
		}
		
		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setBackground(Color.WHITE);
		graphics2D.setPaint(Color.WHITE); 
		graphics2D.fillRect(0, 0, thumbWidth, thumbHeight);
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
   
		 javax.imageio.ImageIO.write(thumbImage, "JPG", new File(thumbnailFile));
		       
	}
	
	/***
	 * 파일 사이즈 <p>
	 * @param file
	 * @return String
	 */
	public static  String getFileSize(Long size){	

		/*
		String size = "0";

		if(file < 1024){
			size = file + " B";
		}else if(file >= 1024 && file < 1024 * 1024){
			size = String.format("%.2f", (double)file / 1024 ) + " KB";
		}else{
			size = String.format("%.2f", (double)file / 1024 ) + " MB";
		}

		return size;
		*/
		
		if (size <= 0)	return "0";
		
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	/***
	 * 파일읽기
	 * @param path : 파일경로<br>
	 * @param filename : 파일명
	 * @return
	 */
	public static String getFileRead(String path, String filename) {
		
		StringBuffer dataList = new StringBuffer();
		BufferedReader in = null;
		try{
			File oTmpFile = new File(path, filename);
			if (!oTmpFile.exists()) 	dataList.append("");

			in = new java.io.BufferedReader(new FileReader(oTmpFile));
			while(in.ready()) {
				dataList.append(in.readLine()+ "\n");
			}

		}catch (Exception e){
			
		}finally{
			if(in != null)	try{in.close();} catch (IOException e) {}
		}

		return dataList.toString();
	}
		
	/***
	 * 파일쓰기
	 * @param str : 파일내용
	 * @param filename : 파일명(경로포함)
	 */
	public static void setFileWrite(String str, String filename, String charset) { 
		FileOutputStream fos = null;
		Writer out = null;
		
		try  {
			fos = new FileOutputStream(filename); 
			out = new OutputStreamWriter(fos, charset); 
			out.write(str); 
			
		} catch (IOException e) {
			
		}finally{
			if(out != null)	try{out.close();} catch (IOException e) {}
			if(fos != null)	try{fos.close();} catch (IOException e) {}
		}
	}
	
	public static String readString(String filepath) {
		StringBuffer sb = new StringBuffer();
		BufferedReader in = null;
		try {
			File file = new File(filepath);
			if (!file.exists()) return null;
			
			in = new BufferedReader(new FileReader(file));
			while(in.ready()) {
				sb.append(in.readLine());
				sb.append("\n");
			}
		} catch(Exception e) {
			throw new RuntimeException("readFile", e);
		} finally {
			if(null != in) {
				try { in.close(); } catch(IOException e) {}
			}
		}
		
		return sb.toString();
	}
	
	public static boolean writeString(String filepath, String content) {
		File file = new File(filepath);
		
		String path = file.getParentFile().toString();
		File dir = new File(path);
		if(!dir.exists()) dir.mkdirs();
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(content);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static byte[] readBin(InputStream in) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		copy(in, buffer);
		
		return buffer.toByteArray();
	}
	
	public static void writeBin(File file, byte[] data) {
		BufferedOutputStream writer = null;
		try {
			String path = file.getParentFile().toString();
			File dir = new File(path);
			if(!dir.exists()) dir.mkdirs();
			
			FileOutputStream fos = new FileOutputStream(file);
			writer = new BufferedOutputStream(fos, 4*1024);
			writeBin(writer, data);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("writeBin", e);
		}
	}
	
	public static void writeBin(OutputStream out, byte[] data) throws IOException {
		out.write(data);
	}

	/**
	 * 파일삭제
	 * @param filepath
	 */
	public static void deleteFile(String filepath) {
		if(StringUtils.isEmpty(filepath)) return;
		
		File f = new File(filepath);
		if(null != f && f.exists()) f.delete();
	}
	
	public static String getExtension(String filename) {
		if(StringUtils.isEmpty(filename)) return null;
		int idx = filename.lastIndexOf(".");
		return idx < 0 ? null : filename.substring(idx+1).toLowerCase();
	}
	
	public static File mkdirs(String path) {
		if(StringUtils.isEmpty(path)) return null;
		
		File dir = new File(path);
		if(!dir.exists()) dir.mkdirs();
		return dir;
	}
	
	public static void copy(File inFile, OutputStream out) throws IOException {
		if(null == inFile || null == out) return;
		
		if(!inFile.exists()) {
			throw new FileNotFoundException(inFile.getPath());
		}
		
		InputStream in = null;
		try {
			in = new FileInputStream(inFile);
			copy(in, out);
		} finally {
			if(null != in) {
				try { in.close(); } catch (IOException e) {}
			}
			
			try { out.flush(); } catch (IOException e) {}
		}
	}
	
	public static void copy(InputStream in, Path outPath) throws IOException {
		copy(in, Files.newOutputStream(outPath));
	}
	
	public static int copy(InputStream in, OutputStream out) throws IOException {
		int total = 0;
		
		byte[] buf = new byte[BUFFER_SIZE];
		int n = 0;
		while ((n = in.read(buf)) != -1) {
			out.write(buf, 0, n);
			total += n;
		}
		out.flush();
		
		return total;
	}
}

