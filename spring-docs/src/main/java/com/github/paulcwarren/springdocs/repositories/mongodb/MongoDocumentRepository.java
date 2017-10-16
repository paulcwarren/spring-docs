package com.github.paulcwarren.springdocs.repositories.mongodb;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.github.paulcwarren.springdocs.domain.Document;

@Repository
@Profile("mongodb")
@CrossOrigin(origins = "http://localhost:8080")
public interface MongoDocumentRepository extends MongoRepository<Document, String> {
	
	List<Document> findByTitle(@Param("title") String title);
	
}
