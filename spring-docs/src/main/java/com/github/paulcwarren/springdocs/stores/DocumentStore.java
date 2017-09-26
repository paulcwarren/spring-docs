package com.github.paulcwarren.springdocs.stores;

import org.springframework.content.commons.renditions.Renderable;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.commons.search.Searchable;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.github.paulcwarren.springdocs.domain.Document;

@CrossOrigin(origins = "http://localhost:9090")
@StoreRestResource(path="documentscontent")
public interface DocumentStore extends ContentStore<Document, String>, Searchable<String>, Renderable<String> {
	//
}
