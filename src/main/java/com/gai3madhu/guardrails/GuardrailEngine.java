package com.gai3madhu.guardrails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

    public class GuardrailEngine {
        private static final Logger log = LoggerFactory.getLogger(GuardrailEngine.class);
        private  List<FlawChecker> checkers = List.of(
                new Flaw01_MissingTransaction(),
                new Flaw02_ImpliedDecimalScaling()
        );

        public void scan(String aiGeneratedCode) {
            log.info("Scanning AI code for IBMi flaws...");
            for (FlawChecker checker : checkers) {
                if (checker.detects(aiGeneratedCode)) {
                    System.out.println("FLAW FOUND: " + checker.getFlawName());
                    System.out.println("WHY: " + checker.getWhyAIDoesIt());
                    System.out.println("FIX: " + checker.fixPrompt());
                }
            }
        }

        public static void main(String[] args) {
            String fakeAiCode = "BigDecimal amount = new BigDecimal(1.15); slice1.validateAmount(amount);";
            new GuardrailEngine().scan(fakeAiCode);

            // Now auto-fix with Claude
            FlawChecker flaw = new Flaw02_ImpliedDecimalScaling();
            if (flaw.detects(fakeAiCode)) {
                //System.out.println("\n--- Fixing with Bedrock Claude ---");
                log.info("\n--- Fixing with Gemini 3.5 flash ---");
                BedrockFixerService fixer = new BedrockFixerService();
                String fixed = fixer.fixCode(flaw, fakeAiCode);
                System.out.println(fixed);
            }
        }
    }
