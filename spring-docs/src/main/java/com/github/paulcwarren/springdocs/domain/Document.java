package com.github.paulcwarren.springdocs.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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

}
