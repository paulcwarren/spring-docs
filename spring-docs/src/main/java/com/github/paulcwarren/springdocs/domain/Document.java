package com.github.paulcwarren.springdocs.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;
import org.springframework.content.commons.annotations.OriginalFileName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Document {

	@Id
    @Column(length=40)
	@ContentId
	private String id = UUID.randomUUID().toString();

	private String title;

	private String author;

	@ContentLength
   	private long contentLen;

	@MimeType
	private String mimeType;

	@OriginalFileName
	private String fileName;

	private String[] categories;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}
}
