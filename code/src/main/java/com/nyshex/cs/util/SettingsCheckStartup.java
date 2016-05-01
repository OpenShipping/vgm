package com.nyshex.cs.util;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Check database version on startup
 */
@Singleton
@Startup
public class SettingsCheckStartup {

    @Inject
    Settings settings;

    @PostConstruct
    private void startup() {
//        if (TimeZone.getTimeZone("UTC") != TimeZone.getDefault()) {
//            throw new SystemNotUTCException(TimeZone.getDefault().getDisplayName());
//        }
    }


    /**
     * Exception thrown for wrong version
     */
    public static class SystemNotUTCException extends RuntimeException {
        /**
         * Constructor
         *
         * @param actualTimezone
         */
        public SystemNotUTCException(final String actualTimezone) {
            super("Invalid system timezone: actualTimezone(" + actualTimezone + ") != expectedTimezone(UTC)");
        }
    }

}
