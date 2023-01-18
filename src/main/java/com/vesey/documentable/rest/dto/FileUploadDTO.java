package com.vesey.documentable.rest.dto;

import java.io.File;

import javax.ws.rs.FormParam;

import com.vesey.documentable.enums.FileUploadTypeEnum;

public class FileUploadDTO {
	private File file;
	private String filename;
	private Integer id;
	private FileUploadTypeEnum type;

	public FileUploadDTO() {

	}

	public FileUploadTypeEnum getType() {
		return type;
	}

	@FormParam("type")
	public void setType(FileUploadTypeEnum type) {
		this.type = type;
	}

	public File getFile() {
		return file;
	}

	@FormParam("file")
	public void setFile(File file) {
		this.file = file;
	}

	public String getFilename() {
		return filename;
	}

	@FormParam("filename")
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Integer getId() {
		return id;
	}

	@FormParam("id")
	public void setId(Integer id) {
		this.id = id;
	}

}