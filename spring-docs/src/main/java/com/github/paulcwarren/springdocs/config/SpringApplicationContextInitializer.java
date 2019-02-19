package com.github.paulcwarren.springdocs.config;

import com.emc.ecs.connector.S3ServiceInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.connector.nfs.NFSServiceInfo;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.service.common.*;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.*;

public class SpringApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Log logger = LogFactory.getLog(SpringApplicationContextInitializer.class);

    private static final Map<Class<? extends ServiceInfo>, String> dataServiceTypeToProfileName = new HashMap<>();
    private static final List<String> validLocalDataProfiles = Arrays.asList("mysql", "postgres-local", "mongodb", "redis");

    private static final Map<Class<? extends ServiceInfo>, String> storeServiceTypeToProfileName = new HashMap<>();
    private static final List<String> validLocalStoreProfiles = Arrays.asList("blob", "gridfs", "fs");

    public static final String IN_MEMORY_PROFILE = "in-memory";
    public static final String FILE_SYSTEM_PROFILE = "fs";

    static {
        dataServiceTypeToProfileName.put(MongoServiceInfo.class, "mongodb");
        dataServiceTypeToProfileName.put(PostgresqlServiceInfo.class, "postgres");
        dataServiceTypeToProfileName.put(MysqlServiceInfo.class, "mysql");
        dataServiceTypeToProfileName.put(RedisServiceInfo.class, "redis");
        dataServiceTypeToProfileName.put(OracleServiceInfo.class, "oracle");
        dataServiceTypeToProfileName.put(SqlServerServiceInfo.class, "sqlserver");

        storeServiceTypeToProfileName.put(NFSServiceInfo.class, "nfs");
        storeServiceTypeToProfileName.put(S3ServiceInfo.class, "ecs");
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment appEnvironment = applicationContext.getEnvironment();
        List<String> profiles = new ArrayList<>();
        profiles.addAll(initializeDataService(appEnvironment));
        profiles.addAll(initializeStoreService(appEnvironment));
        for (String profile : profiles) {
            appEnvironment.addActiveProfile(profile);
        }
    }

	List<String> initializeDataService(ConfigurableEnvironment appEnvironment) {
		Cloud cloud = getCloud();

        String[] persistenceProfiles = getCloudProfile(cloud);
        logger.info(String.format("Cloud repository profiles: %s", (Object[])persistenceProfiles));
        if (persistenceProfiles == null) {
            persistenceProfiles = getActiveDataProfile(appEnvironment);
            logger.info(String.format("Local repository profiles: %s", (Object[])persistenceProfiles));
        }
        if (persistenceProfiles == null) {
            persistenceProfiles = new String[] { IN_MEMORY_PROFILE };
            logger.info(String.format("Default repository profiles: %s", (Object[])persistenceProfiles));
        }
        return Arrays.asList(persistenceProfiles);
	}

	List<String> initializeStoreService(ConfigurableEnvironment appEnvironment) {
		Cloud cloud = getCloud();

        String[] storeProfiles = getCloudStoreProfile(cloud);
        logger.info(String.format("Cloud store profiles: %s", (Object[])storeProfiles));
        if (storeProfiles == null) {
            storeProfiles = getActiveStoreProfile(appEnvironment);
            logger.info(String.format("Local store profiles: %s", (Object[])storeProfiles));
        }
        if (storeProfiles == null) {
            storeProfiles = new String[] { FILE_SYSTEM_PROFILE };
            logger.info(String.format("Default store profiles: %s", (Object[])storeProfiles));
        }
        return Arrays.asList(storeProfiles);
	}

    public String[] getCloudProfile(Cloud cloud) {
        if (cloud == null) {
            return null;
        }

        List<String> profiles = new ArrayList<>();

        List<ServiceInfo> serviceInfos = cloud.getServiceInfos();

        logger.info("Found serviceInfos: " + StringUtils.collectionToCommaDelimitedString(serviceInfos));

        for (ServiceInfo serviceInfo : serviceInfos) {
            if (dataServiceTypeToProfileName.containsKey(serviceInfo.getClass())) {
                profiles.add(dataServiceTypeToProfileName.get(serviceInfo.getClass()));
            }
        }

        if (profiles.size() > 1) {
            throw new IllegalStateException(
                    "Only one service of the following types may be bound to this application: " +
                            dataServiceTypeToProfileName.values().toString() + ". " +
                            "These services are bound to the application: [" +
                            StringUtils.collectionToCommaDelimitedString(profiles) + "]");
        }

        if (profiles.size() > 0) {
            return createProfileNames(profiles.get(0), "cloud");
        }

        return null;
    }

    public String[] getCloudStoreProfile(Cloud cloud) {
        if (cloud == null) {
            return null;
        }

        List<String> profiles = new ArrayList<>();

        List<ServiceInfo> serviceInfos = cloud.getServiceInfos();

        logger.info("Found serviceInfos: " + StringUtils.collectionToCommaDelimitedString(serviceInfos));

        for (ServiceInfo serviceInfo : serviceInfos) {
            if (storeServiceTypeToProfileName.containsKey(serviceInfo.getClass())) {
                profiles.add(storeServiceTypeToProfileName.get(serviceInfo.getClass()));
            }
        }

        if (profiles.size() > 1) {
            throw new IllegalStateException(
                    "Only one service of the following types may be bound to this application: " +
                            storeServiceTypeToProfileName.values().toString() + ". " +
                            "These services are bound to the application: [" +
                            StringUtils.collectionToCommaDelimitedString(profiles) + "]");
        }

        if (profiles.size() > 0) {
            return createProfileNames(profiles.get(0), "cloud");
        }

        return null;
    }

    private Cloud getCloud() {
        try {
            CloudFactory cloudFactory = new CloudFactory();
            return cloudFactory.getCloud();
        } catch (CloudException ce) {
            return null;
        }
    }

    private String[] getActiveDataProfile(ConfigurableEnvironment appEnvironment) {
        List<String> serviceProfiles = new ArrayList<>();

        for (String profile : appEnvironment.getActiveProfiles()) {
            if (validLocalDataProfiles.contains(profile)) {
                serviceProfiles.add(profile);
            }
        }

        if (serviceProfiles.size() > 1) {
            throw new IllegalStateException("Only one active Spring profile may be set among the following: " +
                    validLocalDataProfiles.toString() + ". " +
                    "These profiles are active: [" +
                    StringUtils.collectionToCommaDelimitedString(serviceProfiles) + "]");
        }

        if (serviceProfiles.size() > 0) {
            return createProfileNames(serviceProfiles.get(0), "local");
        }

        return null;
    }

    private String[] getActiveStoreProfile(ConfigurableEnvironment appEnvironment) {
        List<String> storeServiceProfiles = new ArrayList<>();

        for (String profile : appEnvironment.getActiveProfiles()) {
            if (validLocalStoreProfiles.contains(profile)) {
                storeServiceProfiles.add(profile);
            }
        }

        if (storeServiceProfiles.size() > 1) {
            throw new IllegalStateException("Only one active Spring profile may be set among the following: " +
                    validLocalStoreProfiles.toString() + ". " +
                    "These profiles are active: [" +
                    StringUtils.collectionToCommaDelimitedString(storeServiceProfiles) + "]");
        }

        if (storeServiceProfiles.size() > 0) {
            return createProfileNames(storeServiceProfiles.get(0), "local");
        }

        return null;
    }

    private String[] createProfileNames(String baseName, String suffix) {
        String[] profileNames = {baseName, baseName + "-" + suffix};
        logger.info("Setting profile names: " + StringUtils.arrayToCommaDelimitedString(profileNames));
        return profileNames;
    }
}
