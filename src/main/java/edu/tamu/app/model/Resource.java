package edu.tamu.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
public class Resource extends BaseEntity {

	@Column(nullable = false)
    private String name;

	@Column(nullable = false)
    private String path;

	@Column(nullable = false)
    private String url;

	@Column(nullable = false)
    private String mimeType;
	
	public Resource() {
		
	}

	public Resource(String name, String path, String url, String mimeType) {
		this();
		this.name = name;
		this.path = path;		
		this.url = url;
		this.mimeType = mimeType;
	}
	
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
