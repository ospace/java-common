package com.tistory.ospace.common.core;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleBuffer {
	private byte[] buf = null;
	private int    pos = 0;
	private int    offset = 0;
	
	public static SimpleBuffer allocate() {
		return allocate(4096);
	}
	
	public static SimpleBuffer allocate(int size) {
		return new SimpleBuffer(size);
	}
	
	private SimpleBuffer(int size) {
		buf = new byte[size];
	}
	
	public SimpleBuffer append(int i) {
		return append(Integer.toString(i));
	}
	
	public SimpleBuffer append(String str) {
		return append(str.getBytes());
	}
	
	public SimpleBuffer append(byte[] bytes) {
		return append(bytes, 0, bytes.length);
	}
	
	public SimpleBuffer append(byte[] src, int off, int len) {
		if(remaining() < len) {
			resize(Math.max(length()*2, length()+len));
		}
		
		System.arraycopy(src, off, this.buf, position(), len);
		this.pos += len;
		return this;
	}
	
	private void resize(int size) {
		if(this.buf.length == size) return;
		if(this.buf.length > size) {
			throw new RuntimeException("must be larger then the buffer size");
		}
		
		byte[] tmp = new byte[size];
		if(this.buf != null && 0 < this.pos) {
			System.arraycopy(this.buf, 0, tmp, 0, this.buf.length);
		}
		this.buf = tmp;
	}

	public int write(OutputStream out) throws IOException {
		out.write(buf, offset, length());
		return length();
	}
	
	public int capacity() {
		return this.buf.length;
	}
	
	public int remaining() {
		return this.buf.length - pos;
	}
	
	public int length() {
		return pos - offset;
	}

	public byte[] data() {
		return this.buf;
	}
	
	public int offset() {
		return this.offset;
	}
	
	public int position() {
		return this.pos;
	}

	public void move(int val) {
		offset = Math.min(pos, offset+val);
		
	}

	public boolean isEmpty() {
		return offset == pos;
	}
}

