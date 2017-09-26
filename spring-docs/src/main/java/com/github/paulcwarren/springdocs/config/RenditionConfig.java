package com.github.paulcwarren.springdocs.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.content.commons.renditions.RenditionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

@Configuration
public class RenditionConfig {

	@Bean
	RenditionProvider pdfToImage() {
		return new RenditionProvider() {

			@Override
			public String consumes() {
				return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			}

			@Override
			public String[] produces() {
				return new String[] {"image/jpg"};
			}

			@SuppressWarnings("resource")
			@Override
			public InputStream convert(InputStream fromInputSource, String toMimeType) {
				XWPFDocument wordDoc;
				try {
					wordDoc = new XWPFDocument(fromInputSource);
					POIXMLProperties props = wordDoc.getProperties();
					return props.getThumbnailImage();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}
}
