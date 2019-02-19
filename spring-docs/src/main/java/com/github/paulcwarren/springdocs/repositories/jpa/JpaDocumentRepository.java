package com.github.paulcwarren.springdocs.repositories.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.versions.LockingAndVersioningRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.github.paulcwarren.springdocs.domain.Document;

@Repository
@Profile({"in-memory","mysql","postgres","sqlserver"})
@CrossOrigin(origins = {"http://spring-docs-ui.apps.mpivcf.com", "http://localhost:8080"})
public interface JpaDocumentRepository extends JpaRepository<Document, String>, LockingAndVersioningRepository<Document, String>  {


	/**
	 *
	 * Control order so that we can group related content and sort by version number
	 *
	 */
    @Override
    @Query("from Document d order by d.ancestralRootId, d.versionNumber, d.title")
    Page<Document> findAll(Pageable pageable);


}
