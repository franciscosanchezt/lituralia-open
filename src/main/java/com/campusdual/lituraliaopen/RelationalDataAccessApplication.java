package com.campusdual.lituraliaopen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RelationalDataAccessApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(com.campusdual.lituraliaopen.RelationalDataAccessApplication.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) throws Exception {
        try {
            log.info("Querying DB");
            int books = jdbcTemplate.queryForList(
                "SELECT * FROM lituralia.books").size();
            if (books == 0)
                log.warn("DB [FAIL]");
            else
                log.info("DB: [OK] -> " + books);
        } catch (Exception e) {
            log.warn("DB [FAIL]");
        }
    }
}