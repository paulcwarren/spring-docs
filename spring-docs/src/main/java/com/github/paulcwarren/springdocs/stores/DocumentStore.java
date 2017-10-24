package com.github.paulcwarren.springdocs.stores;

import com.github.paulcwarren.springdocs.domain.Document;
import org.springframework.content.commons.renditions.Renderable;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.commons.search.Searchable;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
@StoreRestResource(path="documentscontent")
public interface DocumentStore extends ContentStore<Document, String>, Searchable<String>, Renderable<String> {
	//
}
