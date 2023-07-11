package com.hairyworld.dms.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ClientRepositoryImpl implements ClientRepository {

    private static final Logger LOGGER = LogManager.getLogger(ClientRepositoryImpl.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ClientRepositoryImpl(final DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
}
