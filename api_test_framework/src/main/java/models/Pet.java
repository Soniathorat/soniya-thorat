package models;

import java.util.List;

public class Pet {
	private int id;
	private String name;
	private String status;
	private Category category;
	private List<Tag> tags;
	
	public Pet(int id, String name, String status, Category category, List<Tag> tags) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.category = category;
		this.tags = tags;
	}	
	
	public int getId() { return id; }
	public void setId(int id) { this.id =id; }
	
	
	public String getName() { return name;}
	public void setName(String name) { this.name = name; }
	
	public String getStatus() { return status; }
	public void setStatus(String status) {this.status = status; }
	
	
}
