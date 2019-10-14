package com.github.paulcwarren.springdocs.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;
import org.springframework.content.commons.annotations.OriginalFileName;
import org.springframework.versions.AncestorId;
import org.springframework.versions.AncestorRootId;
import org.springframework.versions.LockOwner;
import org.springframework.versions.SuccessorId;
import org.springframework.versions.VersionLabel;
import org.springframework.versions.VersionNumber;

@Entity
@org.springframework.data.mongodb.core.mapping.Document
@NoArgsConstructor
@Getter
@Setter
public class Document {

	@Id
    @Column(length=40)
	@ContentId
	private String id = UUID.randomUUID().toString();

	private String title;

	private String author;

    private Date created;

    private Date updated;

	@ContentLength
   	private long contentLen;

	@MimeType
	private String mimeType;

	@OriginalFileName
	private String fileName;

	@LockOwner
    private String lockOwner;

    @AncestorId
    private String ancestorId;

    @AncestorRootId
    private String ancestralRootId;

    @SuccessorId
    private String successorId;

    @VersionNumber
    private String versionNumber;

    @VersionLabel
    private String versionLabel;

	private String[] categories = null;

    public Document(Document doc) {
    	this.setTitle(doc.title);
    	this.setAuthor(doc.author);
    	this.setContentLen(doc.contentLen);
    	this.setMimeType(doc.getMimeType());
    	this.setFileName(doc.getFileName());
    	this.setLockOwner(doc.getLockOwner());
    	this.setAncestorId(doc.ancestorId);
    	this.setAncestralRootId(doc.getAncestralRootId());
    	this.setSuccessorId(doc.getSuccessorId());
    	this.setVersionNumber(doc.getVersionNumber());
    	this.setVersionLabel(doc.getVersionLabel());
    }

    @PrePersist
    protected void onCreate() {
      created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
      updated = new Date();
    }
}
