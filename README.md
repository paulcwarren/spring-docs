Spring Docs
===========
<img src="https://travis-ci.org/paulcwarren/spring-docs.svg?branch=master"/>

This is a sample application for using database and storage services with the [Spring Framework](http://spring.io).

This application has been built as a sample Content Service.  It is designed around the 3 central tenets of a Content Management System; content, content metadata and content search.  The design comprises of a domain object that provides metadata around a piece content.  The application exposes REST endpoints for performing CRUD operations on domain objects, for associating content with those domain object, for rendering images from the original content and for searching for domain objects based on text their content may contain.    

The domain object is persisted in one of a variety of different databases - relational, document, and key-value stores.  Uploaded documents associated with those domain objects and stored in a variety of different stores - file, object, BLOBs or Mongo's GridFS - and also indexed using Apache Solr allowing searches "inside" those documents (also known as a fulltext search).  Whilst this is a realistic data model for an Content Service it is not meant to represent a realistic use case for these technologies, since you would typically choose the database and storage service most applicable to the type of data and content you need to store, but it is useful for testing and experimenting with different types of services. 

The application uses Spring Java configuration and [bean profiles](https://spring.io/blog/2011/02/14/spring-3-1-m1-introducing-profile/) to configure the application and the connection objects needed to use the database and storage service.

The application is run as two separate microservices.  spring-docs provides a Spring Boot, Spring Data and Spring Content backend service.  spring-docs-ui provides an angularjs 1.x based user interface. 

## Running the application locally

One Spring bean profile should be activated to choose the database provider that the application should use and another to choose the storage provider. These two profiles are selected by setting the system property `spring.profiles.active` when starting the app.

The Content Service can be started locally using the following command:

~~~
~/spring-docs/spring-docs/$ mvn spring-boot:run -Dspring.profiles.active=<database profile, storage profile>
~~~

where `<database profile>` is one of the following values:

* `in-memory` (no external database required)
* `mysql`
* `postgres`
* `mongodb`
* `mongodb-local`
* `redis`

If no database profile is provided, `in-memory` will be used. If any other profile is provided, the appropriate database server
must be started separately. The application will use the host name `localhost` and the default port to connect to the database.

If more than one of these database profiles are provided, the application will throw an exception and fail to start.

and where `<storage profile>` is one of the following values:

* `fs` (no external storage required)
* `gridfs`
* `s3`			TBD
* `blob`		TBD

Likewise, if no storage profile is provided, `fs` will be used. If any other profile is provided, the appropriate storage server
must be started separately. The application will use the host name `localhost` and the default port to connect to the storage.

If more than one of these storage profiles are provided, the application will throw an exception and fail to start.

The user interface service can be started locally using the following command:

~~~
~/spring-docs/spring-docs-ui/$ mvn spring-boot:run 
~~~

The application will then be available to use at `http://localhost:9090/index.html`.
