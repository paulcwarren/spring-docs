package com.github.paulcwarren.springdocs.config.store;

import static java.lang.String.format;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.content.commons.annotations.HandleAfterSetContent;
import org.springframework.content.commons.annotations.StoreEventHandler;
import org.springframework.content.commons.renditions.RenditionService;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.config.FilesystemStoreConfigurer;
import org.springframework.content.solr.SolrProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;

import com.github.paulcwarren.springdocs.domain.Document;
import com.google.cloud.language.v1.ClassifyTextRequest;
import com.google.cloud.language.v1.ClassifyTextResponse;
import com.google.cloud.language.v1.LanguageServiceClient;

@Configuration
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableFilesystemStores(basePackages = {"com.github.paulcwarren.springdocs.stores"})
@Profile("fs")
public class FsConfig {

	private static final Log logger = LogFactory.getLog(FsConfig.class);


	@Bean
	public ClassificationHandler classidicationHandler(RenditionService renditions, SolrClient solrClient, SolrProperties solrProperties) {
		return new ClassificationHandler(renditions, solrClient, solrProperties);
	}

	@Bean
	public FilesystemStoreConfigurer configurer() {
		return new FilesystemStoreConfigurer() {

			@Override
			public void configureFilesystemStoreConverters(ConverterRegistry registry) {
				registry.addConverter(new Converter<String, String>() {

					@Override
					public String convert(String source) {

						// Support nested directory structure
						//return String.format("%s", source.toString().replaceAll("-", "").replaceAll("..", "$0/"));

						// Standard UID format
						return String.format("%s", source.toString());
					}
				});
			}
		};
	}

	@StoreEventHandler
	public static class ClassificationHandler {

		private RenditionService renditions;

		private SolrClient solrClient;
		private SolrProperties solrProperties;

		public ClassificationHandler(RenditionService renditions, SolrClient solrClient, SolrProperties solrProperties) {
			this.renditions = renditions;
			this.solrClient = solrClient;
			this.solrProperties = solrProperties;
		}

		@HandleAfterSetContent
		@Order(Ordered.LOWEST_PRECEDENCE)
		public void handleAfterSetContent(Document doc) {
			SolrQuery query = new SolrQuery();
			query.setQuery("*:*");
			query.addFilterQuery("id:" + Document.class.getName() + "\\:" + doc.getId().toString());
			QueryRequest request = new QueryRequest(query);
			QueryResponse response = null;
			try {
				response = request.process(solrClient);

				SolrDocumentList results = response.getResults();
				if (results.size() == 1) {

					if (results.get(0).get("content") != null) {
						String extractText = results.get(0).get("content").toString();

						try (LanguageServiceClient language = LanguageServiceClient.create()) {
							// set content to the text string
							com.google.cloud.language.v1.Document gdoc = com.google.cloud.language.v1.Document.newBuilder()
									.setContent(extractText)
									.setType(com.google.cloud.language.v1.Document.Type.PLAIN_TEXT)
									.build();
							ClassifyTextRequest classifyReq = ClassifyTextRequest.newBuilder()
									.setDocument(gdoc)
									.build();
							// detect categories in the given text
							ClassifyTextResponse classifyResp = language.classifyText(classifyReq);

							List<String> categories = classifyResp.getCategoriesList().stream().map(c -> c.getName()).sorted().collect(Collectors.toList());
							doc.setCategories(categories.toArray(new String[]{}));
						}
					}
				}
			} catch (SolrServerException e) {
				logger.error(format("Unexpected solr error extracting text for doc %s", doc.getId()), e);
			} catch (IOException e) {
				logger.error(format("Unexpected error extracting text for doc %s", doc.getId()), e);
			}
		}
	}
}
