package com.github.paulcwarren.springdocs;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

//import com.amazonaws.services.s3.AmazonS3;
import com.github.paulcwarren.springdocs.config.SpringApplicationContextInitializer;

@SpringBootApplication
//@EmbeddedCassandra()
//@CassandraDataSet(keyspace="testKeyspace")
public class SpringDocsApplication extends SpringBootServletInitializer {

//	private static S3Mock api = null;
	
	public static void main(String[] args){
        new SpringApplicationBuilder(SpringDocsApplication.class).
        initializers(new SpringApplicationContextInitializer())
        .application()
        .run(args);
	}

	@Component
	public static class ServiceStarter implements ApplicationListener<ContextRefreshedEvent> {

		@Override
		public void onApplicationEvent(ContextRefreshedEvent event) {
//			AmazonS3 client = event.getApplicationContext().getBean(AmazonS3.class);
//		    client.createBucket("spring-docs");
//			
//		    try {
//				DocumentRepository repo = event.getApplicationContext().getBean(DocumentRepository.class);
//				UUID id = UUIDs.timeBased();
//				Document doc = new Document();
//				doc.setId(id);
//				doc.setTitle("A title");
//				doc.setAuthor("Paul Warren");
//				doc.setKeywords(Collections.singleton("keyword"));
//				doc = repo.save(doc);
//	
//				DocumentStore store = event.getApplicationContext().getBean(DocumentStore.class);
//				InputStream in = this.getClass().getResourceAsStream("/sample.doc");
//				store.setContent(doc, in);
//				repo.save(doc);
//				
//				System.out.println("<---- Searching for 'injured' ---->");
//				boolean found = false;
//				for (UUID uuid : store.findKeyword("injured")) {
//					System.out.println("<---- I found this... ---->" + uuid);
//					found = true;
//				}
//				if (!found) {
//					System.out.println("<---- I found NOTHING for 'injured' ---->");
//				}
//				
//				System.out.println("<---- Searching for 'vehicle' ---->");
//				found = false;
//				for (UUID uuid : store.findKeyword("vehicle")) {
//					System.out.println("<---- I found this... ---->" + uuid);
//					found = true;
//				}
//				if (!found) {
//					System.out.println("<---- I found NOTHING for 'vehicle' ---->");
//				}
//				
//				System.out.println("<---- Searching for 'blah' ---->");
//				Iterable<UUID> uuids = store.findKeyword("blah");
//				found = false;
//				for (UUID uuid : uuids) {
//					System.out.println("<---- I found this... ---->" + uuid);
//					found = true;
//				}
//				if (!found) {
//					System.out.println("<---- I found NOTHING for 'blah' ---->");
//				}
//		    } catch (Throwable e) {
//		    	e.printStackTrace();
//		    } finally {
//		    	System.exit(0);
//		    }
			
		}
	}

	@Component
	public static class ServiceStopper implements ApplicationListener<ContextStoppedEvent> {

		@Override
		public void onApplicationEvent(ContextStoppedEvent event) {
//			api.stop();
//		    EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
		}
	}
}
