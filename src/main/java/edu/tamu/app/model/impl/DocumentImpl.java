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

import edu.tamu.app.model.Document;

public class DocumentImpl implements Document {
	
	private Long id;
	
	private String filename;
	
	private File file;
	
	private String status;
	
	public DocumentImpl(Long id, String filename, String status) {
		super();
		this.id = id;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
