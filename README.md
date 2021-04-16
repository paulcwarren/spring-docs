Spring Docs
===========
<img src="https://travis-ci.org/paulcwarren/spring-docs.svg?branch=master"/>

This is a sample application for using database and storage services with the [Spring Framework](http://spring.io).

The application comprises of two services; `spring-docs-ui`, a UI service backed by `spring-docs`, a content service.

The content service is designed around the 3 central tenets of an Enterprise Content Management System (ECMS); content, content metadata and content search.  A Spring Data Entity `Document` is 
both a target to we can associate content, and a model of the metadata for each piece of associated content.  A Spring Data Repository provides a REST api for
creating, reading, updating and deleting instances of `Document`.  A Spring Content Store provides a REST api for creating, updating and deleting content, for associating it with an 
instance of `Document` and for searching "inside" the content.  Lastly, a fragment is also applied to the Spring Data Repository that adds a REST api for performing pessimistic locking and 
versioning of instances of `Document`.  A feature that is commonly found in ECMS.

For the meta-model, the content service can be configured to persist to a variety of different databases; either relational or document-based.  For content, the content service can be 
configured to persist to a variety of different stores; file, object, BLOB or Mongo's GridFS.  For search, the content service should be configured to run against Apache Solr.  Profiles
are used to configure these various services, as explained below.
 
## Running the Content Service

The Content Service can be started locally using the following command:

~~~
~/spring-docs/spring-docs/$ mvn spring-boot:run [-Dspring-boot.run.profiles=<database profile>, <storage profile> [, google-classification]]
~~~

### Database Profiles

One of the following values:

* `in-memory` (no external database required)
* `mysql`
* `postgres`
* `sqlserver`
* `mongodb`

If no database profile is provided, `in-memory` will be used.  If any other profile is provided, the appropriate database server
must be started separately.

#### Configuring Database Connections

For `mysql`, `postgres` and `sqlserver` you can specify the environment variables; `SPRINGDOCS_DS_URL`, `SPRINGDOCS_DS_USERNAME` and `SPRINGDOCS_DS_PASSWORD`.  `SPRINGDOCS_DS_URL`
should be a correctly formatted jdbc connection string for the relevant database.

For `mongodb` you can specify `SPRINGDOCS_MDB_URL` with a correctly formatted jdbc connection string for the MongoDB database.

If connection variables are ommited the content service will attempt to connect to a "localhost" server and a database called `springdocs`. 

If more than one of these database profiles are provided, the application will throw an exception and fail to start.

### Storage Profiles

One of the following values:

* `fs` (no external storage required)
* `blob` 
* `s3`
* `gridfs`

If no storage profile is provided, `fs` will be used. If any other profile is provided, the appropriate storage server must be started separately. 

`blob` should be used in conjuction with `mysql`, `postgres` or `sqlserver`.  See "Configuraing Database Connections".

For `s3` you must specify the environment variables; `AWS_REGION`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_KEY` and `AWS_BUCKET`

`gridfs` is used in conjunction with `mongodb`.  See "Configuring Database Connections".

If more than one of these storage profiles are provided, the application will throw an exception and fail to start.

### Search Profiles

You can specify the environment variable `SPRINGDOCS_SOLR_URL` to specify the URL of your Apache Solr instance.  If omitted the content service will attempt to connect to 
`http://localhost:8983/solr/solr`.

### Classification Profiles

Document classification can be enabled by adding the `google-classification` to the set of active profiles.  This will require the following:-
 - google private key json file and GOOGLE_APPLICATION_CREDENTIALS environment variable set with the path to this file
 - application to be run with `google-classification` added to the set of active profiles

### CORS

By default the application is configured to allow any client to connect.  You can specify the environment variable `SPRINGDOCS_ALLOW_HOST` to specify the host to allow, restricting
all others.

~~~
~/spring-docs/spring-docs/$ mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DSPRINGDOCS_ALLOW_HOST=http://localhost:8080"
~~~

## Running the UI Service

The user interface service can be started using the following command:

~~~
~/spring-docs/spring-docs-ui/$ mvn spring-boot:run 
~~~

### Content Service Configuration  

By default the UI service is configured to communicate with the content service hosted at `http://localhost:9090/`.  If you want to run the services somewhere other than localhost then
you can specify the environment variable `SPRINGDOCS_CS_URL` to specify your own URL.

The application will then be available to use at `http://localhost:8080/index.html`.
