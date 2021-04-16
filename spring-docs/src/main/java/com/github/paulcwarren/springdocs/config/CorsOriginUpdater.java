package com.github.paulcwarren.springdocs.config;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.system.SystemProperties;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.paulcwarren.springdocs.repositories.JpaDocumentRepository;
import com.github.paulcwarren.springdocs.repositories.MongoDocumentRepository;
import com.github.paulcwarren.springdocs.stores.FsDocumentStore;
import com.github.paulcwarren.springdocs.stores.GridFsDocumentStore;
import com.github.paulcwarren.springdocs.stores.JpaDocumentStore;
import com.github.paulcwarren.springdocs.stores.S3DocumentStore;

public class CorsOriginUpdater {

    private static Method method = null;

    static {

        String allowedHost = SystemProperties.get("SPRINGDOCS_ALLOW_HOST");

        if (allowedHost != null) {

            try {
                method = Class.class.getDeclaredMethod("annotationData", null);
                method.setAccessible(true);

                addAllowedHostTo(JpaDocumentRepository.class, allowedHost);
                addAllowedHostTo(MongoDocumentRepository.class, allowedHost);

                addAllowedHostTo(FsDocumentStore.class, allowedHost);
                addAllowedHostTo(GridFsDocumentStore.class, allowedHost);
                addAllowedHostTo(JpaDocumentStore.class, allowedHost);
                addAllowedHostTo(S3DocumentStore.class, allowedHost);
            }
            catch (Exception e) {
                throw new IllegalStateException(format("Unable to configure allowed host %s", allowedHost), e);
            }
        }
    }

    private static void addAllowedHostTo(Class<?> clazz, String allowedHost) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Object annotationData = method.invoke(clazz, null);

        Field declaredAnnotations = annotationData.getClass().getDeclaredField("declaredAnnotations");
        declaredAnnotations.setAccessible(true);

        Map<Class<? extends Annotation>, Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) declaredAnnotations.get(annotationData);

        CrossOrigin co = (CrossOrigin) annotations.remove(CrossOrigin.class);
        Annotation newCo = new DynamicCrossOrigin(
                appendValue(co.origins(), allowedHost),
                co.allowedHeaders(),
                co.exposedHeaders(),
                co.methods(),
                co.allowCredentials(),
                co.maxAge());

        annotations.put(CrossOrigin.class, newCo);
    }

    private static String[] appendValue(String[] values, String s) {
        List<String> l = new ArrayList<>();
        for (String val : values) {
            l.add(val);
        }
        l.add(s);
        return l.toArray(new String[]{});
    }

    public static class DynamicCrossOrigin implements CrossOrigin {

        private final String[] origins;
        private final String[] allowedHeaders;
        private final String[] exposedHeaders;
        private final RequestMethod[] methods;
        private final String allowCredentials;
        private final long maxAge;

        public DynamicCrossOrigin(
                String[] origins,
                String[] allowedHeaders,
                String[] exposedHeaders,
                RequestMethod[] methods,
                String allowCredentials,
                long maxAge) {

            this.origins = origins;
            this.allowedHeaders = allowedHeaders;
            this.exposedHeaders = exposedHeaders;
            this.methods = methods;
            this.allowCredentials = allowCredentials;
            this.maxAge = maxAge;
        }

        @Override
        public String[] value() {
            return origins;
        }

        @Override
        public String[] origins() {
            return new String[0];
        }

        @Override
        public String[] allowedHeaders() {
            return new String[0];
        }

        @Override
        public String[] exposedHeaders() {
            return new String[0];
        }

        @Override
        public RequestMethod[] methods() {
            return new RequestMethod[0];
        }

        @Override
        public String allowCredentials() {
            return allowCredentials;
        }

        @Override
        public long maxAge() {
            return maxAge;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return CrossOrigin.class;
        }
    }
}
