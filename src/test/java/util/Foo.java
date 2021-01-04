package util;

import java.time.LocalDateTime;

public class Foo {
	private Integer id;
	private String name;
	private LocalDateTime createdDate;
	
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
	@Override
	public String toString() {
		return "Foo [id=" + id + ", name=" + name + ", createdDate=" + createdDate + "]";
	}
}
