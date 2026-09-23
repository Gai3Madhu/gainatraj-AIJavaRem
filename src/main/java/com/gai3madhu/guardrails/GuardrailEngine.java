package com.gai3madhu.guardrails;

import java.util.List;


    public class GuardrailEngine {
        private  List<FlawChecker> checkers = List.of(
                new Flaw01_MissingTransaction(),
                new Flaw02_ImpliedDecimalScaling()
        );

        public void scan(String aiGeneratedCode) {
            System.out.println("Scanning AI code for IBMi flaws...");
            for (FlawChecker checker : checkers) {
                if (checker.detects(aiGeneratedCode)) {
                    System.out.println("❌ FLAW FOUND: " + checker.getFlawName());
                    System.out.println("WHY: " + checker.getWhyAIDoesIt());
                    System.out.println("FIX: " + checker.fixPrompt());
                }
            }
        }

        public static void main(String[] args) {
            String fakeAiCode = "BigDecimal amount = new BigDecimal(1.15); slice1.validateAmount(amount);";
            new GuardrailEngine().scan(fakeAiCode);
        }
    }
