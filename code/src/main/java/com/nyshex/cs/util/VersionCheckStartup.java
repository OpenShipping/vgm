package com.nyshex.cs.util;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.nyshex.cs.data.VersionRepository;

/**
 * Check database version on startup
 */
@Singleton
@Startup
public class VersionCheckStartup {

    @Inject
    private VersionRepository versionRepository;

    @PostConstruct
    private void startup() {
        versionRepository.checkVersion();
    }

}
