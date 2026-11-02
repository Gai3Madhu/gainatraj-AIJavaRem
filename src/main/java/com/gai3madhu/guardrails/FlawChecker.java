package com.gai3madhu.guardrails;

public interface FlawChecker {
    String getFlawName();
    String getWhyAIDoesIt();
    boolean detects(String aiGeneratedCode);
    String fixPrompt();
    String getFixedExample();
}
