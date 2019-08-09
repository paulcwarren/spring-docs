package com.github.paulcwarren.springdocs.stores;

import com.github.paulcwarren.springdocs.domain.Document;

import org.springframework.content.commons.renditions.Renderable;
import org.springframework.content.commons.search.Searchable;
import org.springframework.content.fs.store.FilesystemContentStore;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@StoreRestResource(path="documentscontent")
public interface FsDocumentStore extends FilesystemContentStore<Document, String>, Searchable<String>, Renderable<String> {
	//
}
