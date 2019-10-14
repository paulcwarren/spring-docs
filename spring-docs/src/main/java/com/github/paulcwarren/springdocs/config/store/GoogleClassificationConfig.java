package com.github.paulcwarren.springdocs.config.store;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.github.paulcwarren.springdocs.domain.Document;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.ClassifyTextRequest;
import com.google.cloud.language.v1.ClassifyTextResponse;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.annotations.HandleAfterSetContent;
import org.springframework.content.commons.annotations.StoreEventHandler;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class GoogleClassificationConfig {

	private static final Log logger = LogFactory.getLog(GoogleClassificationConfig.class);

	@Bean
	public ClassificationHandler classificationHandler() {
		return new ClassificationHandler();
	}

	@StoreEventHandler
	public static class ClassificationHandler {

        @Autowired
        ContentStore store;

	    private Credentials credentials = null;

		public ClassificationHandler() {

		    String credsJsonPath = System.getenv("GCP_SERVICE_ACCOUNT");
		    if (credsJsonPath == null) {
		        logger.warn("Disabling google classification as no service account file path was provided.  Try setting GCP_SERVICE_ACCOUNT environment variable");
            } else {
                InputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(credsJsonPath);
                    credentials = ServiceAccountCredentials.fromStream(fileInputStream);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}

		@HandleAfterSetContent
		@Order(Ordered.LOWEST_PRECEDENCE)
		public void handleAfterSetContent(Document doc) {

		    if (credentials != null) {
                InputStream is = store.getContent(doc);

                try {
                    String extractText = extractText(is);
                    List<String> categories = classifyText(extractText);
                    if (categories != null) {
                        doc.setCategories(categories.toArray(new String[] {}));
                    }
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        List<String> classifyText(String extractText) {
            List<String> categories = null;

            LanguageServiceSettings settings = null;
            try {
                settings = LanguageServiceSettings.newBuilder().setCredentialsProvider(new CredentialsProvider() {
                    @Override
                    public Credentials getCredentials() throws IOException {
                        return credentials;
                    }
                }).build();

                try (LanguageServiceClient language = LanguageServiceClient.create(settings)) {

                    com.google.cloud.language.v1.Document gdoc = com.google.cloud.language.v1.Document.newBuilder()
                            .setContent(extractText)
                            .setType(com.google.cloud.language.v1.Document.Type.PLAIN_TEXT)
                            .build();

                    ClassifyTextRequest classifyReq = ClassifyTextRequest.newBuilder()
                            .setDocument(gdoc)
                            .build();

                    ClassifyTextResponse classifyResp = language.classifyText(classifyReq);

                    categories = classifyResp.getCategoriesList().stream().map(c -> c.getName())
                            .sorted()
                            .collect(Collectors.toList());
                }
            }
            catch (IOException e) {
                logger.error("Classifying text", e);
            }

            return categories;
        }

        String extractText(InputStream is) {
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            try {
                parser.parse(is, handler, metadata);
            }
            catch (Exception e) {
                logger.error("Extracting text", e);
            }
            return handler.toString();
        }
    }
}
