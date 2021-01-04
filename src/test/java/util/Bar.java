package util;

public class Bar {
	private Long id;
	private String name;
	private String createdDate;
	
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
	
	@Override
	public String toString() {
		return "Bar [id=" + id + ", name=" + name + ", createdDate=" + createdDate + "]";
	}
}
