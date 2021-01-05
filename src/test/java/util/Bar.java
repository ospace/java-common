package util;

import java.util.List;

public class Bar {
	private Long id;
	private String name;
	private String createdDate;
	private List<Integer> data;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public List<Integer> getData() {
		return data;
	}
	public void setData(List<Integer> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "Bar [id=" + id + ", name=" + name + ", createdDate=" + createdDate + ", data=" + data + "]";
	}
}
