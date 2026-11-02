package com.gai3madhu.guardrails;

public class Flaw01_MissingTransaction implements FlawChecker{

    @Override
    public String getFlawName() {
        return "Missing @Transactional on IBMi-migrated DB operations";
    }

    @Override
    public String getWhyAIDoesIt() {
        return "RPG uses implicit COMMIT. Claude doesn't infer transaction boundary, so it generates plain JDBC without @Transactional";
    }

    @Override
    public boolean detects(String code) {
        boolean hasDbWrite = code.contains("jdbcTemplate.update") || code.contains("INSERT") || code.contains("UPDATE");
        boolean hasTransactional = code.contains("@Transactional");
        return hasDbWrite && !hasTransactional;
    }

    @Override
    public String fixPrompt() {
        return "RULE: If RPG source contains COMMIT, ROLLBK, or CHAIN/UPDATE, generated Java MUST use @Transactional and Spring JdbcTemplate. Never use DriverManager.";
    }

    @Override
    public String getFixedExample() {
        return "@Transactional\npublic void updateCustomer() { jdbcTemplate.update(...); }";

    }
}
