package com.gai3madhu.guardrails;

public class Flaw02_ImpliedDecimalScaling implements FlawChecker {
    @Override
    public String getFlawName() {
        return "AI Fails to Handle Implied Decimal Scaling in AS400 Cross-Slice Calls";
    }

    @Override
    public String getWhyAIDoesIt() {
        return "RPG uses fixed-length numeric with implied decimals (e.g., last 3 digits are decimals). " +
                "Java uses BigDecimal/double with explicit decimal point. AI doesn't infer scale conversion " +
                "when Slice3 (Java) calls Slice1 (RPG validation). So 1.15 -> rounded to 1 -> sent as 001 -> Slice1 thinks 3 decimals.";
    }

    @Override
    public boolean detects(String code) {
        // Flaw if code passes BigDecimal directly to legacy slice without scale adjustment
        boolean callsLegacySlice = code.contains("slice1") || code.contains("validateAmount") || code.contains("legacyCall");
        boolean hasScaleFix = code.contains("movePointRight") || code.contains("setScale") || code.contains("scaleByPowerOfTen");
        // If calling legacy and no scaling, it's a flaw
        return callsLegacySlice && !hasScaleFix && code.contains("BigDecimal");
    }

    @Override
    public String fixPrompt() {
        return "RULE FOR CROSS-SLICE: When Slice3 (modern Java) calls Slice1 (AS400 RPG validator for 2-decimal currency): " +
                "DO NOT pass BigDecimal as-is. RPG expects implied decimal with scale=3. " +
                "Convert: amount.movePointRight(3). Example: 1.15 -> 1150 for validation call. " +
                "Use scaled value ONLY for validation call, preserve original for business logic.";
    }

    @Override
   public String getFixedExample() {
        return "// BEFORE (AI Generated - Flawed)\n" +
                "BigDecimal amount = new BigDecimal(\"1.15\");\n" +
                "boolean isValid = slice1.validateAmount(amount); // Sends 1, fails\n\n" +
                "// AFTER (Fixed - Preserves Legacy Contract)\n" +
                "BigDecimal amount = new BigDecimal(\"1.15\");\n" +
                "BigDecimal forLegacySlice1 = amount.movePointRight(3); // 1.15 -> 1150\n" +
                "boolean isValid = slice1.validateAmount(forLegacySlice1); // Slice1 sees 1150 = 1.150, valid\n" +
                "// Use 'amount' for further business logic, not forLegacySlice1";
    }
}
