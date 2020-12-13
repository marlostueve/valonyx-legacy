/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.util;

import java.io.InputStream;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 
@JsonIgnoreProperties({"content"})
public class FileMeta {
 
    private String fileName;
    private String fileSize;
    private String fileType;
    private String personId;
	
	private String videoURL;
	private String thumbURL;
 
    private InputStream content;

	//getters and setters...
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String _personId) {
		this.personId = _personId;
	}

	
	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}
	

	
	

	public String getVideoURL() {
		return videoURL;
	}

	public void setVideoURL(String _s) {
		this.videoURL = _s;
	}
	
	public String getThumbURL() {
		return thumbURL;
	}

	public void setThumbURL(String _s) {
		this.thumbURL = _s;
	}
 
}

