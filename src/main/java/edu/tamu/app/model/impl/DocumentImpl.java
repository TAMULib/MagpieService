/* 
 * DocumentImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.impl;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.tamu.app.model.Document;

@Entity
@Table(name="all_documents")
public class DocumentImpl implements Document {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="filename", unique=true)
	private String filename;
	
	private File file;
	
	@Column(name="path")
	private String path;
	
	@Column(name="status")
	private String status;
	
	public DocumentImpl() {
		super();
	}
	
	public DocumentImpl(String filename, String status) {
		super();
		this.filename = filename;
		this.status = status;
	}

	public DocumentImpl(Long id, String filename, File file, String status) {
		super();
		this.id = id;
		this.filename = filename;
		this.file = file;
		this.status = status;
	}
	
	public DocumentImpl(Long id, String filename, File file, String path, String status) {
		super();
		this.id = id;
		this.filename = filename;
		this.file = file;
		this.path = path;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
