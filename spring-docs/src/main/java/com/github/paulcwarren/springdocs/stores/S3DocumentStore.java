package com.github.paulcwarren.springdocs.stores;

import com.github.paulcwarren.springdocs.domain.Document;

import org.springframework.content.commons.renditions.Renderable;
import org.springframework.content.commons.search.Searchable;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.content.s3.store.S3ContentStore;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@StoreRestResource(path="documentscontent")
public interface S3DocumentStore extends S3ContentStore<Document, String>, Searchable<String>, Renderable<String> {
	//
}
