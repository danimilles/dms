package com.hairyworld.dms.service;

import com.hairyworld.dms.cache.CacheManager;
import com.hairyworld.dms.cache.DBCacheManagerImpl;
import com.hairyworld.dms.repository.CacheRepositoryImpl;
import com.hairyworld.dms.util.EntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {

    private static final Logger LOGGER = LogManager.getLogger(CacheServiceImpl.class);

    private CacheManager cacheManager;
    private final CacheRepositoryImpl cacheRepository;

    public CacheServiceImpl(final CacheRepositoryImpl cacheRepository) {
        this.cacheRepository = cacheRepository;
        this.cacheManager = new DBCacheManagerImpl();
        reloadCache();
    }

    @Override
    public void reloadCache() {
        LOGGER.info("Loading DB in memory...");
        cacheManager.putEntityCache(cacheRepository.loadClientsAndDogs().get(EntityType.CLIENT), EntityType.CLIENT);
        cacheManager.putEntityCache(cacheRepository.loadDates(), EntityType.DATE);
        cacheManager.putEntityCache(cacheRepository.loadServices(), EntityType.SERVICE);
        cacheManager.putEntityCache(cacheRepository.loadPayments(), EntityType.PAYMENT);
        LOGGER.info("Load successful");
    }

}
