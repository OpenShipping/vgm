package com.nyshex.cs.util;

import javax.ejb.Singleton;

/**
 * Utility class for identifying system settings.
 */
@Singleton
public class Settings {

    /**
     * Types of environments possible.
     */
    public enum Environment {
        /**
         * Local development environment.
         */
        DEV_VGM,
        /**
         * Integration testing environment.
         */
        TEST_VGM,
        /**
         * Live VGM environment.
         */
        VGM
    }

    private final Environment environment;
    private final String awsAccessKey;
    private final String awsSecretKey;

    /**
     * Constructor for injecting system settings.
     */
    public Settings() {
        environment = Environment.valueOf(System.getProperty("env"));
        awsAccessKey = System.getProperty("aws.accessKey");
        awsSecretKey = System.getProperty("aws.secretKey");
    }

    /**
     * @return Environment variable
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * @return AWS Access Key
     */
    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    /**
     * @return AWS Secret Key
     */
    public String getAwsSecretKey() {
        return awsSecretKey;
    }

}
