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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtils {
	public static final int BUFFER_SIZE = 4*1024;

	/**
	 * 이미지 리사이징
	 * @param originalFile
	 * @param thumbnailFile
	 * @param thumbWidth
	 * @param thumbHeight
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
	 * 파일 사이즈
	 * @param size
	 * @return String
	 */
	public static  String getFileSize(Long size){	
		if (size <= 0)	return "0";
		
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	/***
	 * 파일읽기
	 * @param path : 파일경로
	 * @param filename : 파일명
	 * @return
	 */
	public static String getFileRead(String path, String filename) {
		
		StringBuffer dataList = new StringBuffer();
		
		File oTmpFile = new File(path, filename);
		if (!oTmpFile.exists()) dataList.append("");
		
		try (BufferedReader in = new java.io.BufferedReader(new FileReader(oTmpFile))){
			while(in.ready()) {
				dataList.append(in.readLine()+ "\n");
			}
		} catch (Exception e){
		}

		return dataList.toString();
	}
		
	/***
	 * 파일쓰기
	 * @param str : 파일내용
	 * @param filename : 파일명(경로포함)
	 */
	public static void setFileWrite(String str, String filename, String charset) { 
		
		
		try (
			FileOutputStream fos = new FileOutputStream(filename);
			Writer out = new OutputStreamWriter(fos, charset);
		) {
			out.write(str); 
		} catch (IOException e) {
		}
	}
	
	public static String readString(String filepath) {
		StringBuffer sb = new StringBuffer();
		
		File file = new File(filepath);
		if (!file.exists()) return null;
		
		try (BufferedReader in = new BufferedReader(new FileReader(file))){
			while(in.ready()) {
				sb.append(in.readLine());
				sb.append("\n");
			}
		} catch(Exception e) {
			throw new RuntimeException("readFile", e);
		}
		
		return sb.toString();
	}
	
	public static String readResource(String filepath) throws IOException {
		return readResource(filepath, "UTF8");
	}
	
	public static String readResource(String filepath, String charset) throws IOException {
		InputStream fis = null;
		Reader reader = null;
		BufferedReader br = null;
		try {
			fis = CmmUtils.class.getClassLoader().getResourceAsStream(filepath);
			reader = new InputStreamReader(fis, charset);
			br = new BufferedReader(reader);
			StringBuilder sb = new StringBuilder();
		    CharBuffer cbuf = CharBuffer.allocate(1024);
	    	while(0 < br.read(cbuf)) {		    	
		    	cbuf.flip();
		    	sb.append(cbuf);
		    }
		    return sb.toString();
		} finally {
			if(null != br)     br.close();
			if(null != reader) reader.close();
			if(null != fis)    fis.close();
		}
	}
	
	public static boolean writeString(String filepath, String data) {
		File file = new File(filepath);
		
		String path = file.getParentFile().toString();
		File dir = new File(path);
		if(!dir.exists()) dir.mkdirs();
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(data);
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
			writer = new BufferedOutputStream(fos, BUFFER_SIZE);
			writeBin(writer, data);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("writeBin", e);
		}
	}
	
	public static void writeBin(OutputStream out, byte[] data) throws IOException {
		out.write(data);
	}
	
	public static void write(String filename, String data) throws IOException {
		write(filename, data.getBytes());
	}
	
	// FileChannel can be faster than standard IO
	public static void write(String filename, byte[] data) throws IOException {
		try (
			RandomAccessFile stream = new RandomAccessFile(filename, "rw");
			FileChannel channel = stream.getChannel();
		) {
		    ByteBuffer buffer = ByteBuffer.allocate(data.length);
		    buffer.put(data);
		    buffer.flip();
		    channel.write(buffer);
		}
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
		
		try (InputStream in = new FileInputStream(inFile)) {
			copy(in, out);
		} finally {
			try { out.flush(); } catch (IOException e) {}
		}
	}
	
	public static void copy(InputStream in, Path outPath) throws IOException {
		copy(in, Files.newOutputStream(outPath));
	}
	
	public static int copy(InputStream in, OutputStream out) throws IOException {
		assert null != in : "in must not null";
		assert null != out : "out must not null";
		
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
	
	public static String currentDir() {
		return System.getProperty("user.dir");
	}
	
	public static void mergeZipDiffFile(String original, String fetch, String output) {
		mergeZipDiffFile(original, fetch, output, Charset.forName("EUC-KR"));
	}
	
	public static void mergeZipDiffFile(String original, String fetch, String output, Charset charset) {
		assert null != original && !original.isEmpty() : "orignal must not empty";
		assert null != fetch && !fetch.isEmpty() : "fetch must not empty";
		assert null != output && !output.isEmpty() : "output must not empty";
		assert null != charset : "charset must not null";
		
		try (
			ZipFile originalFile = new ZipFile(original, charset);
			ZipFile fetchFile = new ZipFile(fetch, charset);
		) {
			Map<String, ZipEntry> originalFiles = DataUtils.map(originalFile.entries(), key->key.getName(), val->val);
			Map<String, ZipEntry> fetchFiles = DataUtils.map(fetchFile.entries(), key->key.getName(), val->val);
			
			List<String> delFiles = new ArrayList<>();
			List<String> modifyFiles = new ArrayList<>();
			
			for(String key : originalFiles.keySet()) {
				// Check deleted file
				if (!fetchFiles.containsKey(key)) {
					delFiles.add(key);
					continue;
				}
				
				// Check modified file
				if(!equals(originalFile.getInputStream(originalFiles.get(key)), originalFile.getInputStream(fetchFiles.get(key)))) {
					modifyFiles.add(key);
				}
				
				fetchFiles.remove(key);
			}
			
			Set<String> newFiles = fetchFiles.keySet();
			
			try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(output))) {
				byte[] buf = new byte[BUFFER_SIZE];
				DataUtils.iterate(fetchFile.entries(), it->{
					String name = it.getName();
					if(!(newFiles.contains(name) || modifyFiles.contains(name))) return;
					
					ZipEntry zipEntry = new ZipEntry(name);
					
					try (InputStream in = fetchFile.getInputStream(it)) {
						zipOut.putNextEntry(zipEntry);
						
						int len = 0;
						while(0 != (len = read(in, buf))) {
							zipOut.write(buf, 0, len);
						}
					} catch (IOException e) {
						throw new RuntimeException("mergeDiffZipFile: outputZip", e);
					}
				});
			}
		} catch (IOException e) {
			throw new RuntimeException("mergeDiffZipFile: " + original, e);
		}
	}
	
	public static boolean equals(InputStream l, InputStream r) {
		assert null != l : "l must not null";
		assert null != r : "r must not null";
		
		byte[] lBuf = new byte[BUFFER_SIZE]; 
		byte[] rBuf = new byte[BUFFER_SIZE];

		int lLen = 0, rLen = 0;
		try {
			while(true) {
				lLen = read(l, lBuf);
				rLen = read(r, rBuf);
				
				if(0 == lLen || 0 == rLen) break;
				if (lLen != rLen || !equals(lBuf, rBuf, lLen)) return false;
			}
		} catch  (IOException e) {
			return false;
		}
		
		return 0 == lLen && 0 == rLen;
	}
	
	public static boolean equals(byte[] l, byte[] r, int size) {
		assert l.length >= size : "length of l is greater than size";
		assert r.length >= size : "length of r is greater than size";
		
		for(int i=0; i<size; ++i) {
			if (l[i] != r[i]) return false;
		}
		
		return true;
	}
	
	public static int read(InputStream in, byte[] buf) throws IOException {
		assert null != in : "in must not null";
		assert null != buf : "buf must not null";
		
		int ret = 0;
		
		int n = 0;
		while ((n = in.read(buf, ret, buf.length - ret)) != -1) {
			ret += n;
		}
		
		return ret;
	}
	
	public static void move(String from, String to) {
		try {
			Files.move(Paths.get(from), Paths.get(to));
		} catch (IOException e) {
			throw new RuntimeException("move", e);
		}
	}
}

