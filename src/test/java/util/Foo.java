package util;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Foo {
	private Integer id;
	private String name;
	private LocalDateTime createdDate;
	private ArrayList<Integer> data;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	public ArrayList<Integer> getData() {
		return data;
	}
	public void setData(ArrayList<Integer> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "Foo [id=" + id + ", name=" + name + ", createdDate=" + createdDate + ", data=" + data + "]";
	}
}
