Spring Docs
===========

This is a sample application for using database and storage services on [Cloud Foundry](http://cloudfoundry.org) with the [Spring Framework](http://spring.io).

This application has been built to store the same domain object in one of a variety of different persistence technologies - relational, document, and key-value stores.  To store content associated with that domain object in a variety of different storage technologies - file, object, BLOBs or Mongo's GridFS.  To index and search content using Apache Solr.  This is not meant to represent a realistic use case for these technologies, since you would typically choose the service most applicable to the type of data and content you need to store, but it is useful for testing and experimenting with different types of services on Cloud Foundry. 

The application uses Spring Java configuration and [bean profiles](https://spring.io/blog/2011/02/14/spring-3-1-m1-introducing-profile/) to configure the application and the connection objects needed to use the database and storage service. It also uses the [Spring Cloud Connectors](http://cloud.spring.io/spring-cloud-connectors/) library to inspect the environment when running on Cloud Foundry. See the [Cloud Foundry documentation](http://docs.cloudfoundry.org/buildpacks/java/spring-service-bindings.html) for details on configuring a Spring application for Cloud Foundry.

The application is run as two separate microservices.  spring-docs provides the Spring Boot, Spring Data and Spring Content backend service.  spring-docs-ui provides an angularjs 1.x user interface. 

## Running the application locally

One Spring bean profile should be activated to choose the database provider that the application should use and another to choose the storage provider. These two profiles are selected by setting the system property `spring.profiles.active` when starting the app.

The application can be started locally using the following command:

~~~
~/spring-docs/spring-docs/$ mvn spring-boot:run -Dspring.profiles.active=<database profile, storage profile>
~~~

where `<database profile>` is one of the following values:

* `in-memory` (no external database required)
* `mysql`
* `postgres`
* `mongodb`
* `redis`

If no database profile is provided, `in-memory` will be used. If any other profile is provided, the appropriate database server
must be started separately. The application will use the host name `localhost` and the default port to connect to the database.

If more than one of these database profiles are provided, the application will throw an exception and fail to start.

and where `<storage profile>` is one of the following values:

* `filesystem` (no external storage required)
* `gridfs`
* `s3`			TBD
* `blob`		TBD

Likewise, if no storage profile is provided, `filesystem` will be used. If any other profile is provided, the appropriate storage server
must be started separately. The application will use the host name `localhost` and the default port to connect to the storage.

If more than one of these storage profiles are provided, the application will throw an exception and fail to start.
