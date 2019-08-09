package com.github.paulcwarren.springdocs.repositories;

import java.util.List;

import com.github.paulcwarren.springdocs.domain.Document;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
@Profile("mongodb")
@CrossOrigin
public interface MongoDocumentRepository extends MongoRepository<Document, String>  {
	
	List<Document> findByTitle(@Param("title") String title);
	
}
