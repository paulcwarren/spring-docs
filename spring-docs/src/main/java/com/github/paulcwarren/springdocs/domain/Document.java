package com.github.paulcwarren.springdocs.domain;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Document {
	
    @Id
    @Column(length=40)
//    @GeneratedValue(generator="randomId")
//    @GenericGenerator(name="randomId", strategy="RandomIdGenerator")
	@ContentId
	private String id = UUID.randomUUID().toString();
	
	private String title;
	
	private String author;
	
	@ContentLength
	private long contentLen;
	
	@MimeType
	private String mimeType;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getContentLen() {
		return contentLen;
	}

	public void setContentLen(long contentLen) {
		this.contentLen = contentLen;
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
}
